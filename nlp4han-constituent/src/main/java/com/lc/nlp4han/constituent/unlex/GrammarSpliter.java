package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 分裂语法
 * 
 * @author 王宁
 * 
 * 
 */
public class GrammarSpliter
{
	public static void splitGrammar(Grammar oldG, TreeBank treeBank)
	{
		splitRule(oldG.bRules);
		splitRule(oldG.uRules);
		splitRule(oldG.lexicon.getPreRules());
		// 让PreterminalRule概率归一化
		normalizedPreTermianlRules(oldG);
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			splitTreeAnnotation(tree, treeBank.getNonterminalTable());
		}
		oldG.getNumSubsymbolArr().replaceAll(e -> Short.valueOf((short) (e * 2)));
		oldG.getNumSubsymbolArr().set(oldG.symbolIntValue(oldG.getStartSymbol()), (short) 1);
	}

	public static void normalizedPreTermianlRules(Grammar g)
	{

		HashMap<Short, Double[]> sameHeadPRuleScoreSum = new HashMap<Short, Double[]>();
		for (PreterminalRule preRule : g.lexicon.getPreRules())
		{
			if (!sameHeadPRuleScoreSum.containsKey(preRule.getParent()))
			{
				sameHeadPRuleScoreSum.put(preRule.parent, new Double[preRule.getScores().size()]);
			}
			for (int i = 0; i < preRule.getScores().size(); i++)
			{
				if (sameHeadPRuleScoreSum.get(preRule.parent)[i] == null)
				{
					BigDecimal tag_iScoreSum = BigDecimal.valueOf(0.0);
					for (Map.Entry<PreterminalRule, PreterminalRule> entry : g.preRuleBySameHead.get(preRule.parent)
							.entrySet())
					{
						tag_iScoreSum = tag_iScoreSum.add(BigDecimal.valueOf(entry.getValue().getScores().get(i)));
					}
					sameHeadPRuleScoreSum.get(preRule.parent)[i] = tag_iScoreSum.doubleValue();
				}
				preRule.getScores().set(i,
						BigDecimal.valueOf(preRule.getScores().get(i))
								.divide(BigDecimal.valueOf(sameHeadPRuleScoreSum.get(preRule.parent)[i]), 15,
										BigDecimal.ROUND_HALF_UP)
								.doubleValue());
			}
		}
	}

	private static <T extends Rule> void splitRule(HashSet<T> rules)
	{
		for (T rule : rules)
		{
			rule.split();
		}
	}

	public static void splitTreeAnnotation(AnnotationTreeNode tree, NonterminalTable nonterminalTable)
	{
		if (tree == null)
			return;
		if (tree.isLeaf())
			return;
		if (!(tree.getLabel().getSymbol() == nonterminalTable.intValue("ROOT")))
			tree.getLabel().setNumSubSymbol((short) (tree.getLabel().getNumSubSymbol() * 2));

		for (AnnotationTreeNode child : tree.getChildren())
		{
			if (!child.isLeaf())
				splitTreeAnnotation(child, nonterminalTable);
		}
	}
}
