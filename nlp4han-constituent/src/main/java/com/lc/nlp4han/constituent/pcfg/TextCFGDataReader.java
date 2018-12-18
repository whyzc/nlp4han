package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextCFGDataReader implements CFGDataReader
{
	private BufferedReader br;

	public TextCFGDataReader(File file) throws IOException {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	}

	public TextCFGDataReader(InputStream in) {
		br = new BufferedReader(new InputStreamReader(in));
	}

	public TextCFGDataReader(BufferedReader bReader) {
		this.br= bReader;
	}
	@Override
	public String readUTF() throws IOException
	{
		return br.readLine().trim();
	}
	
	@Override
	public void close() throws IOException
	{
		br.close();
	}

}
