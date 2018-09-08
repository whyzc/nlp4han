package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;

/**
 * 带中心词的成分树节点
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class HeadTreeNode extends TreeNode
{

	private String headWord;
	private String headPos;

	public HeadTreeNode(String nodename)
	{
		super(nodename);
	}

	public void setHeadWord(String headWords)
	{
		this.headWord = headWords;
	}

	public String getHeadWord()
	{
		return this.headWord;
	}

	public void setHeadPos(String headWordsPos)
	{
		this.headPos = headWordsPos;
	}

	public String getHeadPos()
	{
		return this.headPos;
	}

	// 返回父节点
	public HeadTreeNode getParent()
	{
		return (HeadTreeNode) parent;
	}

	/**
	 * 第一个儿子
	 * 
	 * @return
	 */
	public HeadTreeNode getFirstChild()
	{
		return (HeadTreeNode) this.children.get(0);
	}

	/**
	 * 获取第一个儿子头结点
	 * 
	 * @return
	 */
	public String getFirstChildHeadWord()
	{
		return ((HeadTreeNode) this.children.get(0)).getHeadWord();
	}

	/**
	 * 获取第一个儿子头结点词性
	 * 
	 * @return
	 */
	public String getFirstChildHeadWordPos()
	{
		return ((HeadTreeNode) this.children.get(0)).getHeadPos();
	}

	/**
	 * 获取最后一个儿子头结点
	 * 
	 * @return
	 */
	public String getLastChildHeadWord()
	{
		return ((HeadTreeNode) this.children.get(this.children.size() - 1)).getHeadWord();
	}

	/**
	 * 获取最后一个儿子头结点词性
	 * 
	 * @return
	 */
	public String getLastChildHeadPos()
	{
		return ((HeadTreeNode) this.children.get(this.children.size() - 1)).getHeadPos();
	}

	/**
	 * 获取最后一个儿子
	 * 
	 * @return
	 */
	public HeadTreeNode getLastChild()
	{
		return (HeadTreeNode) this.children.get(this.children.size() - 1);
	}

	/**
	 * 获取第i个儿子
	 * 
	 * @param i
	 *            儿子的序数
	 * @return
	 */
	public HeadTreeNode getChild(int i)
	{
		return (HeadTreeNode) this.children.get(i);
	}

	/**
	 * 获取第i个儿子头结点
	 * 
	 * @param i
	 *            儿子的序数
	 * @return
	 */
	public String getChildHeadWord(int i)
	{
		return ((HeadTreeNode) this.children.get(i)).getHeadWord();
	}

	/**
	 * 获取第i个儿子头结点词性
	 * 
	 * @param i
	 *            儿子的序数
	 * @return
	 */
	public String getChildHeadPos(int i)
	{
		return ((HeadTreeNode) this.children.get(i)).getHeadPos();
	}

	// 返回子节点列表
	public List<HeadTreeNode> getChildren()
	{
		List<HeadTreeNode> hnode = new ArrayList<>();
		for (TreeNode treeNode : children)
		{
			HeadTreeNode node = (HeadTreeNode) treeNode;
			hnode.add(node);
		}
		return hnode;
	}

	/**
	 * 带有头结点的树的输出（一行括号表达式）
	 */
	@Override
	public String toString()
	{
		if (super.children.size() == 0)
		{
			String str=super.BracketConvert(this.nodename);
			return " " + str + "[" + this.getWordIndex() + "]";
		}
		else
		{
			String treestr = "";
			treestr = "(" + super.BracketConvert(this.nodename) + "{" + super.BracketConvert(this.headWord) + "[" + this.headPos + "]}";

			for (HeadTreeNode node : getChildren())
			{
				treestr += node.toString();
			}
			treestr += ")";
			return treestr;
		}
	}

	/**
	 * 没有头结点的括号表达式
	 * 
	 * @return
	 */
	public String toStringWordIndex()
	{
		if (this.children.size() == 0)
		{
			return " " + super.BracketConvert(this.nodename) + "[" + getWordIndex() + "]";
		}
		else
		{
			String treestr = "";
			treestr = "(" + this.nodename;
			for (TreeNode node : this.children)
			{
				treestr += node.toStringWordIndex();
			}
			treestr += ")";
			return treestr;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		HeadTreeNode node = (HeadTreeNode) obj;
		if (this.toString().equals(node.toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 将词性标注和词语转成树的形式
	 * 
	 * @param words
	 *            k个最好的词语序列
	 * @param poses
	 *            k个最好的词性标注序列
	 * @return
	 */
	public static List<List<HeadTreeNode>> toPosTree(String[] words, String[][] poses)
	{
		List<List<HeadTreeNode>> posTrees = new ArrayList<>();
		for (int i = 0; i < poses.length; i++)
		{
			List<HeadTreeNode> posTree = new ArrayList<HeadTreeNode>();
			for (int j = 0; j < poses[i].length && j < words.length; j++)
			{
				HeadTreeNode pos = new HeadTreeNode(poses[i][j]);
				HeadTreeNode word = new HeadTreeNode(words[j]);
				
				pos.addChild(word);
				word.setParent(pos);
				
				pos.setHeadWord(words[j]);
				
				posTree.add(pos);
			}
			
			posTrees.add(posTree);
		}
		
		return posTrees;
	}
}
