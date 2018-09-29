package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PCFG extends CFG
{
	public PCFG()
	{

	}

	public PCFG(InputStream in, String encoding) throws IOException
	{
		super.readGrammar(in, encoding, "PCFG");
	}

	/**
	 * 获取PCFG中所有非终结符扩展出的规则概率之和与1.0的误差，取其中最大的值返回
	 */
	public double getProMaxErrorOfNonTer()
	{
		double MaxErrorOfPCNF = 0;
		for (String string : super.getNonTerminalSet())
		{
			double pro = 0;
			for (RewriteRule rule : getRuleBylhs(string))
			{
				PRule prule = (PRule) rule;
				pro += prule.getProb();
			}
			if (Math.abs(1.0 - pro) > MaxErrorOfPCNF)
			{
				MaxErrorOfPCNF = Math.abs(1.0 - pro);
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
				if (prule.getProb() > bestPRule.getProb())
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
			return (ArrayList<PRule>) pruleList.subList(0, k);
		}
		return pruleList;
	}
}
