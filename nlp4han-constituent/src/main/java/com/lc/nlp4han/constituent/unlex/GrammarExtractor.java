package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 对二叉树得到初始语法
 * 
 * @author 王宁
 * 
 */
public class GrammarExtractor
{
	// 统计树库过程中得到
	// protected List<Tree<Annotation>> treeBank;
	protected List<AnnotationTreeNode> treeBank;
	protected NonterminalTable nonterminalTable;
	protected HashSet<String> dictionary;

	protected List<Short> preterminal;// 词性标注对应的整数

	protected HashMap<PreterminalRule, Integer>[] preRuleBySameHeadCount;// 长度与preterminal相同
	protected HashMap<BinaryRule, Integer>[] bRuleBySameHeadCount;// 数组下标表示nonterminal对应的整数
	protected HashMap<UnaryRule, Integer>[] uRuleBySameHeadCount;// 数组下标表示nonterminal对应的整数
	// 添加相同孩子为key的map
	protected HashMap<Integer, HashMap<Integer, PreterminalRule>> preRuleBySameChildren; // 外层map<childrenHashcode,内map>,内map<ruleHashcode/rule>
	protected HashMap<Integer, HashMap<Integer, BinaryRule>> bRuleBySameChildren;
	protected HashMap<Integer, HashMap<Integer, UnaryRule>> uRuleBySameChildren;
	// 相同父节点的规则放在一个map中
	protected HashMap<Short, HashMap<Integer, PreterminalRule>> preRuleBySameHead; // 内map<ruleHashcode/rule>
	protected HashMap<Short, HashMap<Integer, BinaryRule>> bRuleBySameHead;
	protected HashMap<Short, HashMap<Integer, UnaryRule>> uRuleBySameHead;
	public int[] numOfSameHeadRule;

	@SuppressWarnings("unchecked")
	public GrammarExtractor(List<AnnotationTreeNode> treeBank, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.nonterminalTable = nonterminalTable;
		dictionary = new HashSet<String>();
		preterminal = nonterminalTable.getIntValueOfPreterminalArr();
		preRuleBySameHeadCount = new HashMap[preterminal.size()];
		Rule.nonterminalTable = AnnotationTreeNode.nonterminalTable;
		for (int i = 0; i < preterminal.size(); i++)
		{
			preRuleBySameHeadCount[i] = new HashMap<PreterminalRule, Integer>();
		}
		bRuleBySameHeadCount = new HashMap[nonterminalTable.getNumSymbol()];
		for (int i = 0; i < nonterminalTable.getNumSymbol(); i++)
		{
			bRuleBySameHeadCount[i] = new HashMap<BinaryRule, Integer>();
		}
		uRuleBySameHeadCount = new HashMap[nonterminalTable.getNumSymbol()];
		for (int i = 0; i < nonterminalTable.getNumSymbol(); i++)
		{
			uRuleBySameHeadCount[i] = new HashMap<UnaryRule, Integer>();
		}
		preRuleBySameHead = new HashMap<Short, HashMap<Integer, PreterminalRule>>();
		uRuleBySameHead = new HashMap<Short, HashMap<Integer, UnaryRule>>();
		bRuleBySameHead = new HashMap<Short, HashMap<Integer, BinaryRule>>();

		preRuleBySameChildren = new HashMap<Integer, HashMap<Integer, PreterminalRule>>();
		uRuleBySameChildren = new HashMap<Integer, HashMap<Integer, UnaryRule>>();
		bRuleBySameChildren = new HashMap<Integer, HashMap<Integer, BinaryRule>>();

		numOfSameHeadRule = new int[this.nonterminalTable.getNumSymbol()];
	}

	public void extractor()
	{
		ArrayDeque<AnnotationTreeNode> queue = new ArrayDeque<AnnotationTreeNode>();
		for (AnnotationTreeNode tree : treeBank)
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
						if (!bRuleBySameHeadCount[parent].containsKey(bRule))
						{
							bRuleBySameHeadCount[parent].put(bRule, 1);
						}
						else
						{
							int count = bRuleBySameHeadCount[parent].get(bRule) + 1;
							bRuleBySameHeadCount[parent].remove(bRule);
							bRuleBySameHeadCount[parent].put(bRule, count);
						}

						if (!bRuleBySameHead.containsKey(parent))
						{
							bRuleBySameHead.put(parent, new HashMap<Integer, BinaryRule>());
						}
						bRuleBySameHead.get(parent).put(bRule.hashCode(), bRule);
						if (!bRuleBySameChildren.containsKey(bRule.chidrenHashcode()))
						{
							bRuleBySameChildren.put(bRule.chidrenHashcode(), new HashMap<Integer, BinaryRule>());
						}
						bRuleBySameChildren.get(bRule.chidrenHashcode()).put(bRule.hashCode(), bRule);
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
							if (!uRuleBySameHeadCount[parent].containsKey(uRule))
							{
								uRuleBySameHeadCount[parent].put(uRule, 1);
							}
							else
							{
								int count = uRuleBySameHeadCount[parent].get(uRule) + 1;
								uRuleBySameHeadCount[parent].remove(uRule);
								uRuleBySameHeadCount[parent].put(uRule, count);
							}

							if (!uRuleBySameHead.containsKey(parent))
							{
								uRuleBySameHead.put(parent, new HashMap<Integer, UnaryRule>());
							}
							uRuleBySameHead.get(parent).put(uRule.hashCode(), uRule);
							if (!uRuleBySameChildren.containsKey(uRule.chidrenHashcode()))
							{
								uRuleBySameChildren.put(uRule.chidrenHashcode(), new HashMap<Integer, UnaryRule>());
							}
							uRuleBySameChildren.get(uRule.chidrenHashcode()).put(uRule.hashCode(), uRule);
						}

					}
					else
					{
						String child = queue.peek().getChildren().get(0).getLabel().getWord();
						dictionary.add(child);
						PreterminalRule preRule = new PreterminalRule(parent, child);
						if (!preRuleBySameHeadCount[preterminal.indexOf(parent)].containsKey(preRule))
						{
							preRuleBySameHeadCount[preterminal.indexOf(parent)].put(preRule, 1);
						}
						else
						{
							int count = preRuleBySameHeadCount[preterminal.indexOf(parent)].get(preRule) + 1;
							preRuleBySameHeadCount[preterminal.indexOf(parent)].remove(preRule);
							preRuleBySameHeadCount[preterminal.indexOf(parent)].put(preRule, count);
						}
						if (!preRuleBySameHead.containsKey(parent))
						{
							preRuleBySameHead.put(parent, new HashMap<Integer, PreterminalRule>());
						}
						preRuleBySameHead.get(parent).put(preRule.hashCode(), preRule);
						if (!preRuleBySameChildren.containsKey(preRule.chidrenHashcode()))
						{
							preRuleBySameChildren.put(preRule.chidrenHashcode(),
									new HashMap<Integer, PreterminalRule>());
						}
						preRuleBySameChildren.get(preRule.chidrenHashcode()).put(preRule.hashCode(), preRule);
					}
				}

				numOfSameHeadRule[parent]++;
				for (AnnotationTreeNode child : queue.poll().getChildren())
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
		for (HashMap<BinaryRule, Integer> map : bRuleBySameHeadCount)
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
		for (HashMap<PreterminalRule, Integer> map : preRuleBySameHeadCount)
		{
			if (map.size() != 0)
				for (Map.Entry<PreterminalRule, Integer> entry : map.entrySet())
				{
					BigDecimal b1 = BigDecimal.valueOf(entry.getValue());
					BigDecimal b2 = BigDecimal.valueOf(numOfSameHeadRule[entry.getKey().parent]);
					double score = b1.divide(b2, 15, BigDecimal.ROUND_HALF_UP).doubleValue();
					entry.getKey().scores.add(score);
				}
		}
		for (HashMap<UnaryRule, Integer> map : uRuleBySameHeadCount)
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
}
