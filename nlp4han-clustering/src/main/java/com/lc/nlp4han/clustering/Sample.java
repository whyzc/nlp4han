package com.lc.nlp4han.clustering;

import java.util.Arrays;

public class Sample implements Cloneable
{
	private double[] vecter = null;

	public double[] getVecter()
	{
		return vecter;
	}

	public void setVecter(double[] vecter)
	{
		this.vecter = vecter;
	}


	@Override
	protected Sample clone()
	{
		Sample result = null;
		try
		{
			result = (Sample)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		result.vecter = vecter.clone();
		return result;
	}

	@Override
	public String toString()
	{
		return "Sample [vecter=" + Arrays.toString(vecter) + "]";
	}
	
}
