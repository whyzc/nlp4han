package com.lc.nlp4han.dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析Stanford样式的依存分析语料
 * 
 * @author 王馨苇
 *
 */
public class DependencySampleParserStanford implements DependencySampleParser
{

	@Override
	public DependencySample parse(String sentence)
	{
		String wordLine[] = sentence.split("\\n");
		// 词
		List<String> words = new ArrayList<String>();
		// 依赖关系
		List<String> dependencies = new ArrayList<String>();
		// 依赖词语的下标
		List<String> dependencyIndices = new ArrayList<String>();
		// 依赖的词语
		List<String> dependencyWords = new ArrayList<String>();

//		words.add("核心");
		words.add(DependencyParser.RootWord);
		for (int i = 0; i < wordLine.length; i++)
		{
			words.add("");
			dependencies.add("");
			dependencyIndices.add("");
			dependencyWords.add("");
		}

		for (int i = 0; i < wordLine.length; i++)
		{
			String[] temp = wordLine[i].split("\\(");

			String[] str = temp[1].split("\\,");

			String wordindex = str[1].split("-")[1].substring(0, str[1].split("-")[1].length() - 1);
			int index = Integer.parseInt(wordindex);
			System.out.println(str[1].split("-")[0]);
			words.set(index, str[1].split("-")[0]);
			dependencies.set(index - 1, temp[0]);
			dependencyWords.set(index - 1, str[0].split("-")[0]);
			dependencyIndices.set(index - 1, str[0].split("-")[1]);
		}

		return new DependencySample(words.toArray(new String[words.size()]), null,
				dependencies.toArray(new String[dependencies.size()]),
				dependencyWords.toArray(new String[dependencyWords.size()]),
				dependencyIndices.toArray(new String[dependencyIndices.size()]));
	}
}
