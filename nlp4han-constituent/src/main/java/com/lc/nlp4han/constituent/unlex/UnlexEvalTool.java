package com.lc.nlp4han.constituent.unlex;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.pcfg.ConstituentTreeStream;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
* @author 作者
* @version 创建时间：2018年10月22日 上午3:29:33
* 类说明
*/
public class UnlexEvalTool
{
	public static void eval() throws IOException
	{
		Grammar g = GrammarExtractorTool.generateInitialGrammar(true, Lexicon.DEFAULT_RAREWORD_THRESHOLD,
				"C:\\Users\\hp\\Desktop\\train.txt");
		GrammarWriter.writerToFile(g, "C:\\Users\\hp\\Desktop\\grammartest");
		PCFG p2nf = g.getPCFG();
		
		UnlexEvaluator evaluator = new UnlexEvaluator(p2nf);
		
		ConstituentMeasure measure = new ConstituentMeasure();
		evaluator.setMeasure(measure);
		
		ObjectStream<String> treeStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File("C:\\Users\\hp\\Desktop\\goldtree.txt")),
				"gbk");
		ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(treeStream);
		evaluator.evaluate(sampleStream);
		
		ConstituentMeasure measureRes = evaluator.getMeasure();
		System.out.println(measureRes);
	}
	public static void main(String[] args)
	{
		try
		{
			eval();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
