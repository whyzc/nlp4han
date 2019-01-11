package org.nlp4han.coref.sieve;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class TestMentionGenerator
{
	@Test
	public void testGenerate()
	{
		String content = "小明正在打球。他没去上课。";
		Document doc = new Document(content);
		
		MentionGenerator extractor = new GrammaticalRoleBasedMentionGenerator(); // 实体提取器，可嵌套多层提取器
		doc = extractor.generate(doc); // 对文本提取实体，实体会存储Document类内
		
		List<List<Mention>> ms = doc.getMentions();
		
		assertEquals(2, ms.size());
	}
	
}
