package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.ModelWrapper;

/**
 * 成分树分析器
 * 
 * 参考文献:
 * Ratnaparkhi A. Learning to Parse Natural Language with Maximum Entropy Models[J]. Machine Learning, 1999: 151-175.
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ConstituentParserME implements ConstituentParser
{

	private POSTaggerForParser<HeadTreeNode> postagger;
	private ChunkerForParserME chunktagger;
	private BuilderAndCheckerME buildAndChecktagger;

	public ConstituentParserME(POSTaggerForParser<HeadTreeNode> postagger, ModelWrapper chunkmodel,
			ModelWrapper buildmodel, ModelWrapper checkmodel,
			ParserContextGenerator contextGen, AbstractHeadGenerator aghw)
	{
		this.postagger = postagger;
		this.chunktagger = new ChunkerForParserME(chunkmodel, contextGen, aghw);
		this.buildAndChecktagger = new BuilderAndCheckerME(buildmodel, checkmodel, contextGen, aghw);
	}

	/**
	 * 得到最好的成分树
	 * 
	 * @param words
	 *            分词序列
	 * @param poses
	 *            词性标记
	 * @return
	 */
	@Override
	public ConstituentTree parse(String[] words, String[] poses)
	{
		String[][] kposes = new String[1][poses.length];
		for (int i = 0; i < kposes.length; i++)
		{
			for (int j = 0; j < kposes[i].length; j++)
			{
				kposes[i][j] = poses[j];
			}
		}

		List<List<HeadTreeNode>> postree = HeadTreeNode.toPosTree(words, kposes);
		List<HeadTreeNode> chunkTree = chunktagger.tagChunk(postree, null);
		List<List<HeadTreeNode>> kchunkTree = new ArrayList<>();
		kchunkTree.add(chunkTree);

		HeadTreeNode headTreeNode = buildAndChecktagger.tagBuildAndCheck(kchunkTree, null);

		ConstituentTree constituent = new ConstituentTree();
		constituent.setRoot(headTreeNode);
		return constituent;
	}

//	/**
//	 * 得到最好的成分树
//	 * 
//	 * @param words
//	 *            分词序列
//	 * @return
//	 */
//	@Override
//	public ConstituentTree parse(String[] words)
//	{
//		List<HeadTreeNode> postree = postagger.posTree(words);
//		
//		List<List<HeadTreeNode>> postrees = new ArrayList<>();
//		postrees.add(postree);
//		
//		List<HeadTreeNode> chunkTree = chunktagger.tagChunk(postrees, null);
//		List<List<HeadTreeNode>> kchunkTree = new ArrayList<>();
//		kchunkTree.add(chunkTree);
//		
//		HeadTreeNode headTreeNode = buildAndChecktagger.tagBuildAndCheck(kchunkTree, null);
//		
//		ConstituentTree constituent = new ConstituentTree();
//		constituent.setRoot(headTreeNode);
//		
//		return constituent;
//	}

	/**
	 * 得到最好的K个成分树
	 * 
	 * @param words
	 *            词语
	 * @param poses
	 *            词性标记
	 * @param k
	 *            最好的K个结果
	 * @return
	 */
	@Override
	public ConstituentTree[] parse(String[] words, String[] poses, int k)
	{
		String[][] kposes = new String[1][poses.length];
		for (int i = 0; i < kposes.length; i++)
		{
			for (int j = 0; j < kposes[i].length; j++)
			{
				kposes[i][j] = poses[j];
			}
		}
		List<List<HeadTreeNode>> postree = HeadTreeNode.toPosTree(words, kposes);
		List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(k, postree, null);
		List<HeadTreeNode> headTreeNode = buildAndChecktagger.tagBuildAndCheck(k, chunkTree, null);
		List<ConstituentTree> constituent = new ArrayList<>();
		for (int i = 0; i < headTreeNode.size(); i++)
		{
			ConstituentTree con = new ConstituentTree();
			con.setRoot(headTreeNode.get(i));
			constituent.add(con);
		}
		return constituent.toArray(new ConstituentTree[constituent.size()]);
	}

//	/**
//	 * 得到最好的K个成分树
//	 * 
//	 * @param words
//	 *            分词序列
//	 * @param k
//	 *            最好的K个结果
//	 * @return
//	 */
//	@Override
//	public ConstituentTree[] parse(String[] words, int k)
//	{
//		List<List<HeadTreeNode>> postree = postagger.posTree(words, k);
//		List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(k, postree, null);
//		List<HeadTreeNode> headTreeNode = buildAndChecktagger.tagBuildAndCheck(k, chunkTree, null);
//		List<ConstituentTree> constituent = new ArrayList<>();
//		for (int i = 0; i < headTreeNode.size(); i++)
//		{
//			ConstituentTree con = new ConstituentTree();
//			con.setRoot(headTreeNode.get(i));
//			constituent.add(con);
//		}
//		return constituent.toArray(new ConstituentTree[constituent.size()]);
//	}
}
