package com.lc.nlp4han.constituent.lex;

public class RuleSidesGenerate
{
	private int direction = 0;// 生成方向，头结点为0,左侧为1，右侧为2
	private String sideLabel = null;//所求孩子节点的标记
	private String sideHeadPOS=null;//所求孩子节点的中心词词标记
	private String sideHeadWord;// 所求的孩子节点的中心词
	private boolean coor=false;//并列结构
	private boolean pu=false;//标点符号，由于只保留了逗号和冒号所以我们可以把它当做并列结构
	private String parentLabel=null;//父节点的非终结符标记
	private String headPOS=null;//中心词词性标记
	private String headWord=null;//中心词
	private String headLabel=null;//中心孩子的标记
	private Distance distance;//距离度量
	@Override
	public String toString()
	{
		return direction+" "+sideLabel+" "+sideHeadPOS+" "+ sideHeadWord+" "+coor+" "+pu+" "+parentLabel+" "+headPOS+" "+headWord+
		" "+headLabel+" "+distance.isAdjacency()+" "+distance.isCrossVerb();
	}
}
