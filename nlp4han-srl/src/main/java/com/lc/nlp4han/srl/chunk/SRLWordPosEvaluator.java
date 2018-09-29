package com.lc.nlp4han.srl.chunk;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisEvaluateMonitor;
import com.lc.nlp4han.ml.util.Evaluator;

/**
 * 基于词和词性的组块分析评价器
 */
public class SRLWordPosEvaluator extends Evaluator<AbstractChunkAnalysisSample>
{

	/**
	 * 组块分析模型
	 */
	private SRLWordPosME chunkTagger;

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
	public SRLWordPosEvaluator(SRLWordPosME chunkTagger)
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
	public SRLWordPosEvaluator(SRLWordPosME chunkTagger, AbstractChunkAnalysisMeasure measure,
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
		SRLWordPosSample wordAndPOSSample = (SRLWordPosSample) sample;

		String[] wordsRef = wordAndPOSSample.getTokens();
		String[] chunkTagsRef = wordAndPOSSample.getTags();

		Object[] objectPosesRef = wordAndPOSSample.getAditionalContext();
		String[] posesRef = new String[objectPosesRef.length];
		for (int i = 0; i < posesRef.length; i++)
			posesRef[i] = (String) objectPosesRef[i];

		String[] chunkTagsPre = chunkTagger.tag(wordsRef, posesRef);

		// 将结果进行解析，用于评估
		SRLWordPosSample prediction = new SRLWordPosSample(wordsRef, posesRef, chunkTagsPre);
		prediction.setTagScheme(sample.getTagScheme());

		measure.update(wordsRef, chunkTagsRef, chunkTagsPre);
		// measure.add(wordAndPOSSample, prediction);
		return prediction;
	}
}
