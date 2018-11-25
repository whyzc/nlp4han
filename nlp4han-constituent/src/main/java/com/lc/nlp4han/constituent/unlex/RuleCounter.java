package com.lc.nlp4han.constituent.unlex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 记录规则的期望次数
 * 
 * @author 王宁
 */
public class RuleCounter
{
	protected HashMap<BinaryRule, double[][][]> bRuleCounter;
	protected HashMap<UnaryRule, double[][]> uRuleCounter;
	protected HashMap<PreterminalRule, double[]> preRuleCounter;
	protected HashMap<Short, Double[]> sameParentRulesCounter;// <parent,[ParentSubIndex,sum]>
	protected HashMap<Short, double[]> sameTagToUNKCounter;// 记录tag_i-->UNK 的期望

	public RuleCounter()
	{
		bRuleCounter = new HashMap<>();
		uRuleCounter = new HashMap<>();
		preRuleCounter = new HashMap<>();
		sameParentRulesCounter = new HashMap<>();
		sameTagToUNKCounter = new HashMap<Short, double[]>();
	}

	public void calRuleExpectation(Grammar g, TreeBank treeBank)
	{
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			refreshRuleCountExpectation(g, tree, tree);
		}
		calSameParentRulesExpectation(g);
	}

	public void calSameParentRulesExpectation(Grammar g)
	{
		for (short pSymbol = 0; pSymbol < g.getNumSymbol(); pSymbol++)
		{
			Double[] count = new Double[g.getNumSubSymbol((short) pSymbol)];
			for (int i = 0; i < count.length; i++)
			{
				count[i] = 0.0;
			}
			if (g.getbRuleBySameHead().containsKey(pSymbol))
			{
				for (Map.Entry<BinaryRule, BinaryRule> entry : g.getbRuleBySameHead().get(pSymbol).entrySet())
				{
					double[][][] ruleCount = bRuleCounter.get(entry.getValue());
					for (int pSubSymbol = 0; pSubSymbol < count.length; pSubSymbol++)
					{
						double tempCount = 0.0;
						for (double[] countArr : ruleCount[pSubSymbol])
						{
							for (double subRuleCount : countArr)
							{
								tempCount += subRuleCount;
							}
						}
						count[pSubSymbol] += tempCount;
					}
				}
			}
			if (g.getuRuleBySameHead().containsKey(pSymbol))
			{
				for (Map.Entry<UnaryRule, UnaryRule> entry : g.getuRuleBySameHead().get(pSymbol).entrySet())
				{
					double[][] ruleCount = uRuleCounter.get(entry.getValue());
					for (int pSubSymbol = 0; pSubSymbol < count.length; pSubSymbol++)
					{
						double tempCount = 0.0;
						for (double subRuleCount : ruleCount[pSubSymbol])
						{
							tempCount += subRuleCount;
						}
						count[pSubSymbol] += tempCount;
					}
				}
			}
			if (g.getPreRuleBySameHead().containsKey(pSymbol))
			{
				for (Map.Entry<PreterminalRule, PreterminalRule> entry : g.getPreRuleBySameHead().get(pSymbol)
						.entrySet())
				{
					double[] ruleCount = preRuleCounter.get(entry.getValue());
					for (int pSubSymbol = 0; pSubSymbol < count.length; pSubSymbol++)
					{
						count[pSubSymbol] += ruleCount[pSubSymbol];
					}
				}
			}
			sameParentRulesCounter.put(pSymbol, count);
		}
	}

	public void refreshRuleCountExpectation(Grammar g, AnnotationTreeNode root, AnnotationTreeNode tree)
	{

		if (tree.getChildren().size() == 0 || tree == null)
			return;
		double scalingFactor;
		if (tree.getChildren().size() == 2)
		{

			AnnotationTreeNode lC = tree.getChildren().get(0);
			AnnotationTreeNode rC = tree.getChildren().get(1);
			BinaryRule rule = new BinaryRule(tree.getLabel().getSymbol(), lC.getLabel().getSymbol(),
					rC.getLabel().getSymbol());
			rule = g.getbRuleBySameHead().get(tree.getLabel().getSymbol()).get(rule);
			LinkedList<LinkedList<LinkedList<Double>>> scores = rule.getScores();
			double[][][] count;
			if (!bRuleCounter.containsKey(rule))
			{
				count = new double[g.getNumSubSymbol(tree.getLabel().getSymbol())][g
						.getNumSubSymbol(lC.getLabel().getSymbol())][g.getNumSubSymbol(rC.getLabel().getSymbol())];
				bRuleCounter.put(rule, count);
			}
			else
			{
				count = bRuleCounter.get(rule);
			}
			scalingFactor = ScalingTools.calcScaleFactor(tree.getLabel().getOuterScale() + lC.getLabel().getInnerScale()
					+ rC.getLabel().getInnerScale() - root.getLabel().getInnerScale());
			for (int i = 0; i < g.getNumSubSymbol(tree.getLabel().getSymbol()); i++)
			{
				for (int j = 0; j < g.getNumSubSymbol(tree.getChildren().get(0).getLabel().getSymbol()); j++)
				{
					for (int k = 0; k < g.getNumSubSymbol(tree.getChildren().get(1).getLabel().getSymbol()); k++)
					{
						count[i][j][k] = count[i][j][k]
								+ (scores.get(i).get(j).get(k) * lC.getLabel().getInnerScores()[j]
										/ root.getLabel().getInnerScores()[0] * rC.getLabel().getInnerScores()[k]
										* scalingFactor * tree.getLabel().getOuterScores()[i]);
					}
				}
			}
		}
		else if (tree.getChildren().size() == 1 && tree.getChildren().get(0).getLabel().getWord() == null)
		{
			AnnotationTreeNode child = tree.getChildren().get(0);
			UnaryRule rule = new UnaryRule(tree.getLabel().getSymbol(), child.getLabel().getSymbol());
			rule = g.getuRuleBySameHead().get(tree.getLabel().getSymbol()).get(rule);
			LinkedList<LinkedList<Double>> scores = rule.getScores();
			double[][] count;
			if (!uRuleCounter.containsKey(rule))
			{
				count = new double[g.getNumSubSymbol(tree.getLabel().getSymbol())][g.getNumSubSymbol(tree.getChildren().get(0).getLabel()
						.getSymbol())];
				uRuleCounter.put(rule, count);
			}
			else
			{
				count = uRuleCounter.get(rule);
			}
			scalingFactor = ScalingTools.calcScaleFactor(tree.getLabel().getOuterScale()
					+ child.getLabel().getInnerScale() - root.getLabel().getInnerScale());
			for (int i = 0; i < g.getNumSubSymbol(tree.getLabel().getSymbol()); i++)
			{
				for (int j = 0; j < g.getNumSubSymbol(tree.getChildren().get(0).getLabel().getSymbol()); j++)
				{
					count[i][j] = count[i][j] + (scores.get(i).get(j) * child.getLabel().getInnerScores()[j]
							/ root.getLabel().getInnerScores()[0] * scalingFactor
							* tree.getLabel().getOuterScores()[i]);
				}
			}
		}
		else if (tree.isPreterminal())
		{
			PreterminalRule rule = new PreterminalRule(tree.getLabel().getSymbol(),
					tree.getChildren().get(0).getLabel().getWord());
			rule = g.getPreRuleBySameHead().get(rule.getParent()).get(rule);
			LinkedList<Double> scores = rule.getScores();
			double[] count;
			if (!preRuleCounter.containsKey(rule))
			{
				count = new double[g.getNumSubSymbol(tree.getLabel().getSymbol())];
				preRuleCounter.put(rule, count);
			}
			else
			{
				count = preRuleCounter.get(rule);
			}
			scalingFactor = ScalingTools
					.calcScaleFactor(tree.getLabel().getOuterScale() - root.getLabel().getInnerScale());
			double tempCount = 0.0;
			for (int i = 0; i <g.getNumSubSymbol(tree.getLabel().getSymbol()) ; i++)
			{
				tempCount = scores.get(i) / root.getLabel().getInnerScores()[0] * scalingFactor
						* tree.getLabel().getOuterScores()[i];
				count[i] = count[i] + tempCount;
				if (g.isRareWord(rule.getWord()))
				{
					if (!sameTagToUNKCounter.containsKey(rule.getParent()))
					{
						sameTagToUNKCounter.put(rule.getParent(), new double[g.getNumSubSymbol(tree.getLabel().getSymbol())]);
					}
					sameTagToUNKCounter.get(rule.getParent())[i] = sameTagToUNKCounter.get(rule.getParent())[i]
							+ tempCount;
				}
			}
		}
		else if (tree.getChildren().size() > 2)
			throw new Error("error tree:more than 2 children.");

		for (AnnotationTreeNode child : tree.getChildren())
		{
			refreshRuleCountExpectation(g, root, child);
		}
	}
}
