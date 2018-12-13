package com.lc.nlp4han.clustering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestKMeans
{	
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
		
		Group g1 = new Group();
		g1.addMember(t1);
		g1.addMember(t2);
		
		Group g2 = new Group();
		g2.addMember(t3);
		g2.addMember(t4);
		
		assertTrue(groups.contains(g1));
		assertTrue(groups.contains(g2));
	}
	
	@Test
	public void test2()
	{
		Text t1 = new Text("我喜欢他");
		Text t2 = new Text("他喜欢我");
		
		Text t3 = new Text("学习语言");
		Text t4 = new Text("学习语言");
		
		List<Text> texts = new ArrayList<Text>();
		texts.add(t1);
		texts.add(t2);
		texts.add(t3);
		texts.add(t4);
		
		List<Group> groups = KMeans.run(texts, 2);
		
		assertEquals(2, groups.size());
		
		Group g1 = new Group();
		g1.addMember(t1);
		g1.addMember(t2);
		
		Group g2 = new Group();
		g2.addMember(t3);
		g2.addMember(t4);
		
		assertTrue(groups.contains(g1));
		assertTrue(groups.contains(g2));
	}
}
