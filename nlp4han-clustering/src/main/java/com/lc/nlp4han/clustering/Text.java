package com.lc.nlp4han.clustering;

import java.io.File;
import java.util.List;

public class Text
{
	private String name;
	private String content;
	private Sample sample;
	
	public Text()
	{
		
	}
	
	public Text(String name, String content)
	{
		// TODO Auto-generated constructor stub
	}

	public Text(File f)
	{
		// TODO Auto-generated constructor stub
	}

	public Text(String content)
	{
		// TODO Auto-generated constructor stub
	}

	public static List<Text> getTexts(String folderPath)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public void generateSample(SampleGenerator sg)
	{
		
	}

	public Sample getSample()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setSample(Sample s)
	{
		// TODO Auto-generated method stub
		
	}

}
