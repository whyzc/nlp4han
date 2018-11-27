package com.lc.nlp4han.clustering;

import java.util.List;

import org.junit.Test;

public class TestKMeans
{
	@Test
	public void testRun()
	{
		String folderPath = "********";
		List<Text> ts = Text.getTexts(folderPath);
		List<Group> grps = KMeans.run(ts, 10) ;
	}
}
