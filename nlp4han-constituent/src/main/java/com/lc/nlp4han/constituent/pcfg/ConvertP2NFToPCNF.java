package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConvertP2NFToPCNF
{
	private PCFG p2nf;
	private HashSet<PRule> deletePRuleSet;
	private HashSet<String> posSet = new HashSet<String>();// 词性标注集

	public ConvertP2NFToPCNF(PCFG p2nf)
	{
		this.p2nf = p2nf;
		this.deletePRuleSet = new HashSet<PRule>();
		getPOSSet();
	}

	public PCFG removeUnitProduction()
	{
		Set<String> nonterSet = p2nf.getNonTerminalSet();
		for (String nonTer : p2nf.getNonTerminalSet())
		{
			for (RewriteRule rule : p2nf.getRuleBylhs(nonTer))
			{
				PRule prule = (PRule) rule;
				if (prule.getRhs().size() == 1)
				{
					String rhs = prule.getRhs().get(0);
					if (posSet.contains(rhs))
					{
						continue;
					}
					if (nonterSet.contains(rhs))
					{
						deletePRuleSet.add(prule);
						removeUPAndAddNewRule(prule);
					}
				}
			}
		}
		DeletePRuleSet();
		return p2nf;
	}

	public void removeUPAndAddNewRule(PRule prule)
	{
		String lhs = prule.getLhs();
		String rhs = prule.getRhs().get(0);

		String[] lhs1 = lhs.split("@");
		if (lhs1.length >= 3)
		{
			return;// 如果单元规则迭代有3次以上，则返回
		}
		if (posSet.contains(prule.getRhs().get(0)))
		{
			p2nf.add(prule);// 若该规则右侧为词性标注则直接添加
			return;
		}
		for (String lhs2 : lhs1)
		{
			if (lhs2.equals(rhs))
			{
				return;// 如果出现循环非终结符则返回
			}
		}
		for (RewriteRule rule : p2nf.getRuleBylhs(prule.getRhs().get(0)))
		{
			PRule prule1 = (PRule) rule;
			PRule prule2 = new PRule(prule.getProOfRule() * prule1.getProOfRule(),
					prule.getLhs() + "@" + prule1.getLhs(), prule1.getRhs());
			if (prule1.getRhs().size() == 2 || !p2nf.getNonTerminalSet().contains(prule1.getRhs().get(0)))
			{
				p2nf.add(prule2);
			}
			else
			{
				removeUPAndAddNewRule(prule2);
			}
		}
	}

	public void DeletePRuleSet()
	{
		for (PRule rule : deletePRuleSet)
		{
			p2nf.getRuleSet().remove(rule);
			p2nf.getRuleBylhs(rule.getLhs()).remove(rule);
			p2nf.getRuleByrhs(rule.getRhs()).remove(rule);
			ArrayList<String> strList = new ArrayList<String>();
			strList.add(rule.getLhs());
			strList.addAll(rule.getRhs());
			p2nf.getPruleMap().remove(strList);
		}
	}

	private void getPOSSet()
	{
		Set<String> nonTer = p2nf.getNonTerminalSet();
		for (RewriteRule rule : p2nf.getRuleSet())
		{
			if (rule.getRhs().size() == 1 && !nonTer.contains(rule.getRhs().get(0)))
			{
				posSet.add(rule.getLhs());
			}
		}
	}
}
