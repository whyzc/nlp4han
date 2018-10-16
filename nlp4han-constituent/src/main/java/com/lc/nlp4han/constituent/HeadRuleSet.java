package com.lc.nlp4han.constituent;

import java.util.HashMap;
import java.util.List;

/**
 * 头结点生成规则集
 * 
 * @author 邱宜龙
 *
 */
public abstract class HeadRuleSet
{
	protected abstract HashMap<String, HeadRule> getNormalRuleSet();
	
	protected abstract HashMap<String, List<HeadRule>> getSpecialRuleSet();
}
