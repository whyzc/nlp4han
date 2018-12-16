package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.HashMap;

import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYLoosePCNF;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.constituent.pcfg.PRule;

/**
 * 将带有隐藏符号的语法当作PCFG直接解析，解析得到派生树当作最终的不带隐藏符号的结构树。
 * 
 * @author 王宁
 */
public class ConstituentParserLatentAnnotation_Viterbi implements ConstituentParserLatentAnnotation
{
	private ConstituentParserCKYLoosePCNF p2nf;

	public ConstituentParserLatentAnnotation_Viterbi(Grammar gLatent)
	{
		this(gLatent, 0.0001, false, false);
	}

	public ConstituentParserLatentAnnotation_Viterbi(Grammar gLatent, double pruneThreshold, boolean secondPrune,
			boolean prior)
	{
		PCFG pcfg = gLatent.getPCFG();
		HashMap<String, Double> posMap = new HashMap<>();
		for (short preterminal : gLatent.allPreterminal())
		{
			String originalPre = gLatent.symbolStrValue(preterminal);
			posMap.put(originalPre, 1.0);
			for (short subPreterminal = 0; subPreterminal < gLatent.getNumSubSymbol(preterminal); subPreterminal++)
			{
				String subPre;
				if (gLatent.getNumSubSymbol(preterminal) == 1)
					subPre = originalPre;
				subPre = originalPre + "_" + subPreterminal;
				PRule pRule = new PRule(1.0, subPre, originalPre);
				pcfg.add(pRule);
			}
		}

		pcfg.setPosMap(posMap);
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
				removeOriginalTag(tree.getRoot());
				TreeUtil.removeLatentLabel(tree.getRoot());
				Binarization.recoverBinaryTree(tree.getRoot());
				trees.add(tree);
			}
		}
		if (trees.size() != 0)
			return trees.toArray(new ConstituentTree[trees.size()]);
		else
			return null;
	}

	
	/**
	 * 去掉为了viterbi解析特意添加的额外的规则
	 * 
	 * @param tree
	 * @return
	 */
	private TreeNode removeOriginalTag(TreeNode tree)
	{
		if (tree.getChildren().size() == 1 && tree.getNodeName().split("_")[0].equals(tree.getChild(0).getNodeName())
				&& !tree.getChild(0).isLeaf())
		{
			ArrayList<TreeNode> children = new ArrayList<TreeNode>(tree.getChild(0).getChildren());
			for (TreeNode child : children)
			{
				child.setParent(tree);
			}
			tree.setChildren(children);
		}
		else
		{
			for (TreeNode subTree : tree.getChildren())
			{
				removeOriginalTag(subTree);
			}
		}
		return tree;
	}
}
