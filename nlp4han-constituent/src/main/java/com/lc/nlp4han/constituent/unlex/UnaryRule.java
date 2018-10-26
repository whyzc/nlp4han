package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * 一元规则
 * 
 * @author 王宁
 */
public class UnaryRule extends Rule
{
	private short child;
	LinkedList<LinkedList<Double>> scores = new LinkedList<LinkedList<Double>>();// 保存规则例如Ai -> Bj 的概率
	double[][] countExpectation = null;

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
		if (parent != 0)
			for (int i = pNumSubSymbol - 1; i >= 0; i--)
			{
				// scores.get(i).replaceAll(e -> BigDecimal.valueOf(e.doubleValue())
				// .divide(BigDecimal.valueOf(2.0), 15,
				// BigDecimal.ROUND_HALF_UP).doubleValue());
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

	public int chidrenHashcode()
	{
		return child;
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

	public double[][] getCountExpectation()
	{
		return countExpectation;
	}

	public void setCountExpectation(double[][] countExpectation)
	{
		this.countExpectation = countExpectation;
	}

	@Override
	boolean withIn(HashSet<? extends Rule> rules)
	{
		if (rules.contains(this))
			return true;
		else
			return false;
	}

	@Override
	public String[] toStringRules()
	{
		String[] strs = new String[scores.size() * scores.get(0).size()];
		int count = 0;
		for (int i = 0; i < scores.size(); i++)
		{
			for (int j = 0; j < scores.get(0).size(); j++)
			{
				String parentStr;
				String childStr;
				if (nonterminalTable.getNumSubsymbolArr().get(parent) == 1)
					parentStr = nonterminalTable.stringValue(parent);
				else
					parentStr = nonterminalTable.stringValue(parent) + "_" + i;
				if (nonterminalTable.getNumSubsymbolArr().get(child) == 1)
					childStr = nonterminalTable.stringValue(child);
				else
					childStr = nonterminalTable.stringValue(child) + "_" + j;
				String str = parentStr + "->" + childStr + " " + scores.get(i).get(j);
				strs[count] = str;
				count++;
			}
		}
		return strs;
	}

	@Override
	public String toStringRule(short... labels)
	{
		if (labels.length != 2)
			throw new Error("参数错误。");
		String parentStr = nonterminalTable.stringValue(parent);
		String childStr = nonterminalTable.stringValue(child);
		String str = parentStr + "_" + labels[0] + "->" + childStr + "_" + labels[1] + " "
				+ scores.get(labels[0]).get(labels[1]);
		return str;
	}

	public TreeMap<String, Double> getParent_i_ScoceSum()
	{
		TreeMap<String, Double> A_iBRuleSum = new TreeMap<>();
		for (int i = 0; i < scores.size(); i++)
		{
			String parentStr;
			if (scores.size() == 1)
			{
				parentStr = nonterminalTable.stringValue(parent);
			}
			else
			{
				parentStr = nonterminalTable.stringValue(parent) + "_" + i;
			}
			BigDecimal A_iScore = BigDecimal.valueOf(0.0);
			for (int j = 0; j < scores.get(0).size(); j++)
			{
				A_iScore = A_iScore.add(BigDecimal.valueOf(scores.get(i).get(j)));
			}
			A_iBRuleSum.put(parentStr, A_iScore.doubleValue());
		}
		return A_iBRuleSum;
	}
}
