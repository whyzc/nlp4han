package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;

/**
 * 专门用于提取带父节点标注的语法
 * 
 * @author 王宁
 */
public class PLabelAddedGrammarExtractorTool
{
	public static Grammar getGrammar(int rareWordThreshold, String trainFilePath, String encoding)
	{
		GrammarExtractor gExtractor = new GrammarExtractor(trainFilePath, true, encoding, rareWordThreshold);
		return gExtractor.getGrammar();
	}

	public static void main(String[] args)
	{
		String trainFilePath = null;
		String outputFilePath = null;
		String encoding = "utf-8";
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
			Grammar g = PLabelAddedGrammarExtractorTool.getGrammar(Lexicon.DEFAULT_RAREWORD_THRESHOLD, trainFilePath,
					encoding);
			GrammarWriter.writeToFile(g, outputFilePath, true);
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
		System.out.println(UnLexGrammarExtractorTool.class.getName() + "\n"
				+ " -train <trainFile> -out <outFile>  [-encoing <encoding>]  ");
	}

}
