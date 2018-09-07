package com.lc.nlp4han.constituent.lex;

import java.util.HashMap;
import java.util.List;

import com.lc.nlp4han.constituent.HeadRule;

/**
 * 为了更好的使用PTB和CTB的头规则集
 * @author qyl
 *
 */
public abstract class HeadRuleSet
{
	protected abstract HashMap<String, HeadRule> getNormalRuleSet();
	protected abstract HashMap<String, List<HeadRule>> getSpecialRuleSet();
}
