package com.lc.nlp4han.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Evaluation
{
	private Map<String, Integer> categoryInfo = new HashMap<String, Integer>();  //类别标记，如文件名以"c1-xxx"，"c2-xxx"等命名，则"c1"，"c2"为类别标记
	private List<Set<Text>> expectedResult = new ArrayList<Set<Text>>();  //期望的聚类结果。根据不同的类别标记，对所有文本进行分类的结果，作为评价的标准
	private int textNumber;  //文本数量
	private List<Group> actualResult = null;  //实际的聚类结果。
	
	public Evaluation()
	{
		
	}
	
	public Evaluation(List<Text> texts)
	{
		init(texts);
	}
	
	public Evaluation(List<Text> texts, List<Group> actualResult2)
	{
		// TODO Auto-generated constructor stub
	}

	public void init(List<Text> texts)
	{
		
	}

	public List<Set<Text>> getExpectedResult()
	{
		return this.expectedResult;
	}
	
	public double purity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public double RIValue()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public double FValue()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setActualResult(List<Group> groups)
	{
		this.actualResult = groups;
	}
	
}
