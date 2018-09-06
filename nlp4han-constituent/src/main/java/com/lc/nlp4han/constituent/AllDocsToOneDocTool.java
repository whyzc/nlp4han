package com.lc.nlp4han.constituent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 将输入路径下的所有文件转成一个文件的运行类
 *
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class AllDocsToOneDocTool
{

	/**
	 * 将输入路径下的所有文件合并成一个文件
	 * 
	 * @param frompath
	 *            输入文件目录名
	 * @param out
	 *            输出文件的文件名
	 * @throws IOException
	 */
	public static void allDocsToOneDocRun(String frompath, String out, String encoding) throws IOException
	{
		File directory = new File(frompath);
		File[] subFiles = directory.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File file)
			{
				// 过滤掉readme文件
				return !file.getName().equals("README");
			}
		});

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), encoding));
		BufferedReader br = null;

		for (File file : subFiles)
		{
			System.out.println("Merging " + file);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			String line = "";
			while ((line = br.readLine()) != null)
			{
				if (!line.equals(""))
				{
					bw.write(line);
					bw.newLine();
				}
			}
			
			br.close();
		}
		
		System.out.println("success");
		bw.close();
		
	}

	public static void main(String[] args) throws IOException
	{
		String encoding = "GBK";
		String inDir = null;
		String out = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-dir"))
			{
				inDir = args[i + 1];
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
		}

		AllDocsToOneDocTool.allDocsToOneDocRun(inDir, out, encoding);

	}
}
