package org.nlp4han.sentiment.nb;

import org.nlp4han.sentiment.SentimentAnalyzer;
import org.nlp4han.sentiment.SentimentAnalyzerEvaluationMonitor;
import org.nlp4han.sentiment.SentimentPolarity;
import org.nlp4han.sentiment.SentimentTextSample;

import com.lc.nlp4han.ml.util.Evaluator;

public class SentimentAnalyzerEvaluator extends Evaluator<SentimentTextSample>{

	private SentimentAnalyzer analyzer;
	private SentimentAnalyzerMeasure measure = new SentimentAnalyzerMeasure();
	
	public SentimentAnalyzerEvaluator(SentimentAnalyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	public SentimentAnalyzerEvaluator(SentimentAnalyzer analyzer,
			SentimentAnalyzerEvaluationMonitor...evaluateMonitors) {
		super(evaluateMonitors);
		this.analyzer=analyzer;
	}
	
	@Override
	public SentimentTextSample processSample(SentimentTextSample sample) {
		String text = sample.getText();//获取测试文件的内容
	    
	    SentimentPolarity sp = analyzer.analyze(text);
	    
	    measure.updateScores(sample.getCategory(), sp.getPolarity());

	    return new SentimentTextSample(sp.getPolarity(), sample.getText());
	}
	
	public void setMeasure(SentimentAnalyzerMeasure measure) {
		this.measure = measure;
	}
	
	public SentimentAnalyzerMeasure getMeasure() {
		return measure;
	}

}
