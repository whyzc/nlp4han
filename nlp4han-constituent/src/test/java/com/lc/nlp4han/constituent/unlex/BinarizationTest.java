package com.lc.nlp4han.constituent.unlex;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.unlex.Binarization;

public class BinarizationTest
{
	@Test
	public void binaryTest()
	{
		String expression = "(ROOT(FRAG(NN 新华社)(NR 上海)(NT 二月)(NT 十日)(NN 电)))";
		TreeNode tree = BracketExpUtil.generateTree(expression);
		tree = Binarization.binarizeTree(tree);
		System.out.println(TreeNode.printTree(tree, 0));
	}
}
