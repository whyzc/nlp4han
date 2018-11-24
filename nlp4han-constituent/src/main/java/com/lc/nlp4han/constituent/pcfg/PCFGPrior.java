package com.lc.nlp4han.constituent.pcfg;

import java.util.HashMap;

public class PCFGPrior extends PCFG
{
	private HashMap<String, Double> priorMap;

	public PCFGPrior(PCFG pcfg, HashMap<String, Double> priorMap)
	{
		super(pcfg.getStartSymbol(), pcfg.getNonTerminalSet(), pcfg.getTerminalSet(), pcfg.getLHS2Rules(),
				pcfg.getRHS2Rules());
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
