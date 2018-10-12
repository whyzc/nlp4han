package com.lc.nlp4han.srl.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;

public class SRLWordPosContextGeneratorConf implements ChunkAnalysisContextGenerator
{

	// 原子特征模版
	private boolean w_1Set; // 前一个词
	private boolean w_2Set; // 前面第二个词
	private boolean w0Set; // 当前词语
	private boolean w1Set; // 后一个词
	private boolean w2Set; // 后面第二个词
	private boolean af0Set; // 当前词后缀
	private boolean pf0Set; // 当前词前缀

	private boolean p_2Set; // 前面第二个词的词性标注
	private boolean p_1Set; // 前一个词的词性标注
	private boolean p0Set; // 当前词性标注
	private boolean p1Set; // 后一个词的词性标注
	private boolean p2Set; // 后面第二个词的词性标注

	private boolean c_2Set; // 前面第二个词的组块标注
	private boolean c_1Set; // 前一个词的组块标注

	// 组合特征模版
	private boolean w_2w_1Set;
	private boolean w_1w0Set;
	private boolean w0w1Set;
	private boolean w1w2Set;
	private boolean w_1w1Set;
	private boolean w_2w_1w0Set;
	private boolean w_1w0w1Set;
	private boolean w0w1w2Set;

	private boolean p_2p_1Set;
	private boolean p_2p0Set;
	private boolean p_2p1Set;
	private boolean p_2p2Set;
	private boolean p_1p1Set;
	private boolean p_1p2Set;
	private boolean p_1p0Set;
	private boolean p0p1Set;
	private boolean p1p2Set;

	private boolean p_2p_1p0Set;
	private boolean p_2p0p1Set;

	private boolean c_2c_1Set;

	// 混合特征
	private boolean w_1p_2Set;
	private boolean w_1p1Set;
	private boolean w_1p2Set;
	private boolean w0p0Set;
	private boolean w1p_2Set;
	private boolean w1p_1Set;
	private boolean w1p2Set;

	private boolean w_2c_2Set;
	private boolean w_2c_1Set;
	private boolean w_1c_2Set;
	private boolean w_1c_1Set;
	private boolean w0c_2Set;
	private boolean w0c_1Set;
	private boolean w1c_2Set;
	private boolean w1c_1Set;
	private boolean w2c_2Set;
	private boolean w2c_1Set;

	private boolean p_2c_2Set;
	private boolean p_2c_1Set;
	private boolean p_1c_2Set;
	private boolean p_1c_1Set;
	private boolean p0c_2Set;
	private boolean p0c_1Set;
	private boolean p1c_2Set;
	private boolean p1c_1Set;
	private boolean p2c_2Set;
	private boolean p2c_1Set;

	private boolean w1p0p1Set;
	private boolean w0p_2Set;
	private boolean w0p_1Set;
	private boolean w0p1Set;
	private boolean w_1p_1Set;
	private boolean w_1p0Set;
	private boolean w1p0Set;
	private boolean p0p2Set;
	private boolean w1p1Set;
	private boolean w2p2Set;
	private boolean w0p2Set;

	private boolean p_1p0p1Set;
	private boolean p0p1c_1Set;
	private boolean p_1p0c_1Set;
	private boolean w1p_1p0Set;
	private boolean p_1p1p2Set;
	private boolean p0p1p2Set;
	private boolean w_2p_1p0Set;
	private boolean w0p_1p0Set;
	private boolean w0p0p1Set;
	private boolean w0w1p1Set;
	private boolean w0w2p2Set;
	private boolean w_1w0p_1Set;

	private boolean predicate;
	private boolean predicatepos;
	private boolean position;
	private boolean distance;
	private boolean prew_2;
	private boolean prew_1;
	private boolean prepos_2;
	private boolean prepos_1;
	private boolean aftw_1;
	private boolean aftw_2;
	private boolean aftpos_1;
	private boolean aftpos_2;

	/**
	 * 构造方法
	 * 
	 * @throws IOException
	 */
	public SRLWordPosContextGeneratorConf() throws IOException
	{
		Properties featureConf = new Properties();
		InputStream featureStream = SRLWordPosContextGeneratorConf.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/srl/chunkfeature.properties");
		featureConf.load(featureStream);

		init(featureConf);
	}

	/**
	 * 构造方法
	 * 
	 * @param properties
	 *            配置文件
	 */
	public SRLWordPosContextGeneratorConf(Properties properties)
	{
		init(properties);
	}

	/**
	 * 根据配置参数初始化特征模版
	 * 
	 * @param config
	 *            配置参数
	 */
	private void init(Properties config)
	{
		// 原子特征
		w_2Set = (config.getProperty("feature.w_2", "true").equals("true"));
		w_1Set = (config.getProperty("feature.w_1", "true").equals("true"));
		w0Set = (config.getProperty("feature.w0", "true").equals("true"));
		w1Set = (config.getProperty("feature.w1", "true").equals("true"));
		w2Set = (config.getProperty("feature.w2", "true").equals("true"));
		pf0Set = (config.getProperty("feature.pf0", "true").equals("true"));
		af0Set = (config.getProperty("feature.af0", "true").equals("true"));

		p_2Set = (config.getProperty("feature.p_2", "true").equals("true"));
		p_1Set = (config.getProperty("feature.p_1", "true").equals("true"));
		p0Set = (config.getProperty("feature.p0", "true").equals("true"));
		p1Set = (config.getProperty("feature.p1", "true").equals("true"));
		p2Set = (config.getProperty("feature.p2", "true").equals("true"));

		c_2Set = (config.getProperty("feature.c_2", "true").equals("true"));
		c_1Set = (config.getProperty("feature.c_1", "true").equals("true"));

		// 组合特征
		w_2w_1Set = (config.getProperty("feature.w_2w_1", "true").equals("true"));
		w_1w0Set = (config.getProperty("feature.w_1w0", "true").equals("true"));
		w0w1Set = (config.getProperty("feature.w0w1", "true").equals("true"));
		w1w2Set = (config.getProperty("feature.w1w2", "true").equals("true"));
		w_1w1Set = (config.getProperty("feature.w_1w1", "true").equals("true"));
		w_2w_1w0Set = (config.getProperty("feature.w_2w_1w0", "true").equals("true"));
		w_1w0w1Set = (config.getProperty("feature.w_1w0w1", "true").equals("true"));
		w0w1w2Set = (config.getProperty("feature.w0w1w2", "true").equals("true"));

		p_2p_1Set = (config.getProperty("feature.p_2p_1", "true").equals("true"));
		p_2p0Set = (config.getProperty("feature.p_2p0", "true").equals("true"));
		p_2p1Set = (config.getProperty("feature.p_2p1", "true").equals("true"));
		p_2p2Set = (config.getProperty("feature.p_2p2", "true").equals("true"));
		p_1p0Set = (config.getProperty("feature.p_1p0", "true").equals("true"));
		p_1p1Set = (config.getProperty("feature.p_1p1", "true").equals("true"));
		p_1p2Set = (config.getProperty("feature.p_1p2", "true").equals("true"));
		p0p1Set = (config.getProperty("feature.p0p1", "true").equals("true"));
		p0p2Set = (config.getProperty("feature.p0p2", "true").equals("true"));
		p1p2Set = (config.getProperty("feature.p1p2", "true").equals("true"));
		p_2p0p1Set = (config.getProperty("feature.p_2p0p1", "true").equals("true"));
		p_2p_1p0Set = (config.getProperty("feature.p_2p_1p0", "true").equals("true"));

		c_2c_1Set = (config.getProperty("feature.c_2c_1", "true").equals("true"));

		// 混合特征
		w_1p_2Set = (config.getProperty("feature.w_1p_2", "true").equals("true"));
		w_1p_1Set = (config.getProperty("feature.w_1p_1", "true").equals("true"));
		w_1p0Set = (config.getProperty("feature.w_1p0", "true").equals("true"));
		w_1p1Set = (config.getProperty("feature.w_1p1", "true").equals("true"));
		w_1p2Set = (config.getProperty("feature.w_1p2", "true").equals("true"));
		w0p_2Set = (config.getProperty("feature.w0p_2", "true").equals("true"));
		w0p_1Set = (config.getProperty("feature.w0p_1", "true").equals("true"));
		w0p0Set = (config.getProperty("feature.w0p0", "true").equals("true"));
		w0p1Set = (config.getProperty("feature.w0p1", "true").equals("true"));
		w0p2Set = (config.getProperty("feature.w0p2", "true").equals("true"));
		w1p_2Set = (config.getProperty("feature.w1p_2", "true").equals("true"));
		w1p_1Set = (config.getProperty("feature.w1p_1", "true").equals("true"));
		w1p0Set = (config.getProperty("feature.w1p0", "true").equals("true"));
		w1p1Set = (config.getProperty("feature.w1p1", "true").equals("true"));
		w1p2Set = (config.getProperty("feature.w1p2", "true").equals("true"));

		w_2c_2Set = (config.getProperty("feature.w_2c_2", "true").equals("true"));
		w_2c_1Set = (config.getProperty("feature.w_2c_1", "true").equals("true"));
		w_1c_2Set = (config.getProperty("feature.w_1c_2", "true").equals("true"));
		w_1c_1Set = (config.getProperty("feature.w_1c_1", "true").equals("true"));
		w0c_2Set = (config.getProperty("feature.w0c_2", "true").equals("true"));
		w0c_1Set = (config.getProperty("feature.w0c_1", "true").equals("true"));
		w1c_2Set = (config.getProperty("feature.w1c_2", "true").equals("true"));
		w1c_1Set = (config.getProperty("feature.w1c_1", "true").equals("true"));
		w2c_2Set = (config.getProperty("feature.w2c_2", "true").equals("true"));
		w2c_1Set = (config.getProperty("feature.w2c_1", "true").equals("true"));

		p_2c_2Set = (config.getProperty("feature.p_2c_2", "true").equals("true"));
		p_2c_1Set = (config.getProperty("feature.p_2c_1", "true").equals("true"));
		p_1c_2Set = (config.getProperty("feature.p_1c_2", "true").equals("true"));
		p_1c_1Set = (config.getProperty("feature.p_1c_1", "true").equals("true"));
		p0c_2Set = (config.getProperty("feature.p0c_2", "true").equals("true"));
		p0c_1Set = (config.getProperty("feature.p0c_1", "true").equals("true"));
		p1c_2Set = (config.getProperty("feature.p1c_2", "true").equals("true"));
		p1c_1Set = (config.getProperty("feature.p1c_1", "true").equals("true"));
		p2c_2Set = (config.getProperty("feature.p2c_2", "true").equals("true"));
		p2c_1Set = (config.getProperty("feature.p2c_1", "true").equals("true"));

		w1p0p1Set = (config.getProperty("feature.w1p0p1", "true").equals("true"));
		w2p2Set = (config.getProperty("feature.w2p2", "true").equals("true"));
		p_1p0p1Set = (config.getProperty("feature.p_1p0p1", "true").equals("true"));
		p0p1c_1Set = (config.getProperty("feature.p0p1c_1", "true").equals("true"));
		p_1p0c_1Set = (config.getProperty("feature.p_1p0c_1", "true").equals("true"));
		w1p_1p0Set = (config.getProperty("feature.w1p_1p0", "true").equals("true"));
		p_1p1p2Set = (config.getProperty("feature.p_1p1p2", "true").equals("true"));
		p0p1p2Set = (config.getProperty("feature.p0p1p2", "true").equals("true"));
		w_2p_1p0Set = (config.getProperty("feature.w_2p_1p0", "true").equals("true"));
		w0p_1p0Set = (config.getProperty("feature.w0p_1p0", "true").equals("true"));
		w0p0p1Set = (config.getProperty("feature.w0p0p1", "true").equals("true"));
		w0w1p1Set = (config.getProperty("feature.w0w1p1", "true").equals("true"));
		w0w2p2Set = (config.getProperty("feature.w0w2p2", "true").equals("true"));
		w_1w0p_1Set = (config.getProperty("feature.w_1w0p_1", "true").equals("true"));

		predicate = (config.getProperty("feature.predicate", "true").equals("true"));
		predicatepos = (config.getProperty("feature.predicatepos", "true").equals("true"));
		position = (config.getProperty("feature.position", "true").equals("true"));
		distance = (config.getProperty("feature.distance", "true").equals("true"));
		prew_2 = (config.getProperty("feature.prew_2", "true").equals("true"));
		prew_1 = (config.getProperty("feature.prew_1", "true").equals("true"));
		prepos_2 = (config.getProperty("feature.prepos_2", "true").equals("true"));
		prepos_1 = (config.getProperty("feature.prepos_1", "true").equals("true"));
		aftw_1 = (config.getProperty("feature.aftw_1", "true").equals("true"));
		aftw_2 = (config.getProperty("feature.aftw_2", "true").equals("true"));
		aftpos_1 = (config.getProperty("feature.aftpos_1", "true").equals("true"));
		aftpos_2 = (config.getProperty("feature.aftpos_2", "true").equals("true"));
	}

	@Override
	public String[] getContext(int index, String[] words, String[] chunkTags, Object[] poses)
	{
		String w_2, w_1, w0, w1, w2, p_2, p_1, p0, p1, p2, c_2, c_1, pf0, af0;
		w_2 = w_1 = w0 = w1 = w2 = p_2 = p_1 = p0 = p1 = p2 = c_2 = c_1 = pf0 = af0 = null;

		String pre, prepos, posi, dist, pw_2, pw_1, pp_2, pp_1, aw_1, aw_2, ap_1, ap_2;
		pre = prepos = posi = dist = pw_2 = pw_1 = pp_2 = pp_1 = aw_1 = aw_2 = ap_1 = ap_2 = null;

		int predicateIndex = 0;

		List<String> features = new ArrayList<String>();
		w0 = words[index];

		for (int i = 0; i < words.length; i++)
		{
			if (words[i].endsWith("-rel"))
			{
				pre = words[i];
				prepos = (String) poses[i];
				predicateIndex = i;
				break;
			}
		}

		if (predicateIndex - 1 >= 0)
		{
			pw_1 = words[predicateIndex - 1];
			pp_1 = (String) poses[predicateIndex - 1];
			if (predicateIndex - 2 >= 0)
			{
				pw_2 = words[predicateIndex - 2];
				pp_2 = (String) poses[predicateIndex - 2];
			}
		}

		if (words.length > predicateIndex + 1)
		{
			aw_1 = words[predicateIndex + 1];
			ap_1 = (String) poses[predicateIndex + 1];
			if (words.length > predicateIndex + 2)
			{
				aw_2 = words[predicateIndex + 2];
				ap_2 = (String) poses[predicateIndex + 2];
			}
		}

		if (index < predicateIndex)
		{
			posi = "before";
			dist = String.valueOf(predicateIndex - index);

		}
		else if (index > predicateIndex)
		{
			posi = "after";
			dist = String.valueOf(index - predicateIndex);
		}
		else
		{

			posi = "-";
			dist = String.valueOf(index - predicateIndex);
		}


		if (w0.length() > 1)
		{
			pf0 = w0.substring(0, 2);
			af0 = w0.substring(w0.length() - 2, w0.length());
		}
		else
			pf0 = af0 = w0;
		p0 = (String) poses[index];

		if (words.length > index + 1)
		{
			w1 = words[index + 1];
			p1 = (String) poses[index + 1];

			if (words.length > index + 2)
			{
				w2 = words[index + 2];
				p2 = (String) poses[index + 2];
			}
		}

		if (index - 1 >= 0)
		{
			w_1 = words[index - 1];
			p_1 = (String) poses[index - 1];
			c_1 = chunkTags[index - 1];

			if (index - 2 >= 0)
			{
				w_2 = words[index - 2];
				p_2 = (String) poses[index - 2];
				c_2 = chunkTags[index - 2];
			}
		}

		if (w0Set)
			features.add("w0=" + w0);
		if (af0Set)
			features.add("af0=" + af0);
		if (pf0Set)
			features.add("pf0=" + pf0);
		if (p0Set)
			features.add("p0=" + p0);
		if (w0p0Set)
			features.add("w0p0=" + w0 + p0);
		if (predicate)
			features.add("predicate=" + pre);
		if (predicatepos)
			features.add("predicatepos=" + prepos);
		if (position)
			features.add("position=" + posi);
		if (distance)
			features.add("distance=" + dist);
		if (prew_2)
			features.add("prew_2=" + pw_2);
		if (prew_1)
			features.add("prew_1=" + pw_1);
		if (prepos_2)
			features.add("prepos_2=" + pp_2);
		if (prepos_1)
			features.add("prepos_1" + pp_1);
		if (aftw_1)
			features.add("aftw_1=" + aw_1);
		if (aftw_2)
			features.add("aftw_2=" + aw_2);
		if (aftpos_1)
			features.add("ap_1=" + ap_1);
		if (aftpos_2)
			features.add("ap_2=" + ap_2);

		if (w_1 != null)
		{
			if (w_1Set)
				features.add("w_1=" + w_1);
			if (p_1Set)
				features.add("p_1=" + p_1);
			if (c_1Set)
				features.add("c_1=" + c_1);
			if (w_1w0Set)
				features.add("w_1w0=" + w_1 + w0);
			if (p_1c_1Set)
				features.add("p_1c_1=" + p_1 + c_1);
			if (w_1c_1Set)
				features.add("w_1c_1=" + w_1 + c_1);
			if (w0p_1Set)
				features.add("w0p_1=" + w0 + p_1);
			if (w_1p_1Set)
				features.add("w_1p_1=" + w_1 + p_1);
			if (p_1p0Set)
				features.add("p_1p0=" + p_1 + p0);
			if (w0c_1Set)
				features.add("w0c_1=" + w0 + c_1);
			if (p0c_1Set)
				features.add("p0c_1=" + p0 + c_1);
			if (w_1p0Set)
				features.add("w_1p0=" + w_1 + p0);
			if (p_1p0c_1Set)
				features.add("p_1p0c_1=" + p_1 + p0 + c_1);
			if (w0p_1p0Set)
				features.add("w0p_1p0=" + w0 + p_1 + p0);
			if (w_1w0p_1Set)
				features.add("w_1w0p_1=" + w_1 + w0 + p_1);

			if (w1 != null)
			{
				if (p_1p0p1Set)
					features.add("p_1p0p1=" + p_1 + p0 + p1);
				if (p_1p1Set)
					features.add("p_1p1=" + p_1 + p1);
				if (w1p_1p0Set)
					features.add("w1p_1p0=" + w1 + p_1 + p0);
				if (p0p1c_1Set)
					features.add("p0p1c_1=" + p0 + p1 + c_1);
				if (w_1w1Set)
					features.add("w_1w1=" + w_1 + w1);
				if (w_1p1Set)
					features.add("w_1p1=" + w_1 + p1);
				if (w1p_1Set)
					features.add("w1p_1=" + w1 + p_1);
				if (w1c_1Set)
					features.add("w1c_1=" + w1 + c_1);
				if (p1c_1Set)
					features.add("p1c_1=" + p1 + c_1);
				if (w2 != null)
				{
					if (w2c_1Set)
						features.add("w2c_1=" + w2 + c_1);
					if (p2c_1Set)
						features.add("p2c_1=" + p2 + c_1);
					if (w_1p2Set)
						features.add("w_1p2=" + w_1 + p2);
					if (p_1p2Set)
						features.add("p_1p2=" + p_1 + p2);
					if (p_1p1p2Set)
						features.add("p_1p1p2=" + p_1 + p1 + p2);
				}
			}

			if (w_2 != null)
			{
				if (w_2Set)
					features.add("w_2=" + w_2);
				if (p_2Set)
					features.add("p_2=" + p_2);
				if (c_2Set)
					features.add("c_2=" + c_2);
				if (w0c_2Set)
					features.add("w0c_2=" + w0 + c_2);
				if (p0c_2Set)
					features.add("p0c_2=" + p0 + c_2);
				if (w_2w_1Set)
					features.add("w_2w_1=" + w_2 + w_1);
				if (w_2w_1w0Set)
					features.add("w_2w_1w0=" + w_2 + w_1 + w0);
				if (w_2c_2Set)
					features.add("w_2c_2=" + w_2 + c_2);
				if (p_2c_2Set)
					features.add("p_2c_2=" + p_2 + c_2);
				if (w_2c_1Set)
					features.add("w_2c_1=" + w_2 + c_1);
				if (p_2c_1Set)
					features.add("p_2c_1=" + p_2 + c_1);
				if (w_1c_2Set)
					features.add("w_1c_2=" + w_1 + c_2);
				if (p_1c_2Set)
					features.add("p_1c_2=" + p_1 + c_2);
				if (c_2c_1Set)
					features.add("c_2c_1=" + c_2 + c_1);
				if (w_1p_2Set)
					features.add("w_1p_2=" + w_1 + p_2);
				if (w0p_2Set)
					features.add("w0p_2=" + w0 + p_2);
				if (p_2p0Set)
					features.add("p_2p0=" + p_2 + p0);
				if (p_2p_1Set)
					features.add("p_2p_1=" + p_2 + p_1);
				if (p_2p_1p0Set)
					features.add("p_2p_1p0=" + p_2 + p_1 + p0);
				if (w_2p_1p0Set)
					features.add("w_2p_1p0=" + w_2 + p_1 + p0);

				if (w1 != null)
				{
					if (w1c_2Set)
						features.add("w1c_2=" + w1 + c_2);
					if (p1c_2Set)
						features.add("p1c_2=" + p1 + c_2);
					if (w1p_2Set)
						features.add("w1p_2=" + w1 + p_2);
					if (p_2p1Set)
						features.add("p_2p1=" + p_2 + p1);
					if (w_1w0w1Set)
						features.add("w_1w0w1=" + w_1 + w0 + w1);
					if (p_2p0p1Set)
						features.add("p_2p0p1=" + p_2 + p0 + p1);

					if (w2 != null)
					{
						if (w2c_2Set)
							features.add("w2c_2=" + w2 + c_2);
						if (p2c_2Set)
							features.add("p2c_2=" + p2 + c_2);
						if (p_2p2Set)
							features.add("p_2p2=" + p_2 + p2);
					}
				}
			}
		}

		if (w1 != null)
		{
			if (w1Set)
				features.add("w1=" + w1);
			if (w0w1Set)
				features.add("w0w1=" + w0 + w1);
			if (p1Set)
				features.add("p1=" + p1);
			if (w0p1Set)
				features.add("w0p1=" + w0 + p1);
			if (w0w1p1Set)
				features.add("w0w1p1=" + w0 + w1 + p1);
			if (w1p0Set)
				features.add("w1p0=" + w1 + p0);
			if (p0p1Set)
				features.add("p0p1=" + p0 + p1);
			if (w0p0p1Set)
				features.add("w0p0p1=" + w0 + p0 + p1);
			if (w1p1Set)
				features.add("w1p1=" + w1 + p1);
			if (w1p0p1Set)
				features.add("w1p0p1=" + w1 + p0 + p1);

			if (w2 != null)
			{
				if (w2Set)
					features.add("w2=" + w2);
				if (p2Set)
					features.add("p2=" + p2);
				if (w1w2Set)
					features.add("w1w2=" + w1 + w2);
				if (w1p2Set)
					features.add("w1p2=" + w1 + p2);
				if (w0w1w2Set)
					features.add("w0w1w2=" + w0 + w1 + w2);
				if (w2p2Set)
					features.add("w2p2=" + w2 + p2);
				if (p1p2Set)
					features.add("p1p2=" + p1 + p2);
				if (p0p1p2Set)
					features.add("p0p1p2=" + p0 + p1 + p2);
				if (p0p2Set)
					features.add("p0p2=" + p0 + p2);
				if (w0p2Set)
					features.add("w0p2=" + w0 + p2);
				if (w0w2p2Set)
					features.add("w0w2p2=" + w0 + w2 + p2);
			}
		}

		String[] contexts = features.toArray(new String[features.size()]);

		return contexts;
	}

	@Override
	public String toString()
	{
		return "ChunkAnalysisContextGenratorConf{" + "w_2Set=" + w_2Set + ", w_1Set=" + w_1Set + ", w0Set=" + w0Set
				+ ", w1Set=" + w1Set + ", w2Set=" + w2Set + ", p_2Set=" + p_2Set + ", p_1Set=" + p_1Set + ", p0Set="
				+ p0Set + ", p1Set=" + p1Set + ", p2Set=" + p2Set + ", c_2Set=" + c_2Set + ", c_1Set=" + c_1Set + '}';
	}

}
