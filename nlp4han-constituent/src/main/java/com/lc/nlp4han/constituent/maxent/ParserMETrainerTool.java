package com.lc.nlp4han.constituent.maxent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 句法分析训练模型运行类
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ParserMETrainerTool
{
	// TODO：不指定单个模型文件，指定模型存放的目录，或采用相同模型名不同模型后缀
	private static void usage()
	{
		System.out.println(ParserMETrainerTool.class.getName()
				+ "-data <corpusFile> -chunkmodel <chunkmodelFile> -buildmodel <buildmodelFile> -checkmodel <checkmodelFile> -type <algorithom>"
				+ "-encoding <encoding>" + "[-cutoff <num>] [-iters <num>]");
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			usage();
			return;
		}
		int cutoff = 3;
		int iters = 100;
		File corpusFile = null;
		File chunkmodelFile = null;
		File buildmodelFile = null;
		File checkmodelFile = null;
		String encoding = "UTF-8";
		String type = "MAXENT";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-chunkmodel"))
			{
				chunkmodelFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-buildmodel"))
			{
				buildmodelFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-checkmodel"))
			{
				checkmodelFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-type"))
			{
				type = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-cutoff"))
			{
				cutoff = Integer.parseInt(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-iters"))
			{
				iters = Integer.parseInt(args[i + 1]);
				i++;
			}
		}

		ParserContextGenerator contextGen = new ParserContextGeneratorConf();
		System.out.println(contextGen);
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
		params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
		params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());

		AbstractHeadGenerator headGenerator = new HeadGeneratorCollins(new HeadRuleSetPTB());
		
		Logger.getLogger("").setLevel(Level.OFF);
		
		// TODO: 可以选择首先训练词性标注模型
		System.out.println("训练组块模型...");
		ChunkerForParserME.train(corpusFile, chunkmodelFile, params, contextGen, encoding, headGenerator);
		
		System.out.println("训练构建模型...");
		BuilderAndCheckerME.trainForBuild(corpusFile, buildmodelFile, params, contextGen, encoding,
				headGenerator);
		
		System.out.println("训练检查模型...");
		BuilderAndCheckerME.trainForCheck(corpusFile, checkmodelFile, params, contextGen, encoding,
				headGenerator);
	}
}
