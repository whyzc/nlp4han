package com.lc.nlp4han.constituent.lex;
/**
 * 此类特殊规则为并列结构和PU(标点符号：逗号和冒号)相关
 * @author qyl
 *
 */
public class RuleSpecialCase
{
	private String CCPOS=null;//并列结构中的连词的词性标注
	private String CCword=null;//连词
	private String parentLabel=null;//并列结构父节点标记
	private String leftPOS=null;//并列结构左侧标记
	private String rightPOS=null;//并列结构右侧标记
	private String lheadWord=null;//并列结构左侧的中心词
	private String rheadWord=null;//并列结构右侧的中心词
	
	
	public RuleSpecialCase(String cCPOS, String cCword, String parentLabel, String leftPOS, String rightPOS,
			String lheadWord, String rheadWord)
	{
		CCPOS = cCPOS;
		CCword = cCword;
		this.parentLabel = parentLabel;
		this.leftPOS = leftPOS;
		this.rightPOS = rightPOS;
		this.lheadWord = lheadWord;
		this.rheadWord = rheadWord;
	}
	public String getCCPOS()
	{
		return CCPOS;
	}
	public void setCCPOS(String cCPOS)
	{
		CCPOS = cCPOS;
	}
	public String getCCword()
	{
		return CCword;
	}
	public void setCCword(String cCword)
	{
		CCword = cCword;
	}
	public String getParentLabel()
	{
		return parentLabel;
	}
	public void setParentLabel(String parentLabel)
	{
		this.parentLabel = parentLabel;
	}
	public String getLeftPOS()
	{
		return leftPOS;
	}
	public void setLeftPOS(String leftPOS)
	{
		this.leftPOS = leftPOS;
	}
	public String getRightPOS()
	{
		return rightPOS;
	}
	public void setRightPOS(String rightPOS)
	{
		this.rightPOS = rightPOS;
	}
	public String getLheadWord()
	{
		return lheadWord;
	}
	public void setLheadWord(String lheadWord)
	{
		this.lheadWord = lheadWord;
	}
	public String getRheadWord()
	{
		return rheadWord;
	}
	public void setRheadWord(String rheadWord)
	{
		this.rheadWord = rheadWord;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((CCPOS == null) ? 0 : CCPOS.hashCode());
		result = prime * result + ((CCword == null) ? 0 : CCword.hashCode());
		result = prime * result + ((leftPOS == null) ? 0 : leftPOS.hashCode());
		result = prime * result + ((lheadWord == null) ? 0 : lheadWord.hashCode());
		result = prime * result + ((parentLabel == null) ? 0 : parentLabel.hashCode());
		result = prime * result + ((rheadWord == null) ? 0 : rheadWord.hashCode());
		result = prime * result + ((rightPOS == null) ? 0 : rightPOS.hashCode());
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
		RuleSpecialCase other = (RuleSpecialCase) obj;
		if (CCPOS == null)
		{
			if (other.CCPOS != null)
				return false;
		}
		else if (!CCPOS.equals(other.CCPOS))
			return false;
		if (CCword == null)
		{
			if (other.CCword != null)
				return false;
		}
		else if (!CCword.equals(other.CCword))
			return false;
		if (leftPOS == null)
		{
			if (other.leftPOS != null)
				return false;
		}
		else if (!leftPOS.equals(other.leftPOS))
			return false;
		if (lheadWord == null)
		{
			if (other.lheadWord != null)
				return false;
		}
		else if (!lheadWord.equals(other.lheadWord))
			return false;
		if (parentLabel == null)
		{
			if (other.parentLabel != null)
				return false;
		}
		else if (!parentLabel.equals(other.parentLabel))
			return false;
		if (rheadWord == null)
		{
			if (other.rheadWord != null)
				return false;
		}
		else if (!rheadWord.equals(other.rheadWord))
			return false;
		if (rightPOS == null)
		{
			if (other.rightPOS != null)
				return false;
		}
		else if (!rightPOS.equals(other.rightPOS))
			return false;
		return true;
	}
	@Override
	public String toString()
	{
		return CCPOS + " " + CCword + " " + parentLabel + " "+ leftPOS + " " + rightPOS + " " + lheadWord + " " + rheadWord ;
	}
	
}
