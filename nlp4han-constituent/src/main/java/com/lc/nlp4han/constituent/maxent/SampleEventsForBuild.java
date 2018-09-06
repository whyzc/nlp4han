package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 为build模型生成事件
 * 
 * @author 王馨苇
 *
 */
public class SampleEventsForBuild extends AbstractEventStream<ConstituentTreeSample>
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
	public SampleEventsForBuild(ObjectStream<ConstituentTreeSample> samples, ParserContextGenerator generator)
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
		List<List<HeadTreeNode>> buildAndCheckTree = sample.getBuildAndCheckTree();
		String[][] ac = sample.getAdditionalContext();
		List<Event> events = generateEvents(words, buildAndCheckTree, actions, ac);
		return events.iterator();
	}

	/**
	 * 事件生成
	 * 
	 * @param words
	 *            词语序列
	 * @param buildAndCheckTree
	 *            buildAndCheck得到的子树
	 * @param actions
	 *            动作序列
	 * @param ac
	 * @return
	 */
	private List<Event> generateEvents(List<String> words, List<List<HeadTreeNode>> buildAndCheckTree,
			List<String> actions, String[][] ac)
	{
		List<Event> events = new ArrayList<Event>(actions.size());
		// buildAndCheck
		// 两个变量i j i控制第几个list j控制list中的第几个
		int j = 0;
		for (int i = 2 * words.size(); i < actions.size(); i = i + 2)
		{
			if (i >= actions.size() - 1)
			{
				System.out.println("cg index error");
				continue;
			}

			String[] buildContext = generator.getContextForBuild(j, buildAndCheckTree.get(i - 2 * words.size()),
					actions, ac);
			events.add(new Event(actions.get(i), buildContext));

			if (actions.get(i + 1).equals("yes"))
			{
				int record = j - 1;
				for (int k = record; k >= 0; k--)
				{
					if (buildAndCheckTree.get(i - 2 * words.size()).get(k).getNodeName().split("_")[0].equals("start"))
					{
						j = k;
						break;
					}
				}
			}
			else if (actions.get(i + 1).equals("no"))
			{
				j++;
			}
		}
		return events;
	}
}
