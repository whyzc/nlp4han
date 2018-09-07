package com.lc.nlp4han.chunk.svm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChunkCorpusStatTool
{
	private static int chunkNum = 0;
	private static int charaNum = 0;
	private static int wordNum = 0;
	private static Map<String, Info> chunks = new HashMap<String, Info>();
	private static Set<String> words = new HashSet<String>();
	private static Set<String> POSs = new HashSet<String>();
	
	private static double avgChunkLen = 0;

	public static void main(String[] args)
	{
		String[] path_encoding = parseArgs(args);

		process(path_encoding[0], path_encoding[1]);

		printResult();
	}

	public static void printResult()
	{
		System.out.println("字数：" + charaNum);
		System.out.println("词数：" + wordNum);
		System.out.println("词条数：" + words.size());
		System.out.println("词性数：" + POSs.size());
		System.out.println("组块类型数：" + chunks.size());
		System.out.println("组块总数：" + chunkNum);
		System.out.println("平均组块长度：" + avgChunkLen);

		System.out.println("***************组块详细数据**************");

		Set<String> set = chunks.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			Info ifr = chunks.get(key);

			System.out.println(key + "----" + "\t数量：" + ifr.number + "  " + "\t平均长度：" + ifr.averageLength);
		}
	}

	public static String[] parseArgs(String[] args)
	{
		String usage = "[-path DOC_PATH] [-encoding ENCODING]\n\n";

		String encoding = "utf-8";

		String docPath = null;

		for (int i = 0; i < args.length; i++)
		{
			if ("-encoding".equals(args[i]))
			{
				encoding = args[i + 1];
				i++;
			}
			else if ("-path".equals(args[i]))
			{
				docPath = args[i + 1];
				i++;
			}
		}

		if (docPath == null)
		{
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final java.nio.file.Path docDir = java.nio.file.Paths.get(docPath);

		if (!java.nio.file.Files.isReadable(docDir))
		{
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		String[] result = new String[2];
		result[0] = docPath;
		result[1] = encoding;
		return result;
	}

	public static void process(String path, String encoding)
	{
		FileInputStream fis = null;
		BufferedReader reader = null;

		try
		{
			fis = new FileInputStream(new File(path));

			reader = new BufferedReader(new InputStreamReader(fis, encoding));

			String tempString = null;

			while ((tempString = reader.readLine()) != null)
			{
				processSample(tempString);
			}
			
			postProcessing();

			reader.close();

		}
		catch (IOException e)
		{

			e.printStackTrace();

		}
		finally
		{

			if (reader != null)
			{

				try
				{
					reader.close();
				}
				catch (IOException e1)
				{

				}

			}

		}
	}

	private static void postProcessing()
	{
		int chunkTotalLen = 0;
		Set<String> set = chunks.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			Info ifr = chunks.get(key);
			
			chunkTotalLen += ifr.averageLength;
			
			ifr.averageLength /= ifr.number;
		}
		
		avgChunkLen = chunkTotalLen / (double)chunkNum;
	}

	private static void processSample(String sample)
	{
		String[] units = sample.split(" +");
		for (int i = 0; i < units.length; i++)
		{
			/*
			 * if (units[i].length()<1) continue;
			 */
			if (units[i].startsWith("["))
			{
				if (units[i].contains("]"))
				{
					count(units[i], 3, 1);
				}
				else
				{
					int j = i;
					count(units[i], 1, 0);
					i++;
					while (!units[i].contains("]"))
					{
						count(units[i], 0, 0);
						i++;
					}
					
					count(units[i], 2, i - j + 1);
				}
			}
			else
			{
				if (units[i].length() > 0)
					count(units[i], 0, 0);
			}

		}

	}

	private static void count(String str, int type, int chunkLen)
	{
		if (type == 0)
		{ // 形如"w/p"

			String[] wordAndPOS = str.split("/");
			if (wordAndPOS.length != 2)
			{
				System.out.println("1" + str);
			}
			
			wordNum++;
			words.add(wordAndPOS[0]);
			charaNum += wordAndPOS[0].length();
			POSs.add(wordAndPOS[1]);
		}
		else if (type == 1)
		{ // 形如"[w/p"
			count(str.substring(1), 0, 0);
		}
		else if (type == 2)
		{ // 形如"w/p]t"
			String[] strs = str.split("]");
			if (strs.length > 1)
			{
				String chunkStr = strs[1];
				String[] wordAndPOS = strs[0].split("/");
				
				wordNum++;
				words.add(wordAndPOS[0]);
				charaNum += wordAndPOS[0].length();
				POSs.add(wordAndPOS[1]);
				
				if (chunks.containsKey(chunkStr))
				{
					Info inf = chunks.get(chunkStr);
					inf.number++;
					inf.averageLength += chunkLen;
				}
				else
				{
					chunks.put(chunkStr, new Info(1, chunkLen));
				}
				
				chunkNum++;
			}
			else
			{
				count(strs[0], 0, 0);
			}
		}
		else if (type == 3)
		{ // 形如"[w/p]t"
			count(str.substring(1), 2, 1);
		}
	}

	static class Info
	{
		int number = 0;
		double averageLength = 0;

		Info()
		{

		}

		Info(int number, double averageLength)
		{
			this.number = number;
			this.averageLength = averageLength;
		}
	}
}
