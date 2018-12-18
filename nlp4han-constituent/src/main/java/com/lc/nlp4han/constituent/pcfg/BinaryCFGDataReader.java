package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BinaryCFGDataReader implements CFGDataReader
{
	private DataInputStream dis;

	public BinaryCFGDataReader(File file) throws FileNotFoundException
	 {
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
	}

	public BinaryCFGDataReader(InputStream in)
	{
		dis = new DataInputStream(in);
	}

	public BinaryCFGDataReader(DataInputStream dis)
	{
		this.dis = dis;
	}

	@Override
	public String readUTF() throws IOException
	{
		return dis.readUTF();
	}

	@Override
	public void close() throws IOException
	{
		dis.close();
	}

}
