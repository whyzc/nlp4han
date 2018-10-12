package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;

/**
 * 短语结构树节点
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class TreeNode implements Cloneable
{
	// 节点名称
	protected String nodename;

	// 父节点
	protected TreeNode parent;

	// 子节点
	protected List<TreeNode> children = new ArrayList<TreeNode>();

	// 当前父节点下的第几颗子树
	// XXX: 通过父节点的数据结构可以确定，需要吗？
	private int index;

	/**
	 * 在句法分析的去除空节点的预处理步骤中：用来标记当前节点是否是空节点 ，是false 在语义分析的去除空节点的预处理步骤中：用来标记当前节点是否是空节点
	 * 在语义分析的剪枝的预处理步骤中：用来标记当前节点是否已被剪枝
	 */
	// XXX: 是否需要？是否在子类中引入？
	private boolean flag;

	// XXX: 是否需要？
	private int wordindex;

	public TreeNode()
	{

	}

	public TreeNode(String nodename)
	{
		this.nodename = nodename;
		this.flag = true;
	}

	public void setNewName(String name)
	{
		this.nodename = name;
	}

	// 设置父节点
	public void setParent(TreeNode parent)
	{
		this.parent = parent;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}

	/**
	 * 添加子节点
	 * 
	 * @param children
	 *            子节点名字
	 */
	public void addChild(String children)
	{
		this.children.add(new TreeNode(children));
	}

	public void addChild(TreeNode children)
	{
		this.children.add(children);
	}

	/**
	 * 添加数个孩子
	 * 
	 * @param children
	 */
	public void addChild(TreeNode[] children)
	{
		for (TreeNode treeNode : children)
		{
			this.addChild(treeNode);
		}
	}

	public void addChild(String[] children)
	{
		for (String treeNode : children)
		{
			this.addChild(treeNode);
		}
	}

	public void setWordIndex(int wordindex)
	{
		this.wordindex = wordindex;
	}

	public int getWordIndex()
	{
		return this.wordindex;
	}

	/**
	 * 判断是否为叶子节点
	 * 
	 * @return
	 */
	public boolean isLeaf()
	{
		return this.children.size() == 0;
	}

	/**
	 * 第一个儿子
	 * 
	 * @return
	 */
	public TreeNode getFirstChild()
	{
		return this.children.get(0);
	}

	/**
	 * 第一个儿子节点的名字
	 * 
	 * @return
	 */
	public String getFirstChildName()
	{
		return this.children.get(0).getNodeName();
	}

	/**
	 * 获取最后一个儿子
	 * 
	 * @return
	 */
	public TreeNode getLastChild()
	{
		return this.children.get(this.children.size() - 1);
	}

	/**
	 * 获取最后一个儿子名字
	 * 
	 * @return
	 */
	public String getLastChildName()
	{
		return this.children.get(this.children.size() - 1).getNodeName();
	}

	/**
	 * 获取第i个儿子
	 * 
	 * @param i
	 *            儿子的序数
	 * @return
	 */
	public TreeNode getChild(int i)
	{
		return this.children.get(i);
	}

	public void setChild(int i, TreeNode c)
	{
		this.children.set(i, c);
	}

	/**
	 * 获取第i个儿子名字
	 * 
	 * @param i
	 *            儿子的序数
	 * @return
	 */
	public String getChildName(int i)
	{
		return this.children.get(i).getNodeName();
	}

	/**
	 * 子节点的个数
	 * 
	 * @return
	 */
	public int getChildrenNum()
	{
		return this.children.size();
	}

	/**
	 * 节点名称
	 * 
	 * @return
	 */
	public String getNodeName()
	{
		return this.nodename;
	}

	/**
	 * 获取结点名称以_分开的左部分
	 * 
	 * @return
	 */
	// XXX: 带动作标记的树才需要，影响该树的一般化
	public String getNodeNameLeftPart()
	{
		return this.nodename.split("_")[0];
	}

	/**
	 * 获取结点名称以_分开的右部分
	 * 
	 * @return
	 */
	// XXX: 带动作标记的树才需要，影响该树的一般化
	public String getNodeNameRightPart()
	{
		return this.nodename.split("_")[1];
	}

	/**
	 * 返回父节点
	 * 
	 * @return
	 */
	public TreeNode getParent()
	{
		return this.parent;
	}

	/**
	 * 返回子节点列表
	 * 
	 * @return
	 */
	public List<? extends TreeNode> getChildren()
	{
		return this.children;
	}

	public int getIndex()
	{
		return this.index;
	}

	public boolean getFlag()
	{
		return this.flag;
	}

	/**
	 * 输出为一行的括号表达式
	 */
	@Override
	public String toString()
	{
		if (this.children.size() == 0)
		{
			return " " + BracketConvert(this.getNodeName());
		}
		else
		{
			String treestr = "";
			treestr = "(" + this.nodename;
			for (TreeNode node : this.children)
			{
				treestr += node.toString();
			}

			treestr += ")";

			return treestr;
		}
	}

	/**
	 * 输出为一行的括号表达式形式，带词的位置
	 * 
	 * @return
	 */
	public String toStringWordIndex()
	{
		if (this.children.size() == 0)
		{
			return " " + BracketConvert(this.getNodeName()) + "[" + this.wordindex + "]";
		}
		else
		{
			String treestr = "";
			treestr = "(" + this.nodename;
			for (TreeNode node : this.children)
			{
				treestr += node.toString();
			}
			treestr += ")";

			return treestr;
		}
	}

	/**
	 * 输出没有换行没有空节点的的括号表达式形式
	 * 
	 * @return
	 */
	public String toStringNoNone()
	{
		if (this.children.size() == 0 && this.flag == true)
		{
			return " " + BracketConvert(this.getNodeName());
		}
		else
		{
			String treestr = "";
			if (this.flag == true)
			{
				treestr = "(" + this.nodename;
			}

			for (TreeNode node : this.children)
			{

				treestr += node.toStringNoNone();
			}

			if (this.flag == true)
			{
				treestr += ")";
			}

			return treestr;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		TreeNode node = (TreeNode) obj;
		if (this.toString().equals(node.toString()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public TreeNode clone() throws CloneNotSupportedException
	{
		TreeNode cloned = (TreeNode) super.clone();
		return cloned;
	}

	/**
	 * 打印没有换行的一整行括号表达式【去掉删除的节点】
	 * 
	 * @return
	 */
	public String toStringWordIndexNoNone()
	{
		if (this.children.size() == 0 && this.flag == true)
		{
			return " " + BracketConvert(this.getNodeName()) + "[" + this.wordindex + "]";
		}
		else
		{
			String treestr = "";
			if (this.flag == true)
			{
				treestr = "(" + this.nodename;
			}

			for (TreeNode node : this.children)
			{

				treestr += node.toStringWordIndexNoNone();
			}

			if (this.flag == true)
			{
				treestr += ")";
			}

			return treestr;
		}
	}

	/**
	 * 输出有缩进和换行的括号表达式
	 * 
	 * @param level
	 *            缩进的空格数
	 */
	public static String printTree(TreeNode tree, int level)
	{
		if (tree.getChildrenNum() == 1 && tree.getFirstChild().getChildrenNum() == 0)
		{
			return "(" + tree.getNodeName() + " " + BracketConvert(tree.getFirstChild().getNodeName()) + ")";
		}
		else if (tree.getChildrenNum() == 1 && tree.getFirstChild().getChildrenNum() == 1
				&& tree.getFirstChild().getFirstChild().getChildrenNum() == 0)
		{
			return "(" + tree.getNodeName() + " " + "(" + tree.getFirstChild().getNodeName() + " "
					+ BracketConvert(tree.getFirstChild().getFirstChild().getNodeName()) + ")" + ")";
		}
		else if (tree.getChildrenNum() > 1 && firstChildIsPosAndWord(tree))
		{
			String str = "";
			str += "(" + tree.getNodeName();
			str += " " + "(" + tree.getFirstChild().getNodeName() + " "
					+ BracketConvert(tree.getFirstChild().getFirstChild().getNodeName()) + ")" + "\n";
			String s = "";
			for (int i = 1; i < tree.getChildrenNum(); i++)
			{
				for (int j = 0; j < level; j++)
				{
					s += "	";
				}
				s += printTree(tree.getChild(i), level + 1);
				if (i == tree.getChildrenNum() - 1)
				{
					s += ")";
				}
				else
				{
					s += "\n";
				}
			}
			return str + s;
		}
		else if (tree.getChildrenNum() > 1 && allChildrenIsPosAndWord(tree))
		{
			String str = "";
			str += "(" + tree.getNodeName();
			for (int i = 0; i < tree.getChildrenNum(); i++)
			{
				if (tree.getChild(i).getChildrenNum() == 1
						&& tree.getFirstChild().getFirstChild().getChildrenNum() == 0)
				{
					if (i == tree.getChildrenNum() - 1)
					{
						str += " " + "(" + tree.getChild(i).getNodeName() + " "
								+ BracketConvert(tree.getChild(i).getFirstChild().getNodeName()) + ")" + ")";
						return str;
					}
					else
					{
						str += " " + "(" + tree.getChild(i).getNodeName() + " "
								+ BracketConvert(tree.getChild(i).getFirstChild().getNodeName()) + ")";
					}
				}
			}
			return str;
		}
		else
		{
			String treeStr = "";
			treeStr = "(" + tree.getNodeName();
			treeStr += "\n";
			for (int i = 0; i < tree.getChildrenNum(); i++)
			{
				for (int j = 0; j < level; j++)
				{
					treeStr += "	";
				}
				treeStr += printTree(tree.getChild(i), level + 1);
				if (i == tree.getChildrenNum() - 1)
				{
					treeStr += ")";
				}
				else
				{
					treeStr += "\n";
				}
			}
			return treeStr;
		}
	}

	/**
	 * 判断是否当前节点下所有的节点都是词性标记和词的结构
	 * 
	 * @param tree
	 * @return
	 */
	private static boolean allChildrenIsPosAndWord(TreeNode tree)
	{
		boolean flag = false;
		for (int i = 0; i < tree.getChildrenNum(); i++)
		{
			if (tree.getChild(i).getChildrenNum() == 1 && tree.getChild(i).getFirstChild().getChildrenNum() == 0)
			{
				flag = true;
			}
			else
			{
				flag = false;
				break;
			}
		}
		if (flag)
		{
//			System.out.println("所有孩子都是词性标注tree.getChildren()==" + tree.getChildren());
		}
		return flag;
	}

	/**
	 * 判断是否当前节点第一个结点是词性标记和词的结构，第二个节点不是这种结构，就不要在去考虑第二个节点之后的节点了
	 * 
	 * @param tree
	 * @return
	 */
	private static boolean firstChildIsPosAndWord(TreeNode tree)
	{
		if (tree.getFirstChild().getChildrenNum() == 1 && tree.getFirstChild().getFirstChild().getChildrenNum() == 0)
		{
			if (tree.getChild(1).getChildrenNum() > 1 || (tree.getChild(1).getChildrenNum() == 1)
					&& tree.getChild(1).getFirstChild().getChildrenNum() > 0)
			{
				return true;
			}
		}
		return false;
	}

	/*
	 * 若节点的名称为"("或者")",则将其输出为"-LRB"或者"-RRB-"
	 */
	public static String BracketConvert(String nodeName)
	{
		String treestr = "";
		if (nodeName == "(")
		{
			treestr = "-LRB-";
		}
		else if (nodeName == ")")
		{
			treestr = "-RRB-";
		}
		else
		{
			treestr = nodeName;
		}
		return treestr;
	}
}
