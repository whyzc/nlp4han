package com.lc.nlp4han.clustering;

import java.util.ArrayList;

public class Sample implements Cloneable
{
	private ArrayList<Double> vecter = null;

	public ArrayList<Double> getVecter()
	{
		return vecter;
	}

	public void setVecter(ArrayList<Double> vecter)
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
		result.vecter = (ArrayList<Double>)vecter.clone();
		return result;
	}
	
	

}
