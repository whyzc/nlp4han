package com.lc.nlp4han.chunk;

import com.lc.nlp4han.ml.util.EvaluationMonitor;

/**
 * 组块分析监测类
 * 
 * @param <T>
 */
public class ChunkAnalysisEvaluateMonitor implements EvaluationMonitor<AbstractChunkAnalysisSample>
{

	/**
	 * 预测正确
	 * 
	 * @param reference
	 *            参考的结果
	 * @param prediction
	 *            预测的结果
	 */
	@Override
	public void correctlyClassified(AbstractChunkAnalysisSample reference, AbstractChunkAnalysisSample prediction)
	{

	}

	/**
	 * 预测出错，打印错误信息
	 * 
	 * @param reference
	 *            参考的结果
	 * @param prediction
	 *            预测的结果
	 */
	@Override
	public void missclassified(AbstractChunkAnalysisSample reference, AbstractChunkAnalysisSample prediction)
	{

	}
}
