package com.lc.nlp4han.clustering;

import java.util.List;

import org.junit.Test;


public class TestSampleGenerator
{
	@Test
	public void testGetSample_1()
	{
		Text text = null;
		
		SampleGenerator sg = null;
		
		FeatureGenerator fg = null;  //FeatureGenerator已初始化过
		
		Sample sample = sg.getSample(text, fg);  //通过FeatureGenerator生成Text的Sample
	}
	
	@Test
	public void testGetSample_2()
	{
		Text text = null;
		
		SampleGenerator sg = null;
		
		FeatureGenerator fg = null;  //FeatureGenerator已初始化过
		
		List<Feature> features = fg.getFeatures(text);
		
		Sample sample = sg.getSample(features);  //对Feature列表生成Sample
	}
	
}
