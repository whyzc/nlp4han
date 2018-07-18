package com.lc.nlp4han.chunk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 将CONLL2000的语料转化为CCL格式
 */
public class Conll2CCL
{

	/**
	 * 将conll2000组块标记语料转化为CCL格式
	 * 
	 * @param inputPath
	 *            CONLL2000语料文件路径
	 * @param encoding
	 *            CONLL2000文件编码
	 * @param outputPath
	 *            CCL格式文件输出路径
	 * @throws IOException
	 */
	public static void transform(String inputPath, String encoding, String outputPath)
			throws IOException
	{
		InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File(inputPath)),
				encoding);
		BufferedReader reader = new BufferedReader(inputStreamReader);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(outputPath)),
				encoding);
		BufferedWriter writer = new BufferedWriter(outputStreamWriter);

		String line = "";
		List<String> wordTags = new ArrayList<>();
		List<String> chunks = new ArrayList<>();
		while ((line = reader.readLine()) != null)
		{
			String[] wordTagAndChunk = null;

			line = line.trim();
			if (!line.equals(""))
			{// 句子内部
				wordTagAndChunk = line.split("\\s+");
				wordTags.add(wordTagAndChunk[0] + "/" + wordTagAndChunk[1]);
				chunks.add(wordTagAndChunk[2]);
			}
			else
			{// 新句子
				String sentence = "";
				List<String> wordsInChunk = new ArrayList<>();
				String chunk = "";
				if (chunks.size() != 0 && chunks.size() == wordTags.size())
				{
					for (int i = 0; i < chunks.size(); i++)
					{
						String chunkTag = chunks.get(i);
						if (chunkTag.equals("O"))
						{
							if (wordsInChunk.size() != 0)
							{
								sentence += "[";
								for (int j = 0; j < wordsInChunk.size(); j++)
								{
									sentence += wordsInChunk.get(j) + "  ";
								}
								sentence = sentence.trim() + "]" + chunk + "  ";

								wordsInChunk.clear();
							}

							sentence += wordTags.get(i) + "  ";
						}
						else if (chunkTag.split("-")[0].equals("B"))
						{
							if (wordsInChunk.size() != 0)
							{
								sentence += "[";
								for (int j = 0; j < wordsInChunk.size(); j++)
								{
									sentence += wordsInChunk.get(j) + "  ";
								}
								sentence = sentence.trim() + "]" + chunk + "  ";

								wordsInChunk.clear();
							}

							chunk = chunkTag.split("-")[1];
							wordsInChunk.add(wordTags.get(i));
						}
						else
							wordsInChunk.add(wordTags.get(i));
					}
				}

				if (wordsInChunk.size() != 0)
				{
					sentence += "[";
					for (int j = 0; j < wordsInChunk.size(); j++)
					{
						sentence += wordsInChunk.get(j) + "  ";
					}
					sentence = sentence.trim() + "]" + chunk + "  ";

					wordsInChunk.clear();
				}

				writer.write(sentence.trim());
				writer.newLine();

				wordTags.clear();
				chunks.clear();
			}
		}
		reader.close();
		writer.close();
	}

	private static void usage()
	{
		System.out.println(
				Conll2CCL.class.getName() + " <CoNLL2000CorpusFile> <CoNLL2000CorpusFileEncoding> <OutpuFile>");
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			usage();
			
			return;
		}

		transform(args[0], args[1], args[2]);

	}
}
