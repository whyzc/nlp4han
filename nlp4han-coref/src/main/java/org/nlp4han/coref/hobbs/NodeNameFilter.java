package org.nlp4han.coref.hobbs;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 根据节点名过滤候选
 *
 */
public class NodeNameFilter extends CandidateFilter
{
//	private List<TreeNode> treeNodes;
	private String[] treeNodeNames;

	public NodeNameFilter()
	{

	}

//	public NodeNameFilter(List<TreeNode> treeNodes)
//	{
//		this.treeNodes = treeNodes;
//	}
//
//	public NodeNameFilter(List<TreeNode> treeNodes, String[] treeNodeNames)
//	{
//		super();
//		this.treeNodes = treeNodes;
//		this.treeNodeNames = treeNodeNames;
//	}
	
	public NodeNameFilter(String[] treeNodeNames)
	{
		this.treeNodeNames = treeNodeNames;
	}

	// 设置接受的结点名
	public void setTreeNodeName(String[] treeNodeName)
	{
		this.treeNodeNames = treeNodeName;
	}

	@Override
	public List<TreeNode> filter(List<TreeNode> treeNodes)
	{
		if (treeNodeNames != null)
		{
			for (int i = 0; i < treeNodes.size(); i++)
			{
				boolean flag = false;
				for (int j = 0; j < treeNodeNames.length; j++)
				{
					if (treeNodeNames[j].equals(treeNodes.get(i).getNodeName()))
					{
						flag = true;
						break;
					}
				}
				if (flag)
					continue;
				treeNodes.remove(i);
				i--;
			}
		}
		return treeNodes;
	}

//	@Override
//	public void setFilteredNodes(List<TreeNode> treeNodes)
//	{
//		this.treeNodes = treeNodes;
//
//	}
}
