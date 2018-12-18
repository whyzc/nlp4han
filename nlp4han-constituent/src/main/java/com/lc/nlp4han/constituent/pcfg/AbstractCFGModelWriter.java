package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.lc.nlp4han.constituent.pcfg.CFG;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.constituent.pcfg.RewriteRule;
/**
 * 写入CFG模型的抽象类
 * @author qyl
 *
 */
abstract class AbstractCFGModelWriter implements CFGModelWriter
{
	private String startSymbol;// 起始符

	private HashMap<String, Double> posMap;// 词性标注及概率映射

	private Set<String> nonTerminalSet;// 非终结符集

	private Set<String> terminalSet;// 终结符集

	private Set<RewriteRule> ruleSet;// 规则集

	public AbstractCFGModelWriter()
	{
		super();
	}

	public AbstractCFGModelWriter(CFG cfg)
	{
		startSymbol = cfg.getStartSymbol();
		posMap = getPosMap(cfg);
		nonTerminalSet = cfg.getNonTerminalSet();
		terminalSet = cfg.getTerminalSet();
		ruleSet = cfg.getRuleSet();
	}

	/**
	 * 得到词性标注概率映射
	 * 
	 * @param cfg
	 * @return
	 */
	private HashMap<String, Double> getPosMap(CFG cfg)
	{
		PCFG pcfg = (PCFG) cfg;
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (String pos : cfg.getPosSet())
		{
			map.put(pos, pcfg.getPosPro(pos));
		}
		return map;
	}

	@Override
	public void persist() throws IOException
	{
		/**
		 * 写入起始符
		 */
		writeUTF("--起始符--");
		writeUTF(startSymbol);

		/**
		 * 写入非终结符
		 */
		writeUTF("--非终结符集--");
		for (String nonter : nonTerminalSet)
		{
			writeUTF(nonter);
		}

		/**
		 * 写入终结符
		 */
		writeUTF("--终结符集--");
		for (String ter : terminalSet)
		{
			writeUTF(ter);
		}

		/**
		 * 写入词性标注映射
		 */
		writeUTF("--词性标注映射--");
		for (String pos : posMap.keySet())
		{
			writeUTF(pos + "=" + posMap.get(pos));
		}

		/**
		 * 写入规则集
		 */
		writeUTF("--规则集--");
		for (RewriteRule rule : ruleSet)
		{
			writeUTF(rule.toString());
		}
		
		writeUTF("完");
		close();
	}
}
