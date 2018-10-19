package com.lc.nlp4han.constituent.maxent;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadRuleSetCTB;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 英文句法分析交叉验证运行类
 * 
 * @author 王馨苇
 *
 */
public class ParserMECVTool
{

	private final String languageCode;

	private final TrainingParameters params;

	private ParserEvaluateMonitor[] listeners;

	public ParserMECVTool(String languageCode, TrainingParameters trainParam, ParserEvaluateMonitor... listeners)
	{
		this.languageCode = languageCode;
		this.params = trainParam;
		this.listeners = listeners;
	}

	/**
	 * 十折交叉验证进行评估
	 * 
	 * @param samples
	 *            样本流
	 * @param nFolds
	 *            几折交叉验证
	 * @param contextGen
	 *            特征生成
	 * @param headGen
	 *            生成头结点
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<ConstituentTreeSample> samples, int nFolds, ParserContextGenerator contextGen,
			AbstractHeadGenerator headGen) throws IOException
	{
		CrossValidationPartitioner<ConstituentTreeSample> partitioner = new CrossValidationPartitioner<ConstituentTreeSample>(
				samples, nFolds);
		int run = 1;
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");

			CrossValidationPartitioner.TrainingSampleStream<ConstituentTreeSample> trainingSampleStream = partitioner
					.next();

			// 训练组块器
			System.out.println("训练组块模型...");
			ModelWrapper chunkmodel = ChunkerForParserME.train(languageCode, trainingSampleStream, params, contextGen);

			// 训练构建器
			System.out.println("训练构建模型...");
			trainingSampleStream.reset();
			ModelWrapper buildmodel = BuilderAndCheckerME.trainForBuild(languageCode, trainingSampleStream, params,
					contextGen);

			// 训练检测器
			System.out.println("训练检查模型...");
			trainingSampleStream.reset();
			ModelWrapper checkmodel = BuilderAndCheckerME.trainForCheck(languageCode, trainingSampleStream, params,
					contextGen);

			ChunkerForParserME chunktagger = new ChunkerForParserME(chunkmodel, contextGen, headGen);
			BuilderAndCheckerME buildandchecktagger = new BuilderAndCheckerME(buildmodel, checkmodel, contextGen,
					headGen);

			ParserMEEvaluator evaluator = new ParserMEEvaluator(chunktagger, buildandchecktagger, headGen, listeners);

			ConstituentMeasure measure = new ConstituentMeasure();
			evaluator.setMeasure(measure);

			System.out.println("评价模型...");
			evaluator.evaluate(trainingSampleStream.getTestSampleStream());

			System.out.println(measure);
			run++;
		}
	}

	private static void usage()
	{
		System.out.println(ParserMECVTool.class.getName()
				+ " -data <corpusFile> -encoding <encoding> -type <algorithm> "
				+ "[-cutoff <num>] [-iters <num>] [-folds <nFolds>] ");
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
		int folds = 10;
		File corpusFile = null;
		String encoding = "UTF-8";
		String type = "MAXENT";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-type"))
			{
				type = args[i + 1];
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
			else if (args[i].equals("-folds"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
		}

		Logger.getLogger("").setLevel(Level.OFF);

		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
		params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
		params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());

		ParserContextGenerator contextGen = new ParserContextGeneratorConf();
		AbstractHeadGenerator headGen = new HeadGeneratorCollins(new HeadRuleSetCTB());
		System.out.println(contextGen);

		ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(corpusFile), encoding);
		ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(lineStream, headGen);

		ParserMECVTool run = new ParserMECVTool("zh", params);
		run.evaluate(sampleStream, folds, contextGen, headGen);
	}
}
