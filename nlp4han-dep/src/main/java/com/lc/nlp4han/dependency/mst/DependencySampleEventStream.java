package com.lc.nlp4han.dependency.mst;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.dependency.DependencyParseContextGenerator;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 训练模型所需要的事件流
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencySampleEventStream extends AbstractEventStream<DependencySample>
{

	// 上下文产生器
	private DependencyParseContextGenerator pcg;

	/**
	 * 构造
	 * 
	 * @param samples
	 *            样本流
	 * @param pcg
	 *            特征
	 */
	public DependencySampleEventStream(ObjectStream<DependencySample> samples, DependencyParseContextGenerator pcg)
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
	 * 产生每个词对应的事件
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
	private List<Event> generateEvents(String[] words, String[] pos, String[] dependency, String[] dependencyWords,
			String[] dependencyIndices, String[][] ac)
	{
		List<Event> events = new ArrayList<Event>(words.length);

		int lenLeft, lenRight;
		if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT == -1)
		{
			lenLeft = -words.length;
			lenRight = words.length;
		}
		else if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT != -1)
		{
			lenLeft = -words.length;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}
		else if (DependencyParseContextGeneratorConf.LEFT != -1 && DependencyParseContextGeneratorConf.RIGHT == -1)
		{
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = words.length;
		}
		else
		{
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}

		int i = 1, j = 0;
		while (i < words.length)
		{
			while (j - i <= lenRight && j - i >= lenLeft && j < words.length)
			{
				if (i != j)
				{
					String[] context = pcg.getContext(i, j, words, pos, ac);
					if (dependencyWords[i - 1].equals(words[j]) || dependencyWords[i - 1] == words[j])
					{
						// i到j的关系：有关系统一设置成dependency
						events.add(new Event(dependency[i - 1], context));
					}
					else
					{
						// i到j的关系：没有关系统一设置成null
						events.add(new Event("null", context));
					}
				}
				
				j++;
			}
			i++;
			j = 0;
		}
		return events;
	}

}
