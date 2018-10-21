package com.lc.nlp4han.constituent.unlex;

import java.util.HashSet;

/**
 * 分裂语法
 * 
 * @author 王宁
 * 
 * 
 */
public class GrammarSpliter
{
	public static void splitGrammar(Grammar oldG)
	{
		splitRule(oldG.bRules);
		splitRule(oldG.uRules);
		splitRule(oldG.lexicon.getPreRules());
		for (AnnotationTreeNode tree : oldG.treeBank)
		{
			splitTreeAnnotation(tree);
		}
		oldG.nonterminalTable.getNumSubsymbolArr().replaceAll(e -> Short.valueOf((short) (e * 2)));
	}

	private static <T extends Rule> void splitRule(HashSet<T> rules)
	{
		for (T rule : rules)
		{
			rule.split();
		}
	}

	public static void splitTreeAnnotation(AnnotationTreeNode tree)
	{
		if (tree == null)
			return;
		if (tree.isLeaf())
			return;
		tree.getLabel().setNumSubSymbol((short) (tree.getLabel().getNumSubSymbol() * 2));

		for (AnnotationTreeNode child : tree.getChildren())
		{
			if (!child.isLeaf())
				splitTreeAnnotation(child);
		}
	}
}
