package com.lc.nlp4han.dependency.tb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 依存分析模型训练应用
 * 
 * @author 刘小峰
 *
 */
public class DependencyTrainerTool
{
	private static void usage()
	{
		System.out.println(DependencyTrainerTool.class.getName()
				+ " -data <corpusFile> -tType<transitionType> -model <modelFile> -encoding <encoding> "
				+ "[-cutoff <num>] [-iters <num>]");
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		if (args.length < 1)
		{
			usage();

			return;
		}

		int cutoff = 3;
		int iters = 100;
		File corpusFile = null;
		File modelFile = null;
		String encoding = "UTF-8";
		String tType = "arceager";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-model"))
			{
				modelFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-tType"))
			{
				tType = args[i + 1];
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

		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
		params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
		DependencyParseContextGenerator gen;
		if (tType.equals("arceager"))
			gen = new DependencyParseContextGeneratorConfArcEager();
		else
			gen = new DependencyParseContextGeneratorConfArcStandard();
		ModelWrapper model = DependencyParserTB.train(corpusFile, params, gen, encoding);
		OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
		model.serialize(modelOut);
		modelOut.close();
	}
}
