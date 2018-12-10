package com.lc.nlp4han.clustering;

public class DistanceCalculator
{
	private SampleCalculator sampleCalculator;
	
	public void setSampleCalculator(SampleCalculator sc)
	{
		this.sampleCalculator = sc;
	}
	
	public double getDistance(Text t1, Text t2)
	{
		if (t1 == null || t2 == null || t1.getSample()==null || t2.getSample()==null)
		{
			throw new RuntimeException("参数错误！");
		}
		Sample s1 = t1.getSample();
		Sample s2 = t2.getSample();
		
		return sampleCalculator.getDistance(s1, s2);
	}
	
	public double getDistance(Text t1, Group g1) 
	{
		if (t1 == null || g1 == null || t1.getSample()==null || g1.getCenter()==null)
		{
			throw new RuntimeException("参数错误！");
		}
		Sample s2 = g1.getCenter();
		return sampleCalculator.getDistance(t1.getSample(), s2);
	}
	
	public double getDistance(Group g1, Group g2)
	{
		if (g1==null || g2==null || g1.getCenter()==null || g2.getCenter()==null || g1.getMembers().size()<1 || g2.getMembers().size()<1)
		{
			throw new RuntimeException("参数错误！");
		}
		
		Sample s1 = g1.getCenter();
		Sample s2 = g2.getCenter();
		return sampleCalculator.getDistance(s1, s2);
	}
}
