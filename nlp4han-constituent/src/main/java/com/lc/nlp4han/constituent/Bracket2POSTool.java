package com.lc.nlp4han.constituent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

/**
 * 将短语结构树转换成词性标注语料
 * 
 * @author 刘小峰
 *
 */
public class Bracket2POSTool
{

	public static void convert(String in, String out, String encoding, String sep) throws IOException
	{

		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(in)),
				encoding);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), encoding));
		String tree = "";
		while ((tree = lineStream.read()) != "")
		{
			String result = BracketExpUtil.extractWordAndPos(tree, sep);
			bw.write(result);
			bw.newLine();
		}
		
		bw.close();
		lineStream.close();
	}

	public static void main(String[] args) throws IOException
	{
		String encoding = "GBK";
		String sep = "/";
		String in = null;
		String out = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-in"))
			{
				in = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-out"))
			{
				out = args[i + 1];
				i++;
			}
			else if (args[i].equals("-sep"))
			{
				out = args[i + 1];
				i++;
			}
		}

		Bracket2POSTool.convert(in, out, encoding, sep);
	}
}
