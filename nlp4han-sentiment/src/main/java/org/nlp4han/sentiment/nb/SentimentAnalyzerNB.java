package org.nlp4han.sentiment.nb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.nlp4han.sentiment.SentimentAnalyzer;
import org.nlp4han.sentiment.SentimentPolarity;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainingParameters;

public class SentimentAnalyzerNB implements SentimentAnalyzer
{

	private ModelWrapper model;
	private SentimentAnalyzerContextGenerator contextGen;
	private Map<String, Object> extraInformation;

	public SentimentAnalyzerNB()
	{

	}

	public SentimentAnalyzerNB(ModelWrapper model, SentimentAnalyzerContextGenerator contextGen)
	{
		this.model = model;
		this.contextGen = contextGen;
	}

	public SentimentAnalyzerNB(ModelWrapper model, SentimentAnalyzerContextGenerator contextGen,
			Map<String, Object> extraInfo)
	{
		this.model = model;
		this.contextGen = contextGen;
		this.extraInformation = extraInfo;
	}

	public static ModelWrapper train(ObjectStream<SentimentTextSample> sampleStream, TrainingParameters params,
			SentimentAnalyzerContextGenerator contextGen) throws IOException
	{

		ClassificationModel nbModel = null;
		Map<String, String> manifestInfoEntries = new HashMap<>();

		ObjectStream<Event> es = new SentimentAnalyzerEventStream(sampleStream, contextGen);
		EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);

		nbModel = trainer.train(es);

		return new ModelWrapper(nbModel);
	}

	@Override
	public SentimentPolarity analyze(String text)
	{
		double[] analyzeResult = model.getModel().eval(contextGen.getContext(text, extraInformation));
		return new SentimentPolarity(getBestResult(analyzeResult));
	}

	private String getBestResult(double[] outcome)
	{
		return model.getModel().getBestOutcome(outcome);
	}

}
