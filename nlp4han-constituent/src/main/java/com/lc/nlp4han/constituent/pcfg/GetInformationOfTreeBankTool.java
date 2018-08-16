package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;

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
		String incoding = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-frompath"))
			{
				frompath = args[i + 1];
				i++;
			}
			if (args[i].equals("-incoding"))
			{
				incoding = args[i + 1];
				i++;
			}
		}
			GetInformationOfTreeBank getTL = new GetInformationOfTreeBank();
			TreeBankReport treeBankReport = getTL.getInformationOfTreeLibrary(frompath, incoding);
			System.out.println(treeBankReport.toString());
	}
}
