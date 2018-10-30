package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * 短语结构树样本类
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ConstituentTreeSample
{

	private List<String> words = new ArrayList<String>();
	private List<String> poses = new ArrayList<String>();

	private List<HeadTreeNode> posTree;
	private List<HeadTreeNode> chunkTree;
	private List<List<HeadTreeNode>> buildAndCheckTree;
	private List<String> actions;

	private String[][] addtionalContext;

	public ConstituentTreeSample(List<HeadTreeNode> posTree, List<HeadTreeNode> chunkTree,
			List<List<HeadTreeNode>> buildAndCheckTree, List<String> actions)
	{
		this(posTree, chunkTree, buildAndCheckTree, actions, null);
	}

	public ConstituentTreeSample(List<HeadTreeNode> posTree, List<HeadTreeNode> chunkTree,
			List<List<HeadTreeNode>> buildAndCheckTree, List<String> actions, String[][] additionalContext)
	{
		posTreeToWordsAndPoses(posTree);
		this.posTree = Collections.unmodifiableList(posTree);
		this.chunkTree = Collections.unmodifiableList(chunkTree);
		this.buildAndCheckTree = Collections.unmodifiableList(buildAndCheckTree);
		this.actions = Collections.unmodifiableList(actions);
		String[][] ac;
		if (additionalContext != null)
		{
			ac = new String[additionalContext.length][];
			for (int i = 0; i < additionalContext.length; i++)
			{
				ac[i] = new String[additionalContext[i].length];
				System.arraycopy(additionalContext[i], 0, ac[i], 0, additionalContext[i].length);
			}
		}
		else
		{
			ac = null;
		}
		this.addtionalContext = ac;
	}

	/**
	 * 将得到的词性标注子树转成字符和字符标记
	 * 
	 * @param posTree
	 */
	public void posTreeToWordsAndPoses(List<HeadTreeNode> posTree)
	{
		for (int i = 0; i < posTree.size(); i++)
		{
			String word = posTree.get(i).getChildren().get(0).getNodeName();
			poses.add(posTree.get(i).getNodeName());
			words.add(word);
		}
	}

	/**
	 * 获取词语
	 * 
	 * @return
	 */
	public List<String> getWords()
	{
		return this.words;
	}

	/**
	 * 获取词性
	 * 
	 * @return
	 */
	public List<String> getPoses()
	{
		return this.poses;
	}

	/**
	 * pos操作得到的子树序列
	 * 
	 * @return
	 */
	public List<HeadTreeNode> getPosTree()
	{
		return this.posTree;
	}

	/**
	 * chunk操作得到的子树序列
	 * 
	 * @return
	 */
	public List<HeadTreeNode> getChunkTree()
	{
		return this.chunkTree;
	}

	/**
	 * buildAndCheck操作得到的子树序列
	 * 
	 * @return
	 */
	public List<List<HeadTreeNode>> getBuildAndCheckTree()
	{
		return this.buildAndCheckTree;
	}

	/**
	 * 动作序列
	 * 
	 * @return
	 */
	public List<String> getActions()
	{
		return this.actions;
	}

	// 第一阶段动作序列
	public List<String> getFirstActions()
	{
		return actions.subList(0, words.size());
	}

	// 第二阶段动作序列
	public List<String> getSecondActions()
	{
		return actions.subList(words.size(), 2 * words.size());
	}

	// 第上阶段动作序列
	public List<String> getThirdActions()
	{
		return actions.subList(2 * words.size(), actions.size());
	}

	/**
	 * 获取额外的上下文信息
	 * 
	 * @return
	 */
	public String[][] getAdditionalContext()
	{
		return this.addtionalContext;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof ConstituentTreeSample)
		{
			ConstituentTreeSample a = (ConstituentTreeSample) obj;
			return Arrays.equals(getActions().stream().toArray(), a.getActions().stream().toArray());
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return "words=" + words + ", actions=" + actions;
	}

	/**
	 * 转成样本类
	 * 
	 * @param words
	 *            词语序列
	 * @param actions
	 *            动作序列
	 * @return
	 */
	public static TreeNode toTree(List<String> words, List<String> actions)
	{
		TreeNode tree = ActionsToTree.actionsToTree(words, actions);
		return tree;
	}
}
