package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYLoosePCNF;

/**
 * 利用PCFG文法解析，再根据隐PCFG计算句法树的概率
 * 
 * @author 王宁
 */
public class ConstituentParserLatentSimple implements ConstituentParser
{
	public static int deafultParserCKYP2NF_K = 10;
	private ConstituentParserCKYLoosePCNF p2nf;
	private Grammar grammarLatentLabel;

	public ConstituentParserLatentSimple(ConstituentParserCKYLoosePCNF p2nf, Grammar grammarLatentLabel)
	{
		this.p2nf = p2nf;
		this.grammarLatentLabel = grammarLatentLabel;
	}

	public ConstituentParserLatentSimple(ConstituentParserCKYLoosePCNF p2nf, Grammar grammarLatentLabel,
			int parserCKYP2NF_K)
	{
		this.p2nf = p2nf;
		this.grammarLatentLabel = grammarLatentLabel;
		deafultParserCKYP2NF_K = parserCKYP2NF_K;
	}

	@Override
	public ConstituentTree parse(String[] words, String[] poses)
	{
		ConstituentTree[] allTrees = parse(words, poses, 1);
		if (allTrees != null)
			return allTrees[0];
		else
			return null;
	}

	@Override
	public ConstituentTree[] parse(String[] words, String[] poses, int k)
	{
		ConstituentTree[] trees = p2nf.parse(words, poses, deafultParserCKYP2NF_K);
		if (trees == null || trees.length == 0)
		{
			// System.out.println("p2nf不能解析该句子");
			return null;
		}
		boolean allNull = true;
		for (ConstituentTree tree : trees)
		{
			allNull = allNull && (tree == null);
			if (!allNull)
				break;
		}
		if (allNull)
		{
			// System.out.println("p2nf不能解析该句子");
			return null;
		}

		TreeMap<Double, ConstituentTree> scores = new TreeMap<Double, ConstituentTree>(new Comparator<Double>()
		{
			@Override
			public int compare(Double o1, Double o2)
			{
				return o2.compareTo(o1);
			}
		});
		for (int i = 0; i < trees.length; i++)
		{
			if (trees[i] != null)
			{
				AnnotationTreeNode annotationTree = grammarLatentLabel.convert2AnnotationTreeNode(trees[i].getRoot());
				double logScore = TreeProbTool.computeProb(grammarLatentLabel, annotationTree);
				if (logScore != Double.NEGATIVE_INFINITY)
				{
					TreeBinarization.unbinarize(trees[i].getRoot());
					scores.put(logScore, trees[i]);
				}
			}
		}
		ArrayList<ConstituentTree> sortedTree = new ArrayList<>();
		for (int i = 0; i < scores.size() && i < k; i++)
		{
			sortedTree.add(scores.pollFirstEntry().getValue());
		}
		if (sortedTree.size() == 0)
		{
			// System.out.println("带隐藏标记语法不能解析");
			return null;
		}
		return sortedTree.toArray(new ConstituentTree[sortedTree.size()]);
	}

	public ConstituentParserCKYLoosePCNF getP2nf()
	{
		return p2nf;
	}

	public void setP2nf(ConstituentParserCKYLoosePCNF p2nf)
	{
		this.p2nf = p2nf;
	}

	public Grammar getGrammarLatentLabel()
	{
		return grammarLatentLabel;
	}

	public void setGrammarLatentLabel(Grammar grammarLatentLabel)
	{
		this.grammarLatentLabel = grammarLatentLabel;
	}

}
