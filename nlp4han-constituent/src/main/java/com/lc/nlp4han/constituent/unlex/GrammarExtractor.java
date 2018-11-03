package com.lc.nlp4han.constituent.unlex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

/**
 * 对二叉树得到初始语法
 * 
 * @author 王宁
 * 
 */
public class GrammarExtractor
{
	// 统计树库过程中得到
	public List<AnnotationTreeNode> treeBank;
	public NonterminalTable nonterminalTable;
	public HashSet<String> dictionary;

	public List<Short> preterminal;// 词性标注对应的整数

	public HashMap<PreterminalRule, Integer>[] preRuleBySameHeadCount;// 长度与preterminal相同
	public HashMap<BinaryRule, Integer>[] bRuleBySameHeadCount;// 数组下标表示nonterminal对应的整数
	public HashMap<UnaryRule, Integer>[] uRuleBySameHeadCount;// 数组下标表示nonterminal对应的整数
	public int[] numOfSameHeadRule;
	public int rareWordThreshold;

	public GrammarExtractor(boolean addParentLabel, int rareWordThreshold, String treeBankPath, String encoding)
	{
		try
		{
			initTreeBank(addParentLabel, treeBankPath, encoding);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		init(rareWordThreshold);
	}

	public GrammarExtractor()
	{
	}

	public void initTreeBank(boolean addParentLabel, String treeBankPath, String encoding) throws IOException
	{
		List<AnnotationTreeNode> annotationTrees = new ArrayList<AnnotationTreeNode>();
		PlainTextByTreeStream stream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(treeBankPath)),
				encoding);
		String expression = stream.read();
		while (expression != "")// 用来得到树库对应的所有结构树Tree<String>
		{
			expression = expression.trim();
			if (!expression.equals(""))
			{
				TreeNode tree = BracketExpUtil.generateTree(expression);
				tree = TreeUtil.removeL2LRule(tree);
				if (addParentLabel)
					tree = TreeUtil.addParentLabel(tree);

				tree = Binarization.binarizeTree(tree);
				annotationTrees.add(AnnotationTreeNode.getInstance(tree));
			}
			expression = stream.read();
		}
		stream.close();
		this.treeBank = annotationTrees;
	}

	@SuppressWarnings("unchecked")
	public void init(int rareWordThreshold)
	{
		this.nonterminalTable = AnnotationTreeNode.nonterminalTable;
		Rule.nonterminalTable = AnnotationTreeNode.nonterminalTable;
		this.rareWordThreshold = rareWordThreshold;
		dictionary = new HashSet<String>();
		preterminal = nonterminalTable.getIntValueOfPreterminalArr();
		preRuleBySameHeadCount = new HashMap[preterminal.size()];
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
		numOfSameHeadRule = new int[this.nonterminalTable.getNumSymbol()];
	}

	private void tally()
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

	public Grammar getInitialGrammar() throws IOException
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
		Grammar intialG = new Grammar(this.treeBank, bRules, uRules, lexicon, this.nonterminalTable);
		return intialG;
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
