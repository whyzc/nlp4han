package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author 王宁 将grammar写到文本中
 */
public class GrammarWriter
{
	/**
	 * 输出标准格式
	 * 
	 * @param gLatentA
	 *            语法
	 * @param outPath
	 * @param ruleSum
	 *            是否输出相同父节点的规则概率之和
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void writeToFileStandard(Grammar gLatentA, String outPath, boolean ruleSum) throws IOException
	{
		TreeMap<String, Map<String, Double>[]> allBAndURules = new TreeMap<>();
		TreeMap<String, Map<String, Double>[]> allPreRules = new TreeMap<>();
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (Short symbol : gLatentA.allNonterminalIntValArr())
		{
			String symbolStr = gLatentA.symbolStrValue(symbol);
			short numSubSymbol = gLatentA.getNumSubSymbol(symbol);
			Map<String, Double>[] bAndURulesBySameSubHead = new HashMap[numSubSymbol];
			for (int i = 0; i < bAndURulesBySameSubHead.length; i++)
			{
				bAndURulesBySameSubHead[i] = new HashMap<String, Double>();
			}
			if (gLatentA.getbRuleSetBySameHead(symbol) != null)
			{
				for (BinaryRule bRule : gLatentA.getbRuleSetBySameHead(symbol))
				{
					String[] subRulesOfbRule = bRule.toStringRules(gLatentA);
					int c = subRulesOfbRule.length / numSubSymbol;
					int index = -1;
					for (int j = 0; j < subRulesOfbRule.length; j++)
					{
						if (j % c == 0)
							index++;
						bAndURulesBySameSubHead[index].put(subRulesOfbRule[j], Double
								.parseDouble(subRulesOfbRule[j].substring(subRulesOfbRule[j].lastIndexOf(" ") + 1)));
					}
					for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum(gLatentA).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), (newScore, score) ->
						{
							return score + newScore;
						});
					}
				}
			}
			if (gLatentA.getuRuleSetBySameHead(symbol) != null)
			{
				for (UnaryRule uRule : gLatentA.getuRuleSetBySameHead(symbol))
				{
					String[] subRuleOfuRule = uRule.toStringRules(gLatentA);
					int c = subRuleOfuRule.length / numSubSymbol;
					int index = -1;
					for (int j = 0; j < subRuleOfuRule.length; j++)
					{
						if (j % c == 0)
							index++;
						bAndURulesBySameSubHead[index].put(subRuleOfuRule[j],
								Double.parseDouble(subRuleOfuRule[j].substring(subRuleOfuRule[j].lastIndexOf(" "))));
					}
					for (Map.Entry<String, Double> entry : uRule.getParent_i_ScoceSum(gLatentA).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}
			allBAndURules.put(symbolStr, bAndURulesBySameSubHead);

			Map<String, Double>[] preRulesBySameSubHead = new HashMap[gLatentA.getNumSubSymbol(symbol)];
			for (int i = 0; i < preRulesBySameSubHead.length; i++)
			{
				preRulesBySameSubHead[i] = new HashMap<String, Double>();
			}
			if (gLatentA.getPreRuleBySameHead().containsKey(symbol))
			{
				for (PreterminalRule preRule : gLatentA.getPreRuleBySameHead().get(symbol).keySet())
				{
					String[] subRuleOfpreRule = preRule.toStringRules(gLatentA);
					int c = subRuleOfpreRule.length / numSubSymbol;
					int index = -1;
					for (int j = 0; j < subRuleOfpreRule.length; j++)
					{
						if (j % c == 0)
							index++;
						preRulesBySameSubHead[index].put(subRuleOfpreRule[j], Double
								.parseDouble(subRuleOfpreRule[j].substring(subRuleOfpreRule[j].lastIndexOf(" "))));
					}
					for (Map.Entry<String, Double> entry : preRule.getParent_i_ScoceSum(gLatentA).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}
			allPreRules.put(symbolStr, preRulesBySameSubHead);
		}
		FileOutputStream fos = new FileOutputStream(outPath + ".allRule");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter rulesWriter = new BufferedWriter(osw);
		rulesWriter.write("--起始符--" + "\n");
		rulesWriter.write(gLatentA.getStartSymbol() + "\n");
		rulesWriter.write("--非终结符集--" + "\n");
		for (int symbol = 0; symbol < gLatentA.getNumSymbol(); symbol++)
		{
			String sym = gLatentA.symbolStrValue((short) symbol);
			if (symbol != gLatentA.getNumSymbol() - 1)
			{
				sym += " ";
			}
			rulesWriter.write(sym);
		}
		rulesWriter.write("\n");
		for (int symbol = 0; symbol < gLatentA.getNumSymbol(); symbol++)
		{
			short numSymbol = gLatentA.getNumSubSymbol((short) symbol);
			String numStr = String.valueOf(numSymbol);
			if (symbol != gLatentA.getNumSymbol() - 1)
			{
				numStr += " ";
			}
			rulesWriter.write(numStr);
		}
		rulesWriter.write("\n");
		for (int i = 0; i < gLatentA.allPreterminal().size(); i++)
		{
			short preterminal = gLatentA.allPreterminal().get(i);
			String pretermianlStr = String.valueOf(preterminal);
			if (i != gLatentA.allPreterminal().size() - 1)
			{
				pretermianlStr += " ";
			}
			rulesWriter.write(pretermianlStr);
		}
		rulesWriter.write("\r");
		rulesWriter.write("--一元二元规则集--" + "\n");
		for (Map.Entry<String, Map<String, Double>[]> entry : allBAndURules.entrySet())
		{
			for (Map<String, Double> innerEntry : entry.getValue())
			{
				ArrayList<Map.Entry<String, Double>> list = new ArrayList<>(innerEntry.entrySet());
				sortList(list);
				for (Map.Entry<String, Double> subRule : list)
				{
					rulesWriter.write(subRule.getKey() + "\n");
				}
			}
		}
		rulesWriter.write("--预终结符规则--" + "\n");
		for (Map.Entry<String, Map<String, Double>[]> entry : allPreRules.entrySet())
		{
			for (Map<String, Double> innerEntry : entry.getValue())
			{
				ArrayList<Map.Entry<String, Double>> list = new ArrayList<>(innerEntry.entrySet());
				sortList(list);
				for (Map.Entry<String, Double> subRule : list)
				{
					rulesWriter.write(subRule.getKey() + "\n");
				}
			}
		}
		rulesWriter.close();
	}

	public static void sortList(ArrayList<Map.Entry<String, Double>> list)
	{
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>()
		{
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2)
			{
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
	}

	public static void writeToFile(Grammar grammar, String filePath, boolean ruleSum) throws IOException
	{
		TreeMap<String, Map<String, Rule>> allBAndURules = new TreeMap<>();
		TreeMap<String, Map<String, PreterminalRule>> allPreRules = new TreeMap<>();
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (Short symbol : grammar.allNonterminalIntValArr())
		{
			Map<String, Rule> bAndURules = new TreeMap<>();
			if (grammar.getbRuleSetBySameHead(symbol) != null)
			{
				for (BinaryRule bRule : grammar.getbRuleSetBySameHead(symbol))
				{
					bAndURules.put(bRule.toStringIgnoreSubSymbol(grammar), bRule);
					for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum(grammar).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}
			if (grammar.getuRuleSetBySameHead(symbol) != null)
			{
				for (UnaryRule uRule : grammar.getuRuleSetBySameHead(symbol))
				{
					bAndURules.put(uRule.toStringIgnoreSubSymbol(grammar), uRule);
					for (Map.Entry<String, Double> entry : uRule.getParent_i_ScoceSum(grammar).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}
			allBAndURules.put(grammar.symbolStrValue(symbol), bAndURules);

			Map<String, PreterminalRule> preRules = new TreeMap<>();
			if (grammar.getPreRuleBySameHead().containsKey(symbol))
			{
				for (PreterminalRule preRule : grammar.getPreRuleBySameHead().get(symbol).keySet())
				{
					preRules.put(preRule.toStringIgnoreSubSymbol(grammar), preRule);
					for (Map.Entry<String, Double> entry : preRule.getParent_i_ScoceSum(grammar).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
				allPreRules.put(grammar.symbolStrValue(symbol), preRules);
			}
		}
		FileOutputStream fos = new FileOutputStream(filePath + ".allRule");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter rulesWriter = new BufferedWriter(osw);

		for (Map.Entry<String, Map<String, Rule>> entry : allBAndURules.entrySet())
		{
			for (Map.Entry<String, Rule> innerEntry : entry.getValue().entrySet())
			{
				innerEntry.getValue().write(rulesWriter, grammar);
			}
		}
		rulesWriter.write("\r");
		for (Map.Entry<String, Map<String, PreterminalRule>> entry : allPreRules.entrySet())
		{
			for (Map.Entry<String, PreterminalRule> innerEntry : entry.getValue().entrySet())
			{
				innerEntry.getValue().write(rulesWriter, grammar);
			}
		}
		rulesWriter.close();
		if (ruleSum)
		{
			FileOutputStream fos4 = new FileOutputStream(filePath + ".RuleScoreSum");
			OutputStreamWriter osw4 = new OutputStreamWriter(fos4, "utf-8");
			BufferedWriter sameParentRuleScoreWriter = new BufferedWriter(osw4);
			for (Map.Entry<String, Double> entry : sameParentRuleScoreSum.entrySet())
			{
				sameParentRuleScoreWriter.write(entry.getKey() + " " + entry.getValue() + "\r");
			}
			sameParentRuleScoreWriter.close();
		}
	}

	public static void writeToFiles(Grammar grammar, String filePath) throws IOException
	{
		TreeSet<String> allBAndURules = new TreeSet<String>();
		TreeSet<String> allPreRules = new TreeSet<String>();
		TreeSet<String> allURules = new TreeSet<>();
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (BinaryRule bRule : grammar.getbRules())
		{
			String[] ruleStr = bRule.toStringRules(grammar);
			allBAndURules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum(grammar).entrySet())
			{
				sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), (score, newScore) -> score + newScore);
			}
		}
		for (UnaryRule uRule : grammar.getuRules())
		{
			String[] ruleStr = uRule.toStringRules(grammar);
			allBAndURules.addAll(Arrays.asList(ruleStr));
			allURules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : uRule.getParent_i_ScoceSum(grammar).entrySet())
			{
				sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), (score, newScore) -> score + newScore);
			}
		}
		for (PreterminalRule preRule : grammar.getLexicon().getPreRules())
		{
			String[] ruleStr = preRule.toStringRules(grammar);
			allPreRules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : preRule.getParent_i_ScoceSum(grammar).entrySet())
			{
				sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), (score, newScore) -> score + newScore);
			}
		}

		FileOutputStream fos = new FileOutputStream(filePath + ".BandURule");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bAndURuleWriter = new BufferedWriter(osw);

		FileOutputStream fos2 = new FileOutputStream(filePath + ".PreRule");
		OutputStreamWriter osw2 = new OutputStreamWriter(fos2, "utf-8");
		BufferedWriter preRuleWriter = new BufferedWriter(osw2);

		FileOutputStream fos3 = new FileOutputStream(filePath + ".URule");
		OutputStreamWriter osw3 = new OutputStreamWriter(fos3, "utf-8");
		BufferedWriter uRuleWriter = new BufferedWriter(osw3);

		FileOutputStream fos4 = new FileOutputStream(filePath + ".RuleScoreSum");
		OutputStreamWriter osw4 = new OutputStreamWriter(fos4, "utf-8");
		BufferedWriter sameParentRuleScoreWriter = new BufferedWriter(osw4);

		for (String bAndURuleStr : allBAndURules)
		{
			bAndURuleWriter.write(bAndURuleStr + "\r");
		}
		for (String preRuleStr : allPreRules)
		{
			preRuleWriter.write(preRuleStr + "\r");
		}
		for (String uRuleStr : allURules)
		{
			uRuleWriter.write(uRuleStr + "\r");
		}
		for (Map.Entry<String, Double> entry : sameParentRuleScoreSum.entrySet())
		{
			sameParentRuleScoreWriter.write(entry.getKey() + " " + entry.getValue() + "\r");
		}
		bAndURuleWriter.close();
		preRuleWriter.close();
		uRuleWriter.close();
		sameParentRuleScoreWriter.close();
	}
}
