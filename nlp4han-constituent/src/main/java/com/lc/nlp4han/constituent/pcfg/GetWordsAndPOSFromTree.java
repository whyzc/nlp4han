package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

public class GetWordsAndPOSFromTree
{
       public static String[] getetWordsFromTree(TreeNode tree) {
    	 List<String> words=new ArrayList<String>();
    	 traverseTree(tree,words);
    	 String[] words1=new String[words.size()];
    	 for(int i=0;i<words.size();i++) {
    		 words1[i]=words.get(i);
    	 }
		return words1;
       }
       private static  void traverseTree(TreeNode node,List<String> words) {
		   if(node.getChildrenNum()==0) {
			   words.add(node.getNodeName());
		   }
    	   for(TreeNode node1:node.getChildren()) {
    		   traverseTree(node1,words);
    	   }
       }
       public static void getWordsAndPOSFromTree(ArrayList<String> words,ArrayList<String> poses,TreeNode tree) {
    	   traverseTree(tree,words,poses);
       }
       private static  void traverseTree(TreeNode node,List<String> words,ArrayList<String> poses) {
		   if(node.getChildrenNum()==0) {
			   poses.add(node.getParent().getNodeName());
			   words.add(node.getNodeName());
		   }
    	   for(TreeNode node1:node.getChildren()) {
    		   traverseTree(node1,words,poses);
    	   }
       }
       
}
