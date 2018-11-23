package com.lc.nlp4han.constituent.maxent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
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
 * 分步骤训练chunk模型
 * 
 * @author 王馨苇
 *
 */
public class ChunkerForParserME implements ChunkerForParser<HeadTreeNode>
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
	 *            生成头结点,chunk步合并的时候需要
	 */
	public ChunkerForParserME(ModelWrapper model, ParserContextGenerator contextGen,
			AbstractHeadGenerator aghw)
	{
		init(model, contextGen, aghw);
	}

	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 * @param aghw
	 *            生成头结点，chunk步合并的时候需要
	 */
	private void init(ModelWrapper model, ParserContextGenerator contextGen,
			AbstractHeadGenerator aghw)
	{
		int beamSize = ChunkerForParserME.DEFAULT_BEAM_SIZE;

		contextGenerator = contextGen;
		size = beamSize;
		sequenceValidator = new DefaultParserSequenceValidator();
		this.headGenerator = aghw;
		this.model = new ParserBeamSearch(beamSize, model.getModel(), 0);
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
	public static ModelWrapper train(File file, TrainingParameters params,
			ParserContextGenerator contextGen, String encoding, AbstractHeadGenerator aghw)
			throws IOException
	{
		ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
		ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(lineStream,
				aghw);
		ModelWrapper model = ChunkerForParserME.train("zh", sampleStream, params, contextGen);
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
	 *            特征生成
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException
	 */
	public static ModelWrapper train(String languageCode,
			ObjectStream<ConstituentTreeSample> sampleStream, TrainingParameters params,
			ParserContextGenerator contextGen) throws IOException
	{
		String beamSizeString = params.getSettings().get(ParserBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = ChunkerForParserME.DEFAULT_BEAM_SIZE;
		if (beamSizeString != null)
		{
			beamSize = Integer.parseInt(beamSizeString);
		}
		
		ClassificationModel chunkModel = null;
		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
		{
			ObjectStream<Event> es = new SampleEventsForChunk(sampleStream, contextGen);
			EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			
			chunkModel = trainer.train(es);
		}
		
		return new ModelWrapper(chunkModel, beamSize);
	}

	/**
	 * 训练模型，并将模型写出
	 * 
	 * @param file
	 *            训练的文本
	 * @param modelFile
	 *            模型文件
	 * @param params
	 *            训练的参数配置
	 * @param contextGen
	 *            特征生成器
	 * @param encoding
	 *            编码方式
	 * @param headGen
	 *            生成头结点的方法
	 *            
	 * @throws IOException
	 */
	public static void train(File file, File modelFile, TrainingParameters params,
			ParserContextGenerator contextGen, String encoding, AbstractHeadGenerator headGen)
			throws IOException
	{
		OutputStream modelOut = null;
		ModelWrapper model = null;

		ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
		ObjectStream<ConstituentTreeSample> sampleStream = new ConstituentTreeSampleStream(lineStream,
				headGen);
		
		model = ChunkerForParserME.train("zh", sampleStream, params, contextGen);
		
		// 模型的写出，文本文件
		modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
		model.serialize(modelOut);

		modelOut.close();

	}

	/**
	 * 得到最好的K个chunk树
	 * 
	 * @param k
	 *            结果数目
	 * @param posTree
	 *            词性标注树
	 * @param ac
	 * @return
	 */
	public List<List<HeadTreeNode>> tagKChunk(int k, List<List<HeadTreeNode>> posTree, Object[] ac)
	{	
		ChunkSequence[] sequences = this.model.bestSequencesForChunk(k, posTree, ac,
				contextGenerator, sequenceValidator);
		
		List<List<HeadTreeNode>> chunkTrees = new ArrayList<List<HeadTreeNode>>();
		for (int i = 0; i < sequences.length; i++)
		{
			int label = sequences[i].getLabel();
			List<HeadTreeNode> tree = new ArrayList<>();
			List<HeadTreeNode> tempTree = posTree.get(label);
			List<String> outcomes = sequences[i].getOutcomes();
			
			for (int j = 0; j < outcomes.size(); j++)
			{
				HeadTreeNode outNode = new HeadTreeNode(outcomes.get(j));
				outNode.addChild(tempTree.get(j));
				tree.add(outNode);
			}
			
			chunkTrees.add(tree);
		}
		
		List<List<HeadTreeNode>> combineChunkTree = new ArrayList<List<HeadTreeNode>>();
		for (int i = 0; i < chunkTrees.size(); i++)
		{
			List<HeadTreeNode> node = ChunkTreeCombineUtil.combineToChunkTrees(chunkTrees.get(i), headGenerator);
			combineChunkTree.add(node);
		}
		
		return combineChunkTree;
	}

	/**
	 * 得到最好的K个chunk树
	 * 
	 * @param k
	 *            结果数目
	 * @param posTree
	 *            词性标注树
	 * @param ac
	 * @return
	 */
	public List<HeadTreeNode> tagChunk(List<List<HeadTreeNode>> posTree, Object[] ac)
	{
		List<List<HeadTreeNode>> chunkTree = tagKChunk(1, posTree, null);
		return chunkTree.get(0);
	}

	/**
	 * 得到chunk子树
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(String[] words, String[] poses)
	{

		List<HeadTreeNode> posTree = new ArrayList<>();
		for (int i = 0; i < poses.length && i < words.length; i++)
		{
			HeadTreeNode pos = new HeadTreeNode(poses[i]);
			pos.addChild(new HeadTreeNode(words[i]));
			posTree.add(pos);
		}

		return chunkTree(posTree);
	}

	/**
	 * 得到chunk子树
	 * 
	 * @param wordsandposes
	 *            词语+词性组成的数组
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(String[] wordsandposes)
	{
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];
		for (int i = 0; i < wordsandposes.length; i++)
		{
			words[i] = wordsandposes[i].split("/")[0];
			poses[i] = wordsandposes[i].split("/")[1];
		}
		return chunkTree(words, poses);
	}

	/**
	 * 得到chunk子树
	 * 
	 * @param wordsandposes
	 *            词语+词性组成的句子
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(String wordsandposes)
	{
		String[] wordandpos = wordsandposes.split("\\s+");
		return chunkTree(wordandpos);
	}

	/**
	 * 得到chunk子树
	 * 
	 * @param posTree
	 *            pos子树
	 * @return
	 */
	@Override
	public List<HeadTreeNode> chunkTree(List<HeadTreeNode> posTree)
	{
		List<List<HeadTreeNode>> allposTree = new ArrayList<>();
		allposTree.add(posTree);
		return ChunkTreeCombineUtil.combineToChunkTrees(tagChunk(allposTree, null), headGenerator);
	}

	/**
	 * 得到chunk结果
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性
	 * @return
	 */
	@Override
	public String[] chunk(String[] words, String[] poses)
	{
		List<HeadTreeNode> posTree = new ArrayList<>();
		for (int i = 0; i < poses.length && i < words.length; i++)
		{
			HeadTreeNode pos = new HeadTreeNode(poses[i]);
			pos.addChild(new HeadTreeNode(words[i]));
			posTree.add(pos);
		}

		return chunk(posTree);
	}

	/**
	 * 得到chunk结果
	 * 
	 * @param wordsandposes
	 *            词语+词性组成数组
	 * @return
	 */
	@Override
	public String[] chunk(String[] wordsandposes)
	{
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];
		for (int i = 0; i < wordsandposes.length; i++)
		{
			words[i] = wordsandposes[i].split("/")[0];
			poses[i] = wordsandposes[i].split("/")[1];
		}
		return chunk(words, poses);
	}

	/**
	 * 得到chunk结果
	 * 
	 * @param wordsandposes
	 *            词语+词性组成的句子
	 * @return
	 */
	@Override
	public String[] chunk(String wordsandposes)
	{
		String[] wordandpos = wordsandposes.split("\\s+");
		return chunk(wordandpos);
	}

	/**
	 * 得到chunk结果
	 * 
	 * @param posTree
	 *            pos子树
	 * @return
	 */
	@Override
	public String[] chunk(List<HeadTreeNode> posTree)
	{
		List<List<HeadTreeNode>> allposTree = new ArrayList<>();
		allposTree.add(posTree);
		List<HeadTreeNode> chunkTree = tagChunk(allposTree, null);
		String[] wordandpos = new String[chunkTree.size()];
		String[] chunkTag = new String[chunkTree.size()];
		String[] output = new String[chunkTree.size()];
		int k = 0;
		int index = -1;
		for (int i = 0; i < chunkTree.size(); i++)
		{

			if (chunkTree.get(i).getNodeName().contains("start"))
			{
				chunkTag[k] = chunkTree.get(i).getNodeNameRightPart();
				wordandpos[k] += getWordAndPos(chunkTree.get(i).getFirstChild());
				for (int j = i + 1; j < chunkTag.length; j++)
				{
					if (chunkTree.get(j).getNodeName().contains("start"))
					{
						break;
					}
					else if (chunkTree.get(j).getNodeName().contains("join"))
					{
						wordandpos[k] += getWordAndPos(chunkTree.get(j).getFirstChild());
						index = j;
					}
				}
				i = index;
				output[k] = "[" + wordandpos[k] + "]" + chunkTag[k] + " ";
				k++;
			}
			else if (chunkTree.get(i).getNodeName().contains("other"))
			{
				chunkTag[k] = "o";
				wordandpos[k] += getWordAndPos(chunkTree.get(i).getFirstChild());
				for (int j = i + 1; j < chunkTag.length; j++)
				{
					if (chunkTree.get(j).getNodeName().contains("start"))
					{
						break;
					}
					else if (chunkTree.get(j).getNodeName().contains("join"))
					{
						wordandpos[k] += getWordAndPos(chunkTree.get(j).getFirstChild());
						index = j;
					}
				}
				i = index;
				output[k] = wordandpos[k] + " ";
				k++;
			}
		}
		return output;
	}

	private String getWordAndPos(HeadTreeNode tree)
	{
		String wordandpos = "";
		for (int i = 0; i < tree.getChildrenNum(); i++)
		{
			if (i == tree.getChildrenNum() - 1)
			{
				wordandpos += tree.getChild(i).getFirstChildName() + "/" + tree.getChildName(i);
			}
			else
			{
				wordandpos += tree.getChild(i).getFirstChildName() + "/" + tree.getChildName(i) + " ";
			}
		}
		return wordandpos;
	}
}
