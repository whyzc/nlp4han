package com.lc.nlp4han.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 基于PAM算法的更新簇中心
 * @author 杨智超
 *
 */
public class PAMUpateGroupCenter implements UpdateGroupCenter
{
	private DistanceCalculator dc = null;
	private List<List<Double>> doubleDimensionalArray = null;  // 二维数组，记录每两个Text间的距离
	private Map<Text, Integer> allTexts = null;  //  记录所有的Text，每个Text对应的vaule为doubleDimensionalArray中的索引号
	
	public PAMUpateGroupCenter()
	{
	}
	
	public PAMUpateGroupCenter(DistanceCalculator dc)
	{
		this.dc = dc;
	}
	
	public void setDistanceCalculator(DistanceCalculator dc)
	{
		this.dc = dc;
	}
	
	@Override
	public boolean updateCenter(List<Group> grps)
	{
		if (allTexts == null || doubleDimensionalArray == null)
			init(grps);
		boolean result = false;
		List<Text> centers = getCenters(grps);
		double oldLossVaule = getTotalLossVaule(centers);
		
		for (int i=0 ; i<centers.size() ; i++)
		{
			List<Text> ts = grps.get(i).getMembers();
			
			Text tmpText = centers.get(i);
			
			for (int j=0 ; j<ts.size() ; j++)
			{
				if (ts.get(j) != tmpText)
				{
					Text t = centers.set(i, ts.get(j));
					double tempLossVaule = getTotalLossVaule(centers);
					
					if (tempLossVaule >= oldLossVaule)  // 恢复
						centers.set(i, t);
					else
						result = true;
						
				}
			}
		}
		
		for (int i=0 ; i<centers.size() ; i++)
		{
			grps.get(i).setCenter(centers.get(i).getSample().clone());
		}
		
		return result;
	}
	
	private double getTotalLossVaule(List<Text> centers)
	{
		double lossVaule = 0.0;
		Set<Text> texts = allTexts.keySet();
		for (Text t : texts)
		{
			lossVaule += getMinLossValue(t, centers);
		}
		return lossVaule;
	}
	
	private double getMinLossValue(Text text, List<Text> texts)
	{
		int index1 = allTexts.get(text);
		double min = Double.POSITIVE_INFINITY;
		
		for (Text t : texts)
		{
			int index2 = allTexts.get(t);
			if (index1 == index2)
				return 0;
			else if (index1 > index2)
			{
				List<Double> temp = doubleDimensionalArray.get(index1);
				if (temp.get(index2) < min)
					min = temp.get(index2);
			}
			else
			{
				List<Double> temp = doubleDimensionalArray.get(index2);
				if (temp.get(index1) < min)
					min = temp.get(index1);
			}
		}
		return min;
	}

	private List<Text> getCenters(List<Group> grps)
	{
		List<Text> result = new ArrayList<Text>();
		
		Set<Text> ts = allTexts.keySet();
		
		for (Group g : grps)
		{
			Sample tempSample = g.getCenter();
			
			for (Text t : ts)
			{
				if (tempSample.equals(t.getSample()))
				{
					result.add(t);
					break;
				}
			}
		}
		return result;
	}

	private void init(List<Group> grps)
	{
		doubleDimensionalArray = new ArrayList<List<Double>>();
		allTexts = new HashMap<Text, Integer>(); 
		for (int i=0 ; i<grps.size() ; i++)  // allTexts中存入所有Text
		{
			List<Text> tempTexts = grps.get(i).getMembers();
			for (int j=0 ; j<tempTexts.size() ; j++)
			{
				allTexts.put(tempTexts.get(j), allTexts.size());
			}
		}
		
		for (int i=0 ; i<allTexts.size() ; i++)  // 初始化doubleDimensionalArray
		{
			List<Double> tmp = new ArrayList<Double>();
			for (int j=0 ; j<=i ; j++)
			{
				tmp.add(0.0);
			}
			doubleDimensionalArray.add(tmp);
		}
		
		Set<Entry<Text, Integer>> texts = allTexts.entrySet();
		Iterator<Entry<Text, Integer>> it1 = texts.iterator();
		
		while (it1.hasNext())
		{
			Entry<Text, Integer> e1 = it1.next();
			
			Iterator<Entry<Text, Integer>> it2 = texts.iterator();
			while (it2.hasNext())
			{
				Entry<Text, Integer> e2 = it2.next();
				
				if (e1.getValue()-e2.getValue() > 0)
				{
					double distance = dc.getDistance(e1.getKey(), e2.getKey());
					doubleDimensionalArray.get(e1.getValue()).set(e2.getValue(), distance);
				}
			}
		}
	}

}
