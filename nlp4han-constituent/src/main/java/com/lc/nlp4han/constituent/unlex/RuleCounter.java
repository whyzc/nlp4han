package com.lc.nlp4han.constituent.unlex;

import java.util.HashMap;
import java.util.LinkedList;

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

	public RuleCounter()
	{
		bRuleCounter = new HashMap<>();
		uRuleCounter = new HashMap<>();
		preRuleCounter = new HashMap<>();
		sameParentRulesCounter = new HashMap<>();
	}

	public double calRuleExpectationAndTreeBankLSS(Grammar g, TreeBank treeBank)
	{
		double lss = 0;
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			TreeBank.calculateInnerScore(g, tree);
			TreeBank.calculateOuterScore(g, tree);
			refreshRuleCountExpectation(g, tree, tree);
			lss += TreeBank.calLogSentenceSocre(tree);
			tree.forgetIOScoreAndScale();
		}
		return lss;
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
				count = new double[tree.getLabel().getNumSubSymbol()][lC.getLabel().getNumSubSymbol()][rC.getLabel()
						.getNumSubSymbol()];
				bRuleCounter.put(rule, count);
			}
			else
			{
				count = bRuleCounter.get(rule);
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
				count = new double[tree.getLabel().getNumSubSymbol()][tree.getChildren().get(0).getLabel()
						.getNumSubSymbol()];
				uRuleCounter.put(rule, count);
			}
			else
			{
				count = uRuleCounter.get(rule);
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
				count = new double[tree.getLabel().getNumSubSymbol()];
				preRuleCounter.put(rule, count);
			}
			else
			{
				count = preRuleCounter.get(rule);
			}
			scalingFactor = ScalingTools
					.calcScaleFactor(tree.getLabel().getOuterScale() - root.getLabel().getInnerScale());
			for (int i = 0; i < tree.getLabel().getNumSubSymbol(); i++)
			{
				count[i] = count[i] + (scores.get(i) / root.getLabel().getInnerScores()[0] * scalingFactor
						* tree.getLabel().getOuterScores()[i]);
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
