package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/*
 * 文法包含包含重写规则，非终结符集，终结符集
 */
public class CFG {
	private String startSymbol;
	private Set<String> nonTerminalSet=new HashSet<String>();//非终结符集
	private Set<String> terminalSet=new HashSet<String>();//终结符集
	private Set<RewriteRule> ruleSet=new HashSet<RewriteRule>();//规则集
	private HashMap<String,HashSet<RewriteRule>> ruleMapStartWithlhs=new HashMap<String,HashSet<RewriteRule>>();//以左部为key值的规则集map
    private HashMap<ArrayList<String>,HashSet<RewriteRule>> ruleMapStartWithrhs=
    		     new HashMap<ArrayList<String>,HashSet<RewriteRule>>();//以规则右部为key值的规则集map
	/*
     * 构造函数,一步创建
     */
    public CFG(Set<String> nonTerminalSet, Set<String> terminalSet,
			HashMap<String, HashSet<RewriteRule>> ruleMapStartWithlhs,
			HashMap<ArrayList<String>,HashSet<RewriteRule>> ruleMapStartWithrhs) {
		this.nonTerminalSet = nonTerminalSet;
		this.terminalSet = terminalSet;
		this.ruleMapStartWithlhs = ruleMapStartWithlhs;
		this.ruleMapStartWithrhs = ruleMapStartWithrhs;
		for(String lhs:ruleMapStartWithlhs.keySet()) {
			ruleSet.addAll(ruleMapStartWithlhs.get(lhs));
		}
	}
	/*
	 * 通过一步一步添加rule来实现规则集，终结符/非终结符的更新
	 */
	public CFG() {
		
	}
	/*
	 * 判断是否为CNF
	 */
	public boolean IsCNF() {
		boolean isCNF=true;
		for(RewriteRule rule:ruleSet) {
			ArrayList<String> list=rule.getRhs();
			if(list.size()>=3) {
				isCNF=false;
				break;
			} 
            if(list.size()==2) {
            	for(String string: list) {
            		if(!nonTerminalSet.contains(string)) {
            			isCNF=false;
            			break;
            		}
            	}
            }
            if(list.size()==1) {
            	if(nonTerminalSet.contains(list.get(0))) {
            		isCNF=false;
            		break;
            	}
            }
		}
		return isCNF;	
	}
	/*
	 * 方法
	 */
	public String getStartSymbol() {
		return startSymbol;
	}
	public void setStartSymbol(String startSymbol) {
		this.startSymbol = startSymbol;
	}
    public Set<String> getNonTerminalSet() {
		return nonTerminalSet;
	}
	public void setNonTerminalSet(Set<String> nonTerminalSet) {
		this.nonTerminalSet = nonTerminalSet;
	}
	public Set<String> getTerminalSet() {
		return terminalSet;
	}
	public void setTerminalSet(Set<String> terminalSet) {
		this.terminalSet = terminalSet;
	}
		/*
		 * 添加单个规则
		 */
	public void add(RewriteRule rule) {	
	  ruleSet.add(rule);
	  if(ruleMapStartWithlhs.get(rule.getLhs())!=null) {
		  ruleMapStartWithlhs.get(rule.getLhs()).add(rule);
  	  }else {
		 HashSet<RewriteRule> set=new HashSet<RewriteRule>();
		 set.add(rule);
		 ruleMapStartWithlhs.put(rule.getLhs(),set);
  	  }
	  if(ruleMapStartWithrhs.keySet().contains(rule.getRhs())) {
		  ruleMapStartWithrhs.get(rule.getRhs()).add(rule); 
	  }else {
		  HashSet<RewriteRule> set=new  HashSet<RewriteRule>();
		  set.add(rule);
		  ruleMapStartWithrhs.put(rule.getRhs(), set);
	  }
	}
	/*
	 * 得到规则集
	 */
	public Set<RewriteRule> getRuleSet() {
		return ruleSet;
	}
	/*
	 * 单独添加非中介符与非终结符
	 */
	public void addNonTerminal(String nonTer) {
		nonTerminalSet.add(nonTer);
	}
	public void addTerminal(String terminal) {
		terminalSet.add(terminal);
	}
		
	/*
	 * 根据规则左部得到所有对应规则
	 */
	public Set<RewriteRule> getRuleBylhs(String lhs){
		return ruleMapStartWithlhs.get(lhs);	
	}
	/*
	 * 根据规则右部得到所有对应规则
	 */
	public Set<RewriteRule> getRuleByrhs(ArrayList<String> rhsList){
		return ruleMapStartWithrhs.get(rhsList);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nonTerminalSet == null) ? 0 : nonTerminalSet.hashCode());
		result = prime * result + ((ruleMapStartWithlhs == null) ? 0 : ruleMapStartWithlhs.hashCode());
		result = prime * result + ((ruleMapStartWithrhs == null) ? 0 : ruleMapStartWithrhs.hashCode());
		result = prime * result + ((ruleSet == null) ? 0 : ruleSet.hashCode());
		result = prime * result + ((startSymbol == null) ? 0 : startSymbol.hashCode());
		result = prime * result + ((terminalSet == null) ? 0 : terminalSet.hashCode());
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
		CFG other = (CFG) obj;
		if (nonTerminalSet == null) {
			if (other.nonTerminalSet != null)
				return false;
		} else if (!nonTerminalSet.equals(other.nonTerminalSet))
			return false;
		if (ruleMapStartWithlhs == null) {
			if (other.ruleMapStartWithlhs != null)
				return false;
		} else if (!ruleMapStartWithlhs.equals(other.ruleMapStartWithlhs))
			return false;
		if (ruleMapStartWithrhs == null) {
			if (other.ruleMapStartWithrhs != null)
				return false;
		} else if (!ruleMapStartWithrhs.equals(other.ruleMapStartWithrhs))
			return false;
		if (ruleSet == null) {
			if (other.ruleSet != null)
				return false;
		} else if (!ruleSet.equals(other.ruleSet))
			return false;
		if (startSymbol == null) {
			if (other.startSymbol != null)
				return false;
		} else if (!startSymbol.equals(other.startSymbol))
			return false;
		if (terminalSet == null) {
			if (other.terminalSet != null)
				return false;
		} else if (!terminalSet.equals(other.terminalSet))
			return false;
		return true;
	}
	@Override
	public String toString()  {
		StringBuilder stb=new StringBuilder();
		Iterator<String> itr1=nonTerminalSet.iterator();
		stb.append("--起始符--"+'\n');
		stb.append(this.getStartSymbol()+'\n');
		
		stb.append("--非终结符集--"+'\n');
		while(itr1.hasNext()) {
			stb.append(itr1.next()+'\n');
		}
		
		Iterator<String> itr2=terminalSet.iterator();
		stb.append("--终结符集--"+'\n');
		while(itr2.hasNext()) {
			stb.append(itr2.next()+'\n');
		}
		
		stb.append("--规则集--"+'\n');
		Set<String> set=ruleMapStartWithlhs.keySet();
		for(String string : set) {
			HashSet<RewriteRule> ruleSet=ruleMapStartWithlhs.get(string);
			Iterator<RewriteRule> itr3=ruleSet.iterator();
			while(itr3.hasNext()) {
				stb.append(itr3.next().toString()+'\n');
			}
		}
		return stb.toString();
	}
}