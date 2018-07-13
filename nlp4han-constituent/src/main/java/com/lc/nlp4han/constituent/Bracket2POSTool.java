package com.lc.nlp4han.constituent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
			String result = extractWordAndPos(tree, sep);
			bw.write(result);
			bw.newLine();
		}
		
		bw.close();
		lineStream.close();
	}

	public static String extractWordAndPos(String bracketStr, String sep)
	{
		bracketStr = format(bracketStr);
		
		List<String> parts = stringToList(bracketStr);
		
		String result = "";
		Stack<String> stack = new Stack<String>();
		for (int i = 0; i < parts.size(); i++)
		{
			if (!parts.get(i).equals(")") && !parts.get(i).equals(" "))
			{
				stack.push(parts.get(i));
			}
			else if (parts.get(i).equals(" "))
			{

			}
			else if (parts.get(i).equals(")"))
			{
				if (!stack.isEmpty())
				{
					String word = stack.pop();
					String pos= stack.pop();
					if(word.equals("-RRB-")) {
						word=")";
					}else if(word.equals("-LRB-")) {
						word="(";
					}
					result += pos + sep + word + " ";
				}
				stack.clear();
				;
			}
		}

		return result;
	}

	private static List<String> stringToList(String bracketStr)
	{
		List<String> parts = new ArrayList<String>();
		for (int index = 0; index < bracketStr.length(); ++index)
		{
			if (bracketStr.charAt(index) == '(' || bracketStr.charAt(index) == ')' || bracketStr.charAt(index) == ' ')
			{
				parts.add(Character.toString(bracketStr.charAt(index)));
			}
			else
			{
				for (int i = index + 1; i < bracketStr.length(); ++i)
				{
					if (bracketStr.charAt(i) == '(' || bracketStr.charAt(i) == ')' || bracketStr.charAt(i) == ' ')
					{
						parts.add(bracketStr.substring(index, i));
						index = i - 1;
						break;
					}
				}
			}
		}
		return parts;
	}

	private static String format(String bracketStr)
	{

		bracketStr = bracketStr.substring(1, bracketStr.length() - 1).trim();

		bracketStr = bracketStr.replaceAll("\\s+", " ");

		String newTree = "";
		for (int c = 0; c < bracketStr.length(); ++c)
		{
			if (bracketStr.charAt(c) == ' ' && (bracketStr.charAt(c + 1) == '(' || bracketStr.charAt(c + 1) == ')'))
			{
				continue;
			}
			else
			{
				newTree = newTree + (bracketStr.charAt(c));
			}
		}

		return newTree;
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
