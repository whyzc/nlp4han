package org.nlp4han.sentiment.nb;

import com.lc.nlp4han.ml.util.EvaluationMonitor;

public class SentimentAnalyzerEvaluationMonitor implements
		EvaluationMonitor<SentimentTextSample>{

	@Override
	public void correctlyClassified(SentimentTextSample reference, SentimentTextSample prediction) {
		
	}

	@Override
	public void missclassified(SentimentTextSample reference, SentimentTextSample prediction) {
		
	}

}
