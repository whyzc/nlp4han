package org.nlp4han.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class BracketUtil
{
	/**
	 * 从字符串中读取一个或多个括号表达式
	 * 
	 * 一行最多只能有一个完整的括号表达式
	 * 
	 * @param str
	 * @return 括号表达式列表
	 * @throws IOException
	 */
	public static ArrayList<String> readBrackets(String str) throws IOException
	{
		BufferedReader in = new BufferedReader(new StringReader(str));
		ArrayList<String> brackets = new ArrayList<String>();
		String line = "";
		String bracketStr = "";
		int left = 0;
		int right = 0;
		while ((line = in.readLine()) != null)
		{
			if (line != "" && !line.equals(""))
			{
				line = line.replaceAll("\n", "");
				char[] chars = line.trim().toCharArray();
				
				bracketStr += line.trim();
				
				for (int i = 0; i < chars.length; i++)
				{
					if (chars[i] == '(')
					{
						left++;
					}
					else if (chars[i] == ')')
					{
						right++;
					}
				}

				if (left == right && left>0)
				{
					brackets.add(bracketStr);
					
					bracketStr = "";
					left = right = 0;
				}
			}
		}

		return brackets;
	}
	
	/**
	 * 将括号表达式去掉空格转成列表的形式
	 * 
	 * 列表中含: 括号、空格和终结符、非终结符
	 * 
	 * @param bracketStr
	 *            括号表达式
	 * @return
	 */
	public static List<String> stringToList(String bracketStr)
	{
		List<String> parts = new ArrayList<String>();
		for (int index = 0; index < bracketStr.length(); ++index)
		{
			char c = bracketStr.charAt(index);
			if (c == '(' || c == ')' || c == ' ')
			{
				parts.add(Character.toString(c));
			}
			else
			{
				for (int i = index + 1; i < bracketStr.length(); ++i)
				{
					char c2 = bracketStr.charAt(i);
					if (c2 == '(' || c2 == ')' || c2 == ' ')
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
	
	/**
	 * 格式化为形如：(A(B1(C1 d1)(C2 d2))(B2 d3)) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连。
	 * 
	 * 包括最外围的括号
	 * 
	 * @param bracketStr
	 *            从训练语料拼接出的一棵树
	 */
	public static String format(String bracketStr)
	{
		bracketStr = bracketStr.trim();
		
		// 所有空白符替换成一位空格
		bracketStr = bracketStr.replaceAll("\\s+", " ");

		// 去掉 ( 和 ) 前的空格
		String newTree = "";
		for (int c = 0; c < bracketStr.length(); ++c)
		{
			if (bracketStr.charAt(c) == ' ' && (bracketStr.charAt(c + 1) == '(' || bracketStr.charAt(c + 1) == ')'))
			{
				// 跳过空格
				continue;
			}
			else
			{
				newTree = newTree + (bracketStr.charAt(c));
			}
		}

		return newTree;
	}
	
	/*
	 * 若节点的名称为"("或者")",则将其输出为"-LRB"或者"-RRB-"
	 */
	public static String BracketConvert(String nodeName)
	{
		String treestr = "";
		if (nodeName == "(")
		{
			treestr = "-LRB-";
		}
		else if (nodeName == ")")
		{
			treestr = "-RRB-";
		}
		else
		{
			treestr = nodeName;
		}
		return treestr;
	}
}
