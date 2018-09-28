package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 王宁
 * @version 创建时间：2018年9月24日 下午8:00:38 记录树中的标记和与之对应整数
 */
public class NonterminalTable
{
	private HashMap<String, Short> str_intMap;
	private HashMap<Short, String> int_strMap;
	private short numSymbol;
	private ArrayList<Short> numSubsymbolArr;

	public NonterminalTable()
	{
		str_intMap = new HashMap<String, Short>();
		int_strMap = new HashMap<Short, String>();
		numSymbol = 0;
		numSubsymbolArr = new ArrayList<Short>();
	}

	public boolean hasSymbol(String symbol)
	{
		if (str_intMap.containsKey(symbol))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param 要添加的symbol
	 * @return 返回新symbol对应的整数，返回0表示已有symbol/int对
	 */
	public int putSymbol(String symbol)
	{
		if (hasSymbol(symbol))
			return 0;
		str_intMap.put(symbol, ++numSymbol);
		int_strMap.put(numSymbol, symbol);
		numSubsymbolArr.add((short) 1);
		return numSymbol;
	}

	public int intValue(String symbol)
	{
		return str_intMap.get(symbol);
	}

	public String stringValue(short intValue)
	{
		return int_strMap.get(intValue);
	}
	
	/**
	 * 
	 * @param symbol
	 * @return 返回该symbol分裂的个数
	 */
	public int numSubsymbol(String symbol)
	{
		if (hasSymbol(symbol))
		{
			int intVlaue = str_intMap.get(symbol);
			return numSubsymbolArr.get(intVlaue - 1);
		}
		else
		{
			return 0;
		}
	}

	public ArrayList<Short> getNumSubsymbolArr()
	{
		return numSubsymbolArr;
	}

	public void setNumSubsymbolArr(ArrayList<Short> numSubsymbolArr)
	{
		this.numSubsymbolArr = numSubsymbolArr;
	}

}
