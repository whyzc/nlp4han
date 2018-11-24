package com.lc.nlp4han.constituent;

import java.util.List;


/**
 * 剪枝规则，用于删除结构树中过小的组块标记
 * @author 杨智超
 *
 */
public class PruningRules
{
	private static int count = 0;
	
	
	/**
	 * 对结构树root运行剪枝规则
	 */
	public static void run(TreeNode root)
	{
		boolean flag = false;
		while (true)
		{
			flag |=  CLP(root);
			flag |=  UCP(root);
			flag |=  ADJP_ADVP(root);
			flag |=  NP_ADJP(root);
			flag |=  VP_ADVP(root);
			flag |=  VP_VP(root);
			//flag |=  NP_DP(root);
			if (!flag)
			{
				count++;
				postProcessing(root);
				break;
			}
			else
				flag = false;
		}
	}
	
	// 为得到更加合理的组块，自己定义方法
	private static void postProcessing(TreeNode root)
	{
		// 名词词性标记
		String[] nouns = {"NN", "NR", "NT"};
		
		// 所有词性标记
		//String[] pos = {"AD", "AS", "BA", "CC", "CD", "CS", "DEC", "DEG", "DER", "DEV", "DT", "ETC", "FW", "IJ", "JJ", "LB", "LC", "M", "MSP", "NN", "NR", "NT", "OD", "ON", "P", "PN", "PU", "SB", "SP", "VA", "VC", "VE", "VV"};
		
		// 动词词性标记
		String[] verbs = {"VA", "VC", "VE", "VV"};
		
		String[] npos = {"NN", "NR", "NT", "JJ", "CD", "DT"};
		
		String[] vpos = {"VV", "VC", "VA", "AD", "CS", "MSP", "DER", "SB"};
		
		String[] adj = {"JJ"};
		
		String[] adv = {"AD"};
		
		delete(root, "NP", "NP", nouns, npos);  // 例如(NP(ADJP(JJ 固定))(NP(NN 资产)))中，ADJP会被删除，此时此方法会将“资产”上方的NP删除
										//形成(NP(JJ 固定)(NN 资产))，最终[固定/jj 资产/NN]NP组块
		
		delete(root, "VP", "VP", verbs, vpos);
		
		delete(root, "ADJP", "ADJP", adj, adv);
		
	}


	/**
	 * 对特定结点进行剪枝，自己定义，用于改善组块提取结果
	 */
	private static void delete(TreeNode root, String nodeName, String parentNodeName, String[] childrenTypes, String[] brotherTypes)
	{
		List<TreeNode> nodes= getNodes(root, nodeName, parentNodeName);
		for (int i=0 ; i<nodes.size() ; i++)
		{
			List<? extends TreeNode> children = nodes.get(i).getChildren();
			
			int index  = TreeNodeUtil.getIndex(nodes.get(i));
			
			if (index<1 || !TreeNodeUtil.isNodeWithSpecifiedName(nodes.get(i).getParent().getChild(index-1), brotherTypes))
			{// 结点位于兄弟结点中的第一个或其前一个兄弟结点结点名不在brotherTypes中，则该结点不被剪枝
				nodes.remove(i);
				i--;
				continue;
			}
			
			if (!TreeNodeUtil.allNodeNames(children, childrenTypes))
			{// 若该结点的孩子中有一个结点名不在childrenTypes中，则该结点不被剪枝
				nodes.remove(i);
				i--;
			}
		}
		
		joinInPar(nodes);
		
	}


	/**
	 * 将nodes中的每个结点下的孩子，转移到其父结点下
	 */
	private static boolean joinInPar(List<TreeNode> nodes)
	{
		if (nodes != null && nodes.size()>0)
		{		
			for (TreeNode node : nodes)
			{
				List<? extends TreeNode> brothers = node.getParent().getChildren();
				int index=0;
				for (int i=0 ; i<brothers.size() ; i++)
				{
					if (node == brothers.get(i))
					{
						index = i;
						break;
					}
				}
				TreeNode parent = node.getParent();
				join(node, parent, index);
			}
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 将nodes中的每个结点下的孩子，转移到相邻的下一个兄弟结点下
	 */
	private static boolean joinInBro(List<TreeNode> nodes)
	{
		if (nodes != null && nodes.size()>0)
		{		
			for (TreeNode node : nodes)
			{
				List<? extends TreeNode> brothers = node.getParent().getChildren();
				int index=0;
				for (int i=0 ; i<brothers.size() ; i++)
				{
					if (node == brothers.get(i))
					{
						index = i;
						break;
					}
				}
				
				TreeNode nextBrother = brothers.get(index+1);
				join(node, nextBrother, 0);
			}
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 结点node1下的所有孩子结点移动到node2下，从node2孩子列表第index个位置开始添加，再将node1删除
	 */
	private static boolean join(TreeNode node1, TreeNode node2, int index)
	{
		if (node1 != null && node2 != null && index < node2.getChildrenNum())
		{		
			List<TreeNode> ts = (List<TreeNode>)node2.getChildren();
			int i = index;
			for (TreeNode child : node1.getChildren())
			{
				child.setParent(node2);
				ts.add(i, child);
				i++;
			}
			
			node1.getParent().getChildren().remove(node1);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * 对NP结点下的ADJP进行剪枝
	 */
	private static boolean NP_ADJP(TreeNode root)
	{
		
		List<TreeNode> nodes= getNodes(root, "ADJP", "NP");
		removeNodesAfterHead(nodes);
		return joinInPar(nodes);
	}
	
	/**
	 * 对ADJP结点下的ADVP进行剪枝
	 */
	private static boolean ADJP_ADVP(TreeNode root)
	{
		List<TreeNode> nodes= getNodes(root, "ADVP", "ADJP");
		removeNodesAfterHead(nodes);
		return joinInPar(nodes);
	}
	
	
	/*private static boolean NP_DP(TreeNode root)
	{//TODO：此条规则是我自己加的，例如：(NP(DP(DT 全))(NP(NN 省)))中，将“全省”作为一个名词组块
		List<TreeNode> nodes= getNodes(root, "DP", "NP");
		for (int i=0 ; i<nodes.size() ; i++)
		{
			int index = TreeNodeUtil.getIndex(nodes.get(i));
			if ( (index == nodes.get(i).getParent().getChildrenNum()-1)  || !nodes.get(i).getParent().getChild(index+1).getNodeName().equals("NP"))
			{// 若结点位于兄弟结点中的最后一个或其后面一个兄弟结点不为“NP”，则该结点不被剪枝
				nodes.remove(i);
				i--;
			}
		}
		return joinInPar(nodes);
	}*/
	
	/**
	 * 对VP结点下的ADVP进行剪枝
	 */
	private static boolean VP_ADVP(TreeNode root)
	{
		String[] VerbsAndVP = { "VC", "VA", "VE", "VV", "VP"};
		List<TreeNode> nodes= getNodes(root, "ADVP", "VP");
		for (int i=0 ; i<nodes.size() ; i++)
		{
			if (!isFrontOfSpecficNode(nodes.get(i), VerbsAndVP))
			{
				nodes.remove(i);
				i--;
			}
		}
		return joinInPar(nodes);
	}
	
	/**
	 * 对VP结点下的VP进行剪枝
	 */
	private static boolean VP_VP(TreeNode root)
	{
		String[] VerbsAndADVP = {"VC", "VA", "VE", "VV", "AD"};
		List<TreeNode> nodes= getNodes(root, "VP", "VP");
		for (int i=0 ; i<nodes.size() ; i++)
		{
			if (!isAfterSpecficNode(nodes.get(i), VerbsAndADVP))
			{
				nodes.remove(i);
				i--;
			}
		}
		return false;
	}
	
	/**
	 * 树root下，若存在QP下的CLP结点，将其删除并返回true；若不存在，返回false。
	 */
	private static boolean CLP(TreeNode root)
	{
		List<TreeNode> nodes= getNodes(root, "CLP", "QP");
		List<TreeNode> nodes2 = getNodes(root, "CLP", "DP");
		List<TreeNode> nodes3 = getNodes(root, "CLP", "CLP");
		
		nodes.addAll(nodes2);
		nodes.addAll(nodes3);
		return joinInPar(nodes);
	}
	
	/**
	 * 树root下，若存在UCP结点，将其删除并返回true；若不存在，返回false。
	 */
	private static boolean UCP(TreeNode root)
	{
		List<TreeNode> nodes = TreeNodeUtil.getNodesWithSpecifiedName(root, new String[] {"UCP"});
		return joinInPar(nodes);
	}
	
	/**
	 * 结点node是否在其父节点的中心词前
	 */
	private static boolean isFrontOfHead(TreeNode node)
	{
		TreeNode parent = node.getParent();
		int index = TreeNodeUtil.getIndex(node);
		AbstractHeadGenerator headGen = new HeadGeneratorCollins(new HeadRuleSetCTB());
		HeadTreeNode headTree1 = TreeToHeadTree.treeToHeadTree(parent, headGen);
		
		String headWordOfParent = headTree1.getHeadWord();
		String headWordOfCur = headTree1.getChildHeadWord(index);
		
		List<TreeNode> leaves = TreeNodeUtil.getAllLeafNodes(parent);
		for (TreeNode leaf : leaves)
		{
			if (leaf.getNodeName().equals(headWordOfCur))
				return true;
			else if (leaf.getNodeName().equals(headWordOfParent))
				return false;
		}
		throw new RuntimeException("错误！");
	}
	
	/**
	 * 在树root下，得到结点名为nodeName且其父节点名为parentNodeName的所有结点
	 */
	private static List<TreeNode> getNodes(TreeNode root, String nodeName, String parentNodeName)
	{
		List<TreeNode> result = TreeNodeUtil.getNodesWithSpecifiedName(root, new String[] {nodeName});
		for (int i=0 ; i<result.size() ; i++)
		{
			if (!result.get(i).getParent().getNodeName().equals(parentNodeName))
			{
				result.remove(i);
				i--;
			}
			
		}
		return result;
	}
	
	/**
	 * nodes中存在某个结点，其在中心词后，则将其删除
	 */
	private static void removeNodesAfterHead(List<TreeNode> nodes)
	{
		for (int i=0 ; i<nodes.size() ; i++)
		{
			if (!isFrontOfHead(nodes.get(i)))
			{
				nodes.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * nodes中存在某个结点，其在中心词前，则将其从列表中移除
	 */
	private static void removeNodesInFrontOfHead(List<TreeNode> nodes)
	{
		for (int i=0 ; i<nodes.size() ; i++)
		{
			if (isFrontOfHead(nodes.get(i)))
			{
				nodes.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * 结点node是否在nodeNames中的结点前面
	 */
	private static boolean isFrontOfSpecficNode(TreeNode node, String[] nodeNames) 
	{
		TreeNode parent  = node.getParent();
		boolean flag = false;
		for (TreeNode n : parent.getChildren())
		{
			if (n==node)
				flag = true;
			else if (TreeNodeUtil.isNodeWithSpecifiedName(n, nodeNames))
			{
				if (flag)
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	/**
	 * 结点node是否在nodeNames中的结点后面
	 */
	private static boolean isAfterSpecficNode(TreeNode node, String[] nodeNames) 
	{
		TreeNode parent  = node.getParent();
		boolean flag = false;
		for (TreeNode n : parent.getChildren())
		{
			if (TreeNodeUtil.isNodeWithSpecifiedName(n, nodeNames) )
				flag = true;
			else if (n==node)
			{
				if (flag)
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	
}
