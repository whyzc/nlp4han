package com.lc.nlp4han.constituent.lex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.lex.CTBPreprocessTool;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

public class POSProUtil
{
	public static HashMap<String, Double> getNonterminalPro(String fileName, String enCoding) throws IOException
	{
		// 括号表达式树拼接成括号表达式String数组
		PlainTextByTreeStream ptbt = new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileName)),
				enCoding);
		String bracketStr = ptbt.read();
		ArrayList<String> bracketStrList = new ArrayList<String>();
		while (bracketStr != null)
		{
			bracketStrList.add(bracketStr);
			bracketStr = ptbt.read();
		}
		ptbt.close();
		// 括号表达式生成文法
		HashMap<String, Double> map = brackets2Map(bracketStrList, null);
		return map;
	}

	public static HashMap<String, Double> brackets2Map(ArrayList<String> bracketStrList, String type)
	{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String bracketStr : bracketStrList)
		{
			TreeNode rootNode1 = BracketExpUtil.generateTree(bracketStr);
			if (type.equals("lex"))
			{
				CTBPreprocessTool.traverseTreeAddNPB(rootNode1);
			}
			traverse(rootNode1, map);
		}
		HashMap<String, Double> map1 = computePro(map);
		return map1;
	}

	private static void traverse(TreeNode node, HashMap<String, Integer> map)
	{
		if (node.getChildrenNum() == 0 || (node.getChildrenNum() == 1 && node.getChild(0).getChildrenNum() == 0))
		{
			return;
		}
		else
		{
			String str = node.getNodeName();
			if (map.containsKey(str))
			{
				int n = map.get(str) + 1;
				map.put(str, n);
			}
			else
			{
				map.put(str, 1);
			}
		}
		for (TreeNode node1 : node.getChildren())
		{
			traverse(node1, map);
		}
	}

	private static HashMap<String, Double> computePro(HashMap<String, Integer> map)
	{
		int sum = 0;
		HashMap<String, Double> map1 = new HashMap<String, Double>();
		for (String str : map.keySet())
		{
			sum += map.get(str);
		}
		for (String str : map.keySet())
		{
			double pro = 1.0 * map.get(str) / sum;
			map1.put(str, pro);
		}
		return map1;
	}
}