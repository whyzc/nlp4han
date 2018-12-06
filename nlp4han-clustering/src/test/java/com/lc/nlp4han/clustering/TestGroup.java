package com.lc.nlp4han.clustering;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestGroup
{
	
	@Test
	public void testGetDistance()
	{
		Group g1 = new Group();
		Group g2 = new Group();
		Distance dc = null;
		
		Double d = Group.getDistance(g1, g2, dc);
	}
	
	@Test
	public void testGetNearestGroup()
	{
		Group g = new Group();
		List<Group> grps = new ArrayList<Group>();
		Distance dc = null;
		
		Group group = Group.getNearestGroup(g, grps, dc);
	}
	
	@Test
	public void testAddMember()
	{
		Group g1 = null;
		Text t = null;
		g1.addMember(t);
	}
	
	@Test
	public void testRemoveMember()
	{
		Group g1 = null;
		Text t1 = null;
		
		g1.removeMember(t1);
	}
	
	@Test
	public void testUpdateCenter()
	{
		Group g1 = null;
		g1.updateCenter();
	}
	
	@Test
	public void testMerge()
	{
		Group g1 = null;
		Group g2 = null;
		g1.merge(g2);
	}
	
	@Test
	public void testGetCenter()
	{
		Group g1 = null;
		
		Sample center = g1.getCenter();
	}
	
	@Test
	public void testGetMembers()
	{
		Group g1 = null;
		List<Text> texts = g1.getMembers();
	}
	
	@Test
	public void testGetMembersNames()
	{
		Text t1 = new Text("a", "aa");
		Text t2 = new Text("b", "aa");
		Text t3 = new Text("c", "aa");
		List<Text> texts = new ArrayList<Text>();
		texts.add(t1);
		texts.add(t2);
		texts.add(t3);
		
		Group g = new Group(texts);
		
		List<String> result = g.getMembersNames();
		
		String[] s = new String[] {"a", "b", "c"};
		List<String> expected = Arrays.asList(s);
		
		assertEquals(expected, result);
	}
	
	@Test
	public void testClear()
	{
		Text t1 = new Text("a", "aa");
		Text t2 = new Text("b", "aa");
		Text t3 = new Text("c", "aa");
		List<Text> texts = new ArrayList<Text>();
		texts.add(t1);
		texts.add(t2);
		texts.add(t3);
		
		Group g = new Group(texts);
		
		g.clear();
		
		assertEquals(0, g.getMembersNumber());
	}
	
}
