package org.nlp4han.sentiment.nb;

import com.lc.nlp4han.ml.util.Evaluator;
import com.lc.nlp4han.ml.util.Mean;

public class SentimentAnalyzerEvaluator extends Evaluator<SentimentTextSample>{

	private SentimentAnalyzerNB analyzer;
	private Mean accuracy = new Mean();
	
	public SentimentAnalyzerEvaluator(SentimentAnalyzerNB analyzer) {
		this.analyzer = analyzer;
	}
	
	public SentimentAnalyzerEvaluator(SentimentAnalyzerNB analyzer,
			SentimentAnalyzerEvaluationMonitor...evaluateMonitors) {
		super(evaluateMonitors);
		this.analyzer=analyzer;
	}
	
	@Override
	public SentimentTextSample processSample(SentimentTextSample sample) {
		String text = sample.getText();//获取测试文件的内容
	    
	    SentimentPolarity sp = analyzer.analyze(text);
	    
	    if (sample.getCategory().equals(sp.getPolarity())) {
	    	accuracy.add(1);
	    }
	    else {
	    	accuracy.add(0);
	    }

	    return new SentimentTextSample(sp.getPolarity(), sample.getText());
	}
	
	/**
	 * 
	 * @return
	 */
	 public double getAccuracy() {
	    return accuracy.mean();
	  }

	  public long getTextCount() {
	    return accuracy.count();
	  }

	  @Override
	  public String toString() {
	    return "Accuracy: " + accuracy.mean() + "\n" +
	        "Number of documents: " + accuracy.count();
	  }

}
