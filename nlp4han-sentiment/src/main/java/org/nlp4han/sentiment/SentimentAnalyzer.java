package org.nlp4han.sentiment;


public interface SentimentAnalyzer
{

	/**
	 * 分析给定文本的情感信息
	 * 
	 * @param text
	 *            给定的文本
	 * @return 情感极性
	 */
	SentimentPolarity analyze(String text);

}
