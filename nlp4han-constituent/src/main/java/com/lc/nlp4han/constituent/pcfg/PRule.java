package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;

public class PRule extends RewriteRule implements Comparable<PRule> 
{
	private double proOfRule;

	public PRule()
	{
		super();
	}

	/**
	 * 初始化PRule
	 * 
	 * @param 规则的概率，以及终结符和非终结符的字符串形式
	 */
	public PRule(double pro, String... args)
	{
		super(args);
		this.proOfRule = pro;
	}
	/**
	 * 由规则字符串构造规则
	 * @param ruleStr
	 *             规则的字符串形式
	 */
	public PRule (String ruleStr) {
		super(ruleStr.split(" ---- ")[0]);
        proOfRule=Double.parseDouble(ruleStr.split(" ---- ")[1]);
	}
	/**
	 * 初始化PRule
	 * @param pro
	 * @param lhs
	 * @param rhs
	 */
	public PRule(double pro, String lhs, ArrayList<String> rhs)
	{
		super(lhs, rhs);
		this.proOfRule = pro;
	}

	/**
	 * 初始化PRule
	 * @param rule
	 * @param pro
	 */
	public PRule(RewriteRule rule, double pro)
	{
		super(rule.getLhs(), rule.getRhs());
		this.proOfRule = pro;
	}

	/**
	 * 得到该规则的概率
	 * @return proOfRule
	 */
	public double getProOfRule()
	{
		return proOfRule;
	}

	/**
	 * 设置该规则的概率
	 * @param proOfRule
	 */
	public void setProOfRule(double proOfRule)
	{
		this.proOfRule = proOfRule;
	}

	@Override
	public String toString()
	{
		StringBuilder strb = new StringBuilder();
		strb.append(super.toString());
		strb.append(" ---- " + " " + proOfRule);
		return strb.toString();
	}

	@Override
	public int compareTo(PRule o)
	{//排序由大到小
		if(proOfRule<o.getProOfRule()) {
			return 1;
		}
		if(proOfRule>o.getProOfRule()){
			return-1;
		}
		return 0;
	}
}
