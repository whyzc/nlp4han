package com.lc.nlp4han.clustering;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestKMeans
{
	@Test
	public void testRun()
	{
		String folderPath = "********";
		List<Text> ts = Text.getTexts(folderPath, false);
		List<Group> grps = KMeans.run(ts, 10) ;
	}
	
	@Test
	public void test1()
	{
		Text t1 = new Text("a b c");
		Text t2 = new Text("a b c a b c");
		
		Text t3 = new Text("d e f");
		Text t4 = new Text("d e f d e");
		
		List<Text> texts = new ArrayList<Text>();
		texts.add(t1);
		texts.add(t2);
		texts.add(t3);
		texts.add(t4);
		
		List<Group> groups = KMeans.run(texts, 2);
		
		assertEquals(2, groups.size());
	}
}
