package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 将动作序列转成一棵树
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ActionsToTree
{

	/**
	 * 第一步词性标注
	 * 
	 * @param words
	 *            词语
	 * @param actions
	 *            动作序列
	 * @return
	 */
	private static List<TreeNode> getPosTree(List<String> words, List<String> actions)
	{
		// 第一步pos
		List<TreeNode> postree = new ArrayList<TreeNode>();
		for (int i = 0; i < words.size(); i++)
		{
			TreeNode node = new TreeNode(words.get(i));
			TreeNode actionsNode = new TreeNode(actions.get(i));
			
			actionsNode.addChild(node);
			node.setParent(actionsNode);
			node.setWordIndex(i);
			
			postree.add(actionsNode);
		}
		
		return postree;
	}

	/**
	 * 第二步chunk标记
	 * 
	 * @param words
	 *            词语
	 * @param actions
	 *            动作序列
	 * @return
	 */
	private static List<TreeNode> getChunkTree(List<TreeNode> postree, List<String> actions)
	{
		// 第二部chunk
		// 不用在这里设置头结点
		List<TreeNode> chunktree = new ArrayList<TreeNode>();
		int len = postree.size();
		for (int i = 0; i < len; i++)
		{
			TreeNode chunk = new TreeNode(actions.get(i + len));
			chunk.addChild(postree.get(i));
			postree.get(i).setParent(chunk);
			
			chunktree.add(chunk);
		}
		
		return chunktree;
	}

	/**
	 * build和check步得到完整的树
	 * 
	 * @param len
	 *            词语的长度，作用是根据这个长度计算出当前应从动作序列中的哪个位置开始
	 * @param combine
	 *            combine之后的子树
	 * @param actions
	 *            动作序列
	 * @return
	 */
	private static TreeNode getTree(int len, List<TreeNode> combine, List<String> actions)
	{
		// 第四部build和check
		int j = 0;
		
		// 遍历上一步得到的combine，根据action进行操作
		for (int i = 0; i < combine.size(); i++)
		{
			TreeNode node = new TreeNode(actions.get(j + 2 * len));
			
			node.addChild(combine.get(i));
			combine.get(i).setParent(node);
			
			combine.set(i, node);
			
			j++;
			if (actions.get(j + 2 * len).equals("no"))
			{// 检测为no什么都不做

			}
			else if (actions.get(j + 2 * len).equals("yes"))
			{// 检测为yes，要和前面到start的部分合并
				// 合并的時候是从当前的位置往前寻找，找到start
				int currentIndex = i;
				int preIndex = -1;// 记录前面的start位置
				while (!combine.get(i--).getNodeNameLeftPart().equals("start"))
				{
					if (i < 0)
					{
						break;
					}
				}
				
				preIndex = i + 1;
				// 进行合并
				// 建立合并后的父节点
				TreeNode combineNode = new TreeNode(combine.get(preIndex).getNodeNameRightPart());
				for (int k = preIndex; k <= currentIndex; k++)
				{
					combineNode.addChild(combine.get(k).getFirstChild());
					combine.get(k).getFirstChild().setParent(combineNode);
				}
				
				combine.set(preIndex, combineNode);
				
				// 删除那些用于合并的join
				for (int k = currentIndex; k >= preIndex + 1; k--)
				{
					combine.remove(preIndex + 1);
				}
				
				// 从合并后的位置继续开始搜索
				i = preIndex - 1;
			}
			if (j + 1 + 2 * len < actions.size())
			{
				j++;
			}
			else
			{
				break;
			}
		}
		
		return combine.get(0);
	}
	
	/**
	 * chunk步得到的结果进行合并 说明：用于合并的chunk子树没有头结点，合并之后也没有头结点
	 * 
	 * @param chunktree
	 *            chunk子树
	 * @return
	 */
	private static List<TreeNode> combineToTree(List<TreeNode> chunktree)
	{
		// 第三部合并
		List<TreeNode> combine = new ArrayList<TreeNode>();
		
		// 遍历所有子树
		for (int i = 0; i < chunktree.size(); i++)
		{
			// 当前子树的根节点是start标记的
			if (chunktree.get(i).getNodeNameLeftPart().equals("start"))
			{
				// 只要是start标记的就去掉root中的start，生成一颗新的子树，
				// 因为有些结构，如（NP(NN chairman)），只有start没有join部分，
				// 所以遇到start就生成新的子树
				TreeNode node = new TreeNode(chunktree.get(i).getNodeNameRightPart());
				
				node.addChild(chunktree.get(i).getFirstChild());
				chunktree.get(i).getChildren().get(0).setParent(node);

				// 判断start后是否有join如果有，就和之前的start合并
				for (int j = i + 1; j < chunktree.size(); j++)
				{				
					if (chunktree.get(j).getNodeNameLeftPart().equals("join"))
					{
						node.addChild(chunktree.get(j).getFirstChild());
						chunktree.get(j).getFirstChild().setParent(node);
					}
					else if (chunktree.get(j).getNodeNameLeftPart().equals("start")
							|| chunktree.get(j).getNodeNameLeftPart().equals("other"))
					{
						break;
					}
				}
				
				// 将一颗合并过的完整子树加入列表
				combine.add(node);			
			}
			else if (chunktree.get(i).getNodeName().equals("other")) // 标记为other的，去掉other
			{
				chunktree.get(i).getFirstChild().setParent(null);
				
				combine.add(chunktree.get(i).getFirstChild());
			}
		}
		
		return combine;
	}

	/**
	 * 动作序列转成一颗完整的树
	 * 
	 * @param words
	 *            词语
	 * @param actions
	 *            动作序列
	 * @return
	 */
	public static TreeNode actionsToTree(List<String> words, List<String> actions)
	{
		// 第一步pos
		List<TreeNode> postree = getPosTree(words, actions);
		
		// 第二部chunk
		List<TreeNode> chunktree = getChunkTree(postree, actions);
		
		// 第三部合并
		// 需要为合并后的结点设置头结点
		List<TreeNode> combine = combineToTree(chunktree);
		
		// 第四部build和check
		TreeNode tree = getTree(words.size(), combine, actions);
		
		return tree;
	}
}
