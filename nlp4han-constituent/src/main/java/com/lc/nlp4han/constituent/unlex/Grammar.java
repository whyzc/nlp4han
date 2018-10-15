package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 王宁
 * @version 创建时间：2018年9月23日 下午2:59:46 表示由树库得到的语法
 */
public class Grammar
{
	public static int iterations = 50;

	protected List<Tree<Annotation>> treeBank;

	protected HashSet<BinaryRule> bRules;
	protected HashSet<UnaryRule> uRules;
	protected Lexicon lexicon;// 包含preRules

	// 相同父节点的规则放在一个map中
	protected HashMap<Short, HashMap<PreterminalRule, LinkedList<Double>>> preRuleBySameHeadWithScore;
	protected HashMap<Short, HashMap<BinaryRule, LinkedList<LinkedList<LinkedList<Double>>>>> bRuleBySameHeadWithScore;
	protected HashMap<Short, HashMap<UnaryRule, LinkedList<LinkedList<Double>>>> uRuleBySameHeadWithScore;

	protected NonterminalTable nonterminalTable;

	public Grammar(List<Tree<Annotation>> treeBank, HashSet<BinaryRule> bRules, HashSet<UnaryRule> uRules,
			Lexicon lexicon, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.bRules = bRules;
		this.uRules = uRules;
		this.lexicon = lexicon;
		this.nonterminalTable = nonterminalTable;
	}

	public void split()
	{
		GrammarSpliter.splitGrammar(this);
		EM(Grammar.iterations);
	}

	public void merger()
	{
		// assume a nonterminal without split

	}

	/**
	 * 将分裂后的语法期望最大化，得到新的规则
	 */
	public void EM(int iterations)
	{
		for (int i = 0; i < iterations; i++)
		{
			for (Tree<Annotation> tree : treeBank)
			{
				calculateInnerScore(tree);
				calculateOuterScore(tree);
				fillRuleCountExpectation(tree);
				TreeUtil.forgetScore(tree);
			}
			refreshRuleScore();
		}
		// forAllRule -> forgetCountExpectation()
	}

	// 计算树上所有节点的所有隐藏节点的内向概率
	public void calculateInnerScore(Tree<Annotation> tree)
	{
		if (tree.isLeaf())
		{
			return;
		}
		for (Tree<Annotation> child : tree.getChildren())
		{
			calculateInnerScore(child);
		}

		if (tree.isPreterminal())
		{
			final PreterminalRule tempPreRule = new PreterminalRule(tree.getLabel().getSymbol(),
					tree.getChildren().get(0).getLabel().getWord());
			if (lexicon.getPreRules().contains(tempPreRule))
			{
				// double[] arr = Arrays.stream(new
				// Double[10]).mapToDouble(Double::valueOf).toArray();
				int length = preRuleBySameHeadWithScore.get(tree.getLabel().getSymbol()).get(tempPreRule).size();
				tree.getLabel().setInnerScores(preRuleBySameHeadWithScore.get(tree.getLabel().getSymbol())
						.get(tempPreRule).toArray(new Double[length]));
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
					LinkedList<LinkedList<Double>> uRuleScores = uRuleBySameHeadWithScore
							.get(tree.getLabel().getSymbol()).get(tempUnaryRule);
					Double[] innerScores = new Double[tree.getLabel().getNumSubSymbol()];
					for (int i = 0; i < innerScores.length; i++)
					{
						BigDecimal innerScores_Ai = BigDecimal.valueOf(0.0);
						for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
						{ // 规则A_i -> B_j的概率
							BigDecimal A_i2B_j = BigDecimal.valueOf(uRuleScores.get(i).get(j));
							BigDecimal B_jInnerScore = BigDecimal
									.valueOf(tree.getChildren().get(0).getLabel().getInnerScores()[j]);
							innerScores_Ai = innerScores_Ai.add(A_i2B_j.multiply(B_jInnerScore));
						}
						innerScores[i] = innerScores_Ai.doubleValue();
					}
					tree.getLabel().setInnerScores(innerScores);
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
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = bRuleBySameHeadWithScore
							.get(tree.getLabel().getSymbol()).get(tempBRule);
					Double[] innerScores = new Double[tree.getLabel().getNumSubSymbol()];
					for (int i = 0; i < innerScores.length; i++)
					{
						BigDecimal innerScores_Ai = BigDecimal.valueOf(0.0);
						for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
						{
							for (int k = 0; k < tree.getChildren().get(1).getLabel().getNumSubSymbol(); k++)
							{
								// 规则A_i -> B_j C_k的概率
								BigDecimal A_i2B_jC_k = BigDecimal.valueOf(bRuleScores.get(i).get(j).get(k));
								BigDecimal B_jInnerScore = BigDecimal
										.valueOf(tree.getChildren().get(0).getLabel().getInnerScores()[j]);
								BigDecimal C_kInnerScore = BigDecimal
										.valueOf(tree.getChildren().get(1).getLabel().getInnerScores()[j]);
								innerScores_Ai = innerScores_Ai
										.add(A_i2B_jC_k.multiply(B_jInnerScore).multiply(C_kInnerScore));
							}
						}
						innerScores[i] = innerScores_Ai.doubleValue();
					}
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
	public void calculateOuterScore(Tree<Annotation> tree)
	{
		calculateOuterScoreHelper(tree, tree, tree.getLabel().getSpanTo() - tree.getLabel().getSpanFrom() - 1);
	}

	private void calculateOuterScoreHelper(Tree<Annotation> treeRoot, Tree<Annotation> treeNode, int sentenceLength)
	{
		if (treeNode == null)
			return;
		if (treeNode.isLeaf())
			return;
		// 计算根节点的外向概率
		// calculateOuterScoreHelper(tree,tree.getLabel().getSpanTo() - 1);
		if (treeNode.getLabel().getSpanFrom() == 0 && treeNode.getLabel().getSpanTo() == sentenceLength + 1)
		{
			Double[] array = new Double[treeNode.getLabel().getSymbol()];
			Arrays.fill(array, 1);
			treeNode.getLabel().setOuterScores(array);
		}
		else
		{
			switch (treeNode.getChildren().size())
			{
			case 1:
				final UnaryRule tempUnaryRule = new UnaryRule(treeNode.getLabel().getSymbol(),
						treeNode.getChildren().get(0).getLabel().getSymbol());
				if (uRules.contains(tempUnaryRule))
				{
					LinkedList<LinkedList<Double>> uRuleScores = uRuleBySameHeadWithScore
							.get(treeNode.getLabel().getSymbol()).get(tempUnaryRule);
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int j = 0; j < outerScores.length; j++)
					{
						BigDecimal outerScores_Bj = BigDecimal.valueOf(0.0);
						Tree<Annotation> parent = treeNode.getParent(treeRoot);
						for (int i = 0; i < parent.getChildren().size(); i++)
						{
							BigDecimal A_i2B_j = BigDecimal.valueOf(uRuleScores.get(i).get(j));
							BigDecimal A_iOuterScore = BigDecimal.valueOf(parent.getLabel().getOuterScores()[i]);
							outerScores_Bj = outerScores_Bj.add(A_i2B_j.multiply(A_iOuterScore));
						}
						outerScores[j] = outerScores_Bj.doubleValue();
					}
				}
				else
				{
					throw new Error("Error grammar: don't contains  uRule :" + tempUnaryRule.toString());
				}

				break;
			case 2:
				//TODO:
				break;
			default:
				throw new Error("error tree:more than two children.");
			}
		}
		for (Tree<Annotation> childNode : treeNode.getChildren())
		{
			calculateOuterScoreHelper(treeRoot, childNode, sentenceLength);
		}

	}

	// public void calculateOuterScoreHelper(Tree<Annotation> tree,int
	// sentenceLength)
	// {
	// if(spanFrom == 0,spanTo == )
	// }

	public void fillRuleCountExpectation(Tree<Annotation> tree)
	{
		calculateRuleCountExpectation(0.0, new UnaryRule((short) 1, (short) 2), (short) 1, (short) 2);
	}

	public void calculateRuleCountExpectation(double count, Rule rule, short... subSymbolIndex)
	{
		// TODO:将count值填写到语法规则集的规则中去
	}

	public void refreshRuleScore()
	{

	}

	public double calculateLikelihood()
	{// 利用规则计算treeBank的似然值
		return 0.0;
	}

	public void writeGrammarToTxt() throws IOException
	{
		TreeMap<String, Double> allBAndURules = new TreeMap<String, Double>();
		TreeMap<String, Double> allPreRules = new TreeMap<String, Double>();
		for (BinaryRule bRule : bRules)
		{
			String parentStr = nonterminalTable.stringValue(bRule.parent);
			String leftChildStr = nonterminalTable.stringValue(bRule.getLeftChild());
			String rightChildStr = nonterminalTable.stringValue(bRule.getRightChild());
			Double score = bRule.scores.get(0).get(0).get(0);
			allBAndURules.put(parentStr + " ->" + leftChildStr + " " + rightChildStr, score);
		}
		for (UnaryRule uRule : uRules)
		{
			String parentStr = nonterminalTable.stringValue(uRule.parent);
			String childStr = nonterminalTable.stringValue(uRule.getChild());
			Double score = uRule.scores.get(0).get(0);
			allBAndURules.put(parentStr + " ->" + childStr, score);
		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			String parentStr = nonterminalTable.stringValue(preRule.parent);
			String childStr = preRule.word;
			Double score = preRule.scores.get(0);
			allPreRules.put(parentStr + " ->" + childStr, score);
		}

		FileOutputStream fos = new FileOutputStream("C:\\Users\\hp\\Desktop\\grammartest.grammar");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "gbk");
		BufferedWriter bAndURuleWriter = new BufferedWriter(osw);

		FileOutputStream fos2 = new FileOutputStream("C:\\Users\\hp\\Desktop\\preruletest.grammar");
		OutputStreamWriter osw2 = new OutputStreamWriter(fos2, "gbk");
		BufferedWriter preRuleWriter = new BufferedWriter(osw2);
		for (Map.Entry<String, Double> entry : allBAndURules.entrySet())
		{
			bAndURuleWriter.write(entry.getKey() + " " + entry.getValue().toString() + "\r");
		}
		for (Map.Entry<String, Double> entry : allPreRules.entrySet())
		{
			preRuleWriter.write(entry.getKey() + " " + entry.getValue().toString() + "\r");
		}
		bAndURuleWriter.close();
		preRuleWriter.close();
	}

}
