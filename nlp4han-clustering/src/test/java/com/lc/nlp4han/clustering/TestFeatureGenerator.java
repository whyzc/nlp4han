package com.lc.nlp4han.clustering;

import java.util.List;

import org.junit.Test;

public class TestFeatureGenerator
{
	@Test
	public void testInit()
	{
		List<Text> texts = null;  //所有的文本
		FeatureGenerator fg = null;
		
		fg.init(texts);  //对FeatureGenerator进行初始化
	}
	
	@Test
	public void testGetFeatures()
	{
		Text t1 = null;
		FeatureGenerator fg = null;
		List<Feature> features = null;
		
		if (fg.isInitialized())		//FeatureGenerator已经初始化
			features = fg.getFeatures(t1);
	}
}
