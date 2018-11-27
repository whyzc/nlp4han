package com.lc.nlp4han.constituent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.lc.nlp4han.constituent.PruningRules;

/**
 * 从短语结构树中提取基本组块工具
 * 
 * @author 杨智超
 *
 */
public class Bracket2BasicChunkTool
{
	/**
	 * 读取文本内容，并抽取基本组块信息
	 */
	private static List<String> process(String path, String encoding)
	{
		FileInputStream fis = null;
		BufferedReader reader = null;

		List<String> chunks = new ArrayList<String>();
		try
		{
			fis = new FileInputStream(new File(path));

			reader = new BufferedReader(new InputStreamReader(fis, encoding));

			String tempString = null;

			while ((tempString = reader.readLine()) != null)
			{
				String str = processSample(tempString);
				chunks.add(str);
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
		return chunks;
	}

	/**
	 * 处理一条括号表达式，提取组块信息
	 */
	private static String processSample(String bracketExpression)
	{
		TreeNode root = BracketExpUtil.generateTree(bracketExpression);
		PruningRules.run(root);
		return getChunks(root);
	}

	/**
	 * 保存提取出来的组块信息
	 */
	private static void saveFile(String filePath, String encoding, List<String> contents) throws IOException
	{
		BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding));
		for (String oneLine : contents)
		{
			bf.write(oneLine);
			bf.write("\n");
		}
		bf.flush();
		bf.close();
	}

	/**
	 * 对剪枝后的结构树提取组块信息
	 */
	private static String getChunks(TreeNode root)
	{
		String[] CHUNKS = { "ADJP", "ADVP", "CP", "DNP", "DP", "DVP", "FRAG", "LCP", "LST", "NP", "PP", "PRN", "QP",
				"VP", "CLP", "UCP" };
		StringBuilder result = new StringBuilder();
		List<TreeNode> leaves = TreeNodeUtil.getAllLeafNodes(root);
		for (int i = 0; i < leaves.size(); i++)
		{
			TreeNode leaf = leaves.get(i);
			if (leaf.getParent().getNodeName().equals("PU")
					&& TreeNodeUtil.getFirstNodeUpWithSpecifiedName(leaf, "FRAG") == null)
			{
				result.append(leaf.getNodeName() + "/" + "PU ");
			}
			else
			{
				TreeNode c = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(leaf, CHUNKS);
				if (c != null)
				{
					List<TreeNode> ls = TreeNodeUtil.getAllLeafNodes(c);
					int start = ls.indexOf(leaf);
					int n = chunkNum(c, start, CHUNKS);
					if (n == 1 && leaf.getParent().getNodeName().equals("PU"))
					{
						result.append(leaf.getNodeName() + "/" + "PU ");
					}
					else
					{
						result.append("[");
						for (int j = 0; j < n; j++)
						{
							if (j != 0)
								result.append(" ");
							result.append(ls.get(start + j).getNodeName() + "/"
									+ ls.get(start + j).getParent().getNodeName());
						}
						result.append("]" + c.getNodeName() + " ");
						i = i + n - 1;
					}
				}
				else
				{
					result.append(leaf.getNodeName() + "/" + leaf.getParent().getNodeName() + " ");
				}
			}
		}
		return result.toString();
	}

	private static int chunkNum(TreeNode node, int index, String[] chunks)
	{
		int num = 1;
		List<TreeNode> leaves = TreeNodeUtil.getAllLeafNodes(node);
		for (int i = 1; i < leaves.size() - index; i++)
		{
			if (TreeNodeUtil.getNodesWithSpecifiedNameBetween2Nodes(leaves.get(index + i), node, chunks).isEmpty())
			{
				num++;
			}
			else
			{
				break;
			}
		}
		while (num > 1)
		{// 最后一个为标点，删除
			if (leaves.get(index + num - 1).getParent().getNodeName().equals("PU")
					&& TreeNodeUtil.getFirstNodeUpWithSpecifiedName(leaves.get(index + num - 1), "FRAG") == null)
				num--;
			else
				break;
		}
		return num;
	}

	public static void main(String[] args) throws IOException
	{
		String[] arg = parseArgs(args);
		if (arg != null)
		{
			List<String> contents = process(arg[0], arg[1]);
			saveFile(arg[2], arg[1], contents);
		}
		else
		{

			Scanner scan = new Scanner(System.in);
			while (true)
			{
				System.out.print("请输入括号表达式：");
				String bracket = scan.nextLine();
				if (bracket.equals("exit"))
					break;
				else
				{
					System.out.print("组块结果：");
					System.out.println(processSample(bracket));
					System.out.println();
				}
			}
			scan.close();
		}
	}

	/**
	 * 解析命令行
	 * 
	 * @param args
	 * @return
	 */
	public static String[] parseArgs(String[] args)
	{
		String usage = "\t1)批处理：-path DOC_PATH [-encoding ENCODING] [-save DOC_PATH]\n"
				+ "\t2)单条输入：无需命令行,控制台输入\"exit\"退出。";

		String encoding = "utf-8";

		String docPath = null;

		String savePath = null;

		String[] result = null;

		if (args.length > 0)
		{
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
				else if ("-save".equals(args[i]))
				{
					savePath = args[i + 1];
					i++;
				}
			}
			if (args.length > 0 && "-sample".equals(args[0]))
			{
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < args.length; i++)
				{
					sb.append(args[i] + " ");
				}

			}

			if (docPath == null)
			{
				System.err.println("Usage: " + usage);
				System.exit(1);
			}

			if (docPath != null)
			{
				final java.nio.file.Path docDir = java.nio.file.Paths.get(docPath);

				if (!java.nio.file.Files.isReadable(docDir))
				{
					System.out.println("Document directory '" + docDir.toAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
					System.exit(1);
				}
			}

			result = new String[3];
			result[0] = docPath;
			result[1] = encoding;
			if (savePath == null)
				result[2] = docPath + ".out";
			else
				result[2] = savePath;
		}

		return result;
	}

}
