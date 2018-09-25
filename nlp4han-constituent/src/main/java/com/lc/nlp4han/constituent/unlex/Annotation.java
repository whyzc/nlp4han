package com.lc.nlp4han.constituent.unlex;
/**
* @author 王宁
* @version 创建时间：2018年9月23日 下午12:32:33
* 树中的节点
*/
public class Annotation
{
	String word;//表示句中词语，非终端节点才有
	short symbol;
	short numSubSymbol;
	short spanFrom,spanTo;
	double[] innerScores;//内向概率
	double[] outerScores;//外向概率
}
