package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 和括号表达式表示相关的工具方法
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class BracketExpUtil {

	/**
	 * 由括号表达式生成成分树结构
	 * 
	 * @param bracketStr 括号表达式
	 * @return
	 */
	public static TreeNode generateTree(String bracketStr){
		bracketStr = format(bracketStr);
		List<String> parts = stringToList(bracketStr);
		
        Stack<TreeNode> tree = new Stack<TreeNode>();
        int wordindex = 0;
        for (int i = 0; i < parts.size(); i++) {//将表达式中的-LRB-和-RRB-转换为"(",")"
			if(!parts.get(i).equals(")") && !parts.get(i).equals(" ")){
				TreeNode tn = new TreeNode(parts.get(i));
				tn.setFlag(true);
				tree.push(tn);
			}else if(parts.get(i).equals(" ")){
				
			}else if(parts.get(i).equals(")")){
				Stack<TreeNode> temp = new Stack<TreeNode>();
				while(!tree.peek().getNodeName().equals("(")){
					if(!tree.peek().getNodeName().equals(" ")){
						temp.push(tree.pop());
					}
				}
				tree.pop();
				TreeNode node = temp.pop();
				while(!temp.isEmpty()){		
					temp.peek().setParent(node);
					if(temp.peek().getChildren().size() == 0){
						TreeNode wordindexnode = temp.pop();
						wordindexnode.setWordIndex(wordindex++);
						node.addChild(wordindexnode);
					}else{
						node.addChild(temp.pop());
					}
				}
				tree.push(node);
			}
		}
        TraverseTreeConvertRRBAndLRB(tree.peek());
        TreeNode treeStruct = tree.pop();
        return treeStruct;
	}
    /**
     * 遍历树，将树中的-RRB-和-LRB-转换为左右括号
     * @param 根节点
     * @return
     */
	public static void TraverseTreeConvertRRBAndLRB(TreeNode node) {
		if(node.getChildrenNum()==0) {
			if(node.getNodeName().equals("-LRB-")) {
				   node.setNewName("(");
				}else if(node.getNodeName().equals("-RRB-")) {
				   node.setNewName(")");
				}
			return;
		}
		for(TreeNode childNode:node.getChildren()) {
			TraverseTreeConvertRRBAndLRB(childNode);
		}
	}
	/**
	 * 将括号表达式去掉空格转成列表的形式
	 * 
	 * @param bracketStr 括号表达式
	 * @return
	 */
	public static List<String> stringToList(String bracketStr){
		List<String> parts = new ArrayList<String>();
        for (int index = 0; index < bracketStr.length(); ++index) {
            if (bracketStr.charAt(index) == '(' || bracketStr.charAt(index) == ')' || bracketStr.charAt(index) == ' ') {
                parts.add(Character.toString(bracketStr.charAt(index)));
            } else {
                for (int i = index + 1; i < bracketStr.length(); ++i) {
                    if (bracketStr.charAt(i) == '(' || bracketStr.charAt(i) == ')' || bracketStr.charAt(i) == ' ') {
                        parts.add(bracketStr.substring(index, i));
                        index = i - 1;
                        break;
                    }
                }
            }
        }
        return parts;
	}
	
	/**
	 * 格式化为形如：(A(B1(C1 d1)(C2 d2))(B2 d3)) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连。
	 * 
	 * @param bracketStr 从训练语料拼接出的一棵树
	 */
	public static String format(String bracketStr){
		//去除最外围的括号
        bracketStr = bracketStr.substring(1, bracketStr.length() - 1).trim();
        //所有空白符替换成一位空格
        bracketStr = bracketStr.replaceAll("\\s+", " ");
        
        //去掉 ( 和 ) 前的空格
        String newTree = "";
        for (int c = 0; c < bracketStr.length(); ++c) {
            if (bracketStr.charAt(c) == ' ' && (bracketStr.charAt(c + 1) == '(' || bracketStr.charAt(c + 1) == ')')) {
                continue;
            } else {
                newTree = newTree + (bracketStr.charAt(c));
            }
        }
        
        return newTree;
	}
}
