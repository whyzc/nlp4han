package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYP2NF;

/**
 * @author 王宁
 */
public class ConstituentParserLatentAnnotation implements ConstituentParser
{
	public static int deafultParserCKYP2NF_K = 10;
	private ConstituentParserCKYP2NF p2nf;
	private Grammar grammarLatentLabel;

	public ConstituentParserLatentAnnotation(ConstituentParserCKYP2NF p2nf, Grammar grammarLatentLabel)
	{
		this.p2nf = p2nf;
		this.grammarLatentLabel = grammarLatentLabel;
	}

	public ConstituentParserLatentAnnotation(ConstituentParserCKYP2NF p2nf, Grammar grammarLatentLabel,
			int parserCKYP2NF_K)
	{
		this.p2nf = p2nf;
		this.grammarLatentLabel = grammarLatentLabel;
		deafultParserCKYP2NF_K = parserCKYP2NF_K;
	}

	@Override
	public ConstituentTree parse(String[] words, String[] poses)
	{
		return parse(words, poses, 1)[0];
	}

	@Override
	public ConstituentTree[] parse(String[] words, String[] poses, int k)
	{
		ConstituentTree[] trees = p2nf.parse(words, poses, deafultParserCKYP2NF_K);
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
			// TODO:重新寻找一个tostring方法
			scores.put(BracketExpScoreComputerTool.comperter(grammarLatentLabel, trees[i].getRoot().toString()),
					trees[i]);
		}

		ArrayList<ConstituentTree> sortedTree = new ArrayList<>();
		for (int i = 0; i < k; i++)
		{
			sortedTree.add(scores.pollFirstEntry().getValue());
		}
		return sortedTree.toArray(new ConstituentTree[sortedTree.size()]);
	}
}
