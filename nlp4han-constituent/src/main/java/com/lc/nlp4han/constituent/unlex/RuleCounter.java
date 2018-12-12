package com.lc.nlp4han.constituent.unlex;

import java.util.HashMap;

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
	protected HashMap<Short, double[]> sameParentRulesCounter;// <parent,[ParentSubIndex,sum]>
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
		// int count = 0;
		// double start = System.currentTimeMillis();
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

	// public void calSameParentRulesExpectation(ArrayList<Short> nSubSymbolArr)
	// {
	// for (Map.Entry<BinaryRule, double[][][]> entry : bRuleCounter.entrySet())
	// {
	// BinaryRule bRule = entry.getKey();
	// double[][][] ruleCount = entry.getValue();
	// double[] samePCount = new double[nSubSymbolArr.get(bRule.getParent())];
	// for (int pSubSymbol = 0; pSubSymbol < samePCount.length; pSubSymbol++)
	// {
	// double tempCount = 0.0;
	// for (double[] countArr : ruleCount[pSubSymbol])
	// {
	// for (double subRuleCount : countArr)
	// {
	// tempCount += subRuleCount;
	// }
	// }
	// samePCount[pSubSymbol] += tempCount;
	// }
	// calSameParentRulesExpectationHelper(bRule.getParent(), samePCount);
	// }
	// for (Map.Entry<UnaryRule, double[][]> entry : uRuleCounter.entrySet())
	// {
	// UnaryRule uRule = entry.getKey();
	// double[][] ruleCount = entry.getValue();
	// double[] samePCount = new double[nSubSymbolArr.get(uRule.getParent())];
	// for (int pSubSymbol = 0; pSubSymbol < samePCount.length; pSubSymbol++)
	// {
	// double tempCount = 0.0;
	// for (double subRuleCount : ruleCount[pSubSymbol])
	// {
	// tempCount += subRuleCount;
	// }
	// samePCount[pSubSymbol] += tempCount;
	// }
	// calSameParentRulesExpectationHelper(uRule.getParent(), samePCount);
	// }
	// for (Map.Entry<PreterminalRule, double[]> entry : preRuleCounter.entrySet())
	// {
	// PreterminalRule preRule = entry.getKey();
	// double[] ruleCount = entry.getValue();
	// double[] samePCount = new double[nSubSymbolArr.get(preRule.getParent())];
	// for (int pSubSymbol = 0; pSubSymbol < ruleCount.length; pSubSymbol++)
	// {
	// samePCount[pSubSymbol] = ruleCount[pSubSymbol];
	// }
	// calSameParentRulesExpectationHelper(preRule.getParent(), samePCount);
	// }
	// }
	//
	// private void calSameParentRulesExpectationHelper(short parent, double[]
	// samePCount)
	// {
	// sameParentRulesCounter.merge(parent, samePCount, (oldCount, newCount) ->
	// {
	// if (newCount.length != oldCount.length)
	// throw new Error("传递参数有错误。");
	// for (int i = 0; i < newCount.length; i++)
	// {
	// oldCount[i] += newCount[i];
	// }
	// return oldCount;
	// });
	// }

	public void calSameParentRulesExpectation(Grammar g)
	{
		for (short pSymbol = 0; pSymbol < g.getNumSymbol(); pSymbol++)
		{
			double[] count = new double[g.getNumSubSymbol((short) pSymbol)];
			// for (int i = 0; i < count.length; i++)
			// {
			// count[i] = 0.0;
			// }
			if (g.getbRuleSetBySameHead(pSymbol) != null)
			{
				for (BinaryRule bRule : g.getbRuleSetBySameHead(pSymbol))
				{
					double[][][] ruleCount = bRuleCounter.get(bRule);
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
			if (g.getuRuleSetBySameHead(pSymbol) != null)
			{
				for (UnaryRule uRule : g.getuRuleSetBySameHead(pSymbol))
				{
					double[][] ruleCount = uRuleCounter.get(uRule);
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
			if (g.getPreRuleSetBySameHead(pSymbol) != null)
			{
				for (PreterminalRule preRule : g.getPreRuleSetBySameHead(pSymbol))
				{
					double[] ruleCount = preRuleCounter.get(preRule);
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
		short pSymbol = pLabel.getSymbol();
		short nSubP = g.getNumSubSymbol(pSymbol);
		Double[] pOutS = pLabel.getOuterScores();
		if (tree.getChildren().size() == 2)
		{

			AnnotationTreeNode lC = tree.getChildren().get(0);
			AnnotationTreeNode rC = tree.getChildren().get(1);
			Annotation lCLabel = lC.getLabel();
			Annotation rCLabel = rC.getLabel();
			short lcSymbol = lCLabel.getSymbol();
			short rcSymbol = rCLabel.getSymbol();
			short nSubLC = g.getNumSubSymbol(lcSymbol);
			short nSubRC = g.getNumSubSymbol(rcSymbol);
			BinaryRule rule = new BinaryRule(pSymbol, lcSymbol, rcSymbol);
			rule = g.getRule(rule);
			double[][][] count;
			if (!bRuleCounter.containsKey(rule))
			{
				count = new double[nSubP][nSubLC][nSubRC];
				bRuleCounter.put(rule, count);
			}
			else
			{
				count = bRuleCounter.get(rule);
			}
			double rootIS = rootLabel.getInnerScores()[0];
			scalingFactor = ScalingTools.calcScaleFactor(pLabel.getOuterScale() + lCLabel.getInnerScale()
					+ rCLabel.getInnerScale() - rootLabel.getInnerScale());
			Double[] lCinnerS = lCLabel.getInnerScores();
			Double[] rCinnerS = rCLabel.getInnerScores();
			for (short i = 0; i < nSubP; i++)
			{
				double pOS = pOutS[i];
				if (pOS == 0)
					continue;
				for (short j = 0; j < nSubLC; j++)
				{
					double lCIS = lCinnerS[j];
					if (lCIS == 0)
						continue;
					for (short k = 0; k < nSubRC; k++)
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
			short cSymbol = cLabel.getSymbol();
			short nSubC = g.getNumSubSymbol(cSymbol);
			UnaryRule rule = new UnaryRule(pSymbol, cSymbol);
			rule = g.getRule(rule);
			double[][] count;
			if (!uRuleCounter.containsKey(rule))
			{
				count = new double[nSubP][nSubC];
				uRuleCounter.put(rule, count);
			}
			else
			{
				count = uRuleCounter.get(rule);
			}
			double rootIS = rootLabel.getInnerScores()[0];
			scalingFactor = ScalingTools
					.calcScaleFactor(pLabel.getOuterScale() + cLabel.getInnerScale() - rootLabel.getInnerScale());
			Double[] cInnerS = cLabel.getInnerScores();
			for (short i = 0; i < nSubP; i++)
			{
				double pOS = pOutS[i];
				if (pOS == 0.0)
					continue;
				for (short j = 0; j < nSubC; j++)
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
			PreterminalRule rule = new PreterminalRule(pSymbol, tree.getChildren().get(0).getLabel().getWord());
			rule = g.getRule(rule);
			double[] count;
			if (!preRuleCounter.containsKey(rule))
			{
				count = new double[nSubP];
				preRuleCounter.put(rule, count);
			}
			else
			{
				count = preRuleCounter.get(rule);
			}
			double rootIS = root.getLabel().getInnerScores()[0];
			scalingFactor = ScalingTools.calcScaleFactor(pLabel.getOuterScale() - root.getLabel().getInnerScale());
			double tempCount = 0.0;
			for (short i = 0; i < nSubP; i++)
			{
				double pOS = pOutS[i];
				if (pOS == 0)
					continue;
				double rs = rule.getScore(i);
				if (rs == 0)
					continue;
				tempCount = rs / rootIS * scalingFactor * pOS;
				count[i] = count[i] + tempCount;
				if (g.isRareWord(rule.getWord()))
				{
					if (!sameTagToUNKCounter.containsKey(pSymbol))
					{
						sameTagToUNKCounter.put(pSymbol, new double[nSubP]);
					}
					sameTagToUNKCounter.get(pSymbol)[i] = sameTagToUNKCounter.get(pSymbol)[i] + tempCount;
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
