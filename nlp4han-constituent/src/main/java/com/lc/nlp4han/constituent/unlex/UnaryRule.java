package com.lc.nlp4han.constituent.unlex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
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
	{
		Random random = Grammar.random;
		boolean randomPerturbation = true;
		// split child
		int pNumSubSymbol = scores.size();
		for (int i = 0; i < pNumSubSymbol; i++)
		{
			LinkedList<Double> sameFather = scores.get(i);// Father均为A_i的scores
			int cNumSubSymbol = sameFather.size();
			for (int j = cNumSubSymbol - 1; j >= 0; j--)
			{

				sameFather.add(j + 1, sameFather.get(j) / 2);
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
		if (randomPerturbation)
		{
			double randomness = 1.0;
			int parentSplitFactor = parent == 0 ? 1 : 2;
			int childSplitFactor = 2;
			int cNumSub_beforeSplit = scores.get(0).size() / 2;
			int pNumSub_beforeSplit = parentSplitFactor == 1 ? 1 : scores.size() / 2;
			for (short cS = 0; cS < cNumSub_beforeSplit; cS++)
			{
				for (short pS = 0; pS < pNumSub_beforeSplit; pS++)
				{
					final double oldScore_beforeSplit = scores.get(pS * parentSplitFactor).get(cS * childSplitFactor)
							* 2;
					for (short p = 0; p < parentSplitFactor; p++)
					{
						double divFactor = childSplitFactor;
						double randomValue = (random.nextDouble() + 0.25) * 0.8;
						double randomComponent = oldScore_beforeSplit / divFactor * randomness / 100.0 * randomValue;
						for (short c = 0; c < childSplitFactor; c++)
						{
							if (c == 1)
							{
								randomComponent = randomComponent * -1;
							}
							short newPS = (short) (parentSplitFactor * pS + p);
							short newLCS = (short) (childSplitFactor * cS + c);
							double splitFactor = childSplitFactor;
							scores.get(newPS).set(newLCS, oldScore_beforeSplit / splitFactor + randomComponent);
						}
					}
				}
			}
		}
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + child;
		return result;
	}

	// public int chidrenHashcode()
	// {
	// return child;
	// }

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
			double A_iScore = 0.0;
			for (int j = 0; j < scores.get(0).size(); j++)
			{
				A_iScore = A_iScore + scores.get(i).get(j);
			}
			A_iBRuleSum.put(parentStr, A_iScore);
		}
		return A_iBRuleSum;
	}
}
