package com.lc.nlp4han.constituent.lex;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LexPCFGModelIOUtil
{
	/**
	 * 加载模型文件，得到n元模型
	 * 
	 * @param modelFile
	 *            待加载的模型文件
	 * @return CFG模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static LexPCFG loadModel(String modelFile) throws ClassNotFoundException, IOException
	{
		LexPCFG lexpcfg=new LexPCFG();
		if (modelFile.endsWith(".bin"))
			lexpcfg.read(new DataInputStream(new FileInputStream(modelFile)));
		else
			lexpcfg.readGrammar(new FileInputStream(modelFile), "utf-8");
		
		return lexpcfg;
	}
	
	/**
	 * 将CFG模型写入文件
	 * CFG和PCFG的写入方法没有区别，故不需要重载
	 * @param lModel
	 *            待写入的cfg模型
	 * @param modelFile
	 *            写入路径
	 * @throws IOException
	 */
	public static void writeModel(LexPCFG lexpcfg, String modelFile) throws IOException
	{
		/**
		 * text-文本文件 binary-二进制文件
		 */
		if (modelFile.endsWith(".txt")) {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelFile), "utf-8"));
		    bw.append(lexpcfg.toString());
		    bw.close();
		}
		else if (modelFile.endsWith(".bin"))
			lexpcfg.write(new DataOutputStream(new FileOutputStream(modelFile)));
		else
			throw new IllegalArgumentException();
	}

}
