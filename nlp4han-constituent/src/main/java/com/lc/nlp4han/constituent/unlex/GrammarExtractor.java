package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 王宁
 * @version 创建时间：2018年9月24日 下午9:02:13 对二叉树得到初始语法 获得规则概率待写
 */
public class GrammarExtractor
{
	// 统计树库过程中得到
	protected List<Tree<Annotation>> treeBank;
	protected NonterminalTable nonterminalTable;

	protected List<Short> preterminal;// 词性标注对应的整数
	protected HashMap<PreterminalRule, Integer>[] preRuleBySameHead;// 长度与preterminal相同
	String[] a = new String[10];
	protected HashMap<BinaryRule, Integer>[] bRuleBySameHead;// 数组下标表示nonterminal对应的整数
	protected HashMap<UnaryRule, Integer>[] uRuleBySameHead;// 数组下标表示nonterminal对应的整数

	public int[] numOfSameHeadRule;

	@SuppressWarnings("unchecked")
	public GrammarExtractor(List<Tree<Annotation>> treeBank, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.nonterminalTable = nonterminalTable;
		preterminal = nonterminalTable.getIntValueOfPreterminalArr();
		preRuleBySameHead = new HashMap[preterminal.size()];
		for (int i = 0; i < preterminal.size(); i++)
		{
			preRuleBySameHead[i] = new HashMap<PreterminalRule,Integer>();
		}
		bRuleBySameHead = new HashMap[nonterminalTable.getNumSymbol()];
		for (int i = 0; i < nonterminalTable.getNumSymbol(); i++)
		{
			bRuleBySameHead[i] = new HashMap<BinaryRule,Integer>();
		}
		uRuleBySameHead = new HashMap[nonterminalTable.getNumSymbol()];
		for (int i = 0; i < nonterminalTable.getNumSymbol(); i++)
		{
			uRuleBySameHead[i] = new HashMap<UnaryRule,Integer>();
		}
		numOfSameHeadRule = new int[this.nonterminalTable.getNumSymbol()];
	}

	public void extractor()
	{
		ArrayDeque<Tree<Annotation>> queue = new ArrayDeque<Tree<Annotation>>();
		for (Tree<Annotation> tree : treeBank)
		{
			queue.offer(tree);
			while (!queue.isEmpty())
			{
				short parent, leftChild = -1, rightChild = -1;
				parent = queue.peek().getLabel().getSymbol();

				if (queue.peek().getChildren().size() == 2)
				{
					// if(queue.peek().getChildren().get(0).getLabel().getWord() == null)
					leftChild = queue.peek().getChildren().get(0).getLabel().getSymbol();
					// if(queue.peek().getChildren().get(1).getLabel().getWord() == null)
					rightChild = queue.peek().getChildren().get(1).getLabel().getSymbol();
					if (leftChild != -1 && rightChild != -1)
					{
						BinaryRule bRule = new BinaryRule(parent, leftChild, rightChild);
						if (!bRuleBySameHead[parent].containsKey(bRule))
						{
							bRuleBySameHead[parent].put(bRule, 1);
						}
						else
						{
							bRuleBySameHead[parent].put(bRule, bRuleBySameHead[parent].get(bRule) + 1);
						}
					}
				}
				else if (queue.peek().getChildren().size() == 1)
				{
					if (queue.peek().getChildren().get(0).getLabel().getWord() == null)
					{
						leftChild = queue.peek().getChildren().get(0).getLabel().getSymbol();
						if (leftChild != -1)
						{
							UnaryRule uRule = new UnaryRule(parent, leftChild);
							if (!uRuleBySameHead[parent].containsKey(uRule))
							{
								uRuleBySameHead[parent].put(uRule, 1);
							}
							else
							{
								uRuleBySameHead[parent].put(uRule, uRuleBySameHead[parent].get(uRule) + 1);
							}
						}

					}
					else
					{
						String child = queue.peek().getChildren().get(0).getLabel().getWord();
						PreterminalRule preRule = new PreterminalRule(parent, child);
						if (!preRuleBySameHead[preterminal.indexOf(parent)].containsKey(preRule))
						{
							preRuleBySameHead[preterminal.indexOf(parent)].put(preRule, 1);
						}
						else
						{
							preRuleBySameHead[preterminal.indexOf(parent)].put(preRule, preRuleBySameHead[preterminal.indexOf(parent)].get(preRule) + 1);
						}
					}
				}

				numOfSameHeadRule[parent]++;
				for (Tree<Annotation> child : queue.poll().getChildren())
				{
					if (!child.getChildren().isEmpty())
						queue.offer(child);
				}
			}
		}
		calculateRuleScores();
	}

	// 计算初始文法的概率
	public void calculateRuleScores()
	{
		for (HashMap<BinaryRule, Integer> map : bRuleBySameHead)
		{
			for (Map.Entry<BinaryRule, Integer> entry : map.entrySet())
			{
				BigDecimal b1 = BigDecimal.valueOf(entry.getValue());
				BigDecimal b2 = BigDecimal.valueOf(numOfSameHeadRule[entry.getKey().parent]);
				double score = b1.divide(b2, 15, BigDecimal.ROUND_HALF_UP).doubleValue();
				entry.getKey().scores.add(new LinkedList<LinkedList<Double>>());
				entry.getKey().scores.get(0).add(new LinkedList<Double>());
				entry.getKey().scores.get(0).get(0).add(score);
			}
		}
		for (HashMap<PreterminalRule, Integer> map : preRuleBySameHead)
		{
			for (Map.Entry<PreterminalRule, Integer> entry : map.entrySet())
			{
				BigDecimal b1 = BigDecimal.valueOf(entry.getValue());
				BigDecimal b2 = BigDecimal.valueOf(numOfSameHeadRule[entry.getKey().parent]);
				double score = b1.divide(b2, 15, BigDecimal.ROUND_HALF_UP).doubleValue();
				entry.getKey().scores.add(score);

			}
		}
		for (HashMap<UnaryRule, Integer> map : uRuleBySameHead)
		{
			for (Map.Entry<UnaryRule, Integer> entry : map.entrySet())
			{
				BigDecimal b1 = BigDecimal.valueOf(entry.getValue());
				BigDecimal b2 = BigDecimal.valueOf(numOfSameHeadRule[entry.getKey().parent]);
				double score = b1.divide(b2, 15, BigDecimal.ROUND_HALF_UP).doubleValue();
				entry.getKey().scores.add(new LinkedList<Double>());
				entry.getKey().scores.get(0).add(score);
			}
		}
	}

	public class WordCount
	{
		protected String word;
		protected int count;

		public WordCount(String word)
		{
			this.word = word;
		}

		public int hashCode()
		{
			return (word == null) ? 0 : word.hashCode();

		}

		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WordCount other = (WordCount) obj;
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

}
