package com.lc.nlp4han.clustering;

import static org.junit.Assert.assertEquals;

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
		
		sg.init(fg);  // SampleGenerator初始化
		
		Sample sample = sg.getSample(text, fg);  //通过FeatureGenerator生成Text的Sample
	}
	
	@Test
	public void testGetSample_2()
	{
		Text text = null;
		
		SampleGenerator sg = null;
		
		FeatureGenerator fg = null;  //FeatureGenerator已初始化过
		
		List<Feature> features = fg.getFeatures(text);
		
		sg.init(fg);  // SampleGenerator初始化
		
		Sample sample = sg.getSample(features, fg);  //对Feature列表生成Sample
	}
	
	@Test
	public void testGetDistance()
	{
		Sample s1 = new Sample();
		Sample s2 = new Sample();
		
		s1.setVecter(new double[] {3.0, 0});
		s2.setVecter(new double[] {0, 4.0});
		
		SampleGenerator sg = new VectorSampleGenerator();
		
		double distance = sg.getDistance(s1, s2);
		
		assertEquals("5.0", String.format("%.1f", distance));
	}
	
}
