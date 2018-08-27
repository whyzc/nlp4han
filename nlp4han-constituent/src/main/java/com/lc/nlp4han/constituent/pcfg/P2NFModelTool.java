package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class P2NFModelTool
{
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}
		String corpusFile = null;
		String encoding = null;
		String topath = null;
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
			else if (args[i].equals("-topath"))
			{
				topath = args[i + 1];
				i++;
			}
		}
		GetP2NFModel(corpusFile, encoding, topath);
	}

	private static void GetP2NFModel(String corpusFile, String encoding, String topath) throws IOException
	{
		PCFG pcfg = GrammarExtractor.getPCFG(corpusFile, encoding);
		PCFG p2nf = new ConvertPCFGToP2NF().convertToCNF(pcfg);
		if (topath == null)
		{
			System.out.println(p2nf.toString());
		}
		else
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(topath), encoding));
			bw.append(p2nf.toString());
			bw.close();
		}
	}
}
