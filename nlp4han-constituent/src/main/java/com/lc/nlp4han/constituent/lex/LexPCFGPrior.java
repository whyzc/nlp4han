package com.lc.nlp4han.constituent.lex;

import java.util.HashMap;
/**
 * 添加词性标注的概率，并在解析时作为先验概率
 * @author qyl
 *
 */
public class LexPCFGPrior extends LexPCFG
{
	private HashMap<String, Double> priorMap = new HashMap<String, Double>();

	public LexPCFGPrior()
	{
	}

	public HashMap<String, Double> getPriorMap()
	{
		return priorMap;
	}

	public double getPosPro(String pos)
	{
		return priorMap.get(pos);
	}

	public void setPriorMap(HashMap<String, Double> priorMap)
	{
		this.priorMap = priorMap;
	}

}
