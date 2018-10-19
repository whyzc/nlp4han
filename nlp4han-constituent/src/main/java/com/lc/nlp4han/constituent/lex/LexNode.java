package com.lc.nlp4han.constituent.lex;

import java.util.HashMap;

public class LexNode
{
	private boolean flag;
	private HashMap<Edge, Double> edgeMap;

	public LexNode(boolean flag, HashMap<Edge, Double> edgeMap)
	{
		this.flag = flag;
		this.edgeMap = edgeMap;
	}

	public LexNode()
	{
	}

	public boolean isFlag()
	{
		return flag;
	}

	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}

	public HashMap<Edge, Double> getEdgeMap()
	{
		return edgeMap;
	}

	public void setEdgeMap(HashMap<Edge, Double> edgeMap)
	{
		this.edgeMap = edgeMap;
	}
}
