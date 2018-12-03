package com.lc.nlp4han.clustering;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Group
{
	private List<Text> members;
	private Sample center;

	public Group()
	{
		members = new LinkedList<Text>();
	}
	
	public Group(Text text)
	{
		if (members == null)
			members = new LinkedList<Text>();
		members.add(text);
	}
	
	public Group(List<Text> texts)
	{
		if (members == null)
			members = new LinkedList<Text>();
		members.addAll(texts);
	}

	public static Double getDistance(Group g1, Group g2, Distance d)
	{
		return d.getDistance(g1, g2);
	}

	public static Group getNearestGroup(Group g, List<Group> grps, Distance d)
	{
		double minDistance = Double.POSITIVE_INFINITY;
		int index = -1;
		for (int i=0 ; i<grps.size() ; i++)
		{
			double distance = d.getDistance(g, grps.get(i));
			if (distance < minDistance)
			{
				minDistance = distance;
				index = i;
			}
		}
		return grps.get(index);
	}

	public boolean updateCenter()
	{
		if (members==null || members.size()<1)
			return false;
			
		ArrayList<Double> newCenter = new ArrayList<Double>();
		int n = members.get(0).getSample().getVecter().size();  // Sample中vector的维度
		for (int i=0 ; i<n ; i++)  // 初始化为0
		{
			newCenter.add(0.0);
		}
		
		for (int i=0 ; i<members.size() ; i++)  // 将members每个成员的Sample中vector的每位对应相加
		{
			List<Double> tmp = members.get(i).getSample().getVecter();
			for (int j=0 ; j<tmp.size() ; j++)
			{
				newCenter.set(j, newCenter.get(j)+tmp.get(j));
			}
		}
		
		for (int i=0 ; i<n ; i++)
		{
			newCenter.set(i, newCenter.get(i)/members.size());
		}
		
		if (newCenter.equals(center.getVecter()))
			return false;
		else
		{
			center.setVecter(newCenter);;
			return true;
		}
	}

	public void addMember(Text t)
	{
		this.members.add(t);
	}

	public void merge(Group g2)
	{
		List<Text> ms = g2.getMembers();
		members.addAll(ms);
	}

	public Boolean removeMember(Text text)
	{
		return members.remove(text);
	}

	public List<Text> getMembers()
	{
		return members;
	}

	public void setMembers(List<Text> members)
	{
		this.members = members;
	}

	public Sample getCenter()
	{
		return center;
	}

	public void setCenter(Sample center)
	{
		this.center = center;
	}
	
	public boolean containsText(Text text)
	{
		if (members != null)
			return members.contains(text);
		else
			return false;
	}

	@Override
	public String toString()
	{
		return "Group [members=" + members + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((members == null) ? 0 : members.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (members == null)
		{
			if (other.members != null)
				return false;
		}
		else if (!members.equals(other.members))
			return false;
		return true;
	}
	
	
}
