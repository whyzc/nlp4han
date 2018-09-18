package com.lc.nlp4han.constituent.lex;
/**
 * 此类特殊规则为并列结构和PU(标点符号：逗号和冒号)相关
 * @author qyl
 *
 */
public class RuleSpecialCase extends RuleCollins
{
	private String CCPOS=null;//并列结构中的连词的词性标注
	private String CCword=null;//连词
	private String leftPOS=null;//并列结构左侧标记
	private String rightPOS=null;//并列结构右侧标记
	private String lheadWord=null;//并列结构左侧的中心词
	private String rheadWord=null;//并列结构右侧的中心词
	
	public RuleSpecialCase(String parentLabel, String cCPOS, String cCword,
			String leftPOS, String rightPOS, String lheadWord, String rheadWord)
	{
		super(parentLabel);
		this.CCPOS = cCPOS;
		this.CCword = cCword;
		this.leftPOS = leftPOS;
		this.rightPOS = rightPOS;
		this.lheadWord = lheadWord;
		this.rheadWord = rheadWord;
	}
 
	@Override
	public String toString()
	{
		return CCPOS + " " + CCword + " " + super.getParentLabel()+ " "+ leftPOS + " " + rightPOS + " " + lheadWord + " " + rheadWord ;
	}
	
}
