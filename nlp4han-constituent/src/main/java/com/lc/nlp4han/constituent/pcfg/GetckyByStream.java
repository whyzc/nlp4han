package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.util.ArrayList;

import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner.TrainingSampleStream;

public class GetckyByStream
{
    public static ConstituentParserCKYOfP2NFImproving getckyFromStream(TrainingSampleStream<ConstituentTree> trainingSampleStream) throws IOException {
    	ArrayList<String> bracketList=new ArrayList<String>();
    	ConstituentTree tree=trainingSampleStream.read();
    	while(tree!=null) {
    		bracketList.add(tree.getRoot().toString());
    		tree=trainingSampleStream.read();
    	}
    	PCFG pcfg=new GrammarExtractor().getPCFG(bracketList);
    	PCFG p2nf=new ConvertPCFGToP2NF().convertToCNF(pcfg);
		return new ConstituentParserCKYOfP2NFImproving(p2nf);	
    }
}
