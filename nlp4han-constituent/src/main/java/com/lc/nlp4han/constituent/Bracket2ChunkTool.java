package com.lc.nlp4han.constituent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

/**
 * 从短语结构树中提取基本组块工具
 * 
 * @author 刘小峰
 * @author 陈林
 *
 */
public class Bracket2ChunkTool
{

	public static void main(String[] args) throws IOException
	{

		if (args.length < 1)
		{
			usage();
			return;
		}

		String in = "";// 输入文件路径
		String out = "";// 输出文件路径
		String chunkTag = "all";// 要提取的标记
		String encoding = "GBK";// 编码格式,默认GBK
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-in"))
			{
				in = args[i + 1];
				i++;
			}
			if (args[i].equals("-out"))
			{
				out = args[i + 1];
				i++;
			}
			if (args[i].equals("-chunkTag"))
			{
				chunkTag = args[i + 1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}

		List<String> tags = new ArrayList<>();
		String[] s = chunkTag.split(",");
		for (int i = 0; i < s.length; i++)
		{
			tags.add(s[i]);
		}

		List<TreeNode> subTrees = run(in, tags, encoding);
		writeFile(subTrees, out);
	}

	private static void usage()
	{
		System.out.println(Bracket2ChunkTool.class.getName()
				+ " -in <inputFile> -out <outputFile> [-chunkTag <tagListSeperatedByComma>] [-encoding <encoding>]");
	}

	private static List<TreeNode> run(String fileIn, List<String> targetChunks, String encoding) throws IOException
	{
		List<TreeNode> treeList = new ArrayList<>(); // 存放标记了基本组块的树结构

		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileIn)),
				encoding);
		String bracketStr = "";
		while ((bracketStr = lineStream.read()) != null)
		{
			TreeNode tree = BracketExpUtil.generateTreeNoTopBracket(bracketStr);
			BaseChunkSearcher.search(tree, targetChunks);
			treeList.add(tree);
		}
		lineStream.close();
		return treeList;
	}

	private static void writeFile(List<TreeNode> subTrees, String fileOut) throws IOException
	{
		FileWriter fw = new FileWriter(fileOut);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < subTrees.size(); i++)
		{
			String chunkStr = BaseChunkSearcher.toChunkString(subTrees.get(i));
			bw.write(chunkStr);
			bw.newLine();
		}
		
		bw.close();
	}

}
