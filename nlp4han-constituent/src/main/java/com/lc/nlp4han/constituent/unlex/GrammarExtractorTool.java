package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * @author 王宁
 * @version 创建时间：2018年9月28日 上午8:48:34 获得Grammar的工具类
 */
public class GrammarExtractorTool
{
	public static Grammar generateInitialGrammar(boolean addParentLabel, int rareWordThreshold, String treeBankPath)
			throws IOException
	{
		List<Tree<String>> trees = new ArrayList<Tree<String>>();
		InputStream ins = new FileInputStream(treeBankPath);
		InputStreamReader isr = new InputStreamReader(ins, "gbk");
		BufferedReader allSentence = new BufferedReader(isr);
		String expression = allSentence.readLine();

		while (expression != null)// 用来得到树库对应的所有结构树Tree<String>
		{
			expression = expression.trim();
			if (!expression.equals(""))
			{
				TreeNode tempTree = BracketExpUtil.generateTreeNotDeleteBracket(expression);
				Tree<String> tree = TreeUtil.getStringTree(tempTree);
				tree = TreeUtil.removeL2LRule(tree);
				if (addParentLabel)
					tree = TreeUtil.addParentLabel(tree);
				tree = TreeUtil.binarizeTree(tree);
				trees.add(tree);
			}
			expression = allSentence.readLine();
		}
		allSentence.close();

		// 将Tree<String> 转化为Tree<Annotation>
		NonterminalTable convertTable = new NonterminalTable();
		List<Tree<Annotation>> annotationTrees = TreeUtil.getAnnotationTrees(trees, convertTable);
		trees = null;

		GrammarExtractor grammarExtractor = new GrammarExtractor(annotationTrees, convertTable);
		grammarExtractor.extractor();

		HashSet<BinaryRule> bRules;
		HashSet<UnaryRule> uRules;
		HashSet<PreterminalRule> preRules;
		HashMap<BinaryRule, Integer> allBRule = new HashMap<BinaryRule, Integer>();
		HashMap<PreterminalRule, Integer> allPreRule = new HashMap<PreterminalRule, Integer>();
		HashMap<UnaryRule, Integer> allURule = new HashMap<UnaryRule, Integer>();

		ArrayList<Short> tagWithRareWord = new ArrayList<Short>();
		ArrayList<Integer> rareWordCount = new ArrayList<Integer>();
		int allRareWord = 0;

		for (HashMap<BinaryRule, Integer> map : grammarExtractor.bRuleBySameHead)
		{
			allBRule.putAll(map);

		}
		for (HashMap<PreterminalRule, Integer> map : grammarExtractor.preRuleBySameHead)
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
		for (HashMap<UnaryRule, Integer> map : grammarExtractor.uRuleBySameHead)
		{
			allURule.putAll(map);
		}
		bRules = new HashSet<BinaryRule>(allBRule.keySet());
		uRules = new HashSet<UnaryRule>(allURule.keySet());
		preRules = new HashSet<PreterminalRule>(allPreRule.keySet());
		Lexicon lexicon = new Lexicon(preRules,grammarExtractor.dictionary ,tagWithRareWord, rareWordCount, allRareWord);
		Grammar intialG = new Grammar(grammarExtractor.treeBank, bRules, uRules, lexicon,
				grammarExtractor.nonterminalTable);
		return intialG;
	}

	public static void main(String[] args)
	{

		try
		{
			long start = System.currentTimeMillis();
			System.out.println("开始提取初始文法");
			System.out.println(start);
			Grammar g = GrammarExtractorTool.generateInitialGrammar(true, Lexicon.DEFAULT_RAREWORD_THRESHOLD,
					"C:\\Users\\hp\\Desktop\\test(2).txt");
			g.writeGrammarToTxt();
			System.out.println("提取初始文法完毕");
			long end = System.currentTimeMillis();
			long time = end - start;
			System.out.println(time);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
