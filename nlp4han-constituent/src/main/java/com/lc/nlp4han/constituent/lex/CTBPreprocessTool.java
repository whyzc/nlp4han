package com.lc.nlp4han.constituent.lex;

import com.lc.nlp4han.constituent.TreeNode;

public class CTBPreprocessTool
{
	//应该是用不到，虽然Collins论文中这样指明，但是在回溯恢复整个树结构时显得尤为麻烦
	/**
	 * 移除逗号和冒号以外的标点符号，后续将逗号和冒号（与CC功能类似）做并列结构处理
	 */
	/**public void removePunctuation(TreeNode node)
	{
		if (node.getNodeName().equals("PU"))
		{
			String pu = node.getFirstChildName();
			if (!(pu.equals("、") ))
			{
				//移除
				node.setFlag(false);
				node.getFirstChild().setFlag(false);
				node.getParent().setFlag(false);
			}
		}
	}*/
	/**
	 * 在句法树中添加基本名词短语节点
	 * 该节点为NP，并且以该节点为根的结构树中不存在NP(或者存在类似NP->NN->word)
	 * @param node
	 */
	public void AddNPBNode(TreeNode node)
	{
		if (IsNPB(node))
		{// 若从中间插入NPB则需要继承node节点的所有孩子,显得繁琐，故选择在上层插入
			node.setNewName("NPB");
			TreeNode newNode = new TreeNode("NP");
			newNode.setParent(node.getParent());
			newNode.setChild(0, node);
			node.setParent(newNode);
		}
	}
	private boolean IsNPB(TreeNode node)
	{
		if (!node.getNodeName().equals("NP"))
		{
			return false;
		}
		else
		{
			for(TreeNode child:node.getChildren()) {
				if(!traverseNode(child)) {
					return false;
				}
			}
			return true;
		}
	}
	private boolean  traverseNode(TreeNode node) {
		if(node.getNodeName().equals("NP")&&!(node.getChildrenNum() == 1 && node.getChild(0).getChildrenNum() == 1
				&& node.getChild(0).getChild(0).getChildrenNum() == 0)) {
			return false;
		}else {
			for(TreeNode child:node.getChildren()) {
				if(!traverseNode(child)) {
					return false;
				}
			}
		}
		return true;
	}
}
