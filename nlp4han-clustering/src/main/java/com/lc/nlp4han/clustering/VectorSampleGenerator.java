package com.lc.nlp4han.clustering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class VectorSampleGenerator implements SampleGenerator
{
	private Map<String, Integer> vectorInfo = null;
	
	@Override
	public Sample getSample(Text text, FeatureGenerator fg)
	{
		List<Feature> features = fg.getFeatures(text);
		Sample s = getSample(features, fg);
		return s;
	}

	@Override
	public Sample getSample(List<Feature> features, FeatureGenerator fg)
	{
		if (vectorInfo == null)
			init(fg);
		double[] vector = new double[vectorInfo.size()];
		for (int i=0 ; i<vector.length ; i++)  // 对vector初始化
		{
			vector[i] = 0;
		}
		
		for (int i=0 ; i<features.size() ; i++)
		{
			int index = vectorInfo.get(features.get(i).getKey());
			vector[index] = features.get(i).getValue();
		}
		
		Sample s = new Sample();
		s.setVecter(vector);
		
		return s;
	}
	
	@Override
	public void init(FeatureGenerator fg)
	{
		if (vectorInfo == null)
			vectorInfo = new HashMap<String, Integer>();
		Map<String, Count> wordInfo = fg.getWordInfo();
		Set<Entry<String, Count>> es = wordInfo.entrySet();
		for (Entry<String, Count> e : es)
		{
			vectorInfo.put(e.getKey(), vectorInfo.size());
		}
	}

	@Override
	public double getDistance(Sample s1, Sample s2)
	{
		double[] v1 = s1.getVecter();
		double[] v2 = s2.getVecter();
		double squareOfResult = 0;
		for (int i=0 ; i<v1.length ; i++)
		{
			squareOfResult += (v1[i]-v2[i]) * (v1[i]-v2[i]);
		}
		return Math.sqrt(squareOfResult);
	}
}
