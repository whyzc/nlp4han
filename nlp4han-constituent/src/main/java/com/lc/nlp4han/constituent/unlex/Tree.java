package com.lc.nlp4han.constituent.unlex;

import java.util.List;
import java.util.Map;

/**
 * @author 王宁
 * @version 创建时间：2018年9月23日 下午12:57:29 表示结构树
 */
public class Tree
{
	private Annotation label;
	private List<Tree> children;

	private static Map<String, Integer> wordStatistics;
	private static Map<Integer, Map<Rule, Integer>> ruleStatistics;

	private static NonterminalTable nonterminalTable;
	public Tree()
	{
	}

	public Tree(Annotation label, List<Tree> children)
	{
		this.label = label;
		this.children = children;
	}

	
	public double[] calculateInnerScore(Grammer g) {
		return null;
	}
	
	public double[] calculateOuterScore(Grammer g) {
		return null;
	}
	
	public void removeA2ARule() {}//移除树中例如A->A的结构
	
	public void countDistribution()// 统计树信息
	{
	}

	public Tree convertToBinaryTree() {//将原来的树转化为二叉树（非严格）
		return null;
	}
	
	public Tree getOriginalTree() {//将二叉树转化为原始的树，不包含A->A的结构
		return null;
	}
	
	public Map<String, Integer> getWordStatistics()
	{
		return wordStatistics;
	}

	public Map<Integer, Map<Rule, Integer>> getRuleStatistics()
	{
		return ruleStatistics;
	}

	
	
	
	
	
	public Annotation getLabel()
	{
		return label;
	}

	public void setLabel(Annotation label)
	{
		this.label = label;
	}

	public List<Tree> getChildren()
	{
		return children;
	}

	public void setChildren(List<Tree> children)
	{
		this.children = children;
	}

}
