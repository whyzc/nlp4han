package com.lc.nlp4han.pos.hmm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.lc.nlp4han.ml.hmm.model.HMM;
import com.lc.nlp4han.ml.hmm.model.HMMWithAStar;
import com.lc.nlp4han.ml.hmm.model.HMMWithViterbi;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.pos.WordPOSMeasure;

public class POSTaggerHMMCrossValidation
{

	private String decodeAlgo;

	public POSTaggerHMMCrossValidation(String decodeAlgo)
	{
		this.decodeAlgo = decodeAlgo;
	}

	public void evaluate(List<SupervisedHMMSample> samples, int folds, String smooth, int order, double ratio)
			throws IOException
	{
		if (folds < 1)
			throw new IllegalArgumentException("折数不能小于1：" + folds);
		System.out.println("cross validating...");

		for (int i = 0; i < folds; i++)
		{
			List<SupervisedHMMSample> trainSamples = new ArrayList<>();
			List<SupervisedHMMSample> testSamples = new ArrayList<>();
			int flag = 0;
			System.out.println("\nRunning : fold-" + (i + 1));
			for (SupervisedHMMSample sample : samples)
			{
				if (flag % folds == i)
					testSamples.add(sample);
				else
					trainSamples.add(sample);

				flag++;
			}
			System.out.println("totalSize = " + samples.size() + "\ttrainSize = " + trainSamples.size()
					+ "\ttestSize = " + testSamples.size());
			long start = System.currentTimeMillis();
			HMModel model = POSTaggerHMM.train(trainSamples, smooth, order, ratio);
			long train = System.currentTimeMillis();

			HMM decodeModel = null;
			switch (decodeAlgo.toUpperCase())
			{
			case "V":
				decodeModel = new HMMWithViterbi(model);
				break;
			case "A":
				decodeModel = new HMMWithAStar(model);
				break;
			}

			Observation[] observations = model.getObservations();
			HashSet<String> dict = new HashSet<>();
			for (Observation observation : observations)
				dict.add(observation.toString());
			WordPOSMeasure measure = new WordPOSMeasure(dict);

			POSTaggerHMMEvaluator evaluator = new POSTaggerHMMEvaluator(decodeModel);
			evaluator.eval(measure, testSamples);
			System.out.println(measure);
			long eval = System.currentTimeMillis();
			System.out.println("训练时间：" + (train - start) / 1000.0 + "s\t评估时间：" + (eval - train) / 1000.0 + "s");
		}
	}

}
