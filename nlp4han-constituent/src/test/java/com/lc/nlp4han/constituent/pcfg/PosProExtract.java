package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.util.HashMap;

public class PosProExtract
{
	public static void main(String args[]) throws IOException
	{
		HashMap<String, Double> map = NonterminalProUtil.getNonterminalPro("D:\\NLP\\Train.txt", "UTF-8");
		double sum=0;
		for (String str : map.keySet())
		{
			sum+=map.get(str);
			System.out.println(str + "= " + map.get(str));
		}
		System.out.println("sum的值="+sum);
	}
}
