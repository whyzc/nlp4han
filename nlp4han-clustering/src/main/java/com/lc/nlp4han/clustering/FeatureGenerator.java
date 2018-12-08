package com.lc.nlp4han.clustering;

import java.util.List;
import java.util.Map;

public interface FeatureGenerator
{
	public void init(List<Text> texts);
	public List<Feature> getFeatures(Text text);
	public boolean isInitialized();
	public Map<String, Count> getWordInfo();
}
