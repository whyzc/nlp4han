package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

public class ExtractCFG
{
	/**
	 * 定义文法的变量
	 */
	private CFG cfg;

	/**
	 * 返回文法集，便于测试
	 */
	public CFG getCFG()
	{
		return this.cfg;
	}

	/**
	 * 生成文法集
	 */
	public CFG CreateCFG(String fileName, String enCoding) throws IOException
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
		bracketStrListConvertToGrammar(bracketStrList);
		return cfg;
	}

	/**
	 * 由括号表达式的list得到对应的文法集合
	 * @param bracketStrList
	 * @throws IOException
	 */
	public void bracketStrListConvertToGrammar(ArrayList<String> bracketStrList) throws IOException
	{
		cfg = new CFG();
		for (String bracketStr : bracketStrList)
		{
			TreeNode rootNode1 = BracketExpUtil.generateTreeNotDeleteBracket(bracketStr);
			traverseTree(rootNode1);
		}
	}

	/**
	 * 遍历树得到CFG
	 */
	/**
	 * @param node
	 */
	public void traverseTree(TreeNode node)
	{
		if (cfg.getStartSymbol() == null)
		{// 起始符提取
			cfg.setStartSymbol(node.getNodeName());
		}
		if (node.getChildren().size() == 0)
		{
			cfg.addTerminal(node.getNodeName());// 终结符提取
			return;
		}
		cfg.addNonTerminal(node.getNodeName());// 非终结符提取

		if (node.getChildren() != null && node.getChildren().size() > 0)
		{
			RewriteRule rule = new RewriteRule(node.getNodeName(), node.getChildren());
			cfg.add(rule);
			;// 添加规则
			for (TreeNode node1 : node.getChildren())
			{// 深度优先遍历
				traverseTree(node1);
			}
		}
	}
}
