package com.lc.nlp4han.pos.hmm;

import java.util.List;

import com.lc.nlp4han.ml.hmm.model.HMM;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.ObservationSequence;
import com.lc.nlp4han.ml.hmm.utils.StateSequence;
import com.lc.nlp4han.pos.WordPOSMeasure;

public class POSTaggerHMMEvaluator
{
	private HMM model;

	public POSTaggerHMMEvaluator(HMM model)
	{
		this.model = model;
	}

	public void eval(WordPOSMeasure measure, List<SupervisedHMMSample> samples)
	{

		for (SupervisedHMMSample sample : samples)
		{
			StateSequence refStateSeuence = sample.getStateSequence();
			ObservationSequence wordSequence = sample.getObservationSequence();

			StateSequence preStateSeuence = model.bestStateSeqence(wordSequence);
			String[] words = new String[wordSequence.length()];
			String[] refPOS = new String[refStateSeuence.length()];
			String[] prePOS = new String[refStateSeuence.length()];
			for (int i = 0; i < words.length; i++)
			{
				words[i] = wordSequence.get(i).toString();
				refPOS[i] = refStateSeuence.get(i).toString();
				prePOS[i] = preStateSeuence.get(i).toString();
			}
			measure.updateScores(words, refPOS, prePOS);
		}
	}

}
