package com.lc.nlp4han.constituent.pcfg;

public class TreeBankReport
{
	private int tokenCount;// 词条数
	private int wordShapeCount;// 词形数
	private int sentenceCount;// 句子数
	private int wordCount;// 总字数(包括标点符号)
	private int nonTerminalCount;// 非终结符的个数
	private int numOfHighestTree;// 库中句法树的最大高度
	private int numOfLowestTree;// 库中句法树的最小高度
	private int meanLevelOfTree;// 库中句法树的平均高度

	public TreeBankReport()
	{

	}

	public TreeBankReport(int tokenCount, int wordShapeCount, int sentenceCount, int wordCount, int nonTerminalCount,
			int numOfHighestTree, int numOfLowestTree, int meanLevelOfTree)
	{
		this.tokenCount = tokenCount;
		this.wordShapeCount = wordShapeCount;
		this.sentenceCount = sentenceCount;
		this.wordCount = wordCount;
		this.nonTerminalCount = nonTerminalCount;
		this.numOfHighestTree = numOfHighestTree;
		this.numOfLowestTree = numOfLowestTree;
		this.meanLevelOfTree = meanLevelOfTree;
	}

	public int getTokenCount()
	{
		return tokenCount;
	}

	public void setTokenCount(int tokenCount)
	{
		this.tokenCount = tokenCount;
	}

	public int getWordShapeCount()
	{
		return wordShapeCount;
	}

	public void setWordShapeCount(int wordShapeCount)
	{
		this.wordShapeCount = wordShapeCount;
	}

	public int getSentenceCount()
	{
		return sentenceCount;
	}

	public void setSentenceCount(int sentenceCount)
	{
		this.sentenceCount = sentenceCount;
	}

	public int getWordCount()
	{
		return wordCount;
	}

	public void setWordCount(int wordCount)
	{
		this.wordCount = wordCount;
	}

	public int getNonTerminalCount()
	{
		return nonTerminalCount;
	}

	public void setNonTerminalCount(int nonTerminalCount)
	{
		this.nonTerminalCount = nonTerminalCount;
	}

	public int getNumOfHighestTree()
	{
		return numOfHighestTree;
	}

	public void setNumOfHighestTree(int numOfHighestTree)
	{
		this.numOfHighestTree = numOfHighestTree;
	}

	public int getNumOfLowestTree()
	{
		return numOfLowestTree;
	}

	public void setNumOfLowestTree(int numOfLowestTree)
	{
		this.numOfLowestTree = numOfLowestTree;
	}

	public int getMeanLevelOfTree()
	{
		return meanLevelOfTree;
	}

	public void setMeanLevelOfTree(int meanLevelOfTree)
	{
		this.meanLevelOfTree = meanLevelOfTree;
	}

	@Override
	public String toString()
	{
		return "tokenCount=" + tokenCount + "\n" + "wordShapeCount=" + wordShapeCount + "\n" + "sentenceCount="
				+ sentenceCount + "\n" + "wordCount=" + wordCount + "\n" + "nonTerminalCount=" + nonTerminalCount + "\n"
				+ "numOfHighestTree=" + numOfHighestTree + "\n" + "numOfLowestTree=" + numOfLowestTree + "\n"
				+ "meanLevelOfTree=" + meanLevelOfTree;
	}
}
