package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;

public class PRule extends RewriteRule {
    private double proOfRule;
    public PRule() {
    	super();
    }
    public PRule(double pro,String ...args) {
    	super(args);
    	this.proOfRule=pro;
    }
    public PRule(double pro,String lhs,ArrayList<String> rhs) {
    	super(lhs,rhs);
    	this.proOfRule=pro;
    }
    public PRule(RewriteRule rule, double pro) {
    	super(rule.getLhs(),rule.getRhs());
    	this.proOfRule=pro;
    }
	public double getProOfRule() {
		return proOfRule;
	}
	
	public void setProOfRule(double proOfRule) {
		this.proOfRule = proOfRule;
	}
	
/*	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(proOfRule);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PRule other = (PRule) obj;
		if (Double.doubleToLongBits(proOfRule) != Double.doubleToLongBits(other.proOfRule))
			return false;
		return true;
	}*/
	@Override
	public String toString() {
		StringBuilder strb=new StringBuilder();
		strb.append(super.getLhs()+ "->");
		for(String st: super.getRhs()) {
			strb.append(st);
			strb.append(" ");
		}
		strb.append(" "+proOfRule);
		return strb.toString();
	}
}
