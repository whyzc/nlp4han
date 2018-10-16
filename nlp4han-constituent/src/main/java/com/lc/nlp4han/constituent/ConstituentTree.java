package com.lc.nlp4han.constituent;

/**
 * 成分树/短语结构树
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ConstituentTree {

	private TreeNode root;
	
	public ConstituentTree()
	{
		
	}
	
	public ConstituentTree(TreeNode rootNode)
	{
		this.root = rootNode;
	}
	
	public void setRoot(TreeNode treeNode){
		this.root = treeNode;
	}
	
	public TreeNode getRoot(){
		return this.root;
	}

	@Override
	public String toString() {
		return root.toString();
	}
	
	/**
	 * 带缩进和换行的输出
	 * 
	 * @return
	 */
	public String toPrettyString()
	{
		return TreeNode.printTree(root, 1);
	}
}
