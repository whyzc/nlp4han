package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GetInformationOfTreeBankTool
{
	/**
	 * 获取树库信息
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
			if (args[i].equals("-incoding"))
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
			GetTreeBankInformationToFile(frompath, topath, incoding);
		}
		else
		{
			GetInformationOfTreeBank getTL = new GetInformationOfTreeBank();
			TreeBankReport treeBankReport = getTL.getInformationOfTreeLibrary(frompath, incoding);
			System.out.println(treeBankReport.toString());
		}
	}

	/**
	 * 从树库中提取文法，然后存入文件指定中
	 */
	private static void GetTreeBankInformationToFile(String fromPath, String toPath, String inCoding) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath), inCoding));
		GetInformationOfTreeBank getTL = new GetInformationOfTreeBank();
		TreeBankReport treeBankReport = getTL.getInformationOfTreeLibrary(fromPath, inCoding);
		bw.append(treeBankReport.toString());
		bw.close();
	}
}
