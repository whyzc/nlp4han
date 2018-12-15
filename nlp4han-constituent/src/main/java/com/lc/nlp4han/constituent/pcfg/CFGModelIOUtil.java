package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CFGModelIOUtil
{
	/**
	 * 加载模型文件，得到cfg模型
	 * 
	 * @param modelFile
	 *            待加载的模型文件
	 * @return CFG模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static CFG loadCFGModel(String modelFile) throws ClassNotFoundException, IOException
	{
		CFG cfg = new CFG();
		if (modelFile.endsWith(".bin"))
			cfg.read(new DataInputStream(new BufferedInputStream(new FileInputStream(modelFile))));
		else
			cfg.readGrammar(new FileInputStream(modelFile), "utf-8");

		return cfg;
	}

	/**
	 * 加载模型文件，得到pcfg模型
	 * 
	 * @param modelFile
	 *            待加载的模型文件
	 * @return CFG模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static PCFG loadPCFGModel(String modelFile) throws ClassNotFoundException, IOException
	{
		PCFG pcfg = new PCFG();
		if (modelFile.endsWith(".bin"))
			pcfg.read(new DataInputStream(new BufferedInputStream(new FileInputStream(modelFile))));
		else
			pcfg.readGrammar(new FileInputStream(modelFile), "utf-8");

		return pcfg;
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
	public static void writeModel(CFG cfg, String modelFile) throws IOException
	{
		/**
		 * text-文本文件 binary-二进制文件
		 */
		if (modelFile.endsWith(".txt"))
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(modelFile), "utf-8"));
			bw.append(cfg.toString());
			bw.close();
		}
		else if (modelFile.endsWith(".bin"))
			cfg.write(new DataOutputStream(new FileOutputStream(modelFile)));
		else
			throw new IllegalArgumentException();
	}

	/**
	 * 加载模型文件，得到CFG模型二进制文件
	 * 
	 * @param dis
	 *            数据输入流
	 * @return CFG模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	/*
	 * public static CFG loadBinaryModel(DataInputStream dis) throws
	 * ClassNotFoundException, IOException { AbstractCFGModelReader modelReader =
	 * new BinaryFileCFGModelReader(dis); return modelReader.constructModel(); }
	 * 
	 *//**
		 * 加载模型文件，得到CFG模型二进制文件
		 * 
		 * @param dis
		 *            数据输入流
		 * @return CFG模型
		 * @throws ClassNotFoundException
		 * @throws IOException
		 */
	/*
	 * public static CFG loadBinaryModel(InputStream dis) throws
	 * ClassNotFoundException, IOException { AbstractCFGModelReader modelReader =
	 * new BinaryFileCFGModelReader(dis); return modelReader.constructModel(); }
	 * 
	 *//**
		 * 加载模型文件，得到CFG模型文本文件
		 * 
		 * @param dis
		 *            数据输入流
		 * @return CFG模型
		 * @throws ClassNotFoundException
		 * @throws IOException
		 *//*
			 * public static CFG loadTextModel(InputStream dis) throws
			 * ClassNotFoundException, IOException { AbstractCFGModelReader modelReader =
			 * new TextFileCFGModelReader(dis); return modelReader.constructModel(); }
			 */

	/*	*//**
			 * 将cfg模型写入文本文件
			 * 
			 * @param lModel
			 *            待写入的cfg模型
			 * @param modelFile
			 *            写入路径
			 * @throws IOException
			 */
	/*
	 * public static void writeTextModel(CFG cfg, BufferedWriter bw) throws
	 * IOException { AbstractCFGModelWriter modelWriter = new
	 * TextFileCFGModelWriter(cfg, bw); modelWriter.persist(); }
	 * 
	 *//**
		 * 将cfg模型写入文本文件
		 * 
		 * @param lModel
		 *            待写入的cfg模型
		 * @param modelFile
		 *            写入路径
		 * @throws IOException
		 */
	/*
	 * public static void writeTextModel(CFG cfg, OutputStream out) throws
	 * IOException { AbstractCFGModelWriter modelWriter = new
	 * TextFileCFGModelWriter(cfg, out); modelWriter.persist(); }
	 * 
	 *//**
		 * 将cfg模型写入二进制文件
		 * 
		 * @param cfg
		 *            待写入的cfg模型
		 * @param dos
		 *            输入流
		 * @throws IOException
		 *//*
			 * public static void writeBinaryModel(CFG cfg, OutputStream dos) throws
			 * IOException { AbstractCFGModelWriter modelWriter = new
			 * BinaryFileCFGModelWriter(cfg, dos); modelWriter.persist(); }
			 */
}
