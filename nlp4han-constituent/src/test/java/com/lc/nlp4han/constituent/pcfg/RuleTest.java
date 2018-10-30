package com.lc.nlp4han.constituent.pcfg;

import static org.junit.Assert.*;

import org.junit.Test;

public class RuleTest
{

	@Test
	public void testConstructFromStr()
	{
		String ruleStr1 = "A->B c";
		RewriteRule r1 = new RewriteRule(ruleStr1);	
		assertEquals(ruleStr1, r1.toString());
		
		String ruleStr2 = "A->B c ---- 0.123";
		RewriteRule r2 = new RewriteRule(ruleStr2);
		assertEquals(ruleStr2, r2.toString());
	}

}
