package com.lc.nlp4han.chunk.word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 基于词的事件生成类
 */
public class ChunkAnalysisWordSampleEvent extends AbstractEventStream<AbstractChunkAnalysisSample>
{

	/**
	 * 上下文生成器
	 */
	private ChunkAnalysisContextGenerator contextgenerator;

	/**
	 * 构造方法
	 * 
	 * @param sampleStream
	 *            样本流
	 * @param contextgenerator
	 *            上下文生成器
	 */
	public ChunkAnalysisWordSampleEvent(ObjectStream<AbstractChunkAnalysisSample> sampleStream,
			ChunkAnalysisContextGenerator contextgenerator)
	{
		super(sampleStream);
		this.contextgenerator = contextgenerator;
	}

	@Override
	protected Iterator<Event> createEvents(AbstractChunkAnalysisSample sample)
	{
		String[] words = sample.getTokens();
		String[] tags = sample.getTags();
		List<Event> events = generateEvents(words, tags);
		return events.iterator();
	}

	/**
	 * 产生事件列表
	 * 
	 * @param words
	 *            词语数组
	 * @param chunkTags
	 *            组块标记数组
	 * @param aditionalContext
	 *            其他上下文信息
	 * @return 事件列表
	 */
	private List<Event> generateEvents(String[] words, String[] tags)
	{
		List<Event> events = new ArrayList<Event>(words.length);
		for (int i = 0; i < words.length; i++)
		{
			String[] context = contextgenerator.getContext(i, words, tags, null);
			events.add(new Event(tags[i], context));
		}

		return events;
	}
}
