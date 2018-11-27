package com.lc.nlp4han.clustering;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class TestText
{
	@Test
	public void Initialization_1()
	{
		File f = new File("******");
		Text t = new Text(f);
	}
	
	@Test
	public void Initialization_2()
	{
		String name = "+++";
		String content = "**************";
		Text t = new Text(name, content);
	}
	
	@Test
	public void Initialization_3()
	{
		String content = "**************";
		Text t = new Text(content);
	}
	
	@Test
	public void testGetTexts()
	{
		String folderPath = "********";
		List<Text> ts = Text.getTexts(folderPath);
	}
	
	@Test
	public void testGetSample()
	{
		Text t = null;
		Sample s = t.getSample();
	}
	
	@Test
	public void testSetSample()
	{
		Sample s = null;
		Text t = null;
		
		t.setSample(s);
	}
	
}
