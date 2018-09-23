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
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

public class LexGrammarExtractor
{
	private LexPCFG lexpcfg;
	private HashSet<String> posSet;
	// 词性标注和词，以及数目
	private HashMap<WordAndPOS, Integer> wordMap = null;
	// P(H|P,t,w)）相关的统计数据
	private HashMap<RuleCollins, AmountAndSort> headGenMap = null;
	private HashMap<RuleCollins, HashSet<String>> parentList = null;
	// 用于生成中心词左右修饰符相关统计数据
	private HashMap<RuleCollins, AmountAndSort> sidesGeneratorMap = null;
	// 用于生成Stop的相关统计数据
	private HashMap<RuleCollins, AmountAndSort> stopGenMap = null;
	// 用于生成并列结构连词（如CC或者逗号和冒号,为简略，我将生成修饰符pos和生成修饰符word都放入此规则
	private HashMap<RuleCollins, AmountAndSort> specialGenMap = null;

	public LexGrammarExtractor()
	{
		lexpcfg = new LexPCFG();
		posSet = lexpcfg.getPosSet();
		wordMap = lexpcfg.getWordMap();
		headGenMap = lexpcfg.getHeadGenMap();
		parentList = lexpcfg.getParentList();
		sidesGeneratorMap = lexpcfg.getSidesGeneratorMap();
		stopGenMap = lexpcfg.getStopGenMap();
		specialGenMap = lexpcfg.getSpecialGenMap();
	}

	public LexPCFG extractGrammar(String fileName, String enCoding) throws IOException
	{
		// 括号表达式树拼接成括号表达式String数组
		PlainTextByTreeStream ptbt = new PlainTextByTreeStream(new FileInputStreamFactory(new File(fileName)),
				enCoding);
		AbstractHeadGenerator headGen = new HeadGeneratorCollins(new HeadRuleSetCTB());
		String bracketStr = ptbt.read();
		while (bracketStr.length() != 0)
		{
			TreeNode rootNode = BracketExpUtil.generateTreeNotDeleteBracket(bracketStr);
			HeadTreeNodeForCollins headNode = TreeToHeadTreeForCollins.treeToHeadTree(rootNode, headGen);
			if (lexpcfg.getStartSymbol() == null)
			{// 起始符提取
				lexpcfg.setStartSymbol(headNode.getNodeName());
			}
			traverseTree(headNode);
			bracketStr = ptbt.read();
		}
		ptbt.close();
		return this.lexpcfg;
	}

	/**
	 * 递归遍历句法树并提取规则
	 * 
	 * @param node
	 */
	private void traverseTree(HeadTreeNodeForCollins node)
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
			traverseTree((HeadTreeNodeForCollins) node1);
		}
	}

	/**
	 * 提取规则
	 * 
	 * @param node
	 */
	private void extractRule(HeadTreeNodeForCollins node)
	{
		if (node.getChildrenNum() == 0)
		{// 出现错误标注节点
			return;
		}
		// 统计生成中心child的数据
		getHeadGR(node);
		// 统计生成中心Child两侧的数据,以及统计两侧不生成Stop的数据
		getSidesGR(node);
		// 统计生成中心Stop的数据,此处是指统计两侧为Stop的数据
		getStopGR(node);
	}

	/**
	 * 收集生成中心child的统计数据
	 * 
	 * @param node
	 */
	private void getHeadGR(HeadTreeNodeForCollins node)
	{
		String headLabel = node.getChild(node.getHeadChildIndex()).getNodeName();
		String parentLabel = node.getNodeName();
		String headword = node.getHeadWord();
		String headpos = node.getHeadPos();
		// 回退模型1
		RuleHeadChildGenerate hcgr0 = new RuleHeadChildGenerate(headLabel, parentLabel, headpos, headword);
		RuleHeadChildGenerate hcgr1 = new RuleHeadChildGenerate(null, parentLabel, headpos, headword);
		addGenerateRule(hcgr0, hcgr1, headGenMap);
		// 回退模型二
		RuleHeadChildGenerate hcgr2 = new RuleHeadChildGenerate(headLabel, parentLabel, headpos, null);
		RuleHeadChildGenerate hcgr3 = new RuleHeadChildGenerate(null, parentLabel, headpos, null);
		addGenerateRule(hcgr2, hcgr3, headGenMap);
		// 回退模型三
		RuleHeadChildGenerate hcgr4 = new RuleHeadChildGenerate(headLabel, parentLabel, null, null);
		RuleHeadChildGenerate hcgr5 = new RuleHeadChildGenerate(null, parentLabel, null, null);
		addGenerateRule(hcgr4, hcgr5, headGenMap);

		// 在解析中需要用此得到候选的父节点，以免不需要的完全遍历非终结符
		RuleHeadChildGenerate rhcg = new RuleHeadChildGenerate(headLabel, null, headpos, headword);
		if (!parentList.containsKey(rhcg))
		{
			HashSet<String> labelSet = new HashSet<String>();
			labelSet.add(parentLabel);
			parentList.put(rhcg, labelSet);
		}
		else
		{
			parentList.get(rhcg).add(parentLabel);
		}
	}

	/**
	 * 统计在已有中心Child的基础上生成两侧孩子的数据
	 * 
	 * @param node
	 */
	private void getSidesGR(HeadTreeNodeForCollins node)
	{
		int headIndex = node.getHeadChildIndex();// 中心节点标记
		String parentLabel = node.getNodeName();// 父节点的非终结符标记
		String headPOS = node.getHeadPos();// 中心词词性标记
		String headWord = node.getHeadWord();// 中心词
		String headLabel = node.getChildName(headIndex);// 中心孩子的标记\

		// 单独取左侧生成规则
		for (int i = headIndex - 1; i >= 0; i--)
		{
			getOneSideGR(1, i, parentLabel, headLabel, headPOS, headWord, headIndex, node);
		}
		// 单独取右侧的生成规则
		for (int i = headIndex; i < node.getChildrenNum(); i++)
		{
			getOneSideGR(2, i, parentLabel, headLabel, headPOS, headWord, headIndex, node);
		}

	}

	/**
	 * 收集生成Stop的统计数据 若存在<direction,Stop,P,H,distance>则意味着可以在该处添加Stop
	 * 
	 * @param node
	 */
	private void getStopGR(HeadTreeNodeForCollins node)
	{
		int headIndex = node.getHeadChildIndex();
		String headLabel = node.getChild(headIndex).getNodeName();
		String parentLabel = node.getNodeName();
		String headWord = node.getHeadWord();
		String headPOS = node.getHeadPos();
		Distance leftDistance = getDistance(node, 1, headIndex, -1);// 左侧距离
		Distance rightDistance = getDistance(node, 1, headIndex, node.getChildrenNum());// 右侧距离

		getOneSideStopGRule(true, parentLabel, headLabel, headPOS, headWord, leftDistance, 1);// 左侧 Stop符号
		getOneSideStopGRule(true, parentLabel, headLabel, headPOS, headWord, rightDistance, 2);// 右侧Stop符号
	}

	/**
	 * 取中心节点两侧的某个孩子的数据 我将用rsg4判断此次生成两侧孩子是否可行
	 * 
	 * @param direction
	 * @param i
	 *            子节点的序列值
	 * @param parentLabel
	 * @param headLabel
	 * @param headPOS
	 * @param headWord
	 * @param headIndex
	 */
	private void getOneSideGR(int direction, int i, String parentLabel, String headLabel, String headPOS,
			String headWord, int headIndex, HeadTreeNodeForCollins node)
	{
		String sideLabel = node.getChildName(i);// 所求孩子节点的标记
		String sideHeadPOS = node.getChildHeadPos(i);// 所求孩子节点的中心词词标记
		String sideHeadWord = node.getChildHeadWord(i);// 所求的孩子节点的中心词
		int coor = 0;// 并列结构,0为不设值，1和2为有或者没有
		int pu = 0;// 标点符号，由于只保留了顿号所以我们可以把它当做并列结构，并列结构,0为不设值，1和2为有或者没有
		Distance distance = getDistance(node, direction, i, headIndex);
		// 若为基本名词短语，则headChild变为前一个修饰符
		if (CTBPreprocessTool.IsNPB(node))
		{
			distance = new Distance();// NPB不需要距离度量，故将其设置为固定值（此处为false）
			if (i > headIndex)
			{
				// 修饰符在headChild右侧
				headLabel = node.getChildName(i - 1);
				headPOS = node.getChildHeadPos(i - 1);
				headWord = node.getChildHeadWord(i - 1);
			}
			else
			{
				// 修饰符在headChild左侧
				headLabel = node.getChildName(i + 1);
				headPOS = node.getChildHeadPos(i + 1);
				headWord = node.getChildHeadWord(i + 1);
			}
		}
		// 此刻虽然不生成Stop但仍要统计其数据，在计算概率时使用
		getOneSideStopGRule(false, parentLabel, headLabel, headPOS, headWord, distance, direction);

		// 生成两侧Label和pos的回退模型
		// 回退模型1
			RuleSidesGenerate rsg0 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, headWord, direction, sideLabel,
					sideHeadPOS, null, coor, pu, distance);
		RuleSidesGenerate rsg1 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, headWord, direction, null, null,
				null, 0, 0, distance);
		addGenerateRule(rsg0, rsg1, sidesGeneratorMap);
		// 回退模型二
		RuleSidesGenerate rsg2 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, null, direction, sideLabel,
				sideHeadPOS, null, coor, pu, distance);
		RuleSidesGenerate rsg3 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, null, direction, null, null,
				null, 0, 0, distance);
		addGenerateRule(rsg2, rsg3, sidesGeneratorMap);
		// 回退模型三
		RuleSidesGenerate rsg4 = new RuleSidesGenerate(headLabel, parentLabel, null, null, direction, sideLabel,
				sideHeadPOS, null, coor, pu, distance);
		RuleSidesGenerate rsg5 = new RuleSidesGenerate(headLabel, parentLabel, null, null, direction, null, null, null,
				0, 0, distance);
		addGenerateRule(rsg4, rsg5, sidesGeneratorMap);

		// 生成两侧word的回退模型
		// 回退模型1
		RuleSidesGenerate rsgword0 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, headWord, direction,
				sideLabel, sideHeadPOS, sideHeadWord, coor, pu, distance);
		RuleSidesGenerate rsgword1 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, headWord, direction,
				sideLabel, sideHeadPOS, null, coor, pu, distance);
		addGenerateRule(rsgword0, rsgword1, sidesGeneratorMap);
		// 回退模型2
		RuleSidesGenerate rsgword2 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, null, direction, sideLabel,
				sideHeadPOS, sideHeadWord, coor, pu, distance);
		RuleSidesGenerate rsgword3 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, null, direction, sideLabel,
				sideHeadPOS, null, coor, pu, distance);
		addGenerateRule(rsgword2, rsgword3, sidesGeneratorMap);
		// 回退模型3
		RuleSidesGenerate rsgword4 = new RuleSidesGenerate(sideHeadPOS, sideHeadWord);
		RuleSidesGenerate rsgword5 = new RuleSidesGenerate(sideHeadPOS, null);
		addGenerateRule(rsgword4, rsgword5, sidesGeneratorMap);
	}

	private void getOneSideStopGRule(boolean stop, String parentLabel, String headLabel, String headPOS,
			String headWord, Distance distance, int direction)
	{
		// 生成两侧word的回退模型,输出只有两种，所以不需要单独添加计算概率和权重时的分母`
		// 回退模型1
		RuleStopGenerate sg0 = new RuleStopGenerate(headLabel, parentLabel, headPOS, headWord, direction, stop,
				distance);
		addGenerateRule(sg0, null, stopGenMap);
		// 回退模型2
		RuleStopGenerate sg2 = new RuleStopGenerate(headLabel, parentLabel, headPOS, null, direction, stop, distance);
		addGenerateRule(sg2, null, stopGenMap);
		// 回退模型3
		RuleStopGenerate sg4 = new RuleStopGenerate(headLabel, parentLabel, null, null, direction, stop, distance);
		addGenerateRule(sg4, null, stopGenMap);
	}

	/**
	 * 得到特定孩子到中心孩子的距离（默认verb和邻接为false）
	 * 
	 * @param node
	 * @param direction
	 * @param i
	 * @param headIndex
	 * @return
	 */
	private Distance getDistance(HeadTreeNodeForCollins node, int direction, int i, int headIndex)
	{
		Distance distance = new Distance();
		// 邻接
		if (Math.abs(node.getHeadChildIndex() - i) == 1)
		{
			distance.setAdjacency(true);
			return distance;
		}
		// 非邻接
		int start, end;
		if (direction == 1)
		{
			start = i + 1;
			end = headIndex - 1;
		}
		else
		{
			start = headIndex + 1;
			end = i - 1;
		}
		for (int j = start; j <= end; j++)
		{
			HeadTreeNodeForCollins collinsNode = (HeadTreeNodeForCollins) node.getChild(j);
			if (collinsNode.isVerb())
			{
				distance.setCrossVerb(true);
				return distance;
			}
		}
		return distance;
	}

	/**
	 * 添加生成头结点的生成规则
	 * 
	 * @param rule1
	 * @param rule2
	 */
	private void addGenerateRule(RuleCollins rule1, RuleCollins rule2, HashMap<RuleCollins, AmountAndSort> map)
	{
		if (rule2 == null)
		{
			if ((!map.containsKey(rule1)))
			{
				map.put(rule1, new AmountAndSort(1, 0));
			}
			else
			{
				map.get(rule1).addAmount(1);
			}
		}
		else
		{
			if (!map.containsKey(rule2))
			{
				map.put(rule2, new AmountAndSort(1, 1));
				map.put(rule1, new AmountAndSort(1, 0));
			}
			else
			{
				if (!map.containsKey(rule1))
				{
					map.put(rule1, new AmountAndSort(1, 0));
					map.get(rule2).addSort(1);
				}
				else
				{
					map.get(rule1).addAmount(1);
				}
				map.get(rule2).addAmount(1);
			}
		}
	}
}
