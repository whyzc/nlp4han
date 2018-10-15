package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * @author 王宁
 * @version 创建时间：2018年9月24日 下午6:45:16 一元规则
 */
public class UnaryRule extends Rule
{
	private short child;
	LinkedList<LinkedList<Double>> scores = new LinkedList<LinkedList<Double>>();// 保存规则例如Ai -> Bj 的概率

	public UnaryRule(short parent, short child)
	{
		super.parent = parent;
		this.child = child;
	}

	@Override
	public void split()
	{// TODO:加入随机扰动
		// split child
		int pNumSubSymbol = scores.size();
		for (int i = 0; i < pNumSubSymbol; i++)
		{
			LinkedList<Double> sameFather = scores.get(i);// Father均为A_i的scores
			int cNumSubSymbol = sameFather.size();
			for (int j = cNumSubSymbol - 1; j >= 0; j--)
			{

				sameFather.add(j + 1, BigDecimal.valueOf(sameFather.get(j))
						.divide(BigDecimal.valueOf(2.0), 15, BigDecimal.ROUND_HALF_UP).doubleValue());
				sameFather.set(j, sameFather.get(j + 1));
			}
			scores.set(i, sameFather);
		}

		// split father
		for (int i = pNumSubSymbol - 1; i >= 0; i--)
		{
			scores.get(i).replaceAll(e -> BigDecimal.valueOf(e.doubleValue())
					.divide(BigDecimal.valueOf(2.0), 15, BigDecimal.ROUND_HALF_UP).doubleValue());
			LinkedList<Double> sameChild = new LinkedList<>(scores.get(i));
			scores.add(i + 1, sameChild);
		}
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
		if (this.parent == parent && this.child == child)
			return true;
		else
			return false;
	}
}
