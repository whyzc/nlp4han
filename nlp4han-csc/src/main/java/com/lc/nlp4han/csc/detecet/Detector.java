package com.lc.nlp4han.csc.detecet;

import com.lc.nlp4han.csc.util.Sentence;

/**
 * 句子错误检测器接口
 */
public interface Detector
{

	/**
	 * 返回给定句子的查错的结果
	 * 
	 * @param sentence
	 *            待查错的句子
	 * @return 查错的结果
	 */
	public DetectResult detect(Sentence sentence);

	/**
	 * 返回给定句子的最优的k个查错的结果
	 * 
	 * @param sentence
	 *            待查错的句子
	 * @param k
	 *            返回的候选结果数
	 * @return 查错的k个结果
	 */
	public DetectResult detect(Sentence sentence, int k);
}
