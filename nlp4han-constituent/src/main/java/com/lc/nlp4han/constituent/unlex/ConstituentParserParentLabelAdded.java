package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;

import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYLoosePCNF;
import com.lc.nlp4han.constituent.pcfg.PCFG;

/**
 * 用带父节点标记的语法解析
 */
public class ConstituentParserParentLabelAdded implements ConstituentParserLatentAnnotation
{
	private ConstituentParserCKYLoosePCNF p2nf;

	public ConstituentParserParentLabelAdded(Grammar gPLAdded)
	{
		this(gPLAdded, 0.0001, false, false);
	}

	public ConstituentParserParentLabelAdded(Grammar gPLAdded, double pruneThreshold, boolean secondPrune,
			boolean prior)
	{
		PCFG pcfg = gPLAdded.getPCFG();
		p2nf = new ConstituentParserCKYLoosePCNF(pcfg, pruneThreshold, secondPrune, prior);
	}

	@Override
	public ConstituentTree parse(String[] words, String[] poses)
	{
		ConstituentTree[] allTree = parse(words, poses, 1);
		if (allTree != null && allTree[0] != null)
			return allTree[0];
		return null;
	}

	@Override
	public ConstituentTree[] parse(String[] words, String[] poses, int k)
	{
		ArrayList<ConstituentTree> trees = new ArrayList<>();
		for (ConstituentTree tree : p2nf.parse(words, poses, k))
		{
			if (tree != null)
			{
				Binarization.recoverBinaryTree(tree.getRoot());
				TreeUtil.removeParentLabel(tree.getRoot());
				trees.add(tree);
			}
		}
		if (trees.size() != 0)
			return trees.toArray(new ConstituentTree[trees.size()]);
		else
			return null;
	}

}
