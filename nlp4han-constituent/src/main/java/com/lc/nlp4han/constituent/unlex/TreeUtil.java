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

	/**
	 * 将Tree<String>>转化为Tree<Annotation>
	 * 
	 * @param trees
	 * @param convertTable
	 * @return 获得树库的Tree<Annotation>表示
	 */
	public static List<Tree<Annotation>> getAnnotationTrees(List<Tree<String>> trees, NonterminalTable convertTable)
	{
		if (trees.isEmpty() || trees == null)
			return null;
		List<Tree<Annotation>> annotationTrees = new ArrayList<Tree<Annotation>>(trees.size());

		for (Tree<String> tree : trees)
		{
			Tree<Annotation> annotationtree = getAnnotationTreesHelper(tree, convertTable);
			annotationTrees.add(annotationtree);
		}
		return annotationTrees;
	}

	public static Tree<Annotation> getAnnotationTreesHelper(Tree<String> tree, NonterminalTable convertTable)
	{
		if (tree == null)
			return null;
		if (tree.isLeaf())
		{
			return new Tree<Annotation>(new Annotation(tree.getLabel()));
		}
		short intValueOfLabel;
		if (convertTable.hasSymbol(tree.getLabel()))
		{
			intValueOfLabel = convertTable.intValue(tree.getLabel());
		}
		else
		{
			intValueOfLabel = convertTable.putSymbol(tree.getLabel());
		}

		if (tree.isPreterminal() && !convertTable.getIntValueOfPreterminalArr().contains(intValueOfLabel))
		{
			convertTable.addToPreterminalArr(intValueOfLabel);
		}

		Tree<Annotation> annotationTree = new Tree<Annotation>(new Annotation(intValueOfLabel, (short) 1));
		ArrayList<Tree<Annotation>> children = new ArrayList<Tree<Annotation>>(tree.getChildren().size());
		for (Tree<String> child : tree.getChildren())
		{
			children.add(getAnnotationTreesHelper(child, convertTable));
		}
		annotationTree.setChildren(children);
		return annotationTree;
	}

	/**
	 * 将TreeNode转化为Tree<String>
	 * 
	 * @param tree
	 * @return Tree<String>
	 */
	public static Tree<String> getStringTree(TreeNode treeNode)
	{
		if (treeNode == null)
			return null;
		if (treeNode.isLeaf())
			return new Tree<String>(treeNode.getNodeName());
		Tree<String> tree = new Tree<String>(treeNode.getNodeName());
		ArrayList<Tree<String>> children = new ArrayList<Tree<String>>(treeNode.getChildren().size());
		for (TreeNode child : treeNode.getChildren())
		{
			children.add(getStringTree(child));
		}
		tree.setChildren(children);
		return tree;
	}

	public static TreeNode binarizeTree(TreeNode normalTree)
	{
		List<TreeNode> children = new ArrayList<>(normalTree.getChildren());
		if ((children.size() == 1 && children.get(0).getChildren().isEmpty()) || children.isEmpty())
			return normalTree;
		List<TreeNode> newChildren = new ArrayList<TreeNode>(children.size());
		for (TreeNode child : children)
		{
			newChildren.add(binarizeTree(child));
		}

		children = newChildren;
		if (children.size() >= 3)
		{
			// children转化为left、right child
			TreeNode leftChild = new TreeNode("@" + normalTree.getNodeName());
			TreeNode rightChild = children.get(children.size() - 1);
			List<TreeNode> tempChildrenOfLC = new ArrayList<TreeNode>(2);
			tempChildrenOfLC.add(children.get(0));
			tempChildrenOfLC.add(children.get(1));
			for (int i = 2; i < children.size() - 1; i++)
			{
				TreeNode tempTree = new TreeNode("@" + normalTree.getNodeName());
				tempTree.addChild(tempChildrenOfLC.toArray(new TreeNode[2]));
				tempChildrenOfLC = new ArrayList<TreeNode>(2);
				tempChildrenOfLC.add(tempTree);
				tempChildrenOfLC.add(children.get(i));

			}
			leftChild.addChild(tempChildrenOfLC.toArray(new TreeNode[2]));
			children = new ArrayList<TreeNode>(2);
			children.add(leftChild);
			children.add(rightChild);
		}

		normalTree.setChildren(children);
		return normalTree;
	}

	public static Tree<String> binarizeTree(Tree<String> normalTree)
	{
		List<Tree<String>> children = normalTree.getChildren();
		if ((children.size() == 1 && children.get(0).getChildren().isEmpty()) || children.isEmpty())
			return normalTree;
		List<Tree<String>> newChildren = new ArrayList<Tree<String>>(children.size());
		for (Tree<String> child : children)
		{
			newChildren.add(binarizeTree(child));
		}

		children = newChildren;
		if (children.size() == 1)
		{
			children = new ArrayList<Tree<String>>(1);
			children = newChildren;
		}
		if (children.size() >= 3)
		{
			// children转化为left、right child
			Tree<String> leftChild = new Tree<String>("@" + normalTree.getLabel());
			Tree<String> rightChild = children.get(children.size() - 1);
			List<Tree<String>> tempChildrenOfLC = new ArrayList<Tree<String>>(2);
			tempChildrenOfLC.add(children.get(0));
			tempChildrenOfLC.add(children.get(1));
			for (int i = 2; i < children.size() - 1; i++)
			{
				Tree<String> tempTree = new Tree<String>("@" + normalTree.getLabel());
				tempTree.setChildren(tempChildrenOfLC);
				tempChildrenOfLC = new ArrayList<Tree<String>>(2);
				tempChildrenOfLC.add(tempTree);
				tempChildrenOfLC.add(children.get(i));

			}
			leftChild.setChildren(tempChildrenOfLC);
			children = new ArrayList<Tree<String>>(2);
			children.add(leftChild);
			children.add(rightChild);
		}

		normalTree.setChildren(children);

		return normalTree;
	}

	private static void recoverBinaryTreeHelper(Tree<String> binaryTree, List<Tree<String>> realChildren)
	{
		if (binaryTree.getChildren().get(0).getLabel().startsWith("@"))
		{
			recoverBinaryTreeHelper(binaryTree.getChildren().get(0), realChildren);
			realChildren.add(binaryTree.getChildren().get(1));
		}
		else
		{
			realChildren.add(binaryTree.getChildren().get(0));
			realChildren.add(binaryTree.getChildren().get(1));
		}
	}

	public static void recoverBinaryTree(Tree<String> binaryTree)
	{
		if (binaryTree.getChildren().isEmpty()
				|| (binaryTree.getChildren().size() == 1 && binaryTree.getChildren().get(0).getChildren().isEmpty()))
			return;
		if (binaryTree.getChildren().get(0).getLabel().startsWith("@"))
		{
			List<Tree<String>> tempChildren = new ArrayList<Tree<String>>();
			recoverBinaryTreeHelper(binaryTree, tempChildren);
			List<Tree<String>> realChildren = new ArrayList<Tree<String>>(tempChildren.size());
			realChildren.addAll(tempChildren);
			binaryTree.setChildren(realChildren);
		}
		for (int i = 0; i < binaryTree.getChildren().size(); i++)
		{
			recoverBinaryTree(binaryTree.getChildren().get(i));
		}
	}

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
	 * 一棵树除了根节点以外，其他所有的节点均添加父节点的label
	 * 
	 * @param tree
	 * @return 一棵树除了根节点以外，其他所有的节点均添加了父节点label的树
	 */
	public static Tree<String> addParentLabel(Tree<String> tree)
	{
		if (tree == null || tree.isLeaf())
			return tree;
		for (Tree<String> child : tree.getChildren())
		{
			addParentLabel(child);
			if (!child.isLeaf())
				child.setLabel(child.getLabel() + "^" + tree.getLabel());
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

	/**
	 * 
	 * @param tree
	 * @return 移除树中例如A->A的结构
	 */
	public static Tree<String> removeL2LRule(Tree<String> tree)
	{
		if (tree.getChildren().isEmpty()
				|| (tree.getChildren().size() == 1 && tree.getChildren().get(0).getChildren().isEmpty()))
			return tree;
		for (int i = 0; i < tree.getChildren().size(); i++)
		{
			removeL2LRule(tree.getChildren().get(i));
		}
		if (tree.getChildren().size() == 1 && !tree.getChildren().get(0).getChildren().isEmpty()
				&& tree.getLabel().equals(tree.getChildren().get(0).getLabel()))
		{
			tree.setChildren(tree.getChildren().get(0).getChildren());
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

	// public static <T extends Rule> void getRuleOfTreeRoot(Tree<Annotation> tree,
	// T rule)
	// {
	// if (tree == null)
	// return;
	// switch (tree.getChildren().size())
	// {
	// case 0:
	// rule = null;
	// break;
	// case 1:
	// if (tree.isPreterminal())
	// {
	// ((PreterminalRule) rule).setParent(tree.getLabel().getSymbol());
	// ((PreterminalRule)
	// rule).setWord(tree.getChildren().get(0).getLabel().getWord());
	// }
	// else
	// {
	// ((UnaryRule) rule).setParent(tree.getLabel().getSymbol());
	// ((UnaryRule)
	// rule).setChild(tree.getChildren().get(0).getLabel().getSymbol());
	// }
	// break;
	// case 2:
	// ((BinaryRule) rule).setParent(tree.getLabel().getSymbol());
	// ((BinaryRule)
	// rule).setLeftChild(tree.getChildren().get(0).getLabel().getSymbol());
	// ((BinaryRule)
	// rule).setRightChild(tree.getChildren().get(1).getLabel().getSymbol());
	// break;
	// default:throw new Error("Error Tree:more than two children.");
	// }
	// }
}
