package org.nlp4han.coref.sieve;

import java.util.List;

public class CorefResolver
{
	private List<Sieve> sieves;
	private MentionExtractor extractor;

	public CorefResolver(List<Sieve> sieves)
	{
		this.sieves = sieves;
	}
	
	public List<CorefCluster> coreferent(Document doc)
	{
		MentionExtractor extractor = new GrammaticalRoleBasedMentionExtractor(); // 实体提取器，可嵌套多层提取器
		doc = extractor.ectract(doc); // 对文本提取实体，实体会存储Document类内
		return null;
	}
}
