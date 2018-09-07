package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

public class BaseChunkSearcher
{
	// 维护一个基本短语标记的列表
	private static List<String> tags = new ArrayList<>();
	static
	{
		tags.add("NP");
		tags.add("ADJP");
		tags.add("ADVP");
		tags.add("CLP");
		tags.add("DNP");
		tags.add("DVP");
		tags.add("DP");
		tags.add("PP");
		tags.add("QP");
		tags.add("UCP");
		tags.add("VP");
		tags.add("LCP");
		tags.add("IP");
		tags.add("PRN");
		tags.add("LST");
		tags.add("CP");
		tags.add("FRAG");
	}

	/**
	 * 提取基本短语块
	 * 
	 * 可以提取的节点在节点名字上做标记
	 * 
	 * @param tn
	 *            短语结构树
	 * @param targetTags
	 *            待提取的基本短语块
	 * 
	 */

	public static void search(TreeNode tn, List<String> targetTags)
	{
		List<? extends TreeNode> children = null;

		boolean childHasNoXP = true;
		boolean tried = false;// 节点是否被遍历过
		if (tn != null)
		{
			children = tn.getChildren();
			if (!children.isEmpty())
			{
				for (TreeNode child : children)
				{
					// 判断该节点的子树中是否包含短语块
					String name = child.getNodeName();
					if (tags.contains(name))
					{
						childHasNoXP = false;
						break;
					}
				}

				tried = true;
			}
		}

		if (tried)
		{
			if (childHasNoXP)
			{
				if (targetTags.contains("all") && tags.contains(tn.getNodeName()))
				{
					tn.setNewName("tag:" + tn.getNodeName());
				}
				else
				{
					if (targetTags.contains(tn.getNodeName()))
					{
						tn.setNewName("tag:" + tn.getNodeName());
					}
				}
				return;
			}
		}

		for (int i = 0; i < tn.getChildrenNum(); i++)// 遍历
		{
			search(children.get(i), targetTags);
		}
	}

	/**
	 * 将短语结构树转换成已标注基本短语的字符串
	 * 
	 * @param t
	 *            待转换的短语结构树
	 */
	public static String toChunkString(TreeNode t)
	{

		if (t.isLeaf())
		{

			return t.getNodeName() + "/";

		}
		else
		{
			String treestr = "";

			for (TreeNode node : t.getChildren())
			{
				treestr += toChunkString(node);

				if (treestr.endsWith("/"))
				{
					treestr += t.getNodeName() + " ";
				}
			}

			if (t.getNodeName().startsWith("tag:"))
			{
				treestr = "[" + treestr.trim() + "]" + t.getNodeName().substring(4, t.getNodeName().length()) + " ";
			}

			return treestr;
		}
	}
}
