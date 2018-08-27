package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author 作者
 * @version 创建时间：2018年8月19日 上午9:51:19 类说明
 */
public class DependencyParseContextGeneratorConf_ArcStandard implements DependencyParseContextGenerator
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

	private boolean s4wset;
	private boolean s4tset;
	private boolean s4wtset;

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

	private boolean s3w_s2tset;
	private boolean s3t_s2wset;
	private boolean s3w_s2wset;
	private boolean s3t_s2tset;
	private boolean s3t_s2wtset;
	private boolean s3w_s2wtset;
	private boolean s3wt_s2wset;
	private boolean s3wt_s2tset;
	private boolean s3wt_s2wtset;

	private boolean s2w_s1tset;
	private boolean s2t_s1wset;
	private boolean s2w_s1wset;
	private boolean s2t_s1tset;
	private boolean s2t_s1wtset;
	private boolean s2w_s1wtset;
	private boolean s2wt_s1wset;
	private boolean s2wt_s1tset;
	private boolean s2wt_s1wtset;

	private boolean s1w_b1tset;
	private boolean s1t_b1wset;
	private boolean s1w_b1wset;
	private boolean s1t_b1tset;
	private boolean s1t_b1wtset;
	private boolean s1w_b1wtset;
	private boolean s1wt_b1wset;
	private boolean s1wt_b1tset;
	private boolean s1wt_b1wtset;

	// private boolean b1w_b2tset;
	// private boolean b1t_b2wset;
	// private boolean b1w_b2wset;
	// private boolean b1t_b2tset;
	// private boolean b1t_b2wtset;
	// private boolean b1w_b2wtset;
	// private boolean b1wt_b2wset;
	// private boolean b1wt_b2tset;
	// private boolean b1wt_b2wtset;

	// 三个单词特征
	private boolean s3w_s2w_s1wset;
	private boolean s3w_s2w_s1tset;
	private boolean s3t_s2t_s1wset;
	private boolean s3t_s2t_s1tset;
	private boolean s3wt_s2wt_s1wtset;

	private boolean s2w_s1w_b1wset;
	private boolean s2w_s1w_b1tset;
	private boolean s2t_s1t_b1wset;
	private boolean s2t_s1t_b1tset;
	private boolean s2wt_s1wt_b1wtset;

	// 四个单词特征
	private boolean s3w_s2w_s1w_b1wset;
	private boolean s3t_s2t_s1t_b1tset;
	private boolean s3w_s2w_s1t_b1tset;
	private boolean s3t_s2t_s1w_b1wset;
	private boolean s3wt_s2wt_s1wt_b1wtset;

	// 动态特征
	private boolean pre_head_1_wset;// 之前一步预测的中心词
	private boolean pre_head_1_tset;

	private boolean pre_head_2_wset;// 之前第二步预测的中心词
	private boolean pre_head_2_tset;

	private boolean pre_action_1set;
	private boolean pre_action_2set;

	private boolean current_s1depword_wset;// s1当前所有依存词的w
	private boolean current_s1depword_tset;// s1当前所有依存词的t

	private boolean current_s2depword_wset;
	private boolean current_s2depword_tset;

	private boolean current_s1depset;// s1与其当前所有依存词之间的依存关系
	private boolean current_s2depset;

	/**
	 * 无参构造
	 * 
	 * @throws IOException
	 *             IO异常
	 */
	public DependencyParseContextGeneratorConf_ArcStandard() throws IOException
	{
		Properties featureConf = new Properties();
		InputStream featureStream = DependencyParseContextGeneratorConf_ArcStandard.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/dependency/feature_arcstandard.properties");
		featureConf.load(featureStream);

		init(featureConf);
	}

	/**
	 * 有参构造
	 * 
	 * @param config
	 *            配置文件
	 */
	public DependencyParseContextGeneratorConf_ArcStandard(Properties config)
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
		s4wset = config.getProperty("feature.s4w", "true").equals("true");
		s4tset = config.getProperty("feature.s4t", "true").equals("true");
		s4wtset = config.getProperty("feature.s4wt", "true").equals("true");

		b1wset = config.getProperty("feature.b1w", "true").equals("true");
		b1tset = config.getProperty("feature.b1t", "true").equals("true");
		b1wtset = config.getProperty("feature.b1wt", "true").equals("true");
		b2wset = config.getProperty("feature.b2w", "true").equals("true");
		b2tset = config.getProperty("feature.b2t", "true").equals("true");
		b2wtset = config.getProperty("feature.b2wt", "true").equals("true");
		b3wset = config.getProperty("feature.b3w", "true").equals("true");
		b3tset = config.getProperty("feature.b3t", "true").equals("true");
		b3wtset = config.getProperty("feature.b3wt", "true").equals("true");

		s3w_s2tset = config.getProperty("feature.s3w_s2t", "true").equals("true");
		s3t_s2wset = config.getProperty("feature.s3t_s2w", "true").equals("true");
		s3w_s2wset = config.getProperty("feature.s3w_s2w", "true").equals("true");
		s3t_s2tset = config.getProperty("feature.s3t_s2t", "true").equals("true");
		s3t_s2wtset = config.getProperty("feature.s3t_s2wt", "true").equals("true");
		s3w_s2wtset = config.getProperty("feature.s3w_s2wt", "true").equals("true");
		s3wt_s2wset = config.getProperty("feature.s3wt_s2w", "true").equals("true");
		s3wt_s2tset = config.getProperty("feature.s3wt_s2t", "true").equals("true");
		s3wt_s2wtset = config.getProperty("feature.s3wt_s2wt", "true").equals("true");

		s2w_s1tset = config.getProperty("feature.s2w_s1t", "true").equals("true");
		s2t_s1wset = config.getProperty("feature.s2t_s1w", "true").equals("true");
		s2w_s1wset = config.getProperty("feature.s2w_s1w", "true").equals("true");
		s2t_s1tset = config.getProperty("feature.s2t_s1t", "true").equals("true");
		s2t_s1wtset = config.getProperty("feature.s2t_s1wt", "true").equals("true");
		s2w_s1wtset = config.getProperty("feature.s2w_s1wt", "true").equals("true");
		s2wt_s1wset = config.getProperty("feature.s2wt_s1w", "true").equals("true");
		s2wt_s1tset = config.getProperty("feature.s2wt_s1t", "true").equals("true");
		s2wt_s1wtset = config.getProperty("feature.s2wt_s1wt", "true").equals("true");

		s1w_b1tset = config.getProperty("feature.s1w_b1t", "true").equals("true");
		s1t_b1wset = config.getProperty("feature.s1t_b1w", "true").equals("true");
		s1w_b1wset = config.getProperty("feature.s1w_b1w", "true").equals("true");
		s1t_b1tset = config.getProperty("feature.s1t_b1t", "true").equals("true");
		s1t_b1wtset = config.getProperty("feature.s1t_b1wt", "true").equals("true");
		s1w_b1wtset = config.getProperty("feature.s1w_b1wt", "true").equals("true");
		s1wt_b1wset = config.getProperty("feature.s1wt_b1w", "true").equals("true");
		s1wt_b1tset = config.getProperty("feature.s1wt_b1t", "true").equals("true");
		s1wt_b1wtset = config.getProperty("feature.s1wt_b1wt", "true").equals("true");

		// b1w_b2tset = config.getProperty("feature.b1w_b2t", "true").equals("true");
		// b1t_b2wset = config.getProperty("feature.b1t_b2w", "true").equals("true");
		// b1w_b2wset = config.getProperty("feature.b1w_b2w", "true").equals("true");
		// b1t_b2tset = config.getProperty("feature.b1t_b2t", "true").equals("true");
		// b1t_b2wtset = config.getProperty("feature.b1t_b2wt", "true").equals("true");
		// b1w_b2wtset = config.getProperty("feature.b1w_b2wt", "true").equals("true");
		// b1wt_b2wset = config.getProperty("feature.b1wt_b2w", "true").equals("true");
		// b1wt_b2tset = config.getProperty("feature.b1wt_b2t", "true").equals("true");
		// b1wt_b2wtset = config.getProperty("feature.b1wt_b2wt",
		// "true").equals("true");

		s3w_s2w_s1wset = config.getProperty("feature.s3w_s2w_s1w", "true").equals("true");
		s3w_s2w_s1tset = config.getProperty("feature.s3w_s2w_s1t", "true").equals("true");
		s3t_s2t_s1wset = config.getProperty("feature.s3t_s2t_s1w", "true").equals("true");
		s3t_s2t_s1tset = config.getProperty("feature.s3t_s2t_s1t", "true").equals("true");
		s3wt_s2wt_s1wtset = config.getProperty("feature.s3wt_s2wt_s1wt", "true").equals("true");

		s2w_s1w_b1wset = config.getProperty("feature.s2w_s1w_b1w", "true").equals("true");
		s2w_s1w_b1tset = config.getProperty("feature.s2w_s1w_b1t", "true").equals("true");
		s2t_s1t_b1wset = config.getProperty("feature.s2t_s1t_b1w", "true").equals("true");
		s2t_s1t_b1tset = config.getProperty("feature.s2t_s1t_b1t", "true").equals("true");
		s2wt_s1wt_b1wtset = config.getProperty("feature.s2wt_s1wt_b1wt", "true").equals("true");

		s3w_s2w_s1w_b1wset = config.getProperty("feature.s3w_s2w_s1w_b1w", "true").equals("true");
		s3t_s2t_s1t_b1tset = config.getProperty("feature.s3t_s2t_s1t_b1t", "true").equals("true");
		s3w_s2w_s1t_b1tset = config.getProperty("feature.s3w_s2w_s1t_b1t", "true").equals("true");
		s3t_s2t_s1w_b1wset = config.getProperty("feature.s3t_s2t_s1w_b1w", "true").equals("true");
		s3wt_s2wt_s1wt_b1wtset = config.getProperty("feature.s3wt_s2wt_s1wt_b1wt", "true").equals("true");

		pre_head_1_wset = config.getProperty("feature.pre_head_1_w", "true").equals("true");
		pre_head_1_tset = config.getProperty("feature.pre_head_1_t", "true").equals("true");

		pre_head_2_wset = config.getProperty("feature.pre_head_2_w", "true").equals("true");
		pre_head_2_tset = config.getProperty("feature.pre_head_2_t", "true").equals("true");

		pre_action_1set = config.getProperty("feature.pre_action_1", "true").equals("true");
		pre_action_2set = config.getProperty("feature.pre_action_2", "true").equals("true");

		current_s1depword_wset = config.getProperty("current_s1depword_w", "true").equals("true");
		current_s1depword_tset = config.getProperty("current_s1depword_t", "true").equals("true");

		current_s2depword_wset = config.getProperty("current_s2depword_w", "true").equals("true");
		current_s2depword_tset = config.getProperty("current_s2depword_t", "true").equals("true");

		current_s1depset = config.getProperty("current_s1dep", "true").equals("true");
		current_s2depset = config.getProperty("current_s2dep", "true").equals("true");

	}

	@Override
	public String[] getContext(int index, String[] wordpos, String[] priorDecisions, Object[] additionalContext)
	{
		Configuration_ArcStandard conf = new Configuration_ArcStandard().generateConfByActions(wordpos, priorDecisions);
		return getContext(conf, priorDecisions, additionalContext);
	}

	public String[] getContext(Configuration_ArcStandard conf, String[] priorDecisions, Object[] additionalContext)
	{

		String s1w, s1t, s2w, s2t, s3w, s3t, s4w, s4t, b1w, b1t, b2w, b2t, b3w, b3t;
		s1w = s1t = s2w = s2t = s3w = s3t = s4w = s4t = b1w = b1t = b2w = b2t = b3w = b3t = null;
		String pre_action_1, pre_action_2, current_s1depword_w, current_s1depword_t, current_s2depword_w,
				current_s2depword_t, current_s1dep, current_s2dep;
		pre_action_1 = pre_action_2 = current_s1depword_w = current_s1depword_t = current_s2depword_w = current_s2depword_t = current_s1dep = current_s2dep = null;

		List<String> features = new ArrayList<String>();
		ArrayDeque<Vertice> stack = conf.getStack();
		List<Vertice> wordsBuffer = conf.getWordsBuffer();

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
			if (stack.size() >= 4)
			{
				Vertice v1 = stack.pop();
				Vertice v2 = stack.pop();
				Vertice v3 = stack.pop();
				s4w = stack.peek().getWord();
				s4t = stack.peek().getPos();
				stack.push(v3);
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

		if (s4wset && s4w != null)
			features.add("s4w=" + s4w);
		if (s4tset && s4t != null)
			features.add("s4t=" + s4t);

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
		if (s4wtset && s4w != null && s4t != null)
			features.add("s4wt=" + s4w + s4t);

		if (b1wtset && b1w != null && b1t != null)
			features.add("b1wt=" + b1w + b1t);
		if (b2wtset && b2w != null && b2t != null)
			features.add("b2wt=" + b2w + b2t);
		if (b3wtset && b3w != null && b3t != null)
			features.add("b3wt=" + b3w + b3t);

		if (s3w_s2tset && s2t != null && s3w != null)
			features.add("s3w_s2t=" + s3w + s2t);
		if (s3t_s2wset && s2w != null && s3t != null)
			features.add("s3t_s2w=" + s3t + s2w);
		if (s3w_s2wset && s2w != null && s3w != null)
			features.add("s3w_s2w=" + s3w + s2w);
		if (s3t_s2tset && s2t != null && s3t != null)
			features.add("s3t_s2t=" + s3t + s2t);
		if (s3t_s2wtset && s2w != null && s2t != null && s3t != null)
			features.add("s3t_s2wt=" + s3t + s2w + s2t);
		if (s3w_s2wtset && s2w != null && s2t != null && s3w != null)
			features.add("s1w_s2wt=" + s3w + s2w + s2t);
		if (s3wt_s2wset && s2w != null && s3w != null && s3t != null)
			features.add("s2wt_s2w=" + s3w + s3t + s2w);
		if (s3wt_s2tset && s2t != null && s3w != null && s3t != null)
			features.add("s3wt_s2t=" + s3w + s3t + s2t);
		if (s3wt_s2wtset && s2w != null && s2t != null && s3w != null && s3t != null)
			features.add("s3wt_s2wt=" + s3w + s3t + s2w + s2t);

		if (s2w_s1tset && s2w != null)
			features.add("s2w_s1t=" + s2w + s1t);
		if (s2t_s1wset && s2t != null)
			features.add("s2t_s1w=" + s2t + s1w);
		if (s2w_s1wset && s2w != null)
			features.add("s2w_s1w=" + s2w + s1w);
		if (s2t_s1tset && s2t != null)
			features.add("s2t_s1t=" + s2t + s1t);
		if (s2t_s1wtset && s2t != null)
			features.add("s2t_s1wt=" + s2t + s1w + s1t);
		if (s2w_s1wtset && s2w != null)
			features.add("s2w_s1wt=" + s2w + s1w + s1t);
		if (s2wt_s1wset && s2w != null && s2t != null)
			features.add("s2wt_s1w=" + s2w + s2t + s1w);
		if (s2wt_s1tset && s2w != null && s2t != null)
			features.add("s2wt_s1t=" + s2w + s2t + s1t);
		if (s2wt_s1wtset && s2w != null && s2t != null)
			features.add("s2wt_s1wt=" + s2w + s2t + s1w + s1t);

		if (s1w_b1tset && b1t != null)
			features.add("s1w_b1t=" + s1w + b1t);
		if (s1t_b1wset && b1w != null)
			features.add("s1t_b1w=" + s1t + b1w);
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

		if (s2w_s1w_b1wset && s2w != null && b1w != null)
			features.add("s2w_s1w_b1w=" + s2w + s1w + b1w);
		if (s2w_s1w_b1tset && s2w != null && b1t != null)
			features.add("s2w_s1w_b1t=" + s2w + s1w + b1t);
		if (s2t_s1t_b1wset && s2t != null && b1w != null)
			features.add("s2t_s1t_b1w=" + s2t + s1t + b1w);
		if (s2t_s1t_b1tset && b1t != null && s2t != null)
			features.add("s2t_s1t_b1t=" + s2t + s1t + b1t);
		if (s2wt_s1wt_b1wtset && s2w != null && s2t != null && b1w != null && b1t != null)
			features.add("s1wt_s2wt_b1wt=" + s2w + s2t + s1w + s1t + b1w + b1t);

		if (s3w_s2w_s1wset && s2w != null && s3w != null)
			features.add("s3w_s2w_s1w=" + s3w + s2w + s1w);
		if (s3w_s2w_s1tset && s2w != null && s3w != null)
			features.add("s3w_s2w_s1t=" + s3w + s2w + s1t);
		if (s3t_s2t_s1wset && s2t != null && s3t != null)
			features.add("s3t_s2t_s1w=" + s3t + s2t + s1w);
		if (s3t_s2t_s1tset && s3t != null && s2t != null)
			features.add("s3t_s2t_s1t=" + s3t + s2t + s1t);
		if (s3wt_s2wt_s1wtset && s2w != null && s2t != null && s3w != null && s3t != null)
			features.add("s3wt_s2wt_s1wt=" + s3w + s3t + s2w + s2t + s1w + s1t);

		if (s3w_s2w_s1w_b1wset && s2w != null && b1w != null && s3w != null)
			features.add("s3w_s2w_s1w_b1w=" + s3w + s2w + s1w + b1w);
		if (s3t_s2t_s1t_b1tset && s2t != null && b1t != null && s3t != null)
			features.add("s3t_s2t_s1t_b1t=" + s3t + s2t + s1t + b1t);
		if (s3w_s2w_s1t_b1tset && s2w != null && b1t != null && s3w != null)
			features.add("s3w_s2w_s1t_b1t=" + s3w + s2w + s1t + b1t);
		if (s3t_s2t_s1w_b1wset && s2t != null && b1w != null && s3t != null)
			features.add("s3t_s2t_s1w_b1w=" + s3t + s2t + s1w + b1w);
		if (s3wt_s2wt_s1wt_b1wtset && s2w != null && s2t != null && b1w != null && b1t != null && s3w != null
				&& s3t != null)
			features.add("s3wt_s2wt_s1wt_b1wt=" + s3w + s3t + s2w + s2t + s1w + s1t + b1w + b1t);

		int indexOfPriorDecision = -1;

		for (int index = 0; index < priorDecisions.length; index++)
		{
			if (priorDecisions[index] != null)
			{
				continue;
			}
			else
			{
				indexOfPriorDecision = index - 1;
				break;
			}
		}

		if (pre_head_1_wset && pre_head_1_tset && indexOfPriorDecision  >= 0
				&& !priorDecisions[indexOfPriorDecision].equals("null/SHIFT"))
		{
			Vertice pre_head_1 = conf.getStack().peek();
			features.add("pre_head_1_w=" + pre_head_1.getWord());
			features.add("pre_head_1_t=" + pre_head_1.getPos());
		}

		if (pre_head_2_wset && pre_head_2_tset && indexOfPriorDecision >= 1
				&& !priorDecisions[indexOfPriorDecision - 1].equals("null/SHIFT"))
		{
			Vertice pre_head_2;
			if (priorDecisions[indexOfPriorDecision].equals("null/SHIFT"))
			{
				Vertice tempVer = conf.getStack().pop();
				pre_head_2 = conf.getStack().peek();
				conf.getStack().push(tempVer);
			}
			else if (ActionType.toType(priorDecisions[indexOfPriorDecision]).getBaseAction()
					.equals("LEFTARC_REDUCE"))
			{
				pre_head_2 = conf.getStack().peek();
			}
			else
			{
				pre_head_2 = conf.getArcs().get(conf.getArcs().size() - 1).getDependent();
			}
			features.add("pre_head_2_w=" + pre_head_2.getWord());
			features.add("pre_head_2_t=" + pre_head_2.getPos());
		}

		if (pre_action_1set && indexOfPriorDecision >= 0)
		{
			pre_action_1 = priorDecisions[indexOfPriorDecision];
			features.add("pre_action_1=" + pre_action_1);
		}
		if (pre_action_2set && indexOfPriorDecision >= 1)
		{
			pre_action_2 = priorDecisions[indexOfPriorDecision - 1];
			features.add("pre_action_2=" + pre_action_2);
		}

		if (current_s1depword_wset && current_s1depword_tset && current_s1depset && conf.getArcs().size() != 0)
		{
			StringBuilder left_w = new StringBuilder();
			StringBuilder right_w = new StringBuilder();
			StringBuilder left_t = new StringBuilder();
			StringBuilder right_t = new StringBuilder();
			StringBuilder left_dep = new StringBuilder();
			StringBuilder right_dep = new StringBuilder();
			for (int i = 0; i < conf.getArcs().size(); i++)
			{
				if (conf.getArcs().get(i).getHead() == conf.getStack().peek())
				{
					if (conf.getArcs().get(i).getHead().getIndexOfWord() > conf.getArcs().get(i).getDependent()
							.getIndexOfWord())
					{
						left_w.append(conf.getArcs().get(i).getDependent().getWord());
						left_t.append(conf.getArcs().get(i).getDependent().getPos());
						left_dep.append(conf.getArcs().get(i).getRelation());
					}
					if (conf.getArcs().get(i).getHead().getIndexOfWord() < conf.getArcs().get(i).getDependent()
							.getIndexOfWord())
					{
						right_w.insert(0, conf.getArcs().get(i).getDependent().getWord());
						right_t.insert(0, conf.getArcs().get(i).getDependent().getPos());
						right_dep.insert(0, conf.getArcs().get(i).getRelation());
					}
				}
			}
			current_s1depword_w = left_w.toString() + right_w.toString();
			current_s1depword_t = left_t.toString() + right_t.toString();
			current_s1dep = left_dep.toString() + right_dep.toString();
			if (current_s1depword_w != null && current_s1depword_t != null)
			{
				features.add("current_s1depword_w=" + current_s1depword_w);
				features.add("current_s1depword_t=" + current_s1depword_t);
				features.add("current_s21dep=" + current_s1dep);
			}

		}

		if (current_s2depword_wset && current_s2depword_tset && current_s2depset && conf.getArcs().size() != 0)
		{
			StringBuilder left_w = new StringBuilder();
			StringBuilder right_w = new StringBuilder();
			StringBuilder left_t = new StringBuilder();
			StringBuilder right_t = new StringBuilder();
			StringBuilder left_dep = new StringBuilder();
			StringBuilder right_dep = new StringBuilder();
			Vertice v = conf.getStack().pop();
			for (int i = 0; i < conf.getArcs().size(); i++)
			{
				if (conf.getArcs().get(i).getHead() == conf.getStack().peek())
				{
					if (conf.getArcs().get(i).getHead().getIndexOfWord() > conf.getArcs().get(i).getDependent()
							.getIndexOfWord())
					{
						left_w.append(conf.getArcs().get(i).getDependent().getWord());
						left_t.append(conf.getArcs().get(i).getDependent().getPos());
						left_dep.append(conf.getArcs().get(i).getRelation());
					}
					if (conf.getArcs().get(i).getHead().getIndexOfWord() < conf.getArcs().get(i).getDependent()
							.getIndexOfWord())
					{
						right_w.insert(0, conf.getArcs().get(i).getDependent().getWord());
						right_t.insert(0, conf.getArcs().get(i).getDependent().getPos());
						right_dep.insert(0, conf.getArcs().get(i).getRelation());
					}
				}
			}
			conf.getStack().push(v);
			current_s2depword_w = left_w.toString() + right_w.toString();
			current_s2depword_t = left_t.toString() + right_t.toString();
			current_s2dep = left_dep.toString() + right_dep.toString();
			if (current_s2depword_w != null && current_s2depword_t != null)
			{
				features.add("current_s2depword_w=" + current_s2depword_w);
				features.add("current_s2depword_t=" + current_s2depword_t);
				features.add("current_s2dep=" + current_s2dep);
			}
		}
		return features.toArray(new String[features.size()]);
	}
}
