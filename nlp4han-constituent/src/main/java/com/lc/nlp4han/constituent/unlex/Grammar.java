package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
		// splitNonterminal(); //for all annotationTree
		// splitRule();// get new scores for each splitted rule
		// calculate Inner/outer Score for each Annotation of each tree

		// EM -> new Rules

		// calculate likelihood of hole treebank
	}

	public void merger()
	{
		// assume a nonterminal without split

	}

	public double calculateLikelihood()
	{// 利用规则计算treeBank的似然值
		return 0.0;
	}

	public void writeGrammarToTxt() throws IOException
	{
		TreeMap<String, Double> allBAndURules = new TreeMap<String, Double>();
		TreeMap<String, Double> allPreRules = new TreeMap<String, Double>();
		for (BinaryRule bRule : bRules)
		{
			String parentStr = nonterminalTable.stringValue(bRule.parent);
			String leftChildStr = nonterminalTable.stringValue(bRule.getLeftChild());
			String rightChildStr = nonterminalTable.stringValue(bRule.getRightChild());
			Double score = bRule.scores.get(0).get(0).get(0);
			allBAndURules.put(parentStr + " ->" + leftChildStr + " " +rightChildStr, score);
		}
		for (UnaryRule uRule : uRules)
		{
			String parentStr = nonterminalTable.stringValue(uRule.parent);
			String childStr = nonterminalTable.stringValue(uRule.getChild());
			Double score = uRule.scores.get(0).get(0);
			allBAndURules.put(parentStr + " ->" + childStr, score);
		}
		for (PreterminalRule preRule : preRules)
		{
			String parentStr = nonterminalTable.stringValue(preRule.parent);
			String childStr = preRule.word;
			Double score = preRule.scores.get(0);
			allPreRules.put(parentStr + " ->" + childStr, score);
		}

		FileOutputStream fos = new FileOutputStream("C:\\Users\\hp\\Desktop\\grammartest.grammar");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "gbk");
		BufferedWriter bAndURuleWriter = new BufferedWriter(osw);

		FileOutputStream fos2 = new FileOutputStream("C:\\Users\\hp\\Desktop\\preruletest.grammar");
		OutputStreamWriter osw2 = new OutputStreamWriter(fos2, "gbk");
		BufferedWriter preRuleWriter = new BufferedWriter(osw2);
		for (Map.Entry<String, Double> entry : allBAndURules.entrySet())
		{
			bAndURuleWriter.write(entry.getKey() + " " + entry.getValue().toString()+"\r");
		}
		for (Map.Entry<String, Double> entry : allPreRules.entrySet())
		{
			preRuleWriter.write(entry.getKey() + " " + entry.getValue().toString()+"\r");
		}
		bAndURuleWriter.close();
		preRuleWriter.close();
	}

}
