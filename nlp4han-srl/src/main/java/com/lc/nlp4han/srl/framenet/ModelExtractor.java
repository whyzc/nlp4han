package com.lc.nlp4han.srl.framenet;

import java.util.ArrayList;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadRuleSetCTB;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;
import com.lc.nlp4han.srl.tree.SRLTreeNode;
import com.lc.nlp4han.srl.tree.TreeToSRLTree;

/**
 * 模型提取器
 * @author qyl
 *
 */
public class ModelExtractor
{
	
	public ModelExtractor()
	{
		
	}
	
	public static SRLModel generateModel(ArrayList<String> list) {
		
		for(String sentence: list) {
		}
		return new SRLModel();
	}

	/**
	 * 从单个句子中提取信息
	 * @param sentece
	 */
	public void ExtractInfo(String sentence) {
		
		//生成句法树
		TreeNode tree=generateTree(sentence);
		
		//生成头结点句法树
		AbstractHeadGenerator headGen = new HeadGeneratorCollins(new HeadRuleSetCTB());
		HeadTreeNode headtree=TreeToHeadTree.treeToHeadTree(tree,headGen);
		
		//转换为语义角色标注树
		SRLTreeNode srlTree=TreeToSRLTree.treeToSRLTree(headtree, null, null);
		
		//提取特征
		ArrayList<String> feg=new ArrayList<String>();//框架元素组
		ExtractFeature(srlTree,feg);
	}
    
	private TreeNode generateTree(String sentence) {
		return new TreeNode();
	}
	
	/**
	 * 从树中提取特征
	 * @param headtree
	 */
	private void ExtractFeature(TreeNode node,ArrayList<String> feg) {
		SRLTreeNode srlnode=(SRLTreeNode)node;
		
		//提取本节点的特征
		if(srlnode.getSemanticRole()!= null){
          feg.add(srlnode.getNodeName());
          
          
		}
		
		//遍历树中的每一个节点
		for(TreeNode srlTreeNode: node.getChildren()) {
			ExtractFeature(srlTreeNode,feg);
		}
	}
}
