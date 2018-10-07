package com.lc.nlp4han.constituent.unlex;

import java.util.LinkedList;

/**
 * @author 王宁
 * @version 创建时间：2018年9月24日 下午10:26:38 表示词性标注产生单词的规则
 */
public class PreterminalRule extends Rule
{
	String word;
	LinkedList<Double> scores = new LinkedList<Double>();

	public PreterminalRule(short parent,String word)
	{
		super.parent = parent;
		this.word = word;
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

}
