package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 处理树
 * 
 * @author 王宁
 */
public class TreeUtil
{

//
//	private static void recoverBinaryTreeHelper(Tree<String> binaryTree, List<Tree<String>> realChildren)
//	{
//		if (binaryTree.getChildren().get(0).getLabel().startsWith("@"))
//		{
//			recoverBinaryTreeHelper(binaryTree.getChildren().get(0), realChildren);
//			realChildren.add(binaryTree.getChildren().get(1));
//		}
//		else
//		{
//			realChildren.add(binaryTree.getChildren().get(0));
//			realChildren.add(binaryTree.getChildren().get(1));
//		}
//	}

//	public static void recoverBinaryTree(Tree<String> binaryTree)
//	{
//		if (binaryTree.getChildren().isEmpty()
//				|| (binaryTree.getChildren().size() == 1 && binaryTree.getChildren().get(0).getChildren().isEmpty()))
//			return;
//		if (binaryTree.getChildren().get(0).getLabel().startsWith("@"))
//		{
//			List<Tree<String>> tempChildren = new ArrayList<Tree<String>>();
//			recoverBinaryTreeHelper(binaryTree, tempChildren);
//			List<Tree<String>> realChildren = new ArrayList<Tree<String>>(tempChildren.size());
//			realChildren.addAll(tempChildren);
//			binaryTree.setChildren(realChildren);
//		}
//		for (int i = 0; i < binaryTree.getChildren().size(); i++)
//		{
//			recoverBinaryTree(binaryTree.getChildren().get(i));
//		}
//	}

	/**
	 * 一棵树除了根节点以外，其他所有的节点均添加父节点的label
	 * 
	 * @param tree
	 * @return 一棵树除了根节点以外，其他所有的节点均添加了父节点label的树
	 */
	public static TreeNode addParentLabel(TreeNode tree)
	{
		if (tree == null || tree.isLeaf())
			return tree;
		for (TreeNode child : tree.getChildren())
		{
			addParentLabel(child);
			if (!child.isLeaf())
				child.setNewName(child.getNodeName() + "^" + tree.getNodeName());
		}
		return tree;
	}

	/**
	 * 由带父节点label的树的得到不带父节点label的树
	 * 
	 * @param tree
	 * @return 不带父节点label的树
	 */
	public static TreeNode removeParentLabel(TreeNode tree)
	{
		if (tree == null || tree.isLeaf())
		{
			return tree;
		}
		for (TreeNode child : tree.getChildren())
		{
			removeParentLabel(child);
		}
		tree.setNewName(tree.getNodeName().split("\\^")[0]);
		return tree;
	}

	/**
	 * 
	 * @param tree
	 * @return 移除树中例如A->A的结构
	 */
	@SuppressWarnings("unchecked")
	public static TreeNode removeL2LRule(TreeNode tree)
	{
		if (tree.getChildren().isEmpty()
				|| (tree.getChildren().size() == 1 && tree.getChildren().get(0).getChildren().isEmpty()))
			return tree;
		for (int i = 0; i < tree.getChildren().size(); i++)
		{
			removeL2LRule(tree.getChildren().get(i));
		}
		if (tree.getChildren().size() == 1 && !tree.getChildren().get(0).getChildren().isEmpty()
				&& tree.getNodeName().equals(tree.getChildren().get(0).getNodeName()))
		{
			tree.setChildren((ArrayList<TreeNode>) (tree.getChildren().get(0).getChildren()));
			for (TreeNode child : tree.getChildren())
			{
				child.setParent(tree);
			}
		}
		return tree;
	}

	public static AnnotationTreeNode forgetScore(AnnotationTreeNode tree)
	{
		if (tree.isLeaf() || tree == null)
			return tree;
		tree.getLabel().setInnerScores(null);
		tree.getLabel().setOuterScores(null);
		for (AnnotationTreeNode child : tree.getChildren())
		{
			forgetScore(child);
		}
		return tree;
	}

}
