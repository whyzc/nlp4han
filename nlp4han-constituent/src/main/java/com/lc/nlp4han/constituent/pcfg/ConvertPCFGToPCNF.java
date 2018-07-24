package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConvertPCFGToPCNF
{
	private PCFG pcnf;
	private PCFG pcfg;
	private Set<RewriteRule> set=new HashSet<RewriteRule>();
	private HashMap<ArrayList<String>,PRule> repeatRuleMap=new HashMap<ArrayList<String>,PRule>();
    public PCFG convertToCNF(PCFG pcfg) {
     this.pcfg=pcfg;
   	 pcnf=new PCFG();
	 pcnf.setNonTerminalSet(pcfg.getNonTerminalSet());
     pcnf.setTerminalSet(pcfg.getTerminalSet());
   	//添加新的起始符
   	 addNewStartSymbol(pcfg);
   	 //前期处理，遍历pcfg将规则加入pcnf
   	 priorDisposal(pcfg);
   	 //消除Unit Production
   	 RemoveUnitProduction(pcfg);
   	 return pcnf;
    }
    /*
     * 添加新的起始符DuIP,新规则，DuIP->IP,因为我是在最后处理Unit Production,集合的遍历和修改不能同时进行，故将这个规则同时放入pcfg中
     */
    private void addNewStartSymbol(PCFG pcfg) {
      String oldStartSymbol=pcfg.getStartSymbol();
      String newStartSymbol= "Du"+pcfg.getStartSymbol();
      pcnf.setStartSymbol(newStartSymbol);//设置新的起始符
 	  pcnf.addNonTerminal(newStartSymbol);//添加新的非终结符
 	  pcnf.add(new PRule(1.0,newStartSymbol,oldStartSymbol));
      pcfg.add(new PRule(1.0,newStartSymbol,oldStartSymbol));//添加新的规则
    } 
	 /* 
	  * 前期处理，遍历的将规则加入pcnf
	  *将字符串个数多于两个的递归的减为两个
	  *将终结符和非终结符混合转换为两个非终结符
	  *直接添加右侧只有一个字符串的规则
	  */
    private void priorDisposal(PCFG pcfg) {
   	 for(PRule rule :pcfg.getPRuleSet()) {
   		 if(rule.getRhs().size()>=3) {
   			//如果右侧中有终结符，则转换为伪非终结符
       		 if(!pcnf.getNonTerminalSet().containsAll(rule.getRhs())) {
       			 ConvertToNonTerRHS(rule);
       		 }
       		 reduceRHSNum(rule); 
   		 }
   	     /*
   	      * 先检测右侧有两个字符串的规则是否为终结符和非终结符混合，若混合则先将终结符转换为非终结符，
   	      */
   		 if(rule.getRhs().size()==2) {
    			//如果右侧中有终结符，则转换为伪非终结符
        		 if(!pcnf.getNonTerminalSet().containsAll(rule.getRhs())) {
        			 ConvertToNonTerRHS(rule);
        		 }
        		 pcnf.add(rule);
    		 }
   		 /*
   		  * 先添加进pcnf随后处理
   		  */
   		 if(rule.getRhs().size()==1) {
   			 pcnf.add(rule);
   		 }
   	 }
    }
    /*
     * 遍历PCFG消除Unit Production
     */
    private void RemoveUnitProduction(PCFG pcfg) {
   	 HashSet<RewriteRule> deleteRuleSet=new HashSet<RewriteRule>();
   	 /*
   	  * 因为此时，原始数据pcfg中右侧字符串的个数为1的规则并未处理
   	  * 而在前期操作中添加的右侧只有一个字符串的规则不需要转换，所以可以调用pcfg来遍历
   	  */
   	 for(PRule rule :pcfg.getPRuleSet()) {
   		 if(rule.getRhs().size()==1) {
       		 if(pcnf.getNonTerminalSet().containsAll(rule.getRhs())) {
       			 //System.out.println("测试--"+rule);
       			 deleteRuleSet.add(rule);
       			 removeUnitProduction(rule);
       		 }
   		 }
   	 }
   	 //删除右侧为一个非终结符的规则
   	 deleteRules(deleteRuleSet);
     addRepeatRulePro();
    }
    /*
     * 将右侧全部转换为非终结符，并添加新的非终结符，新的规则
     */
    private void ConvertToNonTerRHS(RewriteRule rule) {
   	 ArrayList<String> rhs=new ArrayList<String>();
   	 for(String string : rule.getRhs()) {
   		 if(!pcnf.getNonTerminalSet().contains(string)) {
   			 String newString="Du"+string;
   			 pcnf.addNonTerminal(newString);//添加新非终结符
   			 pcnf.add(new PRule(1.0,newString,string));//添加新规则
   			 rhs.add(newString);
   		 }else {
   			 rhs.add(string);
   		 }
   	 }
   	 rule.setRhs(rhs);
    }
    /*
     * 遍历消除Unit Production
     */
    private void removeUnitProduction(PRule rule) {
     Set<PRule> ruleSet=pcfg.getPRuleBylhs(rule.getRhs().get(0));
   	 for(PRule rule1: ruleSet) {
   		 PRule rule2=new PRule(rule.getProOfRule()*rule1.getProOfRule(),rule.getLhs(),rule1.getRhs());
   			if(!(rule1.getRhs().size()==1&&pcfg.getNonTerminalSet().contains(rule1.getRhs().get(0)))) {
   			    if(pcnf.getPRuleSet().contains(rule2)) {
   			       addRulePro(rule2);
   			    }else {
   	   				pcnf.add(rule2);   			    	
   			    }
   			}else {
   				if(SetContains(rule2)) {
   					addReaptRule(rule2);
   					continue;
   				}else {
   			   	    set.add(rule);
   	   				removeUnitProduction(rule2);  					
   				}
   			}
   	 }
    }
    /*
     * 每次选择最右侧字符串的两个为新的规则的右侧字符串
     */
    private void reduceRHSNum(PRule rule) {
   	 if(rule.getRhs().size()==2) {
     	  pcnf.add(rule);
 		  return;
 	    }  
     	List<String> list=rule.getRhs();
     	int size=list.size();
     	String str=list.get(size-2)+list.get(size-1);//新规则的左侧
   	
     	//最右侧的两个非终结符合成一个，并形成新的规则
     	PRule rule1=new PRule(1.0,str,list.get(size-2),list.get(size-1));
     	pcnf.add(rule1);
     	pcnf.addNonTerminal(str);//添加新的非终结符
     	ArrayList<String> rhsList=new ArrayList<String>();
     	rhsList.addAll(rule.getRhs().subList(0,rule.getRhs().size()-2));
     	rhsList.add(str);
     	rule.setRhs(rhsList);
     	/*
     	 *递归，直到rhs的个数为2时
     	 */
     	reduceRHSNum(rule);
    }
    /*
     * 删除右侧为一个非终结符的规则，需要同时在RuleSet,ruleMapStartWithlhs,ruleMapStartWithrhs中删除
     */
    private void deleteRules(HashSet<RewriteRule> ruleSet) {
   	 for(RewriteRule rule: ruleSet) {
   		 pcnf.getRuleSet().remove(rule);
   		 pcnf.getRuleBylhs(rule.getLhs()).remove(rule);
   		 pcnf.getRuleByrhs(rule.getRhs()).remove(rule);
   	 }
    }
    /*
     * 当pcnf中已经存在该规则时，则添加概率
     */
    private void addRulePro(PRule rule) {
    	PRule rule1=pcnf.getPRuleByLHSAndRHS(rule.getLhs(),rule.getRhs());
    	double pro=rule1.getProOfRule();
    	deleteRule(rule1);
    	rule.setProOfRule(rule.getProOfRule()+pro);
    	pcnf.add(rule);
    }
    /*
     * 删除规则
     */
    private void deleteRule(PRule rule) {
  		 pcnf.getRuleSet().remove(rule);
  		 pcnf.getRuleBylhs(rule.getLhs()).remove(rule);
  		 pcnf.getRuleByrhs(rule.getRhs()).remove(rule);
    }
    /*
     *添加在消除Unit Production时重复规则的概率
     */    
    private void addRepeatRulePro() {
   	for(PRule rule:repeatRuleMap.values()) {
    	for(PRule prule:pcnf.getPRuleBylhs(rule.getRhs().get(0))) {
    			PRule rule1=pcnf.getPRuleByLHSAndRHS(rule.getLhs(), prule.getRhs());
    			double pro=prule.getProOfRule();
    			double pro23=pro*rule.getProOfRule()+rule1.getProOfRule();
    			deleteRule(rule1);
      			PRule prule2=new PRule(pro23,rule.getLhs(),prule.getRhs());
    			pcnf.add(prule2);
    			
    		}
       }
    }
    /*
     * 添加重复规则，并将重复的规则进行概率相加
     */
    private void addReaptRule(PRule rule) {
    	ArrayList<String> list=new ArrayList<String>();
    	list.add(rule.getLhs());
    	list.add(rule.getRhs().get(0));
        if(repeatRuleMap.containsKey(list)) {
        	double temp=repeatRuleMap.get(list).getProOfRule()+rule.getProOfRule();
        	repeatRuleMap.get(list).setProOfRule(temp);
        }else {
        	repeatRuleMap.put(list, rule);       	
        }

    }
    /*
     * 查看有无重复规则的
     */
    private boolean SetContains(PRule prule) {
    	for(RewriteRule rule:set) {
    		if(rule.getLhs().equals(prule.getLhs())&&rule.getRhs().equals(prule.getRhs())) {
    			return true;
    		}
    	}
    	return false;
    }
}
