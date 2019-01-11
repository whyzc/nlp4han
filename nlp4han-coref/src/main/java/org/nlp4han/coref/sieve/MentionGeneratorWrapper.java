package org.nlp4han.coref.sieve;

public class MentionGeneratorWrapper implements MentionGenerator
{
	protected MentionGenerator generator = new MentionGeneratorWrapper();
	
	@Override
	public Document generate(Document doc)
	{
		return doc;
	}
}
