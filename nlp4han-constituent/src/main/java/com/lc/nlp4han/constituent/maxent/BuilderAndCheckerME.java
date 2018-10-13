package com.lc.nlp4han.constituent.maxent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 分步骤训练build check模型
 * 
 * @author 王馨苇
 *
 */
public class BuilderAndCheckerME implements BuilderAndChecker<HeadTreeNode>
{
	public static final int DEFAULT_BEAM_SIZE = 20;
	private ParserContextGenerator contextGenerator;
	@SuppressWarnings("unused")
	private int size;
	private ParserSequenceClassificationModel model;

	private ParserSequenceValidator sequenceValidator;
	private AbstractHeadGenerator headGenerator;

	/**
	 * 构造函数，初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 * @param aghw
	 *            生成头结点，build后check为yes时候进行合并的时候需要
	 */
	public BuilderAndCheckerME(ModelWrapper buildmodel, ModelWrapper checkmodel,
			ParserContextGenerator contextGen, AbstractHeadGenerator aghw)
	{
		init(buildmodel, checkmodel, contextGen, aghw);
	}

	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 * @param aghw
	 *            生成头结点，build后check为yes时候进行合并的时候需要
	 */
	private void init(ModelWrapper buildmodel, ModelWrapper checkmodel, ParserContextGenerator contextGen,
			AbstractHeadGenerator aghw)
	{
		int beamSize = BuilderAndCheckerME.DEFAULT_BEAM_SIZE;

		contextGenerator = contextGen;
		size = beamSize;
		sequenceValidator = new DefaultParserSequenceValidator();
		this.headGenerator = aghw;
		this.model = new ParserBeamSearch(beamSize, buildmodel.getModel(), checkmodel.getModel(), 0, aghw);
	}

	/**
	 * 训练模型
	 * 
	 * @param file
	 *            训练文件
	 * @param params
	 *            训练模型的参数配置
	 * @param contextGen
	 *            特征
	 * @param encoding
	 *            编码
	 * @param aghw
	 *            生成头结点的方法
	 * @return 模型和模型信息的包裹结果
	 */
	public static ModelWrapper trainForBuild(File file, TrainingParameters params,
			ParserContextGenerator contextGen, String encoding, AbstractHeadGenerator aghw)
	{
		ModelWrapper model = null;

		try
		{
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(lineStream,
					aghw);
			model = BuilderAndCheckerME.trainForBuild("zh", sampleStream, params, contextGen);
			return model;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 训练模型
	 * 
	 * @param languageCode
	 *            编码
	 * @param sampleStream
	 *            样本流
	 * @param params
	 *            训练模型需要设置的参数
	 * @param contextGen
	 *            特征
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException
	 */
	public static ModelWrapper trainForBuild(String languageCode,
			ObjectStream<ConstituentTreeSample> sampleStream, TrainingParameters params,
			ParserContextGenerator contextGen) throws IOException
	{
		String beamSizeString = params.getSettings().get(ParserBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = BuilderAndCheckerME.DEFAULT_BEAM_SIZE;
		if (beamSizeString != null)
		{
			beamSize = Integer.parseInt(beamSizeString);
		}

		ClassificationModel buildModel = null;
		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());

		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
		{
			ObjectStream<Event> buildes = new SampleEventsForBuild(sampleStream, contextGen);
			EventTrainer buildtrainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			buildModel = buildtrainer.train(buildes);
		}

		return new ModelWrapper(buildModel, beamSize);
	}

	/**
	 * 训练build模型，并将模型写出
	 * 
	 * @param file
	 *            训练的文本
	 * @param buildmodelFile
	 *            模型文件
	 * @param params
	 *            训练模型需要设置的参数
	 * @param contextGen
	 *            上下文 产生器
	 * @param encoding
	 *            编码方式
	 * @param aghw
	 *            生成头结点的方法
	 * @return
	 * @throws IOException
	 */
	public static void trainForBuild(File file, File buildmodelFile, TrainingParameters params,
			ParserContextGenerator contextGen, String encoding, AbstractHeadGenerator aghw)
			throws IOException
	{
		OutputStream modelOut = null;
		ModelWrapper model = null;
		try
		{
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(lineStream,
					aghw);
			model = BuilderAndCheckerME.trainForBuild("zh", sampleStream, params, contextGen);
			modelOut = new BufferedOutputStream(new FileOutputStream(buildmodelFile));
			model.serialize(modelOut);
		}
		finally
		{
			if (modelOut != null)
			{
				try
				{
					modelOut.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 训练模型
	 * 
	 * @param file
	 *            训练文件
	 * @param params
	 *            训练模型的参数配置信息
	 * @param contextGen
	 *            特征
	 * @param encoding
	 *            编码
	 * @param aghw
	 *            生成头结点的方法
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException
	 */
	public static ModelWrapper trainForCheck(File file, TrainingParameters params,
			ParserContextGenerator contextGen, String encoding, AbstractHeadGenerator aghw)
			throws IOException
	{
		ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
		ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(lineStream,
				aghw);
		ModelWrapper model = BuilderAndCheckerME.trainForCheck("zh", sampleStream, params, contextGen);
		return model;

	}

	/**
	 * 训练模型
	 * 
	 * @param languageCode
	 *            编码
	 * @param sampleStream
	 *            样本流
	 * @param params
	 *            训练模型的参数配置信息
	 * @param contextGen
	 *            特征
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException
	 */
	public static ModelWrapper trainForCheck(String languageCode,
			ObjectStream<ConstituentTreeSample> sampleStream, TrainingParameters params,
			ParserContextGenerator contextGen) throws IOException
	{
		String beamSizeString = params.getSettings().get(ParserBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = BuilderAndCheckerME.DEFAULT_BEAM_SIZE;
		if (beamSizeString != null)
		{
			beamSize = Integer.parseInt(beamSizeString);
		}
		ClassificationModel checkModel = null;
		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		// event_model_trainer
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
		{
			sampleStream.reset();
			ObjectStream<Event> checkes = new SampleEventsForCheck(sampleStream, contextGen);
			EventTrainer checktrainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			checkModel = checktrainer.train(checkes);
		}

		return new ModelWrapper(checkModel, beamSize);
	}

	/**
	 * 训练模型，并将模型写出
	 * 
	 * @param file
	 *            训练的文本
	 * @param checkmodelFile
	 *            模型文件
	 * @param params
	 *            训练模型的参数配置信息
	 * @param contextGen
	 *            上下文 产生器
	 * @param encoding
	 *            编码方式
	 * @param aghw
	 *            生成头结点的方法
	 * @return
	 * @throws IOException
	 */
	public static void trainForCheck(File file, File checkmodelFile, TrainingParameters params,
			ParserContextGenerator contextGen, String encoding, AbstractHeadGenerator aghw)
			throws IOException
	{
		OutputStream modelOut = null;
		ModelWrapper model = null;
		try
		{
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(lineStream,
					aghw);
			model = BuilderAndCheckerME.trainForBuild("zh", sampleStream, params, contextGen);
			modelOut = new BufferedOutputStream(new FileOutputStream(checkmodelFile));
			model.serialize(modelOut);
		}
		finally
		{
			if (modelOut != null)
			{
				try
				{
					modelOut.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 得到最好的K个最好结果的树,List中每一个值都是一颗完整的树
	 * 
	 * @param k
	 *            结果数目
	 * @param chunkTree
	 *            chunk标记树
	 * @param ac
	 * @return
	 */
	public List<HeadTreeNode> tagBuildAndCheck(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac)
	{
		List<HeadTreeNode> buildAndCheckTree = new ArrayList<>();
		BuildAndCheckSequence<HeadTreeNode>[] sequences = this.model.bestSequencesForBuildAndCheck(k, chunkTree, ac,
				contextGenerator, sequenceValidator);
		if (sequences == null)
		{
			return null;
		}
		else
		{
			for (int i = 0; i < sequences.length; i++)
			{
				buildAndCheckTree.add(sequences[i].getTree().get(0));
			}
			return buildAndCheckTree;
		}
	}

	/**
	 * 得到最好的K个完整的动作序列
	 * 
	 * @param k
	 *            结果数
	 * @param chunkTree
	 *            k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public List<List<String>> tagKactions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac)
			throws CloneNotSupportedException
	{
		List<List<String>> kActions = new ArrayList<>();
		List<HeadTreeNode> alltree = tagBuildAndCheck(k, chunkTree, null);
		if (alltree == null)
		{
			return null;
		}
		else
		{
			for (int i = 0; i < alltree.size(); i++)
			{
				TreeNode node = BracketExpUtil.generateTreeNoTopBracket("(" + alltree.get(i).toStringWordIndex() + ")");
				HeadTreeNode headTree = TreeToHeadTree.treeToHeadTree(node, headGenerator);
				ConstituentTreeSample sample = HeadTreeToSample.headTreeToSample(headTree,
						headGenerator);
				kActions.add(sample.getActions());
			}
			return kActions;
		}
	}

	/**
	 * 得到最好的K个完整的动作序列
	 * 
	 * @param k
	 *            结果数
	 * @param chunkTree
	 *            k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public List<String> tagActions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac)
			throws CloneNotSupportedException
	{
		List<List<String>> kActions = tagKactions(1, chunkTree, null);
		return kActions.get(0);
	}

	/**
	 * 得到最好的树
	 * 
	 * @param chunkTree
	 *            chunk标记树
	 * @param ac
	 * @return
	 */
	public HeadTreeNode tagBuildAndCheck(List<List<HeadTreeNode>> chunkTree, Object[] ac)
	{
		List<HeadTreeNode> buildAndCheckTree = tagBuildAndCheck(1, chunkTree, ac);
		if (buildAndCheckTree == null)
		{
			return null;
		}
		else
		{
			return buildAndCheckTree.get(0);
		}
	}

	/**
	 * 将分词序列，词性序列和chunk标记序列，转换成chunk子树序列
	 * 
	 * @param words
	 *            分词序列
	 * @param poses
	 *            词性序列
	 * @param chunkTag
	 *            语块标记序列
	 * @return
	 */
	private List<HeadTreeNode> toChunkTreeList(String[] words, String[] poses, String[] chunkTag)
	{
		List<HeadTreeNode> chunkTree = new ArrayList<>();
		for (int i = 0; i < chunkTag.length; i++)
		{
			if (chunkTag[i].equals("O"))
			{
				HeadTreeNode pos = new HeadTreeNode(poses[i]);
				pos.addChild(new HeadTreeNode(words[i]));
				pos.setHeadWord(words[i]);
				pos.setHeadPos(poses[i]);
				chunkTree.add(pos);
			}
			else if (chunkTag[i].endsWith("B"))
			{
				HeadTreeNode node = new HeadTreeNode(chunkTag[i].split("_")[0]);
				int j;
				for (j = i; j < chunkTag.length; j++)
				{
					HeadTreeNode pos = new HeadTreeNode(poses[j]);
					pos.addChild(new HeadTreeNode(words[j]));
					pos.setHeadWord(words[j]);
					pos.setHeadPos(poses[i]);
					node.addChild(pos);
					if (chunkTag[j].endsWith("E"))
					{
						break;
					}
				}
				node.setHeadWord(headGenerator.extractHeadWord(node));
				node.setHeadPos(headGenerator.extractHeadPos(node));
				chunkTree.add(node);
				i = j;
			}
		}
		return chunkTree;
	}

	/**
	 * 得到句法树
	 * 
	 * @param chunkTree
	 *            chunk子树序列
	 * @return
	 */
	@Override
	public HeadTreeNode syntacticTree(List<HeadTreeNode> chunkTree)
	{
		List<List<HeadTreeNode>> allTree = new ArrayList<>();
		allTree.add(chunkTree);
		HeadTreeNode headTreeNode = tagBuildAndCheck(allTree, null);
		return headTreeNode;
	}

	/**
	 * 得到句法树
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性标记
	 * @param chunkTag
	 *            chunk标记
	 * @return
	 */
	@Override
	public HeadTreeNode syntacticTree(String[] words, String[] poses, String[] chunkTag)
	{
		List<HeadTreeNode> chunkTree = toChunkTreeList(words, poses, chunkTag);
		return syntacticTree(chunkTree);
	}

	/**
	 * 得到句法树
	 * 
	 * @param sentence
	 *            由词语词性标记和chunk标记组成的句子,输入的格式[wods/pos word/pos...]tag
	 * @return
	 */
	@Override
	public HeadTreeNode syntacticTree(String sentence)
	{
		return syntacticTree(1, sentence).get(0);
	}

	/**
	 * 得到最好的K个句法树
	 * 
	 * @param k
	 *            最好的K个结果
	 * @param chunkTree
	 *            chunk子树序列
	 * @return
	 */
	@Override
	public List<HeadTreeNode> syntacticTree(int k, List<HeadTreeNode> chunkTree)
	{
		List<List<HeadTreeNode>> allTree = new ArrayList<>();
		allTree.add(chunkTree);
		List<HeadTreeNode> headTreeNode = tagBuildAndCheck(k, allTree, null);
		return headTreeNode;
	}

	/**
	 * 得到最好的K个句法树
	 * 
	 * @param k
	 *            最好的K个结果
	 * @param words
	 *            词语
	 * @param poses
	 *            词性标记
	 * @param chunkTag
	 *            chunk标记
	 * @return
	 */
	@Override
	public List<HeadTreeNode> syntacticTree(int k, String[] words, String[] poses, String[] chunkTag)
	{
		List<HeadTreeNode> chunkTree = toChunkTreeList(words, poses, chunkTag);
		return syntacticTree(k, chunkTree);
	}

	/**
	 * 得到最好的K个句法树
	 * 
	 * @param k
	 *            最好的K个结果
	 * @param sentence
	 *            由词语词性标记和chunk标记组成的句子
	 * @return
	 */
	@Override
	public List<HeadTreeNode> syntacticTree(int k, String sentence)
	{
		List<String> chunkTags = new ArrayList<>();
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();

		boolean isInChunk = false; // 当前词是否在组块中
		List<String> wordTagsInChunk = new ArrayList<>(); // 临时存储在组块中的词与词性
		String[] wordTag = null; // 词与词性标注
		String chunk = null; // 组块的标签
		String[] content = sentence.split("\\s+");
		for (String string : content)
		{
			if (isInChunk)
			{ // 当前词在组块中
				if (string.contains("]"))
				{// 当前词是组块的结束
					String[] strings = string.split("]");
					wordTagsInChunk.add(strings[0]);
					chunk = strings[1];
					isInChunk = false;
				}
				else
				{
					wordTagsInChunk.add(string);
				}
			}
			else
			{// 当前词不在组块中
				if (wordTagsInChunk != null && chunk != null)
				{// 上一个组块中的词未处理，先处理上一个组块中的词
					wordTag = wordTagsInChunk.get(0).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + "_B");

					if (wordTagsInChunk.size() > 2)
					{
						for (int i = 1; i < wordTagsInChunk.size() - 1; i++)
						{
							wordTag = wordTagsInChunk.get(i).split("/");
							words.add(wordTag[0]);
							poses.add(wordTag[1]);
							chunkTags.add(chunk + "_I");
						}
					}
					wordTag = wordTagsInChunk.get(wordTagsInChunk.size() - 1).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + "_E");

					wordTagsInChunk = new ArrayList<>();
					chunk = null;

					if (string.startsWith("["))
					{
						wordTagsInChunk.add(string.replace("[", ""));
						isInChunk = true;
					}
					else
					{
						wordTag = string.split("/");
						words.add(wordTag[0]);
						poses.add(wordTag[1]);
						chunkTags.add("O");
					}

				}
				else
				{
					if (string.startsWith("["))
					{
						wordTagsInChunk.add(string.replace("[", ""));
						isInChunk = true;
					}
					else
					{
						wordTag = string.split("/");
						words.add(wordTag[0]);
						poses.add(wordTag[1]);
						chunkTags.add("O");
					}
				}
			}
		}

		// 句子结尾是组块，进行解析
		if (wordTagsInChunk != null && chunk != null)
		{
			wordTag = wordTagsInChunk.get(0).split("/");
			words.add(wordTag[0]);
			poses.add(wordTag[1]);
			chunkTags.add(chunk + "_B");

			if (wordTagsInChunk.size() > 2)
			{
				for (int i = 1; i < wordTagsInChunk.size() - 1; i++)
				{
					wordTag = wordTagsInChunk.get(i).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + "_I");
				}
			}
			wordTag = wordTagsInChunk.get(wordTagsInChunk.size() - 1).split("/");
			words.add(wordTag[0]);
			poses.add(wordTag[1]);
			chunkTags.add(chunk + "_E");
		}

		return syntacticTree(k, words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]),
				chunkTags.toArray(new String[chunkTags.size()]));
	}
}
