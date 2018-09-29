package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.lc.nlp4han.dependency.DependencyParser;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.DependencyTree;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.EventModelSequenceTrainer;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.Sequence;
import com.lc.nlp4han.ml.util.SequenceTrainer;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;

public class DependencyParserTB implements DependencyParser
{
	public static final int DEFAULT_BEAM_SIZE = 3;

	private DependencyParseContextGenerator contextGenerator;

	private ClassificationModel model;

	private SequenceClassificationModel<String> SModel;

	private SequenceValidator<String> sequenceValidator;

	private Configuration conf;

	public DependencyParserTB(String modelPath, DependencyParseContextGenerator contextGenerator, Configuration conf,
			SequenceValidator<String> sequenceValidator) throws IOException
	{
		this(new File(modelPath), contextGenerator, conf, sequenceValidator);
	}

	public DependencyParserTB(File file, DependencyParseContextGenerator contextGenerator, Configuration conf,
			SequenceValidator<String> sequenceValidator) throws IOException
	{
		this(new ModelWrapper(file), contextGenerator, conf, sequenceValidator);
	}

	public DependencyParserTB(ModelWrapper model, DependencyParseContextGenerator contextGenerator, Configuration conf,
			SequenceValidator<String> sequenceValidator)
	{
		init(model, contextGenerator, conf, sequenceValidator);
	}

	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 */
	private void init(ModelWrapper model, DependencyParseContextGenerator contextGenerator, Configuration conf,
			SequenceValidator<String> sequenceValidator)
	{
		this.model = model.getModel();

		this.SModel = model.getSequenceModel();

		this.conf = conf;

		this.contextGenerator = contextGenerator;

		this.sequenceValidator = sequenceValidator;
	}

	public static ModelWrapper train(String trainDatePath, TrainingParameters params,
			DependencyParseContextGenerator contextGenerator, String encoding) throws IOException
	{
		return train(new File(trainDatePath), params, contextGenerator, encoding);
	}

	public static ModelWrapper train(ObjectStream<DependencySample> samples, TrainingParameters trainParams)
			throws IOException
	{
		return train(samples, trainParams, new DependencyParseContextGeneratorConfArcEager());
	}

	public static ModelWrapper train(File fileData, TrainingParameters params,
			DependencyParseContextGenerator contextGenerator, String encoding) throws IOException
	{
		ObjectStream<String> lineStream = new PlainTextBySpaceLineStream(new MarkableFileInputStreamFactory(fileData),
				encoding);

		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(lineStream, sampleParser);
		return train(sampleStream, params, contextGenerator);
	}

	public static ModelWrapper train(ObjectStream<DependencySample> sampleStream, TrainingParameters params,
			DependencyParseContextGenerator contextGenerator) throws IOException
	{

		// String beamSizeString =
		// params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

		int beamSize = DependencyParserTB.DEFAULT_BEAM_SIZE;
		// if (beamSizeString != null)
		// {
		// beamSize = Integer.parseInt(beamSizeString);
		// }

		ClassificationModel depModel = null;
		SequenceClassificationModel seqDepModel = null;

		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());

		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
		{
			ObjectStream<Event> es;
			if (contextGenerator instanceof DependencyParseContextGeneratorConfArcEager)
				es = new DependencySampleEventStreamArcEager(sampleStream, contextGenerator);
			else
				es = new DependencySampleEventStreamArcStandard(sampleStream, contextGenerator);
			EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			depModel = trainer.train(es);
			System.out.println("训练完毕：");
		}
		else if (TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType))
		{
			System.err.println(TrainerType.EVENT_MODEL_SEQUENCE_TRAINER);
			DependencySampleSequenceStreamArcEager ss;
			// if (contextGenerator instanceof DependencyParseContextGeneratorConf_ArcEager)
			ss = new DependencySampleSequenceStreamArcEager(sampleStream, contextGenerator);
			// else
			// ss=new DependencySampleSequenceStream_ArcStandard(sampleStream,
			// contextGenerator);
			EventModelSequenceTrainer trainer = TrainerFactory.getEventModelSequenceTrainer(params.getSettings(),
					manifestInfoEntries);
			depModel = trainer.train(ss);
		}
		else if (TrainerType.SEQUENCE_TRAINER.equals(trainerType))
		{
			SequenceTrainer trainer = TrainerFactory.getSequenceModelTrainer(params.getSettings(), manifestInfoEntries);
			DependencySampleSequenceStreamArcEager ss;
			// if (contextGenerator instanceof DependencyParseContextGeneratorConf_ArcEager)
			ss = new DependencySampleSequenceStreamArcEager(sampleStream, contextGenerator);
			seqDepModel = trainer.train(ss);
		}
		else
		{
			throw new IllegalArgumentException("Trainer type is not supported: " + trainerType);
		}

		return new ModelWrapper(depModel, beamSize);
	}

	@Override
	public DependencyTree parse(String[] words, String[] poses)
	{
		ArrayList<String> allWords = new ArrayList<String>(Arrays.asList(words));
		allWords.add(0, DependencyParserTB.RootWord);
		ArrayList<String> allPoses = new ArrayList<String>(Arrays.asList(poses));
		allPoses.add(0, "root");
		words = allWords.toArray(new String[allWords.size()]);
		poses = allPoses.toArray(new String[allPoses.size()]);

		Oracle oracleMEBased = new Oracle(model, contextGenerator);
		Action action = new Action();
		conf.initialConf(words, poses);
		String[] priorDecisions = new String[2 * (words.length - 1)];
		int indexOfConf = 0;
		while (!conf.isFinalConf())
		{
			action = oracleMEBased.classify(conf, priorDecisions, null);
			// System.out.println(currentConf.toString() + "*****" + "preAction =" +
			// action.typeToString());
			conf.transfer(action);
			priorDecisions[indexOfConf] = action.typeToString();
			indexOfConf++;
		}
		// System.out.println(currentConf.arcsToString());
		DependencyTree depTree = DependencyTreeTBUtil.getTree(conf, words, poses);
		return depTree;
	}

	@Override
	public DependencyTree parse(String sentence)
	{
		String[] wordsandposes = sentence.split("/");
		return parse(wordsandposes);
	}

	@Override
	public DependencyTree[] parse(String sentence, int k)
	{

		String[] wordsandposes = sentence.split("/");
		return parse(wordsandposes, k);
	}

	@Override
	public DependencyTree[] parse(String[] words, String[] poses, int k)
	{
		ArrayList<String> allWords = new ArrayList<String>(Arrays.asList(words));
		allWords.add(0, DependencyParserTB.RootWord);
		ArrayList<String> allPoses = new ArrayList<String>(Arrays.asList(poses));
		allPoses.add(0, "root");
		words = allWords.toArray(new String[allWords.size()]);
		poses = allPoses.toArray(new String[allPoses.size()]);
		String[] wordpos = new String[(words.length - 1) * 2];
		for (int i = 0; i < words.length; i++)
		{
			wordpos[i] = words[i] + "/" + poses[i];
		}

		Sequence[] allSequence = SModel.bestSequences(k, wordpos, null, contextGenerator, sequenceValidator);
		DependencyTree[] allTree = new DependencyTree[allSequence.length];
		for (int i = 0; i < allSequence.length; i++)
		{
			conf.initialConf(words, poses);
			for (String outcome : allSequence[i].getOutcomes())
			{
				conf.transfer(Action.toType(outcome));
			}
			DependencyTree depTree = DependencyTreeTBUtil.getTree(conf, words, poses);
			allTree[i] = depTree;
		}
		return allTree;
	}

	@Override
	public DependencyTree parse(String[] wordsandposes)
	{
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];
		for (int i = 0; i < wordsandposes.length; i++)
		{
			String[] word_pos = wordsandposes[i].split("/");
			words[i] = word_pos[0];
			poses[i] = word_pos[1];
		}
		return parse(words, poses, 1)[0];
	}

	@Override
	public DependencyTree[] parse(String[] wordsandposes, int k)
	{
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];
		for (int i = 0; i < wordsandposes.length; i++)
		{
			String[] word_pos = wordsandposes[i].split("/");
			words[i] = word_pos[0];
			poses[i] = word_pos[1];
		}
		return parse(words, poses, k);
	}

}