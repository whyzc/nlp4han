package org.nlp4han.coref.sieve;

public class GrammaticalRoleBasedMentionGenerator extends MentionGeneratorWrapper
{
	public GrammaticalRoleBasedMentionGenerator()
	{
		
	}
	
	public GrammaticalRoleBasedMentionGenerator(MentionGenerator generator)
	{
		this.generator = generator;
	}

	@Override
	public Document generate(Document doc)
	{
		// TODO Auto-generated method stub
		Document d = this.generator.generate(doc);
		
		return super.generate(doc);
	}
	
}
