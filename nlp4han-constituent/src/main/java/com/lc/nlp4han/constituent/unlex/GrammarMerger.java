package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 将分裂后的语法合并
 * 
 * @author 王宁
 * 
 */
public class GrammarMerger
{
	public static void mergeGrammar(Grammar grammar, TreeBank treeBank, double mergeRate, RuleCounter ruleCounter)
	{
		treeBank.calIOScore(grammar);
		double[][] mergeWeight = computerMergerWeight(grammar, ruleCounter);
		ArrayList<Short> newNumSubsymbolArr = new ArrayList<>(
				Arrays.asList(new Short[grammar.getNumSubsymbolArr().size()]));
		Collections.copy(newNumSubsymbolArr, grammar.getNumSubsymbolArr());
		Short[][] mergeSymbols = getMergeSymbol(grammar, treeBank, mergeRate, newNumSubsymbolArr, mergeWeight);
		mergeRule(grammar.getbRules(), mergeSymbols, mergeWeight);
		mergeRule(grammar.getuRules(), mergeSymbols, mergeWeight);
		mergeRule(grammar.getLexicon().getPreRules(), mergeSymbols, mergeWeight);
		grammar.setNumSubsymbolArr(newNumSubsymbolArr);
		mergeWeight = null;
		mergeTrees(grammar, treeBank);
		treeBank.forgetIOScoreAndScale();
	}

	public static <T extends Rule> void mergeRule(Set<T> rules, Short[][] symbolToMerge, double[][] mergeWeight)
	{
		for (Rule rule : rules)
		{
			rule.merge(symbolToMerge, mergeWeight);
		}
	}

	public static void mergeTrees(Grammar g, TreeBank treeBank)
	{

		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			mergeTreeAnnotation(g, tree);
		}
	}

	public static void mergeTreeAnnotation(Grammar g, AnnotationTreeNode tree)
	{
		if (tree.isLeaf())
			return;
		tree.getLabel().setNumSubSymbol(g.getNumSubSymbol(tree.getLabel().getSymbol()));
		tree.forgetIOScoreAndScale();
		tree.getLabel().setInnerScores(null);
		tree.getLabel().setOuterScores(null);
		for (AnnotationTreeNode child : tree.getChildren())
		{
			mergeTreeAnnotation(g, child);
		}
	}

	// TODO:处理期望值为0的
	public static double[][] computerMergerWeight(Grammar g, RuleCounter ruleCounter)
	{
		double[][] mergerWeight = new double[g.getNumSymbol()][];
		// 根节点不分裂、不合并
		for (short i = 1; i < mergerWeight.length; i++)
		{
			mergerWeight[i] = new double[g.getNumSubsymbolArr().get(i)];
			for (short j = 0; j < mergerWeight[i].length; j++)
			{
				mergerWeight[i][j] = ruleCounter.sameParentRulesCounter.get(i)[j]
						/ (ruleCounter.sameParentRulesCounter.get(i)[j]
								+ ruleCounter.sameParentRulesCounter.get(i)[j + 1]);
				mergerWeight[i][j + 1] = ruleCounter.sameParentRulesCounter.get(i)[j]
						/ (ruleCounter.sameParentRulesCounter.get(i)[j]
								+ ruleCounter.sameParentRulesCounter.get(i)[j + 1]);
				j++;
			}
		}
		return mergerWeight;
	}

	@SuppressWarnings("unchecked")
	public static Short[][] getMergeSymbol(Grammar g, TreeBank treeBank, double mergeRate,
			ArrayList<Short> newNumSubsymbolArr, double[][] mergeWeight)
	{
		ArrayList<Short>[] symbolToMerge = new ArrayList[g.getNumSymbol()];
		PriorityQueue<SentenceLikehood> senScoreGradient = new PriorityQueue<>();
		// 假设一个符号没有分裂,已知root 没有分裂
		for (short i = 1; i < g.getNumSymbol(); i++)
		{
			for (short j = 0; j < g.getNumSubSymbol(i); j++)
			{
				double tallyLogGradient = 0;
				for (AnnotationTreeNode tree : treeBank.getTreeBank())
				{
					double logSenScoreGradient = getMergeSymbolHelper(g, tree, i, j, mergeWeight);
					tallyLogGradient += logSenScoreGradient;
				}
				System.out.println(
						"若合并非终结符号" + g.symbolStrValue(i) + "的第" + j + "," + (j + 1) + "个子符号后树库似然比：" + tallyLogGradient);
				senScoreGradient.add(new SentenceLikehood(i, j, tallyLogGradient));
				j++;
			}
		}
		int mergeCount = (int) (senScoreGradient.size() * mergeRate);
		double litterBigger = 0.0;
		System.out.println("预计合并" + mergeCount + "对子符号。");
		for (int i = 0; i < mergeCount; i++)
		{
			SentenceLikehood s = senScoreGradient.poll();
			if (symbolToMerge[s.symbol] == null)
			{
				symbolToMerge[s.symbol] = new ArrayList<Short>();
			}
			if (s.logAllSenScoreGradient < 0)
			{
				System.out.println("实际合并" + i + "对子符号。");
				System.out.println("合并第" + i + "对子符号后，树库与合并前的似然值之比为：" + litterBigger);
				break;
			}
			if (i == mergeCount - 1)
			{
				System.out.println("实际合并" + (i + 1) + "对子符号******");
				System.out.println("合并第" + (i + 1) + "对子符号后，树库与合并前的似然值之比为：" + s.logAllSenScoreGradient);
			}
			symbolToMerge[s.symbol].add(s.subSymbolIndex);
			litterBigger = s.logAllSenScoreGradient;
		}

		Short[][] mergeSymbols = new Short[symbolToMerge.length][];
		Short[] subSymbolToMerge;
		// assume a subState without split,root 没有分裂
		for (int i = 1; i < symbolToMerge.length; i++)
		{
			if (symbolToMerge[i] != null)
			{
				symbolToMerge[i].sort(new Comparator<Short>()
				{
					@Override
					public int compare(Short o1, Short o2)
					{
						if (o1 < o2)
							return -1;
						else if (o1 > o2)
							return 1;
						else
							return 0;
					}
				});

				subSymbolToMerge = symbolToMerge[i].toArray(new Short[symbolToMerge[i].size()]);
				mergeSymbols[i] = subSymbolToMerge;
				newNumSubsymbolArr.set(i, (short) (g.getNumSubSymbol((short) i) - subSymbolToMerge.length));
			}
		}
		return mergeSymbols;
	}

	public static double getMergeSymbolHelper(Grammar g, AnnotationTreeNode tree, short symbol, short subSymbolIndex,
			double[][] mergeWeight)
	{
		double logSenScoreGradient = 0;
		for (AnnotationTreeNode child : tree.getChildren())
		{
			if (!child.isLeaf())
				logSenScoreGradient += getMergeSymbolHelper(g, child, symbol, subSymbolIndex, mergeWeight);
		}
		if (tree.getLabel().getSymbol() == symbol && symbol != 0)
		{
			logSenScoreGradient += calLogSenSocreAssumeMergeState_i(tree, subSymbolIndex, mergeWeight)
					- TreeBank.calLogSentenceSocre(tree);
		}
		return logSenScoreGradient;
	}

	/**
	 * @param 树中要合并的节点
	 * @param 树中要合并的节点的subStateIndex
	 * @return 树的似然值
	 */
	public static double calLogSenSocreAssumeMergeState_i(AnnotationTreeNode node, int subStateIndex,
			double[][] mergeWeight)
	{

		if (node.getLabel().getInnerScores() == null || node.getLabel().getOuterScores() == null)
			throw new Error("没有计算树上节点的内外向概率。");
		if (node.isLeaf())
			throw new Error("不能利用叶子节点计算内外向概率。");
		if (node.getLabel().getSymbol() == 0)// root不分裂不合并
			return 0;
		double sentenceScore = 0.0;
		Double[] innerScore = node.getLabel().getInnerScores();
		Double[] outerScores = node.getLabel().getOuterScores();
		int numSubState = innerScore.length;
		if (subStateIndex % 2 == 1)
		{
			subStateIndex = subStateIndex - 1;
		}
		for (int i = 0; i < numSubState; i++)
		{
			if (i == subStateIndex)
			{
				double iWeight = mergeWeight[node.getLabel().getSymbol()][i];
				double brotherWeight = mergeWeight[node.getLabel().getSymbol()][i + 1];
				if (iWeight == Double.NaN || brotherWeight == Double.NaN)
				{
					System.err.println(" count Of SubSymbol_Si and SubSymbol_Si+1 underFlow.");
				}
				sentenceScore += (iWeight * innerScore[i] + brotherWeight * innerScore[i + 1])
						* (outerScores[i] + outerScores[i + 1]);
				i++;
			}
			else
			{
				sentenceScore += innerScore[i] * outerScores[i];
			}
		}
		double logSenScore = Math.log(sentenceScore)
				+ 100 * (node.getLabel().getInnerScale() + node.getLabel().getOuterScale());
		return logSenScore;
	}

	static class SentenceLikehood implements Comparable<SentenceLikehood>
	{
		short symbol;
		short subSymbolIndex;
		double logAllSenScoreGradient;

		SentenceLikehood(short symbol, short subSymbolIndex, double logAllSenScoreGradient)
		{
			this.logAllSenScoreGradient = logAllSenScoreGradient;
			this.symbol = symbol;
			this.subSymbolIndex = subSymbolIndex;
		}

		@Override
		public int compareTo(SentenceLikehood o)
		{
			if (this.logAllSenScoreGradient > o.logAllSenScoreGradient)
				return -1;
			else if (this.logAllSenScoreGradient < o.logAllSenScoreGradient)
				return 1;
			else
				return 0;
		}
	}
}
