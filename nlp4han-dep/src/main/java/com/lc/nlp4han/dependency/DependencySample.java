package com.lc.nlp4han.dependency;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 依存关系样本类
 * 
 * TODO: 内部表示抽象，不以物理格式表示样本，而以逻辑含义表示样本
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencySample
{

	private List<String> words;//包括认为添加的ROOT
	private List<String> pos;
	private List<String> dependency;
	private List<String> dependencyWords;
	private List<String> dependencyIndices;
	private String[][] adtionalContext;

	/**
	 * 构造
	 * 
	 * @param words
	 *            词语
	 * @param pos
	 *            词性
	 * @param dependency
	 *            依存关系
	 * @param dependencyWords
	 *            依存关系对应的词
	 * @param dependencyIndices
	 *            依存关系对应的词的下标
	 */
	public DependencySample(String[] words, String[] pos, String[] dependency, String[] dependencyWords,
			String[] dependencyIndices)
	{
		this(words, pos, dependency, dependencyWords, dependencyIndices, null);
	}

	/**
	 * 构造
	 * 
	 * @param words
	 *            词语
	 * @param pos
	 *            词性
	 * @param dependency
	 *            依存关系
	 * @param dependencyWords
	 *            依存关系对应的词
	 * @param dependencyIndices
	 *            依存关系对应的词的下标
	 */
	public DependencySample(List<String> words, List<String> pos, List<String> dependency, List<String> dependencyWords,
			List<String> dependencyIndices)
	{
		this(words, pos, dependency, dependencyWords, dependencyIndices, null);
	}

	/**
	 * 构造
	 * 
	 * @param words
	 *            词语
	 * @param pos
	 *            词性
	 * @param dependency
	 *            依存关系
	 * @param dependencyWords
	 *            依存关系对应的词
	 * @param dependencyIndices
	 *            依存关系对应的词的下标
	 * @param additionalContext
	 *            额外的信息
	 */
	public DependencySample(String[] words, String[] pos, String[] dependency, String[] dependencyWords,
			String[] dependencyIndices, String[][] additionalContext)
	{
		this(Arrays.asList(words), Arrays.asList(pos), Arrays.asList(dependency), Arrays.asList(dependencyWords),
				Arrays.asList(dependencyIndices), additionalContext);
	}

	/**
	 * 构造
	 * 
	 * @param words
	 *            词语
	 * @param pos
	 *            词性
	 * @param dependency
	 *            依存关系
	 * @param dependencyWords
	 *            依存关系对应的词
	 * @param dependencyIndices
	 *            依存关系对应的词的下标
	 * @param additionalContext
	 *            额外的信息
	 */
	public DependencySample(List<String> words, List<String> pos, List<String> dependency, List<String> dependencyWords,
			List<String> dependencyIndices, String[][] additionalContext)
	{
		// 不能被修改的list
		this.words = Collections.unmodifiableList(words);
		if (pos != null)
		{
			this.pos = Collections.unmodifiableList(pos);
		}
		this.dependency = Collections.unmodifiableList(dependency);
		this.dependencyWords = Collections.unmodifiableList(dependencyWords);
		this.dependencyIndices = Collections.unmodifiableList(dependencyIndices);

		String[][] ac;
		if (additionalContext != null)
		{
			ac = new String[additionalContext.length][];

			for (int i = 0; i < additionalContext.length; i++)
			{
				ac[i] = new String[additionalContext[i].length];
				System.arraycopy(additionalContext[i], 0, ac[i], 0, additionalContext[i].length);
			}
		}
		else
		{
			ac = null;
		}
		this.adtionalContext = ac;
	}

	/**
	 * 输出的样本的格式
	 * 
	 * @return 输出的结果
	 */
	public String toCoNLLString()
	{
		String sample = new String();

		if (pos == null)
		{
			for (int i = 0; i < dependency.size(); i++)
			{
				sample += (i + 1) + "\t" + words.get(i + 1) + "\t" + words.get(i + 1) + "\t" + "_" + "\t"
						+ dependencyIndices.get(i) + "\t" + dependency.get(i) + "\t" + "_" + "\t" + "_" + "\n";
			}
		}
		else
		{
			for (int i = 0; i < dependency.size(); i++)
			{
				sample += (i + 1) + "\t" + words.get(i + 1) + "\t" + words.get(i + 1) + "\t" + pos.get(i + 1) + "\t"
						+ pos.get(i + 1) + "\t" + "_" + "\t" + dependencyIndices.get(i) + "\t" + dependency.get(i)
						+ "\t" + "_" + "\t" + "_" + "\n";
			}
		}

		return sample;
	}

	/**
	 * 输出Stanford依存语料样式
	 * 
	 * @return
	 */
	public String toStanfordString()
	{
		String sample = new String();
		for (int i = 0; i < dependency.size(); i++)
		{
			if (dependencyIndices.get(i).equals("0"))
			{
				sample += dependency.get(i) + "(" + dependencyWords.get(i) + "-" + dependencyIndices.get(i) + ","
						+ words.get(i + 1) + "-" + (i + 1) + ")" + "\n";
			}
		}
		for (int i = 0; i < dependency.size(); i++)
		{
			if (!dependencyIndices.get(i).equals("0"))
			{
				sample += dependency.get(i) + "(" + dependencyWords.get(i) + "-" + dependencyIndices.get(i) + ","
						+ words.get(i + 1) + "-" + (i + 1) + ")" + "\n";
			}
		}
		return sample;
	}

	/**
	 * 获取词语
	 * 
	 * @return 词语数组
	 */
	public String[] getWords()
	{
		return this.words.toArray(new String[words.size()]);
	}

	/**
	 * 获取词性
	 * 
	 * @return 词性
	 */
	public String[] getPos()
	{
		return this.pos.toArray(new String[pos.size()]);
	}

	/**
	 * 获取依存关系
	 * 
	 * @return 依存关系
	 */
	public String[] getDependency()
	{
		return this.dependency.toArray(new String[dependency.size()]);
	}

	/**
	 * 获取依存关系对应的词
	 * 
	 * @return 依存关系对应的词
	 */
	public String[] getDependencyWords()
	{
		return this.dependencyWords.toArray(new String[dependencyWords.size()]);
	}

	/**
	 * 获取依存关系对应的词的下标
	 * 
	 * @return 依存关系对应的词的下标
	 */
	public String[] getDependencyIndices()
	{
		return this.dependencyIndices.toArray(new String[dependencyIndices.size()]);
	}

	/**
	 * 获取额外的信息
	 * 
	 * @return 额外的信息
	 */
	public String[][] getAditionalContext()
	{
		return this.adtionalContext;
	}

	/**
	 * 重写方法
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof DependencySample)
		{
			DependencySample sample = (DependencySample) obj;
			return Arrays.equals(this.getDependency(), sample.getDependency())
					&& Arrays.equals(this.getDependencyWords(), sample.getDependencyWords());
		}
		return true;
	}
}
