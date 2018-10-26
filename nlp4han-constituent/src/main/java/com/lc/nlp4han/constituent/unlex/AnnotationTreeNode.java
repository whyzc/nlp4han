package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * @author 王宁
 * 表示节点是Annotation的树
 */
public class AnnotationTreeNode extends TreeNode
{
	public static NonterminalTable nonterminalTable = new NonterminalTable();
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

	public static AnnotationTreeNode getInstance(TreeNode tree)
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
			AnnotationTreeNode newChild = getInstance(child);
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

	public AnnotationTreeNode forgetScore()
	{
		if (this.isLeaf() || this == null)
			return this;
		label.setInnerScores(null);
		label.setOuterScores(null);
		for (AnnotationTreeNode child : this.getChildren())
		{
			child.forgetScore();
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

	@Override
	public String getFirstChildName()
	{
		if (((AnnotationTreeNode) getChild(0)).label.getWord() != null)
			return ((AnnotationTreeNode) getChild(0)).label.getWord();
		else
			return nonterminalTable.stringValue(((AnnotationTreeNode) getChild(0)).label.getSymbol());
	}

	@Override
	public String getLastChildName()
	{
		if (getChild(getChildrenNum() - 1).label.getWord() != null)
			return getChild(getChildrenNum() - 1).label.getWord();
		else
			return nonterminalTable.stringValue(getChild(getChildrenNum() - 1).label.getSymbol());
	}

	@Override
	public String getNodeName()
	{
		if (isLeaf())
			return label.getWord();
		else
			return nonterminalTable.stringValue(label.getSymbol());
	}

	@Override
	public String getNodeNameLeftPart()
	{
		return getNodeName().split("_")[0];
	}

	@Override
	public String getNodeNameRightPart()
	{
		return getNodeName().split("_")[1];
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnnotationTreeNode> getChildren()
	{
		return (List<AnnotationTreeNode>) super.getChildren();
	}

	@Override
	public String toString()
	{
		if (children.size() == 0)
		{
			return " " + escapeBracket(getNodeName());
		}
		else
		{
			String treestr = "";
			treestr = "(" + getNodeName();
			for (TreeNode node : children)
			{
				treestr += ((AnnotationTreeNode) node).toString();
			}

			treestr += ")";

			return treestr;
		}
	}

	@Override
	public String toStringWordIndex()
	{
		if (this.children.size() == 0)
		{
			return " " + escapeBracket(this.getNodeName()) + "[" + getWordIndex() + "]";
		}
		else
		{
			String treestr = "";
			treestr = "(" + getNodeName();
			for (TreeNode node : this.children)
			{
				treestr += ((AnnotationTreeNode) node).toString();
			}
			treestr += ")";

			return treestr;
		}
	}

	@Override
	public String toStringNoNone()
	{
		if (this.children.size() == 0 && getFlag() == true)
		{
			return " " + escapeBracket(this.getNodeName());
		}
		else
		{
			String treestr = "";
			if (getFlag() == true)
			{
				treestr = "(" + this.getNodeName();
			}

			for (TreeNode node : this.children)
			{

				treestr += ((AnnotationTreeNode) node).toStringNoNone();
			}

			if (getFlag() == true)
			{
				treestr += ")";
			}

			return treestr;
		}
	}

	@Override
	public String toStringWordIndexNoNone()
	{
		if (this.children.size() == 0 && getFlag() == true)
		{
			return " " + escapeBracket(this.getNodeName()) + "[" + getWordIndex() + "]";
		}
		else
		{
			String treestr = "";
			if (getFlag() == true)
			{
				treestr = "(" + this.getNodeName();
			}

			for (TreeNode node : this.children)
			{

				treestr += ((AnnotationTreeNode) node).toStringWordIndexNoNone();
			}

			if (getFlag() == true)
			{
				treestr += ")";
			}

			return treestr;
		}
	}

	public static String printTree(AnnotationTreeNode tree, int level)
	{
		return TreeNode.printTree(tree, level);
	}

	public Annotation getLabel()
	{
		return label;
	}

	public void setLabel(Annotation label)
	{
		this.label = label;
	}

	public static void main(String[] args)
	{
		AnnotationTreeNode annotationTree;
		String expression = "(ROOT(IP(IP(VP(VV 异于)(NP(DNP(NP(NN 平日))(DEG 的))(NP(NN 艺术)(NN 创作)))))(PU ，)(NP(DNP(NP(ADJP(JJ 公共))(NP(NN 艺术)))(DEG 的))(NP(NN 创作者)))(VP(PP(P 除了)(NP(NN 艺术)(NN 理念)))(PU ，)(ADVP(AD 还))(VP(VV 需)(VP(VV 具备)(NP(DNP(NP(NN 建筑)(NN 结构)(PU 、)(NN 环境)(NN 景观)(CC 或)(NN 会计))(DEG 的))(ADJP(JJ 实务))(NP(NN 操作)(NN 能力))))))(PU 。)))";
		expression = expression.trim();
		if (!expression.equals(""))
		{
			TreeNode tempTree = BracketExpUtil.generateTree(expression);
			tempTree = TreeUtil.removeL2LRule(tempTree);
			boolean addParentLabel = true;
			if (addParentLabel)
				tempTree = TreeUtil.addParentLabel(tempTree);
			tempTree = Binarization.binarizeTree(tempTree);
			System.out.println(TreeNode.printTree(tempTree, 0));
			annotationTree = AnnotationTreeNode.getInstance(tempTree);
			System.out.println(printTree(annotationTree, 0));
		}
	}
}
