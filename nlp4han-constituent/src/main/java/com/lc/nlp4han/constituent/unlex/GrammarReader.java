package com.lc.nlp4han.constituent.unlex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
		String str = stream.read();
		if (str != null)
			str = str.trim();
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
				String indexArr[] = str.trim().split(" ");
				preSymbolIndex = new Short[indexArr.length];
				for (int i = 0; i < indexArr.length; i++)
				{
					preSymbolIndex[i] = Short.parseShort(indexArr[i]);
				}
			}
		}
		NonterminalTable nonterminalTable = new NonterminalTable();
		for (String symbol : allSymbols)
		{
			nonterminalTable.putSymbol(symbol);
		}
		nonterminalTable.setIntValueOfPreterminalArr(new ArrayList<Short>(Arrays.asList(preSymbolIndex)));
		nonterminalTable.setNumSubsymbolArr(new ArrayList<Short>(Arrays.asList(numNonterminal)));
		g.setNontermianalTable(nonterminalTable);
		str = stream.read();
		if (str != null)
			str = str.trim();
		String[] rule = null;
		if (str.equals("--一元二元规则集--"))
		{
			while ((str = stream.read()) != null && !str.equals("--预终结符规则集--"))
			{
				str = str.trim();
				rule = str.split(" ");
				if (rule.length == 4)
					g.readURule(rule);
				else if (rule.length == 5)
					g.readBRule(rule);
			}
		}

		if (str.equals("--预终结符规则集--"))
		{
			while ((str = stream.read()) != null)
			{
				str = str.trim();
				if(str.equals("--end--")) {
					break;
				}
				rule = str.split(" ");
				if (rule.length == 4)
					g.readPreRule(rule);
			}
		}
		stream.close();
		return g;
	}

	public static void main(String[] args)
	{
		try
		{
			Grammar g = readGrammar("C:\\Users\\hp\\Desktop\\standard.grammar.allRule", "utf-8");
			System.out.println("语法读取完毕。");
			GrammarWriter.writeToFileStandard(g, "C:\\Users\\hp\\Desktop\\888888", false);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
