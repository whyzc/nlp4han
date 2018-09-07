package com.lc.nlp4han.dependency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 王宁
 * @version 创建时间：2018年8月7日 下午6:11:16 统计中文依存分析语料基本情况
 */
public class DepCorpusReport
{

	private HashMap<String, Integer> word2Count;
	private HashMap<String, Integer> pos2Count;
	private HashMap<Character, Integer> char2Count;
	private HashMap<String, Integer> dep2Count;

	private int countWordTokens = 0;
	private int countCharTokens = 0;

	private HashMap<Integer, HashSet<String>> count2Words = new HashMap<Integer, HashSet<String>>();
	private HashMap<Integer, HashSet<Character>> count2Chars = new HashMap<Integer, HashSet<Character>>();
	private HashMap<Integer, HashSet<String>> count2Deps = new HashMap<Integer, HashSet<String>>();

	public int countSentences = 0;

	private int maxWordCount = 0;
	private int minWordCount = 1000;

	private int maxDepCount = 0;
	private int minDepCount = 1000;

	private int maxCharCount = 0;
	private int minCharCount = 1000;

	public DepCorpusReport(HashMap<String, Integer> word2Count, HashMap<String, Integer> pos2Count,
			HashMap<Character, Integer> char2Count, HashMap<String, Integer> dep2Count, int countWordTokens,
			int countCharTokens, int countSentences)
	{
		this.word2Count = word2Count;
		this.char2Count = char2Count;
		this.pos2Count = pos2Count;
		this.dep2Count = dep2Count;
		
		
		this.countSentences = countSentences;
		this.countWordTokens = countWordTokens;
		this.countCharTokens = countCharTokens;

		for (HashMap.Entry<String, Integer> entry : word2Count.entrySet())
		{
			String word = entry.getKey();
			int count = entry.getValue();

			if (count > maxWordCount)
				maxWordCount = count;

			if (count < minWordCount)
				minCharCount = count;

			HashSet<String> words = new HashSet<String>();
			if (count2Words.containsKey(count))
			{
				words = count2Words.get(count);
				words.add(word);
			}
			else
			{
				words.add(word);
				count2Words.put(count, words);
			}
		}

		for (HashMap.Entry<Character, Integer> entry : char2Count.entrySet())
		{
			char aChar = entry.getKey();
			int count = entry.getValue();

			if (count > maxCharCount)
				maxCharCount = count;

			if (count < minCharCount)
				minWordCount = count;

			HashSet<Character> chars = new HashSet<Character>();
			if (count2Chars.containsKey(count))
			{
				chars = count2Chars.get(count);
				chars.add(aChar);
			}
			else
			{
				chars.add(aChar);
				count2Chars.put(count, chars);
			}
		}

		for (HashMap.Entry<String, Integer> entry : dep2Count.entrySet())
		{
			String dep = entry.getKey();
			int count = entry.getValue();

			if (count > maxDepCount)
				maxDepCount = count;

			if (count < minDepCount)
				minDepCount = count;

			HashSet<String> deps = new HashSet<String>();
			if (count2Deps.containsKey(count))
			{
				deps = count2Deps.get(count);
				deps.add(dep);
			}
			else
			{
				deps.add(dep);
				count2Deps.put(count, deps);
			}
		}
	}

	public int numSentences()
	{
		return countSentences;
	}

	/**
	 * 词条数
	 * 
	 * @return 词条数
	 */
	public int numWordTokens()
	{
		return countWordTokens;
	}

	/**
	 * 词形数
	 * 
	 * @return 词形数
	 */
	public int numWordTypes()
	{
		return word2Count.keySet().size();
	}

	/**
	 * 字数
	 * 
	 * @return 字数
	 */
	public int numCharTokens()
	{
		return countCharTokens;
	}

	/**
	 * 字形数
	 * 
	 * @return 字形数
	 */
	public int numCharTypes()
	{
		return char2Count.keySet().size();
	}

	/**
	 * 词性种数
	 * 
	 * @return 词性种数
	 */
	public int numPosTypes()
	{
		return pos2Count.keySet().size();
	}

	public int numDepTypes()
	{
		return dep2Count.keySet().size();
	}

	/**
	 * 词典
	 * 
	 * @return 词典
	 */
	public Set<String> words()
	{
		return word2Count.keySet();
	}

	/**
	 * 字典
	 * 
	 * @return 字典
	 */
	public Set<Character> chars()
	{
		return char2Count.keySet();
	}

	/**
	 * 依存关系字典
	 * 
	 * @return 依存关系字典
	 */
	public Set<String> deps()
	{
		return dep2Count.keySet();
	}

	/**
	 * 给定词出现的次数
	 * 
	 * @param word
	 * @return 给定词出现的次数
	 */
	public int getWordOccurences(String word)
	{
		if (!word2Count.containsKey(word))
			return 0;
		else
			return word2Count.get(word);
	}

	/**
	 * 给定字出现的次数
	 * 
	 * @param c
	 * @return
	 */
	public int getCharOccurences(char c)
	{
		if (!char2Count.containsKey(c))
			return 0;
		else
			return char2Count.get(c);
	}

	/**
	 * 出现最多的词的出现次数
	 * 
	 * @return 出现最多的词的出现次数
	 */
	public int getMaxWordOccurences()
	{
		return maxWordCount;
	}

	/**
	 * 出现最多的字的出现次数
	 * 
	 * @return 出现最多的字的出现次数
	 */
	public int getMinWordOccurences()
	{
		return minWordCount;
	}

	/**
	 * 出现最少的词的出现次数
	 * 
	 * @return 出现最少的词的出现次数
	 */
	public int getMaxCharOccurences()
	{
		return maxCharCount;
	}

	/**
	 * 出现最少的字的出现次数
	 * 
	 * @return 出现最少的字的出现次数
	 */
	public int getMinCharOccurences()
	{
		return minCharCount;
	}

	/**
	 * 出现给定次数的词的列表
	 * 
	 * @param n
	 * @return 出现给定次数的词的列表
	 */
	public Set<String> getWords(int n)
	{
		if (!count2Words.containsKey(n))
			return new HashSet<String>();
		else
			return count2Words.get(n);
	}

	/**
	 * 出现给定次数的字的列表
	 * 
	 * @param n
	 * @return 出现给定次数的字的列表
	 */
	public Set<Character> getChars(int n)
	{
		if (!count2Chars.containsKey(n))
			return new HashSet<Character>();
		else
			return count2Chars.get(n);
	}

	/**
	 * 出现给定次数的关系的列表
	 * 
	 * @param n
	 * @return 出现给定次数的关系的列表
	 */
	public Set<String> getDep(int n)
	{
		if (!count2Deps.containsKey(n))
			return new HashSet<String>();
		else
			return count2Deps.get(n);
	}

	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("句子条数：" + countSentences + "\n");
		buf.append("字条数：" + numCharTokens() + "\n");
		buf.append("字形数：" + numCharTypes() + "\n");
		buf.append("词条数：" + numWordTokens() + "\n");
		buf.append("词形数：" + numWordTypes() + "\n");
		buf.append("词性种类数：" + numPosTypes() + "\n");
		buf.append("依存关系种类数：" + numDepTypes() + "\n");
		return buf.toString();
	}

}
