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
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYP2NF;
import com.lc.nlp4han.constituent.pcfg.PCFG;

/**
 * 获得Grammar的工具类
 * 
 * @author 王宁
 * 
 */
public class GrammarExtractorTool
{
	public static Grammar generateInitialGrammar(boolean addParentLabel, int rareWordThreshold, String treeBankPath)
			throws IOException
	{
		List<AnnotationTreeNode> annotationTrees = new ArrayList<AnnotationTreeNode>();
		InputStream ins = new FileInputStream(treeBankPath);
		InputStreamReader isr = new InputStreamReader(ins, "utf-8");
		BufferedReader allSentence = new BufferedReader(isr);
		String expression = allSentence.readLine();
		while (expression != null)// 用来得到树库对应的所有结构树Tree<String>
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
			expression = allSentence.readLine();
		}
		allSentence.close();
		GrammarExtractor grammarExtractor = new GrammarExtractor(annotationTrees, AnnotationTreeNode.nonterminalTable);
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

		for (HashMap<BinaryRule, Integer> map : grammarExtractor.bRuleBySameHeadCount)
		{
			allBRule.putAll(map);

		}
		for (HashMap<PreterminalRule, Integer> map : grammarExtractor.preRuleBySameHeadCount)
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
		for (HashMap<UnaryRule, Integer> map : grammarExtractor.uRuleBySameHeadCount)
		{
			allURule.putAll(map);
		}
		bRules = new HashSet<BinaryRule>(allBRule.keySet());
		uRules = new HashSet<UnaryRule>(allURule.keySet());
		preRules = new HashSet<PreterminalRule>(allPreRule.keySet());
		HashMap<Integer, HashMap<Integer, PreterminalRule>> preRuleBySameChildren = grammarExtractor.preRuleBySameChildren; // 外层map<childrenHashcode,内map>,内map<ruleHashcode/rule>
		HashMap<Integer, HashMap<Integer, BinaryRule>> bRuleBySameChildren = grammarExtractor.bRuleBySameChildren;
		HashMap<Integer, HashMap<Integer, UnaryRule>> uRuleBySameChildren = grammarExtractor.uRuleBySameChildren;
		HashMap<Short, HashMap<Integer, PreterminalRule>> preRuleBySameHead = grammarExtractor.preRuleBySameHead; // 内map<ruleHashcode/rule>
		HashMap<Short, HashMap<Integer, BinaryRule>> bRuleBySameHead = grammarExtractor.bRuleBySameHead;
		HashMap<Short, HashMap<Integer, UnaryRule>> uRuleBySameHead = grammarExtractor.uRuleBySameHead;
		Lexicon lexicon = new Lexicon(preRules, grammarExtractor.dictionary, tagWithRareWord, rareWordCount,
				allRareWord);
		Grammar intialG = new Grammar(grammarExtractor.treeBank, bRules, uRules, lexicon, bRuleBySameChildren,
				uRuleBySameChildren, preRuleBySameChildren, bRuleBySameHead, uRuleBySameHead, preRuleBySameHead,
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
			Grammar g = GrammarExtractorTool.generateInitialGrammar(false, Lexicon.DEFAULT_RAREWORD_THRESHOLD,
					"C:\\Users\\hp\\Desktop\\ctb8-bracket-clear-utf8.txt");
			// g.split();
			GrammarWriter.writerToFile(g, "C:\\Users\\hp\\Desktop\\grammartest");
			PCFG pcfg = g.getPCFG();
			System.out.println("提取初始文法完毕");
			long end = System.currentTimeMillis();
			long time = end - start;
			System.out.println(time);
			ConstituentParserCKYP2NF parser = new ConstituentParserCKYP2NF(pcfg);
			// String sentence = "(ROOT(IP(NP(NP(NR 上海)(NR 浦东))(NP(NN 开发)(CC 与)(NN 法制)(NN
			// 建设)))(VP(VV 同步))))";
			String[] words = { "上海", "浦东", "开发", "与", "法制", "建设", "同步" };
			String[] poses = { "NR", "NR", "NN", "CC", "NN", "NN", "VV" };
//			String[] poses = { "NR^NP", "NR^NP", "NN^NP", "CC^NP", "NN^NP", "NN^NP", "VV^VP" };
			ConstituentTree ctree = parser.parse(words, poses);
			TreeNode tree = ctree.getRoot();
			TreeNode.printTree(tree, 1);
			System.out.println("1111");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
