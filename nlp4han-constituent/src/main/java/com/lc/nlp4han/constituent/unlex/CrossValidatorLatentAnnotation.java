package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.pcfg.ConstituentTreeStream;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ObjectStream;

public class CrossValidatorLatentAnnotation
{
	public void evaluate(ObjectStream<String> sentenceStream, int nFolds, ConstituentMeasure measure,
			double pruneThreshold, boolean secondPrune, boolean prior) throws IOException
	{

		CrossValidationPartitioner<String> partitioner = new CrossValidationPartitioner<String>(sentenceStream, nFolds);
		int run = 1;
		double totalTime = 0;
		while (partitioner.hasNext())
		{
			System.out.println("Run" + run + "...");
			long start = System.currentTimeMillis();
			CrossValidationPartitioner.TrainingSampleStream<String> trainingSampleStream = partitioner.next();
			TreeBank treeBank = new TreeBank();
			TreeBank treeBank2 = new TreeBank();
			String expression;
			while ((expression = trainingSampleStream.read()) != null)
			{
				treeBank.addTree(expression, false);
				treeBank2.addTree(expression, false);
			}
			GrammarExtractor gExtractor = new GrammarExtractor(treeBank, Lexicon.DEFAULT_RAREWORD_THRESHOLD);
			Grammar g = gExtractor.getGrammar();
			PCFG pcfg = g.getPCFG();
			GrammarExtractor gExtractor2 = new GrammarExtractor(treeBank2, Lexicon.DEFAULT_RAREWORD_THRESHOLD);
			Grammar gLatent = gExtractor2.getGrammar();
			gLatent = GrammarTrainer.train(gLatent, treeBank2, 1, 0.5, 50);
			System.out.println("训练学习时间：" + (System.currentTimeMillis() - start) + "ms");
			long start2 = System.currentTimeMillis();
			EvaluatorLatentAnnotation evaluator = new EvaluatorLatentAnnotation(gLatent, pcfg, pruneThreshold,
					secondPrune, prior);
			evaluator.setMeasure(measure);
			ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(
					trainingSampleStream.getTestSampleStream());
			evaluator.evaluate(sampleStream);
			System.out.println("解析评价时间：" + (System.currentTimeMillis() - start2) + "ms");
			totalTime += (System.currentTimeMillis() - start);

			System.out.println(measure);
			g = null;
			pcfg = null;
			treeBank = null;
			gExtractor = null;
			run++;
		}
		System.out.println("总体时间： " + totalTime + "ms");
	}
}
