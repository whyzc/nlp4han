package com.lc.nlp4han.clustering;

import org.junit.Test;

public class TestDistance
{
	@Test
	public void testGetDistance_1()
	{
		Text t1 = null;
		Text t2 = null;
		
		SampleGenerator sg = null;
		
		t1.generateSample(sg);
		t2.generateSample(sg);
		
		Distance dis = null;
		double d = dis.getDistance(t1, t2);
	}
	
	@Test
	public void testGetDistance_2()
	{
		Group g1 = null;
		Text t1 = null;
		
		SampleGenerator sg = null;
		t1.generateSample(sg);
		
		g1.updateCenter();
		
		Distance dis = null;
		double d = dis.getDistance(t1, g1);
	}
	
	@Test
	public void testGetDistance_3()
	{
		Group g1 = null;
		Group g2 = null;
		
		Distance dis = null;
		double d = dis.getDistance(g1, g2);
	}
}
