package com.lc.nlp4han.constituent.unlex;
/**
* @author 王宁
* @version 创建时间：2018年9月23日 下午1:31:42
* 规则基类
*/
public abstract class Rule
{
	protected short parent;
	
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + parent;
		return result;
	}
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rule other = (Rule) obj;
		if (parent != other.parent)
			return false;
		return true;
	}
	public short getParent()
	{
		return parent;
	}
	public void setParent(short parent)
	{
		this.parent = parent;
	}
	
}
