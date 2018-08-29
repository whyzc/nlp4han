package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.dependency.DependencyParseErrorPrinter;
import com.lc.nlp4han.dependency.DependencyParseMeasure;
import com.lc.nlp4han.dependency.DependencyParser;
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
	public static void eval(File trainFile, String transitionType, TrainingParameters params, File goldFile,
			String encoding, File errorFile) throws IOException
	{
		ModelWrapper model;
		DependencyParser tagger;
		
		if (trainFile != null) 
		{
			if (transitionType.equals("arceager"))
			{
				model = DependencyParserTB.train(trainFile, params, new DependencyParseContextGeneratorConf_ArcEager(), encoding);
			}
			else
			{
				model = DependencyParserTB.train(trainFile, params, new DependencyParseContextGeneratorConf_ArcStandard(), encoding);
			}
		}
		else
		{
			InputStream inStream;
			if (transitionType.equals("arceager"))
			{
				inStream = DependencyEvalTool.class.getClassLoader()
						.getResourceAsStream("com/lc/nlp4han/dependency/arceager.model");
			}
			else
			{
				inStream = DependencyEvalTool.class.getClassLoader()
						.getResourceAsStream("com/lc/nlp4han/dependency/arc_standard3.model");
			}
			model = new ModelWrapper(inStream);
		}
		
		if (transitionType.equals("arceager"))
		{
			tagger = new DependencyParserTB(model, new DependencyParseContextGeneratorConf_ArcEager());
		}
		else
		{
			tagger = new DependencyParserTB(model, new DependencyParseContextGeneratorConf_ArcStandard());
		}
		

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
				+ " -data <trainFile> -tType<transitionType> -gold <goldFile> -encoding <encoding> [-error <errorFile>]"
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
		String tType = "arceager";
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
			else if (args[i].equals("-tType"))
			{
				tType = args[i + 1];
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
				eval(new File(trainFile), tType, params, new File(goldFile), encoding, new File(errorFile));
			}
			else
				eval(new File(trainFile), tType, params, new File(goldFile), encoding, null);
		}
		else
		{
			if (errorFile != null)
			{
				eval(null, tType, params, new File(goldFile), encoding, new File(errorFile));
			}
			else
				eval(null, tType, params, new File(goldFile), encoding, null);
		}

	}
}
