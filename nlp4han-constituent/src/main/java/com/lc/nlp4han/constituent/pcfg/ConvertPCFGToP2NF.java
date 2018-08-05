package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.List;

public class ConvertPCFGToP2NF
{
	private PCFG pcnf;
	public PCFG convertToCNF(PCFG pcfg)
	{
		pcnf = new PCFG();
		pcnf.setNonTerminalSet(pcfg.getNonTerminalSet());
		pcnf.setTerminalSet(pcfg.getTerminalSet());
		// 添加新的起始符
		addNewStartSymbol(pcfg);
		// 前期处理，遍历pcfg将规则加入pcnf
		priorDisposal(pcfg);
		return pcnf;
	}

	/**
	 * 添加新的起始符DuRoot,新规则，DuIP->IP,因为我是在最后处理Unit
	 * Production,集合的遍历和修改不能同时进行，故将这个规则同时放入pcfg中
	 */
	private void addNewStartSymbol(PCFG pcfg)
	{
		String oldStartSymbol = pcfg.getStartSymbol();
		String newStartSymbol = "Start#" + pcfg.getStartSymbol();
		pcnf.setStartSymbol(newStartSymbol);// 设置新的起始符
		pcnf.addNonTerminal(newStartSymbol);// 添加新的非终结符
		pcnf.add(new PRule(1.0, newStartSymbol, oldStartSymbol));
		pcfg.add(new PRule(1.0, newStartSymbol, oldStartSymbol));// 添加新的规则
	}

	/**
	 * 前期处理，遍历的将规则加入pcnf 将字符串个数多于两个的递归的减为两个 将终结符和非终结符混合转换为两个非终结符 直接添加右侧只有一个字符串的规则
	 */
	private void priorDisposal(PCFG pcfg)
	{
		for (RewriteRule rule : pcfg.getRuleSet())
		{
			if (rule.getRhs().size() >= 3)
			{
				// 如果右侧中有终结符，则转换为伪非终结符
				if (!pcnf.getNonTerminalSet().containsAll(rule.getRhs()))
				{
					ConvertToNonTerRHS(rule);
				}
				reduceRHSNum(rule);
			}
			/*
			 * 先检测右侧有两个字符串的规则是否为终结符和非终结符混合，若混合则先将终结符转换为非终结符，
			 */
			if (rule.getRhs().size() == 2)
			{
				// 如果右侧中有终结符，则转换为伪非终结符
				if (!pcnf.getNonTerminalSet().containsAll(rule.getRhs()))
				{
					ConvertToNonTerRHS(rule);
				}
				pcnf.add(rule);
			}
			/*
			 * 先添加进pcnf随后处理
			 */
			if (rule.getRhs().size() == 1)
			{
				pcnf.add(rule);
			}
		}
	}
	/**
	 * 将右侧全部转换为非终结符，并添加新的非终结符，新的规则
	 */
	private void ConvertToNonTerRHS(RewriteRule rule)
	{
		ArrayList<String> rhs = new ArrayList<String>();
		for (String string : rule.getRhs())
		{
			if (!pcnf.getNonTerminalSet().contains(string))
			{
				String newString = "$" + string+"$";
				pcnf.addNonTerminal(newString);// 添加新非终结符
				pcnf.add(new PRule(1.0, newString, string));// 添加新规则
				rhs.add(newString);
			}
			else
			{
				rhs.add(string);
			}
		}
		rule.setRhs(rhs);
	}
	/**
	 * 每次选择最右侧字符串的两个为新的规则的右侧字符串
	 */
	private void reduceRHSNum(RewriteRule rule)
	{
		if (rule.getRhs().size() == 2)
		{
			pcnf.add(rule);
			return;
		}
		List<String> list = rule.getRhs();
		int size = list.size();
		String str = list.get(size - 2) + "&"+list.get(size - 1);// 新规则的左侧

		// 最右侧的两个非终结符合成一个，并形成新的规则
		PRule rule1 = new PRule(1.0, str, list.get(size - 2), list.get(size - 1));
		pcnf.add(rule1);
		pcnf.addNonTerminal(str);// 添加新的非终结符
		ArrayList<String> rhsList = new ArrayList<String>();
		rhsList.addAll(rule.getRhs().subList(0, rule.getRhs().size() - 2));
		rhsList.add(str);
		rule.setRhs(rhsList);
		/*
		 * 递归，直到rhs的个数为2时
		 */
		reduceRHSNum(rule);
	}
}

