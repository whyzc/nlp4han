package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.omg.CORBA.portable.InputStream;

public class CFGModelIOUtil
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
	public static CFG loadModel(String modelFile) throws ClassNotFoundException, IOException
	{
		AbstractCFGModelReader modelReader;
		if (modelFile.endsWith(".bin"))
			modelReader = new BinaryFileCFGModelReader(new File(modelFile));
		else
			modelReader = new TextFileCFGModelReader(new File(modelFile));

		return modelReader.constructModel();
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
	public static CFG loadBinaryModel(DataInputStream dis) throws ClassNotFoundException, IOException
	{
		AbstractCFGModelReader modelReader = new BinaryFileCFGModelReader(dis);
		return modelReader.constructModel();
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
	public static CFG loadBinaryModel(InputStream dis) throws ClassNotFoundException, IOException
	{
		AbstractCFGModelReader modelReader = new BinaryFileCFGModelReader(dis);
		return modelReader.constructModel();
	}

	/**
	 * 加载模型文件，得到CFG模型文本文件
	 * 
	 * @param dis
	 *            数据输入流
	 * @return CFG模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static CFG loadTextModel(InputStream dis) throws ClassNotFoundException, IOException
	{
		AbstractCFGModelReader modelReader = new TextFileCFGModelReader(dis);
		return modelReader.constructModel();
	}

	/**
	 * 将CFG模型写入文件
	 * 
	 * @param lModel
	 *            待写入的cfg模型
	 * @param modelFile
	 *            写入路径
	 * @throws IOException
	 */
	public static void writeModel(CFG cfg, String modelFile) throws IOException
	{
		AbstractCFGModelWriter modelWriter;

		/**
		 * text-文本文件 binary-二进制文件
		 */
		if (modelFile.endsWith(".txt"))
			modelWriter = new TextFileCFGModelWriter(cfg, new File(modelFile));
		else if (modelFile.endsWith(".bin"))
			modelWriter = new BinaryFileCFGModelWriter(cfg, new File(modelFile));
		else
			throw new IllegalArgumentException();

		modelWriter.persist();
	}

	/**
	 * 将cfg模型写入文本文件
	 * 
	 * @param lModel
	 *            待写入的cfg模型
	 * @param modelFile
	 *            写入路径
	 * @throws IOException
	 */
	public static void writeTextModel(CFG cfg, BufferedWriter bw) throws IOException
	{
		AbstractCFGModelWriter modelWriter = new TextFileCFGModelWriter(cfg, bw);
		modelWriter.persist();
	}
	
	/**
	 * 将cfg模型写入文本文件
	 * 
	 * @param lModel
	 *            待写入的cfg模型
	 * @param modelFile
	 *            写入路径
	 * @throws IOException
	 */
	public static void writeTextModel(CFG cfg, OutputStream out) throws IOException
	{
		AbstractCFGModelWriter modelWriter = new TextFileCFGModelWriter(cfg, out);
		modelWriter.persist();
	}
	
	/**
	 * 将cfg模型写入二进制文件
	 * 
	 * @param cfg
	 *            待写入的cfg模型
	 * @param dos
	 *            输入流
	 * @throws IOException
	 */
	public static void writeBinaryModel(CFG cfg, OutputStream dos) throws IOException
	{
		AbstractCFGModelWriter modelWriter = new BinaryFileCFGModelWriter(cfg, dos);
		modelWriter.persist();
	}
}
