package com.lc.nlp4han.dependency.tb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.dependency.DependencyParser;
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
		Configuration_ArcStandard conf_ArcStandard = Configuration_ArcStandard.initialConf(words, pos);
		String[] priorDecisions = new String[2 * (words.length - 1)];
		List<Event> events = new ArrayList<Event>();
		ActionType at;
		String strOfAType;

		int indexOfWord_S1;// 该单词在words中索引
		int indexOfWord_S2;
		int headIndexOfWord_S1;// 栈顶单词中心词在words中的索引
		int headIndexOfWord_S2;
		int indexOfConf = 0;
		int count = 0;
		while (!conf_ArcStandard.isFinalConf() && count < priorDecisions.length)
		{
			count++;
			String[] context = ((DependencyParseContextGeneratorConf_ArcStandard) pcg).getContext(conf_ArcStandard,
					priorDecisions, null);
			if (conf_ArcStandard.getStack().size() == 1
					&& conf_ArcStandard.getStack().peek().getWord().equals(DependencyParser.RootWord))
			{
				// S1,S2没关系。SHIFT
				at = new ActionType("null", "SHIFT");
//				System.out.println(conf_ArcStandard.toString() + "*****" + "goldAction =" + at.typeToString());
				strOfAType = at.typeToString();
				conf_ArcStandard.shift();
			}
			else
			{
				Vertice S1 = conf_ArcStandard.getStack().pop();
				Vertice S2 = conf_ArcStandard.getStack().peek();
				indexOfWord_S1 = S1.getIndexOfWord();// 该单词在words中索引
				indexOfWord_S2 = S2.getIndexOfWord();
				conf_ArcStandard.getStack().push(S1);
				if (conf_ArcStandard.getStack().size() != 2)
				{
					headIndexOfWord_S2 = Integer.parseInt(dependencyIndices[indexOfWord_S2 - 1]);
				}
				else
				{// S2是ROOTWORD
					headIndexOfWord_S2 = -1;
				}
				headIndexOfWord_S1 = Integer.parseInt(dependencyIndices[indexOfWord_S1 - 1]);

				if (indexOfWord_S1 == headIndexOfWord_S2)
				{// 左弧
					at = new ActionType(dependency[indexOfWord_S2 - 1], "LEFTARC_REDUCE");
//					System.out.println(conf_ArcStandard.toString() + "*****" + "goldAction =" + at.typeToString());
					strOfAType = at.typeToString();
					conf_ArcStandard.addArc(new Arc(dependency[indexOfWord_S2 - 1], S1, S2));
					conf_ArcStandard.reduce(at);
				}
				else if (indexOfWord_S2 == headIndexOfWord_S1)
				{
					if (conf_ArcStandard.canReduce(dependencyIndices))
					{// RIGHTARC_REDUCE
						at = new ActionType(dependency[indexOfWord_S1 - 1], "RIGHTARC_REDUCE");
//						System.out.println(conf_ArcStandard.toString() + "*****" + "goldAction =" + at.typeToString());
						strOfAType = at.typeToString();
						conf_ArcStandard.addArc(new Arc(dependency[indexOfWord_S1 - 1], S2, S1));
						conf_ArcStandard.reduce(at);
					}
					else
					{
						// S1还有依存词在buffer中，先SHIFT
						at = new ActionType("null", "SHIFT");
//						System.out.println(conf_ArcStandard.toString() + "*****" + "goldAction =" + at.typeToString());
						strOfAType = at.typeToString();
						conf_ArcStandard.shift();
					}
				}
				else
				{
					// S1,S2没关系。SHIFT
					at = new ActionType("null", "SHIFT");
//					System.out.println(conf_ArcStandard.toString() + "*****" + "goldAction =" + at.typeToString());
					strOfAType = at.typeToString();
					conf_ArcStandard.shift();
				}
			}
			priorDecisions[indexOfConf] = strOfAType;
			indexOfConf++;

			Event event = new Event(strOfAType, context);
			events.add(event);

		}
		 if(conf_ArcStandard.getArcs().size() == dependency.length) {
//				 System.out.println(TBDepTree.getSample(conf_ArcStandard.getArcs(),
//				 words,pos).toCoNLLString());
				 }else {
				 return new ArrayList<Event>();
				 }
		return events;
	}
}
