package com.lc.nlp4han.constituent.unlex;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

/**
 * 用来读取和写出模型
 * 
 * @author 王宁
 */
public class UnlexModelIOUtil
{

	/**
	 * 加载模型文件，得到unlex模型
	 * 
	 * @param modelFile
	 *            待加载的模型文件
	 * @return unlex模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Grammar loadUnlexModel(String modelFile) throws ClassNotFoundException, IOException
	{
		Grammar unlex = new Grammar();
		if (modelFile.endsWith(".bin"))
		{
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(modelFile)));
			unlex.read(new DataInputStream(new BufferedInputStream(new FileInputStream(modelFile))));
			dis.close();
		}
		else if (modelFile.endsWith(".txt"))
			unlex.read(new PlainTextByLineStream(new FileInputStreamFactory(new File(modelFile)), "utf-8"));
		return unlex;
	}

	/**
	 * 将CFG模型写入文件 CFG和PCFG的写入方法没有区别，故不需要重载
	 * 
	 * @param lModel
	 *            待写入的cfg模型
	 * @param modelFile
	 *            写入路径
	 * @throws IOException
	 */
	public static void writeModel(Grammar unlex, String modelFile) throws IOException
	{
		/**
		 * text-文本文件 binary-二进制文件
		 */
		if (modelFile.endsWith(".txt"))
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelFile), "utf-8"));
			bw.append(unlex.toString());
			bw.close();
		}
		else if (modelFile.endsWith(".bin"))
			unlex.write(new DataOutputStream(new FileOutputStream(modelFile)));
		else
			throw new IllegalArgumentException();
	}

	public static void main(String[] args)
	{
		try
		{
			Grammar unlex = new GrammarExtractor().extractGrammarLatentAnnotation(
					"C:\\Users\\hp\\Desktop\\test\\test100.txt", "utf-8", Lexicon.DEFAULT_RAREWORD_THRESHOLD, 1, 50,
					0.5, 0.01);
			UnlexModelIOUtil.writeModel(unlex, "gWriterTest.txt");
			UnlexModelIOUtil.writeModel(unlex, "gWriterTest.bin");
			UnlexModelIOUtil.loadUnlexModel("gWriterTest.txt");
			UnlexModelIOUtil.loadUnlexModel("gWriterTest.bin");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

}
