package com.lc.nlp4han.constituent.lex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.pcfg.CKYParserEvaluator;
import com.lc.nlp4han.constituent.pcfg.ConstituentTreeStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner.TrainingSampleStream;

/**
 * 基于LexPCFG的CKY解析交叉验证应用
 * 
 * @author qyl
 *
 */
public class LexCKYCrossValidatorTool
{

	private static ConstituentParser getParser(TrainingSampleStream<ConstituentTree> trainingSampleStream)
			throws IOException
	{
		ArrayList<String> bracketList = new ArrayList<String>();
		ConstituentTree tree = trainingSampleStream.read();
		int i=0;
		while (tree != null)
		{
			bracketList.add(tree.getRoot().toString());
			tree = trainingSampleStream.read();
			i++;
		}
        System.out.println("训练模型句子的个数是 "+i);
		System.out.println("从树库提取文法...");
		LexPCFG lexpcfg = LexGrammarExtractor.getLexPCFG(bracketList);

		return new ConstituentParseLexPCFG(lexpcfg);
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
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");

			long start = System.currentTimeMillis();
			CrossValidationPartitioner.TrainingSampleStream<ConstituentTree> trainingSampleStream = partitioner.next();
			ConstituentParser parser = getParser(trainingSampleStream);
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
		LexCKYCrossValidatorTool run = new LexCKYCrossValidatorTool();
		ConstituentMeasure measure = new ConstituentMeasure();

		run.evaluate(sampleStream, folds, measure);
	}
}
