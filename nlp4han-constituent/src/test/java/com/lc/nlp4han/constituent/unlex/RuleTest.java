package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

public class RuleTest
{
	@Test
	public void ruleSplitTest()
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

		try
		{
			GrammarWriter.writerToFile(g, "C:\\Users\\hp\\Desktop\\berforSplit");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		GrammarSpliter.splitGrammar(g, treeBank);
		try
		{
			GrammarWriter.writerToFile(g, "C:\\Users\\hp\\Desktop\\afterSplit");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
