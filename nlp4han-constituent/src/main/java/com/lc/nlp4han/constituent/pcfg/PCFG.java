package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PCFG extends CFG
{
	private HashMap<RewriteRule, PRule> pruleMap = new HashMap<RewriteRule, PRule>();// 规则集map

	public PCFG()
	{

	}

	public PCFG(InputStream in, String encoding) throws IOException
	{
		super.ExtractGrammarFromStream(in, encoding, "PCFG", pruleMap);
	}

	@Override
	public void add(RewriteRule rule)
	{
		super.add(rule);
		pruleMap.put(rule, (PRule) rule);
	}

	/**
	 * 由RewriteRule集转换为PRule规则集
	 * 
	 * @param ruleset
	 * @return pruleSet
	 */
	public static Set<PRule> convertRewriteRuleSetToPRuleSet(Set<RewriteRule> ruleset)
	{
		Iterator<RewriteRule> itr = ruleset.iterator();
		Set<PRule> pruleSet = new HashSet<PRule>();
		while (itr.hasNext())
		{
			pruleSet.add((PRule) itr.next());
		}
		return pruleSet;
	}

	/**
	 * 根据规则中的终结符和非终结符获取概率
	 * 
	 * @param rule
	 * @return
	 */
	public PRule getPRuleByLHSAndRHS(RewriteRule rule)
	{
		return pruleMap.get(rule);
	}

	/**
	 * 获取PCFG中所有非终结符扩展出的规则概率之和与1.0的误差，取其中最大的值返回
	 */
	public double getProMaxErrorOfNonTer()
	{
		double MaxErrorOfPCNF = 0;
		for (String string : super.getNonTerminalSet())
		{
			Set<PRule> pruleSet = convertRewriteRuleSetToPRuleSet(getRuleBylhs(string));
			double pro = 0;
			for (PRule rule : pruleSet)
			{
				pro += rule.getProOfRule();
			}
			if (Math.abs(1.0 - pro) > MaxErrorOfPCNF)
			{
				MaxErrorOfPCNF = 1.0 - pro;
			}
		}
		return MaxErrorOfPCNF;
	}

	/**
	 * 获取集合中概率最高的那个规则
	 * 
	 * @param ruleSet
	 * @return
	 */
	public PRule getHighestProRule(Set<RewriteRule> ruleSet)
	{
		Iterator<RewriteRule> itr = ruleSet.iterator();
		return getHighestProRuleByItr(itr, 1).get(0);
	}

	/**
	 * 从映射中得到概率最大的K个规则
	 * 
	 * @param ruleMap
	 * @param k
	 * @return
	 */
	public ArrayList<PRule> getHighestProRuleFromMap(HashMap<RewriteRule, Integer> ruleMap, int k)
	{
		Iterator<RewriteRule> itr = ruleMap.keySet().iterator();
		return getHighestProRuleByItr(itr, k);
	}

	/**
	 * 从规则迭代器中获取概率最高的k个规则
	 * 
	 * @param itr
	 * @param k
	 * @return
	 */
	public ArrayList<PRule> getHighestProRuleByItr(Iterator<RewriteRule> itr, int k)
	{
		PRule bestPRule = new PRule(-1.0, "FSA", "FDS");
		ArrayList<PRule> pruleList = new ArrayList<PRule>();
		while (itr.hasNext())
		{
			PRule prule = (PRule) itr.next();
			if (k > 1)
			{// 直接将规则添加进pruleList
				pruleList.add(prule);
			}
			else
			{// 根据规则集合直接搜索最大概率规则
				if (prule.getProOfRule() > bestPRule.getProOfRule())
				{
					bestPRule = prule;
				}
			}
		}
		if (k == 1)
		{
			pruleList.add(bestPRule);
		}
		else
		{
			Collections.sort(pruleList);
		}
		/*
		 * 若结果集中多余k个，则截取其中的前k个
		 */
		if (pruleList.size() > k)
		{
			ArrayList<PRule> subPruleList = new ArrayList<PRule>();
			for (int i = 0; i < k; i++)
			{
				subPruleList.add(pruleList.get(i));
			}
			return subPruleList;
		}
		return pruleList;
	}
}
