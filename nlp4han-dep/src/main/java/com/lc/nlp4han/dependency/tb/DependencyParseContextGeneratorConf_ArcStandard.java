package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author 作者
 * @version 创建时间：2018年8月19日 上午9:51:19 类说明
 */
public class DependencyParseContextGeneratorConf_ArcStandard extends DependencyParseContextGenerator
{

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

	public void init(Properties config)
	{
		super.init(config);

		pre_head_1_wset = config.getProperty("feature.pre_head_1_w", "false").equals("true");
		pre_head_1_tset = config.getProperty("feature.pre_head_1_t", "false").equals("true");

		pre_head_2_wset = config.getProperty("feature.pre_head_2_w", "false").equals("true");
		pre_head_2_tset = config.getProperty("feature.pre_head_2_t", "false").equals("true");

		pre_action_1set = config.getProperty("feature.pre_action_1", "false").equals("true");
		pre_action_2set = config.getProperty("feature.pre_action_2", "false").equals("true");

		current_s1depword_wset = config.getProperty("feature.current_s1depword_w", "false").equals("true");
		current_s1depword_tset = config.getProperty("feature.current_s1depword_t", "false").equals("true");

		current_s2depword_wset = config.getProperty("feature.current_s2depword_w", "false").equals("true");
		current_s2depword_tset = config.getProperty("feature.current_s2depword_t", "false").equals("true");

		current_s1depset = config.getProperty("feature.current_s1dep", "false").equals("true");
		current_s2depset = config.getProperty("feature.current_s2dep", "false").equals("true");
	}

	@Override
	public String[] getContext(int index, String[] wordpos, String[] priorDecisions, Object[] additionalContext)
	{
		Configuration_ArcStandard conf = new Configuration_ArcStandard();
		conf.generateConfByActions(wordpos, priorDecisions);
		return getContext(conf, priorDecisions, additionalContext);
	}

}
