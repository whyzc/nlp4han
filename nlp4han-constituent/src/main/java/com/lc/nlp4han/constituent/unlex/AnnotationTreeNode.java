package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * @author 王宁 表示节点是Annotation的树
 */
public class AnnotationTreeNode extends TreeNode
{
	// public static NonterminalTable nonterminalTable = new NonterminalTable();
	private Annotation label;

	private AnnotationTreeNode()
	{
	}

	private AnnotationTreeNode(Annotation label)
	{
		this.label = label;
	}

	public static AnnotationTreeNode getInstance()
	{
		return new AnnotationTreeNode();
	}

	public static AnnotationTreeNode getInstance(TreeNode tree, NonterminalTable nonterminalTable)
	{

		if (tree instanceof AnnotationTreeNode)
		{
			return (AnnotationTreeNode) tree;
		}
		if (tree == null)
		{
			return null;
		}
		if (tree.getNodeName() == null)
		{
			return null;
		}

		if (tree.isLeaf())
		{
			return new AnnotationTreeNode(new Annotation(tree.getNodeName()));
		}
		short intValueOfLabel;
		if (nonterminalTable.hasSymbol(tree.getNodeName()))
		{
			intValueOfLabel = nonterminalTable.intValue(tree.getNodeName());
		}
		else
		{
			intValueOfLabel = nonterminalTable.putSymbol(tree.getNodeName());
		}

		if (tree.getChild(0).isLeaf() && tree.getChildren().size() == 1
				&& !nonterminalTable.getIntValueOfPreterminalArr().contains(intValueOfLabel))
		{
			nonterminalTable.addToPreterminalArr(intValueOfLabel);
		}

		AnnotationTreeNode annotationTree = new AnnotationTreeNode(new Annotation(intValueOfLabel, (short) 1));
		annotationTree.setNewName(null);
		ArrayList<AnnotationTreeNode> tempChildren = new ArrayList<AnnotationTreeNode>(tree.getChildren().size());
		for (TreeNode child : tree.getChildren())
		{
			AnnotationTreeNode newChild = getInstance(child, nonterminalTable);
			newChild.setParent(annotationTree);
			tempChildren.add(newChild);
		}
		for (int i = 0; i < tempChildren.size(); i++)
			annotationTree.addChild(tempChildren.get(i));
		return annotationTree.setSpanFT();
	}

	public boolean isPreterminal()
	{
		if (children.size() == 1 && children.get(0).isLeaf())
			return true;
		else
			return false;
	}

	public AnnotationTreeNode forgetIOScoreAndScale()
	{
		if (this.isLeaf() || this == null)
			return this;
		label.setInnerScores(null);
		label.setOuterScores(null);
		label.setInnerScale(0);
		label.setOuterScale(0);
		for (AnnotationTreeNode child : this.getChildren())
		{
			child.forgetIOScoreAndScale();
		}
		return this;
	}

	public AnnotationTreeNode setSpanFT()
	{
		getSpanFTHelper(0);
		return this;
	}

	private int getSpanFTHelper(int count)
	{
		if (this.isPreterminal())
		{

			this.label.setSpanFrom((short) count);
			this.label.setSpanTo((short) (count + 1));
			return count + 1;
		}

		for (AnnotationTreeNode child : getChildren())
		{
			count = child.getSpanFTHelper(count);
		}
		this.label.setSpanFrom(this.getFirstChild().getLabel().getSpanFrom());
		this.label.setSpanTo(this.getLastChild().getLabel().getSpanTo());
		return count;
	}

	@Override
	public AnnotationTreeNode getFirstChild()
	{
		return (AnnotationTreeNode) super.getFirstChild();
	}

	@Override
	public AnnotationTreeNode getLastChild()
	{
		return (AnnotationTreeNode) super.getLastChild();
	}

	@Override
	public AnnotationTreeNode getChild(int i)
	{
		return (AnnotationTreeNode) super.getChild(i);
	}

	@Override
	public String getChildName(int i)
	{
		return ((AnnotationTreeNode) children.get(i)).getNodeName();
	}

	@Override
	public AnnotationTreeNode getParent()
	{
		return (AnnotationTreeNode) super.getParent();
	}

	public String getNodeName(NonterminalTable nonterminalTable)
	{
		if (isLeaf())
			return label.getWord();
		else
			return nonterminalTable.stringValue(label.getSymbol());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnnotationTreeNode> getChildren()
	{
		return (List<AnnotationTreeNode>) super.getChildren();
	}

	public Annotation getLabel()
	{
		return label;
	}

	public void setLabel(Annotation label)
	{
		this.label = label;
	}
}
