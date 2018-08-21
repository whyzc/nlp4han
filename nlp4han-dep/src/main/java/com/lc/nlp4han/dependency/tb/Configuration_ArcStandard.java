package com.lc.nlp4han.dependency.tb;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;

/**
* @author 作者
* @version 创建时间：2018年8月19日 上午9:59:53
* 类说明
*/
public class Configuration_ArcStandard extends Configuration
{
	private ArrayDeque<Vertice> stack = new ArrayDeque<Vertice>();
	private LinkedList<Vertice> wordsBuffer = new LinkedList<Vertice>();
	private ArrayList<Arc> arcs = new ArrayList<Arc>();

	public Configuration_ArcStandard(ArrayDeque<Vertice> stack, LinkedList<Vertice> wordsBuffer, ArrayList<Arc> arcs)
	{
		super(stack,wordsBuffer,arcs);
	}

	public Configuration_ArcStandard(String[] words, String[] pos)
	{
		super(words,pos);
	}
	
	public Configuration_ArcStandard()
	{
	}
	
	public  Configuration_ArcStandard generateConfByActions(String[] wordpos, String[] priorActions) {
		//暂时先不管这个
		return null;
	}
		
	/**
	 * 当栈顶两个单词有右弧关系时，判断是否right_reduce
	 * 
	 * @return 当wordBuffer中有单词是栈顶单词的依存词时返回false
	 */
	public boolean canReduce(String[] dependencyIndices) {
		int indexOfWord_Bi;// 该单词在words中索引
		int indexOfWord_S1 = stack.peek().getIndexOfWord();
		int headIndexOfWord_Bi;// buffer第i个单词中心词在words中的索引
		for (int i = 0; i < wordsBuffer.size(); i++)
		{
			indexOfWord_Bi = wordsBuffer.get(i).getIndexOfWord();// 该单词在words中索引
			headIndexOfWord_Bi = Integer.parseInt(dependencyIndices[indexOfWord_Bi - 1]);// buffer第i个单词中心词在words中的索引
			if (indexOfWord_S1 == headIndexOfWord_Bi )
				return false;
		}
		return true;
	}
	
	//共三类基本操作RIGHTARC_REDUCE、LEFTARC_REDUCE、SHIFT
	public Configuration_ArcStandard transition(ActionType actType) {
		Vertice S1 = stack.pop();
		Vertice S2 = stack.peek();
		stack.push(S1);
		switch (actType.getBaseAction())
		{
		case "RIGHTARC_REDUCE":
			return addArc(new Arc(actType.getRelation(),S2,S1)).reduce(actType);
		case "LEFTARC_REDUCE":
			return addArc(new Arc(actType.getRelation(),S1,S2)).reduce(actType);
		case "SHIFT":
			return shift();
		default:
			throw new IllegalArgumentException("参数不合法!");
		}
	}
	
	public Configuration_ArcStandard addArc(Arc arc)
	{
		arcs.add(arc);
		return this;
	}
	
	public Configuration_ArcStandard shift()
	{
		if (wordsBuffer.size() != 0)
		{
			stack.push(wordsBuffer.remove(0));
			return this;
		}
		else
		{
			return null;// ?
		}

	}
	
	public Configuration_ArcStandard reduce(ActionType actType)
	{
		if (actType.getBaseAction().equals("RIGHTARC_REDUCE")) {
			stack.pop();
			return this;
		}else if(actType.getBaseAction().equals("LEFTARC_REDUCE")){
			Vertice S1 = stack.pop();
			stack.pop();
			stack.push(S1);
			return this;
		}else
			return null;
	}

}
