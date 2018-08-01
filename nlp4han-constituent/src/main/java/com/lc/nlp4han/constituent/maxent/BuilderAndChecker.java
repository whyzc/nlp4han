package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 
 * @author 王馨苇
 *
 */
public interface BuilderAndChecker<T extends TreeNode>
{

	/**
	 * 得到句法树
	 * 
	 * @param chunkTree
	 *            chunk子树序列
	 * @return
	 */
	T syntacticTree(List<T> chunkTree);

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
	T syntacticTree(String[] words, String[] poses, String[] chunkTag);

	/**
	 * 得到句法树
	 * 
	 * @param sentence
	 *            由词语词性标记和chunk标记组成的句子
	 * @return
	 */
	T syntacticTree(String sentence);

	/**
	 * 得到最好的K个句法树
	 * 
	 * @param k
	 *            最好的K个结果
	 * @param chunkTree
	 *            chunk子树序列
	 * @return
	 */
	List<T> syntacticTree(int k, List<T> chunkTree);

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
	List<T> syntacticTree(int k, String[] words, String[] poses, String[] chunkTag);

	/**
	 * 得到最好的K个句法树
	 * 
	 * @param k
	 *            最好的K个结果
	 * @param sentence
	 *            由词语词性标记和chunk标记组成的句子
	 * @return
	 */
	List<T> syntacticTree(int k, String sentence);
}
