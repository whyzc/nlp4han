package com.lc.nlp4han.constituent.pcfg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GrammarTest {
    private ArrayList<String> sentences;
    private ExtractGrammar extractGrammar;
    @Before
    public void BeforeTest() throws FileNotFoundException {
    	extractGrammar=new ExtractGrammar();
    	sentences=new ArrayList<String>();
    	sentences.add("(ROOT(IP(NP(Det (NN 市长)(的))(NP (NN 幕僚)))(VP(Aux-VP (会)(VP(VV 整理)(NP(Det (NN 产业整合)(的)(NN 详细报告))))))(PU 。)))");
    	sentences.add("(ROOT(IP(NP(NP (NR 中国))(NP (NN 篮球) (NN 协会)))(VP(PP (P 在)(NP(NP (NR 北京市) (NR 通州区) (NR 张家湾镇))"
    			+ "(NP (NN 中心) (NN 小学))))(VP (VV 举行) (AS 了)(NP(NN 篮球) (NN 联赛) (NN 启动) (NN 仪式))))(PU 。)))");
    }
    
    @Test
    public void getCFGTest() throws UnsupportedOperationException, FileNotFoundException, IOException {
    	extractGrammar.bracketStrListConvertToGrammar(sentences, "CFG");
    	CFG cfg=extractGrammar.getGrammar();
    	Set<RewriteRule> rules=new HashSet<RewriteRule>();//规则左侧为NP的集合
    	Set<RewriteRule> ruleMix=new HashSet<RewriteRule>();//测试一些特殊的规则
    	Set<RewriteRule> rules3=new HashSet<RewriteRule>();//测试规则右侧的集合
    	//以由左侧得到所有规则右侧来进行测试
    	String startSymbol="IP";//起始符
    	//非终结符集
    	String[] list= {"IP","NP","Det","NN","VP","PU","PP","P","NR","VV","AS","Aux-VP"};
    	Set<String> nonTerminal=new HashSet<String>();
    	for(String string:list) {
    		nonTerminal.add(string);
    	}
    	//终结符集
    	String[] list1= {"市长","的","幕僚","会","整理","产业整合","的","详细报告","。"
    			,"中国","篮球","协会","在","北京市","通州区","张家湾镇",
    			"中心","小学","举行","了","联赛","启动","仪式"};
    	Set<String> terminal=new HashSet<String>();
    	for(String string :list1) {
    		terminal.add(string);
    	}
    	ruleMix.add(new RewriteRule("Det","NN","的"));
    	ruleMix.add(new RewriteRule("Det","NN","的","NN"));
    	ruleMix.add(new RewriteRule("NR","北京市"));
    	ruleMix.add(new RewriteRule("NN","幕僚"));
    	ruleMix.add(new RewriteRule("VP","Aux-VP"));
    	ruleMix.add(new RewriteRule("Aux-VP","会","VP"));
    	ruleMix.add(new RewriteRule("NP","Det"));
    	
    	rules.add(new RewriteRule("NP","Det","NP"));
    	rules.add(new RewriteRule("NP","Det"));
    	rules.add(new RewriteRule("NP","NN"));
    	rules.add(new RewriteRule("NP","NP","NP"));
    	rules.add(new RewriteRule("NP","NR"));
    	rules.add(new RewriteRule("NP","NN","NN"));
    	rules.add(new RewriteRule("NP","NR","NR","NR"));
    	rules.add(new RewriteRule("NP","NN","NN","NN","NN"));
    	
    	rules3.add(new RewriteRule("Aux-VP","会","VP"));
    	ArrayList<String> strList=new ArrayList<String>();
    	strList.add("会");
    	strList.add("VP");
    	//判别是否为CNF的测试
    	Assert.assertFalse(cfg.IsCNF());
    	//测试由规则左侧得到的集合是否准确完整
    	Assert.assertTrue(cfg.getRuleBylhs("NP").containsAll(rules)&&rules.containsAll(cfg.getRuleBylhs("NP")));
    	//规则右侧集的测试
    	Assert.assertEquals(cfg.getRuleByrhs(strList),rules3);
    	//起始符测试
    	Assert.assertEquals(startSymbol,cfg.getStartSymbol());
    	//非终结符集的测试
    	Assert.assertEquals(nonTerminal, cfg.getNonTerminalSet());
    	//终结符集的测试
    	Assert.assertEquals(terminal, cfg.getTerminalSet());
    	//整体规则集的测试
    	Assert.assertTrue(cfg.getRuleSet().containsAll(ruleMix));	
    }
    }
