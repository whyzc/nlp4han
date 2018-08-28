package org.nlp4han.sentiment.nb;

import java.io.IOException;

import com.lc.nlp4han.segment.WordSegFactory;
import com.lc.nlp4han.segment.WordSegmenter;

public class NGramWordFeatureGenerator extends NGramFeatureGenerator
{

	public NGramWordFeatureGenerator(int n)
	{
		super(n);
	}

	@Override
	public String[] tokenize(String text)
	{
		String[] tokens = null;
		try
		{
			WordSegmenter segmenter = WordSegFactory.getWordSegmenter();
			tokens = segmenter.segment(text);
		}
		catch (IOException e)
		{

			e.printStackTrace();
		}
		return tokens;
	}

}
