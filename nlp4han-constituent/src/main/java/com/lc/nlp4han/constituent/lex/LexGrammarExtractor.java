package com.lc.nlp4han.constituent.lex;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

public class LexGrammarExtractor
{
	private LexPCFG lexpcfg;
	private HashSet<String> posSet;
	// 词性标注和词，以及数目
	private HashMap<WordAndPOS, Integer> wordMap = null;
	// P(H|P,t,w)）相关的统计数据
	private HashMap<RuleHeadChildGenerate, AmountAndSort> headGenMap = null;
	private HashMap<RuleHeadChildGenerate, HashSet<String>> parentList = null;
	// 用于生成中心词左右修饰符相关统计数据
	private HashMap<RuleSidesGenerate, AmountAndSort> sidesGeneratorMap = null;
	// 用于生成Stop的相关统计数据
	private HashMap<RuleStopGenerator, AmountAndSort> stopGenMap = null;
	// 用于生成并列结构连词（如CC或者逗号和冒号,为简略，我将生成修饰符pos和生成修饰符word都放入此规则
	private HashMap<RuleSpecialCase, AmountAndSort> specialGenMap = null;

	public LexGrammarExtractor()
	{
		lexpcfg = new LexPCFG();
		posSet = lexpcfg.getPosSet();
		wordMap = lexpcfg.getWordMap();
		headGenMap = lexpcfg.getHeadGenMap();
		parentList = lexpcfg.getParentList();
		sidesGeneratorMap=lexpcfg.getSidesGeneratorMap();
		stopGenMap = lexpcfg.getStopGenMap();
		specialGenMap = lexpcfg.getSpecialGenMap();
	}
  
	public void extractGrammar(String fileName, String enCoding) throws IOException
	{
		// 括号表达式树拼接成括号表达式String数组
		PlainTextByTreeStream ptbt = new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileName)),
				enCoding);
		AbstractHeadGenerator headGen = new HeadGeneratorCollins(new HeadRuleSetCTB());
		String bracketStr = ptbt.read();
		while (bracketStr.length() != 0)
		{
			TreeNode rootNode = BracketExpUtil.generateTreeNotDeleteBracket(bracketStr);
			HeadTreeNode headNode = TreeToHeadTree.treeToHeadTree(rootNode, headGen);
			if (lexpcfg.getStartSymbol() == null)
			{// 起始符提取
				lexpcfg.setStartSymbol(headNode.getNodeName());
			}
			traverseTree(headNode);
		}
		ptbt.close();
	}

	private void traverseTree(HeadTreeNode node)
	{
		if (node.getChildrenNum() == 1 && node.getChild(0).getChildrenNum() == 0)
		{
			posSet.add(node.getNodeName());
			WordAndPOS wap = new WordAndPOS(node.getNodeName(), node.getFirstChildName());
			WordAndPOS pos = new WordAndPOS(node.getNodeName(), null);
			if (!wordMap.containsKey(pos))
			{
				wordMap.put(pos, 1);
				wordMap.put(wap, 1);
			}
			else
			{
				if (!wordMap.containsKey(wap))
				{
					wordMap.put(wap, 1);
				}
				else
				{
					wordMap.put(wap, wordMap.get(wap) + 1);
				}
				wordMap.put(pos, wordMap.get(pos) + 1);
			}
			// 处理pos节点后，其孩子节点不用处理
			return;
		}
		else
		{
			extractRule(node);
		}
		for (HeadTreeNode node1 : node.getChildren())
		{
			traverseTree(node1);
		}
	}

	/**
	 * 提取规则
	 * 
	 * @param node
	 */
	private void extractRule(HeadTreeNode node)
	{
		// 统计生成中心child的数据
		GetHeadGR(node);
		// 统计生成中心Stop的数据
		GetStopGR(node);
	}

	/**
	 * 收集生成中心child的统计数据
	 * 
	 * @param node
	 */
	private void GetHeadGR(HeadTreeNode node)
	{
		String headLabel = node.getChild(getHeadChild(node)).getNodeName();
		String parentLabel = node.getNodeName();
		String headword = node.getHeadWord();
		String headpos = node.getHeadPos();
		// 回退模型1
		RuleHeadChildGenerate hcgr0 = new RuleHeadChildGenerate(headLabel, parentLabel, headpos, headword);
		RuleHeadChildGenerate hcgr1 = new RuleHeadChildGenerate(null, parentLabel, headpos, headword);
		addHeadChildGenerateRule(hcgr0, hcgr1);
		// 回退模型二
		RuleHeadChildGenerate hcgr2 = new RuleHeadChildGenerate(headLabel, parentLabel, headpos, null);
		RuleHeadChildGenerate hcgr3 = new RuleHeadChildGenerate(null, parentLabel, headpos, null);
		addHeadChildGenerateRule(hcgr2, hcgr3);
		// 回退模型三
		RuleHeadChildGenerate hcgr4 = new RuleHeadChildGenerate(headLabel, parentLabel, null, null);
		RuleHeadChildGenerate hcgr5 = new RuleHeadChildGenerate(null, parentLabel, null, null);
		addHeadChildGenerateRule(hcgr4, hcgr5);

		// 由中心孩子生成父节点标记
		parentList.get(new RuleHeadChildGenerate(headLabel, null, headpos, headword)).add(parentLabel);
	}

	/**
	 * 收集生成Stop的统计数据
	 * 
	 * @param node
	 */
	private void GetStopGR(HeadTreeNode node)
	{
		int headIndex = getHeadChild(node);
		String headLabel = node.getChild(headIndex).getNodeName();
		String parentLabel = node.getNodeName();
		String headword = node.getHeadWord();
		String headpos = node.getHeadPos();
		Distance leftDistance = getDistance(node, headIndex, 1);// 右侧距离
		Distance rightDistance = getDistance(node, headIndex, 1);// 左侧距离
	}

	private Distance getDistance(HeadTreeNode node, int headIndex, int direction)
	{
        boolean adj=false;
        boolean verb=false;
        int start=0;
        int end=headIndex;
        if(direction==2) {
           start=headIndex;
           end=node.getChildrenNum()-1;
        }
        for(;start<end;start++) {
        	
        }
 		return null;
	}

	/**
	 * 
	 * @param rule1
	 * @param rule2
	 */
	private void addHeadChildGenerateRule(RuleHeadChildGenerate rule1, RuleHeadChildGenerate rule2)
	{
		if (!headGenMap.containsKey(rule2))
		{
			headGenMap.put(rule2, new AmountAndSort(1, 1));
			headGenMap.put(rule1, new AmountAndSort(1, 0));
		}
		else
		{
			if (!headGenMap.containsKey(rule1))
			{
				headGenMap.put(rule1, new AmountAndSort(1, 0));
				headGenMap.get(rule2).addSort(1);
			}
			else
			{
				headGenMap.get(rule1).addAmount(1);
			}
			headGenMap.get(rule2).addAmount(1);
		}
	}

	// 查找某节点的headChild的index
	private int getHeadChild(HeadTreeNode node)
	{
		String word = node.getHeadWord();
		String pos = node.getHeadPos();
		for (int i = 0; i < node.getChildrenNum() - 1; i++)
		{
			if (word.equals(node.getChildHeadWord(i)) && pos.equals(node.getChildHeadPos(i)))
			{
				return i;
			}
		}
		return -1;
	}
	private boolean containVerb(HeadTreeNode node) {
		return false;
	}
}
