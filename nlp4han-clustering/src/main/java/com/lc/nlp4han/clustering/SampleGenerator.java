package com.lc.nlp4han.clustering;

import java.util.List;

public interface SampleGenerator
{
	public Sample getSample(Text text, FeatureGenerator fg);
	public Sample getSample(List<Feature> features);
}
