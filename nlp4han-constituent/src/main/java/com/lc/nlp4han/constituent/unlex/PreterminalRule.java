package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * @author 王宁
 * @version 创建时间：2018年9月24日 下午10:26:38 表示词性标注产生单词的规则
 */
public class PreterminalRule extends Rule
{
	String word;
	LinkedList<Double> scores = new LinkedList<Double>();

	public PreterminalRule(short parent, String word)
	{
		super.parent = parent;
		this.word = word;
	}

	@Override
	public void split()
	{
		// split father
		int pNumSubSymbol = scores.size();
		for (int i = pNumSubSymbol - 1; i >= 0; i--)
		{
			scores.add(i + 1, BigDecimal.valueOf(scores.get(i))
					.divide(BigDecimal.valueOf(2.0), 15, BigDecimal.ROUND_HALF_UP).doubleValue());
			scores.set(i, scores.get(i + 1));
		}
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PreterminalRule other = (PreterminalRule) obj;
		if (word == null)
		{
			if (other.word != null)
				return false;
		}
		else if (!word.equals(other.word))
			return false;
		return true;
	}

	public String getWord()
	{
		return word;
	}

	public void setWord(String word)
	{
		this.word = word;
	}

	public LinkedList<Double> getScores()
	{
		return scores;
	}

	public void setScores(LinkedList<Double> scores)
	{
		this.scores = scores;
	}

}
