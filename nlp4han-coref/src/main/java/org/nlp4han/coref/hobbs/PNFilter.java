package org.nlp4han.coref.hobbs;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 用于过滤中心词为代词的NP候选词
 * 
 * @author 杨智超
 *
 */
public class PNFilter extends FilterWrapper
{

	public PNFilter(CandidateFilter filter)
	{
		this.filter = filter;
	}

	@Override
	public List<TreeNode> filter()
	{
		List<TreeNode> treeNodes = filter.filter();
		
		for (int i = 0; i < treeNodes.size(); i++)
		{
			TreeNode node = treeNodes.get(i);
			if (node.getNodeName().equals("NP"))
			{
				TreeNode head = TreeNodeUtil.getHead(node, NPHeadRuleSetPTB.getNPRuleSet());
				String strOfHead = TreeNodeUtil.getString(head);
				if (isPronoun(strOfHead))
				{
					treeNodes.remove(i);
					i--;
				}
			}
		}
		return treeNodes;
	}

	@Override
	public void setFilteredNodes(List<TreeNode> treeNodes)
	{
		filter.setFilteredNodes(treeNodes);
	}

	private boolean isPronoun(String str)
	{
		String[] pronouns = { "我", "我们", "你", "你们", "她", "她们", "他", "他们", "它", "它们" };
		if (str != null)
			for (String pro : pronouns)
			{
				if (str.equals(pro))
					return true;
			}
		return false;
	}

}
