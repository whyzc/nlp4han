package org.nlp4han.coref.sieve;

public class MentionExtractorWrapper implements MentionExtractor
{
	protected MentionExtractor extractor = new MentionExtractorWrapper();
	
	@Override
	public Document ectract(Document doc)
	{
		return doc;
	}
}
