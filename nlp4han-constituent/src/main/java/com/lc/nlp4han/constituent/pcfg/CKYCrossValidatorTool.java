package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner.TrainingSampleStream;

public class CKYCrossValidatorTool
{
	
	private static ConstituentParserCKYOfP2NFImproving getParser(
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

	/**
	 * n折交叉验证评估
	 * 
	 * @param sampleStream
	 *            样本流
	 * @param nFolds
	 *            折数
	 * @param contextGenerator
	 *            上下文
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<ConstituentTree> sampleStream, int nFolds, ConstituentMeasure measure)
			throws IOException
	{

		CrossValidationPartitioner<ConstituentTree> partitioner = new CrossValidationPartitioner<ConstituentTree>(
				sampleStream, nFolds);
		int run = 1;
		// 小于折数的时候
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");

			long start = System.currentTimeMillis();
			CrossValidationPartitioner.TrainingSampleStream<ConstituentTree> trainingSampleStream = partitioner.next();
			ConstituentParserCKYOfP2NFImproving parser = getParser(trainingSampleStream);
			
			System.out.println("训练学习时间：" + (System.currentTimeMillis() - start) + "ms");
			
			CKYParserEvaluator evaluator = new CKYParserEvaluator(parser);
			evaluator.setMeasure(measure);

			System.out.println("开始评价...");
			
			start = System.currentTimeMillis();
			evaluator.evaluate(trainingSampleStream.getTestSampleStream());
			System.out.println("解析评价时间：" + (System.currentTimeMillis() - start) + "ms");
			
			System.out.println(measure);
			run++;
		}
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			// usage();
			return;
		}

		int folds = 10;
		File corpusFile = null;
		String encoding = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-folds"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
		}
		
		ObjectStream<String> treeStream = new PlainTextByTreeStream(new FileInputStreamFactory(corpusFile), encoding);
		ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(treeStream);
		CKYCrossValidatorTool run = new CKYCrossValidatorTool();
		ConstituentMeasure measure = new ConstituentMeasure();
		
		run.evaluate(sampleStream, folds, measure);
	}
}
