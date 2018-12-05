package com.lc.nlp4han.clustering;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TestEvaluation
{
	@Test
	public void testConstructor_1()
	{
		List<Text> texts = null;
		List<Group> actualResult = null;
		
		Evaluation eval = new Evaluation(texts);
	}
	@Test
	public void testConstructor_2()
	{
		List<Text> texts = null;
		List<Group> actualResult = null;
		
		Evaluation eval = new Evaluation(texts, actualResult);
	}
	@Test
	public void testConstructor_3()
	{
		List<Text> texts = null;
		List<Group> actualResult = null;
		
		Evaluation eval = new Evaluation(texts, actualResult);
	}
	
	@Test
	public void testGetExpectedResult()
	{
		Text t1 = new Text("1-1", "aa");  // name="1-1", content="aa"
		Text t2 = new Text("1-2", "aa");
		Text t3 = new Text("2-1", "aa");
		Text t4 = new Text("2-2", "aa");
		Text t5 = new Text("3-1", "aa");
		List<Text> texts = new ArrayList<Text>();
		texts.add(t1);
		texts.add(t2);
		texts.add(t3);
		texts.add(t4);
		texts.add(t5);
		
		Evaluation e = new Evaluation();
		e.init(texts);  // 初始化，根据Text中的name将文本分类成期望的结果
		List<Set<Text>> actual = e.getExpectedResult();
		
		
		Set<Text> expected1 = new HashSet<Text>();
		expected1.add(t1);
		expected1.add(t2);
		Set<Text> expected2 = new HashSet<Text>();
		expected2.add(t3);
		expected2.add(t4);
		Set<Text> expected3 = new HashSet<Text>();
		expected3.add(t5);
		
		List<Set<Text>> expected = new ArrayList<Set<Text>>();
		expected.add(expected1);
		expected.add(expected2);
		expected.add(expected3);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSetActualResult()
	{
		List<Text> texts = null;
		
		List<Group> grps = KMeans.run(texts, 2);
		
		Evaluation eval = new Evaluation(texts);
		
		eval.setActualResult(grps);  // 设置被评价的聚类结果
	}
	
	@Test
	public void testPurity()
	{
		List<Text> texts = null;
		List<Group> actualResult = null;
		
		Evaluation eval = new Evaluation(texts, actualResult);
		double purity = eval.purity();
	}
	
	@Test
	public void testRIValue()
	{
		List<Text> texts = null;
		List<Group> actualResult = null;
		
		Evaluation eval = new Evaluation(texts, actualResult);
		double RI = eval.RIValue();
	}
	
	@Test
	public void testFValue()
	{
		List<Text> texts = null;
		List<Group> actualResult = null;
		
		Evaluation eval = new Evaluation(texts, actualResult);
		double F = eval.FValue();
	}
	
}
