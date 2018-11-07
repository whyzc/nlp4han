package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;

/**
 * 获得Grammar的工具类
 * 
 * @author 王宁
 * 
 */
public class GrammarExtractorTool
{
	public static Grammar getGrammar(int SMCycle, double mergeRate, int EMIterations, boolean addParentLabel,
			int rareWordThreshold, String treeBankPath, String encoding) throws IOException
	{
		GrammarExtractor gExtractor;
		if (SMCycle <= 0)
		{
			gExtractor = new GrammarExtractor(addParentLabel, rareWordThreshold, treeBankPath, encoding);
			return gExtractor.getGrammar();
		}

		else
		{
			gExtractor = new GrammarExtractor(SMCycle, mergeRate, EMIterations, addParentLabel, rareWordThreshold,
					treeBankPath, encoding);
			return gExtractor.getGrammar(SMCycle, mergeRate, EMIterations);
		}
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
				GrammarTrainer.EMIterations = iterations;
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
			Grammar g = GrammarExtractorTool.getGrammar(1, 0.5, iterations, false, Lexicon.DEFAULT_RAREWORD_THRESHOLD,
					trainFilePath, encoding);
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
