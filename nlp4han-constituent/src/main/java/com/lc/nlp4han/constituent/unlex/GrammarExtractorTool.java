package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author 王宁
 * @version 创建时间：2018年9月28日 上午8:48:34 获得Grammar的工具类
 */
public class GrammarExtractorTool
{
	public static Grammar generateInitialGrammar(String treeBankPath) throws IOException
	{
		List<Tree<String>> trees = new ArrayList<Tree<String>>();
		InputStream ins = new FileInputStream(treeBankPath);
		InputStreamReader isr = new InputStreamReader(ins, "gbk");
		BufferedReader allSentence = new BufferedReader(isr);
		String expression = allSentence.readLine();
		
		while (expression != null)//用来得到树库对应的所有结构树Tree<String>
		{
			expression = expression.trim();
			if (!expression.equals(""))
			{
				System.out.println(expression);
			}
			expression = allSentence.readLine();
		}
		allSentence.close();
		
		//将Tree<String> 转化为Tree<>Annotation
		NonterminalTable convertTable = new NonterminalTable();
		List<Tree<Annotation>> annotationTrees = TreeProcessor.getAnnotationTrees(trees, convertTable);
		trees=null;
		
		GrammarExtractor grammarExtractor = new GrammarExtractor(annotationTrees,convertTable);
		grammarExtractor.extractor();
		
		HashSet<BinaryRule> bRules;
		HashSet<UnaryRule> uRules;
		HashSet<PreterminalRule> preRules;
		HashMap<BinaryRule, Integer> allBRule  = new HashMap<BinaryRule, Integer>();
		HashMap<PreterminalRule, Integer> allPreRule = new HashMap<PreterminalRule, Integer>();
		HashMap<UnaryRule, Integer> allURule = new HashMap<UnaryRule,Integer>();
		for(HashMap<BinaryRule, Integer> map :grammarExtractor.bRuleBySameHead)
		{
			allBRule.putAll(map);
		}
		for(HashMap<PreterminalRule, Integer> map : grammarExtractor.preRuleBySameHead)
		{
			allPreRule.putAll(map);
		}
		for(HashMap<UnaryRule, Integer> map : grammarExtractor.uRuleBySameHead)
		{
			allURule.putAll(map);
		}
		bRules = new HashSet<BinaryRule>(allBRule.keySet());
		uRules = new HashSet<UnaryRule>(allURule.keySet());
		preRules = new HashSet<PreterminalRule>(allPreRule.keySet());
		//
		grammarExtractor.bRuleBySameHead.get(0).putAll(grammarExtractor.bRuleBySameHead.get(1));
		Grammar intialG = new Grammar(grammarExtractor.treeBank,bRules,uRules,preRules,grammarExtractor.nonterminalTable); 
		return intialG;
		}
	public static void main(String[] args)
	{

		try
		{
			GrammarExtractorTool.generateInitialGrammar("C:\\Users\\hp\\Desktop\\test(2).txt");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
