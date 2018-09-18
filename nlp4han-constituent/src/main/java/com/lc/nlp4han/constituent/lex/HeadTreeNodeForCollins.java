package com.lc.nlp4han.constituent.lex;

import com.lc.nlp4han.constituent.HeadTreeNode;

public class HeadTreeNodeForCollins extends HeadTreeNode
{
	private boolean verb = false;// 该节点是否包含动词
	private int headChildIndex;// 该节点的headChild的索引值

	public HeadTreeNodeForCollins(String nodename)
	{
		super(nodename);
	}

	public boolean isVerb()
	{
		return verb;
	}

	public void setVerb(boolean verb)
	{
		this.verb = verb;
	}

	public int getHeadChildIndex()
	{
		return headChildIndex;
	}

	public void setHeadChildIndex(int headChildIndex)
	{
		this.headChildIndex = headChildIndex;
	}

	/**
	 * 带有头结点、动词属性、以及headChild的索引值树的输出（一行括号表达式）
	 */
	@Override
	public String toString()
	{
		if (super.children.size() == 0)
		{
			String str = super.BracketConvert(this.nodename);
			return " " + str + "[" + this.getWordIndex() + "]";
		}
		else
		{
			String treestr = "";
			treestr = "(" + super.BracketConvert(this.nodename) + "{" + super.BracketConvert(super.getHeadWord()) + "["
					+ super.getHeadPos() + " " + this.verb + " " + this.headChildIndex + "]}";
			for (HeadTreeNode node : getChildren())
			{
				treestr += node.toString();
			}
			treestr += ")";
			return treestr;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		HeadTreeNodeForCollins node = (HeadTreeNodeForCollins) obj;
		if (this.toString().equals(node.toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
