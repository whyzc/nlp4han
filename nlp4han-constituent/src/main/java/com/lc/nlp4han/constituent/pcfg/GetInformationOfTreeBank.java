package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

public class GetInformationOfTreeBank
{
	private int tokenCount;// 词条数
	private HashSet<String> wordShapeSet;
	private int sentenceCount;// 句子数
	private int wordCount;// 总字数(包括标点符号)
	private int nonTerminalCount;// 非终结符的个数

	public GetInformationOfTreeBank()
	{
		tokenCount = 0;
		wordShapeSet = new HashSet<String>();
		sentenceCount = 0;
		wordCount = 0;
		nonTerminalCount = 0;
	}

	/**
	 * 生成文法集
	 */
	public TreeBankReport getInformationOfTreeLibrary(String fileName, String enCoding) throws IOException
	{
		// 括号表达式树拼接成括号表达式String数组
		PlainTextByTreeStream ptbt = new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileName)),
				enCoding);
		String bracketStr = ptbt.read();
		int heighest = 0;
		int lowest = 1000;
		int heightTatleCount = 0;
		while (bracketStr.length() != 0)
		{
			sentenceCount++;
			int height = TraverseTree(BracketExpUtil.generateTreeNotDeleteBracket(bracketStr));
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
		int meanLevelOfTree = heightTatleCount / sentenceCount;
		ptbt.close();
		// 括号表达式生成文法
		return new TreeBankReport(tokenCount, wordShapeSet.size(), sentenceCount, wordCount, nonTerminalCount, heighest,
				lowest, meanLevelOfTree);
	}

	private int TraverseTree(TreeNode node)
	{
		if (node.getChildrenNum() == 0)
		{
			// 根节点
			tokenCount++;// 词条数
			wordShapeSet.add(node.getNodeName());
			wordCount += node.getNodeName().length();// 总字数(包括标点符号)
			return 1;
		}
		else
		{
			nonTerminalCount++;
		}
		int height = 0;
		for (TreeNode node1 : node.getChildren())
		{
			int length = TraverseTree(node1) + 1;
			if (length > height)
			{
				height = length;
			}
		}
		return height;
	}
}
