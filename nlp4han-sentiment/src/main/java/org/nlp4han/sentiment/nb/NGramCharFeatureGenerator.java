package org.nlp4han.sentiment.nb;


public class NGramCharFeatureGenerator extends NGramFeatureGenerator
{
	public NGramCharFeatureGenerator(int n) {
		super(n);
	}

	@Override
	public String[] tokenize(String text)
	{
		String[] tokens = text.split("");
		return tokens;
	}

}
