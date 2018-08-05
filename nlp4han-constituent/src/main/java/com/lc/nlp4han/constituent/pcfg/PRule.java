package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;

public class PRule extends RewriteRule
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
		strb.append(super.getLhs() + "->");
		for (String st : super.getRhs())
		{
			strb.append(st);
			strb.append(" ");
		}
		strb.append(" ---- " + " " + proOfRule);
		return strb.toString();
	}
}
