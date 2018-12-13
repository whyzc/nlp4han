package com.lc.nlp4han.constituent;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNodeUtil;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

/**
 * 短语结构树树库统计应用
 *
 */
public class PSTBankReportTool
{
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}

		String frompath = null;
		String encoding = "GBK";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				frompath = args[i + 1];
				i++;
			}

			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}

		PSTBankReport treeBankReport = new PSTBankReportTool().getTreeBankInformation(frompath, encoding);

		System.out.println(treeBankReport.toString());
	}

	private int tokenCount = 0;// 词条数
	private HashSet<String> wordShapeSet = new HashSet<String>();
	private int sentenceCount = 0;// 句子数
	private int charCount = 0;// 总字数(包括标点符号)
	private HashSet<String> nonTerminalSet = new HashSet<String>();// 非终结符集合
	private HashSet<String> posSet = new HashSet<String>();// 词性标注集合

	private PSTBankReport getTreeBankInformation(String frompath, String encoding) throws IOException
	{
		// 括号表达式树拼接成括号表达式String数组
		PlainTextByTreeStream ptbt = new PlainTextByTreeStream(new FileInputStreamFactory(new File(frompath)),
				encoding);
		String bracketStr = ptbt.read();
		int heighest = 0;
		int lowest = 1000;
		int heightTatleCount = 0;
		int[] length = new int[6];
		while (bracketStr!=null)
		{
			sentenceCount++;

			TreeNode root = BracketExpUtil.generateTree(bracketStr);
			addLengthOfSentence(length, root);
			int height = traverseTree(root);

			if (height > heighest)
			{
				heighest = height;
			}

			if (height < lowest)
			{
				lowest = height;
			}

			heightTatleCount += height;

			bracketStr = ptbt.read();
		}

		ptbt.close();

		int meanLevelOfTree = heightTatleCount / sentenceCount;

		// 括号表达式生成文法
		return new PSTBankReport(tokenCount, wordShapeSet.size(), sentenceCount, charCount, nonTerminalSet.size(),
				heighest, lowest, meanLevelOfTree, posSet.size(),length[0],length[1],length[2],length[3],length[4],length[5]);
	}

	private void addLengthOfSentence(int[] length, TreeNode root)
	{
		int num = TreeNodeUtil.getLengthFromTree(root);
		if (0 < num && num <= 20)
		{
			length[0]++;
		}
		else if (20 < num && num <= 40)
		{
			length[1]++;
		}
		else if (40 < num && num <= 60)
		{
			length[2]++;
		}
		else if (60 < num && num <= 80)
		{
			length[3]++;
		}
		else if (80 < num && num <= 100)
		{
			length[4]++;
		}
		else if (num > 100)
		{
			length[5]++;
		}

	}

	private int traverseTree(TreeNode node)
	{
		if (node.getChildrenNum() == 0)
		{
			tokenCount++;// 词条数
			wordShapeSet.add(node.getNodeName());
			charCount += node.getNodeName().length();// 总字数(包括标点符号)

			return 1;
		}
		else
		{
			nonTerminalSet.add(node.nodename);
		}

		if (node.getChildrenNum() == 1 && node.getChild(0).getChildrenNum() == 0)
		{
			posSet.add(node.getNodeName());// 添加词性标注
		}

		int height = 0;
		for (TreeNode node1 : node.getChildren())
		{
			int length = traverseTree(node1) + 1;

			if (length > height)
			{
				height = length;
			}
		}

		return height;
	}
}
