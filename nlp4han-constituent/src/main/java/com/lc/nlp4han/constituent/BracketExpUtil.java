package com.lc.nlp4han.constituent;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 和括号表达式表示相关的工具方法
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class BracketExpUtil
{
	/**
	 * 从流中读取一个或多个括号表达式
	 * 
	 * @param in
	 * @return 括号表达式列表
	 * @throws IOException
	 */
	public static ArrayList<String> readBrackets(BufferedReader in) throws IOException
	{
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

	// 去掉最外围一层的括号和文法符号
	public static TreeNode generateTree(String bracketStr)
	{
		bracketStr = format(bracketStr);
		return generateProcess(bracketStr);
	}

	/**
	 * 生成树，但是不去掉最外层括号表达式
	 */
	public static TreeNode generateTreeNotDeleteBracket(String bracketStr)
	{
		bracketStr = formatNotDeleteBracket(bracketStr);
		return generateProcess(bracketStr);
	}

	/**
	 * 遍历树，将树中的-RRB-和-LRB-转换为左右括号
	 * 
	 * @param 根节点
	 * @return
	 */
	public static void TraverseTreeConvertRRBAndLRB(TreeNode node)
	{
		if (node.getChildrenNum() == 0)
		{
			if (node.getNodeName().equals("-LRB-"))
			{
				node.setNewName("(");
			}
			else if (node.getNodeName().equals("-RRB-"))
			{
				node.setNewName(")");
			}
			return;
		}
		
		for (TreeNode childNode : node.getChildren())
		{
			TraverseTreeConvertRRBAndLRB(childNode);
		}
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
	 * 格式化为形如：A(B1(C1 d1)(C2 d2))(B2 d3) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连。
	 * 
	 * 去掉最外围的一对括号。
	 * 
	 * @param bracketStr
	 *            括号表达式
	 */
	public static String format(String bracketStr)
	{
		bracketStr = bracketStr.trim();
		
		// 去除最外围的括号
		bracketStr = bracketStr.substring(1, bracketStr.length() - 1).trim();

		return formatProcess(bracketStr);
	}

	/**
	 * 格式化为形如：(A(B1(C1 d1)(C2 d2))(B2 d3)) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连.
	 * 
	 * 不去掉最外层括号表达式
	 * 
	 * @param bracketStr
	 *            括号表达式
	 * @return
	 */
	public static String formatNotDeleteBracket(String bracketStr)
	{
		bracketStr = bracketStr.trim();
		return formatProcess(bracketStr);
	}

	private static String formatProcess(String bracketStr)
	{
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

	private static TreeNode generateProcess(String bracketStr)
	{
		List<String> parts = stringToList(bracketStr);

		Stack<TreeNode> tree = new Stack<TreeNode>();
		int wordindex = 0;
		for (int i = 0; i < parts.size(); i++)
		{
			String str = parts.get(i);
			if (!str.equals(")") && !str.equals(" ")) // 左括号或文法符号
			{
				TreeNode tn = new TreeNode(str);
				tn.setFlag(true);
				tree.push(tn);
			}
			else if (str.equals(" "))
			{

			}
			else if (str.equals(")"))
			{
				Stack<TreeNode> temp = new Stack<TreeNode>();
				while (!tree.peek().getNodeName().equals("("))
				{
					if (!tree.peek().getNodeName().equals(" "))
					{
						temp.push(tree.pop());
					}
				}
				
				tree.pop();
				TreeNode node = temp.pop();
				while (!temp.isEmpty())
				{
					temp.peek().setParent(node);
					if (temp.peek().getChildren().size() == 0)
					{
						TreeNode wordindexnode = temp.pop();
						wordindexnode.setWordIndex(wordindex++);
						node.addChild(wordindexnode);
					}
					else
					{
						node.addChild(temp.pop());
					}
				}
				
				tree.push(node);
			}
		}
		
		// 将表达式中的-LRB-和-RRB-转换为"(",")"
		TreeNode treeStruct = tree.pop();
		TraverseTreeConvertRRBAndLRB(treeStruct);
		return treeStruct;
	}
}
