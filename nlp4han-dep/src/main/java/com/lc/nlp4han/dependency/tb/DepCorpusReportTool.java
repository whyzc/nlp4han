package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.lc.nlp4han.dependency.DepCorpusReport;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * @author 王宁
 * @version 创建时间：2018年8月7日 下午7:31:23 统计中文依存分析语料基本情况应用
 */
public class DepCorpusReportTool
{

	private static void usage()
	{
		System.out.println(DepCorpusReport.class.getName() + " -data <corpusFile> [-encoding encoding]");
	}

	public static DepCorpusReport report(String source, String encoding) throws IOException
	{
		System.out.println("语料统计数据 for " + source);

		ObjectStream<String> lineStream = new PlainTextBySpaceLineStream(
				new MarkableFileInputStreamFactory(new File(source)), encoding);
		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(lineStream, sampleParser);

		int countWordTokens = 0;
		int countCharTokens = 0;
		HashMap<String, Integer> word2Count = new HashMap<String, Integer>();
		HashMap<Character, Integer> char2Count = new HashMap<Character, Integer>();
		HashMap<String, Integer> pos2Count = new HashMap<String, Integer>();
		HashMap<String, Integer> dep2Count = new HashMap<String, Integer>();

		DependencySample sentence = null;
		int countSentences = 0;
		while ((sentence = sampleStream.read()) != null)
		{
			countSentences++;
			String[] words = sentence.getWords();
			String[] poses = sentence.getPos();
			String[] deps = sentence.getDependency();
			countWordTokens += words.length;

			for (String word : words)
			{
				countCharTokens += word.length();

				int count = 0;
				if (word2Count.containsKey(word))
				{
					count = word2Count.get(word);
				}

				count++;
				word2Count.put(word, count);

				for (int i = 0; i < word.length(); i++)
				{
					char c = word.charAt(i);

					count = 0;
					if (char2Count.containsKey(c))
					{
						count = char2Count.get(c);
					}

					count++;
					char2Count.put(c, count);
				}
			}

			for (String pos : poses)
			{
				int count = 0;
				if (pos2Count.containsKey(pos))
				{
					count = pos2Count.get(pos);
				}

				count++;
				pos2Count.put(pos, count);
			}

			for (String dep : deps)
			{
				int count = 0;
				if (dep2Count.containsKey(dep))
				{
					count = dep2Count.get(dep);
				}

				count++;
				dep2Count.put(dep, count);
			}
		}

		sampleStream.close();

		return new DepCorpusReport(word2Count, pos2Count, char2Count, dep2Count, countWordTokens, countCharTokens,
				countSentences);
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			usage();
			return;
		}

		String corpusFile = null;
		String encoding = "utf-8";
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}

		DepCorpusReport report = report(corpusFile, encoding);

		System.out.println(report);
	}
}
