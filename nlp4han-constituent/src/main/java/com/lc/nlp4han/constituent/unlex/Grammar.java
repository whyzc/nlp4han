package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 表示由树库得到的语法
 * @author 王宁
 *  
 */
public class Grammar
{
	public static int iterations = 50;

	protected List<Tree<Annotation>> treeBank;

	protected HashSet<BinaryRule> bRules;
	protected HashSet<UnaryRule> uRules;
	protected Lexicon lexicon;// 包含preRules

	// 相同父节点的规则放在一个map中,拟删除
	protected HashMap<Short, HashMap<PreterminalRule, LinkedList<Double>>> preRuleBySameHeadWithScore;
	protected HashMap<Short, HashMap<BinaryRule, LinkedList<LinkedList<LinkedList<Double>>>>> bRuleBySameHeadWithScore;
	protected HashMap<Short, HashMap<UnaryRule, LinkedList<LinkedList<Double>>>> uRuleBySameHeadWithScore;
	// 相同父节点的规则放在一个map中
	protected HashMap<Short, HashMap<Integer, PreterminalRule>> preRuleBySameHead; // <hashcode/rule>
	protected HashMap<Short, HashMap<Integer, BinaryRule>> bRuleBySameHead;
	protected HashMap<Short, HashMap<Integer, UnaryRule>> uRuleBySameHead;

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
				refreshRuleCountExpectation(tree);
				TreeUtil.forgetScore(tree);
			}
			refreshRuleScore();

		}
		// forAllRule -> forgetRuleCountExpectation();
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
				int length = tree.getLabel().getNumSubSymbol();
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
										.valueOf(tree.getChildren().get(1).getLabel().getInnerScores()[k]);
								innerScores_Ai = innerScores_Ai
										.add(A_i2B_jC_k.multiply(B_jInnerScore).multiply(C_kInnerScore));
							}
						}
						innerScores[i] = innerScores_Ai.doubleValue();
					}
					tree.getLabel().setInnerScores(innerScores);
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
		if (tree == null)
			return;
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
			Double[] array = new Double[treeNode.getLabel().getNumSubSymbol()];
			Arrays.fill(array, 1);
			treeNode.getLabel().setOuterScores(array);
		}
		else
		{
			Tree<Annotation> parent = treeNode.getParent(treeRoot);
			switch (parent.getChildren().size())
			{
			case 1:
				final UnaryRule tempUnaryRule = new UnaryRule(parent.getLabel().getSymbol(),
						treeNode.getLabel().getSymbol());
				if (uRules.contains(tempUnaryRule))
				{
					LinkedList<LinkedList<Double>> uRuleScores = uRuleBySameHeadWithScore
							.get(parent.getLabel().getSymbol()).get(tempUnaryRule);
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int j = 0; j < outerScores.length; j++)
					{
						BigDecimal outerScores_Bj = BigDecimal.valueOf(0.0);
						for (int i = 0; i < parent.getChildren().size(); i++)
						{
							BigDecimal A_i2B_j = BigDecimal.valueOf(uRuleScores.get(i).get(j));
							BigDecimal A_iOuterScore = BigDecimal.valueOf(parent.getLabel().getOuterScores()[i]);
							outerScores_Bj = outerScores_Bj.add(A_i2B_j.multiply(A_iOuterScore));
						}
						outerScores[j] = outerScores_Bj.doubleValue();
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
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = bRuleBySameHeadWithScore
							.get(parent.getLabel().getSymbol()).get(tempBRule);
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int i = 0; i < outerScores.length; i++)
					{
						BigDecimal outerScoreB_i = BigDecimal.valueOf(0.0);
						for (int j = 0; j < parent.getLabel().getNumSubSymbol(); j++)
						{
							BigDecimal A_jOuterscore = BigDecimal.valueOf(parent.getLabel().getOuterScores()[j]);
							for (int k = 0; k < siblingNode_InScore.length; k++)
							{
								BigDecimal C_kInnerScore = BigDecimal.valueOf(siblingNode_InScore[k]);
								if (parent.getChildren().get(0) == treeNode)
								{

									BigDecimal A_j2B_iC_k = BigDecimal.valueOf(bRuleScores.get(j).get(i).get(k));
									outerScoreB_i = outerScoreB_i
											.add(A_j2B_iC_k.multiply(A_jOuterscore).multiply(C_kInnerScore));

								}
								else
								{
									BigDecimal A_j2C_kB_i = BigDecimal.valueOf(bRuleScores.get(j).get(k).get(i));
									outerScoreB_i = outerScoreB_i
											.add(A_j2C_kB_i.multiply(A_jOuterscore).multiply(C_kInnerScore));
								}

							}
						}
						outerScores[i] = outerScoreB_i.doubleValue();
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
		for (Tree<Annotation> childNode : treeNode.getChildren())
		{
			calculateOuterScoreHelper(treeRoot, childNode, sentenceLength);
		}

	}

	public void refreshRuleCountExpectation(Tree<Annotation> tree)
	{
		if (tree.getChildren().size() == 0 || tree == null)
			return;
		Rule rule = null;
		if (tree.getChildren().size() == 2)
		{
			rule = new BinaryRule(tree.getLabel().getSymbol(), tree.getChildren().get(0).getLabel().getSymbol(),
					tree.getChildren().get(1).getLabel().getSymbol());
			LinkedList<LinkedList<LinkedList<Double>>> scores = bRuleBySameHead.get(tree.getLabel().getSymbol())
					.get(rule.hashCode()).getScores();
			double[][][] count = bRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule.hashCode())
					.getCountExpectation();
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
						count[i][j][k] = BigDecimal.valueOf(count[i][j][k]).add(BigDecimal
								.valueOf(tree.getLabel().getOuterScores()[i])
								.multiply(BigDecimal.valueOf(scores.get(i).get(j).get(k)))
								.multiply(BigDecimal.valueOf(tree.getChildren().get(0).getLabel().getInnerScores()[j]))
								.multiply(BigDecimal.valueOf(tree.getChildren().get(1).getLabel().getInnerScores()[k])))
								.doubleValue();
					}
				}
			}
			bRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule.hashCode()).setCountExpectation(count);
		}
		else if (tree.getChildren().size() == 1 && tree.getChildren().get(0).getLabel().getWord() == null)
		{
			rule = new UnaryRule(tree.getLabel().getSymbol(), tree.getChildren().get(0).getLabel().getSymbol());
			LinkedList<LinkedList<Double>> scores = uRuleBySameHead.get(tree.getLabel().getSymbol())
					.get(rule.hashCode()).getScores();
			double[][] count = uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule.hashCode())
					.getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()][tree.getChildren().get(0).getLabel()
						.getNumSubSymbol()];
			}
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
				{

					count[i][j] = BigDecimal.valueOf(count[i][j])
							.add(BigDecimal.valueOf(tree.getLabel().getOuterScores()[i])
									.multiply(BigDecimal.valueOf(scores.get(i).get(j)))
									.multiply(BigDecimal
											.valueOf(tree.getChildren().get(0).getLabel().getInnerScores()[j])))
							.doubleValue();
				}
			}
			uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule.hashCode()).setCountExpectation(count);
		}
		else if (tree.getChildren().size() == 1 && tree.getChildren().get(0).getLabel().getWord() != null)
		{
			rule = new PreterminalRule(tree.getLabel().getSymbol(), tree.getChildren().get(0).getLabel().getWord());
			LinkedList<Double> scores = preRuleBySameHead.get(rule.getParent()).get(rule.hashCode()).getScores();
			double[] count = preRuleBySameHead.get(rule.getParent()).get(rule.hashCode()).getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()];
			}
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				count[i] = BigDecimal.valueOf(count[i]).add(BigDecimal.valueOf(tree.getLabel().getOuterScores()[i])
						.multiply(BigDecimal.valueOf(scores.get(i)))).doubleValue();
			}
		}
		else if (tree.getChildren().size() > 2)
			throw new Error("error tree:more than 2 children.");

		for (Tree<Annotation> child : tree.getChildren())
		{
			refreshRuleCountExpectation(child);
		}
	}

	public void refreshRuleScore()
	{
		// 使用规则数量的期望的比作为新的规则概率
		HashMap<Integer, HashMap<Integer, BigDecimal>> sameParentRulesCount = new HashMap<>();// <parent,<ParentSubIndex,denominator>>
		for (BinaryRule bRule : bRules)
		{

			int pNumSub = bRule.getCountExpectation().length;
			int lCNumSub = bRule.getCountExpectation()[0].length;
			int rCNumSub = bRule.getCountExpectation()[0][0].length;

			for (int i = 0; i < pNumSub; i++)
			{
				BigDecimal denominator;
				if (sameParentRulesCount.containsKey((int) bRule.parent)
						&& sameParentRulesCount.get((int) bRule.parent).containsKey(i))
				{
					denominator = sameParentRulesCount.get((int) bRule.parent).get(i);
				}
				else
				{
					denominator = calculateSameParentRuleCount(sameParentRulesCount, bRule.parent, i);
				}
				for (int j = 0; j < lCNumSub; j++)
				{
					for (int k = 0; k < rCNumSub; k++)
					{
						bRule.getScores().get(i).get(j).set(k, BigDecimal.valueOf(bRule.getCountExpectation()[i][j][k])
								.divide(denominator, 15, BigDecimal.ROUND_HALF_UP).doubleValue());
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
				BigDecimal denominator;
				if (sameParentRulesCount.containsKey((int) uRule.parent)
						&& sameParentRulesCount.get((int) uRule.parent).containsKey(i))
				{
					denominator = sameParentRulesCount.get((int) uRule.parent).get(i);
				}
				else
				{
					denominator = calculateSameParentRuleCount(sameParentRulesCount, uRule.parent, i);
				}
				for (int j = 0; j < cNumSub; j++)
				{

					uRule.getScores().get(i).set(j, BigDecimal.valueOf(uRule.getCountExpectation()[i][j])
							.divide(denominator, 15, BigDecimal.ROUND_HALF_UP).doubleValue());

				}
			}
		}

		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			int pNumSub = preRule.getCountExpectation().length;
			for (int i = 0; i < pNumSub; i++)
			{
				BigDecimal denominator;
				if (sameParentRulesCount.containsKey((int) preRule.parent)
						&& sameParentRulesCount.get((int) preRule.parent).containsKey(i))
				{
					denominator = sameParentRulesCount.get((int) preRule.parent).get(i);
				}
				else
				{
					denominator = calculateSameParentRuleCount(sameParentRulesCount, preRule.parent, i);
				}

				preRule.getScores().set(i, BigDecimal.valueOf(preRule.getCountExpectation()[i])
						.divide(denominator, 15, BigDecimal.ROUND_HALF_UP).doubleValue());

			}
		}
	}
	//TODO:
	public BigDecimal calculateSameParentRuleCount(HashMap<Integer, HashMap<Integer, BigDecimal>> sameParentRulesCount,
			int parent, int pSubSymbolIndex)
	{
		return null;
	}

	public double calculateLikelihood()
	{// 利用规则计算treeBank的似然值
		return 0.0;
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
