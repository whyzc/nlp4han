package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;

import org.junit.Test;

public class RuleTest
{
	@Test
	public void ruleSplitTest()
	{
		Grammar g = new GrammarExtractorTest().extractor();
		try
		{
			GrammarWriter.writerToFile(g, "C:\\Users\\hp\\Desktop\\berforSplit");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		GrammarSpliter.splitGrammar(g);
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
