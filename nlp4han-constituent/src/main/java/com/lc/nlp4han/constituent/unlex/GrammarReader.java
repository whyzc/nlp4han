package com.lc.nlp4han.constituent.unlex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

/**
 * 从unlex语法模型中读取语法
 * 
 * @author 王宁
 */
public class GrammarReader
{
	public static Grammar readGrammar(String modelPath, String encoding) throws FileNotFoundException, IOException
	{
		PlainTextByLineStream stream = new PlainTextByLineStream(new FileInputStreamFactory(new File(modelPath)),
				encoding);
		String[] allSymbols = null;// ROOT、......
		Short[] numNonterminal = null;// ROOT 1
		HashMap<String, Short> sym_intValue = new HashMap<>();
		ArrayList<Short> tagWithRareWord = null;
		ArrayList<Integer> rareWordCount = null;
		int allRareWord = 0;
		String str;
		if ((str = stream.read()) != null)
		{
			allSymbols = str.trim().split(" ");
		}
		if ((str = stream.read()) != null)
		{
			String[] numNonArr = str.trim().split(" ");
			numNonterminal = new Short[numNonArr.length];
			for (int i = 0; i < numNonArr.length; i++)
			{
				numNonterminal[i] = Short.parseShort(numNonArr[i]);
			}
		}
		for (short i = 0; i < allSymbols.length; i++)
		{
			sym_intValue.put(allSymbols[i], i);
		}
		if ((str = stream.read()) != null)
		{
			tagWithRareWord = new ArrayList<Short>();
			for (String tagIntValue : str.trim().split(" "))
			{
				tagWithRareWord.add(Short.parseShort(tagIntValue));
			}
		}
		if ((str = stream.read()) != null)
		{
			rareWordCount = new ArrayList<Integer>();
			for (String count : str.trim().split(" "))
			{
				rareWordCount.add(Integer.parseInt(count));
			}
		}

		if ((str = stream.read()) != null)
		{
			allRareWord = Integer.parseInt(str.trim());
		}

		HashMap<BinaryRule, LinkedList<LinkedList<LinkedList<Double>>>> bRulesMap = new HashMap<>();
		HashMap<UnaryRule, LinkedList<LinkedList<Double>>> uRulesMap = new HashMap<>();

		HashMap<PreterminalRule, LinkedList<Double>> preRulesMap = new HashMap<>();
		HashSet<String> dictionary = new HashSet<>();

		String[] rule = null;
		while ((str = stream.read()) != null)
		{
			boolean isPreterminalRule = false;
			if (str.equals(""))
			{
				isPreterminalRule = true;
				continue;
			}
			str = str.trim();
			rule = str.split(" ");
			if (rule.length == 4 && isPreterminalRule)
			{
				String word = rule[2];
				String tag = rule[0].split("_")[0];
				double score = Double.parseDouble(rule[3]);
				short index_subTag;
				short numTag = numNonterminal[sym_intValue.get(tag)];
				if (numTag == 1)
					index_subTag = 0;
				else
					index_subTag = Short.parseShort(rule[0].split("_")[1]);
				dictionary.add(word);
				PreterminalRule preRule = new PreterminalRule(sym_intValue.get(tag), word);
				LinkedList<Double> scores;
				if (preRulesMap.containsKey(preRule))
				{
					scores = preRulesMap.get(preRule);
					scores.set(index_subTag, score);
				}
				else
				{
					scores = new LinkedList<Double>();
					for (int i = 0; i < numTag; i++)
					{
						scores.add(0.0);
					}
					preRulesMap.put(preRule, scores);
				}
			}
			else if (rule.length == 4)
			{
				String parent = rule[0].split("_")[0];
				String child = rule[2].split("_")[0];
				double score = Double.parseDouble(rule[3]);
				short index_pSubSym;
				short index_cSubSym;
				short numSymP = numNonterminal[sym_intValue.get(parent)];
				short numSymC = numNonterminal[sym_intValue.get(child)];
				if (numSymP == 1)
					index_pSubSym = 0;
				else
					index_pSubSym = Short.parseShort(rule[0].split("_")[1]);
				if (numSymC == 1)
					index_cSubSym = 0;
				else
					index_cSubSym = Short.parseShort(rule[2].split("_")[1]);
				UnaryRule uRule = new UnaryRule(sym_intValue.get(parent), sym_intValue.get(child));
				LinkedList<LinkedList<Double>> scores;
				if (uRulesMap.containsKey(uRule))
				{
					scores = uRulesMap.get(uRule);
					scores.get(index_pSubSym).set(index_cSubSym, score);
				}
				else
				{
					scores = new LinkedList<LinkedList<Double>>();
					for (int i = 0; i < numSymP; i++)
					{
						LinkedList<Double> list = new LinkedList<Double>();
						for (int j = 0; j < numSymC; j++)
						{
							list.add(0.0);
						}
						scores.add(list);
					}
					uRulesMap.put(uRule, scores);
				}
			}
			else if (rule.length == 5)
			{
				String parent = rule[0].split("_")[0];
				String lChild = rule[2].split("_")[0];
				String rChild = rule[3].split("_")[0];
				double score = Double.parseDouble(rule[4]);
				short index_pSubSym;
				short index_lCSubSym;
				short index_rCSubSym;
				short numSymP = numNonterminal[sym_intValue.get(parent)];
				short numSymLC = numNonterminal[sym_intValue.get(lChild)];
				short numSymRC = numNonterminal[sym_intValue.get(rChild)];
				if (numSymP == 1)
					index_pSubSym = 0;
				else
					index_pSubSym = Short.parseShort(rule[0].split("_")[1]);
				if (numSymLC == 1)
					index_lCSubSym = 0;
				else
					index_lCSubSym = Short.parseShort(rule[2].split("_")[1]);
				if (numSymRC == 1)
					index_rCSubSym = 0;
				else
					index_rCSubSym = Short.parseShort(rule[3].split("_")[1]);
				BinaryRule bRule = new BinaryRule(sym_intValue.get(parent), sym_intValue.get(lChild),
						sym_intValue.get(rChild));
				LinkedList<LinkedList<LinkedList<Double>>> scores;
				if (bRulesMap.containsKey(bRule))
				{
					scores = bRulesMap.get(bRule);
					scores.get(index_pSubSym).get(index_lCSubSym).set(index_rCSubSym, score);
				}
				else
				{
					scores = new LinkedList<LinkedList<LinkedList<Double>>>();
					for (int i = 0; i < numSymP; i++)
					{
						LinkedList<LinkedList<Double>> lr = new LinkedList<LinkedList<Double>>();
						for (int j = 0; j < numSymLC; j++)
						{
							LinkedList<Double> r = new LinkedList<Double>();
							for (int k = 0; k < numSymRC; k++)
							{
								r.add(0.0);
							}
							lr.add(r);
						}
						scores.add(lr);
					}
					bRulesMap.put(bRule, scores);
				}
			}
		}
		stream.close();
		Lexicon lexicon = new Lexicon(new HashSet<PreterminalRule>(preRulesMap.keySet()), dictionary, tagWithRareWord,
				rareWordCount, allRareWord);// 包含preRules
		Grammar g = new Grammar(new HashSet<BinaryRule>(bRulesMap.keySet()), new HashSet<UnaryRule>(uRulesMap.keySet()),
				lexicon, null);
		return g;
	}
}
