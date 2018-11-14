package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 得到语法
 * 
 * @author 王宁
 * 
 */
public class GrammarExtractor
{
	public TreeBank treeBank;
	public HashSet<String> dictionary;

	public List<Short> preterminal;// 词性标注对应的整数

	public HashMap<PreterminalRule, Integer>[] preRuleBySameHeadCount;// 长度与preterminal相同
	public HashMap<BinaryRule, Integer>[] bRuleBySameHeadCount;// 数组下标表示nonterminal对应的整数
	public HashMap<UnaryRule, Integer>[] uRuleBySameHeadCount;// 数组下标表示nonterminal对应的整数
	public int[] numOfSameHeadRule;

	public GrammarExtractor(String treeBankPath, boolean addParentLabel, String encoding)
	{
		try
		{
			treeBank = new TreeBank(treeBankPath, addParentLabel, encoding);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		initGrammarExtractor();

	}

	public GrammarExtractor(TreeBank treeBank)
	{
		this.treeBank = treeBank;
		initGrammarExtractor();
	}

	public GrammarExtractor()
	{
	}

	/**
	 * 
	 * @return 初始语法
	 */
	public Grammar getGrammar(int rareWordThreshold)
	{
		tally();
		HashSet<BinaryRule> bRules;
		HashSet<UnaryRule> uRules;
		HashSet<PreterminalRule> preRules;
		HashMap<BinaryRule, Integer> allBRule = new HashMap<BinaryRule, Integer>();
		HashMap<PreterminalRule, Integer> allPreRule = new HashMap<PreterminalRule, Integer>();
		HashMap<UnaryRule, Integer> allURule = new HashMap<UnaryRule, Integer>();

		ArrayList<Short> tagWithRareWord = new ArrayList<Short>();
		ArrayList<Integer> rareWordCount = new ArrayList<Integer>();
		int allRareWord = 0;

		for (HashMap<BinaryRule, Integer> map : this.bRuleBySameHeadCount)
		{
			allBRule.putAll(map);

		}
		for (HashMap<PreterminalRule, Integer> map : this.preRuleBySameHeadCount)
		{
			allPreRule.putAll(map);
			for (Map.Entry<PreterminalRule, Integer> entry : map.entrySet())
			{
				boolean flag = false;// 表示该规则的左部的tag是否添加到tagWithRareWord中
				if (entry.getValue() <= rareWordThreshold)
				{
					if (!flag)
					{
						tagWithRareWord.add(entry.getKey().getParent());
						rareWordCount.add(1);
						flag = true;
					}
					else
					{
						rareWordCount.set(rareWordCount.size() - 1, rareWordCount.get(rareWordCount.size() - 1) + 1);
					}
					allRareWord++;
				}
			}
		}
		for (HashMap<UnaryRule, Integer> map : this.uRuleBySameHeadCount)
		{
			allURule.putAll(map);
		}
		bRules = new HashSet<BinaryRule>(allBRule.keySet());
		uRules = new HashSet<UnaryRule>(allURule.keySet());
		preRules = new HashSet<PreterminalRule>(allPreRule.keySet());
		Lexicon lexicon = new Lexicon(preRules, this.dictionary, tagWithRareWord, rareWordCount, allRareWord);
		Grammar intialG = new Grammar(bRules, uRules, lexicon, treeBank.getNonterminalTable());
		return intialG;
	}

	public Grammar getGrammar(int SMCycle, double mergeRate, int EMIterations, int rareWordThreshold)
	{
		Grammar g = getGrammar(rareWordThreshold);
		if (SMCycle != 0)
			GrammarTrainer.train(g, treeBank, SMCycle, mergeRate, EMIterations);
		return g;
	}

	@SuppressWarnings("unchecked")
	public void initGrammarExtractor()
	{
		dictionary = new HashSet<String>();
		preterminal = treeBank.getNonterminalTable().getIntValueOfPreterminalArr();
		preRuleBySameHeadCount = new HashMap[preterminal.size()];
		for (int i = 0; i < preterminal.size(); i++)
		{
			preRuleBySameHeadCount[i] = new HashMap<PreterminalRule, Integer>();
		}
		bRuleBySameHeadCount = new HashMap[treeBank.getNonterminalTable().getNumSymbol()];
		for (int i = 0; i < treeBank.getNonterminalTable().getNumSymbol(); i++)
		{
			bRuleBySameHeadCount[i] = new HashMap<BinaryRule, Integer>();
		}
		uRuleBySameHeadCount = new HashMap[treeBank.getNonterminalTable().getNumSymbol()];
		for (int i = 0; i < treeBank.getNonterminalTable().getNumSymbol(); i++)
		{
			uRuleBySameHeadCount[i] = new HashMap<UnaryRule, Integer>();
		}
		numOfSameHeadRule = new int[this.treeBank.getNonterminalTable().getNumSymbol()];
	}

	private void tally()
	{
		ArrayDeque<AnnotationTreeNode> queue = new ArrayDeque<AnnotationTreeNode>();
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			queue.offer(tree);
			while (!queue.isEmpty())
			{
				short parent, leftChild = -1, rightChild = -1;
				parent = queue.peek().getLabel().getSymbol();

				if (queue.peek().getChildren().size() == 2)
				{
					leftChild = queue.peek().getChildren().get(0).getLabel().getSymbol();
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
				double b1 = entry.getValue();
				double b2 = numOfSameHeadRule[entry.getKey().parent];
				double score = b1 / b2;
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
					double b1 = entry.getValue();
					double b2 = numOfSameHeadRule[entry.getKey().parent];
					double score = b1 / b2;
					entry.getKey().scores.add(score);
				}
		}
		for (HashMap<UnaryRule, Integer> map : uRuleBySameHeadCount)
		{
			for (Map.Entry<UnaryRule, Integer> entry : map.entrySet())
			{
				double b1 = entry.getValue();
				double b2 = numOfSameHeadRule[entry.getKey().parent];
				double score = b1 / b2;
				entry.getKey().scores.add(new LinkedList<Double>());
				entry.getKey().scores.get(0).add(score);
			}
		}
	}
}
