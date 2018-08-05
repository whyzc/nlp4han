package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ConvertPCFGToP2NFTool
{
	/**
	 * 由PCFG提取P2CNF的命令行应用程序
	 */
	public static void main(String[] args) throws IOException{
		if (args.length < 1)
		{
			return;
		}
		String frompath = null;
		String topath = null;
		String incoding = null;
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
			if(args[i].equals("-incoding")) {
				incoding=args[i+1];
				i++;
			}
		}
		if(topath!=null) {
			/*
			 * 存储文法和提取文法格式一般相同
			 */
			ConvertPCFGToPCNFToFile(frompath,topath,incoding);
		}else {
			PCFG pcfg=GetGrammarFromFile.getPCFGFromFile(frompath, incoding);
			System.out.println(new ConvertPCFGToP2NF().convertToCNF(pcfg).toString());
       }
	}
	/**
	* 从树库中提取PCFG文法，转换为P2NF然后存入指定文件中
	*/
	private static void ConvertPCFGToPCNFToFile(String fromPath,String toPath,String inCoding) throws UnsupportedOperationException, IOException {
	   BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath),inCoding));   
	   PCFG pcfg=GetGrammarFromFile.getPCFGFromFile(fromPath, inCoding);
	   bw.append(new ConvertPCFGToP2NF().convertToCNF(pcfg).toString());
	   bw.close();
	}
}