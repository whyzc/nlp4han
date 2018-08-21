package org.nlp4han.sentiment.nb;

import java.io.IOException;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;

public class SentimentTextSampleStream extends FilterObjectStream<String, SentimentTextSample>
{

	public SentimentTextSampleStream(ObjectStream<String> samples)
	{
		super(samples);// 验证输入的文本不为空
	}

	public SentimentTextSample read() throws IOException
	{
		String sampleString = samples.read();

		if (sampleString != null)
		{
			String[] text = sampleString.split("\\|\\|");
			String category = text[0];
			String content = text[1];

			SentimentTextSample sample;
			sample = new SentimentTextSample(category, content);

			return sample;
		}

		return null;
	}
}
