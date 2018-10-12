package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GrammarConvertorTool
{

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			System.out.println("请输入类似 -data 数据输入路径 -out 结果输出路径 -converttype 文法的目标类型 -encoding 编码格式");
			return;
		}
		String corpusFile = null;
		String type = null;
		String encoding = null;
		String topath = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-converttype"))
			{
				type = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-out"))
			{
				topath = args[i + 1];
				i++;
			}
		}
		GrammarConvertor(corpusFile, type, encoding, topath);
	}

	private static void GrammarConvertor(String corpusFile, String type, String encoding, String topath)
			throws IOException
	{
		GrammarConvertor convertor = new GrammarConvertor();
		CFG cfg, cnf;
		if (type.contains("P"))
		{
			cfg = new PCFG(new FileInputStream(new File(corpusFile)), encoding);
		}
		else
		{
			cfg = new CFG(new FileInputStream(new File(corpusFile)), encoding);
		}

		if (type.equals("CNF"))
		{
			cnf = convertor.convertCFGToCNF(cfg);
		}
		else if (type.equals("P2NF"))
		{
			cnf = convertor.convertPCFGToP2NF((PCFG) cfg);
		}
		else
		{
			cnf = convertor.convertPCFGToPCNF((PCFG) cfg);
		}
		if (topath == null)
		{
			System.out.println(cnf.toString());
		}
		else
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(topath), encoding));
			bw.append(cnf.toString());
			bw.close();
		}
	}
}
