package com.lc.nlp4han.constituent.lex;
/**
 * 虽然在处理基本名词短语与一般的生成stop方式不同，但是其数据形式相同
 * @author qyl
 *
 */
public class RuleStopGenerator
{
	private int direction = 0;//终止符生成方向,左侧为1，右侧为2
	private boolean stop;
	private String headLabel = null;//中心节点标记
	private String parentLabel=null;//父节点的非终结符标记
	private String headPOS=null;//中心词词性标记,在NPB中为上一个修饰符的pos
	private String headWord=null;//中心词,在NPB中为上一个修饰符的word
	private Distance distance=null;
	@Override
	public String toString()
	{
		return direction +" " + stop +" " + headLabel+" " + parentLabel +" " + headPOS +" " + headWord
				+" " + distance;
	}
	
}
