package org.nlp4han.sentiment.nb;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;


public class SentimentAnalyzerContextGeneratorConf implements SentimentAnalyzerContextGenerator{
	private FeatureGenerator featureGenerator;
	
	public SentimentAnalyzerContextGeneratorConf(FeatureGenerator featureGenerator) {
		this.featureGenerator= featureGenerator;
	}	
	

	@Override
	public String[] getContext(String text, Map<String, Object> extraInformation) {
		Collection<String> context = new LinkedList<>();
		Collection<String> extractedFeatures = 
				featureGenerator.extractFeatures(text, extraInformation);
		context.addAll(extractedFeatures);
		return context.toArray(new String[context.size()]);
		
	}

}
