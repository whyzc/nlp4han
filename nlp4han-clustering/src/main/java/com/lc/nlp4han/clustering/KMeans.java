package com.lc.nlp4han.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeans
{
	private static final int TIMES = 5000;
	
	public static List<Group> run(List<Text> texts, int k)
	{
		if (texts==null || k<1 || texts.size()<k)
			throw new RuntimeException("参数错误！");
 
		List<Group> groups = new ArrayList<Group>();
		
		boolean exitFlag = true;
		
		FeatureGenerator fg = new WordBasedFeatureGenerator();
		fg.init(texts);
		
		
		DistanceCalculator distance = new JaccardCoefficientBasedDistanceCalculator();
		
		for (int i=0 ; i<texts.size() ; i++)
		{
			Text t = texts.get(i);
			Sample s = new Sample(fg.getFeatures(t));
			t.setSample(s);
		}
		
		// 随机初始化
		Random random = new Random();
		List<Integer> randomValues = new ArrayList<Integer>();
		
		for (int i=0 ; i<k ; i++)
		{
			int r = random.nextInt(texts.size());
			if (randomValues.contains(r) || r<0)
			{
				i--;
				continue;
			}
			randomValues.add(r);
		}
		
		for (int i=0 ; i<k ; i++)
		{
			Group g = new Group();
			g.setCenter(texts.get(randomValues.get(i)).getSample().clone());
			groups.add(g);
		}
		
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
			
			for (int j=0 ; j<k ; j++)
			{
				if (groups.get(j).updateCenter())
					exitFlag = false;
			}
			
			if (exitFlag)
				break;
			else
				exitFlag = true;
		}
		
		if (iterationTimes > TIMES)
			System.out.println("共迭代："+ (iterationTimes-1) + "次");
		else
			System.out.println("共迭代："+ iterationTimes + "次");
		return groups;
	}
	
	private static int minDistanceGroup(Text t, List<Group> grps, DistanceCalculator d)
	{
		double min = -1;
		int index = -1;
		for (int i=0 ; i<grps.size() ; i++)
		{
			double distance = d.getDistance(t, grps.get(i));
			if (distance > min)
			{
				min = distance;
				index = i;
			}
		}
		return index;
	}
	
}
