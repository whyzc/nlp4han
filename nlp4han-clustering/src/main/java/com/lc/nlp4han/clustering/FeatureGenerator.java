package com.lc.nlp4han.clustering;

import java.util.List;

public interface FeatureGenerator
{
	public void init(List<Text> texts);
	public List<Feature> getFeatures(Text text);
	public boolean isInitialized();
}
