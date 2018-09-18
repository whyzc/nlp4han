package com.lc.nlp4han.constituent.lex;
/**
 * 用于存储生成中心节点两侧的数据
 * @author qyl
 *
 */
public class RuleSidesGenerate extends  RuleHeadChildGenerate
{
	private int direction = 0;//头结点为0,左侧为1，右侧为2
	private String sideLabel = null;//所求孩子节点的标记
	private String sideHeadPOS=null;//所求孩子节点的中心词词标记
	private String sideHeadWord;// 所求的孩子节点的中心词
	private int  coor=0;//并列结构
	private int  pu=0;//标点符号，由于只保留了顿号所以我们可以把它当做并列结构
	private Distance distance=new Distance();//距离度量
	public RuleSidesGenerate(String headLabel, String parentLabel, String headPOS, String headWord, int direction,
			String sideLabel, String sideHeadPOS, String sideHeadWord, int coor, int  pu, Distance distance)
	{
		super(headLabel, parentLabel, headPOS, headWord);
		this.direction = direction;
		this.sideLabel = sideLabel;
		this.sideHeadPOS = sideHeadPOS;
		this.sideHeadWord = sideHeadWord;
		this.coor = coor;
		this.pu = pu;
		this.distance = distance;
	}
    
	public RuleSidesGenerate( String sideHeadPOS, String sideHeadWord)
	{
		super(null, null, null, null);
		this.sideHeadPOS = sideHeadPOS;
		this.sideHeadWord = sideHeadWord;
	}

	@Override
	public String toString()
	{
		return direction+" "+sideLabel+" "+sideHeadPOS+" "+ sideHeadWord+" "+coor+" "+pu+" "+super.toString()+" "+distance.isAdjacency()+" "+distance.isCrossVerb();
	}
}
