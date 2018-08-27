package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

public class CrossValidatorTool
{

	/**
	 * n折交叉验证评估
	 * 
	 * @param sampleStream
	 *            样本流
	 * @param nFolds
	 *            折数
	 * @param contextGenerator
	 *            上下文
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<ConstituentTree> sampleStream, int nFolds, ConstituentMeasure measure)
			throws IOException
	{

		CrossValidationPartitioner<ConstituentTree> partitioner = new CrossValidationPartitioner<ConstituentTree>(
				sampleStream, nFolds);
		int run = 1;
		// 小于折数的时候
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");

			CrossValidationPartitioner.TrainingSampleStream<ConstituentTree> trainingSampleStream = partitioner.next();
			ConstituentParserCKYOfP2NFImproving cky = GetckyByStream.getckyFromStream(trainingSampleStream);
			CKYParserEvaluator evaluator = new CKYParserEvaluator(cky);
			evaluator.setMeasure(measure);
			// 设置测试集（在测试集上进行评价）
			evaluator.evaluate(trainingSampleStream.getTestSampleStream());
			System.out.println(measure);
			run++;
		}
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			// usage();
			return;
		}

		int folds = 10;
		File corpusFile = null;
		String encoding = null;
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
			else if (args[i].equals("-folds"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
		}
		ObjectStream<String> treeStream = new PlainTextByTreeStream(new FileInputStreamFactory(corpusFile), encoding);
		ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(treeStream);
		CrossValidatorTool run = new CrossValidatorTool();
		ConstituentMeasure measure = new ConstituentMeasure();
		run.evaluate(sampleStream, folds, measure);
	}
}
