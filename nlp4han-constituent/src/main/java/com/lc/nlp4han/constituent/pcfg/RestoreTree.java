package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 用pcnf进行剖析句子时，将得到的树恢复为宾州树的工具类
 * 
 * @author qyl
 *
 */
public class RestoreTree
{
	// 先将将孩子节点中的@符号去掉再将根节点中的@去掉
	public static TreeNode restoreTree2(TreeNode node)
	{
		if (node.isLeaf())
			return null;
		for (int i = 0; i < 2; i++)
		{
			restoreTree2(node.getFirstChild());
			restoreTree2(node.getLastChild());
		}

		if (node.getNodeName().startsWith("@"))
		{
			for (TreeNode child : node.getChildren())
			{
				child.setParent(node.getParent());
			}
			List<TreeNode> realChildren = new ArrayList<TreeNode>(node.getChildren());
			realChildren.add(node.getParent().getChild(1));
			node.getParent().setChildren(realChildren);
		}
		else if (node.getNodeName().contains("@"))
		{
			String[] strs = node.getNodeName().split("@");
			node.setNewName(strs[0]);
			ArrayList<TreeNode> tempChildren = new ArrayList<TreeNode>(node.getChildren());
			node.setChildren(new ArrayList<TreeNode>());
			TreeNode temp = node;
			for (int i = 1; i < strs.length; i++)
			{
				TreeNode newNode = new TreeNode(strs[i]);
				temp.addChild(newNode);
				newNode.setParent(temp);
				temp = newNode;
			}
			temp.setChildren(tempChildren);
			for (TreeNode child : tempChildren)
			{
				child.setParent(temp);
			}
		}
		return node;
	}

	public static TreeNode restoreTree(TreeNode node)
	{
		TreeNode tempRootNode = new TreeNode("tempRoot");
		traverseConvert(tempRootNode, node, 0);
		return tempRootNode.getChild(0);
	}

	/**
	 * 先序遍历
	 * 
	 * @param parentNode
	 * @param node
	 * @param num
	 */
	public static void traverseConvert(TreeNode parentNode, TreeNode node, int num)
	{
		TreeNode newNode = null;
		String lhs = node.getNodeName();

		// 若为两个非终结符合并形成的伪非终结符，跳过
		if (lhs.contains("&"))
		{// 跳过直接处理node的孩子，也就是相当于parentNode的第二个和第三个孩子
			traverseConvert(parentNode, node.getChild(0), 1);
			traverseConvert(parentNode, node.getChild(1), 2);
			return;
		}
		// 若为伪非终结符
		if (lhs.contains("$"))
		{
			newNode = new TreeNode(node.getChildName(0));
			addChild(newNode, parentNode, num);
			return;
		}

		if (!lhs.contains("@"))
		{
			newNode = new TreeNode(lhs);
			addChild(newNode, parentNode, num);
		}
		else
		{// 单元规则
			String[] strs = lhs.split("@");
			for (int i = 0; i < strs.length; i++)
			{
				newNode = new TreeNode(strs[i]);
				addChild(newNode, parentNode, 0);
				parentNode = newNode;
			}
		}

		if (node.getChildrenNum() == 0)
		{
			newNode.setWordIndex(node.getWordIndex());
			return;
		}
		else
		{
			for (int i = 0; i < node.getChildrenNum(); i++)
			{
				traverseConvert(newNode, node.getChild(i), i);
			}
		}

	}

	/**
	 * 添加孩子
	 * 
	 * @param node
	 * @param parentNode
	 * @param num
	 */
	private static void addChild(TreeNode node, TreeNode parentNode, int num)
	{
		node.setIndex(num);
		node.setParent(parentNode);
		parentNode.addChild(node);
	}
}
