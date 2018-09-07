package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * 合并chunk子树
 * 
 * @author 王馨苇
 *
 */
public class ChunkTreeCombineUtil
{

	/**
	 * 对CHUNK子树进行合并，就是合并start和join部分 说明：用于合并的chunk子树有头结点，合并的过程中要重新生成头结点，保持树依旧是带头结点的树
	 * 
	 * @param subTree
	 *            第二部CHUNK得到的若干棵子树
	 * @return
	 */
	public static List<HeadTreeNode> combineToHeadTree(List<HeadTreeNode> subTree, AbstractHeadGenerator headGen)
	{
		List<HeadTreeNode> combineChunk = new ArrayList<HeadTreeNode>();
		// 遍历所有子树
		for (int i = 0; i < subTree.size(); i++)
		{
			// 当前子树的根节点是start标记的
			if (subTree.get(i).getNodeNameLeftPart().equals("start"))
			{
				// 只要是start标记的就去掉root中的start，生成一颗新的子树，
				// 因为有些结构，如（NP(NN chairman)），只有start没有join部分，
				// 所以遇到start就生成新的子树
				HeadTreeNode node = new HeadTreeNode(subTree.get(i).getNodeNameRightPart());
				node.addChild(subTree.get(i).getFirstChild());
				node.setHeadWord(
						headGen.extractHeadWord(node, HeadRuleSetPTB.getNormalRuleSet(), HeadRuleSetPTB.getSpecialRuleSet()));
				node.setHeadPos(headGen.extractHeadPos(node, HeadRuleSetPTB.getNormalRuleSet(),
						HeadRuleSetPTB.getSpecialRuleSet()));
				subTree.get(i).getFirstChild().setParent(node);

				for (int j = i + 1; j < subTree.size(); j++)
				{
					// 判断start后是否有join如果有，就和之前的start合并
					if (subTree.get(j).getNodeNameLeftPart().equals("join"))
					{
						node.addChild(subTree.get(j).getFirstChild());
						subTree.get(j).getFirstChild().setParent(node);
					}
					else if (subTree.get(j).getNodeNameLeftPart().equals("start")
							|| subTree.get(j).getNodeNameLeftPart().equals("other"))
					{
						break;
					}
					node.setHeadWord(headGen.extractHeadWord(node, HeadRuleSetPTB.getNormalRuleSet(),
							HeadRuleSetPTB.getSpecialRuleSet()));
					node.setHeadPos(headGen.extractHeadPos(node, HeadRuleSetPTB.getNormalRuleSet(),
							HeadRuleSetPTB.getSpecialRuleSet()));
				}
				// 将一颗合并过的完整子树加入列表
				combineChunk.add(node);
				// 标记为other的，去掉other
			}
			else if (subTree.get(i).getNodeName().equals("other"))
			{
				subTree.get(i).getFirstChild().setParent(null);
				subTree.get(i).getFirstChild().setHeadWord(subTree.get(i).getChildren().get(0).getHeadWord());
				subTree.get(i).getFirstChild().setHeadPos(subTree.get(i).getChildren().get(0).getHeadPos());
				combineChunk.add(subTree.get(i).getFirstChild());
			}
		}
		return combineChunk;
	}

}
