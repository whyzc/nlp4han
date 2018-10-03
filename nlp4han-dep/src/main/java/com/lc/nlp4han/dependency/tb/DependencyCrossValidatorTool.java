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
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 交叉验证应用
 *
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

		// 交叉验证
		DependencyParseCrossValidator crossValidator = new DependencyParseCrossValidator(params);
		DependencyParseContextGenerator contextGen;
		Configuration conf;
		SequenceValidator<String> validator;
		if (transitionType.equals("arceager"))
		{
			contextGen = new DependencyParseContextGeneratorConfArcEager();
			conf = new ConfigurationArcEager();
			validator = new DependencyParseSequenceValidatorArcEager();
		}

		else
		{
			contextGen = new DependencyParseContextGeneratorConfArcStandard();
			conf = new ConfigurationArcStandard();
			validator = new DependencyParseSequenceValidatorArcStandard();
		}
		
		LocalDateTime start = LocalDateTime.now();
		
		crossValidator.evaluate(sampleStream, folds, contextGen, conf, validator);
		
		LocalDateTime end = LocalDateTime.now();
		BigDecimal time = new BigDecimal(end.toString()).subtract(new BigDecimal(start.toString()));
		System.out.println("消耗时间:" + time);
	}

}
