package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.constituent.pcfg.CFG;

public abstract class AbstractCFGModelReader
{
	private CFGDataReader dataReader;

	public AbstractCFGModelReader(File file) throws FileNotFoundException
	{
		String fileName = file.getName();
		InputStream input = new FileInputStream(file);

		// 读取不同格式的文件
		if (fileName.endsWith(".bin"))// 二进制文件
			this.dataReader = new BinaryCFGDataReader(input);
		else // 文本文件
			this.dataReader = new TextCFGDataReader(input);
	}

	public AbstractCFGModelReader(CFGDataReader dataReader)
	{
		this.dataReader = dataReader;
	}

	/**
	 * 返回读取的n元模型
	 * 
	 * @return 读取的n元模型
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public CFG getModel() throws IOException, ClassNotFoundException
	{
		return constructModel();
	}

	/**
	 * 重构n元模型
	 * 
	 * @return 读取n元模型
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract CFG constructModel() throws IOException, ClassNotFoundException;

	public String readUTF() throws IOException
	{
		return dataReader.readUTF();
	}

	public void close() throws IOException
	{
		dataReader.close();
	}
}
