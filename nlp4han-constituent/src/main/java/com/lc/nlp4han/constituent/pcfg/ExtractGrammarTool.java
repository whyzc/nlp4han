package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ExtractGrammarTool {
    /*
     * 提取CFG的命令行应用程序
     */
	public static void main(String[] args) throws IOException{
		if (args.length < 1)
		{
			return;
		}
		String frompath = null;
		String topath = null;
		String incoding = null;
		String outcoding = null;
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
			if(args[i].equals("-outcoding")) {
				outcoding=args[i+1];
				i++;
			}
		}
		if(topath!=null) {
			ExtractGrammarToFile(frompath,topath,incoding,outcoding);
		}else {
			System.out.println(new Extract(frompath,incoding).getCFG().toString());
		}
	}
   /*
    * 从树库中提取文法，然后存入文件指定中
    */
   public static void ExtractGrammarToFile(String fromPath,String toPath,String inCoding,String outCoding) throws UnsupportedOperationException, IOException {
	   BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath),outCoding));
	   
	   Extract ext=new Extract(fromPath,inCoding);
	   CFG  cfg=ext.getCFG();
	   bw.append(cfg.toString());
	   bw.close();
   }
}
