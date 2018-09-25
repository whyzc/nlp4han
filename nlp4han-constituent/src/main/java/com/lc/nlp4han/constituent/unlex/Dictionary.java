package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author 王宁
 * @version 创建时间：2018年9月24日 下午9:02:13 词典
 */
public class Dictionary
{
	
	
	
	//统计树库过程中得到
	ArrayList<Short> preterminal;// 词性标注对应的整数
	ArrayList<HashSet<WordCount>> wordcount;// 长度与preterminal相同
	
	ArrayList<HashMap<BinaryRule,Integer>> bRuleBySameHead;//数组下标表示nonterminal对应的整数
	ArrayList<HashMap<UnaryRule,Integer>> uRuleBySameHead;//数组下标表示nonterminal对应的整数

	NonterminalTable nonterminalTable; 
	// HashSet<String> words;
	// HashSet<PreterminalRule>[] preRule;//长度与preterminal相同
	public Dictionary()
	{
		preterminal = new ArrayList<Short>();
		wordcount = new ArrayList<HashSet<WordCount>>();
	}
	
	public class WordCount
	{
		protected String word;
		protected int count;

		public WordCount(String word)
		{
			this.word = word;
		}

		public int hashCode()
		{
			return (word == null) ? 0 : word.hashCode();
			
		}

		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WordCount other = (WordCount) obj;
			if (word == null)
			{
				if (other.word != null)
					return false;
			}
			else if (!word.equals(other.word))
				return false;
			return true;
		}
	}
	
}
