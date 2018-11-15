package com.lc.nlp4han.constituent.unlex;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * @author 王宁
 */
public class TreeUtilTest
{
	@Test
	public void removeL2LTest()
	{
		String expression = "(Root(frag(frag(NN 自然)(NN 语言)(NP (NP (NN 学校)(NN 学生))))))";
		String expressionRemoveL2L = "(Root(frag(frag(NN 自然)(NN 语言)(NP (NP (NN 学校)(NN 学生))))))";
		TreeNode treeRemoveL2L = BracketExpUtil.generateTree(expressionRemoveL2L);
		TreeNode treeRemoveL2LAuto = TreeUtil.removeL2LRule(BracketExpUtil.generateTree(expression));
		Identical.identical(treeRemoveL2L, treeRemoveL2LAuto);
	}

	@Test
	public void addParentLabelTest()
	{
		String expression = "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))";
		String expressionWithP = "(ROOT(IP^ROOT(NP^IP(PN^NP i))(VP^IP(VV^VP like)(NP^VP(DT^NP the)(NN^NP book)))))";
		TreeNode treeWithP = BracketExpUtil.generateTree(expressionWithP);
		TreeNode treeWithPAuto = TreeUtil.addParentLabel(BracketExpUtil.generateTree(expression));
		Identical.identical(treeWithP, treeWithPAuto);
	}

	@Test
	public void removeParentLabelTest()
	{
		String expression = "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))";
		String expressionWithP = "(ROOT(IP^ROOT(NP^IP(PN^NP i))(VP^IP(VV^VP like)(NP^VP(DT^NP the)(NN^NP book)))))";
		TreeNode treeWithoutP = BracketExpUtil.generateTree(expression);
		TreeNode treeWithoutPAuto = TreeUtil
				.removeParentLabel(TreeUtil.addParentLabel(BracketExpUtil.generateTree(expression)));
		TreeNode treeWithoutPAuto2 = TreeUtil.removeParentLabel(BracketExpUtil.generateTree(expressionWithP));
		Identical.identical(treeWithoutP, treeWithoutPAuto);
		Identical.identical(treeWithoutP, treeWithoutPAuto2);
	}
}
