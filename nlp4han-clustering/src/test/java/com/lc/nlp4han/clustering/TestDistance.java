package com.lc.nlp4han.clustering;

import org.junit.Test;

public class TestDistance
{
	@Test
	public void testGetDistance_1()
	{
		Text t1 = null;
		Text t2 = null;
		
		FeatureGenerator fg = null;
		SampleGenerator sg = null;
		
		t1.generateSample(sg, fg);
		t2.generateSample(sg, fg);
		
		Distance dis = null;
		double d = dis.getDistance(t1, t2);  //Text与Text间的距离
	}
	
	@Test
	public void testGetDistance_2()
	{
		Group g1 = null;
		Text t1 = null;
		
		FeatureGenerator fg = null;
		SampleGenerator sg = null;
		t1.generateSample(sg, fg);
		
		g1.updateCenter();
		
		Distance dis = null;
		double d = dis.getDistance(t1, g1);  //Text与Group间的距离
	}
	
	@Test
	public void testGetDistance_3()
	{
		Group g1 = null;
		Group g2 = null;
		
		Distance dis = null;
		double d = dis.getDistance(g1, g2);  //Group与Group间的距离
	}
}
