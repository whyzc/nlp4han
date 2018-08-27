package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GrammarExractorTool
{
	/**
	 * 提取Grammar的命令行应用程序
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}
		String frompath = null;
		String topath = null;
		String incoding = null;
		String type = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-frompath"))
			{
				frompath = args[i + 1];
				i++;
			}
			if (args[i].equals("-topath"))
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
				incoding = args[i + 1];
				i++;
			}
		}
		if (topath != null)
		{
			/*
			 * 存储文法和提取文法格式一般相同
			 */
			ExtractGrammarToFile(frompath, topath, incoding, type);
		}
		else
		{
			if (type.contains("P"))
			{
				System.out.println(GrammarExtractor.getPCFG(frompath, incoding).toString());
			}
			else
			{
				System.out.println(GrammarExtractor.getCFG(frompath, incoding).toString());
			}
		}
	}

	/**
	 * 从树库中提取文法，然后存入文件指定中
	 */
	private static void ExtractGrammarToFile(String fromPath, String toPath, String inCoding, String type)
			throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath), inCoding));
		if (type.contains("P"))
		{
			bw.append(GrammarExtractor.getPCFG(fromPath, inCoding).toString());
		}
		else
		{
			bw.append(GrammarExtractor.getCFG(fromPath, inCoding).toString());
		}
		bw.close();
	}
}
