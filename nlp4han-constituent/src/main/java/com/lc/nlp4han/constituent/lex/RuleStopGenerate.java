package com.lc.nlp4han.constituent.lex;
/**
 * 虽然在处理基本名词短语与一般的生成stop方式不同，但是其数据形式相同
 * @author qyl
 *
 */
public class RuleStopGenerate extends RuleHeadChildGenerate
{
	private int direction = 0;//终止符生成方向,左侧为1，右侧为2
	private boolean stop;
	private Distance distance=null;
	
	public RuleStopGenerate(String headLabel, String parentLabel, String headPOS, String headWord, int direction,
			boolean stop, Distance distance)
	{
		super(headLabel, parentLabel, headPOS, headWord);
		this.direction = direction;
		this.stop = stop;
		this.distance = distance;
	}

	@Override
	public String toString()
	{
		return direction +" " + stop +" " + super.toString()+" " + distance;
	}
	
}
