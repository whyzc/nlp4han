package com.lc.nlp4han.clustering;

public interface Distance
{
	public double getDistance(Text t1, Text t2);
	public double getDistance(Text t1, Group g1);
	public double getDistance(Group g1, Group g2);
}
