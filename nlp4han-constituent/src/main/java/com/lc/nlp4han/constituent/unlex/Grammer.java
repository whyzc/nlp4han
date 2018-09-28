package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.HashSet;

/**
* @author 王宁
* @version 创建时间：2018年9月23日 下午2:59:46
* 表示由树库得到的语法
*/
public class Grammer
{
	protected ArrayList<Tree> treeBank;
	
	protected HashSet<BinaryRule> bRules;
	protected HashSet<UnaryRule> uRules;
	protected HashSet<PreterminalRule> preRules;
	
	protected NonterminalTable nonterminalTable; 
	
	public void split() {}
	
	public void merger() {}
	
	public double calculateLikelihood() {//利用规则计算treeBank的似然值
		return 0.0;
	}
}
