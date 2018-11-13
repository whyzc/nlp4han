package com.lc.nlp4han.constituent.unlex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 语法训练器
 * 
 * @author 王宁
 */
public class GrammarTrainer
{
	public static int EMIterations = 50;

	public static Grammar train(Grammar g, TreeBank treeBank, int SMCycle, double mergeRate, int EMIterations)
	{
		System.out.println("SMCycle:" + SMCycle);
		for (int i = 0; i < SMCycle; i++)
		{
			GrammarSpliter.splitGrammar(g, treeBank);
			EM(g, treeBank, EMIterations);
			System.err.println("分裂完成。");
			GrammarMerger.mergeGrammar(g, treeBank, mergeRate);
			EM(g, treeBank, EMIterations);
			g.sameParentRulesCount = new HashMap<>();
			treeBank.forgetIOScoreAndScale();
			System.err.println("合并完成。");
		}
		return g;
	}

	/**
	 * 将处理后的语法期望最大化，得到新的规则
	 */
	public static void EM(Grammar g, TreeBank treeBank, int iterations)
	{
		for (int i = 0; i < iterations; i++)
		{
			for (AnnotationTreeNode tree : treeBank.getTreeBank())
			{
				// System.out.println("计算第" + count + "颗树的内外向概率。");
				TreeBank.calculateInnerScore(g, tree);
				TreeBank.calculateOuterScore(g, tree);
				refreshRuleCountExpectation(g, tree, tree);
				if (i != iterations - 1)
				{
					tree.forgetIOScoreAndScale();
				}
			}
			refreshRuleScore(g);
			if (i != iterations - 1)
			{
				g.sameParentRulesCount = new HashMap<>();
			}
			g.forgetRuleCountExpectation();
			// System.out.println("第" + (i + 1) + "次EM结束");
		}
		System.out.println("EM算法结束。");
	}

	public static void refreshRuleCountExpectation(Grammar g, AnnotationTreeNode root, AnnotationTreeNode tree)
	{

		if (tree.getChildren().size() == 0 || tree == null)
			return;
		Rule rule = null;
		double scalingFactor;
		if (tree.getChildren().size() == 2)
		{

			AnnotationTreeNode lC = tree.getChildren().get(0);
			AnnotationTreeNode rC = tree.getChildren().get(1);
			rule = new BinaryRule(tree.getLabel().getSymbol(), lC.getLabel().getSymbol(), rC.getLabel().getSymbol());
			LinkedList<LinkedList<LinkedList<Double>>> scores = g.bRuleBySameHead.get(tree.getLabel().getSymbol())
					.get(rule).getScores();
			double[][][] count = g.bRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()][lC.getLabel().getNumSubSymbol()][rC.getLabel()
						.getNumSubSymbol()];
			}
			scalingFactor = ScalingTools.calcScaleFactor(tree.getLabel().getOuterScale() + lC.getLabel().getInnerScale()
					+ rC.getLabel().getInnerScale() - root.getLabel().getInnerScale());
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
				{
					for (int k = 0; k < tree.getChildren().get(1).getLabel().getNumSubSymbol(); k++)
					{
						count[i][j][k] = count[i][j][k]
								+ (scores.get(i).get(j).get(k) * lC.getLabel().getInnerScores()[j]
										/ root.getLabel().getInnerScores()[0] * rC.getLabel().getInnerScores()[k]
										* scalingFactor * tree.getLabel().getOuterScores()[i]);
					}
				}
			}
			g.bRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).setCountExpectation(count);
		}
		else if (tree.getChildren().size() == 1 && tree.getChildren().get(0).getLabel().getWord() == null)
		{
			AnnotationTreeNode child = tree.getChildren().get(0);
			rule = new UnaryRule(tree.getLabel().getSymbol(), child.getLabel().getSymbol());
			LinkedList<LinkedList<Double>> scores = g.uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule)
					.getScores();
			double[][] count = g.uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()][tree.getChildren().get(0).getLabel()
						.getNumSubSymbol()];
			}
			scalingFactor = ScalingTools.calcScaleFactor(tree.getLabel().getOuterScale()
					+ child.getLabel().getInnerScale() - root.getLabel().getInnerScale());
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
				{
					count[i][j] = count[i][j] + (scores.get(i).get(j) * child.getLabel().getInnerScores()[j]
							/ root.getLabel().getInnerScores()[0] * scalingFactor
							* tree.getLabel().getOuterScores()[i]);
				}
			}
			g.uRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).setCountExpectation(count);
		}
		else if (tree.isPreterminal())
		{
			rule = new PreterminalRule(tree.getLabel().getSymbol(), tree.getChildren().get(0).getLabel().getWord());
			LinkedList<Double> scores = g.preRuleBySameHead.get(rule.getParent()).get(rule).getScores();
			double[] count = g.preRuleBySameHead.get(rule.getParent()).get(rule).getCountExpectation();
			if (count == null)
			{
				count = new double[tree.getLabel().getNumSubSymbol()];
			}
			scalingFactor = ScalingTools
					.calcScaleFactor(tree.getLabel().getOuterScale() - root.getLabel().getInnerScale());
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				// System.out.println(TreeBank.nonterminalTable.stringValue(tree.getLabel().getSymbol())
				// + "_" + i + " "
				// + tree.getLabel().getOuterScores()[i]);
				count[i] = count[i] + (scores.get(i) / root.getLabel().getInnerScores()[0] * scalingFactor
						* tree.getLabel().getOuterScores()[i]);

			}
			g.preRuleBySameHead.get(tree.getLabel().getSymbol()).get(rule).setCountExpectation(count);
		}
		else if (tree.getChildren().size() > 2)
			throw new Error("error tree:more than 2 children.");

		for (AnnotationTreeNode child : tree.getChildren())
		{
			refreshRuleCountExpectation(g, root, child);
		}
	}

	/**
	 * 跟新规则的scores
	 * 
	 * @param g
	 *            语法
	 */
	public static void refreshRuleScore(Grammar g)
	{
		double newScore;
		double denominator;
		for (BinaryRule bRule : g.bRules)
		{

			int pNumSub = bRule.getCountExpectation().length;
			int lCNumSub = bRule.getCountExpectation()[0].length;
			int rCNumSub = bRule.getCountExpectation()[0][0].length;

			for (int i = 0; i < pNumSub; i++)
			{

				if (g.sameParentRulesCount.containsKey(bRule.parent)
						&& g.sameParentRulesCount.get(bRule.parent)[i] != null)
				{
					denominator = g.sameParentRulesCount.get(bRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(g, bRule.parent, i);
				}
				for (int j = 0; j < lCNumSub; j++)
				{
					for (int k = 0; k < rCNumSub; k++)
					{
						newScore = bRule.getCountExpectation()[i][j][k] / denominator;
						if (newScore < Rule.rulethres)
						{
							newScore = 0.0;
						}

						bRule.getScores().get(i).get(j).set(k, newScore);
					}
				}
			}

		}

		for (UnaryRule uRule : g.uRules)
		{
			int pNumSub = uRule.getCountExpectation().length;
			int cNumSub = uRule.getCountExpectation()[0].length;
			for (int i = 0; i < pNumSub; i++)
			{
				if (g.sameParentRulesCount.containsKey(uRule.parent)
						&& g.sameParentRulesCount.get(uRule.parent)[i] != null)
				{
					denominator = g.sameParentRulesCount.get(uRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(g, uRule.parent, i);
				}
				for (int j = 0; j < cNumSub; j++)
				{
					newScore = uRule.getCountExpectation()[i][j] / denominator;
					if (newScore < Rule.rulethres)
					{
						newScore = 0.0;
					}
					uRule.getScores().get(i).set(j, newScore);

				}
			}
		}

		for (PreterminalRule preRule : g.lexicon.getPreRules())
		{
			preRule.getCountExpectation();
			int pNumSub = preRule.getCountExpectation().length;
			for (int i = 0; i < pNumSub; i++)
			{
				if (g.sameParentRulesCount.containsKey(preRule.parent)
						&& g.sameParentRulesCount.get(preRule.parent)[i] != null)
				{
					denominator = g.sameParentRulesCount.get(preRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(g, preRule.parent, i);
				}
				newScore = preRule.getCountExpectation()[i] / denominator;
				if (newScore < Rule.rulethres)
				{
					newScore = 0.0;
				}
				preRule.getScores().set(i, newScore);
			}
		}
	}

	/*
	 * 如果表格中(parent,pSubsymbolIndex)位置没有数值表示该值该没有计算，否则直接从表格中取值
	 */
	public static Double calculateSameParentRuleCount(Grammar g, int parent, int pSubSymbolIndex)
	{
		if (!g.sameParentRulesCount.containsKey((short) parent)
				|| g.sameParentRulesCount.get((short) parent)[pSubSymbolIndex] == null)
		{
			double ruleCount = 0.0;
			if (g.bRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<BinaryRule, BinaryRule> entry : g.bRuleBySameHead.get((short) parent).entrySet())
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
			if (g.uRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<UnaryRule, UnaryRule> entry : g.uRuleBySameHead.get((short) parent).entrySet())
				{
					double[][] count = entry.getValue().getCountExpectation();
					for (int i = 0; i < count[pSubSymbolIndex].length; i++)
					{
						ruleCount = ruleCount + count[pSubSymbolIndex][i];
					}
				}
			if (g.preRuleBySameHead.containsKey((short) parent))
				for (Map.Entry<PreterminalRule, PreterminalRule> entry : g.preRuleBySameHead.get((short) parent)
						.entrySet())
				{
					double[] count = entry.getValue().getCountExpectation();
					ruleCount = ruleCount + count[pSubSymbolIndex];
				}
			if (g.sameParentRulesCount.containsKey((short) parent))
			{
				g.sameParentRulesCount.get((short) parent)[pSubSymbolIndex] = ruleCount;
			}
			else
			{
				Double[] countArr = new Double[g.nonterminalTable.getNumSubsymbolArr().get(parent)];
				countArr[pSubSymbolIndex] = ruleCount;
				g.sameParentRulesCount.put((short) parent, countArr);
			}
			return ruleCount;
		}
		else
		{
			return g.sameParentRulesCount.get((short) parent)[pSubSymbolIndex];
		}
	}
}
