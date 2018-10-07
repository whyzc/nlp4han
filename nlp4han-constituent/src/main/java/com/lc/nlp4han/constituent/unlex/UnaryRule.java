package com.lc.nlp4han.constituent.unlex;

import java.util.LinkedList;

/**
 * @author 王宁
 * @version 创建时间：2018年9月24日 下午6:45:16 一元规则
 */
public class UnaryRule extends Rule
{
	private short child;
	LinkedList<LinkedList<Double>> scores = new LinkedList<LinkedList<Double>>();// 保存规则例如A -> B 的概率

	public UnaryRule(short parent, short child)
	{
		super.parent = parent;
		this.child = child;
	}
	
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + child;
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
		UnaryRule other = (UnaryRule) obj;
		if (child != other.child)
			return false;
		return true;
	}

	public short getChild()
	{
		return child;
	}

	public void setChild(short child)
	{
		this.child = child;
	}

	public LinkedList<LinkedList<Double>> getScores()
	{
		return scores;
	}

	public void setScores(LinkedList<LinkedList<Double>> score)
	{
		this.scores = score;
	}

	public boolean isSameRule(short parent, short child)
	{
		if(this.parent == parent && this.child == child)
			return true;
		else
			return false;
	}
}
