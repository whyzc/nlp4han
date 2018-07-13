package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;


public class RewriteRule {

	private String lhs;//规则左部
	private ArrayList<String> rhs=new ArrayList<String>();//规则右部
    
	public RewriteRule(String lhs,ArrayList<String> list) {
		this.lhs=lhs;
		for(String type: list) {
			this.rhs.add(type);	
		}
	}
	public RewriteRule(String ...args) {
		this.lhs=args[0];
		for(int i=1;i<args.length;i++) {
			this.rhs.add(args[i]);
		}
	}
	public RewriteRule(String lhs,List<? extends TreeNode> children) {
		super();
        this.lhs=lhs;
        for(TreeNode node: children) {
        	this.rhs.add(node.getNodeName());
        }
	}
	public void setLhs(String lhs) {
		this.lhs = lhs;
	}
	 public String getLhs() {
		return lhs;
	}

	public void setlhs(String lhs) {
		this.lhs = lhs;
	}

	public ArrayList<String> getRhs() {
		return rhs;
	}

	public void setRhs(ArrayList<String> rhs) {
		this.rhs = rhs;
	}

	 @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
		result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RewriteRule other = (RewriteRule) obj;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		if (rhs == null) {
			if (other.rhs != null)
				return false;
		} else if (!rhs.equals(other.rhs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder strb=new StringBuilder();
		strb.append(lhs + "->");
		for(String st: rhs) {
			strb.append(st);
			strb.append(" ");
		}
		return strb.toString();
	}
}
