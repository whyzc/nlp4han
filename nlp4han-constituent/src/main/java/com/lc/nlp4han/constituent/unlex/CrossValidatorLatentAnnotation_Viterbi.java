package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.pcfg.ConstituentTreeStream;
import com.lc.nlp4han.ml.util.CrossValidationPartitioner;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * @author 王宁
 */
public class CrossValidatorLatentAnnotation_Viterbi
{
	public void evaluate(ObjectStream<String> sentenceStream, int nFolds, ConstituentMeasure measure, int SMCycle,
			double mergeRate, int EMIterations, double smoothRate, double pruneThreshold, boolean secondPrune,
			boolean prior) throws IOException
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
			String expression;
			while ((expression = trainingSampleStream.read()) != null)
			{
				treeBank.addTree(expression, false);
			}
			GrammarExtractor gExtractor = new GrammarExtractor(treeBank, Lexicon.DEFAULT_RAREWORD_THRESHOLD);
			Grammar gLatent = gExtractor.getGrammar();
			gLatent = GrammarTrainer.train(gLatent, treeBank, SMCycle, mergeRate, EMIterations, smoothRate);
			System.out.println("训练学习时间：" + (System.currentTimeMillis() - start) + "ms");
			long start2 = System.currentTimeMillis();
			ConstituentParserLatentAnnotation_Viterbi parser = new ConstituentParserLatentAnnotation_Viterbi(gLatent,
					pruneThreshold, secondPrune, prior);
			EvaluatorLatentAnnotation_Viterbi evaluator = new EvaluatorLatentAnnotation_Viterbi(parser);
			evaluator.setMeasure(measure);
			ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(
					trainingSampleStream.getTestSampleStream());
			evaluator.evaluate(sampleStream);
			System.out.println("解析评价时间：" + (System.currentTimeMillis() - start2) + "ms");
			totalTime += (System.currentTimeMillis() - start);

			System.out.println(measure);
			gLatent = null;
			treeBank = null;
			gExtractor = null;
			run++;
		}
		System.out.println("总体时间： " + totalTime + "ms");
	}
}
