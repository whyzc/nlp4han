package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.nlp4han.sentiment.SentimentAnalyzer;
import org.nlp4han.sentiment.SentimentPolarity;

import com.lc.nlp4han.ml.util.ModelWrapper;

/**
 * 情感分析器用户使用接口
 * 
 * @author lim
 *
 */
public class SentimentAnalyzerUserTool
{

	public static void main(String[] args) throws IOException
	{

		if (args.length < 1)
		{
			usage();
			return;
		}

		String strategy = "NB";// or RB
		String modelPath = "";
		String xBase = "character";
		int nGram = 2;

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-strategy"))
			{
				strategy = args[i + 1];
				i++;
			}
			if (args[i].equals("-model"))
			{
				modelPath = args[i + 1];
				i++;
			}
			if (args[i].equals("-ng"))
			{
				nGram = Integer.parseInt(args[i + 1]);
				i++;
			}
			if (args[i].equals("-flag"))
			{
				xBase = args[i + 1];
				i++;
			}
		}

		SentimentAnalyzer myAnalyzer = null;

		if (strategy.equals("NB"))
		{

			File modelFile = new File(modelPath);
			ModelWrapper model = new ModelWrapper(modelFile);

			FeatureGenerator fg =null;
			switch(xBase) {
			case "word":
				fg = new NGramWordFeatureGenerator(nGram);
				break;
			case "character":
				fg = new NGramCharFeatureGenerator(nGram);
				break;
			}
			SentimentAnalyzerContextGenerator contextGen = 
					new SentimentAnalyzerContextGeneratorConf(fg);

			myAnalyzer = new SentimentAnalyzerNB(model, contextGen);

		}
		else
		{

			TreeGenerator treeGen = new TreeGenerator();
			myAnalyzer = new SentimentAnalyzerRB(treeGen);

		}
		
		Scanner input = new Scanner(System.in);
		String text = "";
		while (true)
		{
			System.out.println("请输入待分析的文本：");
			text = input.nextLine();
			
			if (text.equals(""))
			{
				System.out.println("内容为空，请重新输入！");
			}
			else if (text.equals("exit"))
			{
				break;
			}
			else
			{
				SentimentPolarity sp = myAnalyzer.analyze(text);
				System.out.println(sp);
			}
		}
		
		input.close();
		
	}

	private static void usage()
	{
		System.out.println(SentimentAnalyzerUserTool.class.getName()
				+ "-strategy <strategyForAnalysis> -model <modelPath>  -ng <nGramFeature> -flag <wordOrCharacter>");
	}

}
