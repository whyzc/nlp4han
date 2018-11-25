package org.nlp4han.tools;

import java.util.ArrayList;

//一个括号表达式对应一个TreeAtTxt对象
public class TreeAtTxt
{
	private ArrayList<TreePanelNode> treeListInOneTree;// 只有一颗树的森林，用来表示某个txt文件中的一个由括号表达式表示的树

	// 树所在的文件
	private String txtPath;

	public TreeAtTxt()
	{
	}

	public TreeAtTxt(ArrayList<TreePanelNode> treeListWithOneTree)
	{
		this.treeListInOneTree = treeListWithOneTree;
	}

	public TreeAtTxt(TreePanelNode treeRoot)
	{
		treeListInOneTree.add(treeRoot);
	}

	/**
	 * 
	 * @param treeRoot 树根
	 * @param txtPath 树所在的文件名
	 */
	public TreeAtTxt(TreePanelNode treeRoot, String txtPath)
	{
		this.treeListInOneTree = new ArrayList<TreePanelNode>();
		treeListInOneTree.add(treeRoot);
		this.txtPath = txtPath;
	}

//	/*
//	 * 
//	 *
//	 * 该方法将由txt文件中括号表达式组成的一个String转换为一个个TreeAtTxt对象，并封装到ArrayList中。
//	 * TreeAtTxt的txtPath属性为该文件路径，treeListWithOneTree属性为任意一个括号表达式对应的森林。
//	 * 
//	 */
//	public ArrayList<TreeAtTxt> getAllTreeListsOfOneTxt(String strOfExpressions, String txtPath)
//	{
//		ArrayList<TreeAtTxt> treeLists = new ArrayList<TreeAtTxt>();
//		ArrayList<TreePanelNode> trees = new TreePanelNode().fromTextToTree(strOfExpressions);
//		if (trees == null)
//			return null;
//		for (TreePanelNode tree : trees)
//		{
//			treeLists.add(new TreeAtTxt(tree, txtPath));
//		}
//		return treeLists;
//	}

	public String treePositionAtTxt(ArrayList<TreeAtTxt> allTreesAtTxt)
	{
		String txtPath = this.txtPath;
		int count = 0;
		int position = 0;
		for (int i = 0; i < allTreesAtTxt.size(); i++)
		{
			if (allTreesAtTxt.get(i).getTxtPath().equals(txtPath))
			{
				count++;
				if (allTreesAtTxt.get(i).equals(this))
					position = count;
			}
			else if (count != 0)
			{
				break;
			}

		}
		return position + " / " + count;
	}

	public ArrayList<TreePanelNode> getTreeListInOneTree()
	{
		return treeListInOneTree;
	}

	public void setTreeLisInOneTree(ArrayList<TreePanelNode> treeListWithOneTree)
	{
		this.treeListInOneTree = treeListWithOneTree;
	}

	public String getTxtPath()
	{
		return txtPath;
	}

	public void setTxtPath(String txtPath)
	{
		this.txtPath = txtPath;
	}

}
