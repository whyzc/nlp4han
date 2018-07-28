package org.nlp4han.sentiment.nb;

import java.util.Map;

public interface SentimentAnalyzer
{
	/**
	 * 分析给定文本的情感极性
	 *
	 * @param text
	 *            待分析的文本
	 * @param 附加信息
	 * @return 情感极性
	 */
	SentimentPolarity analyze(String text, Map<String, Object> extraInformation);

	/**
	 * 分析给定文本的情感信息
	 * 
	 * @param text
	 *            给定的文本
	 * @return 情感极性
	 */
	SentimentPolarity analyze(String text);

}
