package com.lc.nlp4han.constituent.lex;
/**
 * 用于存储生成头结点的数据
 * 
 * @author qyl
 *
 */
public class RuleHeadChildGenerate  extends RuleCollins
{

	private String headLabel = null;//中心节点标记
	public RuleHeadChildGenerate(String headLabel,String parentLabel, String headPOS, String headWord)
	{
		super(parentLabel, headPOS, headWord);
        this.headLabel=headLabel;
	}
	@Override
	public String toString()
	{
		return super.toString()+headLabel;
	}
    
	
}
