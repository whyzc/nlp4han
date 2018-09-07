package org.nlp4han.sentiment.nb;

import java.util.Collection;
import java.util.Map;

public interface FeatureGenerator {
	/**
	   * Extract features from given text fragments
	   *
	   * @param text    用来提取特征的文本
	   * @param extraInformation optional extra information to be used by the feature generator  更多的信息
	   * @return    返回的是一个特征的集合
	   */
	  Collection<String> extractFeatures(String text, Map<String, Object> extraInformation);
}
