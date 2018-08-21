package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DependencyParseContextGeneratorConf_ArcEager implements DependencyParseContextGenerator
{
	// 定义变量控制feature的使用
	// 一个单词特征
	private boolean s1wset;
	private boolean s1tset;
	private boolean s1wtset;

	private boolean s2wset;
	private boolean s2tset;
	private boolean s2wtset;

	private boolean s3wset;
	private boolean s3tset;
	private boolean s3wtset;

	private boolean b1wset;
	private boolean b1tset;
	private boolean b1wtset;

	private boolean b2wset;
	private boolean b2tset;
	private boolean b2wtset;

	private boolean b3wset;
	private boolean b3tset;
	private boolean b3wtset;

	// 两个单词特征
	private boolean s1w_b1tset;
	private boolean s1t_b1wset;
	private boolean s1w_b1wset;
	private boolean s1t_b1tset;
	private boolean s1t_b1wtset;
	private boolean s1w_b1wtset;
	private boolean s1wt_b1wset;
	private boolean s1wt_b1tset;
	private boolean s1wt_b1wtset;

	private boolean b1w_b2tset;
	private boolean b1t_b2wset;
	private boolean b1w_b2wset;
	private boolean b1t_b2tset;
	private boolean b1t_b2wtset;
	private boolean b1w_b2wtset;
	private boolean b1wt_b2wset;
	private boolean b1wt_b2tset;
	private boolean b1wt_b2wtset;

	private boolean s1w_s2tset;
	private boolean s1t_s2wset;
	private boolean s1w_s2wset;
	private boolean s1t_s2tset;
	private boolean s1t_s2wtset;
	private boolean s1w_s2wtset;
	private boolean s1wt_s2wset;
	private boolean s1wt_s2tset;
	private boolean s1wt_s2wtset;

	// 三个单词特征
	private boolean s1w_s2w_b1wset;
	private boolean s1w_s2w_b1tset;
	private boolean s1t_s2t_b1wset;
	private boolean s1t_s2t_b1tset;
	private boolean s1wt_s2wt_b1wtset;

	private boolean s1w_b1w_b2wset;
	private boolean s1w_b1t_b2tset;
	private boolean s1t_b1w_b2wset;
	private boolean s1t_b1t_b2tset;
	private boolean s1wt_b1wt_b2wtset;

	// 四个单词特征
	private boolean s1w_s2w_b1w_b2wset;
	private boolean s1t_s2t_b1t_b2tset;
	private boolean s1w_s2w_b1t_b2tset;
	private boolean s1t_s2t_b1w_b2wset;
	private boolean s1wt_s2wt_b1wt_b2wtset;

	// 动态特征
	private boolean s1_h_wset;
	private boolean s1_h_tset;
	private boolean pre_action_1set;
	private boolean pre_action_2set;

	/**
	 * 无参构造
	 * 
	 * @throws IOException
	 *             IO异常
	 */
	public DependencyParseContextGeneratorConf_ArcEager() throws IOException
	{
		Properties featureConf = new Properties();
		InputStream featureStream = DependencyParseContextGeneratorConf_ArcEager.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/dependency/tbfeature.properties");
		featureConf.load(featureStream);

		init(featureConf);
	}

	/**
	 * 有参构造
	 * 
	 * @param config
	 *            配置文件
	 */
	public DependencyParseContextGeneratorConf_ArcEager(Properties config)
	{
		init(config);
	}

	private void init(Properties config)
	{

		s1wset = config.getProperty("feature.s1w", "true").equals("true");
		s1tset = config.getProperty("feature.s1t", "true").equals("true");
		s1wtset = config.getProperty("feature.s1wt", "true").equals("true");
		s2wset = config.getProperty("feature.s2w", "true").equals("true");
		s2tset = config.getProperty("feature.s2t", "true").equals("true");
		s2wtset = config.getProperty("feature.s2wt", "true").equals("true");
		s3wset = config.getProperty("feature.s3w", "true").equals("true");
		s3tset = config.getProperty("feature.s3t", "true").equals("true");
		s3wtset = config.getProperty("feature.s3wt", "true").equals("true");
		b1wset = config.getProperty("feature.b1w", "true").equals("true");
		b1tset = config.getProperty("feature.b1t", "true").equals("true");
		b1wtset = config.getProperty("feature.b1wt", "true").equals("true");
		b2wset = config.getProperty("feature.b2w", "true").equals("true");
		b2tset = config.getProperty("feature.b2t", "true").equals("true");
		b2wtset = config.getProperty("feature.b2wt", "true").equals("true");
		b3wset = config.getProperty("feature.b3w", "true").equals("true");
		b3tset = config.getProperty("feature.b3t", "true").equals("true");
		b3wtset = config.getProperty("feature.b3wt", "true").equals("true");

		s1w_b1tset = config.getProperty("feature.s1w_b1t", "true").equals("true");
		s1t_b1wset = config.getProperty("feature.s1t_b1w", "true").equals("true");
		s1w_b1wset = config.getProperty("feature.s1w_b1w", "true").equals("true");
		s1t_b1tset = config.getProperty("feature.s1t_b1t", "true").equals("true");
		s1t_b1wtset = config.getProperty("feature.s1t_b1wt", "true").equals("true");
		s1w_b1wtset = config.getProperty("feature.s1w_b1wt", "true").equals("true");
		s1wt_b1wset = config.getProperty("feature.s1wt_b1w", "true").equals("true");
		s1wt_b1tset = config.getProperty("feature.s1wt_b1t", "true").equals("true");
		s1wt_b1wtset = config.getProperty("feature.s1wt_b1wt", "true").equals("true");

		b1w_b2tset = config.getProperty("feature.b1w_b2t", "true").equals("true");
		b1t_b2wset = config.getProperty("feature.b1t_b2w", "true").equals("true");
		b1w_b2wset = config.getProperty("feature.b1w_b2w", "true").equals("true");
		b1t_b2tset = config.getProperty("feature.b1t_b2t", "true").equals("true");
		b1t_b2wtset = config.getProperty("feature.b1t_b2wt", "true").equals("true");
		b1w_b2wtset = config.getProperty("feature.b1w_b2wt", "true").equals("true");
		b1wt_b2wset = config.getProperty("feature.b1wt_b2w", "true").equals("true");
		b1wt_b2tset = config.getProperty("feature.b1wt_b2t", "true").equals("true");
		b1wt_b2wtset = config.getProperty("feature.b1wt_b2wt", "true").equals("true");

		s1w_s2tset = config.getProperty("feature.s1w_s2t", "true").equals("true");
		s1t_s2wset = config.getProperty("feature.s1t_s2w", "true").equals("true");
		s1w_s2wset = config.getProperty("feature.s1w_s2w", "true").equals("true");
		s1t_s2tset = config.getProperty("feature.s1t_s2t", "true").equals("true");
		s1t_s2wtset = config.getProperty("feature.s1t_s2wt", "true").equals("true");
		s1w_s2wtset = config.getProperty("feature.s1w_s2wt", "true").equals("true");
		s1wt_s2wset = config.getProperty("feature.s1wt_s2w", "true").equals("true");
		s1wt_s2tset = config.getProperty("feature.s1wt_s2t", "true").equals("true");
		s1wt_s2wtset = config.getProperty("feature.s1wt_s2wt", "true").equals("true");

		s1w_s2w_b1wset = config.getProperty("feature.s1w_s2w_b1w", "true").equals("true");
		s1w_s2w_b1tset = config.getProperty("feature.s1w_s2w_b1t", "true").equals("true");
		s1t_s2t_b1wset = config.getProperty("feature.s1t_s2t_b1w", "true").equals("true");
		s1t_s2t_b1tset = config.getProperty("feature.s1t_s2t_b1t", "true").equals("true");
		s1wt_s2wt_b1wtset = config.getProperty("feature.s1wt_s2wt_b1wt", "true").equals("true");

		s1w_b1w_b2wset = config.getProperty("feature.s1w_b1w_b2w", "true").equals("true");
		s1w_b1t_b2tset = config.getProperty("feature.s1w_b1t_b2t", "true").equals("true");
		s1t_b1w_b2wset = config.getProperty("feature.s1t_b1w_b2w", "true").equals("true");
		s1t_b1t_b2tset = config.getProperty("feature.s1t_b1t_b2t", "true").equals("true");
		s1wt_b1wt_b2wtset = config.getProperty("feature.s1wt_b1wt_b2wt", "true").equals("true");

		s1w_s2w_b1w_b2wset = config.getProperty("feature.s1w_s2w_b1w_b2w", "true").equals("true");
		s1t_s2t_b1t_b2tset = config.getProperty("feature.s1t_s2t_b1t_b2t", "true").equals("true");
		s1w_s2w_b1t_b2tset = config.getProperty("feature.s1w_s2w_b1t_b2t", "true").equals("true");
		s1t_s2t_b1w_b2wset = config.getProperty("feature.s1t_s2t_b1w_b2w", "true").equals("true");
		s1wt_s2wt_b1wt_b2wtset = config.getProperty("feature.s1wt_s2wt_b1wt_b2wt", "true").equals("true");

		// 动态特征
		s1_h_wset = config.getProperty("feature.s1_h_w", "true").equals("true");
		s1_h_tset = config.getProperty("feature.s1_h_t", "true").equals("true");
		pre_action_1set = config.getProperty("feature.pre_action_1", "true").equals("true");
		pre_action_2set = config.getProperty("feature.pre_action_2", "true").equals("true");

	}

	@Override
	public String[] getContext(int index, String[] wordpos, String[] priorDecisions, Object[] additionalContext)
	{
		Configuration_ArcEager conf = new Configuration_ArcEager().generateConfByActions(wordpos, priorDecisions);
		return getContext(conf, priorDecisions, additionalContext);
	}

	public String[] getContext(Configuration_ArcEager conf, String[] priorDecisions, Object[] additionalContext)
	{

		String s1w, s1t, s2w, s2t, s3w, s3t, b1w, b1t, b2w, b2t, b3w, b3t;
		s1w = s1t = s2w = s2t = s3w = s3t = b1w = b1t = b2w = b2t = b3w = b3t = null;

		String s1_h_w, s1_h_t, pre_action_1, pre_action_2;
		s1_h_w = s1_h_t = pre_action_1 = pre_action_2 = null;
		List<String> features = new ArrayList<String>();
		ArrayDeque<Vertice> stack = conf.getStack();
		List<Vertice> wordsBuffer = conf.getWordsBuffer();

		if (conf.getStack().size() != 1)
		{
			for (Arc arc : conf.getArcs())
			{
				if (arc.getDependent() == conf.getStack().peek())
				{
					s1_h_w = arc.getHead().getWord();
					s1_h_t = arc.getDependent().getPos();
					break;
				}
			}
		}

		if (priorDecisions.length >= 1)
		{
			pre_action_1 = priorDecisions[priorDecisions.length - 1];
			if (priorDecisions.length >= 2)
				pre_action_2 = priorDecisions[priorDecisions.length - 2];
		}

		s1w = stack.peek().getWord();
		s1t = stack.peek().getPos();

		if (stack.size() >= 2)
		{
			Vertice vertice = stack.pop();
			s2w = stack.peek().getWord();
			s2t = stack.peek().getPos();
			stack.push(vertice);

			if (stack.size() >= 3)
			{
				Vertice v1 = stack.pop();
				Vertice v2 = stack.pop();
				s3w = stack.peek().getWord();
				s3t = stack.peek().getPos();
				stack.push(v2);
				stack.push(v1);
			}
		}

		if (wordsBuffer.size() >= 1)
		{
			b1w = wordsBuffer.get(0).getWord();
			b1t = wordsBuffer.get(0).getPos();
			if (wordsBuffer.size() >= 2)
			{
				b2w = wordsBuffer.get(1).getWord();
				b2t = wordsBuffer.get(1).getPos();
				if (wordsBuffer.size() >= 3)
				{
					b3w = wordsBuffer.get(2).getWord();
					b3t = wordsBuffer.get(2).getPos();
				}
			}
		}

		if (s1_h_wset && s1_h_w != null)
		{
			features.add("s1_h_w=" + s1_h_w);
		}
		if (s1_h_tset && s1_h_t != null)
		{
			features.add("s1_h_t=" + s1_h_t);
		}

		if (pre_action_1set && pre_action_1 != null)
		{
			features.add("pre_action_1=" + pre_action_1);
		}
		
		if (pre_action_2set && pre_action_2 != null)
		{
			features.add("pre_action_2=" + pre_action_2);
		}
		
		
		
		if (s1wset)
			features.add("s1w=" + s1w);
		if (s1tset)
			features.add("s1t=" + s1t);

		if (s2wset && s2w != null)
			features.add("s2w=" + s2w);
		if (s2tset && s2t != null)
			features.add("s2t=" + s2t);

		if (s3wset && s3w != null)
			features.add("s3w=" + s3w);
		if (s3tset && s3t != null)
			features.add("s3t=" + s3t);

		if (b1wset && b1w != null)
			features.add("b1w=" + b1w);
		if (b1tset && b1t != null)
			features.add("b1t=" + b1t);

		if (b2wset && b2w != null)
			features.add("b2w=" + b2w);
		if (b2tset && b2t != null)
			features.add("b2t=" + b2t);

		if (b3wset && b3w != null)
			features.add("b3w=" + b3w);
		if (b3tset && b3t != null)
			features.add("b3t=" + b3t);

		if (s1wtset)
			features.add("s1wt=" + s1w + s1t);
		if (s2wtset && s2w != null && s2t != null)
			features.add("s2wt=" + s2w + s2t);
		if (s3wtset && s3w != null && s3t != null)
			features.add("s3wt=" + s3w + s3t);

		if (b1wtset && b1w != null && b1t != null)
			features.add("b1wt=" + b1w + b1t);
		if (b2wtset && b2w != null && b2t != null)
			features.add("b2wt=" + b2w + b2t);
		if (b3wtset && b3w != null && b3t != null)
			features.add("b3wt=" + b3w + b3t);

		if (s1w_b1tset && b1t != null)
			features.add("s1w_b1t=" + s1w + b1t);
		if (s1t_b1wset && b1w != null)
			features.add("s1w_b1t=" + s1t + b1w);
		if (s1w_b1wset && b1w != null)
			features.add("s1w_b1w=" + s1w + b1w);
		if (s1t_b1tset && b1t != null)
			features.add("s1t_b1t=" + s1t + b1t);
		if (s1t_b1wtset && b1t != null && b1w != null)
			features.add("s1t_b1wt=" + s1t + b1w + b1t);
		if (s1w_b1wtset && b1t != null && b1w != null)
			features.add("s1w_b1wt=" + s1w + b1w + b1t);
		if (s1wt_b1wset && b1w != null)
			features.add("s1wt_b1w=" + s1w + s1t + b1w);
		if (s1wt_b1tset && b1t != null)
			features.add("s1wt_b1t=" + s1w + s1t + b1t);
		if (s1wt_b1wtset && b1t != null && b1w != null)
			features.add("s1wt_b1wt=" + s1w + s1t + b1w + b1t);

		if (b1w_b2tset && b1w != null && b2t != null)
			features.add("b1w_b2t=" + b1w + b2t);
		if (b1t_b2wset && b1t != null && b2w != null)
			features.add("b1t_b2w=" + b1t + b2w);
		if (b1w_b2wset && b1w != null && b2w != null)
			features.add("b1w_b2w=" + b1w + b2w);
		if (b1t_b2tset && b1t != null && b2t != null)
			features.add("b1t_b2t=" + b1t + b2t);
		if (b1t_b2wtset && b1t != null && b2w != null && b2t != null)
			features.add("b1t_b2wt=" + b1t + b2w + b2t);
		if (b1w_b2wtset && b1w != null && b2w != null && b2t != null)
			features.add("b1w_b2wt=" + b1w + b2w + b2t);
		if (b1wt_b2wset && b1w != null && b1t != null && b2w != null)
			features.add("b1wt_b2w=" + b1w + b1t + b2w);
		if (b1wt_b2tset && b1w != null && b1t != null && b2t != null)
			features.add("b1wt_b2t=" + b1w + b1t + b2t);
		if (b1wt_b2wtset && b1w != null && b1t != null && b2w != null && b2t != null)
			features.add("b1wt_b2wt=" + b1w + b1t + b2w + b2t);

		if (s1w_s2tset && s2t != null)
			features.add("s1w_s2t=" + s1w + s2t);
		if (s1t_s2wset && s2w != null)
			features.add("s1t_s2w=" + s1t + s2w);
		if (s1w_s2wset && s2w != null)
			features.add("s1w_s2w=" + s1w + s2w);
		if (s1t_s2tset && s2t != null)
			features.add("s1t_s2t=" + s1t + s2t);
		if (s1t_s2wtset && s2w != null && s2t != null)
			features.add("s1t_s2wt=" + s1t + s2w + s2t);
		if (s1w_s2wtset && s2w != null && s2t != null)
			features.add("s1w_s2wt=" + s1w + s2w + s2t);
		if (s1wt_s2wset && s2w != null)
			features.add("s1wt_s2w=" + s1w + s1t + s2w);
		if (s1wt_s2tset && s2t != null)
			features.add("s1wt_s2t=" + s1w + s1t + s2t);
		if (s1wt_s2wtset && s2w != null && s2t != null)
			features.add("s1wt_s2wt=" + s1w + s1t + s2w + s2t);

		if (s1w_s2w_b1wset && s2w != null && b1w != null)
			features.add("s1w_s2w_b1w=" + s1w + s2w + b1w);
		if (s1w_s2w_b1tset && s2w != null && b1t != null)
			features.add("s1w_s2w_b1t=" + s1w + s2w + b1t);
		if (s1t_s2t_b1wset && s2t != null && b1w != null)
			features.add("s1t_s2t_b1w=" + s1t + s2t + b1w);
		if (s1t_s2t_b1tset && b1t != null)
			features.add("s1t_s2t_b1t=" + s1t + s2t + b1t);
		if (s1wt_s2wt_b1wtset && s2w != null && s2t != null && b1w != null && b1t != null)
			features.add("s1wt_s2wt_b1wt=" + s1w + s1t + s2w + s2t + b1w + b1t);

		if (s1w_b1w_b2wset && b1w != null && b2w != null)
			features.add("s1w_b1w_b2w=" + s1w + b1w + b2w);
		if (s1w_b1t_b2tset && b1t != null && b2t != null)
			features.add("s1w_b1t_b2t=" + s1w + b1t + b2t);
		if (s1t_b1w_b2wset && b1w != null && b2w != null)
			features.add("s1t_b1w_b2w=" + s1t + b1w + b2w);
		if (s1t_b1t_b2tset && b1t != null && b2t != null)
			features.add("s1t_b1t_b2t=" + s1t + b1t + b2t);
		if (s1wt_b1wt_b2wtset && b1w != null && b1t != null && b2w != null && b2t != null)
			features.add("s1wt_b1wt_b2wt=" + s1w + s1t + b1w + b1t + b2w + b2t);

		if (s1w_s2w_b1w_b2wset && s2w != null && b1w != null && b2w != null)
			features.add("s1w_s2w_b1w_b2w=" + s1w + s2w + b1w + b2w);
		if (s1t_s2t_b1t_b2tset && s2t != null && b1t != null && b2t != null)
			features.add("s1t_s2t_b1t_b2t=" + s1t + s2t + b1t + b2t);
		if (s1w_s2w_b1t_b2tset && s2w != null && b1t != null && b2t != null)
			features.add("s1w_s2w_b1t_b2t=" + s1w + s2w + b1t + b2t);
		if (s1t_s2t_b1w_b2wset && s2t != null && b1w != null && b2w != null)
			features.add("s1t_s2t_b1w_b2w=" + s1t + s2t + b1w + b2w);
		if (s1wt_s2wt_b1wt_b2wtset && s2w != null && s2t != null && b1w != null && b1t != null && b2w != null
				&& b2t != null)
			features.add("s1wt_s2wt_b1wt_b2wt=" + s1w + s1t + s2w + s2t + b1w + b1t + b2w + b2t);

		return features.toArray(new String[features.size()]);
	}

}
