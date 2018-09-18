package com.lc.nlp4han.constituent.lex;

import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
/**
 * 在以前代码基础上添加生成Verb和headIndex属性的代码，待重构
 * @author qyl
 *
 */
public class TreeToHeadTreeForCollins
{
		/**
		 * 将一颗无头结点的树转成带头结点,头结点的索引和是否含有动词的标记verb(boolean值)的树
		 * @param treeNode
		 * @return
		 */
		public static HeadTreeNodeForCollins treeToHeadTree(TreeNode treeNode, AbstractHeadGenerator headGen)
		{
			String[] verbArray= { "VA", "VC", "VE", "VV", "BA", "LB"};
			HashSet<String> verbs=new HashSet<String>();
			for(String verb:verbArray) {
				verbs.add(verb);
			}
			
			String treeStr = "(" + treeNode.toStringWordIndexNoNone() + ")";
			treeStr = BracketExpUtil.format(treeStr);
			
			int indexTree;// 记录当前是第几颗子树
			List<String> parts = BracketExpUtil.stringToList(treeStr);
			Stack<HeadTreeNodeForCollins> tree = new Stack<HeadTreeNodeForCollins>();
			for (int i = 0; i < parts.size(); i++)
			{
				if (!parts.get(i).equals(")") && !parts.get(i).equals(" "))
				{
					tree.push(new HeadTreeNodeForCollins(parts.get(i)));
				}
				else if (parts.get(i).equals(" "))
				{

				}
				else if (parts.get(i).equals(")"))
				{
					indexTree = 0;
					Stack<HeadTreeNodeForCollins> temp = new Stack<HeadTreeNodeForCollins>();
					while (!tree.peek().getNodeName().equals("("))
					{
						if (!tree.peek().getNodeName().equals(" "))
						{
							temp.push(tree.pop());
						}
					}
					tree.pop();
					HeadTreeNodeForCollins node = temp.pop();
					while (!temp.isEmpty())
					{
						temp.peek().setParent(node);
						temp.peek().setIndex(indexTree++);
						if (temp.peek().getChildrenNum() == 0)
						{
							HeadTreeNode wordindexnode = temp.peek();
							String[] str = temp.peek().getNodeName().split("\\[");
							wordindexnode.setNewName(str[0]);
							wordindexnode.setWordIndex(Integer.parseInt(str[1].substring(0, str[1].length() - 1)));
							node.addChild(wordindexnode);
						}
						else
						{
							node.addChild(temp.peek());
						}
						temp.pop();
					}
					//识别并标识基本名词短语
					CTBPreprocessTool.AddNPBNode(node);
					// 设置头节点的部分
					// 为每一个非终结符，且不是词性标记的设置头节点
					// 对于词性标记的头节点就是词性标记对应的词本身
					//词性标记的头结点的索引为0
					// (1)为词性标记的时候，头节点为词性标记下的词语
					if (node.getChildrenNum() == 1 && node.getFirstChild().getChildrenNum() == 0)
					{
						node.setHeadWord(node.getFirstChildName());
						node.setHeadPos(node.getNodeName());
						node.setHeadChildIndex(0);
						if(verbs.contains(node.getNodeName())) {
							node.setVerb(true);						
						}
						// (2)为非终结符，且不是词性标记的时候，由规则推出
					}
					else if (!node.isLeaf())
					{
						node.setHeadWord(headGen.extractHeadWord(node));
						node.setHeadPos(headGen.extractHeadPos(node));
						node.setHeadChildIndex(headGen.extractHeadIndex(node));
						for(HeadTreeNode child:node.getChildren()) {
							HeadTreeNodeForCollins child1=(HeadTreeNodeForCollins)child;
							if(child1.isVerb()==true) {
								node.setVerb(true);
							}
						}
					}
					tree.push(node);
				}
			}
			HeadTreeNodeForCollins headTreeNode=tree.pop();
			TraverseTreeConvertRRBAndLRB(headTreeNode);
			return headTreeNode;
		}
		private static void TraverseTreeConvertRRBAndLRB(HeadTreeNode node) {
			if(node.getChildrenNum()==0) {
				if(node.getNodeName().equals("-LRB-")) {
					   node.setNewName("(");
					}else if(node.getNodeName().equals("-RRB-")) {
					   node.setNewName(")");
					}
				return;
			}else if(node.getHeadWord().equals("-LRB-")) {
				  node.setHeadWord("(");
			}else if(node.getHeadWord().equals("-RRB-")) {
				node.setHeadWord(")");
			}
			for(HeadTreeNode childNode:node.getChildren()) {
				TraverseTreeConvertRRBAndLRB(childNode);
			}
		}
}
