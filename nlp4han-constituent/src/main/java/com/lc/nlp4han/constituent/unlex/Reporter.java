package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * @author 作者
 * @version 创建时间：2018年10月10日 上午10:24:32 类说明
 */
public class Reporter
{
	public static Map<String, Integer> words = new TreeMap<String, Integer>();
	public static Map<String, Integer> tags = new TreeMap<String, Integer>();
	public static Map<String, Integer> nonterminals = new TreeMap<String, Integer>();

	public static int tokens = 0;

	public static void report(List<Tree<String>> treeBank)
	{
		for (Tree<String> tree : treeBank)
		{
			reportHelper(tree);
		}
	}

	public static void reportHelper(Tree<String> tree)
	{
		if (tree == null)
			return;
		if (!tree.isLeaf())
		{
			if (tree.isPreterminal())
			{
				if (!tags.containsKey(tree.getLabel()))
				{
					tags.put(tree.getLabel(), 1);
				}
				else
				{
					tags.replace(tree.getLabel(), tags.get(tree.getLabel()).intValue() + 1);
				}
			}
			else 
			{
				if (!nonterminals.containsKey(tree.getLabel()))
				{
					nonterminals.put(tree.getLabel(), 1);
				}
				else
				{
					nonterminals.replace(tree.getLabel(), nonterminals.get(tree.getLabel()).intValue() + 1);
				}
			}
		}
		else
		{
			tokens ++;
			if (!words.containsKey(tree.getLabel()))
			{
				words.put(tree.getLabel(), 1);
			}
			else
			{
				words.replace(tree.getLabel(), words.get(tree.getLabel()).intValue() + 1);
			}
		}

		for (Tree<String> child : tree.getChildren())
		{
			reportHelper(child);
		}
	}

	public static void main(String[] args)
	{
		String outputPath = "C:\\Users\\hp\\Desktop\\ctb8-bracket-clear-utf8.report";

		String treeBankPath = "C:\\Users\\hp\\Desktop\\自然语言处理\\自然语言处理\\语料\\中文句法结构树库（括号表达式）\\ctb8-bracket-clear-utf8.txt";
		boolean addParentLabel = false;
		boolean binaryTree = false;
		List<Tree<String>> trees = new ArrayList<Tree<String>>();
		System.out.println("开始统计。");
		try
		{

			InputStream ins = new FileInputStream(treeBankPath);
			InputStreamReader isr = new InputStreamReader(ins, "utf-8");
			BufferedReader allSentence = new BufferedReader(isr);
			String expression = allSentence.readLine();

			while (expression != null)// 用来得到树库对应的所有结构树Tree<String>
			{
				expression = expression.trim();
				if (!expression.equals(""))
				{
					TreeNode tempTree = BracketExpUtil.generateTreeNotDeleteBracket(expression);
					Tree<String> tree = TreeUtil.getStringTree(tempTree);
					tree = TreeUtil.removeL2LRule(tree);
					if (addParentLabel)
						tree = TreeUtil.addParentLabel(tree);
					if (binaryTree)
						tree = TreeUtil.binarizeTree(tree);
					trees.add(tree);
				}

				expression = allSentence.readLine();
			}
			allSentence.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		report(trees);

		try
		{
			OutputStream os = new FileOutputStream(outputPath);

			OutputStreamWriter osw = new OutputStreamWriter(os, "gbk");
			BufferedWriter bw = new BufferedWriter(osw);

			bw.write("非终端节点：" + nonterminals.size() + "\n");
			for (Map.Entry<String, Integer> nonterminal : nonterminals.entrySet())
			{
				bw.write(nonterminal.getKey() + " " + nonterminal.getValue() + "\n");
			}

			bw.write("********************\n");
			bw.write("词性标记：" + tags.size() + "\n");
			for (Map.Entry<String, Integer> tag : tags.entrySet())
			{
				bw.write(tag.getKey() + " " + tag.getValue() + "\n");
			}

			bw.write("********************\n");
			bw.write("词条数：" + tokens + "\n");
			bw.write("词型数：" + words.size() + "\n");
			for (Map.Entry<String, Integer> word : words.entrySet())
			{
				bw.write(word.getKey() + " " + word.getValue() + "\n");
			}
			bw.close();
			System.out.println("统计完毕。");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
