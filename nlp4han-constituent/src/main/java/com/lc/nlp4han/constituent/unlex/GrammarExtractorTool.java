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
import com.lc.nlp4han.constituent.CTBExtractorTool;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYP2NF;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.constituent.pcfg.TreeNodeUtil;

/**
 * 获得Grammar的工具类
 * 
 * @author 王宁
 * 
 */
public class GrammarExtractorTool
{
	public static Grammar generateInitialGrammar(boolean addParentLabel, int rareWordThreshold, String treeBankPath,
			String encoding) throws IOException
	{
		List<AnnotationTreeNode> annotationTrees = new ArrayList<AnnotationTreeNode>();
		InputStream ins = new FileInputStream(treeBankPath);
		InputStreamReader isr = new InputStreamReader(ins, encoding);
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
		Lexicon lexicon = new Lexicon(preRules, grammarExtractor.dictionary, tagWithRareWord, rareWordCount,
				allRareWord);
		Grammar intialG = new Grammar(grammarExtractor.treeBank, bRules, uRules, lexicon,
				grammarExtractor.nonterminalTable);
		return intialG;
	}

	public static void main(String[] args)
	{
		String trainFilePath = null;
		String outputFilePath = null;
		String encoding = "utf-8";
		int iterations = 50;// em算法迭代次数
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-train"))
			{
				trainFilePath = args[i + 1];
				i++;
			}
			if (args[i].equals("-out"))
			{
				outputFilePath = args[i + 1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			if (args[i].equals("-em"))
			{
				iterations = Integer.parseInt(args[i + 1]);
				Grammar.iterations = iterations;
				i++;
			}
		}
		if (trainFilePath == null || outputFilePath == null)
		{
			usage();
			System.exit(0);
		}

		try
		{
			long start = System.currentTimeMillis();
			System.out.println("开始提取初始文法");
			Grammar g = GrammarExtractorTool.generateInitialGrammar(false, Lexicon.DEFAULT_RAREWORD_THRESHOLD,
					trainFilePath, encoding);
			g.split();
			GrammarWriter.writerToFile(g, outputFilePath);
			System.out.println("提取初始文法完毕");
			long end = System.currentTimeMillis();
			long time = end - start;
			System.out.println("提取语法消耗时间：" + time);

			// long start1 = System.currentTimeMillis();
			// PCFG pcfg = g.getPCFG();
			// ConstituentParserCKYP2NF parser = new ConstituentParserCKYP2NF(pcfg);
			// long end1 = System.currentTimeMillis();
			// System.out.println("语法转化消耗时间：" + (end1 - start1));
			// String sentence = "(ROOT(IP(NP(NP(NR 上海)(NR 浦东))(NP(NN 开发)(CC 与)(NN 法制)(NN
			// 建设)))(VP(VV 同步))))";
			// String sentence = "(ROOT(FRAG(P 据)(NR 新华社)(NR 伦敦)(NT ２月)(NT １３日)(NN 电)))";
			// TreeNode root = BracketExpUtil.generateTree(sentence);
			// TreeUtil.addParentLabel(root);
			// ArrayList<String> allWords = new ArrayList<>();
			// ArrayList<String> allPoses = new ArrayList<>();
			// TreeNodeUtil.getWordsAndPOSFromTree(allWords, allPoses, root);
			// // String[] words = { "上海", "浦东", "开发", "与", "法制", "建设", "同步" };
			// // // String[] poses = { "NR", "NR", "NN", "CC", "NN", "NN", "VV" };
			// // String[] poses = { "NR^NP", "NR^NP", "NN^NP", "CC^NP", "NN^NP", "NN^NP",
			// // "VV^VP" };
			// String words[] = allWords.toArray(new String[allWords.size()]);
			// String poses[] = allPoses.toArray(new String[allPoses.size()]);
			// long strat2 = System.currentTimeMillis();
			// ConstituentTree ctree = parser.parse(words, poses);
			// TreeNode tree = TreeUtil.removeParentLabel(ctree.getRoot());
			// long end2 = System.currentTimeMillis();
			// System.out.println("解析时间：" + (end2 - strat2));
			// System.out.println(TreeNode.printTree(tree, 0));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void usage()
	{
		System.out.println(GrammarExtractorTool.class.getName() + "\n"
				+ " -train <trainFile> -out <outFile> [-encoing <encoding>] [-em <emIterations>]");
	}
}
