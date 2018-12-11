package com.lc.nlp4han.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sample
{
	private Map<String, Feature> features = null;

	public Sample()
	{
		
	}
	
	public Sample(List<Feature> fs)
	{
		setFeatures(fs);
	}

	public Feature getFeature(String key)
	{
		Feature f = features.get(key);
		return f;
	}
	
	/**
	 * 获取所有特征
	 * @return 所有特征
	 */
	public List<Feature> getFeatures()
	{
		List<Feature> result = new ArrayList<Feature>();
		
		Set<String> keys = features.keySet();
		for (String key : keys)
		{
			Feature f = features.get(key);
			result.add(f.clone());
		}
		
		return result;
	}

	/**
	 * 设置特征
	 * @param fs 被指定的特征列表
	 */
	public void setFeatures(List<Feature> fs)
	{
		clear();
		add(fs);
	}
	
	/**
	 * 添加特征
	 * @param fs
	 */
	public void add(List<Feature> fs)
	{
		if (features == null)
			features = new HashMap<String, Feature>();
		for (Feature f : fs)
		{
			features.put(f.getKey(), f);
		}
	}
	
	/**
	 * 添加特征
	 * @param f
	 */
	public void add(Feature f)
	{
		if (features == null)
			features = new HashMap<String, Feature>();
		
		features.put(f.getKey(), f);
		
	}
	
	public void clear()
	{
		if (features != null)
			this.features.clear();
	}

	public Set<String> getKeySet()
	{
		return features.keySet();
	}
	
	public boolean containsKey(String key) 
	{
		return features.containsKey(key);
	}

	@Override
	public Sample clone()
	{
		Sample result = new Sample();
		
		result.features = new HashMap<String, Feature>();
		Set<String> keys = features.keySet();
		
		for (String key : keys)
		{
			Feature f = features.get(key);
			result.features.put(key, f.clone());
		}
		
		return result;
	}
	

	@Override
	public String toString()
	{
		return getFeatures() + "";
	}

	public int size()
	{
		if (features != null)
			return features.size();
		else
			return 0;
	}

	
}
