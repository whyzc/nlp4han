package com.lc.nlp4han.constituent;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BracketTest
{
	private TreeNode treeNode;
	private String string;
	private String testString;
	private String testStringWithIndex;
	private String testPosAndWord;
	private String StringInTree = "";
	private String testTree;
	private String testHeadTreeNode;

	@Before
	public void beforeTest()
	{
		string = "(ROOT(IP(NP(NR 我)(PU -LRB-)(NN 李斯)(PU -RRB-))(VP(Verb 是)(NP(PU -LRB-)(NN 大秦)(PU -RRB-)(NR 丞相)))(PU 。)))";
		testString = "(IP(NP(NR 我)(PU -LRB-)(NN 李斯)(PU -RRB-))(VP(Verb 是)(NP(PU -LRB-)(NN 大秦)(PU -RRB-)(NR 丞相)))(PU 。))";
		testStringWithIndex = "(IP(NP(NR 我[0])(PU -LRB-[1])(NN 李斯[2])(PU -RRB-[3]))(VP(Verb 是[4])(NP(PU -LRB-[5])(NN 大秦[6])(PU -RRB-[7])(NR 丞相[8])))(PU 。[9]))";
		testPosAndWord = "NR->我 PU->( NN->李斯 PU->) Verb->是 PU->( NN->大秦 PU->) NR->丞相 PU->。";
		testTree = " IP NP NR 我 PU ( NN 李斯 PU ) VP Verb 是 NP PU ( NN 大秦 PU ) NR 丞相 PU 。";
		testHeadTreeNode = "(IP{李斯[NN]}(NP{李斯[NN]}(NR{我[NR]} 我[0])(PU{-LRB-[PU]} -LRB-[1])(NN{李斯[NN]} 李斯[2])(PU{-RRB-[PU]} -RRB-[3]))(VP{大秦[NN]}(Verb{是[Verb]} 是[4])(NP{大秦[NN]}(PU{-LRB-[PU]} -LRB-[5])(NN{大秦[NN]} 大秦[6])(PU{-RRB-[PU]} -RRB-[7])(NR{丞相[NR]} 丞相[8])))(PU{。[PU]} 。[9]))";
		treeNode = BracketExpUtil.generateTree(string);
		TraverseTree(treeNode);
	}

	@SuppressWarnings("static-access")
	@Test
	public void BracketConvertTest() throws IOException
	{
		// System.out.println(headTreeNode.toString());
		/*
		 * 测试修改一，在括号表达式转化为树节点的形式时，将-LRB-，-RRB-转换为"(",")" 测试在树形式时左右括号是否为"("")"的形式
		 */
		Assert.assertTrue(testTree.equals(StringInTree));
		// 测试修改二，利用TreeNode的toString将树转换为一行括号表达式时，将"(",")"转换为-LRB-，-RRB-
		String toString = treeNode.toString();
		assertEquals(toString, testString);
		// 测试修改三，利用利用TreeNode的toStringWordIndex()将树转换为一行带位置信息的括号表达式，将"(",")"转换为-LRB-，-RRB-
		/*
		 * 括号转换成功，但并没有得到位置信息
		 */
		String toStringWordIndex = treeNode.toStringWordIndex();
		assertEquals(testString, toStringWordIndex);

		// 测试修改四，利用利用TreeNode的toStringNoNone()输出没有换行没有空节点的的括号表达式形式，将"(",")"转换为-LRB-，-RRB-
		String toStringNoNone = treeNode.toStringNoNone();
		assertEquals(toStringNoNone, testString);
		// 测试修改五，利用利用TreeNode的toStringWordIndexNoNone()打印没有换行的一整行括号表达式【去掉删除的节点】，将"(",")"转换为-LRB-，-RRB-
		/*
		 * 括号转换成功，并得到位置信息
		 */
		String toStringWordIndexNoNone = treeNode.toStringWordIndexNoNone();
		assertEquals(toStringWordIndexNoNone, testStringWithIndex);

		// 测试六，从树库中提取词和词性标注
		assertEquals(Bracket2POSTool.extractWordAndPos(toString, "->").trim(), testPosAndWord);

		/*
		 * 测试七,printTree，输出树时，它的空格数目不好计算，而且只要看它的括号表达式中的左右括号是否转换，所以，我直接打印出来观看,其结果如下 (IP
		 * (NP (NR 我) (PU -LRB-) (NN 李斯) (PU -RRB-)) (VP (Verb 是) (NP (PU -LRB-) (NN 大秦)
		 * (PU -RRB-) (NR 丞相))) (PU 。))
		 */
		System.out.println(treeNode.printTree(treeNode, 1));
	}

	/*
	 * 测试带头结点的树
	 */
	@Test
	public void TraverseHeadTreeTest()
	{
		AbstractHeadGenerator headGen = new HeadGeneratorCollins();
		TreeNode tree1 = BracketExpUtil.generateTree(
				"(ROOT(IP(NP(NR 我)(PU -LRB-)(NN 李斯)(PU -RRB-))(VP(Verb 是)(NP(PU -LRB-)(NN 大秦)(PU -RRB-)(NR 丞相)))(PU 。)))");
		HeadTreeNode headTree1 = TreeToHeadTree.treeToHeadTree(tree1, headGen);
		// 带头节点的树的输出
		assertEquals(testHeadTreeNode, headTree1.toString());
		// 带头节点的树输出不带头节点的括号表达式
		assertEquals(testStringWithIndex, headTree1.toStringWordIndex());
	}

	/*
	 * 遍历树，获取树形态时的节点的值
	 */
	private void TraverseTree(TreeNode node)
	{
		StringInTree += " " + node.getNodeName();
		for (TreeNode node1 : node.getChildren())
		{
			TraverseTree(node1);
		}
	}
	
	@Test
	public void formatTest()
	{
		String bracketStr = "(A (B1(C1 d1)(C2 d2)) (D3 d3))  ";
		String expectStr1 = "A(B1(C1 d1)(C2 d2))(D3 d3)";
		String expectStr2 = "(A(B1(C1 d1)(C2 d2))(D3 d3))";
		
		String formatStr1 = BracketExpUtil.format(bracketStr);	
		assertEquals(expectStr1, formatStr1);
		
		String formatStr2 = BracketExpUtil.formatNotDeleteBracket(bracketStr);	
		assertEquals(expectStr2, formatStr2);
	}
	
	@Test
	public void generateTest()
	{
		String bracketStr = "(A (B1(C1 d1)(C2 d2)) (D3 d3))  ";
		String expectStr1 = "(A(B1(C1 d1)(C2 d2))(D3 d3))";
		
		TreeNode tree1 = BracketExpUtil.generateTreeNotDeleteBracket(bracketStr);
		String s1 = tree1.toString();
		String formatStr1 = BracketExpUtil.formatNotDeleteBracket(s1);	
		assertEquals(expectStr1, formatStr1);
		
		String bracketStr2 = "(A (B1(C1 d1)  (C2 d2)) ) ";
		String expectStr2 = "B1(C1 d1)(C2 d2)";
		
		TreeNode tree2 = BracketExpUtil.generateTree(bracketStr2);
		String s2 = tree2.toString();
		String formatStr2 = BracketExpUtil.format(s2);	
		assertEquals(expectStr2, formatStr2);
	}
	
	@Test
	public void readBrackesTest() throws IOException
	{
		String bracketStr1 = "(A (B1(C1 d1)(C2 d2)) \r\n (D3 d3)) ";
		String bracketStr2 = "(A \n (B1(C1 d1)(C2 d2)) (D3 d3)) \n (A (B1 b1)) ";
		String bstr2 = "(A(B1 b1))";
		
		ArrayList<String> brackets = BracketExpUtil.readBrackets(bracketStr1);		
		assertEquals(1, brackets.size());
		
		brackets = BracketExpUtil.readBrackets(bracketStr2);	
		assertEquals(2, brackets.size());
		
		String bstr = brackets.get(1);
		String fstr = BracketExpUtil.formatNotDeleteBracket(bstr);	
		assertEquals(bstr2, fstr);
	}
}
