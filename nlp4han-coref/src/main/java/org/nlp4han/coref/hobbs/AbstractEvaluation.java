package org.nlp4han.coref.hobbs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEvaluation
{
	private List<String> information1 = new ArrayList<String>();
	private List<String> expection = new ArrayList<String>();
	private List<String> information3 = new ArrayList<String>();
	public void parse(String[] args)
	{
		
		String[] path_encoding = parseArgs(args);
		readFileByLines(path_encoding[0], path_encoding[1]);
	}

	public String[] parseArgs(String[] args)
	{
		String usage = "[-path DOC_PATH] [-encoding ENCODING]\n\n";

		String encoding = "utf-8";

		String docPath = null;

		for (int i = 0; i < args.length; i++)
		{
			if ("-encoding".equals(args[i]))
			{
				encoding = args[i + 1];
				i++;
			}
			else if ("-path".equals(args[i]))
			{
				docPath = args[i + 1];
				i++;
			}
		}

		if (docPath == null)
		{
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final java.nio.file.Path docDir = java.nio.file.Paths.get(docPath);

		if (!java.nio.file.Files.isReadable(docDir))
		{
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		String[] result = new String[2];
		result[0] = docPath;
		result[1] = encoding;
		return result;
	}

	public void readFileByLines(String path, String encoding)
	{
		FileInputStream fis = null;
		BufferedReader reader = null;

		try
		{
			fis = new FileInputStream(new File(path));

			reader = new BufferedReader(new InputStreamReader(fis, encoding));

			String tempString = null;

			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null)
			{
				processingData(tempString);
			}

			reader.close();

		}
		catch (IOException e)
		{

			e.printStackTrace();

		}
		finally
		{

			if (reader != null)
			{

				try
				{
					reader.close();
				}
				catch (IOException e1)
				{

				}

			}

		}
	}

	private void processingData(String oneLine)
	{
		
		boolean tag = false;
		tag = extractAnEvaluationSample(oneLine, information1, expection, information3);
		if (tag == true)
		{
			processAnEvaluationSample(information1, expection, information3);
			information1.clear();
			expection.clear();
			information3.clear();
			tag = false;
		}
	}

	public abstract void processAnEvaluationSample(List<String> information1, List<String> information2,
			List<String> information3);

	public abstract boolean extractAnEvaluationSample(String oneLine, List<String> information1,
			List<String> information2, List<String> information3);

}
