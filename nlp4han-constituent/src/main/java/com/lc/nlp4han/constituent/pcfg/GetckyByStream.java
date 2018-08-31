package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.util.ArrayList;

import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner.TrainingSampleStream;

public class GetckyByStream
{
	public static ConstituentParserCKYOfP2NFImproving getckyFromStream(
			TrainingSampleStream<ConstituentTree> trainingSampleStream) throws IOException
	{
		ArrayList<String> bracketList = new ArrayList<String>();
		ConstituentTree tree = trainingSampleStream.read();
		while (tree != null)
		{
			bracketList.add(tree.getRoot().toString());
			tree = trainingSampleStream.read();
		}
		
		System.out.println("从树库提取文法...");
		PCFG pcfg = new GrammarExtractor().getPCFG(bracketList);
		
		System.out.println("对文法进行转换...");
		PCFG p2nf = new ConvertPCFGToP2NF().convertToCNF(pcfg);
		return new ConstituentParserCKYOfP2NFImproving(p2nf);
	}
}
