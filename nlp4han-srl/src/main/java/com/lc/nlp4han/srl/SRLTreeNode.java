package com.lc.nlp4han.srl;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 语义角色标注树
 * @author 王馨苇
 *
 */
public class SRLTreeNode extends TreeNode{

	public SRLTreeNode(String nodename){
		super(nodename);
	}
	
	//语义角色标注信息
	private String semanticRole;
	
	public void setSemanticRole(String semanticRole){
		this.semanticRole = semanticRole;
	}
	
	public String getSemanticRole(){
		return this.semanticRole;
	}

	
//	//返回父节点
//	public SemanticRoleTree getParent(){
//		return (SemanticRoleTree) parent;
//	}
//
//	//返回子节点列表
//	public List<? extends SemanticRoleTree> getChildren(){
//		List<SemanticRoleTree> hnode = new ArrayList<SemanticRoleTree>();
//		for (TreeNode treeNode : children) {
//			SemanticRoleTree node = (SemanticRoleTree) treeNode;
//			hnode.add(node);
//		}
//		return hnode;
//	}
	
	@Override
	public String toString() {
		if(this.children.size() == 0){
			return " "+this.nodename+"["+getWordIndex()+"]";
		}else{
			String treestr = "";
			if(this.semanticRole != null){
				treestr = "("+this.nodename+"{"+this.semanticRole+"}";
			}else{
				treestr = "("+this.nodename;
			}			
			for (TreeNode node:this.children) {
				treestr += node.toString();
			}
			treestr += ")";
			return treestr;
		}
	}

	/**
	 * 输出无空节点的语义角色树的括号表达式
	 * @return
	 */
	public String toNoNoneSRLBracket(){
		if(this.children.size() == 0 && getFlag() == true){
			return " "+this.nodename+"["+getWordIndex()+"]";
		}else{
			String treestr = "";
			if(getFlag() == true){
				if(this.semanticRole != null){
					treestr = "("+this.nodename+"{"+this.semanticRole+"}";
				}else{
					treestr = "("+this.nodename;
				}	
			}					
			for (TreeNode node:this.children) {
				treestr += ((SRLTreeNode) node).toNoNoneSRLBracket();
			}
			if(getFlag() == true){
				treestr += ")";
			}			
			return treestr;
		}
	}
	
	/**
	 * 括号表达式
	 * @return
	 */
	public String toStringWordIndex(){
		if(this.children.size() == 0){
			return " "+this.nodename+"["+getWordIndex()+"]";
		}else{
			String treestr = "";
			treestr = "("+this.nodename;
			for (TreeNode node:this.children) {
				treestr += node.toStringWordIndex();
			}
			treestr += ")";
			return treestr;
		}
	}
	
	/**
	 * 打印中括号的形式
	 * @param node
	 * @return
	 */
	public static String printSRLBracket(SRLTreeNode node){

		if(node.getChildrenNum() == 0){
			return node.getNodeName()+" ";
		}else{	
			String treestr = "";
			if(node.getSemanticRole() != null){		
				treestr += "[ ";
				String role = node.getSemanticRole();

				for (TreeNode tree:node.children) {
					treestr += printSRLBracket((SRLTreeNode) tree);
				}
				treestr += "]"+role+" ";
			}else{
				for (TreeNode tree:node.children) {
					treestr += printSRLBracket((SRLTreeNode) tree);
				}
			}
			return treestr;
		}				
	}
}
