package org.nlp4han.coref.sieve;

public class GrammaticalRoleBasedMentionExtractor extends MentionExtractorWrapper
{
	public GrammaticalRoleBasedMentionExtractor()
	{
		
	}
	
	public GrammaticalRoleBasedMentionExtractor(MentionExtractor extractor)
	{
		this.extractor = extractor;
	}

	@Override
	public Document ectract(Document doc)
	{
		// TODO Auto-generated method stub
		Document d = this.extractor.ectract(doc);
		
		return super.ectract(doc);
	}
	
}
