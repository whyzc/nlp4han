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
		GrammarExtractor gExtractor = new GrammarExtractor(treeBankPath, addParentLabel, encoding);
		if (SMCycle <= 0)
		{
			return gExtractor.getGrammar(rareWordThreshold);
		}
		else
		{
			return gExtractor.getGrammar(SMCycle, mergeRate, EMIterations, rareWordThreshold);
		}
	}

	public static void main(String[] args)
	{
		String trainFilePath = null;
		String outputFilePath = null;
		String encoding = "utf-8";
		int iterations = 50;// em算法迭代次数
		int SMCycle = 1;
		boolean addParentLabel = false;
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
			if (args[i].equals("-sm"))
			{
				SMCycle = Integer.parseInt(args[i + 1]);
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			if (args[i].equals("-plabel"))
			{
				addParentLabel = Boolean.parseBoolean(args[i + 1]);
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
			Grammar g = GrammarExtractorTool.getGrammar(SMCycle, 0.5, iterations, addParentLabel,
					Lexicon.DEFAULT_RAREWORD_THRESHOLD, trainFilePath, encoding);
			GrammarWriter.writeToFile(g, outputFilePath);
			System.out.println("提取初始文法完毕");
			long end = System.currentTimeMillis();
			long time = end - start;
			System.out.println("提取语法消耗时间：" + time);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void usage()
	{
		System.out.println(GrammarExtractorTool.class.getName() + "\n"
				+ " -train <trainFile> -out <outFile> [-sm <SMCycle>] [-encoing <encoding>] [-em <emIterations>] [-plabel <addParentLabel>]");
	}
}
