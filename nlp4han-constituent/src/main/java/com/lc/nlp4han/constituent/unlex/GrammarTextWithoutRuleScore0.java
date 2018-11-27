package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * @author 王宁
 */
public class GrammarTextWithoutRuleScore0
{

	public static void main(String[] args) throws IOException
	{
		String grammarTextFile = args[0];
		String out = args[1];
		ArrayList<String> allRule = new ArrayList<String>();
		FileInputStream fis = new FileInputStream(grammarTextFile);
		InputStreamReader isr = new InputStreamReader(fis, "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String rule;
		int count_0 = 0;// 记录为零的规则数
		int count_0NoPreterminal = 0;
		while ((rule = br.readLine()) != null)
		{
			if ((rule = rule.trim()) != "")
			{
				String[] arr = rule.split(" ");
				try
				{
					if (Double.parseDouble(arr[arr.length - 1]) != 0.0)
						allRule.add(rule);
					else
						count_0++;
				}
				catch (NumberFormatException e)
				{
					count_0NoPreterminal = count_0;
					System.out.println("概率为零的非preterminalRule总数：" + count_0NoPreterminal);
					allRule.add("");
				}
			}
		}
		System.out.println("概率为零的preterminalRule总数：" + (count_0 - count_0NoPreterminal));
		System.out.println("概率为零的所有规则总数：" + count_0);
		br.close();
		FileOutputStream fos = new FileOutputStream(out);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		for (String aRule : allRule)
		{
			bw.write(aRule + "\n");
		}
		bw.close();
	}

}
