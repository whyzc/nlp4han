package com.lc.nlp4han.clustering;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class TestSampleGenerator
{
	
	@Test
	public void testGetDistance()
	{
		Feature f1 = new Feature("a", 3.0);
		List<Feature> fs1 = new ArrayList<Feature>();
		fs1.add(f1);
		
		Feature f2 = new Feature("b", 4.0);
		List<Feature> fs2 = new ArrayList<Feature>();
		fs2.add(f2);
		
		List<Feature> fs = new ArrayList<Feature>();
		fs.add(f1);
		fs.add(f2);
		
		Sample s1 = new Sample(fs1);
		Sample s2 = new Sample(fs2);
		
		
		SampleCalculator sg = new VectorSampleGenerator();
		sg.init(fs);
		
		double distance = sg.getDistance(s1, s2);
		
		assertEquals("5.0", String.format("%.1f", distance));
	}
	
}
