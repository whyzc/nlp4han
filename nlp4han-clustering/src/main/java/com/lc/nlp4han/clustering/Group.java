package com.lc.nlp4han.clustering;

import java.util.List;

public class Group
{
	List<Sample> samples;

	public Group()
	{
		
	}
	
	public Group(Sample sample)
	{
		// TODO Auto-generated constructor stub
	}
	
	public Group(List<Sample> samples)
	{
		// TODO Auto-generated constructor stub
	}

	public static Double getDistance(Group g1, Group g2, Distance dc)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static Group getNearestGroup(Group g, List<Group> grps, Distance dc)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void updateCenter()
	{
		// TODO Auto-generated method stub
		
	}

	public void addMember()
	{
		// TODO Auto-generated method stub
		
	}

	public void merge(Group g2)
	{
		// TODO Auto-generated method stub
		
	}

	public Text getCenter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Text> getMembers()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public Text removeMember(Text text)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
