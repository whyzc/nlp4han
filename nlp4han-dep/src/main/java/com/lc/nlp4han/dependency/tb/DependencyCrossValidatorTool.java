package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.perceptron.SimplePerceptronSequenceTrainer;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * @author 王宁
 * @version 创建时间：2018年7月25日 上午12:26:19 交叉验证的工具类
 */
public class DependencyCrossValidatorTool
{

	private static void usage()
	{
		System.out.println(DependencyCrossValidatorTool.class.getName()
				+ " -data <corpusFile> -tType<transitionType> -encoding <encoding> [-folds <nFolds>] "
				+ "[-cutoff <num>] [-iters <num>]");
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
		// File corpusFile = new
		// File("C:\\Users\\hp\\Desktop\\UD_English-EWT\\en_ewt-ud-train.conllu");
		// File corpusFile2 = new
		// File("C:\\Users\\hp\\Desktop\\UD_Chinese-GSD\\zh_gsd-ud-train.conllu");
		String encoding = "UTF-8";
		String transitionType = "arceager";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-tType"))
			{
				transitionType = args[i + 1];
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
			else if (args[i].equals("-folds"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
		}

		TrainingParameters params = TrainingParameters.defaultParams();

		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
		params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
		// 修改训练模型为EventModelSequenceTrainer
		// params.put(TrainingParameters.ALGORITHM_PARAM,
		// SimplePerceptronSequenceTrainer.PERCEPTRON_SEQUENCE_VALUE);

		ObjectStream<String> linesStream = new PlainTextBySpaceLineStream(
				new MarkableFileInputStreamFactory(corpusFile), encoding);

		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(linesStream, sampleParser);

		// ObjectStream<String> linesStream2 = new PlainTextBySpaceLineStream(
		// new MarkableFileInputStreamFactory(corpusFile2), encoding);
		//
		// DependencySampleParser sampleParser2 = new DependencySampleParserCoNLL();
		// ObjectStream<DependencySample> sampleStream2 = new
		// DependencySampleStream(linesStream2, sampleParser2);

		// 交叉验证
		DependencyParseCrossValidator crossValidator = new DependencyParseCrossValidator(params);
		DependencyParseContextGenerator contextGen;
		if (transitionType.equals("arceager"))
			contextGen = new DependencyParseContextGeneratorConf_ArcEager();
		else
			contextGen = new DependencyParseContextGeneratorConf_ArcStandard();
		LocalDateTime start = LocalDateTime.now();
		crossValidator.evaluate(sampleStream, folds, contextGen,transitionType);
		LocalDateTime end = LocalDateTime.now();
		BigDecimal time = new BigDecimal(end.toString()).subtract(new BigDecimal(start.toString()));
		System.out.println("消耗时间:" + time);

		// // 交叉验证
		// DependencyParseCrossValidator crossValidator2 = new
		// DependencyParseCrossValidator(params);
		// DependencyParseContextGenerator contextGen2 = new
		// DependencyParseContextGeneratorConf_ArcEager();
		// LocalDateTime start2 = LocalDateTime.now();
		// crossValidator2.evaluate(sampleStream2, folds, contextGen2);
		// System.out.println("开始时间:" + start2);
	}

}
