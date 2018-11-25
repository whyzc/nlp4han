package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 语法归纳应用
 * 
 * 从树库中归纳解析器需要的文法模型
 * 
 * 和交叉验证使用相同的文法
 * 
 *
 */
public class GrammarInducerlTool
{
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}
		String corpusFile = null;
		String encoding = "UTF-8";
		String modelFile = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-model"))
			{
				modelFile = args[i + 1];
				i++;
			}
		}
		
		GetP2NFModel(corpusFile, encoding, modelFile);
	}

	private static void GetP2NFModel(String corpusFile, String encoding, String modelFile) throws IOException
	{
		PCFG pcfg = GrammarExtractor.getPCFG(corpusFile, encoding);
		
//		PCFG p2nf = GrammarConvertor.convertPCFGToPCNF(pcfg);
		PCFG p2nf = GrammarConvertor.convertPCFGToP2NF(pcfg);
		
		if (modelFile == null)
		{
			System.out.println(p2nf.toString());
		}
		else
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelFile), encoding));
			bw.append(p2nf.toString());
			bw.close();
		}
	}
}
