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
		TreeBank treeBank = new TreeBank();
		for (int i = 0; i < sentences.length; i++)
		{
			treeBank.addTree(sentences[i], false);
		}
	}

	@Test
	public void IOScoreTest()
	{

		Boolean addParentLabel = false;
		String[] sentences = { "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))",
				"(ROOT(VP (VT give)(NP (PN me))(NP (CD two) (NN books))))",
				"(ROOT(IP(NP (DT the) (NN boy))(VP (VT saw)(NP (DT the) (NN dog)))))" };
		TreeBank treeBank = new TreeBank();
		for (int i = 0; i < sentences.length; i++)
		{
			treeBank.addTree(sentences[i], addParentLabel);
		}
		GrammarExtractor gExtractor = new GrammarExtractor(treeBank);
		Grammar g = gExtractor.getGrammar(1);
		GrammarSpliter.splitGrammar(g, treeBank);
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			TreeBank.calculateInnerScore(g, tree);
			TreeBank.calculateOuterScore(g, tree);
		}
	}
}