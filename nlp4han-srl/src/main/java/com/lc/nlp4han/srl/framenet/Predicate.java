package com.lc.nlp4han.srl.framenet;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 语义角色标注过程中使用的谓词类
 * @author qyl
 *
 */
public class Predicate
{
	private HashMap<String,Integer> map=new HashMap<String,Integer>();

	public Predicate()
	{
		
	}
	
	public Predicate(HashMap<String, Integer> map)
	{
		this.map = map;
	}
    
	public void addPredicate(String word,int index) {
		map.put(word, index);
	}
	
	public int[] getPredicateIndexs() {
       return null;
	}
	
	public ArrayList<String> getPredicates(){
		return null;		
	}
	
	public int getIndex4Predicate(String predicate) {
		return map.get(predicate);
	}
}
