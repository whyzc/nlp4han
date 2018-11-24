package com.lc.nlp4han.constituent;


/**
 * 由普通树转换为带有头结点的树
 * 
 * 由于是树之间的转换故英文左右括号的检查可以省略
 * 
 * @author qyl
 *
 */
public class TreeToHeadTree
{
	public static HeadTreeNode treeToHeadTree(TreeNode treeNode, AbstractHeadGenerator headGen)
	{
		// 作为临时的根节点
		HeadTreeNode rootNode = new HeadTreeNode("tempRoot");

		traverseConvert(rootNode, treeNode, 0, headGen);
		
		HeadTreeNode realTree = rootNode.getChild(0);
		
		realTree.setParent(null);
		
		return realTree;

//		return rootNode.getChild(0);
	}

	/**
	 * 利用递归将普通树转换为head树，类似后序遍历
	 * 
	 * @param parentNode
	 * @param treeNode
	 * @param i
	 * @param headGen
	 */
	private static void traverseConvert(HeadTreeNode parentNode, TreeNode treeNode, int i,
			AbstractHeadGenerator headGen)
	{
		HeadTreeNode node = new HeadTreeNode(treeNode.getNodeName());
		parentNode.addChild(node);
		node.setIndex(i);
		node.setParent(parentNode);

		// 先遍历孩子
		for (int j = 0; j < treeNode.getChildrenNum(); j++)
		{
			traverseConvert(node, treeNode.getChild(j), j, headGen);
		}

		//遍历本节点
		if (treeNode.getChildrenNum() == 0)
		{// 若为终结符节点
			node.setWordIndex(treeNode.getWordIndex());
		}
		else if (treeNode.getChildrenNum() == 1 && treeNode.getChild(0).getChildrenNum()== 0)
		{// 若为词性标注节点
			node.setHeadPos(treeNode.getNodeName());
			node.setHeadWord(treeNode.getChildName(0));
		}
		else
		{
			node.setHeadWord(headGen.extractHeadWord(node));
			node.setHeadPos(headGen.extractHeadPos(node));
		}
	}
}
