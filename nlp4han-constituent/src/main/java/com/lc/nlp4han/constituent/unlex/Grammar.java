package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	protected List<AnnotationTreeNode> treeBank;

	protected HashSet<BinaryRule> bRules;
	protected HashSet<UnaryRule> uRules;
	protected Lexicon lexicon;// 包含preRules

	// 添加相同孩子为key的map
	protected HashMap<Integer, HashMap<Integer, PreterminalRule>> preRuleBySameChildren; // 外层map<childrenHashcode,内map>,内map<ruleHashcode/rule>
	protected HashMap<Integer, HashMap<Integer, BinaryRule>> bRuleBySameChildren;
	protected HashMap<Integer, HashMap<Integer, UnaryRule>> uRuleBySameChildren;
	// 相同父节点的规则放在一个map中
	protected HashMap<Short, HashMap<Integer, PreterminalRule>> preRuleBySameHead; // 内map<ruleHashcode/rule>
	protected HashMap<Short, HashMap<Integer, BinaryRule>> bRuleBySameHead;
	protected HashMap<Short, HashMap<Integer, UnaryRule>> uRuleBySameHead;

	protected NonterminalTable nonterminalTable;

	public Grammar(List<AnnotationTreeNode> treeBank, HashSet<BinaryRule> bRules, HashSet<UnaryRule> uRules,
			Lexicon lexicon, HashMap<Integer, HashMap<Integer, BinaryRule>> bRuleBySameChildren,
			HashMap<Integer, HashMap<Integer, UnaryRule>> uRuleBySameChildren,
			HashMap<Integer, HashMap<Integer, PreterminalRule>> preRuleBySameChildren,
			HashMap<Short, HashMap<Integer, BinaryRule>> bRuleBySameHead,
			HashMap<Short, HashMap<Integer, UnaryRule>> uRuleBySameHead,
			HashMap<Short, HashMap<Integer, PreterminalRule>> preRuleBySameHead, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.bRules = bRules;
		this.uRules = uRules;
		this.lexicon = lexicon;
		this.bRuleBySameChildren = bRuleBySameChildren;
		this.uRuleBySameChildren = uRuleBySameChildren;
		this.preRuleBySameChildren = preRuleBySameChildren;
		this.bRuleBySameHead = bRuleBySameHead;
		this.uRuleBySameHead = uRuleBySameHead;
		this.preRuleBySameHead = preRuleBySameHead;
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
		for (int i = 0; i < 1; i++)
		{
			for (AnnotationTreeNode tree : treeBank)
			{
				calculateInnerScore(tree);
				calculateOuterScore(tree);
				refreshRuleCountExpectation(tree, tree);
				tree.forgetScore();
			}
			refreshRuleScore();
			// forAllRule -> forgetRuleCountExpectation();
			// System.out.println("第" + i + "次EM结束");
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
				// double[] arr = Arrays.stream(new
				// Double[10]).mapToDouble(Double::valueOf).toArray();
				int length = tree.getLabel().getNumSubSymbol();

				PreterminalRule realRule = preRuleBySameHead.get(tree.getLabel().getSymbol())
						.get(tempPreRule.hashCode());
				// for (double score : realRule.getScores())
				// System.out.println(score);
				tree.getLabel().setInnerScores(realRule.getScores().toArray(new Double[length]));

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
							.get(tempUnaryRule.hashCode()).getScores();
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
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = bRuleBySameHead
							.get(tree.getLabel().getSymbol()).get(tempBRule.hashCode()).getScores();
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
							.get(tempUnaryRule.hashCode()).getScores();
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int j = 0; j < outerScores.length; j++)
					{
						BigDecimal outerScores_Bj = BigDecimal.valueOf(0.0);
						for (int i = 0; i < parent.getLabel().getNumSubSymbol(); i++)
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
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = bRuleBySameHead
							.get(parent.getLabel().getSymbol()).get(tempBRule.hashCode()).getScores();
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
								.multiply(BigDecimal.valueOf(tree.getChildren().get(1).getLabel().getInnerScores()[k]))
								.divide(BigDecimal.valueOf(root.getLabel().getInnerScores()[0]), 15,
										BigDecimal.ROUND_HALF_UP))
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
					System.out.println(count[i][j]);
					count[i][j] = BigDecimal.valueOf(count[i][j])
							.add(BigDecimal.valueOf(tree.getLabel().getOuterScores()[i])
									.multiply(BigDecimal.valueOf(scores.get(i).get(j)))
									.multiply(BigDecimal
											.valueOf(tree.getChildren().get(0).getLabel().getInnerScores()[j]))
									.divide(BigDecimal.valueOf(root.getLabel().getInnerScores()[0]), 15,
											BigDecimal.ROUND_HALF_UP))
							.doubleValue();
				}
			}
			uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule.hashCode()).setCountExpectation(count);
		}
		else if (tree.isPreterminal())
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
						.multiply(BigDecimal.valueOf(scores.get(i)))
						.divide(BigDecimal.valueOf(root.getLabel().getInnerScores()[0]), 15, BigDecimal.ROUND_HALF_UP))
						.doubleValue();
			}
			preRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule.hashCode()).setCountExpectation(count);
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
		// 使用规则数量的期望的比作为新的规则概率
		HashMap<Short, BigDecimal[]> sameParentRulesCount = new HashMap<>();// <parent,<ParentSubIndex,denominator>>
		for (BinaryRule bRule : bRules)
		{
			int pNumSub = bRule.getCountExpectation().length;
			int lCNumSub = bRule.getCountExpectation()[0].length;
			int rCNumSub = bRule.getCountExpectation()[0][0].length;

			for (int i = 0; i < pNumSub; i++)
			{
				BigDecimal denominator;
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

					uRule.getScores().get(i).set(j, BigDecimal.valueOf(uRule.getCountExpectation()[i][j])
							.divide(denominator, 15, BigDecimal.ROUND_HALF_UP).doubleValue());

				}
			}
		}

		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			preRule.getCountExpectation();
			int pNumSub = preRule.getCountExpectation().length;
			for (int i = 0; i < pNumSub; i++)
			{
				BigDecimal denominator;
				if (sameParentRulesCount.containsKey(preRule.parent)
						&& sameParentRulesCount.get(preRule.parent)[i] != null)
				{
					denominator = sameParentRulesCount.get(preRule.parent)[i];
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

	/*
	 * 如果表格中(parent,pSubsymbolIndex)位置没有数值表示该值该没有计算，否则直接从表格中取值
	 */
	public BigDecimal calculateSameParentRuleCount(HashMap<Short, BigDecimal[]> sameParentRulesCount, int parent,
			int pSubSymbolIndex)
	{
		if (!sameParentRulesCount.containsKey((short) parent)
				|| sameParentRulesCount.get((short) parent)[pSubSymbolIndex] == null)
		{
			BigDecimal ruleCount = BigDecimal.valueOf(0.0);
			if (bRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<Integer, BinaryRule> entry : bRuleBySameHead.get((short) parent).entrySet())
				{
					double[][][] count = entry.getValue().getCountExpectation();
					for (int i = 0; i < count[pSubSymbolIndex].length; i++)
					{
						for (int j = 0; j < count[pSubSymbolIndex][i].length; j++)
						{
							ruleCount = ruleCount.add(BigDecimal.valueOf(count[pSubSymbolIndex][i][j]));
						}
					}
				}
			if (uRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<Integer, UnaryRule> entry : uRuleBySameHead.get((short) parent).entrySet())
				{
					double[][] count = entry.getValue().getCountExpectation();
					for (int i = 0; i < count[pSubSymbolIndex].length; i++)
					{
						ruleCount = ruleCount.add(BigDecimal.valueOf(count[pSubSymbolIndex][i]));
					}
				}
			if (preRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<Integer, PreterminalRule> entry : preRuleBySameHead.get((short) parent).entrySet())
				{
					double[] count = entry.getValue().getCountExpectation();
					ruleCount = ruleCount.add(BigDecimal.valueOf(count[pSubSymbolIndex]));
				}
			if (sameParentRulesCount.containsKey((short) parent))
			{
				sameParentRulesCount.get((short) parent)[pSubSymbolIndex] = ruleCount;
			}
			else
			{
				BigDecimal[] countArr = new BigDecimal[nonterminalTable.getNumSubsymbolArr().get(parent)];
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

}
