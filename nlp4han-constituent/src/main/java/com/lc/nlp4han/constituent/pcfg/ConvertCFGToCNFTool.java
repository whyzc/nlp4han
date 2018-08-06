package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author qyl
 *
 */
public class ConvertCFGToCNFTool
{

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}
		String CFGpath = null;
		String CNFpath = null;
		String encoding = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-CFGpath"))
			{
				CFGpath = args[i + 1];
				i++;
			}
			if (args[i].equals("-CNFpath"))
			{
				CNFpath = args[i + 1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		readCFGAndConvertToCNF(CFGpath, CNFpath, encoding);

	}

	/**
	 * 从文档中读取CFG,然后转为CNF并存储到指定文件
	 * @param CFGpath
	 * @param CNFpath
	 * @param encoding
	 * @throws IOException
	 */
	private static void readCFGAndConvertToCNF(String CFGpath, String CNFpath, String encoding) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CNFpath), encoding));
		CFG cfg = GetGrammarFromFile.getCFGFromFile(CFGpath, encoding);
		bw.append(new ConvertCFGToCNF().convertToCNF(cfg).toString());
		bw.close();
	}
}
