package com.lc.nlp4han.clustering;

import java.util.List;

public interface FeatureGenerator
{
	public List<String> getFeatures(Text text);
}
