package com.lc.nlp4han.constituent.maxent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 英文句法分析评估运行类
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ParserMEEvalTool
{

	private static void usage()
	{
		System.out.println(ParserMEEvalTool.class.getName()
				+ "-data <corpusFile> -type <algorithom> -postagger <postagger>"
				+ "-gold <goldFile> -error <errorFile> -encoding <encoding>" + " [-cutoff <num>] [-iters <num>]");
	}

	public static void eval(String postaggertype, File trainFile, TrainingParameters params, File goldFile,
			String encoding, File errorFile) throws IOException
	{
		long start = System.currentTimeMillis();
		ParserContextGenerator contextGen = new ParserContextGeneratorConf();
		System.out.println(contextGen);
		AbstractHeadGenerator aghw = new HeadGeneratorCollins(new HeadRuleSetPTB());
//		ModelWrapper posmodel = new ModelWrapper(new File("data\\model\\pos\\en-pos-maxent.bin"));
		ModelWrapper chunkmodel = ChunkerForParserME.train(trainFile, params, contextGen, encoding, aghw);
		ModelWrapper buildmodel = BuilderAndCheckerME.trainForBuild(trainFile, params, contextGen,
				encoding, aghw);
		ModelWrapper checkmodel = BuilderAndCheckerME.trainForCheck(trainFile, params, contextGen,
				encoding, aghw);
		System.out.println("训练时间： " + (System.currentTimeMillis() - start));
//		POSTaggerForParser<HeadTreeNode> postagger;
//		if (postaggertype.equals("china"))
//		{
//			postagger = new POSTaggerForParserMEChinese(posmodel);
//		}
//		else
//		{
//			postagger = new POSTaggerForParserMEEnglish(posmodel);
//		}
		ChunkerForParserME chunktagger = new ChunkerForParserME(chunkmodel, contextGen, aghw);
		BuilderAndCheckerME buildandchecktagger = new BuilderAndCheckerME(buildmodel,
				checkmodel, contextGen, aghw);

		ConstituentMeasure measure = new ConstituentMeasure();
		ParserMEEvaluator evaluator = null;
		ParserErrorPrinter printer = null;
		if (errorFile != null)
		{
			System.out.println("Print error to file " + errorFile);
			printer = new ParserErrorPrinter(new FileOutputStream(errorFile));
//			evaluator = new ParserEvaluatorForByStep(postagger, chunktagger, buildandchecktagger, aghw,
//					printer);
			evaluator = new ParserMEEvaluator(chunktagger, buildandchecktagger, aghw,
					printer);
		}
		else
		{
//			evaluator = new ParserEvaluatorForByStep(postagger, chunktagger, buildandchecktagger, aghw);
			evaluator = new ParserMEEvaluator(chunktagger, buildandchecktagger, aghw);
		}
		evaluator.setMeasure(measure);
		ObjectStream<String> linesStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
		ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(linesStream,
				aghw);
		evaluator.evaluate(sampleStream);
		ConstituentMeasure measureRes = evaluator.getMeasure();
		System.out.println("标注时间： " + (System.currentTimeMillis() - start));
		System.out.println(measureRes);
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
		String type = "MAXENT";
		String postagger = "english";
		int cutoff = 3;
		int iters = 100;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				trainFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-type"))
			{
				type = args[i + 1];
				i++;
			}
			else if (args[i].equals("-postagger"))
			{
				postagger = args[i + 1];
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
		params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());
		if (errorFile != null)
		{
			eval(postagger, new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
		}
		else
			eval(postagger, new File(trainFile), params, new File(goldFile), encoding, null);
	}
}
