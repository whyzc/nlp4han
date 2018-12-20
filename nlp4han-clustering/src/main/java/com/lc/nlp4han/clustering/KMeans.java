package com.lc.nlp4han.clustering;

import java.util.ArrayList;
import java.util.List;

public class KMeans
{
	private static final int TIMES = 5000;
	
	public static List<Group> run(List<Text> texts, int k)
	{
		if (texts==null || k<1 || texts.size()<k)
			throw new RuntimeException("参数错误！");
 
		FeatureGenerator fg = new WordBasedZeroOneFeatureGenerator();
		if (!fg.isInitialized())
			fg.init(texts);
		
		DistanceCalculator distance = new JaccardCoefficientBasedDistanceCalculator();
		
		UpdateGroupCenter ugc = new PAMUpateGroupCenter(distance);
		
		for (int i=0 ; i<texts.size() ; i++)
		{
			Text t = texts.get(i);
			Sample s = new Sample(fg.getFeatures(t));
			t.setSample(s);
		}
		
		
		Initialization init = new RandomInitialization();
		List<Group> groups = init.initialize(texts, k);
		
		
		int iterationTimes = 0;
		for (; iterationTimes<TIMES ; iterationTimes++)
		{
			for (int j=0 ; j<k ; j++)
			{
					groups.get(j).clear();
			}
			
			for (int j=0 ; j<texts.size() ; j++)
			{
				int index = minDistanceGroup(texts.get(j), groups, distance);
				groups.get(index).addMember(texts.get(j));
			}
			
			
			if (!ugc.updateCenter(groups))
				break;
			
		}
		
		if (iterationTimes > TIMES)
			System.out.println("共迭代："+ (iterationTimes-1) + "次");
		else
			System.out.println("共迭代："+ iterationTimes + "次");
		return groups;
	}
	
	private static int minDistanceGroup(Text t, List<Group> grps, DistanceCalculator d)
	{
		double min = Double.POSITIVE_INFINITY;
		int index = -1;
		for (int i=0 ; i<grps.size() ; i++)
		{
			double distance = d.getDistance(t, grps.get(i));
			if (distance < min)
			{
				min = distance;
				index = i;
			}
			else if (distance == 0)
			{
				if (grps.get(i).getMembersNumber() < grps.get(index).getMembersNumber())
				{
					index = i;
				}
			}
		}
		return index;
	}
	
}
