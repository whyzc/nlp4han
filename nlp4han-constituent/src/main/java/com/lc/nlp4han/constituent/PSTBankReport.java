package com.lc.nlp4han.constituent;

/**
 * 短语结构树树库统计报告
 *
 */
public class PSTBankReport
{
	private int tokenCount;// 词条数
	private int wordShapeCount;// 词形数
	private int sentenceCount;// 句子数
	private int charCount;// 总字数(包括标点符号)
	private int nonTerminalCount;// 非终结符的个数
	private int POSCount;//词性标注数目
	private int numOfHighestTree;// 库中句法树的最大高度
	private int numOfLowestTree;// 库中句法树的最小高度
	private int meanLevelOfTree;// 库中句法树的平均高度
	private int length0_20;
	private int length20_40;
	private int length40_60;
	private int length60_80;
	private int length80_100;
	private int length100;

	public PSTBankReport()
	{

	}

	public PSTBankReport(int tokenCount, int wordShapeCount, int sentenceCount, int charCount, int nonTerminalCount,
			int numOfHighestTree, int numOfLowestTree, int meanLevelOfTree,int POSCount)
	{
		this.tokenCount = tokenCount;
		this.wordShapeCount = wordShapeCount;
		this.sentenceCount = sentenceCount;
		this.charCount = charCount;
		this.nonTerminalCount = nonTerminalCount;
		this.numOfHighestTree = numOfHighestTree;
		this.numOfLowestTree = numOfLowestTree;
		this.meanLevelOfTree = meanLevelOfTree;
		this.POSCount=POSCount;
	}
    
	public PSTBankReport(int tokenCount, int wordShapeCount, int sentenceCount, int charCount, int nonTerminalCount,
			int pOSCount, int numOfHighestTree, int numOfLowestTree, int meanLevelOfTree, int length0_20,
			int length20_40, int length40_60, int length60_80, int length80_100, int length100)
	{
		this.tokenCount = tokenCount;
		this.wordShapeCount = wordShapeCount;
		this.sentenceCount = sentenceCount;
		this.charCount = charCount;
		this.nonTerminalCount = nonTerminalCount;
		POSCount = pOSCount;
		this.numOfHighestTree = numOfHighestTree;
		this.numOfLowestTree = numOfLowestTree;
		this.meanLevelOfTree = meanLevelOfTree;
		this.length0_20 = length0_20;
		this.length20_40 = length20_40;
		this.length40_60 = length40_60;
		this.length60_80 = length60_80;
		this.length80_100 = length80_100;
		this.length100 = length100;
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
		return charCount;
	}

	public void setWordCount(int wordCount)
	{
		this.charCount = wordCount;
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
	public int getPOSCount()
	{
		return POSCount;
	}

	public void setPOSCount(int pOSCount)
	{
		POSCount = pOSCount;
	}
    
	public int getCharCount()
	{
		return charCount;
	}

	public void setCharCount(int charCount)
	{
		this.charCount = charCount;
	}

	public int getLength0_20()
	{
		return length0_20;
	}

	public void setLength0_20(int length0_20)
	{
		this.length0_20 = length0_20;
	}

	public int getLength20_40()
	{
		return length20_40;
	}

	public void setLength20_40(int length20_40)
	{
		this.length20_40 = length20_40;
	}

	public int getLength40_60()
	{
		return length40_60;
	}

	public void setLength40_60(int length40_60)
	{
		this.length40_60 = length40_60;
	}

	public int getLength60_80()
	{
		return length60_80;
	}

	public void setLength60_80(int length60_80)
	{
		this.length60_80 = length60_80;
	}

	public int getLength80_100()
	{
		return length80_100;
	}

	public void setLength80_100(int length80_100)
	{
		this.length80_100 = length80_100;
	}

	public int getLength100()
	{
		return length100;
	}

	public void setLength100(int length100)
	{
		this.length100 = length100;
	}

/*	@Override
	public String toString()
	{
		return "PSTBankReport [tokenCount=" + tokenCount + ", wordShapeCount=" + wordShapeCount + ", sentenceCount="
				+ sentenceCount + ", charCount=" + charCount + ", nonTerminalCount=" + nonTerminalCount + ", POSCount="
				+ POSCount + ", numOfHighestTree=" + numOfHighestTree + ", numOfLowestTree=" + numOfLowestTree
				+ ", meanLevelOfTree=" + meanLevelOfTree + ", length0_20=" + length0_20 + ", length20_40=" + length20_40
				+ ", length40_60=" + length40_60 + ", length60_80=" + length60_80 + ", length80_100=" + length80_100
				+ ", length100=" + length100 + "]";
	}*/

@Override
	public String toString()
	{
		return "tokenCount=" + tokenCount + "\n" + "wordShapeCount=" + wordShapeCount + "\n" + "sentenceCount="
				+ sentenceCount + "\n" + "charCount=" + charCount + "\n" + "nonTerminalCount=" + nonTerminalCount + "\n"
				+ "numOfHighestTree=" + numOfHighestTree + "\n" + "numOfLowestTree=" + numOfLowestTree + "\n"
				+ "meanLevelOfTree=" + meanLevelOfTree+"\n"+ "POSCount=" + POSCount+"\n"+ "length0_20=" + length0_20 + "\n"+ "length20_40=" + length20_40
				+ "\n"+ "length40_60=" + length40_60 + "\n"+ "length60_80=" + length60_80 +"\n"+ "length80_100=" + length80_100
				+ "\n"+ "length100=" + length100;
	}
	
}
