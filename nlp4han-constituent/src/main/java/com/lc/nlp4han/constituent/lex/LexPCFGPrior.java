package com.lc.nlp4han.constituent.lex;

import java.util.HashMap;

public class LexPCFGPrior extends LexPCFG
{
	private HashMap<String, Double> priorMap = new HashMap<String, Double>();

	public LexPCFGPrior(LexPCFG lexpcfg, HashMap<String, Double> priorMap)
	{
		super(lexpcfg.getStartSymbol(), lexpcfg.getPosSet(), lexpcfg.getWordMap(), lexpcfg.getPosesOfWord(),
				lexpcfg.getHeadGenMap(), lexpcfg.getParentList(), lexpcfg.getSidesGeneratorMap(),
				lexpcfg.getStopGenMap(), lexpcfg.getSpecialGenMap());
		this.priorMap = priorMap;
	}

	public HashMap<String, Double> getPriorMap()
	{
		return priorMap;
	}

	public void setPriorMap(HashMap<String, Double> priorMap)
	{
		this.priorMap = priorMap;
	}

}
