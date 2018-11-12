package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.unlex.AnnotationTreeNode;
import com.lc.nlp4han.constituent.unlex.Binarization;
import com.lc.nlp4han.constituent.unlex.GrammarExtractor;
import com.lc.nlp4han.constituent.unlex.TreeUtil;

public class AnnotationTreeNodeTest
{
	@Test
	public void getAnnotationTreeTest()
	{
		String[] sentences = { "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))",
				"(ROOT(VP (VT give)(NP (PN me))(NP (CD two) (NN books))))",
				"(ROOT(IP(NP (DT the) (NN boy))(VP (VT saw)(NP (DT the) (NN dog)))))" };
		ArrayList<AnnotationTreeNode> annotationTrees = new ArrayList<>();
		TreeBank treeBank = new TreeBank(annotationTrees, new NonterminalTable());
		for (int i = 0; i < sentences.length; i++)
		{
			TreeNode tree = BracketExpUtil.generateTree(sentences[i]);
			tree = TreeUtil.removeL2LRule(tree);
			tree = Binarization.binarizeTree(tree);
			annotationTrees.add(AnnotationTreeNode.getInstance(tree, treeBank.getNonterminalTable()));
			// AnnotationTreeNode中非叶子节点不包含label对应的字符串，只包含对应的整数。在打印时根据转换表格将整数转化回字符串
			System.out.println(AnnotationTreeNode.printTree(annotationTrees.get(i), 0));
		}
	}

	@Test
	public void IOScoreTest()
	{
		GrammarExtractor gExtractor = new GrammarExtractor();
		Boolean addParentLabel = false;
		String[] sentences = { "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))",
				"(ROOT(VP (VT give)(NP (PN me))(NP (CD two) (NN books))))",
				"(ROOT(IP(NP (DT the) (NN boy))(VP (VT saw)(NP (DT the) (NN dog)))))" };
		ArrayList<AnnotationTreeNode> annotationTrees = new ArrayList<>();
		TreeBank treeBank = new TreeBank(annotationTrees, new NonterminalTable());
		for (int i = 0; i < sentences.length; i++)
		{
			TreeNode tree = BracketExpUtil.generateTree(sentences[i]);
			tree = TreeUtil.removeL2LRule(tree);
			if (addParentLabel)
				tree = TreeUtil.addParentLabel(tree);
			tree = Binarization.binarizeTree(tree);
			annotationTrees.add(AnnotationTreeNode.getInstance(tree, treeBank.getNonterminalTable()));
		}
		gExtractor.treeBank = treeBank;
		gExtractor.initGrammar(1);
		Grammar g = gExtractor.getGrammar(1, 0.5, 50);
		GrammarSpliter.splitGrammar(g, treeBank);
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			TreeBank.calculateInnerScore(g, tree);
			TreeBank.calculateOuterScore(g, tree);
		}
	}
}