package org.nlp4han.sentiment.nb;

import java.io.IOException;

import org.nlp4han.sentiment.SentimentTextSample;

import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

public class SentimentAnalyzerCrossValidation
{

	private final TrainingParameters params;

	public SentimentAnalyzerCrossValidation(TrainingParameters params)
	{
		this.params = params;
	}

	public void evaluate(ObjectStream<SentimentTextSample> sampleStream, int nFolds,
			SentimentAnalyzerContextGenerator contextGen, SentimentAnalyzerMeasure measure) throws IOException
	{
		CrossValidationPartitioner<SentimentTextSample> partitioner = new CrossValidationPartitioner<>(sampleStream,
				nFolds);
		int run = 1;
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");

			CrossValidationPartitioner.TrainingSampleStream<SentimentTextSample> trainingSampleStream = partitioner
					.next();

			ModelWrapper model = SentimentAnalyzerNB.train(trainingSampleStream, params, contextGen);

			SentimentAnalyzerEvaluator evaluator = new SentimentAnalyzerEvaluator(
					new SentimentAnalyzerNB(model, contextGen));
			evaluator.setMeasure(measure);

			evaluator.evaluate(trainingSampleStream.getTestSampleStream());

			System.out.println(measure);

			run++;

		}

		System.out.println(measure);
	}

}
