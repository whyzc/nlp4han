package com.lc.nlp4han.srl.framenet;
/**
 * 分类阶段的事件
 * @author qyl
 *
 */
public class Event4Classification
{
	//特征
	//中心词
	private String HeadWord;
	//voice,此处特质主动/被动
	private boolean active;
	//位置，此处记录该角色在谓词的前后位置
	private boolean beforePred;
	//gov,只有两个值：S/VP,而且被限制于NP使用
	private boolean govVP;
	//Phrase Type,短语类型
	private String phraseType;
	//目标词/谓词
	private String predicate;
	
	//语义角色/框架元素
	private String role;
	
	//概率值
	private double value;

	public Event4Classification()
	{
		
	}
	
	public Event4Classification(String headWord, boolean active, boolean beforePred, boolean govVP, String phraseType,
			String predicate, String role, double value)
	{
		HeadWord = headWord;
		this.active = active;
		this.beforePred = beforePred;
		this.govVP = govVP;
		this.phraseType = phraseType;
		this.predicate = predicate;
		this.role = role;
		this.value = value;
	}

	public String getHeadWord()
	{
		return HeadWord;
	}

	public void setHeadWord(String headWord)
	{
		HeadWord = headWord;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean isBeforePred()
	{
		return beforePred;
	}

	public void setBeforePred(boolean beforePred)
	{
		this.beforePred = beforePred;
	}

	public boolean isGovVP()
	{
		return govVP;
	}

	public void setGovVP(boolean govVP)
	{
		this.govVP = govVP;
	}

	public String getPhraseType()
	{
		return phraseType;
	}

	public void setPhraseType(String phraseType)
	{
		this.phraseType = phraseType;
	}

	public String getPredicate()
	{
		return predicate;
	}

	public void setPredicate(String predicate)
	{
		this.predicate = predicate;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}
}
