package com.lc.nlp4han.clustering;

import java.util.List;

public class Group
{
	private List<Text> members;
	private Text center;

	public Group()
	{
		
	}
	
	public Group(Text text)
	{
		// TODO Auto-generated constructor stub
	}
	
	public Group(List<Text> texts)
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

	public Text removeMember(Text text)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Text> getMembers()
	{
		return members;
	}

	public void setMembers(List<Text> members)
	{
		this.members = members;
	}

	public Text getCenter()
	{
		return center;
	}

	public void setCenter(Text center)
	{
		this.center = center;
	}
	
}
