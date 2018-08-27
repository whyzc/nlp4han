package com.lc.nlp4han.dependency.tb;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.dependency.tb.DependencyParseContextGenerator;
import com.lc.nlp4han.dependency.DependencyParser;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * @author hp
 *
 */
public class DependencySampleEventStream_ArcEager extends AbstractEventStream<DependencySample>
{

	// 上下文产生器
	private static DependencyParseContextGenerator pcg;
	private int errCount = 0;

	/**
	 * 构造
	 * 
	 * @param samples
	 *            样本流
	 * @param pcg
	 *            特征
	 */
	public DependencySampleEventStream_ArcEager(ObjectStream<DependencySample> samples, DependencyParseContextGenerator pcg)
	{

		super(samples);
		DependencySampleEventStream_ArcEager.pcg = pcg;
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
		if (events.isEmpty() && sample != null)
		{
			errCount++;
//			try
//			{
//				FileOutputStream s;
//				s = new FileOutputStream("C:\\Users\\hp\\Desktop\\erroSample\\erroSample" + errCount + ".txt");
//				OutputStreamWriter ow = new OutputStreamWriter(s, "utf-8");
//				BufferedWriter fr = new BufferedWriter(ow);
//				fr.write(sample.toCoNLLString());
//				fr.close();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
		}
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
	public static List<Event> generateEvents(String[] words, String[] pos, String[] dependency, String[] dependencyWords,
			String[] dependencyIndices, String[][] ac)
	{

		if (words.length == 0)
			return new ArrayList<Event>(words.length);
		Configuration_ArcEager conf_ArcEager = Configuration_ArcEager.initialConf(words, pos);
		String[] priorDecisions = new String[2 * (words.length - 1) ];

		List<Event> events = new ArrayList<Event>();
		ActionType at;
		String strOfAType;
		int indexOfWord_S1;// 该单词在words中索引
		int indexOfWord_B1;
		int headIndexOfWord_S1;// 栈顶单词中心词在words中的索引
		int headIndexOfWord_B1;

		int indexOfConf = 0;
		while (!conf_ArcEager.isFinalConf())
		{// buffer为空是终止配置
			// System.out.println(conf_ArcEager.toString());
			//因为这个方法不是继承超类的方法，故适用强制转换
			String[] context = ((DependencyParseContextGeneratorConf_ArcEager)pcg).getContext(conf_ArcEager, priorDecisions, null);

			if (conf_ArcEager.getWordsBuffer().size() == 0 && conf_ArcEager.getStack().size() > 1)
			{
				// Reduce
				at = new ActionType("null", "REDUCE");
//				System.out.println(conf_ArcEager.toString() + "*****" + "goldAction =" + at.typeToString());
				strOfAType = at.typeToString();
				conf_ArcEager.reduce();
			}
			else
			{
				indexOfWord_S1 = conf_ArcEager.getStack().peek().getIndexOfWord();// 该单词在words中索引
				indexOfWord_B1 = conf_ArcEager.getWordsBuffer().get(0).getIndexOfWord();
				if (conf_ArcEager.getStack().size() != 1)
				{
					headIndexOfWord_S1 = Integer.parseInt(dependencyIndices[indexOfWord_S1 - 1]);// 栈顶单词中心词在words中的索引
				}
				else
				{// 防止数组越界
					if (!conf_ArcEager.getStack().peek().getWord().equals(DependencyParser.RootWord))
						System.err.println("不是gold句子。");
					headIndexOfWord_S1 = -1;// 栈顶单词中心词在words中的索引
				}
				headIndexOfWord_B1 = Integer.parseInt(dependencyIndices[indexOfWord_B1 - 1]);

				if (indexOfWord_B1 == headIndexOfWord_S1)
				{// 左弧
					at = new ActionType(dependency[indexOfWord_S1 - 1], "LEFTARC_REDUCE");
//					System.out.println(conf_ArcEager.toString() + "*****" + "goldAction =" + at.typeToString());
					strOfAType = at.typeToString();
					conf_ArcEager.addArc(new Arc(dependency[indexOfWord_S1 - 1], conf_ArcEager.getWordsBuffer().get(0),
							conf_ArcEager.getStack().peek()));
					conf_ArcEager.reduce();
				}
				else if (indexOfWord_S1 == headIndexOfWord_B1)
				{// 右弧
					if (conf_ArcEager.getStack().size() == 1)
					{
						if (!conf_ArcEager.getStack().peek().getWord().equals(DependencyParser.RootWord))
							System.err.println("不是gold句子。");
						at = new ActionType(DependencyParser.RootDep, "RIGHTARC_SHIFT");
					}
					else
					{
						at = new ActionType(dependency[indexOfWord_B1 - 1], "RIGHTARC_SHIFT");
					}
//					System.out.println(conf_ArcEager.toString() + "*****" + "goldAction =" + at.typeToString());
					strOfAType = at.typeToString();
					conf_ArcEager.addArc(
							new Arc(strOfAType, conf_ArcEager.getStack().peek(), conf_ArcEager.getWordsBuffer().get(0)));
					conf_ArcEager.shift();

				}
				else if (conf_ArcEager.canReduce(dependencyIndices))
				{
					// Reduce
					at = new ActionType("null", "REDUCE");
//					System.out.println(conf_ArcEager.toString() + "*****" + "goldAction =" + at.typeToString());
					strOfAType = at.typeToString();
					conf_ArcEager.reduce();

				}
				else
				{
					// Shift
					at = new ActionType("null", "SHIFT");
//					System.out.println(conf_ArcEager.toString() + "*****" + "goldAction =" + at.typeToString());
					strOfAType = at.typeToString();
					conf_ArcEager.shift();

				}
			}
			priorDecisions[indexOfConf] = strOfAType;
			indexOfConf++;

			Event event = new Event(strOfAType, context);
			events.add(event);
		}
		// if(conf_ArcEager.getArcs().size() != dependency.length) {
		// System.out.println(TBDepTree.getSample(conf_ArcEager.getArcs(),
		// words,pos).toCoNLLString());
		// }else {
		// return new ArrayList<Event>();
		// }
		return events;
	}
}