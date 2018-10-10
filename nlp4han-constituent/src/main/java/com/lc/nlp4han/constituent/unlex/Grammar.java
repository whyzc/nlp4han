package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

		// 分裂树库中树的Annotation
		for (Tree<Annotation> tree : treeBank)
		{
			splitTreeAnnotation(tree);
		}

		// 修改记录每个非终结符分裂后的个数的数组
		for (int i = 0; i < nonterminalTable.getNumSubsymbolArr().size(); i++)
		{
			nonterminalTable.getNumSubsymbolArr().add(i, (short) (2 * nonterminalTable.getNumSubsymbolArr().get(i)));
		}

		// splitRule();

		EM();
	}

	/**
	 * 修改该分裂后的规则的概率
	 * 
	 * @param rule
	 */
	public void splitRule(Rule rule)
	{
		rule.split();
	}

	public void splitTreeAnnotation(Tree<Annotation> tree)
	{
		if (tree == null)
			return;
		if (tree.isLeaf())
			return;
		tree.getLabel().setNumSubSymbol((short) (tree.getLabel().getNumSubSymbol() * 2));
		for (Tree<Annotation> child : tree.getChildren())
		{
			splitTreeAnnotation(child);
		}
	}

	public void merger()
	{
		// assume a nonterminal without split

	}

	/**
	 * 将分裂后的语法期望最大化，得到新的规则
	 */
	public void EM()
	{
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
			allBAndURules.put(parentStr + " ->" + leftChildStr + " " + rightChildStr, score);
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
			bAndURuleWriter.write(entry.getKey() + " " + entry.getValue().toString() + "\r");
		}
		for (Map.Entry<String, Double> entry : allPreRules.entrySet())
		{
			preRuleWriter.write(entry.getKey() + " " + entry.getValue().toString() + "\r");
		}
		bAndURuleWriter.close();
		preRuleWriter.close();
	}

}
