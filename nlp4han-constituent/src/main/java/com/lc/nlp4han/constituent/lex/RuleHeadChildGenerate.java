package com.lc.nlp4han.constituent.lex;
/**
 * 用于统计生成头结点的数据
 * 
 * @author qyl
 *
 */
public class RuleHeadChildGenerate
{
	private String headLabel = null;//中心节点标记
	private String parentLabel=null;//父节点的非终结符标记
	private String headPOS=null;//中心词词性标记
	private String headWord=null;//中心词
	public String getHeadLabel()
	{
		return headLabel;
	}
	public void setHeadLabel(String headLabel)
	{
		this.headLabel = headLabel;
	}
	public String getParentLabel()
	{
		return parentLabel;
	}
	public void setParentLabel(String parentLabel)
	{
		this.parentLabel = parentLabel;
	}
	public String getHeadPOS()
	{
		return headPOS;
	}
	public void setHeadPOS(String headPOS)
	{
		this.headPOS = headPOS;
	}
	public String getHeadWord()
	{
		return headWord;
	}
	public void setHeadWord(String headWord)
	{
		this.headWord = headWord;
	}
	public RuleHeadChildGenerate(String headLabel, String parentLabel, String headPOS, String headWord)
	{
		super();
		this.headLabel = headLabel;
		this.parentLabel = parentLabel;
		this.headPOS = headPOS;
		this.headWord = headWord;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headLabel == null) ? 0 : headLabel.hashCode());
		result = prime * result + ((headPOS == null) ? 0 : headPOS.hashCode());
		result = prime * result + ((headWord == null) ? 0 : headWord.hashCode());
		result = prime * result + ((parentLabel == null) ? 0 : parentLabel.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleHeadChildGenerate other = (RuleHeadChildGenerate) obj;
		if (headLabel == null)
		{
			if (other.headLabel != null)
				return false;
		}
		else if (!headLabel.equals(other.headLabel))
			return false;
		if (headPOS == null)
		{
			if (other.headPOS != null)
				return false;
		}
		else if (!headPOS.equals(other.headPOS))
			return false;
		if (headWord == null)
		{
			if (other.headWord != null)
				return false;
		}
		else if (!headWord.equals(other.headWord))
			return false;
		if (parentLabel == null)
		{
			if (other.parentLabel != null)
				return false;
		}
		else if (!parentLabel.equals(other.parentLabel))
			return false;
		return true;
	}
	@Override
	public String toString()
	{
		return "HeadChildGenerateRule [headLabel=" + headLabel + ", parentLabel=" + parentLabel + ", headPOS=" + headPOS
				+ ", headWord=" + headWord + "]";
	}
	
}
