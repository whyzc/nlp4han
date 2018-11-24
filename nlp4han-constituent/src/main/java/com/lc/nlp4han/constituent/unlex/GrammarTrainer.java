package com.lc.nlp4han.constituent.unlex;

import java.util.Map;

/**
 * 语法训练器
 * 
 * @author 王宁
 */
public class GrammarTrainer
{
	public static int EMIterations = 50;
	public static RuleCounter ruleCounter;

	public static Grammar train(Grammar g, TreeBank treeBank, int SMCycle, double mergeRate, int EMIterations)
	{
		System.out.println("SMCycle:" + SMCycle);
		for (int i = 0; i < SMCycle; i++)
		{
			GrammarSpliter.splitGrammar(g, treeBank);
			EM(g, treeBank, EMIterations);
			System.err.println("分裂完成。");
			GrammarMerger.mergeGrammar(g, treeBank, mergeRate, ruleCounter);
			EM(g, treeBank, EMIterations);
			System.err.println("合并完成。");
		}
		return g;
	}

	/**
	 * 将处理后的语法期望最大化，得到新的规则
	 */
	public static void EM(Grammar g, TreeBank treeBank, int iterations)
	{
		double lss = 0;
		for (int i = 0; i < iterations; i++)
		{

			ruleCounter = new RuleCounter();
			lss = calRuleExpectationAndTreeBankLSS(g, treeBank);
			System.out.println("在本次EM前树库的Log似然值：" + lss);
			recalculateRuleScore(g);

		}
		System.out.println("EM算法结束。");
	}

	public static double calRuleExpectationAndTreeBankLSS(Grammar g, TreeBank treeBank)
	{
		return ruleCounter.calRuleExpectationAndTreeBankLSS(g, treeBank);
	}

	/**
	 * 跟新规则的scores
	 * 
	 * @param g
	 *            语法
	 */
	public static void recalculateRuleScore(Grammar g)
	{
		double newScore;
		double denominator;
		for (BinaryRule bRule : g.getbRules())
		{

			int pNumSub = g.getNumSubSymbol(bRule.getParent());
			int lCNumSub = g.getNumSubSymbol(bRule.getLeftChild());
			int rCNumSub = g.getNumSubSymbol(bRule.getRightChild());

			for (int i = 0; i < pNumSub; i++)
			{

				if (ruleCounter.sameParentRulesCounter.containsKey(bRule.parent)
						&& ruleCounter.sameParentRulesCounter.get(bRule.parent)[i] != null)
				{
					denominator = ruleCounter.sameParentRulesCounter.get(bRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(g, bRule.parent, i);
				}
				for (int j = 0; j < lCNumSub; j++)
				{
					for (int k = 0; k < rCNumSub; k++)
					{
						newScore = ruleCounter.bRuleCounter.get(bRule)[i][j][k] / denominator;
						if (newScore < Rule.rulethres)
						{
							newScore = 0.0;
						}

						bRule.getScores().get(i).get(j).set(k, newScore);
					}
				}
			}

		}

		for (UnaryRule uRule : g.getuRules())
		{
			int pNumSub = g.getNumSubSymbol(uRule.getParent());
			int cNumSub = g.getNumSubSymbol(uRule.getChild());
			for (int i = 0; i < pNumSub; i++)
			{
				if (ruleCounter.sameParentRulesCounter.containsKey(uRule.parent)
						&& ruleCounter.sameParentRulesCounter.get(uRule.parent)[i] != null)
				{
					denominator = ruleCounter.sameParentRulesCounter.get(uRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(g, uRule.parent, i);
				}
				for (int j = 0; j < cNumSub; j++)
				{
					newScore = ruleCounter.uRuleCounter.get(uRule)[i][j] / denominator;
					if (newScore < Rule.rulethres)
					{
						newScore = 0.0;
					}
					uRule.getScores().get(i).set(j, newScore);
				}
			}
		}

		for (PreterminalRule preRule : g.getLexicon().getPreRules())
		{
			int pNumSub = g.getNumSubSymbol(preRule.parent);
			for (int i = 0; i < pNumSub; i++)
			{
				if (ruleCounter.sameParentRulesCounter.containsKey(preRule.parent)
						&& ruleCounter.sameParentRulesCounter.get(preRule.parent)[i] != null)
				{
					denominator = ruleCounter.sameParentRulesCounter.get(preRule.parent)[i];
				}
				else
				{
					denominator = calculateSameParentRuleCount(g, preRule.parent, i);
				}
				newScore = ruleCounter.preRuleCounter.get(preRule)[i] / denominator;
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
		if (!ruleCounter.sameParentRulesCounter.containsKey((short) parent)
				|| ruleCounter.sameParentRulesCounter.get((short) parent)[pSubSymbolIndex] == null)
		{
			double ruleCount = 0.0;
			if (g.getbRuleBySameHead().containsKey((short) parent))
				for (Map.Entry<BinaryRule, BinaryRule> entry : g.getbRuleBySameHead().get((short) parent).entrySet())
				{
					double[][][] count = ruleCounter.bRuleCounter.get(entry.getValue());
					for (int i = 0; i < count[pSubSymbolIndex].length; i++)
					{
						for (int j = 0; j < count[pSubSymbolIndex][i].length; j++)
						{
							ruleCount = ruleCount + count[pSubSymbolIndex][i][j];
						}
					}
				}
			if (g.getuRuleBySameHead().containsKey((short) parent))
				for (Map.Entry<UnaryRule, UnaryRule> entry : g.getuRuleBySameHead().get((short) parent).entrySet())
				{
					double[][] count = ruleCounter.uRuleCounter.get(entry.getValue());
					for (int i = 0; i < count[pSubSymbolIndex].length; i++)
					{
						ruleCount = ruleCount + count[pSubSymbolIndex][i];
					}
				}
			if (g.getPreRuleBySameHead().containsKey((short) parent))
				for (Map.Entry<PreterminalRule, PreterminalRule> entry : g.getPreRuleBySameHead().get((short) parent)
						.entrySet())
				{
					double[] count = ruleCounter.preRuleCounter.get(entry.getValue());
					ruleCount = ruleCount + count[pSubSymbolIndex];
				}
			if (ruleCounter.sameParentRulesCounter.containsKey((short) parent))
			{
				ruleCounter.sameParentRulesCounter.get((short) parent)[pSubSymbolIndex] = ruleCount;
			}
			else
			{
				Double[] countArr = new Double[g.getNumSubSymbol((short) parent)];
				countArr[pSubSymbolIndex] = ruleCount;
				ruleCounter.sameParentRulesCounter.put((short) parent, countArr);
			}
			return ruleCount;
		}
		else
		{
			return ruleCounter.sameParentRulesCounter.get((short) parent)[pSubSymbolIndex];
		}
	}
}
