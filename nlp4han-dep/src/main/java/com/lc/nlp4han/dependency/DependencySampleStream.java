package com.lc.nlp4han.dependency;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 依存样本流
 * 
 * 将多行(句子中每个词一行)表示的依存句子文本转换成内部依存样本
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencySampleStream extends FilterObjectStream<String, DependencySample>
{

	private static Logger logger = Logger.getLogger(DependencySampleStream.class.getName());

	private DependencySampleParser sampleParser;

	public DependencySampleStream(ObjectStream<String> samples, DependencySampleParser sampleParser)
	{
		super(samples);

		this.sampleParser = sampleParser;
	}

	/**
	 * 读取两个空行之间的句子，并转换成依存样本DependencySample
	 * 
	 * @return 返回解析之后的依存样本
	 */
	public DependencySample read() throws IOException
	{
		// 这里的read()读取训练语料中两个空行之间的内容
		String sentences = samples.read();

		if (sentences != "" || !(sentences.equals("")))
		{
			DependencySample sample = null;
			try
			{
				sample = sampleParser.parse(sentences);
			}
			catch (Exception e)
			{
				if (logger.isLoggable(Level.WARNING))
				{
					logger.warning("Error during parsing, ignoring sentence: " + sentences);
				}
//				System.out.println(sentences);
				sample = new DependencySample(new String[] {}, new String[] {}, new String[] {}, new String[] {},
						new String[] {});
			}

			return sample;

		}
		else
		{
			return null;
		}

	}

}
