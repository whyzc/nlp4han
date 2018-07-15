package org.nlp4han.sentiment.nb;

import java.util.Map;

public interface SentimentAnalyzerContextGenerator {
	
	String[] getContext(String text, Map<String, Object> extraInformation);
}
