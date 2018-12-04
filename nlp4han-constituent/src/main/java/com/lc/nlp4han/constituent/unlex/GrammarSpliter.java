package com.lc.nlp4han.constituent.unlex;

import java.io.IOException;
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
		splitRule(oldG.getbRules());
		splitRule(oldG.getuRules());
		splitRule(oldG.getLexicon().getPreRules());
		oldG.getNumSubsymbolArr().replaceAll(e -> Short.valueOf((short) (e * 2)));
		oldG.getNumSubsymbolArr().set(oldG.symbolIntValue(oldG.getStartSymbol()), (short) 1);
		// 让PreterminalRule概率归一化
		GrammarTrainer.normalizedPreTermianlRules(oldG);
		for (AnnotationTreeNode tree : treeBank.getTreeBank())
		{
			splitTreeAnnotation(tree, oldG);
		}

	}

	public static void normalizedPreTermianlRules(Grammar g)
	{
		HashMap<Short, Double[]> sameHeadPRuleScoreSum = new HashMap<Short, Double[]>();
		for (PreterminalRule preRule : g.getLexicon().getPreRules())
		{
			if (!sameHeadPRuleScoreSum.containsKey(preRule.getParent()))
			{
				sameHeadPRuleScoreSum.put(preRule.parent, new Double[g.getNumSubSymbol(preRule.getParent())]);
			}
			for (int i = 0; i < preRule.getScores().size(); i++)
			{
				if (sameHeadPRuleScoreSum.get(preRule.parent)[i] == null)
				{
					BigDecimal tag_iScoreSum = BigDecimal.valueOf(0.0);
					for (Map.Entry<PreterminalRule, PreterminalRule> entry : g.getPreRuleBySameHead()
							.get(preRule.parent).entrySet())
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

	public static void splitTreeAnnotation(AnnotationTreeNode tree, Grammar g)
	{
		if (tree == null)
			return;
		if (tree.isLeaf())
			return;
		if (!(tree.getLabel().getSymbol() == g.getNonterminalTable().intValue("ROOT")))
			tree.getLabel().setNumSubSymbol((short) (g.getNumSubSymbol(tree.getLabel().getSymbol())));

		for (AnnotationTreeNode child : tree.getChildren())
		{
			if (!child.isLeaf())
				splitTreeAnnotation(child, g);
		}
	}

	public static void main()
	{
		Grammar g = null;
		try
		{
			g = GrammarExtractorToolLatentAnnotation.getGrammar(0, 0.5, 50, 0.01, Lexicon.DEFAULT_RAREWORD_THRESHOLD,
					"C:\\Users\\hp\\Desktop\\test100tree.txt", "utf-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			GrammarSpliter.splitGrammar(g, new TreeBank("C:\\Users\\hp\\Desktop\\test100tree.txt", false, "utf-8"));

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
