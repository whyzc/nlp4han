package com.lc.nlp4han.clustering;

import java.util.List;

public interface SampleGenerator
{
	public void init(List<Text> texts, FeatureGenerator fg);
	public Sample getSample(Text text, FeatureGenerator fg);
}
