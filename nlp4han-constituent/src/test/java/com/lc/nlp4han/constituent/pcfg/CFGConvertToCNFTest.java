package com.lc.nlp4han.constituent.pcfg;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class CFGConvertToCNFTest
{
	private CFG cfg;
	private CFG cnf;

	@Before
	public void BeforeConvert()
	{
		cfg = new CFG();
		String startSymbol = "S";
		String[] nonTerminallist = { "S", "A", "B", "C", "D", "E" };
		String[] terminallist = { "a", "b", "c", "d", "e", "g" };
		HashSet<String> nonTerminalSet = new HashSet<String>();
		HashSet<String> terminalSet = new HashSet<String>();
		listConvertToSet(nonTerminallist, nonTerminalSet);
		listConvertToSet(terminallist, terminalSet);
		cfg.setNonTerminalSet(nonTerminalSet);
		cfg.setTerminalSet(terminalSet);
		cfg.setStartSymbol(startSymbol);
		cfg.add(new RewriteRule("S", "B"));
		cfg.add(new RewriteRule("S", "A", "S"));
		cfg.add(new RewriteRule("S", "A", "B", "c", "D", "e", "g"));
		cfg.add(new RewriteRule("S", "D", "e", "g", "B"));
		cfg.add(new RewriteRule("S", "C", "d"));
		cfg.add(new RewriteRule("S", "S"));

		cfg.add(new RewriteRule("A", "c", "D"));
		cfg.add(new RewriteRule("A", "a"));
		cfg.add(new RewriteRule("A", "B"));
		cfg.add(new RewriteRule("A", "S"));

		cfg.add(new RewriteRule("B", "b"));
		cfg.add(new RewriteRule("B", "C", "D"));
		cfg.add(new RewriteRule("B", "E"));
		cfg.add(new RewriteRule("B", "D"));

		cfg.add(new RewriteRule("C", "c"));
		cfg.add(new RewriteRule("C", "d"));
		cfg.add(new RewriteRule("C", "e"));

		cfg.add(new RewriteRule("D", "d"));
		cfg.add(new RewriteRule("D", "d", "E", "d"));

		cfg.add(new RewriteRule("E", "e"));

		cnf = GrammarConvertor.convertCFGToCNF(cfg);

	}

	/**
	 * 添加新的起始符DuS,新的规则DuS->S(该规则在消除Unit Production时删除)
	 */
	@Test
	public void addNewStartSymbolTest()
	{

		// 起始符替换
		Assert.assertEquals(cnf.getStartSymbol(), "Du" + "S");
		// 新的非终结符
		Assert.assertTrue(cnf.getNonTerminalSet().contains("Du" + "S"));
	}

	/**
	 * 如果规则右侧的字符串个数大于或等于三个则递归减少至两个，在递归前将右侧的终结符转换为非终结符
	 */
	@Test
	public void ReduceNumOfRHSTest()
	{
		HashSet<RewriteRule> set = new HashSet<RewriteRule>();
		// new RewriteRule("S","A","B","c","D","e","g")
		set.add(new RewriteRule("Dug", "g"));
		set.add(new RewriteRule("Due", "e"));
		set.add(new RewriteRule("Duc", "c"));
		set.add(new RewriteRule("DueDug", "Due", "Dug"));
		set.add(new RewriteRule("DDueDug", "D", "DueDug"));
		set.add(new RewriteRule("DucDDueDug", "Duc", "DDueDug"));
		set.add(new RewriteRule("BDucDDueDug", "B", "DucDDueDug"));
		set.add(new RewriteRule("S", "A", "BDucDDueDug"));

		// new RewriteRule("S","D","e","g","B");
		set.add(new RewriteRule("Due", "e"));
		set.add(new RewriteRule("Dug", "g"));
		set.add(new RewriteRule("DugB", "Dug", "B"));
		set.add(new RewriteRule("DueDugB", "Due", "DugB"));
		set.add(new RewriteRule("S", "D", "DueDugB"));

		// new RewriteRule("D","d","E","d")
		set.add(new RewriteRule("Dud", "d"));
		set.add(new RewriteRule("EDud", "E", "Dud"));
		set.add(new RewriteRule("D", "Dud", "EDud"));
		Assert.assertTrue(cnf.getRuleSet().containsAll(set));
	}

	/**
	 * 右侧有两个，且为终结符与非终结符混合，则将终结转换为非终结符
	 */
	@Test
	public void ConvertMixRuleToCNFTest()
	{
		HashSet<RewriteRule> set = new HashSet<RewriteRule>();
		// new RewriteRule("S","C","d")
		set.add(new RewriteRule("Dud", "d"));
		set.add(new RewriteRule("S", "C", "Dud"));

		// new RewriteRule("A","c","D")
		set.add(new RewriteRule("Duc", "c"));
		set.add(new RewriteRule("A", "Duc", "D"));
		Assert.assertTrue(cnf.getRuleSet().containsAll(set));
	}

	/**
	 * 消除Unit Production 测试点：1.S->S，DuS->S的移除 2.类似A->B的移除 3.类似A->b的添加(原为B->b)
	 * 4.类似A->Duc B的添加 5.类似A->C D的添加(原为B->C D)
	 * 6.类似A->A,BDucDDueDug的添加(原为A->S,S->A,BDucDDueDug的)
	 * 7.类似A->d的添加,A->D的不添加(原为A->B,B->D,D->d) 8.类似B->b的不删除
	 */
	@Test
	public void RemoveUnitProductionTest()
	{
		HashSet<RewriteRule> deleteSet = new HashSet<RewriteRule>();
		deleteSet.add(new RewriteRule("S", "S"));
		deleteSet.add(new RewriteRule("DuS", "S"));
		deleteSet.add(new RewriteRule("A", "B"));
		deleteSet.add(new RewriteRule("A", "S"));
		deleteSet.add(new RewriteRule("B", "E"));
		deleteSet.add(new RewriteRule("B", "D"));
		// 中间结果，在A->B,B->D,D->d的过程中不添加A->D
		deleteSet.add(new RewriteRule("A", "D"));
		HashSet<RewriteRule> addSet = new HashSet<RewriteRule>();
		// 由消除规则B->E添加的
		addSet.add(new RewriteRule("B", "e"));
		// 由消除规则B->D添加的
		addSet.add(new RewriteRule("B", "d"));
		addSet.add(new RewriteRule("B", "Dud", "EDud"));
		// 由消除规则S->B添加的
		addSet.add(new RewriteRule("S", "Dud", "EDud"));
		addSet.add(new RewriteRule("S", "b"));
		addSet.add(new RewriteRule("S", "C", "D"));
		addSet.add(new RewriteRule("S", "d"));
		addSet.add(new RewriteRule("S", "e"));
		// 由消除规则DuS->S添加的
		addSet.add(new RewriteRule("DuS", "A", "BDucDDueDug"));
		addSet.add(new RewriteRule("DuS", "Dud", "EDud"));
		addSet.add(new RewriteRule("DuS", "b"));
		addSet.add(new RewriteRule("DuS", "C", "D"));
		addSet.add(new RewriteRule("DuS", "d"));
		addSet.add(new RewriteRule("DuS", "e"));
		addSet.add(new RewriteRule("DuS", "A", "S"));
		addSet.add(new RewriteRule("DuS", "C", "Dud"));
		addSet.add(new RewriteRule("DuS", "D", "DueDugB"));
		// 由消除规则A->B添加的
		addSet.add(new RewriteRule("A", "Dud", "EDud"));
		addSet.add(new RewriteRule("A", "b"));
		addSet.add(new RewriteRule("A", "C", "D"));
		addSet.add(new RewriteRule("A", "d"));
		addSet.add(new RewriteRule("A", "e"));
		// 由消除规则A->S添加的
		addSet.add(new RewriteRule("A", "A", "BDucDDueDug"));
		addSet.add(new RewriteRule("A", "A", "S"));
		addSet.add(new RewriteRule("A", "D", "DueDugB"));
		addSet.add(new RewriteRule("A", "C", "Dud"));

		Assert.assertTrue(cnf.getRuleSet().containsAll(addSet));
		for (RewriteRule rule : deleteSet)
		{
			Assert.assertFalse(cnf.getRuleSet().contains(rule));
		}
	}

	private void listConvertToSet(String[] list, HashSet<String> set)
	{
		for (String string : list)
		{
			set.add(string);
		}
	}
}