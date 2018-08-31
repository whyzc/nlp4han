package com.lc.nlp4han.pos.hmm;

import java.io.IOException;
import java.util.List;

import com.lc.nlp4han.ml.hmm.learn.HMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedAdditionHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedGoodTuringHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedInterpolationHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedMLHMMTrainer;
import com.lc.nlp4han.ml.hmm.learn.SupervisedWittenBellHMMTrainer;
import com.lc.nlp4han.ml.hmm.model.HMM;
import com.lc.nlp4han.ml.hmm.model.HMMWithAStar;
import com.lc.nlp4han.ml.hmm.model.HMMWithViterbi;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.ml.hmm.utils.ObservationSequence;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;
import com.lc.nlp4han.ml.hmm.utils.StringObservation;
import com.lc.nlp4han.pos.POSTagger;

public class POSTaggerHMM implements POSTagger
{
	private HMModel model;
	private String decodeAlgo;

	public POSTaggerHMM()
	{

	}

	public POSTaggerHMM(HMModel model)
	{
		this.model = model;
		this.decodeAlgo = "A";
	}

	public POSTaggerHMM(HMModel model, String decodeAlgo)
	{
		this.model = model;
		this.decodeAlgo = decodeAlgo;
	}

	public static HMModel train(List<SupervisedHMMSample> supervisedSamples, String smooth, int order, double ratio)
			throws IOException
	{

		HMModel hmmModel = null;
		HMMTrainer trainer = null;

		switch (smooth.toUpperCase())
		{
		case "ML":
			trainer = new SupervisedMLHMMTrainer(supervisedSamples, order);
			break;
		case "ADD":
			trainer = new SupervisedAdditionHMMTrainer(supervisedSamples, order);
			break;
		case "INT":
			trainer = new SupervisedInterpolationHMMTrainer(supervisedSamples, ratio, order);
			break;
		case "WB":
			trainer = new SupervisedWittenBellHMMTrainer(supervisedSamples, order);
			break;
		case "GT":
			trainer = new SupervisedGoodTuringHMMTrainer(supervisedSamples, order);
			break;
		default:
			throw new IllegalArgumentException("错误的平滑方法：" + smooth);
		}
		hmmModel = trainer.train();
		return hmmModel;
	}

	public String[] tag(String[] sentence)
	{
		HMM hmm = null;
		switch (decodeAlgo.toUpperCase())
		{
		case "A":
			hmm = new HMMWithAStar(model);
			break;
		case "V":
			hmm = new HMMWithViterbi(model);
			break;
		}

		Observation[] words = new Observation[sentence.length];
		for (int i = 0; i < sentence.length; i++)
		{
			words[i] = new StringObservation(sentence[i]);
		}
		ObservationSequence wordSequence = new ObservationSequence(words);
		StateSequence state = hmm.bestStateSeqence(wordSequence);
		String[] pos = state.toString().split(" ");
		return pos;
	}

}
