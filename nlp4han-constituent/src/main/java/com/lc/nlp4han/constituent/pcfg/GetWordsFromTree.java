package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

public class GetWordsFromTree
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
}
