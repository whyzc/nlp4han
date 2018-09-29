package com.lc.nlp4han.chunk.svm;

import java.io.IOException;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisEvaluateMonitor;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosEvaluator;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosME;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSample;
import com.lc.nlp4han.ml.util.EvaluationMonitor;
import com.lc.nlp4han.ml.util.Evaluator;

public class ChunkAnalysisSVMEvaluator extends Evaluator<AbstractChunkAnalysisSample>
{
	private ChunkAnalysisSVMME chunkTagger;

	private AbstractChunkAnalysisMeasure measure;

	public ChunkAnalysisSVMEvaluator(ChunkAnalysisSVMME chunkTagger, AbstractChunkAnalysisMeasure measure,
			EvaluationMonitor<AbstractChunkAnalysisSample>... aListeners)
	{
		super(aListeners);
		this.chunkTagger = chunkTagger;
		this.measure = measure;
	}

	public ChunkAnalysisSVMEvaluator(ChunkAnalysisSVMME chunkTagger)
	{
		this.chunkTagger = chunkTagger;
	}

	public void setChunkTagger(ChunkAnalysisSVMME chunkTagger)
	{
		this.chunkTagger = chunkTagger;
	}

	public void setMeasure(AbstractChunkAnalysisMeasure measure)
	{
		this.measure = measure;
	}

	@Override
	protected AbstractChunkAnalysisSample processSample(AbstractChunkAnalysisSample sample)
	{

		ChunkAnalysisWordPosSample wordAndPOSSample = (ChunkAnalysisWordPosSample) sample;

		String[] wordsRef = wordAndPOSSample.getTokens();
		String[] chunkTagsRef = wordAndPOSSample.getTags();

		Object[] objectPosesRef = wordAndPOSSample.getAditionalContext();
		String[] posesRef = new String[objectPosesRef.length];
		for (int i = 0; i < posesRef.length; i++)
			posesRef[i] = (String) objectPosesRef[i];

		String[] chunkTagsPre = null;
		try
		{
			chunkTagsPre = chunkTagger.tag(wordsRef, posesRef);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// 将结果进行解析，用于评估
		ChunkAnalysisWordPosSample prediction = new ChunkAnalysisWordPosSample(wordsRef, posesRef, chunkTagsPre);
		prediction.setTagScheme(sample.getTagScheme());

		measure.update(wordsRef, chunkTagsRef, chunkTagsPre);
		// measure.add(wordAndPOSSample, prediction);
		return prediction;
	}

	public AbstractChunkAnalysisMeasure getMeasure()
	{
		return measure;
	}

}
