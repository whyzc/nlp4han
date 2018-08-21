package com.lc.nlp4han.dependency.tb;

import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * @author 作者
 * @version 创建时间：2018年8月19日 上午9:55:22 类说明
 */
public class DependencySampleEventStream_ArcStandard extends AbstractEventStream<DependencySample>
{
	// 上下文产生器
	private static DependencyParseContextGenerator pcg;

	public DependencySampleEventStream_ArcStandard(ObjectStream<DependencySample> samples,
			DependencyParseContextGenerator pcg)
	{
		super(samples);
		this.pcg = pcg;
	}

	/**
	 * 根据依存样本流创建事件
	 * 
	 * @param sample
	 *            依存样本
	 */
	@Override
	protected Iterator<Event> createEvents(DependencySample sample)
	{
		String[] words = sample.getWords();
		String[] pos = sample.getPos();
		String[] dependency = sample.getDependency();
		String[] dependencyWords = sample.getDependencyWords();
		String[] dependencyIndices = sample.getDependencyIndices();
		String[][] ac = sample.getAditionalContext();
		List<Event> events = generateEvents(words, pos, dependency, dependencyWords, dependencyIndices, ac);
		return events.iterator();
	}

	/**
	 * 产生对应的事件
	 * 
	 * @param words
	 *            词语
	 * @param pos
	 *            词性
	 * @param dependency
	 *            依存关系
	 * @param dependencyWords
	 *            依存词
	 * @param dependencyIndices
	 *            依存词的下标
	 * @param ac
	 *            额外的信息
	 * @return 事件列表
	 */
	public static List<Event> generateEvents(String[] words, String[] pos, String[] dependency,
			String[] dependencyWords, String[] dependencyIndices, String[][] ac)
	{
		return null;
	}
}
