package com.lc.nlp4han.clustering;

public class VectorBasedDistanceCalculator extends DistanceCalculator
{

	@Override
	public double getDistance(Text t1, Text t2)  // 欧氏距离
	{
		if (t1 == null || t2 == null || t1.getSample()==null || t2.getSample()==null)
		{
			throw new RuntimeException("参数错误！");
		}
		Sample s1 = t1.getSample();
		Sample s2 = t2.getSample();
		
		return getDistance(s1, s2);
	}
	
	public double getDistance(Sample s1, Sample s2)
	{
		double[] v1 = s1.getVecter();
		double[] v2 = s2.getVecter();
		double squareOfResult = 0;
		for (int i=0 ; i<v1.length ; i++)
		{
			squareOfResult += (v1[i]-v2[i]) * (v1[i]-v2[i]);
		}
		return Math.sqrt(squareOfResult);
	}

	@Override
	public double getDistance(Text t1, Group g1)
	{
		if (t1 == null || g1 == null || t1.getSample()==null || g1.getCenter()==null)
		{
			throw new RuntimeException("参数错误！");
		}
		Sample s2 = g1.getCenter();
		return getDistance(t1.getSample(), s2);
	}

	@Override
	public double getDistance(Group g1, Group g2)
	{
		if (g1==null || g2==null || g1.getCenter()==null || g2.getCenter()==null || g1.getMembers().size()<1 || g2.getMembers().size()<1)
		{
			throw new RuntimeException("参数错误！");
		}
		
		Sample s1 = g1.getCenter();
		Sample s2 = g2.getCenter();
		return getDistance(s1, s2);
	}
	
	/**
	 * 向量长度
	 */
	private double vectorLength(double[] v)
	{
		double sumOfSquares = 0;
		for (int i=0 ; i<v.length ; i++)
		{
			sumOfSquares += v[i]*v[i];
		}
		
		return Math.sqrt(sumOfSquares);
	}
	
	/**
	 * 向量积
	 */
	private double scalarProduct(double[] v1, double[] v2)
	{
		if (v1 == null || v2 == null || v1.length != v2.length || v1.length < 1)
		{
			throw new RuntimeException("参数错误！");
		}
		double result = 0;
		for (int i=0 ; i<v1.length ; i++)
		{
			result += v1[i]*v2[i];
		}
		
		return result;
	}
	
	/**
	 * 向量余弦
	 */
	private double cosineOfVectors(double[] v1, double[] v2)
	{
		double denominator = vectorLength(v1)*vectorLength(v2);
		if (denominator == 0)
			return 0.0;
		else
			return (scalarProduct(v1, v2) / denominator);
	}

}
