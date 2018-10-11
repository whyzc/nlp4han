package com.lc.nlp4han.chunk.svm;

import java.io.IOException;
import java.util.HashSet;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.svm.libsvm.svm_model;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSampleStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ObjectStream;

public class ChunkAnalysisSVMCrossValidation
{

	/**
	 * 训练的参数集
	 */
	private final String[] arg;

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
	public ChunkAnalysisSVMCrossValidation(String[] arg)
	{
		this.arg = arg;
	}

	/**
	 * n折交叉验证评估
	 * 
	 * @param sampleStream
	 *            样本流
	 * @param nFolds
	 *            折数
	 * @param contextGenerator
	 *            上下文生成器
	 * @param measure
	 *            组块分析评价器
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<AbstractChunkAnalysisSample> sampleStream, int nFolds,
			ChunkAnalysisContextGenerator contextGenerator, AbstractChunkAnalysisMeasure measure) throws IOException
	{
		CrossValidationPartitioner<AbstractChunkAnalysisSample> partitioner = new CrossValidationPartitioner<AbstractChunkAnalysisSample>(
				sampleStream, nFolds);

		int run = 1;
		// 小于折数的时候
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");
			String label = ((ChunkAnalysisWordPosSampleStream) sampleStream).getScheme();
			CrossValidationPartitioner.TrainingSampleStream<AbstractChunkAnalysisSample> trainingSampleStream = partitioner
					.next();
			HashSet<String> dict = getDict(trainingSampleStream);
			trainingSampleStream.reset();
			measure.setDictionary(dict);

			ChunkAnalysisSVMME me = new ChunkAnalysisSVMME();
			long start = System.currentTimeMillis();
			svm_model model = me.train(trainingSampleStream, arg, contextGenerator);
			System.out.println("训练时间： " + (System.currentTimeMillis()-start));
			
			ChunkAnalysisSVMME svmme = new ChunkAnalysisSVMME(contextGenerator, model, label);
			svmme.init(me.getFeatureStructure(), me.getClassificationResults(), me.getFeatures());
			
			ChunkAnalysisSVMEvaluator evaluator = new ChunkAnalysisSVMEvaluator(svmme, measure);

			evaluator.setMeasure(measure);

			start = System.currentTimeMillis();
			evaluator.evaluate(trainingSampleStream.getTestSampleStream());
			System.out.println("标注时间： " + (System.currentTimeMillis()-start));

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
