package org.nlp4han.coref.sieve;

import org.nlp4han.coref.hobbs.Attribute;

public class Mention
{
	private int mentionID;
	private String headWord;
	private int headIndex;
	private int sentenceIndex;
	
	private boolean isPronoun;
	
	private Attribute attribute;
	
	private Entity entity;
	
	private String grammaticalRole;
	
	public Mention()
	{
		
	}
	
	public Mention(int sentenceIndex, int headIndex)
	{
		
	}
	
	public String getHead()
	{
		// TODO Auto-generated method stub
		return headWord;
	}
	
	public void setHead(String head, int sentenceIndex, int headIndex)
	{
		this.headWord = head;
		this.sentenceIndex = sentenceIndex;
		this.headIndex = headIndex;
	}
}
