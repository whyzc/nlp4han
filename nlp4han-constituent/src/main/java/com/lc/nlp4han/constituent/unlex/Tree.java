package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示结构树
 * @author 王宁
 */
public class Tree<T>
{
	private T label;
	private List<Tree<T>> children;

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

	public boolean isPreterminal()
	{
		if (getChildren().size() == 1 && getChildren().get(0).isLeaf())
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

	public Tree<T> getParent(Tree<T> treeRoot)
	{
		if (treeRoot.isLeaf())
			return null;
		for (Tree<T> child : treeRoot.getChildren())
		{
			if (this == child)
			{
				return treeRoot;
			}
		}
		for (Tree<T> subTree : treeRoot.getChildren())
			getParent(subTree);
		return null;
	}
}
