package com.lc.nlp4han.constituent.pcfg;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文法提取应用
 * 
 * 支持提取CFG和PCFG文法
 *
 */
public class GrammarExractorTool
{
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}
		String frompath = null;
		String topath = null;
		String encoding = null;
		String type = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				frompath = args[i + 1];
				i++;
			}
			if (args[i].equals("-out"))
			{
				topath = args[i + 1];
				i++;
			}
			if (args[i].equals("-type"))
			{
				type = args[i + 1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		
		type = type.toUpperCase();
		
		
		if (topath != null)
		{
			ExtractGrammarToFile(frompath, topath, encoding, type);
		}
		else
		{
			if (type.equals("PCFG"))
			{
				System.out.println(GrammarExtractor.getPCFG(frompath, encoding).toString());
			}
			else if(type.equals("CFG"))
			{
				System.out.println(GrammarExtractor.getCFG(frompath, encoding).toString());
			}
			else
				System.out.println("抽取文法类型不对: " + type);
		}
	}

	/**
	 * 从树库中提取文法，然后存入文件指定中
	 */
	private static void ExtractGrammarToFile(String fromPath, String toPath, String inCoding, String type)
			throws IOException
	{
		DataOutputStream out=new DataOutputStream(new FileOutputStream(toPath));
		
		if (type.equals("PCFG"))
		{
			PCFG pcfg=GrammarExtractor.getPCFG(fromPath, inCoding);
			pcfg.write(out);
		}
		else if(type.equals("CFG"))
		{
			CFG cfg=GrammarExtractor.getCFG(fromPath, inCoding);
			cfg.write(out);
		}
		else {
			System.out.println("抽取文法类型不对: " + type);
		}
	}
}
