package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 为chunk模型生成事件
 * 
 * @author 王馨苇
 *
 */
public class SampleEventsForChunk extends AbstractEventStream<ConstituentTreeSample>
{

	private ParserContextGenerator generator;

	/**
	 * 构造
	 * 
	 * @param samples
	 *            样本流
	 * @param generator
	 *            上下文产生器
	 */
	public SampleEventsForChunk(ObjectStream<ConstituentTreeSample> samples,
			ParserContextGenerator generator)
	{
		super(samples);
		this.generator = generator;
	}

	/**
	 * 生成事件
	 */
	@Override
	protected Iterator<Event> createEvents(ConstituentTreeSample sample)
	{
		List<String> words = sample.getWords();
		List<String> actions = sample.getActions();
		List<HeadTreeNode> chunkTree = sample.getChunkTree();
		String[][] ac = sample.getAdditionalContext();
		
//		System.out.println("Generating chunk events for " + sample);
		List<Event> events = generateEvents(words, chunkTree, actions, ac);
		
		return events.iterator();
	}

	/**
	 * 事件生成
	 * 
	 * @param words
	 *            词语序列
	 * @param chunkTree
	 *            chunk得到的子树
	 * @param actions
	 *            动作序列
	 * @param ac
	 * @return
	 */
	private List<Event> generateEvents(List<String> words, List<HeadTreeNode> chunkTree, List<String> actions,
			String[][] ac)
	{
		List<Event> events = new ArrayList<Event>(actions.size());

		for (int i = words.size(); i < 2 * words.size(); i++)
		{
			if((i - words.size()) >= chunkTree.size())
			{
				System.out.println("cg index error");
				continue;
			}
			
			String[] context = generator.getContextForChunk(i - words.size(), chunkTree, actions, ac);
			
			events.add(new Event(actions.get(i), context));
		}

		return events;
	}
}
