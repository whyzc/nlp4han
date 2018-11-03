package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.unlex.AnnotationTreeNode;
import com.lc.nlp4han.constituent.unlex.Binarization;
import com.lc.nlp4han.constituent.unlex.TreeUtil;
import com.lc.nlp4han.constituent.unlex.GrammarExtractor;

public class GrammarExtractorTest
{
	@Test
	public Grammar extractor()
	{
		GrammarExtractor gExtractor = new GrammarExtractor();
		Boolean addParentLabel = false;
		String[] sentences = { "(ROOT(IP(NP (PN i))(VP (VV like)(NP (DT the) (NN book)))))",
				"(ROOT(VP (VT give)(NP (PN me))(NP (CD two) (NN books))))",
				"(ROOT(IP(NP (DT the) (NN boy))(VP (VT saw)(NP (DT the) (NN dog)))))" };
		ArrayList<AnnotationTreeNode> annotationTrees = new ArrayList<>();
		for (int i = 0; i < sentences.length; i++)
		{
			TreeNode tree = BracketExpUtil.generateTree(sentences[i]);
			tree = TreeUtil.removeL2LRule(tree);
			if (addParentLabel)
				tree = TreeUtil.addParentLabel(tree);
			tree = Binarization.binarizeTree(tree);
			annotationTrees.add(AnnotationTreeNode.getInstance(tree));
		}
		gExtractor.treeBank = annotationTrees;
		gExtractor.init(1);
		try
		{
			return gExtractor.getInitialGrammar();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
