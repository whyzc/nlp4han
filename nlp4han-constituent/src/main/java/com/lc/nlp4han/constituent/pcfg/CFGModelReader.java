package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.lc.nlp4han.constituent.pcfg.CFG;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.constituent.pcfg.PRule;
import com.lc.nlp4han.constituent.pcfg.RewriteRule;

public class CFGModelReader extends AbstractCFGModelReader
{
	public CFGModelReader(File file) throws IOException {
		super(file);
	}

	public CFGModelReader(CFGDataReader dataReader)
	{
		super(dataReader);
	}

	@Override
	public CFG constructModel() throws IOException, ClassNotFoundException
	{
		boolean isPCFG =false;
		String startSymbol = null;
		Set<String> nonTerminalSet = new HashSet<String>();// 非终结符集
		Set<String> terminalSet = new HashSet<String>();// 终结符集
		HashMap<String, Double> posMap = new HashMap<String, Double>();// 词性标注-概率映射
		Set<RewriteRule> ruleSet = new HashSet<RewriteRule>();// 规则集
		
		String str = readUTF();
		if (str.equals("--起始符--"))
		{
			startSymbol=readUTF();
		}
		readUTF();//此行为--非终结符--，不处理
		
		str = readUTF();
		while (!str.equals("--终结符集--"))
		{
			nonTerminalSet.add(str);
			str =readUTF();
		}

		str =readUTF();
		while (!str.equals("--词性标注映射--"))
		{
			terminalSet.add(str);
			str = readUTF();
		}

		str = readUTF();
		while (!str.equals("--规则集--"))
		{
			String[] strs=str.split("=");
			posMap.put(strs[0],Double.parseDouble(strs[1]) );
			str = readUTF();
		}
		
		str = readUTF();
		String[] strs=str.split(" ");
		if(strs[strs.length-2].equals("----"))
			isPCFG=true;
		while (!str.equals("完"))
		{
			if(isPCFG) {
				ruleSet.add(new PRule(str));			
			}else {
				ruleSet.add(new RewriteRule(str));
			}
			str = readUTF();
		}
		
        close();
        if(isPCFG)	
        	return 	new PCFG(startSymbol, posMap, nonTerminalSet, terminalSet,ruleSet);
        else 
        	return 	new CFG(startSymbol, posMap, nonTerminalSet, terminalSet,ruleSet);
	}

}
