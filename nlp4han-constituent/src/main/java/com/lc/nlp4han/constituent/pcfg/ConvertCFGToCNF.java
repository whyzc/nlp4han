package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConvertCFGToCNF {
	 private CFG cnf;
	 private Set<RewriteRule> set=new HashSet<RewriteRule>();
     public CFG convertToCNF(CFG cfg) {
    	 cnf=new CFG();
		 cnf.setNonTerminalSet(cfg.getNonTerminalSet());
         cnf.setTerminalSet(cfg.getTerminalSet());
    	//添加新的起始符
    	 addNewStartSymbol(cfg);
    	 //前期处理，遍历cfg将规则加入cnf
    	 priorDisposal(cfg);
    	 //消除Unit Production
    	 RemoveUnitProduction(cfg);
    	 return cnf;
     }
     /*
      * 添加新的起始符DuIP,新规则，DuIP->IP,因为我是在最后处理Unit Production,集合的遍历和修改不能同时进行，故将这个规则同时放入cfg中
      */
     private void addNewStartSymbol(CFG cfg) {
       String oldStartSymbol=cfg.getStartSymbol();
       String newStartSymbol= "Du"+cfg.getStartSymbol();
       cnf.setStartSymbol(newStartSymbol);//设置新的起始符
  	   cnf.addNonTerminal(newStartSymbol);//添加新的非终结符
  	   cnf.add(new RewriteRule(newStartSymbol,oldStartSymbol));
       cfg.add(new RewriteRule(newStartSymbol,oldStartSymbol));//添加新的规则
     } 
	 /* 
	  * 前期处理，遍历的将规则加入cnf
	  *将字符串个数多于两个的递归的减为两个
	  *将终结符和非终结符混合转换为两个非终结符
	  *直接添加右侧只有一个字符串的规则
	  */
     private void priorDisposal(CFG cfg) {
    	 for(RewriteRule rule :cfg.getRuleSet()) {
    		 if(rule.getRhs().size()>=3) {
    			//如果右侧中有终结符，则转换为伪非终结符
        		 if(!cnf.getNonTerminalSet().containsAll(rule.getRhs())) {
        			 ConvertToNonTerRHS(rule);
        		 }
        		 reduceRHSNum(rule); 
    		 }
    	     /*
    	      * 先检测右侧有两个字符串的规则是否为终结符和非终结符混合，若混合则先将终结符转换为非终结符，
    	      */
    		 if(rule.getRhs().size()==2) {
     			//如果右侧中有终结符，则转换为伪非终结符
         		 if(!cnf.getNonTerminalSet().containsAll(rule.getRhs())) {
         			 ConvertToNonTerRHS(rule);
         		 }
         		 cnf.add(rule);
     		 }
    		 /*
    		  * 先添加进cnf随后处理
    		  */
    		 if(rule.getRhs().size()==1) {
    			 if(rule.getLhs().equals(rule.getRhs().get(0))){
            		 continue;//左右相同的跳过，不添加
        		 }
    			 cnf.add(rule);
    		 }
    	 }
     }
     /*
      * 遍历CFG消除Unit Production
      */
     private void RemoveUnitProduction(CFG cfg) {
    	 HashSet<RewriteRule> deleteRuleSet=new HashSet<RewriteRule>();
    	 /*
    	  * 因为此时，原始数据cfg中右侧字符串的个数为1的规则并未处理
    	  * 而在前期操作中添加的右侧只有一个字符串的规则不需要转换，所以可以调用cfg来遍历
    	  */
    	 for(RewriteRule rule :cfg.getRuleSet()) {
    		 if(rule.getRhs().size()==1) {
    			 if(rule.getLhs().equals(rule.getRhs().get(0))){
            		 continue;//左右相同的跳过，不处理
        		 }
        		 if(cnf.getNonTerminalSet().containsAll(rule.getRhs())) {
        			 deleteRuleSet.add(rule);
        			 removeUnitProduction(rule);
        		 }
    		 }
    	 }
    	 //删除右侧为一个非终结符的规则
    	 deleteRules(deleteRuleSet);
     }
     /*
      * 将右侧全部转换为非终结符，并添加新的非终结符，新的规则
      */
     private void ConvertToNonTerRHS(RewriteRule rule) {
    	 ArrayList<String> rhs=new ArrayList<String>();
    	 for(String string : rule.getRhs()) {
    		 if(!cnf.getNonTerminalSet().contains(string)) {
    			 String newString="Du"+string;
    			 cnf.addNonTerminal(newString);//添加新非终结符
    			 cnf.add(new RewriteRule(newString,string));//添加新规则
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
     private void removeUnitProduction(RewriteRule rule) {
    	 if(set.contains(rule)) {
    		 System.out.println("rule "+rule);
    		 return;
    	 }
    	 set.add(rule);
    	 Set<RewriteRule> ruleSet=new HashSet<RewriteRule>();
    	 //需要将其重新装入一个HashSet中，否则在遍历时set的迭代器会报错
    	 for(RewriteRule rule4: cnf.getRuleBylhs(rule.getRhs().get(0))){
    		 ruleSet.add(rule4);
    	 }
    	 for(RewriteRule rule1: ruleSet) {
    		 RewriteRule rule2=new RewriteRule(rule.getLhs(),rule1.getRhs());
    		 if(rule1.getRhs().size()==1&&rule.getLhs().equals(rule1.getRhs().get(0))) {
    			 continue;//左右侧相同的跳过不添加
    		 }
    		if(rule1.getRhs().size()==1&&cnf.getNonTerminalSet().contains(rule1.getRhs().get(0))) {
    			removeUnitProduction(rule2); 
    		} else {
    			cnf.add(rule2); 
    		 }
    	 }
     }
     /*
      * 每次选择最右侧字符串的两个为新的规则的右侧字符串
      */
     private void reduceRHSNum(RewriteRule rule) {
    	 if(rule.getRhs().size()==2) {
      	   cnf.add(rule);
  		   return;
  	    }  
      	List<String> list=rule.getRhs();
      	int size=list.size();
      	String str=list.get(size-2)+list.get(size-1);//新规则的左侧
    	
      	//最右侧的两个非终结符合成一个，并形成新的规则
      	RewriteRule rule1=new RewriteRule(str,list.get(size-2),list.get(size-1));
      	cnf.add(rule1);
      	cnf.addNonTerminal(str);//添加新的非终结符
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
    		 cnf.getRuleSet().remove(rule);
    		 cnf.getRuleBylhs(rule.getLhs()).remove(rule);
    		 cnf.getRuleByrhs(rule.getRhs()).remove(rule);
    	 }
    	 
     }
}
