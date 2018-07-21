package com.lc.nlp4han.chunk.word;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisEvaluateMonitor;
import com.lc.nlp4han.ml.util.Evaluator;

/**
 * 基于词的组块分析评价器
 */
public class ChunkAnalysisWordEvaluator extends Evaluator<AbstractChunkAnalysisSample>
{

	/**
	 * 组块分析模型
	 */
	private ChunkAnalysisWordME chunkTagger;

	/**
	 * 组块分析评估
	 */
	private AbstractChunkAnalysisMeasure measure;

	/**
	 * 构造方法
	 * 
	 * @param tagger
	 *            训练得到的模型
	 */
	public ChunkAnalysisWordEvaluator(ChunkAnalysisWordME chunkTagger)
	{
		this.chunkTagger = chunkTagger;
	}

	/**
	 * 构造方法
	 * 
	 * @param tagger
	 *            训练得到的模型
	 * @param evaluateMonitors
	 *            评估的监控管理器
	 */
	public ChunkAnalysisWordEvaluator(ChunkAnalysisWordME chunkTagger, AbstractChunkAnalysisMeasure measure,
			ChunkAnalysisEvaluateMonitor... evaluateMonitors)
	{
		super(evaluateMonitors);
		this.chunkTagger = chunkTagger;
		this.measure = measure;
	}

	/**
	 * 设置评估指标的对象
	 * 
	 * @param measure
	 *            评估指标计算的对象
	 */
	public void setMeasure(AbstractChunkAnalysisMeasure measure)
	{
		this.measure = measure;
	}

	/**
	 * 得到评估的指标
	 * 
	 * @return
	 */
	public AbstractChunkAnalysisMeasure getMeasure()
	{
		return measure;
	}

	@Override
	protected AbstractChunkAnalysisSample processSample(AbstractChunkAnalysisSample sample)
	{
		String[] wordsRef = sample.getTokens();
		String[] chunkTagsRef = sample.getTags();

		String[] chunkTagsPre = chunkTagger.tag(wordsRef);

		// 将结果进行解析，用于评估
		AbstractChunkAnalysisSample prediction = new ChunkAnalysisWordSample(wordsRef, chunkTagsPre);
		prediction.setTagScheme(sample.getTagScheme());
		measure.update(wordsRef, chunkTagsRef, chunkTagsPre);
		// measure.add(sample, prediction);
		return prediction;
	}
}
