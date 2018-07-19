package com.lc.nlp4han.chunk;

/**
 * 样本解析抽象类
 */
public abstract class AbstractChunkSampleParser
{
	// BIO/BIEO/BIEOS标注方案
	protected String scheme;

	public AbstractChunkSampleParser()
	{
		setTagScheme();
	}

	public String getTagScheme()
	{
		return scheme;
	}

	protected abstract void setTagScheme();

	/**
	 * 返回由字符串句子解析而成的样本
	 * 
	 * @return 样本
	 */
	public abstract AbstractChunkAnalysisSample parse(String sentence);
}
