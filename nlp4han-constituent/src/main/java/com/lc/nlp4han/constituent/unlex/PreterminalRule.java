package com.lc.nlp4han.constituent.unlex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;

/**
 * 表示词性标注产生单词的规则
 * 
 * @author 王宁
 * 
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
		Random random = Grammar.random;
		boolean randomPerturbation = true;
		// split father
		int pNumSubSymbol = scores.size();
		for (int i = pNumSubSymbol - 1; i >= 0; i--)
		{
			scores.add(i + 1, scores.get(i));
			scores.set(i, scores.get(i + 1));
		}
		if (randomPerturbation)
		{
			double randomness = 1.0;
			int parentSplitFactor = 2;
			int pNumSub_beforeSplit = scores.size() / 2;
			for (short pS = 0; pS < pNumSub_beforeSplit; pS++)
			{
				final double oldScore_beforeSplit = scores.get(pS * parentSplitFactor);

				for (short p = 0; p < parentSplitFactor; p++)
				{
					double randomValue = (random.nextDouble() + 0.25) * 0.8;
					double randomComponent = oldScore_beforeSplit * randomness / 100.0 * randomValue;
					short newPS = (short) (parentSplitFactor * pS + p);
					scores.set(newPS, oldScore_beforeSplit + randomComponent);
				}
			}
		}
	}

	@Override
	public void merge(Short[][] symbolToMerge, double[][] weights)
	{
		if (symbolToMerge[parent] == null)
			return;
		// 合并parent
		int nPToMerge = symbolToMerge[parent].length;
		for (int indexPToMerge = nPToMerge - 1; indexPToMerge >= 0; indexPToMerge--)
		{
			int indexP = symbolToMerge[parent][indexPToMerge];
			double scoresP1ToC = scores.get(indexP);
			double scoresP2ToC = scores.get(indexP + 1);
			// 合并parent的subSymbol时需要赋予规则概率权重
			scores.set(indexP, scoresP1ToC * weights[parent][indexP] + scoresP2ToC * weights[parent][indexP + 1]);
			scores.remove(indexP + 1);
		}
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	public int chidrenHashcode()
	{
		return word.hashCode();
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

	@Override
	boolean withIn(HashSet<? extends Rule> rules)
	{
		if (rules.contains(this))
			return true;
		else
			return false;
	}

	@Override
	public String[] toStringRules(NonterminalTable nonterminalTable)
	{
		String[] strs = new String[scores.size()];
		for (int i = 0; i < scores.size(); i++)
		{
			String parentStr;
			if (nonterminalTable.getNumSubsymbolArr().get(parent) == 1)
				parentStr = nonterminalTable.stringValue(parent);
			else
				parentStr = nonterminalTable.stringValue(parent) + "_" + i;
			String childStr = word;
			String str = parentStr + "->" + childStr + " " + scores.get(i);
			strs[i] = str;
		}
		return strs;
	}

	public String toStringRule(NonterminalTable nonterminalTable, short... labels)
	{
		if (labels.length != 1)
			throw new Error("参数错误。");
		String parentStr = nonterminalTable.stringValue(parent);
		String childStr = word;
		String str = parentStr + "_" + labels[0] + "->" + childStr + " " + scores.get(labels[0]);
		return str;
	}

	public TreeMap<String, Double> getParent_i_ScoceSum(NonterminalTable nonterminalTable)
	{
		TreeMap<String, Double> A_iWordRuleSum = new TreeMap<>();
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
			A_iWordRuleSum.put(parentStr, scores.get(i));
		}
		return A_iWordRuleSum;
	}
}
