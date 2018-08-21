package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.dependency.DependencyParseErrorPrinter;
import com.lc.nlp4han.dependency.DependencyParseMeasure;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 依存解析评价应用
 * 
 * @author 刘小峰
 *
 */
public class DependencyEvalTool
{
	public static void eval(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile)
			throws IOException
	{
		DependencyParseContextGenerator gen = new DependencyParseContextGeneratorConf_ArcEager();
		ModelWrapper model;
		if (trainFile != null)
			model = DependencyParser_ArcEager.train(trainFile, params, gen, encoding);
		else
		{
			InputStream inStream = DependencyEvalTool.class.getClassLoader()
					.getResourceAsStream("com/lc/nlp4han/dependency/tb_cpostag2.model");
			model = new ModelWrapper(inStream);
		}

		DependencyParser_ArcEager tagger = new DependencyParser_ArcEager(model, gen);

		DependencyParseMeasure measure = new DependencyParseMeasure();
		DependencyParseEvaluator evaluator = null;
		DependencyParseErrorPrinter errorPrinter = null;
		if (errorFile != null)
		{
			errorPrinter = new DependencyParseErrorPrinter(new FileOutputStream(errorFile));
			evaluator = new DependencyParseEvaluator(tagger, errorPrinter);
		}
		else
		{
			evaluator = new DependencyParseEvaluator(tagger, errorPrinter);
		}
		evaluator.setMeasure(measure);

		ObjectStream<String> linesStream = new PlainTextBySpaceLineStream(new MarkableFileInputStreamFactory(goldFile),
				encoding);
		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(linesStream, sampleParser);
		evaluator.evaluate(sampleStream);
		
		System.out.println(evaluator.getMeasure().getData());
		System.out.println(evaluator.getMeasure());
	}

	private static void usage()
	{
		System.out.println(DependencyEvalTool.class.getName()
				+ " -data <trainFile> -gold <goldFile> -encoding <encoding> [-error <errorFile>]"
				+ " [-cutoff <num>] [-iters <num>]");
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			usage();

			return;
		}

		String trainFile = null;
		String goldFile = null;
		String errorFile = null;
		String encoding = "UTF-8";
		int cutoff = 3;
		int iters = 100;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				trainFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-gold"))
			{
				goldFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-error"))
			{
				errorFile = args[i + 1];
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

		if (trainFile != null)
		{
			if (errorFile != null)
			{
				eval(new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
			}
			else
				eval(new File(trainFile), params, new File(goldFile), encoding, null);
		}
		else
		{
			if (errorFile != null)
			{
				eval(null, params, new File(goldFile), encoding, new File(errorFile));
			}
			else
				eval(null, params, new File(goldFile), encoding, null);
		}

	}
}
