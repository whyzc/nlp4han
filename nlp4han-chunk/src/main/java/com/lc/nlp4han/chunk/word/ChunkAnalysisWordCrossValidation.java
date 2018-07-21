package com.lc.nlp4han.chunk.word;

import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 基于词的组块分析交叉验证
 */
public class ChunkAnalysisWordCrossValidation
{

	/**
	 * 训练的参数集
	 */
	private final TrainingParameters params;

	/**
	 * 构造方法
	 * 
	 * @param encoding
	 *            编码格式
	 * @param params
	 *            训练的参数
	 * @param monitor
	 *            监听器
	 */
	public ChunkAnalysisWordCrossValidation(TrainingParameters params)
	{
		this.params = params;
	}

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
	public void evaluate(ObjectStream<AbstractChunkAnalysisSample> sampleStream, int nFolds,
			ChunkAnalysisContextGenerator contextGenerator, AbstractChunkAnalysisMeasure measure,
			SequenceValidator<String> sequenceValidator) throws IOException
	{

		String scheme = ((ChunkAnalysisWordSampleStream) sampleStream).getScheme();
		CrossValidationPartitioner<AbstractChunkAnalysisSample> partitioner = new CrossValidationPartitioner<AbstractChunkAnalysisSample>(
				sampleStream, nFolds);

		int run = 1;
		// 小于折数的时候
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");

			CrossValidationPartitioner.TrainingSampleStream<AbstractChunkAnalysisSample> trainingSampleStream = partitioner
					.next();
			HashSet<String> dict = getDict(trainingSampleStream);
			trainingSampleStream.reset();
			measure.setDictionary(dict);

			ChunkAnalysisWordME me = new ChunkAnalysisWordME();
			ModelWrapper model = me.train(trainingSampleStream, params, contextGenerator);
			ChunkAnalysisWordEvaluator evaluator = new ChunkAnalysisWordEvaluator(
					new ChunkAnalysisWordME(model, sequenceValidator, contextGenerator, scheme), measure);
			evaluator.setMeasure(measure);
			// 设置测试集（在测试集上进行评价）
			evaluator.evaluate(trainingSampleStream.getTestSampleStream());

			System.out.println(measure);
			run++;
		}

	}

	/**
	 * 获取词典
	 * 
	 * @param sampleStream
	 *            样本流
	 * @return 词典
	 * @throws IOException
	 */
	private HashSet<String> getDict(ObjectStream<AbstractChunkAnalysisSample> sampleStream) throws IOException
	{
		HashSet<String> dictionary = new HashSet<String>();
		AbstractChunkAnalysisSample sample = null;
		while ((sample = sampleStream.read()) != null)
		{
			String[] words = sample.getTokens();

			for (String word : words)
				dictionary.add(word);
		}

		return dictionary;
	}
}
