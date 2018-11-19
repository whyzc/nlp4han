package com.lc.nlp4han.constituent.unlex;

import org.junit.Test;

import com.lc.nlp4han.constituent.unlex.GrammarExtractor;

public class GrammarExtractorTest
{
	@Test
	public void extractor()// 测试提取初始语法
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
		GrammarExtractor gExtractor = new GrammarExtractor(treeBank, Lexicon.DEFAULT_RAREWORD_THRESHOLD);
		Grammar g = gExtractor.getGrammar();
	}
}
