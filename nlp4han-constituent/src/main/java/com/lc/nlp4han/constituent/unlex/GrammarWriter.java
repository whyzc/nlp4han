package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author 王宁 将grammar写到文本中
 */
public class GrammarWriter
{
	// TODO:待修改为输出标准格式
	public static void writeToFile(Grammar grammar, String filePath, boolean ruleSum) throws IOException
	{
		TreeMap<String, Map<String, Rule>> allBAndURules = new TreeMap<>();
		TreeMap<String, Map<String, PreterminalRule>> allPreRules = new TreeMap<>();
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (Short symbol : grammar.allNonterminalIntValArr())
		{
			Map<String, Rule> bAndURules = new TreeMap<>();
			if (grammar.getbRuleBySameHead().containsKey(symbol))
			{
				for (BinaryRule bRule : grammar.getbRuleBySameHead().get(symbol).keySet())
				{
					bAndURules.put(bRule.toStringIgnoreSubSymbol(grammar), bRule);
					for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum(grammar).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}
			if (grammar.getuRuleBySameHead().containsKey(symbol))
			{
				for (UnaryRule uRule : grammar.getuRuleBySameHead().get(symbol).keySet())
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
