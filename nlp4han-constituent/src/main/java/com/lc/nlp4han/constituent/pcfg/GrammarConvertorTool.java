package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;

public class GrammarConvertorTool
{

	public static void main(String[] args) throws IOException, ClassNotFoundException
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
			throws IOException, ClassNotFoundException
	{
		CFG cfg, cnf;
		cfg = CFGModelIOUtil.loadModel(corpusFile);

		if (type.equals("CNF"))
		{
			cnf = GrammarConvertor.CFG2CNF(cfg);
		}
		else if (type.equals("P2NF"))
		{
			cnf = GrammarConvertor.PCFG2LoosePCNF((PCFG) cfg);
		}
		else
		{
			cnf = GrammarConvertor.PCFG2PCNF((PCFG) cfg);
		}
		
		if (topath == null)
		{
			System.out.println(cnf.toString());
		}
		else
		{
			CFGModelIOUtil.writeModel(cnf, topath);
		}
	}
}
