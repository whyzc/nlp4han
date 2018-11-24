package com.lc.nlp4han.constituent.unlex;

import static org.junit.Assert.assertEquals;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * @author 王宁
 */
public class Identical
{
	public static boolean identical(TreeNode tree1, TreeNode tree2)
	{
		if (tree1 == null && tree2 == null)
			return true;
		if (tree1 == tree2)
			return true;
		assertEquals("两棵树不相同", tree1.getNodeName(), tree2.getNodeName());
		if (tree1.getChildrenNum() != tree2.getChildrenNum())
			return false;
		boolean flag = true;
		if (!tree1.isLeaf())
		{
			for (int i = 0; i < tree1.getChildrenNum(); i++)
			{
				flag = flag && identical(tree1.getChild(i), tree2.getChild(i));
			}
		}
		return flag;
	}
}
