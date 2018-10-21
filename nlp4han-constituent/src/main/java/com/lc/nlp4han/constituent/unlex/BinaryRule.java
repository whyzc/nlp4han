package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * 二元规则
 * 
 * @author 王宁
 */
public class BinaryRule extends Rule
{
	private short leftChild;
	private short rightChild;
	LinkedList<LinkedList<LinkedList<Double>>> scores = new LinkedList<LinkedList<LinkedList<Double>>>();// 保存规则例如A_i ->
																											// B_j
																											// C_k的概率
	double[][][] countExpectation = null;

	public BinaryRule(short parent, short lChild, short rChild)
	{
		super.parent = parent;
		this.leftChild = lChild;
		this.rightChild = rChild;
	}

	@Override
	public void split()
	{
		// split rightChild
		int pNumSubSymbol = scores.size();
		for (int i = 0; i < pNumSubSymbol; i++)
		{
			int lCNumSubsymbol = scores.get(i).size();
			for (int j = 0; j < lCNumSubsymbol; j++)
			{
				int rCNumSubsymbol = scores.get(i).get(j).size();
				for (int k = rCNumSubsymbol - 1; k >= 0; k--)
				{
					scores.get(i).get(j).add(k + 1, BigDecimal.valueOf(scores.get(i).get(j).get(k))
							.divide(BigDecimal.valueOf(2.0), 15, BigDecimal.ROUND_HALF_UP).doubleValue());
					scores.get(i).get(j).set(k, scores.get(i).get(j).get(k + 1));
				}
			}
		}
		// split leftChild
		for (int i = 0; i < pNumSubSymbol; i++)
		{
			int lCNumSubsymbol = scores.get(i).size();
			for (int j = lCNumSubsymbol - 1; j >= 0; j--)
			{
				scores.get(i).get(j).replaceAll(e -> BigDecimal.valueOf(e.doubleValue())
						.divide(BigDecimal.valueOf(2.0), 15, BigDecimal.ROUND_HALF_UP).doubleValue());
				LinkedList<Double> sameRC = new LinkedList<Double>(scores.get(i).get(j));
				scores.get(i).add(j + 1, sameRC);
			}

		}
		// split father
		for (int i = pNumSubSymbol - 1; i >= 0; i--)
		{
			LinkedList<LinkedList<Double>> sameFather = new LinkedList<LinkedList<Double>>();
			for (int j = 0; j < scores.get(i).size(); j++)
			{
				scores.get(i).get(j).replaceAll(e -> BigDecimal.valueOf(e.doubleValue())
						.divide(BigDecimal.valueOf(2.0), 15, BigDecimal.ROUND_HALF_UP).doubleValue());
				LinkedList<Double> sameLRC = new LinkedList<Double>(scores.get(i).get(j));
				sameFather.add(sameLRC);
			}
			scores.add(i + 1, sameFather);
		}
	}

	public boolean isSameRule(short parent, short lChild, short rChild)
	{
		if (this.parent == parent && this.leftChild == lChild && this.rightChild == rChild)
			return true;
		else
			return false;
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + leftChild;
		result = prime * result + rightChild;
		return result;
	}

	public int chidrenHashcode()
	{
		final int prime = 31;
		int result = leftChild;
		result = result * prime + rightChild;
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
		BinaryRule other = (BinaryRule) obj;
		if (leftChild != other.leftChild)
			return false;
		if (rightChild != other.rightChild)
			return false;
		return true;
	}

	public short getLeftChild()
	{
		return leftChild;
	}

	public void setLeftChild(short leftChild)
	{
		this.leftChild = leftChild;
	}

	public short getRightChild()
	{
		return rightChild;
	}

	public void setRightChild(short rightChild)
	{
		this.rightChild = rightChild;
	}

	public LinkedList<LinkedList<LinkedList<Double>>> getScores()
	{
		return scores;
	}

	public void setScores(LinkedList<LinkedList<LinkedList<Double>>> scores)
	{
		this.scores = scores;
	}

	public double[][][] getCountExpectation()
	{
		return countExpectation;
	}

	public void setCountExpectation(double[][][] countExpectation)
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

	public static void main(String[] args)
	{
		HashSet<BinaryRule> set = new HashSet<BinaryRule>();
		BinaryRule bRule = new BinaryRule((short) 1, (short) 2, (short) 3);
		bRule.withIn(set);
	}

	@Override
	public String[] toStringRules()
	{
		String[] strs = new String[scores.size() * scores.get(0).size() * scores.get(0).get(0).size()];
		int count = 0;
		for (int i = 0; i < scores.size(); i++)
		{
			for (int j = 0; j < scores.get(0).size(); j++)
			{
				for (int k = 0; k < scores.get(0).get(0).size(); k++)
				{
					String parentStr;
					String lChildStr;
					String rChildStr;
					if (nonterminalTable.getNumSubsymbolArr().get(parent) == 1)
						parentStr = nonterminalTable.stringValue(parent);
					else
						parentStr = nonterminalTable.stringValue(parent) + "_" + i;
					if (nonterminalTable.getNumSubsymbolArr().get(leftChild) == 1)
						lChildStr = nonterminalTable.stringValue(leftChild);
					else
						lChildStr = nonterminalTable.stringValue(leftChild) + "_" + j;
					if (nonterminalTable.getNumSubsymbolArr().get(rightChild) == 1)
						rChildStr = nonterminalTable.stringValue(rightChild);
					else
						rChildStr = nonterminalTable.stringValue(rightChild) + "_" + k;
					String str = parentStr + "->" + lChildStr + " " + rChildStr + " " + scores.get(i).get(j).get(k);
					strs[count] = str;
					count++;
				}
			}
		}
		return strs;
	}

	@Override
	public String toStringRule(short... labels)
	{
		if (labels.length != 3)
			throw new Error("参数错误。");
		String parentStr = nonterminalTable.stringValue(parent);
		String lChildStr = nonterminalTable.stringValue(leftChild);
		String rChildStr = nonterminalTable.stringValue(rightChild);
		String str = parentStr + "_" + labels[0] + "->" + lChildStr + "_" + labels[1] + " " + rChildStr + "_"
				+ labels[2] + " " + scores.get(labels[0]).get(labels[1]).get(labels[2]);
		return str;
	}

	public TreeMap<String, Double> getParent_i_ScoceSum()
	{
		TreeMap<String, Double> A_iBCRuleSum = new TreeMap<>();
		if (scores.size() == 1)
		{
			A_iBCRuleSum.put(nonterminalTable.stringValue(parent), scores.get(0).get(0).get(0));
		}
		else
		{
			for (int i = 0; i < scores.size(); i++)
			{
				BigDecimal A_iScore = BigDecimal.valueOf(0.0);
				for (int j = 0; j < scores.get(0).size(); j++)
				{
					for (int k = 0; k < scores.get(0).get(0).size(); k++)
					{
						A_iScore.add(BigDecimal.valueOf(scores.get(i).get(j).get(k)));
					}
				}
				A_iBCRuleSum.put(nonterminalTable.stringValue(parent) + "_" + i, A_iScore.doubleValue());
			}
		}
		return A_iBCRuleSum;
	}

}
