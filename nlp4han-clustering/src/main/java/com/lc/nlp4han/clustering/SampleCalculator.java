package com.lc.nlp4han.clustering;

import java.util.List;

public interface SampleCalculator
{
	public void init(FeatureGenerator fg);
	public void init(List<Feature> fg);
	public double getDistance(Sample s1, Sample s2);
}
