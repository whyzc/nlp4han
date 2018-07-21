package com.lc.nlp4han.chunk.wordpos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;


/**
 * 基于词和词性的组块分析样本流
 */
public class ChunkAnalysisWordPosSampleStream extends FilterObjectStream<String, AbstractChunkAnalysisSample>
{

	private static Logger logger = Logger.getLogger(ChunkAnalysisWordPosSampleStream.class.getName());

	private AbstractChunkSampleParser parse;
	private String scheme;

	/**
	 * 构造方法
	 * 
	 * @param samples
	 *            输入流
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public ChunkAnalysisWordPosSampleStream(ObjectStream<String> samples, AbstractChunkSampleParser parse, String label)
			throws FileNotFoundException, UnsupportedEncodingException
	{
		super(samples);

		this.parse = parse;
		this.scheme = label;
	}

	public String getScheme()
	{
		return scheme;
	}

	/**
	 * 读取训练语料进行解析
	 * 
	 * @return 样本
	 */
	public AbstractChunkAnalysisSample read() throws IOException
	{
		String sentence = samples.read();

		if (sentence != null)
		{
			AbstractChunkAnalysisSample sample = null;
			if (sentence.compareTo("") != 0)
			{
				try
				{
					sample = parse.parse(sentence);
				}
				catch (Exception e)
				{
					if (logger.isLoggable(Level.WARNING))
						logger.warning("解析样本时出错, 忽略句子: " + sentence);

					sample = new ChunkAnalysisWordPosSample(new String[] {}, new String[] {}, new String[] {});
				}
				return sample;
			}
			else
				return new ChunkAnalysisWordPosSample(new String[] {}, new String[] {}, new String[] {});
		}
		else
			return null;
	}

	public void close() throws IOException
	{
		samples.close();
	}

	public void reset() throws IOException, UnsupportedOperationException
	{
		samples.reset();
	}
}
