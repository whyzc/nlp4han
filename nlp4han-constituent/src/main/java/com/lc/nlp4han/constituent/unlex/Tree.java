package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 王宁
 * @version 创建时间：2018年9月23日 下午12:57:29 表示结构树
 */
public class Tree<T>
{
	private T label;
	private List<Tree<T>> children;

	// private static Map<String, Integer> wordStatistics;
	// private static Map<Integer, Map<Rule, Integer>> ruleStatistics;

	private static NonterminalTable nonterminalTable;

	public Tree(T label)
	{
		this.label = label;
		this.children = new ArrayList<Tree<T>>();
	}

	public Tree(T label, List<Tree<T>> children)
	{
		this.label = label;
		this.children = children;
	}

	public double[] calculateInnerScore(Grammar g)
	{
		return null;
	}

	public double[] calculateOuterScore(Grammar g)
	{
		return null;
	}

	// public Map<String, Integer> getWordStatistics()
	// {
	// return wordStatistics;
	// }
	//
	// public Map<Integer, Map<Rule, Integer>> getRuleStatistics()
	// {
	// return ruleStatistics;
	// }

	public boolean isLeaf()
	{
		if (getChildren().isEmpty())
			return true;
		else
			return false;
	}

	public T getLabel()
	{
		return label;
	}

	public void setLabel(T label)
	{
		this.label = label;
	}

	public List<Tree<T>> getChildren()
	{
		return children;
	}

	public void setChildren(List<Tree<T>> children)
	{
		this.children = children;
	}

}
