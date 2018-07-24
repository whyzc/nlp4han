package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class PCFG extends CFG
{
    public PCFG() {
    	
    }
    //判断规则是否为CNF形式
    public boolean IsPCNF() {
    	return super.IsCNF();
    }
    /*
	 * 得到概率规则集
	 */
	public Set<PRule> getPRuleSet() {
		return convertRewriteRuleSetToPRuleSet(super.getRuleSet());
	}
	/*
	 * 根据规则左部得到所有对应概率规则集
	 */
	public Set<PRule> getPRuleBylhs(String lhs){
		return convertRewriteRuleSetToPRuleSet(super.getRuleBylhs(lhs));
	}
	/*
	 * 根据规则右部得到所有概率对应规则
	 */
	public Set<PRule> getPRuleByrhs(ArrayList<String> rhsList){
		return convertRewriteRuleSetToPRuleSet(super.getRuleByrhs(rhsList));
	}
	/*
	 * 由RewriteRule集转换为PRule规则集
	 */
	private Set<PRule> convertRewriteRuleSetToPRuleSet(Set<RewriteRule> ruleset){
		Iterator<RewriteRule> itr=ruleset.iterator();
		Set<PRule> pruleSet=new HashSet<PRule>();
		while(itr.hasNext()) {
	     	  pruleSet.add((PRule)itr.next());
	        }
		return pruleSet;
	}
	//根据规则中的终结符和非终结符获取概率
	public PRule getPRuleByLHSAndRHS(String lhs,ArrayList<String> rhs) {
		   Set<PRule> set=this.getPRuleBylhs(lhs);
		   if(set!=null) {
			   for(PRule prule:set) {
				   if(prule.getRhs().equals(rhs)) {
					   return prule;
				   }
				   
			   }
		   }
	       return null;
	   }
}
