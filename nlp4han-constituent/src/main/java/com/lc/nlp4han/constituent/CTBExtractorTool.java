package com.lc.nlp4han.constituent;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class CTBExtractorTool
{
	// 读取文件内容
	private static String getFileContent(File file, String encoding) throws IOException
	{
		System.out.println("Extracting from " + file);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

		String line = null;
		StringBuilder buf = new StringBuilder();
		while ((line = in.readLine()) != null)
		{
			line = line.trim();
			buf.append(line);
		}

		in.close();

		return buf.toString();
	}
	
	// 从文件内容中提取句子
	private static ArrayList<String> getSentences(String str)
	{
		ArrayList<String> sentences = new ArrayList<String>();

		Pattern pattern = Pattern.compile("<S[^>]*?>(.*?)</S>");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find())
		{
			int start = matcher.start(1);
			int end = matcher.end(1);

			String match = str.substring(start, end);

			sentences.add(match);
		}

		return sentences;
	}
	
	private static void usage()
	{
		System.out.println(CTBExtractorTool.class.getName()
				+ " -in <inFileOrDir> -out <outFile> [-encoing <encoding>]");
	}
	
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			usage();

			return;
		}
		
		String in = null;
		String outFile = null;
		String encoding = "UTF-8";
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
				outFile = args[i + 1];
				i++;
			}
		}
		
		File inFile = new File(in);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), encoding));
		if(inFile.isFile())
		{
			String content = getFileContent(inFile, encoding);
			
			ArrayList<String> sentences = getSentences(content);
			
			for(String s : sentences)
				out.println(s);
		}
		else
		{
			File[] files = inFile.listFiles();
			
			for(File f : files)
			{
				String content = getFileContent(f, encoding);
				
				ArrayList<String> sentences = getSentences(content);
				
				for(String s : sentences)
					out.println(s);
			}
		}
		
		out.close();
	}
}
