package com.lc.nlp4han.constituent.unlex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

/**
 * 从unlex语法模型中读取语法
 * 
 * @author 王宁
 */
public class GrammarReader
{
	public static Grammar readGrammar(String modelPath, String encoding) throws FileNotFoundException, IOException
	{
		Grammar g = new Grammar();
		PlainTextByLineStream stream = new PlainTextByLineStream(new FileInputStreamFactory(new File(modelPath)),
				encoding);
		String str = stream.read().trim();
		String[] allSymbols = null;// ROOT、......
		Short[] numNonterminal = null;// ROOT 1
		Short[] preSymbolIndex = null;

		if (str.equals("--起始符--"))
		{
			g.setStartSymbol(stream.read().trim());
		}
		str = stream.read().trim();
		if (str.equals("--非终结符集--"))
		{
			if ((str = stream.read()) != null)
			{
				allSymbols = str.trim().split(" ");
			}
			if ((str = stream.read()) != null)
			{
				String[] numNonArr = str.trim().split(" ");
				numNonterminal = new Short[numNonArr.length];
				for (int i = 0; i < numNonArr.length; i++)
				{
					numNonterminal[i] = Short.parseShort(numNonArr[i]);
				}
			}
			if ((str = stream.read()) != null)
			{
				String indexArr[] = str.split(" ");
				preSymbolIndex = new Short[indexArr.length];
				for (int i = 0; i < indexArr.length; i++)
				{
					preSymbolIndex[i] = Short.parseShort(indexArr[i]);
				}
			}
		}

		NonterminalTable nonterminalTable = new NonterminalTable();
		nonterminalTable.setNumInitialSymbol((short) allSymbols.length);
		for (String symbol : allSymbols)
		{
			nonterminalTable.putSymbol(symbol);
		}
		nonterminalTable.setIntValueOfPreterminalArr(new ArrayList<Short>(Arrays.asList(preSymbolIndex)));
		nonterminalTable.setNumSubsymbolArr(new ArrayList<Short>(Arrays.asList(numNonterminal)));
		g.setNontermianalTable(nonterminalTable);
		str = stream.read().trim();
		String[] rule = null;
		if (str.equals("--一元二元规则集--"))
		{
			while ((str = stream.read()) != null && !str.equals("--预终结规则--"))
			{
				str = str.trim();
				rule = str.split(" ");
				if (rule.length == 4)
					g.readBRule(rule);
				else if (rule.length == 5)
					g.readURule(rule);
			}
		}

		if (str.equals("--预终结规则--"))
		{
			while ((str = stream.read()) != null)
			{
				str = str.trim();
				rule = str.split(" ");
				if (rule.length == 4)
					g.readPreRule(rule);
			}
		}
		g.init();
		stream.close();
		return g;
	}
}
