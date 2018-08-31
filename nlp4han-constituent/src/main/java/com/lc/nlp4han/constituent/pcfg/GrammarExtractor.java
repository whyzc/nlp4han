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

public class GrammarExtractor
{
	/**
	 * 定义文法的变量
	 */
	private CFG grammar = null;
	private HashMap<PRule, Integer> ruleCounter = null;

	public static CFG getCFG(String fileName, String enCoding) throws IOException
	{

		return new GrammarExtractor().CreateGrammar(fileName, enCoding,"CFG");
	}

	public static PCFG getPCFG(String fileName, String enCoding) throws IOException
	{

		return (PCFG)new GrammarExtractor().CreateGrammar(fileName, enCoding,"PCFG");
	}
	/**
	 * 返回文法集，便于测试
	 */
	public CFG getCFG()
	{
		return this.grammar;
	}
	/**
	 * 生成文法集
	 */
	public CFG CreateGrammar(String fileName, String enCoding, String type) throws IOException
	{
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
		bracketStrListConvertToGrammar(bracketStrList,type);
		if(type.contains("P")) {
			ComputeProOfRule();
		}
		return grammar;

	}

	// 由括号表达式的list得到对应的文法集合
	public void bracketStrListConvertToGrammar(ArrayList<String> bracketStrList,String type) throws IOException
	{
		if (type.contains("P"))
		{
			grammar = new PCFG();
			ruleCounter = new HashMap<PRule, Integer>();
		}
		else
		{
			grammar = new CFG();
		}
		for (String bracketStr : bracketStrList)
		{
			TreeNode rootNode1 = BracketExpUtil.generateTreeNotDeleteBracket(bracketStr);
			traverseTree(rootNode1,type);
		}
	}

	/**
	 * 遍历树得到基本文法
	 */
	private void traverseTree(TreeNode node,String type)
	{
		if (grammar.getStartSymbol() == null)
		{// 起始符提取
			grammar.setStartSymbol(node.getNodeName());
		}
		if (node.getChildren().size() == 0)
		{
			grammar.addTerminal(node.getNodeName());// 终结符提取
			return;
		}
		grammar.addNonTerminal(node.getNodeName());// 非终结符提取

		if (node.getChildren() != null && node.getChildren().size() > 0)
		{
			RewriteRule rule = new RewriteRule(node.getNodeName(), node.getChildren());
			if(type.contains("P")) {
				rule = new PRule(rule,0);
				addRuleCount((PRule)rule);
			}
			grammar.add(rule);
			;// 添加规则
			for (TreeNode node1 : node.getChildren())
			{// 深度优先遍历
				traverseTree(node1,type);
			}
		}
	}

	/**
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

	/**
	 * 计算规则概率
	 */
	private void ComputeProOfRule()
	{
		for (String nonTer : grammar.getNonTerminalSet())
		{
//			Set<PRule> set = PCFG.convertRewriteRuleSetToPRuleSet(grammar.getRuleBylhs(nonTer));
			Set<RewriteRule> set = grammar.getRuleBylhs(nonTer);
			int allNum = 0;
			for (RewriteRule rule : set)
			{
				PRule pr = (PRule)rule;
				allNum += ruleCounter.get(pr);
			}
			for (RewriteRule rule : set)
			{
				PRule pr = (PRule)rule;
				pr.setProOfRule(1.0 * ruleCounter.get(rule) / allNum);
			}
		}
	}
	/**
	 * @throws IOException
	 * 由括号表达式列表直接得到PCFG
	 */
	public PCFG getPCFG(ArrayList<String> bracketStrList) throws IOException
	{
		grammar = new PCFG();
		ruleCounter = new HashMap<PRule, Integer>();
		bracketStrListConvertToGrammar(bracketStrList,"PCFG");
		ComputeProOfRule();
		return (PCFG)grammar;
	}
	/**
	 * 获得计数器
	 */
	public HashMap<PRule, Integer> getRuleCounter()
	{
		return ruleCounter;
	}
}
