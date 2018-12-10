package com.lc.nlp4han.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sample implements Cloneable
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
	 * @return
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

	public void setFeatures(List<Feature> fs)
	{
		clear();
		add(fs);
	}
	
	public void add(List<Feature> fs)
	{
		if (features == null)
			features = new HashMap<String, Feature>();
		for (Feature f : fs)
		{
			features.put(f.getKey(), f);
		}
	}
	
	public void clear()
	{
		if (features != null)
			this.features.clear();
	}


	@Override
	protected Sample clone()
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

	
}
