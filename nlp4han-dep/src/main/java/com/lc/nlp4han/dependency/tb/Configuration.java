package com.lc.nlp4han.dependency.tb;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Configuration {
	private ArrayDeque<Vertice> stack = new ArrayDeque<Vertice>();
	private LinkedList<Vertice> wordsBuffer = new LinkedList<Vertice>();
	private ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	
	public Configuration(ArrayDeque<Vertice> stack, LinkedList<Vertice> wordsBuffer, ArrayList<Arc> arcs)
	{

		stack.push(wordsBuffer.get(0));
		wordsBuffer.remove(0);
		this.stack = stack;
		this.wordsBuffer = wordsBuffer;
		this.arcs = arcs;
	}
	
	public Configuration(String[] words, String[] pos)
	{
		if (words.length != 0)
		{
			wordsBuffer = Vertice.getWordsBuffer(words, pos);
			stack.push(wordsBuffer.get(0));
			wordsBuffer.remove(0);
		}
	}
	
	public Configuration()
	{
	}
	
	
	public abstract  Configuration generateConfByActions(String[] wordpos, String[] priorActions) ;
	/**
	 * 通过基本操作对当前conf进行转换
	 * 
	 * @return 转换后的conf
	 */
	public abstract Configuration transition(ActionType actType);
	/**
	 * 判断是否reduce
	 * 
	 * @return 有关系返回true
	 */
	public abstract boolean canReduce(String[] dependencyIndices);
	
	
	
	
	
	public static Configuration_ArcEager initialConf(String[] words, String[] pos)
	{
		return new Configuration_ArcEager(words, pos);
	}
	public boolean isFinalConf()
	{
		if (wordsBuffer.isEmpty() && stack.size() == 1)
			return true;
		else
			return false;
	}
	
	public String toString()
	{
		Vertice[] vS = stack.toArray(new Vertice[stack.size()]);
		Vertice[] vB = wordsBuffer.toArray(new Vertice[wordsBuffer.size()]);
		StringBuilder stackStr = new StringBuilder();
		StringBuilder bufferStr = new StringBuilder();
		for (int i = 0; i < stack.size(); i++)
		{
			stackStr.append(vS[stack.size() - i - 1].toString() + " ");
		}
		for (int i = 0; i < wordsBuffer.size(); i++)
		{
			bufferStr.append(vB[i].toString() + " ");
		}

		return "栈底至栈顶元素：" + stackStr.toString() + " ___" + "buffer:" + bufferStr.toString();
	}

	public String arcsToString()
	{
		StringBuilder allArc = new StringBuilder();
		allArc.append("arcs:" + "\r\n");
		for (int i = 0; i < arcs.size(); i++)
		{
			allArc.append(arcs.get(arcs.size() - i - 1).toString() + "\r\n");
		}
		return allArc.toString();
	}

	public ArrayDeque<Vertice> getStack()
	{
		return stack;
	}

	public void setStack(ArrayDeque<Vertice> stack)
	{
		this.stack = stack;
	}

	public LinkedList<Vertice> getWordsBuffer()
	{
		return wordsBuffer;
	}

	public void setWordsBuffer(LinkedList<Vertice> wordsBuffer)
	{
		this.wordsBuffer = wordsBuffer;
	}

	public ArrayList<Arc> getArcs()
	{
		return arcs;
	}

	public void setArcs(ArrayList<Arc> arcs)
	{
		this.arcs = arcs;
	}
}
