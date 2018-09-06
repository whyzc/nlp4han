package com.lc.nlp4han.dependency.tb;

import java.util.LinkedList;

import com.lc.nlp4han.dependency.DependencySample;

public class Vertice
{
	private String word;
	private String pos;
	private int indexOfWord;// 单词在句子中的位置，第一个位置为0。该句子包括人为添加的“核心”

	public Vertice(String word, String pos, int indexOfWord)
	{
		this.word = word;
		this.pos = pos;
		this.indexOfWord = indexOfWord;
	}

	public static LinkedList<Vertice> getWordsBuffer(DependencySample sample)
	{

		String[] words = sample.getWords();
		String[] pos = sample.getPos();

		return getWordsBuffer(words, pos);
	}

	public static LinkedList<Vertice> getWordsBuffer(String[] words, String[] pos)
	{

		LinkedList<Vertice> wordsBuffer = new LinkedList<Vertice>();
		for (int i = 0; i < words.length; i++) 
			wordsBuffer.add(new Vertice(words[i], pos[i], i));
		return wordsBuffer;
	}

	public String getWord()
	{
		return word;
	}

	public String getPos()
	{
		return pos;
	}

	public int getIndexOfWord()
	{
		return indexOfWord;
	}

	@Override
	public String toString()
	{
		return word + "/" + indexOfWord;
	}

}
