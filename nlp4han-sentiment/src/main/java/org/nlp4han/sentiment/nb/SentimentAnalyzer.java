package org.nlp4han.sentiment.nb;

import java.util.Map;

public interface SentimentAnalyzer {
	/**
	   * Analyze the given text provided as tokens along with
	   * the provided extra information
	   *
	   * @param text the tokens of text to categorize
	   * @param extraInformation extra information
	   * @return per category probabilities
	   */
	  SentimentPolarity analyze(String text, Map<String, Object> extraInformation);

	  /**
	   * Analyze the given text, provided in separate tokens.
	   * @param text the tokens of text to categorize
	   * @return per category probabilities
	   */
	  SentimentPolarity analyze(String text);



	  /**
	   * get the number of categories
	   *
	   * @return the no. of categories
	   */
	  int getNumberOfCategories();



}
