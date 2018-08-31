package com.lc.nlp4han.pos.hmm;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.lc.nlp4han.ml.hmm.model.HMM;
import com.lc.nlp4han.ml.hmm.model.HMMWithAStar;
import com.lc.nlp4han.ml.hmm.model.HMMWithViterbi;
import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;
import com.lc.nlp4han.ml.hmm.utils.Observation;
import com.lc.nlp4han.pos.WordPOSMeasure;

/**
 * 基于隐马尔科夫的词性标注器的评估工具类
 * 
 * @author lim
 *
 */
public class POSTaggerHMMEvalTool
{
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{

		if (args.length < 1)
		{
			usage();
			return;
		}

		int order = 1;
		String modelPath = "E:\\codeprac\\HMM\\pos-hmm.model";
		String dataPath = "E:\\codeprac\\HMM\\pos.test";
		String decodeAlgo = "A";
		String modelFileType = "object";
		String encoding = "UTF-8";

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				dataPath = args[i + 1];
				i++;
			}
			if (args[i].equals("-model"))
			{
				modelPath = args[i + 1];
				i++;
			}
			if (args[i].equals("-decode"))
			{
				decodeAlgo = args[i + 1];
				i++;
			}
			if (args[i].equals("-type"))
			{
				modelFileType = args[i + 1];
				i++;
			}
			if (args[i].equals("-order"))
			{
				order = Integer.parseInt(args[i + 1]);
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}

		long startTime = System.currentTimeMillis();
		
		File evalFile = new File(dataPath);
		List<SupervisedHMMSample> evalSamples = POSTaggerHMMSamplesReader.readSupervisedHMMSamples(evalFile, order,
				encoding);

		File modelFile = new File(modelPath);
		HMModel hmModel = ModelInput.loadModel(modelFile, modelFileType);
		HMM decodeModel = null;
		switch (decodeAlgo.toUpperCase())
		{
		case "A":
			decodeModel = new HMMWithAStar(hmModel);
			break;
		case "V":
			decodeModel = new HMMWithViterbi(hmModel);
			break;
		}
		POSTaggerHMMEvaluator evaluator = new POSTaggerHMMEvaluator(decodeModel);

		Observation[] observations = hmModel.getObservations();
		HashSet<String> dict = new HashSet<>();
		for (Observation observation : observations)
			dict.add(observation.toString());
		WordPOSMeasure measure = new WordPOSMeasure(dict);

		evaluator.eval(measure, evalSamples);
		System.out.println(measure);
		System.out.println("评估时间：" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
	}

	private static void usage()
	{
		System.out.println(POSTaggerHMMEvalTool.class.getName()
				+ "-data <testData> -model <modelOutPath> -decode <decodeAlgorithm> "
				+ "-type <modelFileType> -order <orderOfHMM> -encoding <encoding>");
	}

}
