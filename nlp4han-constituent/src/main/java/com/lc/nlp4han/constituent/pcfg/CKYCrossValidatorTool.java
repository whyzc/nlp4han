package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner.TrainingSampleStream;

/**
 * 基于PCFG的CKY解析交叉验证应用
 *
 */
public class CKYCrossValidatorTool
{

	private static ConstituentParser getParser(TrainingSampleStream<ConstituentTree> trainingSampleStream,
			double pruneThreshold, boolean secondPrune,boolean prior) throws IOException
	{
		ArrayList<String> bracketList = new ArrayList<String>();
		ConstituentTree tree = trainingSampleStream.read();
		while (tree != null)
		{
			bracketList.add(tree.getRoot().toString());
			tree = trainingSampleStream.read();
		}
		
		System.out.println("从树库提取文法...");
		PCFG pcfg = GrammarExtractor.getPCFG(bracketList);

		System.out.println("对文法进行转换...");
		PCFG pcnf = GrammarConvertor.convertPCFGToP2NF(pcfg);
		
		if(prior) {
			@SuppressWarnings("unchecked")
			ArrayList<String> bracketListClone=(ArrayList<String>) bracketList.clone();
			HashMap<String,Double> map=NonterminalProUtil.brackets2Map(bracketListClone,"pcfg");
			pcnf=new PCFGPrior(pcnf,map);
		}
		return new ConstituentParserCKYP2NF(pcnf, pruneThreshold, secondPrune,prior);
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
	public void evaluate(ObjectStream<ConstituentTree> sampleStream, int nFolds, ConstituentMeasure measure,
			double pruneThreshold, boolean secondPrune,boolean prior) throws IOException
	{
		CrossValidationPartitioner<ConstituentTree> partitioner = new CrossValidationPartitioner<ConstituentTree>(
				sampleStream, nFolds);
		int run = 1;
		double totalTime = 0;
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");

			long start = System.currentTimeMillis();
			CrossValidationPartitioner.TrainingSampleStream<ConstituentTree> trainingSampleStream = partitioner.next();
			ConstituentParser parser = getParser(trainingSampleStream, pruneThreshold, secondPrune,prior);
			System.out.println("训练学习时间：" + (System.currentTimeMillis() - start) + "ms");

			CKYParserEvaluator evaluator = new CKYParserEvaluator(parser);
			evaluator.setMeasure(measure);

			System.out.println("开始评价...");

			start = System.currentTimeMillis();
			evaluator.evaluate(trainingSampleStream.getTestSampleStream());
			System.out.println("解析评价时间：" + (System.currentTimeMillis() - start) + "ms");
			totalTime += (System.currentTimeMillis() - start);

			System.out.println(measure);
			run++;
		}
		System.out.println("总体时间： " + totalTime + "ms");
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
		double pruneThreshold = 0.0001;
		boolean secondPrune = false;
		boolean prior = false;
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
			else if (args[i].equals("-pruneThreshold"))
			{
				pruneThreshold = Double.parseDouble(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-secondPrune"))
			{
				secondPrune = Boolean.parseBoolean(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-prior"))
			{
				prior = Boolean.parseBoolean(args[i + 1]);
				i++;
			}
		}

		ObjectStream<String> treeStream = new PlainTextByTreeStream(new FileInputStreamFactory(corpusFile), encoding);
		ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(treeStream);
		CKYCrossValidatorTool run = new CKYCrossValidatorTool();
		ConstituentMeasure measure = new ConstituentMeasure();
	
		run.evaluate(sampleStream, folds, measure, pruneThreshold, secondPrune,prior);
	}
}
