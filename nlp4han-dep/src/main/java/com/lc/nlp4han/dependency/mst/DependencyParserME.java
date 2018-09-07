package com.lc.nlp4han.dependency.mst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.lc.nlp4han.dependency.DependencyParseContextGenerator;
import com.lc.nlp4han.dependency.DependencyParseContextGeneratorConf;
import com.lc.nlp4han.dependency.DependencyParser;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleEventStream;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.DependencyTree;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

/**
 * 基于最大熵的依存解析器
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencyParserME implements DependencyParser
{
	public static final int DEFAULT_BEAM_SIZE = 3;

	private DependencyParseContextGenerator contextGenerator;

	private ClassificationModel mm;

	public DependencyParserME(File model) throws IOException
	{
		this(new ModelWrapper(model), new DependencyParseContextGeneratorConf());
	}

	public DependencyParserME(File model, DependencyParseContextGenerator contextGen) throws IOException
	{
		this(new ModelWrapper(model), contextGen);
	}

	public DependencyParserME(ModelWrapper model) throws IOException
	{
		init(model, new DependencyParseContextGeneratorConf());

	}

	public DependencyParserME(ModelWrapper model, DependencyParseContextGenerator contextGen)
	{
		init(model, contextGen);

	}

	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 */
	private void init(ModelWrapper model, DependencyParseContextGenerator contextGen)
	{
		mm = model.getModel();

		contextGenerator = contextGen;
	}

	/**
	 * 训练模型
	 * 
	 * @param file
	 *            训练文件
	 * @param params
	 *            训练参数
	 * @param contextGen
	 *            特征
	 * @param encoding
	 *            编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException
	 */
	public static ModelWrapper train(File file, TrainingParameters params, DependencyParseContextGenerator contextGen,
			String encoding) throws IOException
	{
		ObjectStream<String> lineStream = new PlainTextBySpaceLineStream(new MarkableFileInputStreamFactory(file), encoding);
		// TODO: 样本解析器灵活选择，不能固化
		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(lineStream, sampleParser);
		ModelWrapper model = DependencyParserME.train(sampleStream, params, contextGen);
		return model;
	}

	/**
	 * 训练模型
	 * 
	 * @param sampleStream
	 *            样本流
	 * @param params
	 *            训练参数
	 * @param contextGen
	 *            特征
	 * @return 模型
	 * @throws IOException
	 *             IO异常
	 */
	public static ModelWrapper train(ObjectStream<DependencySample> sampleStream, TrainingParameters params,
			DependencyParseContextGenerator contextGen) throws IOException
	{
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

		int beamSize = DependencyParserME.DEFAULT_BEAM_SIZE;
		if (beamSizeString != null)
		{
			beamSize = Integer.parseInt(beamSizeString);
		}

		ClassificationModel maxentModel = null;
		SequenceClassificationModel<String> seqModel = null;

		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
		{
			ObjectStream<Event> es = new DependencySampleEventStream(sampleStream, contextGen);
			EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			maxentModel = trainer.train(es);
		}

		if (maxentModel != null)
		{
			return new ModelWrapper(maxentModel, beamSize);
		}
		else
		{
			return new ModelWrapper(seqModel);
		}
	}

	/**
	 * 获得关系非null的最大概率及其对应的关系
	 * 
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @param additionaContext
	 *            额外的信息
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagNoNull(String[] sentence, String[] pos, Object[] additionaContext)
	{
		int i = 1, j = 0;
		double[][] proba = new double[sentence.length][sentence.length];
		String[][] dependencyRelation = new String[sentence.length][sentence.length];
		for (int m = 0; m < sentence.length; m++)
		{
			for (int n = 0; n < sentence.length; n++)
			{
				proba[m][n] = 0.0;
				dependencyRelation[m][n] = "null";
			}
		}

		int lenLeft, lenRight;
		if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT == -1)
		{
			lenLeft = -sentence.length;
			lenRight = sentence.length;
		}
		else
		{
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}

		while (i < sentence.length)
		{
			while (j - i <= lenRight && j - i >= lenLeft && j < sentence.length)
			{
				if (i != j)
				{
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);

					String tempDependency[] = new String[temp.length];
					for (int k = 0; k < temp.length; k++)
					{
						tempDependency[k] = mm.getOutcome(k);
					}

					double max = -1;
					int record = -1;
					for (int k = 0; k < temp.length; k++)
					{
						if ((temp[k] > max) && (tempDependency[k].compareTo("null") != 0))
						{
							max = temp[k];
							record = k;
						}
					}

					// 根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = tempDependency[record];
					// 最大的概率
					proba[i][j] = temp[record];
				}

				j++;
			}

			i++;
			j = 0;
		}

		return new DependencyParseMatrix(sentence, pos, proba, dependencyRelation);
	}

	/**
	 * 获得关系非null的最大K个概率及其对应的关系
	 * 
	 * @param kRes
	 *            获得的最好的结果的个数
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @param additionaContext
	 *            额外的信息
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagK(int kRes, String[] sentence, String[] pos, Object[] additionaContext)
	{

		int i = 1, j = 0;
		String[][] proba = new String[sentence.length][sentence.length];
		String[][] dependencyRelation = new String[sentence.length][sentence.length];

		int lenLeft, lenRight;
		if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT == -1)
		{
			lenLeft = -sentence.length;
			lenRight = sentence.length;
		}
		else if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT != -1)
		{
			lenLeft = -sentence.length;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}
		else if (DependencyParseContextGeneratorConf.LEFT != -1 && DependencyParseContextGeneratorConf.RIGHT == -1)
		{
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = sentence.length;
		}
		else
		{
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}

		while (i < sentence.length)
		{
			while (j - i <= lenRight && j - i >= lenLeft && j < sentence.length)
			{
				if (i != j)
				{
					Queue<DepDatum> queue = new PriorityQueue<>();
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);
					String tempDependency[] = new String[temp.length];
					for (int k = 0; k < temp.length; k++)
					{
						if (mm.getOutcome(k).compareTo("null") != 0)
						{
							tempDependency[k] = mm.getOutcome(k);
							queue.add(new DepDatum(temp[k], k));
						}
					}
					String tempProba = "";
					String tempDepen = "";
					for (int k = 0; k < kRes; k++)
					{
						DepDatum data = queue.poll();
						if (k == kRes - 1)
						{
							tempProba += data.getValue();
							tempDepen += tempDependency[data.getIndex()];
						}
						else
						{
							tempProba += data.getValue() + "_";
							tempDepen += tempDependency[data.getIndex()] + "_";
						}
					}
					// 根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = tempDepen;
					// 最大的概率
					proba[i][j] = tempProba;
				}
				j++;
			}
			i++;
			j = 0;
		}

		return new DependencyParseMatrix(sentence, pos, proba, dependencyRelation);
	}

	/**
	 * 获得关系非null的最大概率及其对应的关系
	 * 
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagNoNull(String[] sentence, String[] pos)
	{
		return tagNoNull(sentence, pos, null);
	}

	/**
	 * 获得关系非null的最大K个概率及其对应的关系
	 * 
	 * @param k
	 *            获得的最好的结果的个数
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagK(int k, String[] sentence, String[] pos)
	{

		return tagK(k, sentence, pos, null);
	}

	@Override
	public DependencyTree parse(String sentence)
	{
		String[] wordsandposes = sentence.split("\\s+");
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		for (int i = 0; i < wordsandposes.length; i++)
		{
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}

		return parse(words, poses);
	}

	@Override
	public DependencyTree parse(String[] words, String[] poses)
	{
		List<String> wordslist = Arrays.asList(words);
		List<String> allwords = new ArrayList<>();
		allwords.add(0, "核心");
		allwords.addAll(wordslist);

		List<String> poseslist = Arrays.asList(poses);
		List<String> allposes = new ArrayList<>();
		allposes.add(0, "root");
		allposes.addAll(poseslist);

		DependencyParseMatrix proba = tagNoNull(allwords.toArray(new String[allwords.size()]),
				allposes.toArray(new String[allposes.size()]));

		DependencySample sample;
		sample = MaxSpanningTree.getMaxTreeNoLoop(proba);
		DependencyTree parse = new DependencyTree(sample);

		return parse;
	}

	@Override
	public DependencyTree parse(String[] wordsandposes)
	{
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		for (int i = 0; i < wordsandposes.length; i++)
		{
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}

		return parse(words, poses);
	}

	@Override
	public DependencyTree[] parse(String sentence, int k)
	{
		String[] wordsandposes = sentence.split("\\s+");
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		for (int i = 0; i < wordsandposes.length; i++)
		{
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}

		return parse(words, poses, k);
	}

	@Override
	public DependencyTree[] parse(String[] words, String[] poses, int k)
	{
		List<String> wordslist = Arrays.asList(words);
		List<String> allwords = new ArrayList<>();
		allwords.add(0, "核心");
		allwords.addAll(wordslist);

		List<String> poseslist = Arrays.asList(poses);
		List<String> allposes = new ArrayList<>();
		allposes.add(0, "root");
		allposes.addAll(poseslist);

		DependencyParseMatrix proba = tagK(k, allwords.toArray(new String[allwords.size()]),
				allposes.toArray(new String[allposes.size()]));

		DependencyTree[] parse = new DependencyTree[k];
		parse = MaxSpanningTree.getKMaxTrees(k, proba);

		return parse;
	}

	@Override
	public DependencyTree[] parse(String[] wordsandposes, int k)
	{
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		for (int i = 0; i < wordsandposes.length; i++)
		{
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}

		return parse(words, poses, k);
	}
}
