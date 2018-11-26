package org.nlp4han.tools;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;


/**
 * 树编辑面板上的树节点
 *
 * 包含:显示信息和短语结构树信息
 */
public class TreePanelNode
{
	private Vector<TreePanelNode> children = new Vector<TreePanelNode>();
	private TreePanelNode parent;
	private TreePanelNode root;

	private int x, y, width, height;

	private Object value;

	private double angle;// 该节点与父节点之间的“夹角”，根节点不用计算

	public TreePanelNode(TreePanelNode parent, int x, int y, int width, int height, Object value)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.value = value;

		this.parent = parent;

		if (parent == null)
			this.root = this;
		else
		{
			this.root = parent.getRoot();
		}
	}
	
	public TreePanelNode(Object value)
	{
		this.value = value;
	}

	public TreePanelNode()
	{
	}

	/*
	 *
	 * 判断树的叶子节点是否均没有兄弟节点(在导出括号表达式时不允许叶子节点有兄弟节点)
	 */
	public boolean leafHasNoSibling()
	{
		TreePanelNode root = this.getRoot();

		for (TreePanelNode node : allNodes(root))
		{
			if (node.getChildren().isEmpty())
				if (node.hasSibling())
				{
					return false;
				}
		}

		return true;

	}

	// 判断是否有兄弟节点
	private boolean hasSibling()
	{
		if (this.equals(this.getRoot()))
			return false;
		if (this.getParent().getChildren().size() > 1)
			return true;
		else
			return false;
	}

	// 获得多棵树的所有节点
	public static Vector<TreePanelNode> allNodes(ArrayList<TreePanelNode> treeLists)
	{
		Vector<TreePanelNode> nodes = new Vector<TreePanelNode>();
		for (TreePanelNode aRoot : treeLists)
		{
			Vector<TreePanelNode> tmp = allNodes(aRoot);

			nodes.addAll(tmp);
		}

		return nodes;
	}

	// 返回以root节点为根节点的所有子孙节点的Vector序列，包含root本身
	public static Vector<TreePanelNode> allNodes(TreePanelNode root)
	{
		Vector<TreePanelNode> nodes = new Vector<TreePanelNode>();
		if (root == null)
			return nodes;
		else
		{
			nodes.add(root);
			int i = 0;
			for (; i < nodes.size(); i++)
			{
				if (nodes.get(i).children.size() != 0)
				{
					for (TreePanelNode child : nodes.get(i).children)
					{
						nodes.add(child);
					}
				}
			}

			return nodes;
		}
	}

	// 给定一个节点，将该组节点的子节点按照其angle的大小从高到低排列，改变了节点的儿子节点顺序
	public Vector<TreePanelNode> sortByAngle()
	{
		int length = children.size();
		// System.out.println(length);
		if (length != 0)
		{
			int i = 1;
			for (; i < length; i++)
			{
				for (int j = 0; j < length - i; j++)
				{
					if (children.elementAt(j).getAngle() < children.elementAt(j + 1).getAngle())
					{
						TreePanelNode temp = children.elementAt(j);
						children.set(j, children.elementAt(j + 1));
						children.set(j + 1, temp);
					}
				}
			}
		}
		return children;
	}

	/*
	 * 计算该节点与父节点之间的“夹角”
	 * 
	 * 父节点的孩子节点存放在Vector中，Vector中序号小的节点是兄长节点
	 * 
	 * 兄长节点大于兄弟节点与父节点的"夹角"，夹角从pi/2到-3pi/2.由于y轴向下，故该夹角与正常的夹角都有些不一样。
	 * 
	 * 子节点在以父节点为原点的正常坐标轴的第二象限时，夹角为pi/2->0;在第三象限，夹角为0->-pi/2;在第四象限，夹角为-pi/2->-pi;
	 * 在第一象限，夹角为-pi->-3pi/2.
	 * 
	 * 夹角大小表示一种先后关系
	 */
	public void calculateAngle()
	{
		if (this.parent != null)
		{
			if ((this.x + this.width / 2) != (this.parent.x + this.parent.width / 2))
			{
				double xChild = this.x + this.width / 2;
				double yChild = this.y + this.height / 2;
				double xParent = this.parent.x + this.parent.width / 2;
				double yParent = this.parent.y + this.parent.height / 2;

				if (xChild < xParent && yChild <= yParent)
					this.angle = Math.atan((yParent - yChild) / (xParent - xChild));

				if (xChild < xParent && yChild > yParent)
					this.angle = Math.atan((yParent - yChild) / (xParent - xChild));

				if (xChild > xParent && yChild >= yParent)
					this.angle = Math.atan((yParent - yChild) / (xParent - xChild)) - Math.PI;

				if (xChild > xParent && yChild < yParent)
					this.angle = Math.atan((yParent - yChild) / (xParent - xChild)) - Math.PI;
			}
			else if (y + this.height / 2 > this.parent.y + this.parent.height / 2)
				this.angle = -Math.PI / 2;
			else
				this.angle = -3 * Math.PI / 2;
		}
		else
		{
			this.angle = 0;// 根节点的angle暂时定位0
		}
	}

	/*
	 * 计算该节点的所有子节点与该节点的角度
	 */
	public void calculateAngleOfChildren()
	{
		int length = this.children.size();
		if (length != 0)
		{
			for (int i = 0; i < length; i++)
			{
				children.elementAt(i).calculateAngle();
			}
		}
	}

	public boolean hasChangeAfterMoveNode(int xAfterMoved, int yAfterMoved)
	{
		if (allNodes(root).size() <= 2)
			return false;
		if (getParent() != null && !getChildren().isEmpty() && getParent().getChildren().size() >= 2)
		{// 有兄弟节点
			// 判断是否真的修改
			double angelAfterMoved = 0;
			if ((xAfterMoved + this.width / 2) != (this.parent.x + this.parent.width / 2))
			{// 计算移动后与父节点的角度，没有将数据结构跟新，只是简单计算值
				double xChild = xAfterMoved + this.width / 2;
				double yChild = yAfterMoved + this.height / 2;
				double xParent = this.parent.x + this.parent.width / 2;
				double yParent = this.parent.y + this.parent.height / 2;
				if (xChild < xParent && yChild <= yParent)
					angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild));
				if (xChild < xParent && yChild > yParent)
					angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild));
				if (xChild > xParent && yChild >= yParent)
					angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild)) - Math.PI;
				if (xChild > xParent && yChild < yParent)
					angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild)) - Math.PI;
			}
			else if (yAfterMoved + this.height / 2 > this.parent.y + this.parent.height / 2)
				angelAfterMoved = -Math.PI / 2;
			else
				angelAfterMoved = -3 * Math.PI / 2;
			int count = 0;
			int i = 0;
			for (; i < getChildren().size(); i++)
			{
				if (angelAfterMoved >= getChildren().get(i).getAngle())
				{
					count = i;
					break;
				}
			}
			if (i == getChildren().size())
				count = i;
			if (angelAfterMoved == angle)
				return false;
			else if (angelAfterMoved > angle)
			{
				return (children.indexOf(this) + 1 - count) >= 2 ? true : false;
			}
			else
				return (count - children.indexOf(this)) >= 2 ? true : false;

		}
		else if (getChildren().size() >= 2)
		{// 节点有两个以上儿子
			// 判断是否修改
			ArrayList<Double> angles = new ArrayList<Double>();
			for (TreePanelNode son : children)
			{
				double angelAfterMoved = 0;
				if ((son.x + son.width / 2) != (xAfterMoved + this.width / 2))
				{// 计算移动后与父节点的角度，没有将数据结构跟新，只是简单计算值
					double xChild = son.x + son.width / 2;
					double yChild = son.y + son.height / 2;
					double xParent = xAfterMoved + this.width / 2;
					double yParent = yAfterMoved + this.height / 2;
					if (xChild < xParent && yChild <= yParent)
						angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild));
					if (xChild < xParent && yChild > yParent)
						angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild));
					if (xChild > xParent && yChild >= yParent)
						angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild)) - Math.PI;
					if (xChild > xParent && yChild < yParent)
						angelAfterMoved = Math.atan((yParent - yChild) / (xParent - xChild)) - Math.PI;
				}
				else if (son.y + son.height / 2 > yAfterMoved + this.height / 2)
					angelAfterMoved = -Math.PI / 2;
				else
					angelAfterMoved = -3 * Math.PI / 2;
				angles.add(angelAfterMoved);
			}
			for (int i = 0; i < angles.size() - 1; i++)
			{
				if (angles.get(i) - angles.get(i + 1) < 0.00000001)
					return true;
			}
			return false;

		}
		return false;
	}

	/*
	 * 
	 * 删除该节点与其父节点的关系，使得该节点成为新树的根节点
	 * 
	 */
	public void detach()
	{
		if (this != this.root)
		{
			this.parent.getChildren().remove(this);
			this.angle = 0;
			this.parent = null;
			for (TreePanelNode aNode : allNodes(this))
			{
				aNode.setRoot(this);
			}
		}
	}

	public boolean isLineSelected(int X1, int Y1, int X2, int Y2)
	{// 两个线段有交点
		if (this == this.root)
			return false;

		if (!(Math.min(X1, X2) <= Math.max(x + width / 2, parent.x + parent.width / 2)
				&& Math.max(X1, X2) >= Math.min(x + width / 2, parent.x + parent.width / 2)
				&& Math.min(Y1, Y2) <= Math.max(y, parent.y + parent.height)
				&& Math.max(Y1, Y2) >= Math.min(y, parent.y + parent.height)))
		{
			return false;
		}
		else
		{
			double u = (X1 - (parent.x + parent.width / 2)) * (y - (parent.y + parent.height))
					- (x + width / 2 - (parent.x + parent.width / 2)) * (Y1 - (parent.y + parent.height));
			double v = (X2 - (parent.x + parent.width / 2)) * (y - (parent.y + parent.height))
					- (x + width / 2 - (parent.x + parent.width / 2)) * (Y2 - (parent.y + parent.height));
			double w = (parent.x + parent.width / 2 - X1) * (Y2 - Y1) - (X2 - X1) * (parent.y + parent.height - Y1);
			double z = (x + width / 2 - X1) * (Y2 - Y1) - (X2 - X1) * (y - Y1);

			if (u * v <= 0.00000001 && w * z <= 0.00000001)
				return true;
			else
				return false;
		}

	}

	/*
	 * 将选中节点修改为该节点所在树的根节点
	 */
	public TreePanelNode changeRoot()
	{
		TreePanelNode preRoot = this.root;
		if (this.equals(this.root))
			return preRoot;
		// 所有节点均先修改其root值

		for (TreePanelNode node : allNodes(preRoot))
			node.setRoot(this);
		// 1.修改被选中节点
		this.setAngle(0);
		this.children.add(this.parent);

		// 2.修改被选节点与原root路径上所有（不包括被选节点及原root）的节点
		TreePanelNode cursor = this.getParent(), begin, preParent;
		begin = this;

		for (; !cursor.equals(preRoot); begin = cursor, cursor = preParent)
		{
			preParent = cursor.getParent();
			cursor.setParent(begin);
			cursor.getChildren().remove(begin);
			cursor.getChildren().add(preParent);
			cursor.calculateAngle();
			cursor.sortByAngle();
		}
		this.setParent(null);
		this.sortByAngle();

		// 3.修改原root
		preRoot.setParent(begin);
		preRoot.getChildren().remove(begin);
		preRoot.calculateAngle();
		preRoot.sortByAngle();

		// 其他节点均只需要修改其root

		return this;
	}

	/*
	 * 对树进行先根遍历得到一个序列
	 */
	private static Vector<TreePanelNode> traverseRootFisrt(TreePanelNode root, Vector<TreePanelNode> nodes_rootFirst)
	{
		nodes_rootFirst.add(root);
		if (root.getChildren().size() != 0)
		{
			for (TreePanelNode child : root.getChildren())
			{
				traverseRootFisrt(child, nodes_rootFirst);
			}
		}

		return nodes_rootFirst;
	}

	/*
	 * 判断给定叶子节点是树上按先根遍历的第几个叶子节点
	 * 
	 * 第一个叶子的序号为1
	 */
	private static int getIndexOfLeaf(TreePanelNode leaf)
	{
		if (leaf.children.size() != 0)
			return 0;

		int count = 0;
		TreePanelNode rootOfLeaf = leaf.getRoot();
		Vector<TreePanelNode> nodes_rootFirst = new Vector<TreePanelNode>();

		// 得到树的先根遍历节点序列
		nodes_rootFirst = traverseRootFisrt(rootOfLeaf, nodes_rootFirst);

		for (TreePanelNode theLeaf : nodes_rootFirst)
		{
			if (theLeaf.getChildren().size() == 0)
			{
				count++;
				if (theLeaf.equals(leaf))
					return count;
			}
		}
		return count;
	}

	/*
	 * 返回指定节点在树上的深度或层次,根节点层次为0
	 */
	private int treeLevel()
	{
		TreePanelNode cursor = this;
		int count = 0;
		while (!cursor.equals(this.root))
		{
			count++;
			cursor = cursor.parent;
		}
		return count;
	}

	/*
	 * 给指定的单颗树分配位置和大小，不会计算角度。采用后根遍历树
	 */
	public static TreePanelNode allocatePosition(TreePanelNode root)
	{
		int rectWidth = 60, rectHeight = 30;
		int levelDepth = 80;
		if (root.getChildren().size() != 0)
		{
			int countOfChild = 0;
			int rootX = 0;

			// 先对孩子分配位置和大小
			for (TreePanelNode child : root.getChildren())
			{
				countOfChild++;
				allocatePosition(child);
			}

			for (TreePanelNode child : root.getChildren())
			{
				rootX += child.getX();
			}

			root.setX(rootX / countOfChild);
			root.setY(levelDepth * root.treeLevel());

			root.setHeight(rectHeight);
			root.setWidth(rectWidth);
		}
		else
		{
			// 该root是叶子节点
			root.setX(80 * getIndexOfLeaf(root));
			root.setY(levelDepth * root.treeLevel());

			root.setHeight(rectHeight);
			root.setWidth(rectWidth);
		}

		return root;
	}

	/*
	 * 给指定的森林分配位置。采用后根遍历树
	 */
	public static ArrayList<TreePanelNode> allocatePosAndAngle(ArrayList<TreePanelNode> treeLists)
	{
		if (treeLists.size() == 0 || treeLists == null)
		{
			return new ArrayList<TreePanelNode>();
		}

		int count = 0;// 表示森林中的第count颗树
		for (TreePanelNode tree : treeLists)
		{
			if (count == 0)
				allocatePosition(tree);
			else
			{
				allocatePosition(tree);

				// 前一棵树的节点
				Vector<TreePanelNode> nodesOfTree = allNodes(treeLists.get(count - 1));

				// 相对于前一棵树往下移动这棵树的距离
				int modificationY = nodesOfTree.get(nodesOfTree.size() - 1).getY() + 80;

				// 往下移动本颗树
				for (TreePanelNode node : allNodes(tree))
					node.setY(node.getY() + modificationY);
			}

			count++;
		}

		for (TreePanelNode node : allNodes(treeLists))
			node.calculateAngle();

		return treeLists;
	}

	/**
	 *  将括号表达式转化为树，并分配位置和初始化角度
	 *  
	 * @param strWithFormat 包含一个或多个括号表达式的字符串
	 * @return
	 */
	public static ArrayList<TreePanelNode> fromTextToTree(String strWithFormat)
	{
		try
		{
			ArrayList<String> brackets = BracketUtil.readBrackets(strWithFormat);
			
			ArrayList<TreePanelNode> treeLists = new ArrayList<TreePanelNode>();
			for(String bracket : brackets)
			{
				TreePanelNode tree = generateTree(bracket);
				
				for (TreePanelNode aNode : allNodes(tree))
				{
					aNode.setRoot(tree);
				}
				
				treeLists.add(tree);
			}
			
			treeLists = allocatePosAndAngle(treeLists);

			for (TreePanelNode tree : treeLists)
			{
				for (TreePanelNode node : allNodes(tree))
				{
					node.calculateAngle();
				}
			}
			
			return treeLists;
		}
		catch (IOException e)
		{
			return new ArrayList<TreePanelNode>();
		}
		
//		String line = toOneLine(strWithFormat);
//		if (line.trim().length() == 0)
//			return null;
//
//		ArrayList<TreePanelNode> treeLists = allocatePosAndAngle(getTree(stringToList(format(line))));
//
//		for (TreePanelNode tree : treeLists)
//		{
//			for (TreePanelNode node : allNodes(tree))
//			{
//				node.calculateAngle();
//			}
//		}
//
//		return treeLists;
	}
	
	private static TreePanelNode generateTree(String bracketStr)
	{
		bracketStr = BracketUtil.format(bracketStr);
		
		List<String> parts = BracketUtil.stringToList(bracketStr);

		Stack<TreePanelNode> trees = new Stack<TreePanelNode>();
		for (int i = 0; i < parts.size(); i++)
		{
			String str = parts.get(i);
			if (!str.equals(")") && !str.equals(" ")) // 左括号或文法符号
			{
				TreePanelNode tn = new TreePanelNode(str);
				trees.push(tn);
			}
			else if (str.equals(" "))
			{

			}
			else if (str.equals(")"))
			{
				Stack<TreePanelNode> temp = new Stack<TreePanelNode>();
				while (!trees.peek().getValue().equals("("))
				{
					if (!trees.peek().getValue().equals(" "))
					{
						temp.push(trees.pop());
					}
				}
				
				trees.pop();
				TreePanelNode node = temp.pop();
				while (!temp.isEmpty())
				{
					temp.peek().setParent(node);
					if (temp.peek().getChildren().size() == 0)
					{
						TreePanelNode wordindexnode = temp.pop();

						node.addChild(wordindexnode);
					}
					else
					{
						node.addChild(temp.pop());
					}
				}
				
				trees.push(node);
			}
		}
		
		// 将表达式中的-LRB-和-RRB-转换为"(",")"
		TreePanelNode treeStruct = trees.pop();
//		TraverseTreeConvertRRBAndLRB(treeStruct);
		return treeStruct;
	}

	// 将树转化为一个字符串数组，其中包括添加的用于换行和空格的字符串
	public LinkedList<String> changeIntoText()
	{
		LinkedList<String> tree = new LinkedList<String>();
		if (treeToText(this, tree) != -1)
		{// 表示不是空树
			tree.set(0, "(");
			return tree;
		}
		else
			return null;
	}

	// 将树的节点插入到list中，并返回根节点在list中的索引。包括换行符和空格，便于将list输出到文本中成为一个多行有格式的括号表达式。
	private int treeToText(TreePanelNode root, LinkedList<String> listOfTree)
	{// 该函数表示将root为根的树排列好在list中，并返回root节点在list中的索引，同时判断该根节点是否有儿子节点，
		// 如果该节点有儿子节点且儿子节点不是叶子节点就在list中root的'('前插入换行符和一定的空格组成的字符串。
		if (root == null)
		{
			return -1;
		}

		int indexOfFirstChild = -1;
		int indexOfRoot = -1;
		if (root.getChildren().size() != 0)
		{
			int count = 0;
			for (; count < root.getChildren().size();)
			{// 把所有以root的儿子为根节点的子树插入到listOfTree中去，并返回第一个儿子在listOfTree中的索引
				count++;
				if (count == 1)
				{
					indexOfFirstChild = treeToText(root.getChildren().get(count - 1), listOfTree);
				}
				else
				{
					treeToText(root.getChildren().get(count - 1), listOfTree);
				}
			}
			// 处理根节点
			// System.out.println("indexOfFirst====" + indexOfFirstChild);

			if (!isInsertString(root.getChildren().get(0)))
			{// 判断root的第一个孩子节点前面是否添加调整格式的字符串，if成立表示没有添加
				if (!isInsertString(root))
				{// 判断root节点前面是否用添加格式字符串。未添加
					listOfTree.add(indexOfFirstChild - 1, root.getValue().toString());
					listOfTree.add(indexOfFirstChild - 1, " (");// root的index和第一个孩子返回的index大小相同
					listOfTree.add(")");
					indexOfRoot = indexOfFirstChild;
				}
				else
				{// root节点前面要添加格式字符串
					listOfTree.add(indexOfFirstChild - 1, root.getValue().toString());
					listOfTree.add(indexOfFirstChild - 1, " (");// root的index和第一个孩子返回的index大小相同
					String format = "";
					for (int i = 0; i < root.treeLevel(); i++)
					{
						format = format + " ";
						format = format + " ";
					}
					if (root.treeLevel() != 0)
						format = format.substring(0, format.length() - 1);
					listOfTree.add(indexOfFirstChild - 1, "\r\n" + format);
					listOfTree.add(")");
					indexOfRoot = indexOfFirstChild + 1;
				}
			}
			else
			{
				// 即判断root节点前面是否用添加格式字符串
				if (!isInsertString(root))
				{// root节点前面不用添加格式字符串
					listOfTree.add(indexOfFirstChild - 2, root.getValue().toString());
					listOfTree.add(indexOfFirstChild - 2, " (");// root的index和第一个孩子返回的index大小相同
					listOfTree.add(")");
					indexOfRoot = indexOfFirstChild - 1;
				}
				else
				{// root节点前面要添加格式字符串
					listOfTree.add(indexOfFirstChild - 2, root.getValue().toString());
					listOfTree.add(indexOfFirstChild - 2, " (");// root的index和第一个孩子返回的index大小相同
					String format = "\r\n";
					for (int i = 0; i < root.treeLevel(); i++)
					{
						format = format + "  ";
					}
					if (root.treeLevel() != 0)
						format = format.substring(0, format.length() - 1);
					listOfTree.add(indexOfFirstChild - 2, format);
					listOfTree.add(")");
					indexOfRoot = indexOfFirstChild;
				}
			}

		}
		else
		{// 处理root是叶子节点，结束递归的条件
			if (root.equals(this.getRoot()))
			{// 即树上只有一个根节点
				listOfTree.add("(");
				listOfTree.add(root.getValue().toString());
				listOfTree.add(")");
				indexOfRoot = 1;
			}
			else
			{
				listOfTree.add(" ");// 把叶子节点外边的括号删除
				listOfTree.add(root.getValue().toString());
				indexOfFirstChild = listOfTree.size() - 1;
				// listOfTree.add(")");
				indexOfRoot = indexOfFirstChild;
			}

		}

		return indexOfRoot;
	}

	// 判断list中该节点之前是否要插入格式字符串
	private boolean isInsertString(TreePanelNode node)
	{
		if (node == null)
			return false;
		
		if (node.equals(node.getRoot()))
			return false;
		
		if (node.getChildren().isEmpty())
			return false;
		else if (!node.getChildren().get(0).getChildren().isEmpty())
			return true;
		else
		{
			for (TreePanelNode siblingBeforeNode : node.getParent().getChildren())
			{
				if (siblingBeforeNode.equals(node))
					break;
				if (isInsertString(siblingBeforeNode))
					return true;
			}
			return false;
		}
	}

	public void addChild(TreePanelNode n)
	{
		addChild(children.size(), n);
	}

	public void addChild(int index, TreePanelNode n)
	{
		children.add(index, n);
		n.parent = this;
		// root设置???
	}

	public void removeChild(TreePanelNode n)
	{
		children.remove(n);
	}

	public void removeChild(int index)
	{
		children.remove(index);
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public Point getLocation()
	{
		return new Point(x, y);
	}

	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public Rectangle getBounds()
	{
		return new Rectangle(x, y, width, height);
	}

	public Vector<TreePanelNode> getChildren()
	{
		return children;
	}

	public void setChildren(Vector<TreePanelNode> children)
	{
		if (children != null)
			this.children = children;
		else
			this.children = new Vector<TreePanelNode>();
	}

	public TreePanelNode getParent()
	{
		return parent;
	}

	public void setParent(TreePanelNode parent)
	{
		this.parent = parent;
	}

	public TreePanelNode getRoot()
	{
		return root;
	}

	public void setRoot(TreePanelNode root)
	{
		this.root = root;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public double getAngle()
	{
		return angle;
	}

	public void setAngle(double angle)
	{
		this.angle = angle;
	}

	// 测试输出树的基本值
	public void print()
	{
		// 测试
		int count = -1;
		for (TreePanelNode theNode : allNodes(this.getRoot()))
		{
			count++;
			System.out.println("count: " + count + theNode.getValue() + "的x,y为：" + theNode.getX() + "," + theNode.getY()
					+ "角度是" + theNode.getAngle());
			if (!theNode.getChildren().isEmpty())
				System.out.println("firstChlild: " + theNode.getChildren().get(0).getValue().toString());
			if (theNode.getParent() != null)
				System.out.println("parent: " + theNode.getParent().getValue().toString());
		}
	}
	
	public int getChildrenNum()
	{
		return children.size();
	}
	
	public String getNodeName()
	{
		return value.toString();
	}
	
	public TreePanelNode getFirstChild()
	{
		return children.get(0);
	}
	
	public TreePanelNode getChild(int i)
	{
		return children.get(i);
	}
	
	/**
	 * 输出有缩进和换行的括号表达式
	 * 
	 * @param level
	 *            缩进的空格数
	 */
	public static String printTree(TreePanelNode tree, int level)
	{
		if (tree.getChildrenNum() == 1 && tree.getFirstChild().getChildrenNum() == 0)
		{
			return "(" + tree.getNodeName() + " " + BracketUtil.BracketConvert(tree.getFirstChild().getNodeName()) + ")";
		}
		else if (tree.getChildrenNum() == 1 && tree.getFirstChild().getChildrenNum() == 1
				&& tree.getFirstChild().getFirstChild().getChildrenNum() == 0)
		{
			return "(" + tree.getNodeName() + " " + "(" + tree.getFirstChild().getNodeName() + " "
					+ BracketUtil.BracketConvert(tree.getFirstChild().getFirstChild().getNodeName()) + ")" + ")";
		}
		else if (tree.getChildrenNum() > 1 && firstChildIsPosAndWord(tree))
		{
			String str = "";
			str += "(" + tree.getNodeName();
			str += " " + "(" + tree.getFirstChild().getNodeName() + " "
					+ BracketUtil.BracketConvert(tree.getFirstChild().getFirstChild().getNodeName()) + ")" + "\n";
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
								+ BracketUtil.BracketConvert(tree.getChild(i).getFirstChild().getNodeName()) + ")" + ")";
						return str;
					}
					else
					{
						str += " " + "(" + tree.getChild(i).getNodeName() + " "
								+ BracketUtil.BracketConvert(tree.getChild(i).getFirstChild().getNodeName()) + ")";
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
	private static boolean allChildrenIsPosAndWord(TreePanelNode tree)
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
			System.out.println("所有孩子都是词性标注tree.getChildren()==" + tree.getChildren());
		}
		return flag;
	}

	/**
	 * 判断是否当前节点第一个结点是词性标记和词的结构，第二个节点不是这种结构，就不要在去考虑第二个节点之后的节点了
	 * 
	 * @param tree
	 * @return
	 */
	private static boolean firstChildIsPosAndWord(TreePanelNode tree)
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

}
