package com.lc.nlp4han.constituent;

import java.util.List;
import java.util.Stack;

/**
 * 将不含头结点的句法树，转换成包含头结点的句法树
 * 
 * @author 王馨苇
 *
 */
public class TreeToHeadTree
{

	/**
	 * 将一颗无头结点的树转成带头结点的树
	 * 
	 * @param treeNode
	 * @return
	 */
	public static HeadTreeNode treeToHeadTree(TreeNode treeNode, AbstractHeadGenerator headGen)
	{
		String treeStr = "(" + treeNode.toStringWordIndexNoNone() + ")";
		treeStr = BracketExpUtil.format(treeStr);

		int indexTree;// 记录当前是第几颗子树
		List<String> parts = BracketExpUtil.stringToList(treeStr);
		Stack<HeadTreeNode> tree = new Stack<HeadTreeNode>();
		for (int i = 0; i < parts.size(); i++)
		{
			if (!parts.get(i).equals(")") && !parts.get(i).equals(" "))
			{
				tree.push(new HeadTreeNode(parts.get(i)));
			}
			else if (parts.get(i).equals(" "))
			{

			}
			else if (parts.get(i).equals(")"))
			{
				indexTree = 0;
				Stack<HeadTreeNode> temp = new Stack<HeadTreeNode>();
				while (!tree.peek().getNodeName().equals("("))
				{
					if (!tree.peek().getNodeName().equals(" "))
					{
						temp.push(tree.pop());
					}
				}
				tree.pop();
				HeadTreeNode node = temp.pop();
				while (!temp.isEmpty())
				{
					temp.peek().setParent(node);
					temp.peek().setIndex(indexTree++);
					if (temp.peek().getChildrenNum() == 0)
					{
						HeadTreeNode wordindexnode = temp.peek();
						String[] str = temp.peek().getNodeName().split("\\[");
						wordindexnode.setNewName(str[0]);
						wordindexnode.setWordIndex(Integer.parseInt(str[1].substring(0, str[1].length() - 1)));
						node.addChild(wordindexnode);
					}
					else
					{
						node.addChild(temp.peek());
					}
					temp.pop();
				}
				// 设置头节点的部分
				// 为每一个非终结符，且不是词性标记的设置头节点
				// 对于词性标记的头节点就是词性标记对应的词本身
				// (1)为词性标记的时候，头节点为词性标记下的词语
				if (node.getChildrenNum() == 1 && node.getFirstChild().getChildrenNum() == 0)
				{
					node.setHeadWord(node.getFirstChildName());
					node.setHeadPos(node.getNodeName());
					// (2)为非终结符，且不是词性标记的时候，由规则推出
				}
				else if (!node.isLeaf())
				{
					node.setHeadWord(headGen.extractHeadWord(node));
					node.setHeadPos(headGen.extractHeadPos(node));
				}
				tree.push(node);
			}
		}
		HeadTreeNode headTreeNode = tree.pop();
		unescapeBracket(headTreeNode);
		return headTreeNode;
	}

	private static void unescapeBracket(HeadTreeNode node)
	{
		if (node.getChildrenNum() == 0)
		{
			if (node.getNodeName().equals("-LRB-"))
			{
				node.setNewName("(");
			}
			else if (node.getNodeName().equals("-RRB-"))
			{
				node.setNewName(")");
			}
			return;
		}
		else if (node.getHeadWord().equals("-LRB-"))
		{
			node.setHeadWord("(");
		}
		else if (node.getHeadWord().equals("-RRB-"))
		{
			node.setHeadWord(")");
		}
		
		for (HeadTreeNode childNode : node.getChildren())
		{
			unescapeBracket(childNode);
		}
	}
}
