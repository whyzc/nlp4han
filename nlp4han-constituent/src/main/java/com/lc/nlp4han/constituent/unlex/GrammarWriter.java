package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;

/**
 * @author 王宁 将grammar写到文本中
 */
public class GrammarWriter
{
	public static void writerToFile(Grammar grammar, String filePath) throws IOException
	{
		TreeSet<String> allBAndURules = new TreeSet<String>();
		TreeSet<String> allPreRules = new TreeSet<String>();
		TreeSet<String> allURules = new TreeSet<>();
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (BinaryRule bRule : grammar.bRules)
		{
			String[] ruleStr = bRule.toStringRules();
			allBAndURules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum().entrySet())
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
			String[] ruleStr = uRule.toStringRules();
			allBAndURules.addAll(Arrays.asList(ruleStr));
			allURules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : uRule.getParent_i_ScoceSum().entrySet())
			{
				sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(), (score, newScore) -> score + newScore);
			}
		}
		for (PreterminalRule preRule : grammar.lexicon.getPreRules())
		{
			String[] ruleStr = preRule.toStringRules();
			allPreRules.addAll(Arrays.asList(ruleStr));
			for (Map.Entry<String, Double> entry : preRule.getParent_i_ScoceSum().entrySet())
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
}
