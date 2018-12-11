package com.lc.nlp4han.constituent.unlex;

import java.util.HashMap;
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
		int count = 0;
		double start = System.currentTimeMillis();
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			refreshRuleCountExpectation(g, tree, tree);
			// if (++count % 100 == 0)
			// {
			// double end = System.currentTimeMillis();
			// System.out.println(count / (end - start) * 1000.0 + "/s");
			// }
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
		Annotation rootLabel = root.getLabel();
		Annotation pLabel = tree.getLabel();
		if (tree.getChildren().size() == 2)
		{

			AnnotationTreeNode lC = tree.getChildren().get(0);
			AnnotationTreeNode rC = tree.getChildren().get(1);
			Annotation lCLabel = lC.getLabel();
			Annotation rCLabel = rC.getLabel();
			BinaryRule rule = new BinaryRule(pLabel.getSymbol(), lCLabel.getSymbol(), rCLabel.getSymbol());
			rule = g.getbRuleBySameHead().get(pLabel.getSymbol()).get(rule);
			double[][][] count;
			if (!bRuleCounter.containsKey(rule))
			{
				count = new double[g.getNumSubSymbol(pLabel.getSymbol())][g.getNumSubSymbol(lCLabel.getSymbol())][g
						.getNumSubSymbol(rCLabel.getSymbol())];
				bRuleCounter.put(rule, count);
			}
			else
			{
				count = bRuleCounter.get(rule);
			}
			double rootIS = rootLabel.getInnerScores()[0];
			scalingFactor = ScalingTools.calcScaleFactor(pLabel.getOuterScale() + lCLabel.getInnerScale()
					+ rCLabel.getInnerScale() - rootLabel.getInnerScale());
			Double[] pOutS = pLabel.getOuterScores();
			Double[] lCinnerS = lCLabel.getInnerScores();
			Double[] rCinnerS = rCLabel.getInnerScores();
			for (short i = 0; i < g.getNumSubSymbol(pLabel.getSymbol()); i++)
			{
				double pOS = pOutS[i];
				if (pOS == 0)
					continue;
				for (short j = 0; j < g.getNumSubSymbol(lCLabel.getSymbol()); j++)
				{
					double lCIS = lCinnerS[j];
					if (lCIS == 0)
						continue;
					for (short k = 0; k < g.getNumSubSymbol(rC.getLabel().getSymbol()); k++)
					{
						double rCIS = rCinnerS[k];
						if (rCIS == 0)
							continue;
						double rS = rule.getScore(i, j, k);
						if (rS == 0)
							continue;
						count[i][j][k] = count[i][j][k] + (rS * lCIS / rootIS * rCIS * scalingFactor * pOS);
					}
				}
			}
		}
		else if (tree.getChildren().size() == 1 && tree.getChildren().get(0).getLabel().getWord() == null)
		{
			AnnotationTreeNode child = tree.getChildren().get(0);
			Annotation cLabel = child.getLabel();
			UnaryRule rule = new UnaryRule(tree.getLabel().getSymbol(), cLabel.getSymbol());
			rule = g.getuRuleBySameHead().get(pLabel.getSymbol()).get(rule);
			double[][] count;
			if (!uRuleCounter.containsKey(rule))
			{
				count = new double[g.getNumSubSymbol(pLabel.getSymbol())][g.getNumSubSymbol(cLabel.getSymbol())];
				uRuleCounter.put(rule, count);
			}
			else
			{
				count = uRuleCounter.get(rule);
			}
			double rootIS = rootLabel.getInnerScores()[0];
			scalingFactor = ScalingTools
					.calcScaleFactor(pLabel.getOuterScale() + cLabel.getInnerScale() - rootLabel.getInnerScale());
			Double[] pOutS = pLabel.getOuterScores();
			Double[] cInnerS = cLabel.getInnerScores();
			for (short i = 0; i < g.getNumSubSymbol(pLabel.getSymbol()); i++)
			{
				double pOS = pOutS[i];
				if (pOS == 0.0)
					continue;
				for (short j = 0; j < g.getNumSubSymbol(cLabel.getSymbol()); j++)
				{
					double cIS = cInnerS[j];
					if (cIS == 0.0)
						continue;
					double rS = rule.getScore(i, j);
					if (rS == 0.0)
						continue;
					count[i][j] = count[i][j] + (rS * cIS / rootIS * scalingFactor * pOS);
				}
			}
		}
		else if (tree.isPreterminal())
		{
			PreterminalRule rule = new PreterminalRule(tree.getLabel().getSymbol(),
					tree.getChildren().get(0).getLabel().getWord());
			rule = g.getPreRuleBySameHead().get(rule.getParent()).get(rule);
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
			double rootIS = root.getLabel().getInnerScores()[0];
			scalingFactor = ScalingTools
					.calcScaleFactor(tree.getLabel().getOuterScale() - root.getLabel().getInnerScale());
			double tempCount = 0.0;
			for (short i = 0; i < g.getNumSubSymbol(tree.getLabel().getSymbol()); i++)
			{
				double pOS = tree.getLabel().getOuterScores()[i];
				if (pOS == 0)
					continue;
				double rs = rule.getScore(i);
				if (rs == 0)
					continue;
				tempCount = rs / rootIS * scalingFactor * tree.getLabel().getOuterScores()[i];
				count[i] = count[i] + tempCount;
				if (g.isRareWord(rule.getWord()))
				{
					if (!sameTagToUNKCounter.containsKey(rule.getParent()))
					{
						sameTagToUNKCounter.put(rule.getParent(),
								new double[g.getNumSubSymbol(tree.getLabel().getSymbol())]);
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
