package com.lc.nlp4han.constituent.lex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LexGrammarExtractorTool
{
	/**
	 * 提取LexGrammar的命令行应用程序
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}
		String frompath = null;
		String topath = null;
		String encoding = "UTF-8";
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
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		if (topath != null)
		{
			ExtractLexGrammarToFile(frompath, topath, encoding);
		}
		else
		{
				System.out.println(new LexGrammarExtractor().extractGrammar(frompath, encoding).toString());
		}
	}
	/**
	 * 从树库中提取文法，然后存入文件指定中
	 */
	private static void ExtractLexGrammarToFile(String fromPath, String toPath, String inCoding)
			throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath), inCoding));
		bw.append(new LexGrammarExtractor().extractGrammar(fromPath, inCoding).toString());
		bw.close();
	}
}
