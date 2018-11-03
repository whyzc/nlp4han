package com.lc.nlp4han.constituent.unlex;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * @author 作者
 * @version 创建时间：2018年11月2日 下午8:55:23 类说明
 */
public class TreeUtilTest
{
	@Test
	public void removeL2LTest()
	{
		String expression = "(Root(frag(frag(NN 自然)(NN 语言))))";
		TreeNode tree = BracketExpUtil.generateTree(expression);
		TreeUtil.removeL2LRule(tree);
		System.out.println("\n" + TreeNode.printTree(tree, 0));
	}

	@Test
	public void addParentLabelTest()
	{
		String expression = "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))";
		TreeNode tree = BracketExpUtil.generateTree(expression);
		TreeUtil.addParentLabel(tree);
		System.out.println("\n" + TreeNode.printTree(tree, 0));
	}

	@Test
	public void removeParentLabelTest()
	{
		String expression = "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))";
		TreeNode tree = BracketExpUtil.generateTree(expression);
		TreeUtil.addParentLabel(tree);
		TreeUtil.removeParentLabel(tree);
		System.out.println("\n" + TreeNode.printTree(tree, 0));
	}
}
