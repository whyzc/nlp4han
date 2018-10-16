package com.lc.nlp4han.constituent.lex;

/**
 * 短语结构中中心词与修饰符之间的距离度量
 * 距离度量用邻接和块跨越动词的boolean变量表示
 * @author qyl
 *
 */
public class Distance
{

	private boolean adjacency=false;
	private boolean crossVerb=false;

	
	public Distance()
	{
	}
	public Distance(boolean adjacency, boolean crossVerb)
	{
		this.adjacency = adjacency;
		this.crossVerb = crossVerb;
	}
	public boolean isAdjacency()
	{
		return adjacency;
	}

	public void setAdjacency(boolean adjacency)
	{
		this.adjacency = adjacency;
	}

	public boolean isCrossVerb()
	{
		return crossVerb;
	}

	public void setCrossVerb(boolean crossVerb)
	{
		this.crossVerb = crossVerb;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (adjacency ? 1231 : 1237);
		result = prime * result + (crossVerb ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Distance other = (Distance) obj;
		if (adjacency != other.adjacency)
			return false;
		if (crossVerb != other.crossVerb)
			return false;
		return true;
	}
	@Override
	public String toString()
	{
		return adjacency + " " + crossVerb;
	}
	
}