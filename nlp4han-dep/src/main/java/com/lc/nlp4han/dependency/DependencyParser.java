package com.lc.nlp4han.dependency;

/**
 * 依存句法分析器接口
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public interface DependencyParser
{
	public static final String RootWord = "RootWord";
	public static final String RootDep = "root"
			+ "";
	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param sentence
	 *            分词后并进行词性标注后的句子，形式为w1/p1 w2/p2...
	 * @return 依存树
	 */
	public DependencyTree parse(String sentence);

	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param words
	 *            分词之后的词语
	 * @param poses
	 *            词性标记
	 * 
	 * @return 依存树
	 */
	public DependencyTree parse(String[] words, String[] poses);

	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param wordsandposes
	 *            分词+词性标记的词语组成的数组，形式为w/p
	 * @return 依存树
	 */
	public DependencyTree parse(String[] wordsandposes);

	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param sentence
	 *            分词后并进行词性标注后的句子，形式为w1/p1 w2/p2...
	 * @return 依存树
	 */
	public DependencyTree[] parse(String sentence, int k);

	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param words
	 *            分词之后的词语
	 * @param poses
	 *            词性标记
	 * @return 依存分析之后的结果
	 */
	public DependencyTree[] parse(String[] words, String[] poses, int k);

	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param wordsandposes
	 *            分词+词性标记的词语组成的数组，形式为w/p
	 * @return 依存树
	 */
	public DependencyTree[] parse(String[] wordsandposes, int k);
}
