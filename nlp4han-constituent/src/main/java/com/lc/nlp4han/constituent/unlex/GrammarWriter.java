package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;

/**
 * @author 王宁 将grammar写到文本中
 */
public class GrammarWriter
{
	public static void writeToFiles(Grammar grammar, String filePath) throws IOException
	{
		TreeSet<String> allBAndURules = new TreeSet<String>();
		TreeSet<String> allPreRules = new TreeSet<String>();
		TreeSet<String> allURules = new TreeSet<>();
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (BinaryRule bRule : grammar.bRules)
		{
			String[] ruleStr = bRule.toStringRules(grammar.nonterminalTable);
			allBAndURules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum(grammar.nonterminalTable).entrySet())
			{
				sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), new BiFunction<Double, Double, Double>()
				{
					@Override
					public Double apply(Double t, Double u)
					{
						return t + u;
					}
				});
			}
		}
		for (UnaryRule uRule : grammar.uRules)
		{
			String[] ruleStr = uRule.toStringRules(grammar.nonterminalTable);
			allBAndURules.addAll(Arrays.asList(ruleStr));
			allURules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : uRule.getParent_i_ScoceSum(grammar.nonterminalTable).entrySet())
			{
				sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), (score, newScore) -> score + newScore);
			}
		}
		for (PreterminalRule preRule : grammar.lexicon.getPreRules())
		{
			String[] ruleStr = preRule.toStringRules(grammar.nonterminalTable);
			allPreRules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : preRule.getParent_i_ScoceSum(grammar.nonterminalTable).entrySet())
			{
				sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), (score, newScore) -> score + newScore);
			}
		}

		FileOutputStream fos = new FileOutputStream(filePath + ".BandURule");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "gbk");
		BufferedWriter bAndURuleWriter = new BufferedWriter(osw);

		FileOutputStream fos2 = new FileOutputStream(filePath + ".PreRule");
		OutputStreamWriter osw2 = new OutputStreamWriter(fos2, "gbk");
		BufferedWriter preRuleWriter = new BufferedWriter(osw2);

		FileOutputStream fos3 = new FileOutputStream(filePath + ".URule");
		OutputStreamWriter osw3 = new OutputStreamWriter(fos3, "gbk");
		BufferedWriter uRuleWriter = new BufferedWriter(osw3);

		FileOutputStream fos4 = new FileOutputStream(filePath + ".RuleScoreSum");
		OutputStreamWriter osw4 = new OutputStreamWriter(fos4, "gbk");
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

	public static void writeToFile(Grammar grammar, String filePath) throws IOException
	{
		TreeMap<String, Map<String, Rule>> allBAndURules = new TreeMap<>();
		TreeMap<String, Map<String, PreterminalRule>> allPreRules = new TreeMap<>();
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (short symbol : grammar.nonterminalTable.getInt_strMap().keySet())
		{
			Map<String, Rule> bAndURules = new TreeMap<>();
			if (grammar.bRuleBySameHead.containsKey(symbol))
			{
				for (BinaryRule bRule : grammar.bRuleBySameHead.get(symbol).keySet())
				{
					bAndURules.put(bRule.toStringIgnoreSubSymbol(grammar.nonterminalTable), bRule);
					for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum(grammar.nonterminalTable)
							.entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								new BiFunction<Double, Double, Double>()
								{
									@Override
									public Double apply(Double t, Double u)
									{
										return t + u;
									}
								});
					}
				}
			}
			if (grammar.uRuleBySameHead.containsKey(symbol))
			{
				for (UnaryRule uRule : grammar.uRuleBySameHead.get(symbol).keySet())
				{
					bAndURules.put(uRule.toStringIgnoreSubSymbol(grammar.nonterminalTable), uRule);
					for (Map.Entry<String, Double> entry : uRule.getParent_i_ScoceSum(grammar.nonterminalTable)
							.entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}
			allBAndURules.put(grammar.nonterminalTable.stringValue(symbol), bAndURules);

			Map<String, PreterminalRule> preRules = new TreeMap<>();
			if (grammar.preRuleBySameHead.containsKey(symbol))
			{
				for (PreterminalRule preRule : grammar.preRuleBySameHead.get(symbol).keySet())
				{
					preRules.put(preRule.toStringIgnoreSubSymbol(grammar.nonterminalTable), preRule);
					for (Map.Entry<String, Double> entry : preRule.getParent_i_ScoceSum(grammar.nonterminalTable)
							.entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
				allPreRules.put(grammar.nonterminalTable.stringValue(symbol), preRules);
			}
		}
		FileOutputStream fos = new FileOutputStream(filePath + ".allRule");
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter rulesWriter = new BufferedWriter(osw);

		FileOutputStream fos4 = new FileOutputStream(filePath + ".RuleScoreSum");
		OutputStreamWriter osw4 = new OutputStreamWriter(fos4, "utf-8");
		BufferedWriter sameParentRuleScoreWriter = new BufferedWriter(osw4);

		for (Map.Entry<String, Map<String, Rule>> entry : allBAndURules.entrySet())
		{
			for (Map.Entry<String, Rule> innerEntry : entry.getValue().entrySet())
			{
				for (String ruleStr : innerEntry.getValue().toStringRules(grammar.nonterminalTable))
				{
					rulesWriter.write(ruleStr + "\r");
				}
			}
		}
		rulesWriter.write("\r");
		for (Map.Entry<String, Map<String, PreterminalRule>> entry : allPreRules.entrySet())
		{
			for (Map.Entry<String, PreterminalRule> innerEntry : entry.getValue().entrySet())
			{
				for (String ruleStr : innerEntry.getValue().toStringRules(grammar.nonterminalTable))
				{
					rulesWriter.write(ruleStr + "\r");
				}
			}

		}
		for (Map.Entry<String, Double> entry : sameParentRuleScoreSum.entrySet())
		{
			sameParentRuleScoreWriter.write(entry.getKey() + " " + entry.getValue() + "\r");
		}
		rulesWriter.close();
		sameParentRuleScoreWriter.close();
	}
}
