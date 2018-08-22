package com.lc.nlp4han.constituent;

import java.io.IOException;

import com.lc.nlp4han.constituent.pcfg.TreeBankReport;

public class PSTBankReportTool
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
		String encoding = "GBK";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				frompath = args[i + 1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		
		PSTBankReport getTL = new PSTBankReport();
		TreeBankReport treeBankReport = getTL.getInformationOfTreeLibrary(frompath, encoding);
		System.out.println(treeBankReport.toString());
	}
}
