package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
	protected List<HashMap<PreterminalRule, Integer>> preRuleBySameHead;// 长度与preterminal相同

	protected List<HashMap<BinaryRule, Integer>> bRuleBySameHead;// 数组下标表示nonterminal对应的整数
	protected List<HashMap<UnaryRule, Integer>> uRuleBySameHead;// 数组下标表示nonterminal对应的整数

	public int[] numOfSameHeadRule;

	public GrammarExtractor(List<Tree<Annotation>> treeBank, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.nonterminalTable = nonterminalTable;
		preterminal = nonterminalTable.getIntValueOfPreterminalArr();
		preRuleBySameHead = new ArrayList<HashMap<PreterminalRule, Integer>>(preterminal.size());
		bRuleBySameHead = new ArrayList<HashMap<BinaryRule, Integer>>(this.nonterminalTable.getNumSymbol());
		uRuleBySameHead = new ArrayList<HashMap<UnaryRule, Integer>>();
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
						if (!bRuleBySameHead.get(parent).containsKey(bRule))
						{
							bRuleBySameHead.get(parent).put(bRule, 1);
						}
						else
						{
							bRuleBySameHead.get(parent).put(bRule, bRuleBySameHead.get(parent).get(bRule) + 1);
						}
					}
				}
				else// if (queue.peek().getChildren().size() == 1)
				{
					if (queue.peek().getChildren().get(0).getLabel().getWord() == null)
					{
						leftChild = queue.peek().getChildren().get(0).getLabel().getSymbol();
						if (leftChild != -1)
						{
							UnaryRule uRule = new UnaryRule(parent, leftChild);
							if (!uRuleBySameHead.get(parent).containsKey(uRule))
							{
								uRuleBySameHead.get(parent).put(uRule, 1);
							}
							else
							{
								uRuleBySameHead.get(parent).put(uRule, uRuleBySameHead.get(parent).get(uRule) + 1);
							}
						}

					}
					else
					{
						String child = queue.peek().getChildren().get(0).getLabel().getWord();
						PreterminalRule preRule = new PreterminalRule(parent, child);
						if (!preRuleBySameHead.get(parent).containsKey(preRule))
						{
							preRuleBySameHead.get(parent).put(preRule, 1);
						}
						else
						{
							preRuleBySameHead.get(parent).put(preRule, preRuleBySameHead.get(parent).get(preRule) + 1);
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

	//计算初始文法的概率
	public static void calculateRuleScores()
	{
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
