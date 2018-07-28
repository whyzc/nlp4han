package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

public class ExtractPCFG
{
	/*
	 * 定义文法的变量
	 */
	private PCFG pcfg;
	private HashMap<PRule, Integer> ruleCounter;

	/*
	 * 生成文法集
	 */
	public PCFG CreatePCFG(String fileName, String enCoding) throws IOException
	{
		pcfg = new PCFG();
		ruleCounter = new HashMap<PRule, Integer>();
		// 括号表达式树拼接成括号表达式String数组
		PlainTextByTreeStream ptbt = new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileName)),
				enCoding);
		String bracketStr = ptbt.read();
		ArrayList<String> bracketStrList = new ArrayList<String>();
		while (bracketStr.length() != 0)
		{
			bracketStrList.add(bracketStr);
			bracketStr = ptbt.read();
		}
		ptbt.close();
		// 括号表达式生成文法
		bracketStrListConvertToGrammar(bracketStrList);
		ComputeProOfRule();
		return pcfg;
	}

	// 由括号表达式的list得到对应的文法集合
	private void bracketStrListConvertToGrammar(ArrayList<String> bracketStrList) throws IOException
	{
		for (String bracketStr : bracketStrList)
		{
			TreeNode rootNode1 = BracketExpUtil.generateTree(bracketStr);
			traverseTree(rootNode1);
		}
	}

	/*
	 * 遍历树得到基本文法
	 */
	private void traverseTree(TreeNode node)
	{
		if (pcfg.getStartSymbol() == null)
		{// 起始符提取
			pcfg.setStartSymbol(node.getNodeName());
		}
		if (node.getChildren().size() == 0)
		{
			pcfg.addTerminal(node.getNodeName());// 终结符提取
			return;
		}
		pcfg.addNonTerminal(node.getNodeName());// 非终结符提取

		if (node.getChildren() != null && node.getChildren().size() > 0)
		{
			PRule rule = new PRule(new RewriteRule(node.getNodeName(), node.getChildren()), 0);
			addRuleCount(rule);
			pcfg.add(rule);
			;// 添加规则
			for (TreeNode node1 : node.getChildren())
			{// 深度优先遍历
				traverseTree(node1);
			}
		}
	}

	/*
	 * 添加规则的计数
	 */
	private void addRuleCount(PRule rule)
	{
		if (ruleCounter.containsKey(rule))
		{
			ruleCounter.put(rule, ruleCounter.get(rule) + 1);
		}
		else
		{
			ruleCounter.put(rule, 1);
		}
	}

	/*
	 * 计算规则概率
	 */
	private void ComputeProOfRule()
	{
		for (String nonTer : pcfg.getNonTerminalSet())
		{
			Set<PRule> set = pcfg.getPRuleBylhs(nonTer);
			int allNum = 0;
			for (PRule rule : set)
			{
				allNum += ruleCounter.get(rule);
			}
			for (PRule rule : set)
			{
				rule.setProOfRule(1.0 * ruleCounter.get(rule) / allNum);
			}
		}
	}

	/*
	 * 获得计数器
	 */
	public HashMap<PRule, Integer> getRuleCounter()
	{
		return ruleCounter;
	}
}
