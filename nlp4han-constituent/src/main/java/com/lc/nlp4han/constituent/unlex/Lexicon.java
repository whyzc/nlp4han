package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 用来保存preTerminalRule，以及用来预测一个未知词型的词性标记的概率P(word|tag)
 * 
 * @author 王宁
 * @version 创建时间：2018年10月11日 下午9:25:22
 */
public class Lexicon
{
	public static int DEFAULT_RAREWORD_THRESHOLD = 1;
	private HashSet<PreterminalRule> preRules;
	private HashSet<String> dictionary;

	private ArrayList<Short> tagWithRareWord;
	private ArrayList<Integer> rareWordCount;// rareWordCount[i]表示tagOfRareWord[i]对应的tag包含的rareword的个数
	private int allRareWord;

	private double[] scores;// 一个未知词是某tag的概率

	public Lexicon(HashSet<PreterminalRule> preRules, HashSet<String> dictionary,ArrayList<Short> tagWithRareWord ,
			ArrayList<Integer> rareWordCount, int allRareWord)
	{
		this.preRules = preRules;
		this.dictionary = dictionary;
		this.tagWithRareWord = tagWithRareWord;
		this.rareWordCount = rareWordCount;
		this.allRareWord = allRareWord;
		scores = new double[tagWithRareWord.size()];
		calculateScore();
	}

	public void calculateScore()
	{
		for (int i = 0; i < tagWithRareWord.size(); i++)
		{
			scores[i] = BigDecimal.valueOf(rareWordCount.get(i))
					.divide(BigDecimal.valueOf(allRareWord), 15,BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}

	public boolean hasRecorded(String word)
	{
		return dictionary.contains(word);
	}

	public HashSet<String> getDictionary()
	{
		return dictionary;
	}

	public void setDictionary(HashSet<String> dictionary)
	{
		this.dictionary = dictionary;
	}

	public double[] getScores()
	{
		return scores;
	}

	public void setScores(double[] scores)
	{
		this.scores = scores;
	}

	public HashSet<PreterminalRule> getPreRules()
	{
		return preRules;
	}

	public void setPreRules(HashSet<PreterminalRule> preRules)
	{
		this.preRules = preRules;
	}

	public ArrayList<Short> getTagWithRareWord()
	{
		return tagWithRareWord;
	}

	public void setTagWithRareWord(ArrayList<Short> tagWithRareWord)
	{
		this.tagWithRareWord = tagWithRareWord;
	}

	public ArrayList<Integer> getRareWordCount()
	{
		return rareWordCount;
	}

	public void setRareWordCount(ArrayList<Integer> rareWordCount)
	{
		this.rareWordCount = rareWordCount;
	}

	public int getAllRareWord()
	{
		return allRareWord;
	}

	public void setAllRareWord(int allRareWord)
	{
		this.allRareWord = allRareWord;
	}

}
