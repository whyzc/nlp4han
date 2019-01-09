package com.lc.nlp4han.srl.framenet;

import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.srl.tree.SRLTreeNode;
import com.lc.nlp4han.srl.tree.SemanticRoleStructure;

/**
 * 对需要解析的句子进行处理的工具类
 * @author qyl
 *
 */
public class PreprocessUtil
{
	/**
	 * 由字符串句子生成节点树
	 * @param sentence
	 * @return
	 */
	public static TreeNode generateTree(String sentence) {
		return null;
	}

	/**
	 * 得到一颗节点树的目标词
	 * @param node
	 */
	public static Predicate getPredicate(String sentence) {
		return null;
	}
	
	/**
	 * 由普通的节点树转换为语义标注节点树
	 * @param node
	 * @param srs
	 * @return
	 */
   public static SRLTreeNode treeToSRLTree(TreeNode node,SemanticRoleStructure[] srs) {
	return null;   
   }
}
