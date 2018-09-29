package com.lc.nlp4han.constituent.unlex;

/**
 * @author 王宁
 * @version 创建时间：2018年9月23日 下午12:32:33 树中的节点
 */
public class Annotation
{
	private String word;// 表示句中词语，非终端节点才有
	private short symbol = -1;
	private short numSubSymbol;
	private short spanFrom, spanTo;
	private double[] innerScores;// 内向概率
	private double[] outerScores;// 外向概率

	public Annotation(short symbol, short numSubSymbol)
	{
		this.symbol = symbol;
		this.numSubSymbol = numSubSymbol;
	}

	public Annotation(String word)
	{
		this.word = word;
	}

	public String getWord()
	{
		return word;
	}

	public void setWord(String word)
	{
		this.word = word;
	}

	public short getSymbol()
	{
		return symbol;
	}

	public void setSymbol(short symbol)
	{
		this.symbol = symbol;
	}

	public short getNumSubSymbol()
	{
		return numSubSymbol;
	}

	public void setNumSubSymbol(short numSubSymbol)
	{
		this.numSubSymbol = numSubSymbol;
	}

	public short getSpanTo()
	{
		return spanTo;
	}

	public void setSpanTo(short spanTo)
	{
		this.spanTo = spanTo;
	}

	public short getSpanFrom()
	{
		return spanFrom;
	}

	public void setSpanFrom(short spanFrom)
	{
		this.spanFrom = spanFrom;
	}

	public double[] getInnerScores()
	{
		return innerScores;
	}

	public void setInnerScores(double[] innerScores)
	{
		this.innerScores = innerScores;
	}

	public double[] getOuterScores()
	{
		return outerScores;
	}

	public void setOuterScores(double[] outerScores)
	{
		this.outerScores = outerScores;
	}

	
}
