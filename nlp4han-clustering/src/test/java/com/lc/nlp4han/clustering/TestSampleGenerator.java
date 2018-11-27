package com.lc.nlp4han.clustering;

import java.util.List;

import org.junit.Test;


public class TestSampleGenerator
{
	
	@Test
	public void testInit()
	{
		List<Text> ts = null;
		
		FeatureGenerator fg = null;
		
		SampleGenerator sg = null;
		
		sg.init(ts, fg);
	}
	
	@Test
	public void testGetSample()
	{
		List<Text> ts = null;
		
		SampleGenerator sg = null;
		
		FeatureGenerator fg = null;
		
		sg.init(ts, fg);	//对FeatureGenerator初始化
		
		Text text = null;
		
		Sample sample = sg.getSample(text, fg);
	}
	
	
}
