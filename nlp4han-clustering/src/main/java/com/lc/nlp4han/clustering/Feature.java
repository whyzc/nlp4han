package com.lc.nlp4han.clustering;

public class Feature
{
	private String key;
	private double value;
	
	public Feature()
	{
		
	}
	
	public Feature(String key, double value)
	{
		this.key = key;
		this.value = value;
	}
	
	public String getKey()
	{
		return key;
	}
	public void setKey(String key)
	{
		this.key = key;
	}
	public double getValue()
	{
		return value;
	}
	public void setValue(double value)
	{
		this.value = value;
	}
	
}
