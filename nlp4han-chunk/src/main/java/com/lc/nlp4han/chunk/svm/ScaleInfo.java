package com.lc.nlp4han.chunk.svm;

public class ScaleInfo
{
	public int lower;
	public int upper;
	public int[] ranges;
	
	public ScaleInfo()
	{
		
	}
	
	public ScaleInfo(int lower, int upper, int[] ranges)
	{
		super();
		this.lower = lower;
		this.upper = upper;
		this.ranges = ranges;
	}
	
	public ScaleInfo(int lower, int upper)
	{
		super();
		this.lower = lower;
		this.upper = upper;
	}
	
}
