package org.nlp4han.sentiment.nb;

import java.io.IOException;

import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.Mean;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

public class SentimentAnalyzerCrossValidation {
	
	private final TrainingParameters params;
	private Mean textAccuracy = new Mean();
	
	public SentimentAnalyzerCrossValidation(TrainingParameters params) {
		this.params = params;
	}
	
	public void evaluate(ObjectStream<SentimentTextSample> sampleStream,int nFolds,
			SentimentAnalyzerContextGenerator contextGen) throws IOException {
		CrossValidationPartitioner<SentimentTextSample> partitioner = new CrossValidationPartitioner<>(
		        sampleStream, nFolds);
		
		while (partitioner.hasNext()) {

		      CrossValidationPartitioner.TrainingSampleStream<SentimentTextSample> trainingSampleStream = partitioner
		          .next();

		      ModelWrapper model = SentimentAnalyzerNB.train(
		          trainingSampleStream, params,contextGen);

		      SentimentAnalyzerEvaluator evaluator = new SentimentAnalyzerEvaluator(
		          new SentimentAnalyzerNB(model,contextGen));

		      evaluator.evaluate(trainingSampleStream.getTestSampleStream());

		      textAccuracy.add(evaluator.getAccuracy(),
		          evaluator.getTextCount());

		    }
	}
	
	public double getTextAccuracy() {
		return textAccuracy.mean();
	}
	
	public long getTextCount() {
		return textAccuracy.count();
	}

}
