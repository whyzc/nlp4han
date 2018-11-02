package com.lc.nlp4han.constituent.unlex;

//import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.constituent.pcfg.PRule;

/**
 * 表示由树库得到的语法
 * 
 * @author 王宁
 * 
 */
public class Grammar
{
	public static int iterations = 50;
	public static int countsssss = 0;
	public static double rulethres = 1.0e-30;
	protected List<AnnotationTreeNode> treeBank;

	protected HashSet<BinaryRule> bRules;
	protected HashSet<UnaryRule> uRules;
	protected Lexicon lexicon;// 包含preRules

	// 添加相同孩子为key的map
	protected HashMap<Integer, HashMap<PreterminalRule, PreterminalRule>> preRuleBySameChildren; // 外层map<word在字典中的索引,内map>
	protected HashMap<Short, HashMap<Short, HashMap<BinaryRule, BinaryRule>>> bRuleBySameChildren;
	protected HashMap<Short, HashMap<UnaryRule, UnaryRule>> uRuleBySameChildren;
	// 相同父节点的规则放在一个map中
	protected HashMap<Short, HashMap<PreterminalRule, PreterminalRule>> preRuleBySameHead; // 内map<ruleHashcode/rule>
	protected HashMap<Short, HashMap<BinaryRule, BinaryRule>> bRuleBySameHead;
	protected HashMap<Short, HashMap<UnaryRule, UnaryRule>> uRuleBySameHead;

	protected NonterminalTable nonterminalTable;
	// 使用规则数量的期望的比作为新的规则概率
	protected HashMap<Short, Double[]> sameParentRulesCount = new HashMap<>();// <parent,[ParentSubIndex,denominator]>

	public static Random random = new Random(0);

	public Grammar(List<AnnotationTreeNode> treeBank, HashSet<BinaryRule> bRules, HashSet<UnaryRule> uRules,
			Lexicon lexicon, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.bRules = bRules;
		this.uRules = uRules;
		this.lexicon = lexicon;
		this.bRuleBySameChildren = new HashMap<Short, HashMap<Short, HashMap<BinaryRule, BinaryRule>>>();
		this.uRuleBySameChildren = new HashMap<Short, HashMap<UnaryRule, UnaryRule>>();
		this.preRuleBySameChildren = new HashMap<Integer, HashMap<PreterminalRule, PreterminalRule>>();
		this.bRuleBySameHead = new HashMap<Short, HashMap<BinaryRule, BinaryRule>>();
		this.uRuleBySameHead = new HashMap<Short, HashMap<UnaryRule, UnaryRule>>();
		this.preRuleBySameHead = new HashMap<Short, HashMap<PreterminalRule, PreterminalRule>>();
		this.nonterminalTable = nonterminalTable;
		init();
		grammarExam();
	}

	public void split()
	{
		GrammarSpliter.splitGrammar(this);
		EM(Grammar.iterations);
	}

	public void merger(double mergeRate)
	{
		PriorityQueue<SentenceLikehood> senScoreGradient = new PriorityQueue<>();
		// assume a subState without split,root 没有分裂
		for (short i = 1; i < nonterminalTable.getNumSymbol(); i++)
		{
			for (short j = 0; j < nonterminalTable.getNumSubsymbolArr().get(i); j++)
			{
				double tallyGradient = 1;
				for (AnnotationTreeNode tree : treeBank)
				{
					double sentenceScoreGradient = 1;
					mergeHelper(tree, i, j, sentenceScoreGradient);
					tallyGradient *= sentenceScoreGradient;
				}
				senScoreGradient.add(new SentenceLikehood(i, j, tallyGradient));
				j++;
			}
		}
		int mergeCount = (int) (senScoreGradient.size() * mergeRate);
		PriorityQueue<SentenceLikehood> newQueue = new PriorityQueue<>(new Comparator<SentenceLikehood>()
		{
			@Override
			public int compare(SentenceLikehood o1, SentenceLikehood o2)
			{
				if (o1.symbol > o2.symbol)
					return 1;
				else if (o1.symbol < o2.symbol)
					return -1;
				else
				{
					if (o1.subSymbolIndex > o2.subSymbolIndex)
						return 1;
					else if (o1.subSymbolIndex < o2.subSymbolIndex)
						return -1;
					else
						return 0;
				}
			}
		});
		for (int i = 0; i < mergeCount; i++)
		{
			newQueue.add(senScoreGradient.poll());
		}
		for (SentenceLikehood sLikehood : newQueue)
		{
			short symbol = sLikehood.symbol;
			short subSymbolIndex = sLikehood.subSymbolIndex;
			realMerge(symbol, subSymbolIndex);
		}

	}

	public void mergeHelper(AnnotationTreeNode tree, short symbol, short subSymbolIndex, double senScoreGradient)
	{
		if (tree.isLeaf())
			return;
		if (tree.getLabel().getSymbol() == symbol)
		{
			senScoreGradient = calSenSocreAssumeMergeState_i(tree, subSymbolIndex) / calculateSentenceSocre(tree);
		}
		for (AnnotationTreeNode child : tree.getChildren())
		{
			mergeHelper(child, symbol, subSymbolIndex, senScoreGradient);
		}
	}

	// TODO:
	public void realMerge(short symbol, short subSymbolIndex)
	{

	}

	/**
	 * 将分裂后的语法期望最大化，得到新的规则
	 */
	public void EM(int iterations)
	{
		for (int i = 0; i < iterations; i++)
		{
			int count = 0;
			for (AnnotationTreeNode tree : treeBank)
			{
				count++;
				System.out.println("计算第" + count + "颗树的内外向概率。");
				calculateInnerScore(tree);
				calculateOuterScore(tree);
				refreshRuleCountExpectation(tree, tree);
				if (i != iterations - 1)
				{
					tree.forgetIOScore();
				}
			}
			refreshRuleScore();
			if (i != iterations - 1)
			{
				sameParentRulesCount = new HashMap<>();
				forgetRuleCountExpectation();
			}
			System.out.println("第" + (i + 1) + "次EM结束");
		}
	}

	// 计算树上所有节点的所有隐藏节点的内向概率
	public void calculateInnerScore(AnnotationTreeNode tree)
	{
		if (tree.isLeaf())
		{
			return;
		}
		for (AnnotationTreeNode child : tree.getChildren())
		{
			calculateInnerScore(child);
		}

		if (tree.isPreterminal())
		{
			final PreterminalRule tempPreRule = new PreterminalRule(tree.getLabel().getSymbol(),
					tree.getChildren().get(0).getLabel().getWord());
			if (lexicon.getPreRules().contains(tempPreRule))
			{
				int length = tree.getLabel().getNumSubSymbol();

				PreterminalRule realRule = preRuleBySameHead.get(tree.getLabel().getSymbol()).get(tempPreRule);
				tree.getLabel().setInnerScores(realRule.getScores().toArray(new Double[length]));
				// for (int i = 0; i < tree.getLabel().getInnerScores().length; i++)
				// {
				// System.out.println(nonterminalTable.stringValue(tree.getLabel().getSymbol())
				// + "["
				// + tree.getLabel().getSpanFrom() + "," + tree.getLabel().getSpanTo() + "]" +
				// "innerScore_"
				// + i + ":" + tree.getLabel().getInnerScores()[i]);
				// }

			}
			else
			{
				throw new Error("Error grammar: don't contains  preRule :" + tempPreRule.toString());
			}
		}
		else
		{
			switch (tree.getChildren().size())
			{
			case 1:
				final UnaryRule tempUnaryRule = new UnaryRule(tree.getLabel().getSymbol(),
						tree.getChildren().get(0).getLabel().getSymbol());
				if (uRules.contains(tempUnaryRule))
				{
					LinkedList<LinkedList<Double>> uRuleScores = uRuleBySameHead.get(tree.getLabel().getSymbol())
							.get(tempUnaryRule).getScores();
					Double[] innerScores = new Double[tree.getLabel().getNumSubSymbol()];
					tree.getLabel().setInnerScores(innerScores);
					for (int i = 0; i < innerScores.length; i++)
					{
						double innerScores_Ai = 0.0;
						for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
						{ // 规则A_i -> B_j的概率
							double A_i2B_j = uRuleScores.get(i).get(j);
							double B_jInnerScore = tree.getChildren().get(0).getLabel().getInnerScores()[j];
							innerScores_Ai = innerScores_Ai + (A_i2B_j * B_jInnerScore);
						}
						innerScores[i] = innerScores_Ai;
						// System.out.println(nonterminalTable.stringValue(tree.getLabel().getSymbol())
						// + "["
						// + tree.getLabel().getSpanFrom() + "," + tree.getLabel().getSpanTo() + "]"
						// + "innerScore_" + i + ":" + tree.getLabel().getInnerScores()[i]);
					}
					// tree.getLabel().setInnerScores(innerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  uRule :" + tempUnaryRule.toString());
				}
				break;
			case 2:
				final BinaryRule tempBRule = new BinaryRule(tree.getLabel().getSymbol(),
						tree.getChildren().get(0).getLabel().getSymbol(),
						tree.getChildren().get(1).getLabel().getSymbol());
				if (bRules.contains(tempBRule))
				{
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = bRuleBySameHead
							.get(tree.getLabel().getSymbol()).get(tempBRule).getScores();
					Double[] innerScores = new Double[tree.getLabel().getNumSubSymbol()];
					tree.getLabel().setInnerScores(innerScores);
					for (int i = 0; i < innerScores.length; i++)
					{
						double innerScores_Ai = 0.0;
						for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
						{
							for (int k = 0; k < tree.getChildren().get(1).getLabel().getNumSubSymbol(); k++)
							{
								// 规则A_i -> B_j C_k的概率
								double A_i2B_jC_k = bRuleScores.get(i).get(j).get(k);
								double B_jInnerScore = tree.getChildren().get(0).getLabel().getInnerScores()[j];
								double C_kInnerScore = tree.getChildren().get(1).getLabel().getInnerScores()[k];
								innerScores_Ai = innerScores_Ai + (A_i2B_jC_k * B_jInnerScore * C_kInnerScore);
							}
						}
						innerScores[i] = innerScores_Ai;
						// System.out.println(nonterminalTable.stringValue(tree.getLabel().getSymbol())
						// + "["
						// + tree.getLabel().getSpanFrom() + "," + tree.getLabel().getSpanTo() + "]"
						// + "innerScore_" + i + ":" + tree.getLabel().getInnerScores()[i]);
					}
					// tree.getLabel().setInnerScores(innerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  bRule :" + tempBRule.toString());
				}
				break;
			default:
				throw new Error("Error tree: more than two children.");
			}
		}

	}

	/**
	 * tree的根标记为ROOT
	 * 
	 * @param tree
	 */
	public void calculateOuterScore(AnnotationTreeNode tree)
	{
		if (tree == null)
			return;
		calculateOuterScoreHelper(tree, tree);
	}

	private void calculateOuterScoreHelper(AnnotationTreeNode treeRoot, AnnotationTreeNode treeNode)
	{

		if (treeNode == null)
			return;
		if (treeNode.isLeaf())
			return;
		// 计算根节点的外向概率
		if (treeNode == treeRoot)
		{
			Double[] array = new Double[treeNode.getLabel().getNumSubSymbol()];
			Arrays.fill(array, 1.0);
			treeNode.getLabel().setOuterScores(array);
		}
		else
		{
			AnnotationTreeNode parent = treeNode.getParent();
			switch (parent.getChildren().size())
			{
			case 1:
				final UnaryRule tempUnaryRule = new UnaryRule(parent.getLabel().getSymbol(),
						treeNode.getLabel().getSymbol());
				if (uRules.contains(tempUnaryRule))
				{
					LinkedList<LinkedList<Double>> uRuleScores = uRuleBySameHead.get(parent.getLabel().getSymbol())
							.get(tempUnaryRule).getScores();
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int j = 0; j < outerScores.length; j++)
					{
						double outerScores_Bj = 0.0;
						for (int i = 0; i < parent.getLabel().getNumSubSymbol(); i++)
						{
							double A_i2B_j = uRuleScores.get(i).get(j);
							double A_iOuterScore = parent.getLabel().getOuterScores()[i];
							outerScores_Bj = outerScores_Bj + (A_i2B_j * A_iOuterScore);
						}
						outerScores[j] = outerScores_Bj;
						// System.out.println(nonterminalTable.stringValue(treeNode.getLabel().getSymbol())
						// + "["
						// + treeNode.getLabel().getSpanFrom() + "," + treeNode.getLabel().getSpanTo() +
						// "]"
						// + "outerScore_" + j + ":" + outerScores_Bj);
					}
					treeNode.getLabel().setOuterScores(outerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  uRule :" + tempUnaryRule.toString());
				}

				break;
			case 2:
				// 获取兄弟节点的内向概率
				Double[] siblingNode_InScore;
				final BinaryRule tempBRule;
				if (parent.getChildren().get(0) == treeNode)
				{
					siblingNode_InScore = parent.getChildren().get(1).getLabel().getInnerScores();
					tempBRule = new BinaryRule(parent.getLabel().getSymbol(), treeNode.getLabel().getSymbol(),
							parent.getChildren().get(1).getLabel().getSymbol());
				}
				else
				{
					siblingNode_InScore = parent.getChildren().get(0).getLabel().getInnerScores();
					tempBRule = new BinaryRule(parent.getLabel().getSymbol(),
							parent.getChildren().get(0).getLabel().getSymbol(), treeNode.getLabel().getSymbol());
				}

				if (bRules.contains(tempBRule))
				{
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = bRuleBySameHead
							.get(parent.getLabel().getSymbol()).get(tempBRule).getScores();
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int i = 0; i < outerScores.length; i++)
					{
						double outerScoreB_i = 0.0;
						for (int j = 0; j < parent.getLabel().getNumSubSymbol(); j++)
						{
							double A_jOuterscore = parent.getLabel().getOuterScores()[j];
							for (int k = 0; k < siblingNode_InScore.length; k++)
							{
								double C_kInnerScore = siblingNode_InScore[k];
								if (parent.getChildren().get(0) == treeNode)
								{

									double A_j2B_iC_k = bRuleScores.get(j).get(i).get(k);
									outerScoreB_i = outerScoreB_i + (A_j2B_iC_k * A_jOuterscore * C_kInnerScore);

								}
								else
								{
									double A_j2C_kB_i = bRuleScores.get(j).get(k).get(i);
									outerScoreB_i = outerScoreB_i + (A_j2C_kB_i * A_jOuterscore * C_kInnerScore);
								}

							}
						}
						// System.out.println(nonterminalTable.stringValue(treeNode.getLabel().getSymbol())
						// + "["
						// + treeNode.getLabel().getSpanFrom() + "," + treeNode.getLabel().getSpanTo() +
						// "]"
						// + "outerScore_" + i + ":" + outerScoreB_i);
						outerScores[i] = outerScoreB_i;
					}
					treeNode.getLabel().setOuterScores(outerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  bRule :" + tempBRule.toString());
				}

				break;
			default:
				throw new Error("error tree:more than two children.");
			}
		}
		for (AnnotationTreeNode childNode : treeNode.getChildren())
		{
			calculateOuterScoreHelper(treeRoot, childNode);
		}

	}

	public void refreshRuleCountExpectation(AnnotationTreeNode root, AnnotationTreeNode tree)
	{

		if (tree.getChildren().size() == 0 || tree == null)
			return;
		Rule rule = null;
		if (tree.getChildren().size() == 2)
		{
			rule = new BinaryRule(tree.getLabel().getSymbol(), tree.getChildren().get(0).getLabel().getSymbol(),
					tree.getChildren().get(1).getLabel().getSymbol());
			LinkedList<LinkedList<LinkedList<Double>>> scores = bRuleBySameHead.get(tree.getLabel().getSymbol())
					.get(rule).getScores();
			double[][][] count = bRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()][tree.getChildren().get(0).getLabel()
						.getNumSubSymbol()][tree.getChildren().get(1).getLabel().getNumSubSymbol()];
			}

			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
				{
					for (int k = 0; k < tree.getChildren().get(1).getLabel().getNumSubSymbol(); k++)
					{
						count[i][j][k] = count[i][j][k] + (tree.getLabel().getOuterScores()[i]
								* scores.get(i).get(j).get(k) * tree.getChildren().get(0).getLabel().getInnerScores()[j]
								* tree.getChildren().get(1).getLabel().getInnerScores()[k]
								/ root.getLabel().getInnerScores()[0]);
						// if (nonterminalTable.stringValue(rule.parent).equals("@PP")
						// &&
						// nonterminalTable.stringValue(tree.getChildren().get(0).getLabel().getSymbol())
						// .equals("PP")
						// &&
						// nonterminalTable.stringValue(tree.getChildren().get(1).getLabel().getSymbol())
						// .equals("CC"))
						// {
						// System.err.println(nonterminalTable.stringValue(rule.parent) + i + "->"
						// +
						// nonterminalTable.stringValue(tree.getChildren().get(0).getLabel().getSymbol())
						// + j
						// + " "
						// +
						// nonterminalTable.stringValue(tree.getChildren().get(1).getLabel().getSymbol())
						// + k
						// + "的期望：" + count[i][j][k]);
						// }

					}
				}
			}

			bRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).setCountExpectation(count);
			// if (nonterminalTable.stringValue(rule.parent).equals("@PP")
			// &&
			// nonterminalTable.stringValue(tree.getChildren().get(0).getLabel().getSymbol()).equals("PP")
			// &&
			// nonterminalTable.stringValue(tree.getChildren().get(1).getLabel().getSymbol()).equals("CC"))
			// {
			// for (BinaryRule bRule : bRules)
			// {
			// if (bRule.hashCode() == rule.hashCode())
			// {
			// System.err.println(bRule ==
			// bRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule));
			// for (double[][] arr : bRule.getCountExpectation())
			// for (double[] arr1 : arr)
			// for (double thecount : arr1)
			// {
			// System.err.println(thecount);
			// }
			// }
			// }
			// }
		}
		else if (tree.getChildren().size() == 1 && tree.getChildren().get(0).getLabel().getWord() == null)
		{
			rule = new UnaryRule(tree.getLabel().getSymbol(), tree.getChildren().get(0).getLabel().getSymbol());
			LinkedList<LinkedList<Double>> scores = uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule)
					.getScores();
			double[][] count = uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()][tree.getChildren().get(0).getLabel()
						.getNumSubSymbol()];
			}
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
				{
					// countsssss++;
					// System.out.println("count :" + countsssss +
					// root.getLabel().getInnerScores()[0]);
					count[i][j] = count[i][j] + (tree.getLabel().getOuterScores()[i] * scores.get(i).get(j)
							* tree.getChildren().get(0).getLabel().getInnerScores()[j]
							/ root.getLabel().getInnerScores()[0]);
				}
			}
			uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).setCountExpectation(count);
		}
		else if (tree.isPreterminal())
		{
			rule = new PreterminalRule(tree.getLabel().getSymbol(), tree.getChildren().get(0).getLabel().getWord());
			LinkedList<Double> scores = preRuleBySameHead.get(rule.getParent()).get(rule).getScores();
			double[] count = preRuleBySameHead.get(rule.getParent()).get(rule).getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()];
			}
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				count[i] = count[i]
						+ (tree.getLabel().getOuterScores()[i] * scores.get(i) / root.getLabel().getInnerScores()[0]);

			}
			preRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).setCountExpectation(count);
		}
		else if (tree.getChildren().size() > 2)
			throw new Error("error tree:more than 2 children.");

		for (AnnotationTreeNode child : tree.getChildren())
		{
			refreshRuleCountExpectation(root, child);
		}
	}

	public void refreshRuleScore()
	{

		// int count = 0;
		for (BinaryRule bRule : bRules)
		{
			// count++;
			// System.out.println(count);
			// for (String str : bRule.toStringRules())
			// System.out.println(str);
			// System.out.println(sameParentRulesCount.containsKey(bRule.parent));
			if (sameParentRulesCount.containsKey(bRule.parent))
			{
				for (Double score : sameParentRulesCount.get(bRule.parent))
					System.out.println(score);
			}

			bRule.getCountExpectation();
			int pNumSub = bRule.getCountExpectation().length;
			int lCNumSub = bRule.getCountExpectation()[0].length;
			int rCNumSub = bRule.getCountExpectation()[0][0].length;

			for (int i = 0; i < pNumSub; i++)
			{
				double denominator;
				if (sameParentRulesCount.containsKey(bRule.parent) && sameParentRulesCount.get(bRule.parent)[i] != null)
				{
					denominator = sameParentRulesCount.get(bRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(sameParentRulesCount, bRule.parent, i);
				}
				for (int j = 0; j < lCNumSub; j++)
				{
					for (int k = 0; k < rCNumSub; k++)
					{
						bRule.getScores().get(i).get(j).set(k, bRule.getCountExpectation()[i][j][k] / denominator);
					}
				}
			}

		}

		for (UnaryRule uRule : uRules)
		{
			int pNumSub = uRule.getCountExpectation().length;
			int cNumSub = uRule.getCountExpectation()[0].length;
			for (int i = 0; i < pNumSub; i++)
			{
				double denominator;
				if (sameParentRulesCount.containsKey(uRule.parent) && sameParentRulesCount.get(uRule.parent)[i] != null)
				{
					denominator = sameParentRulesCount.get(uRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(sameParentRulesCount, uRule.parent, i);
				}
				for (int j = 0; j < cNumSub; j++)
				{

					uRule.getScores().get(i).set(j, uRule.getCountExpectation()[i][j] / denominator);

				}
			}
		}

		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			preRule.getCountExpectation();
			int pNumSub = preRule.getCountExpectation().length;
			for (int i = 0; i < pNumSub; i++)
			{
				double denominator;
				if (sameParentRulesCount.containsKey(preRule.parent)
						&& sameParentRulesCount.get(preRule.parent)[i] != null)
				{
					denominator = sameParentRulesCount.get(preRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(sameParentRulesCount, preRule.parent, i);
				}

				preRule.getScores().set(i, preRule.getCountExpectation()[i] / denominator);

			}
		}
	}

	/*
	 * 如果表格中(parent,pSubsymbolIndex)位置没有数值表示该值该没有计算，否则直接从表格中取值
	 */
	public Double calculateSameParentRuleCount(HashMap<Short, Double[]> sameParentRulesCount, int parent,
			int pSubSymbolIndex)
	{
		if (!sameParentRulesCount.containsKey((short) parent)
				|| sameParentRulesCount.get((short) parent)[pSubSymbolIndex] == null)
		{
			double ruleCount = 0.0;
			if (bRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<BinaryRule, BinaryRule> entry : bRuleBySameHead.get((short) parent).entrySet())
				{
					double[][][] count = entry.getValue().getCountExpectation();
					for (int i = 0; i < count[pSubSymbolIndex].length; i++)
					{
						for (int j = 0; j < count[pSubSymbolIndex][i].length; j++)
						{
							ruleCount = ruleCount + count[pSubSymbolIndex][i][j];
						}
					}
				}
			if (uRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<UnaryRule, UnaryRule> entry : uRuleBySameHead.get((short) parent).entrySet())
				{
					double[][] count = entry.getValue().getCountExpectation();
					for (int i = 0; i < count[pSubSymbolIndex].length; i++)
					{
						ruleCount = ruleCount + count[pSubSymbolIndex][i];
					}
				}
			if (preRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<PreterminalRule, PreterminalRule> entry : preRuleBySameHead.get((short) parent)
						.entrySet())
				{
					double[] count = entry.getValue().getCountExpectation();
					ruleCount = ruleCount + count[pSubSymbolIndex];
				}
			if (sameParentRulesCount.containsKey((short) parent))
			{
				sameParentRulesCount.get((short) parent)[pSubSymbolIndex] = ruleCount;
			}
			else
			{
				Double[] countArr = new Double[nonterminalTable.getNumSubsymbolArr().get(parent)];
				countArr[pSubSymbolIndex] = ruleCount;
				sameParentRulesCount.put((short) parent, countArr);
			}
			return ruleCount;
		}
		else
		{
			return sameParentRulesCount.get((short) parent)[pSubSymbolIndex];
		}
	}

	public double calculateSentenceSocre(AnnotationTreeNode node)
	{
		if (node.getLabel().getInnerScores() == null || node.getLabel().getOuterScores() == null)
			throw new Error("没有计算树上节点的内外向概率。");
		if (node.isLeaf())
			throw new Error("不能利用叶子节点计算内外向概率。");
		double sentenceScore = 0.0;
		Double[] innerScore = node.getLabel().getInnerScores();
		Double[] outerScores = node.getLabel().getOuterScores();
		for (int i = 0; i < innerScore.length; i++)
		{
			sentenceScore += innerScore[i] * outerScores[i];
		}
		return sentenceScore;
	}

	/**
	 * @param 树中要合并的节点
	 * @param 树中要合并的节点的subStateIndex
	 * @return 树的似然值
	 */
	public double calSenSocreAssumeMergeState_i(AnnotationTreeNode node, int subStateIndex)
	{
		if (node.getLabel().getInnerScores() == null || node.getLabel().getOuterScores() == null)
			throw new Error("没有计算树上节点的内外向概率。");
		if (node.isLeaf())
			throw new Error("不能利用叶子节点计算内外向概率。");
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
				double iRuleCount = calculateSameParentRuleCount(sameParentRulesCount, node.getLabel().getSymbol(), i);
				double fullBrotherRuleCount = calculateSameParentRuleCount(sameParentRulesCount,
						node.getLabel().getSymbol(), i + 1);
				double iFrequency = iRuleCount / iRuleCount + fullBrotherRuleCount;
				double brotherFrequency = fullBrotherRuleCount / iRuleCount + fullBrotherRuleCount;
				sentenceScore += (iFrequency * innerScore[i] + brotherFrequency * innerScore[i + 1])
						* (outerScores[i] + outerScores[i + 1]);
				i++;
			}
			else
			{
				sentenceScore += innerScore[i] * outerScores[i];
			}
		}
		return sentenceScore;
	}

	public void forgetRuleCountExpectation()
	{
		for (UnaryRule uRule : uRules)
		{
			uRule.setCountExpectation(null);
		}
		for (BinaryRule bRule : bRules)
		{
			bRule.setCountExpectation(null);
		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			preRule.setCountExpectation(null);
		}
	}

	/**
	 * 返回CFG，其中规则包含一元规则、二元规则
	 * 
	 * @return
	 */
	public PCFG getPCFG()
	{
		PCFG pcfg = new PCFG();
		for (UnaryRule uRule : uRules)
		{
			PRule pRule = new PRule(uRule.getScores().get(0).get(0), nonterminalTable.stringValue(uRule.parent),
					nonterminalTable.stringValue(uRule.getChild()));
			pcfg.add(pRule);
		}
		for (BinaryRule bRule : bRules)
		{
			PRule pRule = new PRule(bRule.getScores().get(0).get(0).get(0), nonterminalTable.stringValue(bRule.parent),
					nonterminalTable.stringValue(bRule.getLeftChild()),
					nonterminalTable.stringValue(bRule.getRightChild()));
			pcfg.add(pRule);
		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			PRule pRule = new PRule(preRule.getScores().get(0), nonterminalTable.stringValue(preRule.getParent()),
					preRule.getWord());
			pcfg.add(pRule);
		}
		pcfg.setStartSymbol("ROOT");
		return pcfg;
	}

	public void init()
	{
		for (BinaryRule bRule : bRules)
		{

			if (!bRuleBySameHead.containsKey(bRule.parent))
			{
				bRuleBySameHead.put(bRule.parent, new HashMap<BinaryRule, BinaryRule>());
			}
			bRuleBySameHead.get(bRule.parent).put(bRule, bRule);

			if (!bRuleBySameChildren.containsKey(bRule.getLeftChild()))
			{

				bRuleBySameChildren.put(bRule.getLeftChild(), new HashMap<Short, HashMap<BinaryRule, BinaryRule>>());
			}
			if (!bRuleBySameChildren.get(bRule.getLeftChild()).containsKey(bRule.getRightChild()))
			{
				bRuleBySameChildren.get(bRule.getLeftChild()).put(bRule.getRightChild(),
						new HashMap<BinaryRule, BinaryRule>());
			}
			bRuleBySameChildren.get(bRule.getLeftChild()).get(bRule.getRightChild()).put(bRule, bRule);
		}

		for (UnaryRule uRule : uRules)
		{
			if (!uRuleBySameHead.containsKey(uRule.parent))
			{
				uRuleBySameHead.put(uRule.parent, new HashMap<UnaryRule, UnaryRule>());
			}
			uRuleBySameHead.get(uRule.parent).put(uRule, uRule);

			if (!uRuleBySameChildren.containsKey(uRule.getChild()))
			{
				uRuleBySameChildren.put(uRule.getChild(), new HashMap<UnaryRule, UnaryRule>());
			}
			uRuleBySameChildren.get(uRule.getChild()).put(uRule, uRule);
		}

		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			if (!preRuleBySameHead.containsKey(preRule.parent))
			{
				preRuleBySameHead.put(preRule.parent, new HashMap<PreterminalRule, PreterminalRule>());
			}
			preRuleBySameHead.get(preRule.parent).put(preRule, preRule);

			if (!preRuleBySameChildren.containsKey(lexicon.getDictionary().get(preRule.getWord())))
			{
				preRuleBySameChildren.put(lexicon.getDictionary().get(preRule.getWord()),
						new HashMap<PreterminalRule, PreterminalRule>());
			}
			preRuleBySameChildren.get(lexicon.getDictionary().get(preRule.getWord())).put(preRule, preRule);
		}
	}

	public void grammarExam()
	{
		int berrcount = 0, uerrcount = 0, perrcount = 0;
		int berrcount1 = 0, uerrcount1 = 0, perrcount1 = 0;
		for (BinaryRule bRule : bRules)
		{
			if (bRule == bRuleBySameHead.get(bRule.parent).get(bRule))
			{
				// System.out.println(true);
			}
			else
			{
				berrcount++;
				System.err.println(false + "********************二元规则集bRuleBySameHead" + berrcount);
			}
			if (bRule != bRuleBySameChildren.get(bRule.getLeftChild()).get(bRule.getRightChild()).get(bRule))
			{
				berrcount1++;
				System.err.println(false + "********************二元规则集bRuleBySameChildren" + berrcount1);
			}
		}
		for (UnaryRule uRule : uRules)
		{
			if (uRule == uRuleBySameHead.get(uRule.parent).get(uRule))
			{
				// System.out.println(true);
			}
			else
			{
				uerrcount++;
				System.err.println(false + "********************一元规则uRuleBySameHead" + uerrcount);
			}
			if (uRule != uRuleBySameChildren.get(uRule.getChild()).get(uRule))
			{
				uerrcount1++;
				System.err.println(false + "********************一元规则uRuleBySameChildren" + uerrcount1);
			}
		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			if (preRule == preRuleBySameHead.get(preRule.parent).get(preRule))
			{
				// System.out.println(true);
			}
			else
			{
				perrcount++;
				System.err.println(false + "********************预终结符号规则preRuleBySameHead" + perrcount);
			}
			if (preRule != preRuleBySameChildren.get(lexicon.getDictionary().get(preRule.getWord())).get(preRule))
			{
				perrcount1++;
				System.err.println(false + "********************预终结符号规则preRuleBySameChildren" + perrcount1);
			}
		}
	}

	public HashSet<BinaryRule> getbRules()
	{
		return bRules;
	}

	public HashSet<UnaryRule> getuRules()
	{
		return uRules;
	}

	public Lexicon getLexicon()
	{
		return lexicon;
	}

	public HashSet<PreterminalRule> getPreRules()
	{
		return lexicon.getPreRules();
	}

	public NonterminalTable getNonterminalTable()
	{
		return nonterminalTable;
	}

	class SentenceLikehood implements Comparable<SentenceLikehood>
	{
		short symbol;
		short subSymbolIndex;
		double sentenceScoreGradient;

		SentenceLikehood(short symbol, short subSymbolIndex, double sentenceScoreGradient)
		{
			this.sentenceScoreGradient = sentenceScoreGradient;
			this.symbol = symbol;
			this.subSymbolIndex = subSymbolIndex;
		}

		@Override
		public int compareTo(SentenceLikehood o)
		{
			if (this.sentenceScoreGradient > o.sentenceScoreGradient)
				return 1;
			else if (this.sentenceScoreGradient < o.sentenceScoreGradient)
				return -1;
			else
				return 0;
		}

	}
}
