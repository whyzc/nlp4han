package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QuikSortTest
{
	private ArrayList<PRule> ruleList;

	@Before
	public void BeforeTest()
	{
		ruleList = new ArrayList<PRule>();
		ruleList.add(new PRule(0.1, "A", "a"));
		ruleList.add(new PRule(0.2, "A", "c"));
		ruleList.add(new PRule(0.3, "A", "b"));
		ruleList.add(new PRule(0.25, "A", "d"));
		ruleList.add(new PRule(0.4, "A", "e"));
		ruleList.add(new PRule(0.315, "A", "w"));
		ruleList.add(new PRule(0.25, "A", "dddddd"));
		ruleList.add(new PRule(0.008, "A", "p"));
	}

	@Test
	public void quikSortTest()
	{
		PCFG.SortPRuleList(ruleList, 0, 7);
		System.out.println(ruleList.toString());
		Assert.assertTrue(ruleList.get(0).equals(new PRule(0.4, "A", "e")));
	}
}
