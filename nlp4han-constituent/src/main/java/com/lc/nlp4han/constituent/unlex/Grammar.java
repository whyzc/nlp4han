package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author 王宁
 * @version 创建时间：2018年9月23日 下午2:59:46 表示由树库得到的语法
 */
public class Grammar
{
	protected List<Tree<Annotation>> treeBank;

	protected HashSet<BinaryRule> bRules;
	protected HashSet<UnaryRule> uRules;
	protected HashSet<PreterminalRule> preRules;

	protected NonterminalTable nonterminalTable;

	public Grammar(List<Tree<Annotation>> treeBank, HashSet<BinaryRule> bRules, HashSet<UnaryRule> uRules,
			HashSet<PreterminalRule> preRules, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.bRules = bRules;
		this.uRules = uRules;
		this.preRules = preRules;
		this.nonterminalTable = nonterminalTable;
	}

	public void split()
	{
//		splitNonterminal(); //for all annotationTree
//		splitRule();// get new scores for each splitted rule
// 		calculate Inner/outer Score for each Annotation of each tree
		
//		EM -> new Rules		
		
     
//		calculate likelihood of hole treebank
	}

	public void merger()
	{
//	assume a nonterminal without split

	}

	public double calculateLikelihood()
	{// 利用规则计算treeBank的似然值
		return 0.0;
	}
}
