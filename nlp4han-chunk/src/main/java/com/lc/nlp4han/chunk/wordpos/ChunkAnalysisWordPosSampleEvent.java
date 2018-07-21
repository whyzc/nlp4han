package com.lc.nlp4han.chunk.wordpos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 基于词和词性的事件生成类
 */
public class ChunkAnalysisWordPosSampleEvent extends AbstractEventStream<AbstractChunkAnalysisSample>
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
	public ChunkAnalysisWordPosSampleEvent(ObjectStream<AbstractChunkAnalysisSample> sampleStream,
			ChunkAnalysisContextGenerator contextgenerator)
	{
		super(sampleStream);
		this.contextgenerator = contextgenerator;
	}

	@Override
	protected Iterator<Event> createEvents(AbstractChunkAnalysisSample sample)
	{
		ChunkAnalysisWordPosSample wordAndPOSSample = (ChunkAnalysisWordPosSample) sample;
		String[] words = wordAndPOSSample.getTokens();
		Object[] poses = wordAndPOSSample.getAditionalContext();
		String[] chunkTags = wordAndPOSSample.getTags();
		List<Event> events = generateEvents(words, poses, chunkTags);
		return events.iterator();
	}

	/**
	 * 产生事件列表
	 * 
	 * @param words
	 *            词语数组
	 * @param poses
	 *            词性数组
	 * @param chunkTags
	 *            组块标记数组
	 * @param aditionalContext
	 *            其他上下文信息
	 * @return 事件列表
	 */
	private List<Event> generateEvents(String[] words, Object[] poses, String[] chunkTags)
	{
		List<Event> events = new ArrayList<Event>(words.length);
		for (int i = 0; i < words.length; i++)
		{
			// 产生事件的部分
			String[] context = contextgenerator.getContext(i, words, chunkTags, poses);
			events.add(new Event(chunkTags[i], context));
		}

		return events;
	}
}
