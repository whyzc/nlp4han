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
		String startSymbol = "ROOT";
		String[] nonTerminallist = { "S", "A", "B", "C", "D", "E" };
		String[] terminallist = { "a", "b", "c", "d", "e", "g" };
		HashSet<String> nonTerminalSet = new HashSet<String>();
		HashSet<String> terminalSet = new HashSet<String>();
		listConvertToSet(nonTerminallist, nonTerminalSet);
		listConvertToSet(terminallist, terminalSet);
		cfg.setNonTerminalSet(nonTerminalSet);
		cfg.setTerminalSet(terminalSet);
		cfg.setStartSymbol(startSymbol);
		cfg.add(new RewriteRule("S->B"));
		cfg.add(new RewriteRule("S->A S"));
		cfg.add(new RewriteRule("S->A B c D e g"));
		cfg.add(new RewriteRule("S->D e g B"));
		cfg.add(new RewriteRule("S->C d"));
		cfg.add(new RewriteRule("S->S"));

		cfg.add(new RewriteRule("A->c D"));
		cfg.add(new RewriteRule("A->B"));
		cfg.add(new RewriteRule("A->S"));

		cfg.add(new RewriteRule("B->C D"));
		cfg.add(new RewriteRule("B->E"));
		cfg.add(new RewriteRule("B->D"));

		cfg.add(new RewriteRule("C->c"));
		cfg.add(new RewriteRule("C->d"));
		cfg.add(new RewriteRule("C->e"));

		cfg.add(new RewriteRule("D->d"));

		cfg.add(new RewriteRule("E->e"));

		cnf = GrammarConvertor.CFG2CNF(cfg);

	}

	/**
	 * 不用替换起始符，新的规则 添加新的起始符DuS,新的规则DuS->S(该规则在消除Unit Production时删除)
	 */
	@Test
	public void addNewStartSymbolTest()
	{
		// 起始符不变
		Assert.assertEquals(cnf.getStartSymbol(), "ROOT");
	}

	/**
	 * 如果规则右侧的字符串个数大于或等于三个则递归减少至两个，在递归前将右侧的终结符转换为非终结符
	 */
	@Test
	public void ReduceNumOfRHSTest()
	{
		HashSet<RewriteRule> set = new HashSet<RewriteRule>();
		// new RewriteRule("S","A","B","c","D","e","g")
		set.add(new RewriteRule("$g$->g"));
		set.add(new RewriteRule("$e$->e"));
		set.add(new RewriteRule("$c$->c"));
		set.add(new RewriteRule("$e$&$g$->$e$ $g$"));
		set.add(new RewriteRule("D&$e$&$g$->D $e$&$g$"));
		set.add(new RewriteRule("$c$&D&$e$&$g$->$c$ D&$e$&$g$"));
		set.add(new RewriteRule("B&$c$&D&$e$&$g$->B $c$&D&$e$&$g$"));
		set.add(new RewriteRule("S->A B&$c$&D&$e$&$g$"));

		// new RewriteRule("S","D","e","g","B");
		set.add(new RewriteRule("$g$&B->$g$ B"));
		set.add(new RewriteRule("$e$&$g$&B->$e$ $g$&B"));
		set.add(new RewriteRule("S->D $e$&$g$&B"));

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
		set.add(new RewriteRule("$d$->d"));
		set.add(new RewriteRule("S->C $d$"));

		// new RewriteRule("A","c","D")
		set.add(new RewriteRule("$c$->c"));
		set.add(new RewriteRule("A->$c$ D"));
		for (RewriteRule rule : set)
		{
			if (!cnf.getRuleSet().contains(rule))
			{
				System.out.println("混合 "+rule.toString());
			}
		}
		Assert.assertTrue(cnf.getRuleSet().containsAll(set));
	}

	/**
	 * 将词性标注作为终结符 消除Unit Production 测试点：1.S->S，DuS->S的移除 2.类似A->B的移除
	 * 3.类似A->b的添加(原为B->b) 4.类似A->Duc B的添加 5.类似A->C D的添加(原为B->C D)
	 * 6.类似A->A,BDucDDueDug的添加(原为A->S,S->A,BDucDDueDug的)
	 * 7.类似A->d的添加,A->D的不添加(原为A->B,B->D,D->d) 8.类似B->b的不删除
	 */
	@Test
	public void RemoveUnitProductionTest()
	{
		HashSet<RewriteRule> deleteSet = new HashSet<RewriteRule>();
		deleteSet.add(new RewriteRule("S->S"));
		deleteSet.add(new RewriteRule("A->B"));
		deleteSet.add(new RewriteRule("A->S"));
		// 中间结果，在A->B,B->D,D->d的过程中不添加A->D
		HashSet<RewriteRule> addSet = new HashSet<RewriteRule>();
		// 由消除规则S->B添加的
		addSet.add(new RewriteRule("S@B->C D"));
		addSet.add(new RewriteRule("S@B->D"));
		addSet.add(new RewriteRule("S@B->E"));
		// 由消除规则A->B添加的
		addSet.add(new RewriteRule("A@B->C D"));
		addSet.add(new RewriteRule("A@B->D"));
		addSet.add(new RewriteRule("A@B->E"));
		// 由消除规则A->S添加的
		addSet.add(new RewriteRule("A@S->A B&$c$&D&$e$&$g$"));
		addSet.add(new RewriteRule("A@S->A S"));
		addSet.add(new RewriteRule("A@S->D $e$&$g$&B"));
		addSet.add(new RewriteRule("A@S->C $d$"));
		
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