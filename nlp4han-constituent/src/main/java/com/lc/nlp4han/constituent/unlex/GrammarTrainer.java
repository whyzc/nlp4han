package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

/**
 * 语法训练器
 * 
 * @author 王宁
 */
public class GrammarTrainer
{
	public static int EMIterations = 50;
	public static RuleCounter ruleCounter;

	public static Grammar train(Grammar g, TreeBank treeBank, int SMCycle, double mergeRate, int EMIterations,
			double smooth)
	{
		treeBank.calIOScore(g);
		double totalLSS = treeBank.calLogTreeBankSentenceSocre();
		System.out.println("训练前树库似然值：" + totalLSS);
		System.out.println("SMCycle:" + SMCycle);
		for (int i = 0; i < SMCycle; i++)
		{
			System.err.println("开始分裂。");
			GrammarSpliter.splitGrammar(g, treeBank);
			EM(g, treeBank, EMIterations);
			System.err.println("分裂完成。");
			System.err.println("开始合并。");
			GrammarMerger.mergeGrammar(g, treeBank, mergeRate, ruleCounter);
			EM(g, treeBank, EMIterations / 2);
			System.err.println("合并完成。");
			SmoothByRuleOfSameChild smoother = new SmoothByRuleOfSameChild(smooth);
			System.err.println("开始平滑规则。");
			smoother.smooth(g);
			normalizeBAndURule(g);
			normalizedPreTermianlRules(g);
			EM(g, treeBank, EMIterations / 2);
			System.err.println("平滑规则完成。");
		}
		if (SMCycle != 0)
		{
			double[][] subTag2UNKScores = calTag2UNKScores(g);
			g.setSubTag2UNKScores(subTag2UNKScores);
		}
		return g;
	}

	/**
	 * 将处理后的语法期望最大化，得到新的规则
	 */
	public static void EM(Grammar g, TreeBank treeBank, int iterations)
	{
		if (iterations > 0)
		{
			double totalLSS = 0;
			treeBank.calIOScore(g);
			totalLSS = treeBank.calLogTreeBankSentenceSocre();
			System.out.println("EM算法开始前树库的log似然值：" + totalLSS);
			for (int i = 0; i < iterations; i++)
			{
				calRuleExpectation(g, treeBank);
				// System.out.println("EStep完成。");
				recalculateRuleScore(g);
				// System.out.println("MStep完成。");
				treeBank.calIOScore(g);
				totalLSS = treeBank.calLogTreeBankSentenceSocre();
				System.out.println("在本次EM迭代后树库的Log似然值：" + totalLSS);
			}
			calRuleExpectation(g, treeBank);
			System.out.println("EM算法结束。");
			System.out.println("EM算法结束后树库的log似然值：" + totalLSS);
		}

	}

	public static void calRuleExpectation(Grammar g, TreeBank treeBank)
	{
		ruleCounter = new RuleCounter();
		ruleCounter.calRuleExpectation(g, treeBank);
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

			for (short i = 0; i < pNumSub; i++)
			{
				if (ruleCounter.sameParentRulesCounter.get(bRule.parent)[i] != 0.0)
				{
					denominator = ruleCounter.sameParentRulesCounter.get(bRule.parent)[i];
				}
				else
				{
					throw new Error("sameParentRulesCounter计算错误。");
				}
				for (short j = 0; j < lCNumSub; j++)
				{
					for (short k = 0; k < rCNumSub; k++)
					{
						newScore = ruleCounter.bRuleCounter.get(bRule)[i][j][k] / denominator;
						if (newScore < Rule.ruleThres)
						{
							newScore = 0.0;
						}

						bRule.setScore(i, j, k, newScore);
					}
				}
			}

		}

		for (UnaryRule uRule : g.getuRules())
		{
			int pNumSub = g.getNumSubSymbol(uRule.getParent());
			int cNumSub = g.getNumSubSymbol(uRule.getChild());
			for (short i = 0; i < pNumSub; i++)
			{
				if (ruleCounter.sameParentRulesCounter.get(uRule.parent)[i] != 0.0)
				{
					denominator = ruleCounter.sameParentRulesCounter.get(uRule.parent)[i];
				}
				else
				{
					throw new Error("sameParentRulesCounter计算错误。");
				}
				for (short j = 0; j < cNumSub; j++)
				{
					newScore = ruleCounter.uRuleCounter.get(uRule)[i][j] / denominator;
					if (newScore < Rule.ruleThres)
					{
						newScore = 0.0;
					}
					uRule.setScore(i, j, newScore);
				}
			}
		}

		for (PreterminalRule preRule : g.getLexicon().getPreRules())
		{
			int pNumSub = g.getNumSubSymbol(preRule.parent);
			for (short i = 0; i < pNumSub; i++)
			{
				if (ruleCounter.sameParentRulesCounter.get(preRule.parent)[i] != 0.0)
				{
					denominator = ruleCounter.sameParentRulesCounter.get(preRule.parent)[i];
				}
				else
				{
					throw new Error("sameParentRulesCounter计算错误。");
				}
				newScore = ruleCounter.preRuleCounter.get(preRule)[i] / denominator;
				if (newScore < Rule.preRulethres)
				{
					newScore = 0.0;
				}
				preRule.setScore(i, newScore);
			}
		}
	}

	public static double[][] calTag2UNKScores(Grammar g)
	{
		double[][] subTag2UNKScores = new double[g.getNumSymbol()][];
		for (int tag = 0; tag < g.getNumSymbol(); tag++)
		{
			if (!g.hasPreterminalSymbol((short) tag))
				continue;
			subTag2UNKScores[tag] = new double[g.getNumSubSymbol((short) tag)];
			for (int subT = 0; subT < subTag2UNKScores[tag].length; subT++)
			{
				if (ruleCounter.sameTagToUNKCounter.containsKey((short) tag))
				{
					double subTagCount = ruleCounter.sameParentRulesCounter.get((short) tag)[subT];
					double subTagUNKCount = ruleCounter.sameTagToUNKCounter.get((short) tag)[subT];
					subTag2UNKScores[tag][subT] = subTagUNKCount / subTagCount;
					System.out.println(g.symbolStrValue((short) tag) + "_" + subT + " " + subTagUNKCount / subTagCount);
				}
				else
				{
					subTag2UNKScores[tag][subT] = 1;
					System.out.println(g.symbolStrValue((short) tag) + " 没有出现过UNK.");
				}
			}
		}
		return subTag2UNKScores;
	}

	// public static void normalizeBAndURule(Grammar g)
	// {
	// HashMap<Short, Double[]> sameHeadRuleScoreSum = new HashMap<Short,
	// Double[]>();
	// for (Map.Entry<Short, HashMap<BinaryRule, BinaryRule>> entry :
	// g.getbRuleBySameHead().entrySet())
	// {
	// Double[] ruleScoreSum = new Double[g.getNumSubSymbol(entry.getKey())];
	// sameHeadRuleScoreSum.put(entry.getKey(), ruleScoreSum);
	// for (Map.Entry<BinaryRule, BinaryRule> innerEntry :
	// entry.getValue().entrySet())
	// {
	// for (short i = 0; i < ruleScoreSum.length; i++)
	// {
	// if (ruleScoreSum[i] == null)
	// {
	// ruleScoreSum[i] = 0.0;
	// }
	// ruleScoreSum[i] += innerEntry.getKey().getParent_i_ScoceSum(i);
	// }
	// }
	// }
	//
	// for (Map.Entry<Short, HashMap<UnaryRule, UnaryRule>> entry :
	// g.getuRuleBySameHead().entrySet())
	// {
	// Double[] ruleScoreSum;
	// if (!sameHeadRuleScoreSum.containsKey(entry.getKey()))
	// {
	// sameHeadRuleScoreSum.put(entry.getKey(), new
	// Double[g.getNumSubSymbol(entry.getKey())]);
	// }
	// ruleScoreSum = sameHeadRuleScoreSum.get(entry.getKey());
	// for (Map.Entry<UnaryRule, UnaryRule> innerEntry :
	// entry.getValue().entrySet())
	// {
	// for (short i = 0; i < ruleScoreSum.length; i++)
	// {
	// if (ruleScoreSum[i] == null)
	// {
	// ruleScoreSum[i] = 0.0;
	// }
	// ruleScoreSum[i] += innerEntry.getKey().getParent_i_ScoceSum(i);
	// }
	// }
	// }
	//
	// for (BinaryRule bRule : g.getbRules())
	// {
	// short nSubParent = g.getNumSubSymbol(bRule.getParent());
	// for (short subP = 0; subP < nSubParent; subP++)
	// {
	// double tag_iScoreSum = sameHeadRuleScoreSum.get(bRule.getParent())[subP];
	// short nSubLC = g.getNumSubSymbol(bRule.getLeftChild());
	// for (short subLC = 0; subLC < nSubLC; subLC++)
	// {
	// short nSubRC = g.getNumSubSymbol(bRule.getRightChild());
	// for (short subRC = 0; subRC < nSubRC; subRC++)
	// {
	// double score = bRule.getScore(subP, subLC, subRC);
	// bRule.setScore(subP, subLC, subRC, score / tag_iScoreSum);
	// }
	// }
	// }
	// }
	// for (UnaryRule uRule : g.getuRules())
	// {
	// short nSubParent = g.getNumSubSymbol(uRule.getParent());
	// for (short subP = 0; subP < nSubParent; subP++)
	// {
	// double tag_iScoreSum = sameHeadRuleScoreSum.get(uRule.getParent())[subP];
	// short nSubC = g.getNumSubSymbol(uRule.getChild());
	// for (short subC = 0; subC < nSubC; subC++)
	// {
	// double score = uRule.getScore(subP, subC);
	// uRule.setScore(subP, subC, score / tag_iScoreSum);
	// }
	// }
	// }
	// }

	public static void normalizeBAndURule(Grammar g)
	{
		HashMap<Short, Double[]> sameHeadRuleScoreSum = new HashMap<Short, Double[]>();
		for (short symbol = 0; symbol < g.getNumSymbol(); symbol++)
		{
			if (!g.hasPreterminalSymbol(symbol))
			{
				Double[] ruleScoreSum = new Double[g.getNumSubSymbol(symbol)];
				sameHeadRuleScoreSum.put(symbol, ruleScoreSum);
				Set<BinaryRule> sameHeadBSet = g.getbRuleSetBySameHead(symbol);
				if (sameHeadBSet != null)
					for (BinaryRule bRule : sameHeadBSet)
					{
						for (short i = 0; i < ruleScoreSum.length; i++)
						{
							if (ruleScoreSum[i] == null)
							{
								ruleScoreSum[i] = 0.0;
							}
							ruleScoreSum[i] += bRule.getParent_i_ScoceSum(i);
						}
					}
				Set<UnaryRule> sameHeadUSet = g.getuRuleSetBySameHead(symbol);
				if (sameHeadUSet != null)
					for (UnaryRule uRule : sameHeadUSet)
					{
						for (short i = 0; i < ruleScoreSum.length; i++)
						{
							if (ruleScoreSum[i] == null)
							{
								ruleScoreSum[i] = 0.0;
							}
							ruleScoreSum[i] += uRule.getParent_i_ScoceSum(i);
						}
					}
			}
		}

		for (BinaryRule bRule : g.getbRules())
		{
			short nSubParent = g.getNumSubSymbol(bRule.getParent());
			for (short subP = 0; subP < nSubParent; subP++)
			{
				double tag_iScoreSum = sameHeadRuleScoreSum.get(bRule.getParent())[subP];
				short nSubLC = g.getNumSubSymbol(bRule.getLeftChild());
				for (short subLC = 0; subLC < nSubLC; subLC++)
				{
					short nSubRC = g.getNumSubSymbol(bRule.getRightChild());
					for (short subRC = 0; subRC < nSubRC; subRC++)
					{
						double score = bRule.getScore(subP, subLC, subRC);
						bRule.setScore(subP, subLC, subRC, score / tag_iScoreSum);
					}
				}
			}
		}
		for (UnaryRule uRule : g.getuRules())
		{
			short nSubParent = g.getNumSubSymbol(uRule.getParent());
			for (short subP = 0; subP < nSubParent; subP++)
			{
				double tag_iScoreSum = sameHeadRuleScoreSum.get(uRule.getParent())[subP];
				short nSubC = g.getNumSubSymbol(uRule.getChild());
				for (short subC = 0; subC < nSubC; subC++)
				{
					double score = uRule.getScore(subP, subC);
					uRule.setScore(subP, subC, score / tag_iScoreSum);
				}
			}
		}
	}

	public static void normalizedPreTermianlRules(Grammar g)
	{
		HashMap<Short, Double[]> sameHeadPRuleScoreSum = new HashMap<Short, Double[]>();
		for (PreterminalRule preRule : g.getLexicon().getPreRules())
		{
			short parent = preRule.parent;
			short nSubP = g.getNumSubSymbol(parent);
			if (!sameHeadPRuleScoreSum.containsKey(parent))
			{
				sameHeadPRuleScoreSum.put(parent, new Double[nSubP]);
			}

			for (short i = 0; i < nSubP; i++)
			{
				Double sameHeadScoreSum = sameHeadPRuleScoreSum.get(parent)[i];
				if (sameHeadScoreSum == null)
				{
					BigDecimal tag_iScoreSum = BigDecimal.valueOf(0.0);
					for (PreterminalRule theRule : g.getPreRuleSetBySameHead(parent))
					{
						tag_iScoreSum = tag_iScoreSum.add(BigDecimal.valueOf(theRule.getScore(i)));
					}
					sameHeadPRuleScoreSum.get(preRule.parent)[i] = tag_iScoreSum.doubleValue();
					sameHeadScoreSum = tag_iScoreSum.doubleValue();
				}
				preRule.setScore(i,
						BigDecimal.valueOf(preRule.getScore(i))
								.divide(BigDecimal.valueOf(sameHeadScoreSum), 15,
										BigDecimal.ROUND_HALF_UP)
								.doubleValue());
			}
		}
	}
}
