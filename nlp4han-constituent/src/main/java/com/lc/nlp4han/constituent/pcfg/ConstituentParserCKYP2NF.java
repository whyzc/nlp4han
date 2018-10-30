package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;

public class ConstituentParserCKYP2NF implements ConstituentParser
{
	private CKYTreeNode[][] table;// 存储在该点的映射表
	private PCFG pcnf;

	public ConstituentParserCKYP2NF(PCFG pcnf)
	{
		this.pcnf = pcnf;
	}

	/**
	 * 得到概率最高的成分树
	 * 
	 * @param words
	 *            分词序列
	 * @param poses
	 *            词性标记
	 * @return
	 */
	@Override
	public ConstituentTree parse(String[] words, String[] poses)
	{
		return getParseResult(words, poses, 1)[0];
	}

	/**
	 * 得到概率最高的成分树
	 * 
	 * @param words
	 * 
	 * @return
	 */
	public ConstituentTree parse(String[] words)
	{
		return getParseResult(words, null, 1)[0];
	}

	/**
	 * 得到概率最高k个的成分树
	 * 
	 * @param words
	 *            分词序列
	 * @param poses
	 *            词性标记
	 * @param k
	 *            结果数目
	 * @return
	 */
	@Override
	public ConstituentTree[] parse(String[] words, String[] poses, int k)
	{
		return getParseResult(words, poses, k);
	}

	/**
	 * 得到概率最高k个的成分树
	 * 
	 * @param words
	 *            分词序列
	 * @param k
	 *            结果数目
	 * @return
	 */
	public ConstituentTree[] parse(String[] words, int k)
	{
		return getParseResult(words, null, k);
	}

	/**
	 * 得到成分树数组的通用方法
	 * 
	 * @param words
	 *            分词序列
	 * @param poses
	 *            词性标记
	 * @param k
	 *            结果数目
	 * @return
	 */
	private ConstituentTree[] getParseResult(String[] words, String[] poses, int k)
	{
		int i = 0;
		ConstituentTree[] treeArray = new ConstituentTree[k];
		ArrayList<String> bracketList = parseCKY(words, poses, k, true);
		if (bracketList.size() == 0 && words.length <= 70)
		{
			bracketList = parseCKY(words, poses, k, false);
		}
		for (String bracketString : bracketList)
		{
			TreeNode rootNode = RestoreTree.restoreTree2(BracketExpUtil.generateTree(bracketString));
			treeArray[i++] = new ConstituentTree(rootNode);
		}
		return treeArray;
	}

	/**
	 * CKY算法的具体函数
	 * 
	 * @param words
	 *            分词序列
	 * 
	 * @param pos
	 *            词性标注
	 * 
	 * @param numOfResulets
	 *            需要求的结果数
	 * 
	 * @return 输出k个句子解析结果
	 */
	private ArrayList<String> parseCKY(String[] words, String[] pos, int numOfResulets, boolean prun)
	{
		int n = words.length;
		// 初始化
		initializeChart(words, pos, numOfResulets);

		// 填充chart图中的边
		for (int span = 2; span <= n; span++)
		{
			for (int i = 0; i <= n - span; i++)
			{
				int j = i + span;
				for (int k = i + 1; k <= j - 1; k++)
				{// 遍历table[i][k]和table[k][j]中的映射表，更新table[i][j]和back[i][j]
					updateTable(i, k, j, n, numOfResulets);
				}
				// 剪枝
				if (prun)
				{
					prunEdge(i, j);
				}

			}
		}

		// 回溯并生成括号表达式列表,此刻的树并未还原为宾州树库的形式
		return bracketStringListGenerate(n, numOfResulets, table, pcnf);
	}

	/**
	 * 剪枝
	 * 
	 * @param i
	 * @param j
	 */
	private void prunEdge(int i, int j)
	{
		HashMap<String, ArrayList<CKYPRule>> map = table[i][j].getPruleMap();
		ArrayList<String> deleteList = new ArrayList<String>();
		double bestPro = -1.0;
		for (String str : map.keySet())
		{
			if (map.get(str).get(0).getProb() > bestPro)
			{
				bestPro = map.get(str).get(0).getProb();
			}
		}
		for (String str : map.keySet())
		{
			if (map.get(str).get(0).getProb() < bestPro * 0.0001)
			{
				deleteList.add(str);
			}
		}
		for (String str : deleteList)
		{
			map.remove(str);
		}
	}

	/**
	 * 初始化线图
	 * 
	 * @param words
	 * @param poses
	 * @param numOfResulets
	 */
	private void initializeChart(String[] words, String[] poses, int numOfResulets)
	{
		int n = words.length;
		table = new CKYTreeNode[n + 1][n + 1];
		for (int i = 0; i < n; i++)
		{
			for (int j = 1; j <= n; j++)
			{
				if (j >= i + 1)
				{// 只有矩阵的上三角存储数据
					table[i][j] = new CKYTreeNode(new HashMap<String, ArrayList<CKYPRule>>(), false);
				}
				if (j == i + 1)
				{
					table[i][j].setFlag(true);
					// 由分词结果反推得到规则，并进行table表对角线的初始化
					HashMap<String, ArrayList<CKYPRule>> ruleMap = table[j - 1][j].getPruleMap();
					if (poses == null)
					{
						ArrayList<String> rhs = new ArrayList<String>();
						rhs.add(words[j - 1]);
						for (RewriteRule rule0 : pcnf.getRuleByrhs(rhs))
						{
							PRule rule = (PRule) rule0;
							ArrayList<CKYPRule> ckyPRulList = new ArrayList<CKYPRule>();
							// 此处延迟概率初始化至updateRuleMapOfTable
							ckyPRulList.add(new CKYPRule(1.0, rule.getLhs(), rule.getRhs(), 0, 0, 0));
							HashMap<String, Double> lhsAndProMap = new HashMap<String, Double>();
							updateRuleMapOfTable(rule, ruleMap, rule.getLhs(), ckyPRulList, numOfResulets,
									lhsAndProMap);
						}
					}
					else
					{
						// 根据分词和词性标注的结果进行table表对角线的j初始化
						ArrayList<CKYPRule> ckyPRulList = new ArrayList<CKYPRule>();
						ckyPRulList.add(new CKYPRule(1.0, poses[j - 1], words[j - 1], 0, 0, 0));
						PRule rule = new PRule(1.0, poses[j - 1], words[j - 1]);
						HashMap<String, Double> lhsAndProMap = new HashMap<String, Double>();
						updateRuleMapOfTable(rule, ruleMap, rule.getLhs(), ckyPRulList, numOfResulets, lhsAndProMap);
					}
				}
			}
		}
	}

	/**
	 * @param i
	 *            table表横坐标点
	 * @param k
	 *            分裂的值
	 * @param j
	 *            table表纵坐标点
	 * @param n
	 *            words的长度
	 * @param numOfResulets
	 *            所需的最佳结果数
	 */
	private void updateTable(int i, int k, int j, int n, int numOfResulets)
	{
		HashMap<String, ArrayList<CKYPRule>> ikRuleMap = table[i][k].getPruleMap();
		HashMap<String, ArrayList<CKYPRule>> kjRuleMap = table[k][j].getPruleMap();
		if (ikRuleMap.size() != 0 && kjRuleMap.size() != 0)
		{// 如果在ik点和kj点的映射表不为空
			Iterator<String> itrIk = ikRuleMap.keySet().iterator();
			while (itrIk.hasNext())
			{
				String ikStr = itrIk.next();
				Iterator<String> itrKj = kjRuleMap.keySet().iterator();
				while (itrKj.hasNext())
				{
					String kjStr = itrKj.next();
					Set<RewriteRule> ruleSet = pcnf.getRuleByrhs(ikStr, kjStr);
					if (ruleSet == null)
					{// 若ruleSet为空，则遍历下一个kjStr
						continue;
					}
					Iterator<RewriteRule> itr = ruleSet.iterator();
					while (itr.hasNext())
					{
						PRule prule = (PRule) itr.next();
						ArrayList<CKYPRule> ckyPRuleList = getKCKYPRuleFromTable(k, numOfResulets, ikRuleMap.get(ikStr),
								kjRuleMap.get(kjStr), prule);
						HashMap<String, Double> lhsAndProMap = new HashMap<String, Double>();
						HashMap<String, ArrayList<CKYPRule>> ruleMap = table[i][j].getPruleMap();
						updateRuleMapOfTable(prule, ruleMap, prule.getLhs(), ckyPRuleList, numOfResulets, lhsAndProMap);
					}
				}
			}
		}
	}

	/**
	 * 更新table表中ruleMap,其中每个终结符对应的ArrayList中个最多有numOfResulets个 遇到单位产品时，比如S->A->B
	 * D新建规则，pro=P(S->A)*P(A->B D),LHS=S+ "@" +A ，RHS=B D。并在Map中以S作为key值
	 * 
	 * @param rule
	 * @param ruleMap
	 * @param lhs
	 * @param ckyPRuleList
	 * @param numOfResulets
	 * @param lhsAndProMap
	 */
	private void updateRuleMapOfTable(PRule rule, HashMap<String, ArrayList<CKYPRule>> ruleMap, String lhs,
			ArrayList<CKYPRule> ckyPRuleList, int numOfResulets, HashMap<String, Double> lhsAndProMap)
	{
		// 该非终结符对应的映射表不存在，直接添加
		if (!ruleMap.keySet().contains(lhs))
		{
			ArrayList<CKYPRule> tempList = makeNewArrayList(ckyPRuleList, rule);
			ruleMap.put(lhs, tempList);
		} // 若该非终结符对应的映射表已满，而且其中概率最小的比ckyPRuleList中最大的还要大则不处理
		else if (ruleMap.get(lhs).size() == numOfResulets
				&& ruleMap.get(lhs).get(numOfResulets - 1).getProb() >= ckyPRuleList.get(0).getProb() * rule.getProb())
		{

		} // 将ckyPRuleList和ruleMap中该非终结符对应的规则表联合再排序
		else
		{
			ArrayList<CKYPRule> tempList = makeNewArrayList(ckyPRuleList, rule);
			tempList.addAll(ruleMap.get(lhs));
			Collections.sort(tempList);
			/*
			 * 若结果集中多余k个，则截取其中的前k个
			 */
			if (tempList.size() > numOfResulets)
			{
				ArrayList<CKYPRule> subList = new ArrayList<CKYPRule>();
				for (int i = 0; i < numOfResulets; i++)
				{
					subList.add(tempList.get(i));
				}
				ruleMap.put(lhs, subList);
			}
			else
			{
				ruleMap.put(lhs, tempList);
			}
		}
		int size = ruleMap.get(lhs).size();
		lhsAndProMap.put(lhs, ruleMap.get(lhs).get(size - 1).getProb());
		Set<RewriteRule> ruleSet = pcnf.getRuleByrhs(lhs);

		if (ruleSet != null)
		{
			for (RewriteRule rule0 : ruleSet)
			{
				PRule prule = (PRule) rule0;
				double pro1 = prule.getProb() * rule.getProb() * ckyPRuleList.get(0).getProb();
				if (lhsAndProMap.containsKey(prule.getLhs()))
				{
					if (lhsAndProMap.get(prule.getLhs()) >= pro1)
					{
						continue;
					}
				}
				PRule prule1 = new PRule(prule.getProb() * rule.getProb(), prule.getLhs() + "@" + rule.getLhs(),
						rule.getRhs());
				updateRuleMapOfTable(prule1, ruleMap, prule.getLhs(), ckyPRuleList, numOfResulets, lhsAndProMap);
			}
		}
	}

	/**
	 * 由规则右侧反推规则左侧，由右侧的非终结符得到对应的列表ikCKYPRuleList，kjCKYPRuleList，然后不同的列表中的规则两两结合，取前numOfResulets个并返回
	 * 
	 * @param k
	 *            分裂位置
	 * @param numOfResultRule
	 *            所需结果数
	 * @param ikCKYPRuleList
	 *            ik点的非终结符ikStr对应的规则列表
	 * @param kjCKYPRuleList
	 *            kj点的非终结符kjStr对应的规则列表
	 * @param prule
	 *            ij点的规则
	 * @return
	 */
	private ArrayList<CKYPRule> getKCKYPRuleFromTable(int k, int numOfResulets, ArrayList<CKYPRule> ikCKYPRuleList,
			ArrayList<CKYPRule> kjCKYPRuleList, PRule prule)
	{
		String lhs = prule.getLhs();
		ArrayList<CKYPRule> tempList = new ArrayList<CKYPRule>();
		for (int i = 0; i < ikCKYPRuleList.size(); i++)
		{
			CKYPRule ikCKYPRule = ikCKYPRuleList.get(i);
			for (int j = 0; j < kjCKYPRuleList.size(); j++)
			{
				CKYPRule kjCKYPRule = kjCKYPRuleList.get(j);
				tempList.add(new CKYPRule(ikCKYPRule.getProb() * kjCKYPRule.getProb(), lhs, prule.getRhs(), k, i, j));
			}
		}
		Collections.sort(tempList);
		/*
		 * 若结果集中多余k个，则截取其中的前k个
		 */
		if (tempList.size() > numOfResulets)
		{
			ArrayList<CKYPRule> subList = new ArrayList<CKYPRule>();
			for (int i = 0; i < numOfResulets; i++)
			{
				subList.add(tempList.get(i));
			}
			return subList;
		}
		return tempList;

	}

	private ArrayList<CKYPRule> makeNewArrayList(ArrayList<CKYPRule> ckyPRuleList, PRule rule)
	{
		ArrayList<CKYPRule> tempList = new ArrayList<CKYPRule>();
		for (int i = 0; i < ckyPRuleList.size(); i++)
		{
			CKYPRule ckyprule = ckyPRuleList.get(i);
			tempList.add(new CKYPRule(rule.getProb() * ckyprule.getProb(), rule.getLhs(), ckyprule.getRhs(),
					ckyprule.getK(), ckyprule.getI(), ckyprule.getJ()));
		}
		return tempList;
	}

	/**
	 * 生成括号表达式
	 * 
	 * @param n
	 * @param numOfResulets
	 * @param table
	 * @param pcnf
	 * @return
	 */
	public static ArrayList<String> bracketStringListGenerate(int n, int numOfResulets, CKYTreeNode[][] table,
			PCFG pcnf)
	{
		// 查找概率最大的n个结果
		ArrayList<CKYPRule> resultRuleList = table[0][n].getPruleMap().get(pcnf.getStartSymbol());
		ArrayList<String> resultList = new ArrayList<String>();

		if (resultRuleList == null)
		{
			return resultList;
		}
		for (CKYPRule prule : resultRuleList)
		{
			StringBuilder strBuilder = new StringBuilder();
			// 从最后一个节点[0,n]开始回溯
			CreateStringBuilder(0, n, prule, strBuilder, table);
			resultList.add(strBuilder.toString());
		}
		return resultList;
	}

	/**
	 * 先根遍历
	 * 
	 * @param i
	 * @param j
	 * @param prule
	 * @param strBuilder
	 * @param table
	 */
	private static void CreateStringBuilder(int i, int j, CKYPRule prule, StringBuilder strBuilder,
			CKYTreeNode[][] table)
	{
		strBuilder.append("(");

		strBuilder.append(prule.getLhs());
		if (i == j - 1)
		{// 对角线存储词性规则
			strBuilder.append(" ");
			strBuilder.append(prule.getRhs().get(0));
			strBuilder.append(")");
			return;
		}
		// 第一个孩子
		CKYPRule lPrule = table[i][prule.getK()].getPruleMap().get(prule.getRhs().get(0)).get(prule.getI());
		CreateStringBuilder(i, prule.getK(), lPrule, strBuilder, table);
		// 第二个孩子
		CKYPRule rPrule = table[prule.getK()][j].getPruleMap().get(prule.getRhs().get(1)).get(prule.getJ());
		CreateStringBuilder(prule.getK(), j, rPrule, strBuilder, table);

		strBuilder.append(")");
	}

	/**
	 * 内部类,table存储类,记录在table[i][j]点中的映射规则表，以及用于判断是否为对角线上点的flag
	 */
	class CKYTreeNode
	{
		private HashMap<String, ArrayList<CKYPRule>> pruleMap;
		// flag用来判断是否为对角线上的点
		private boolean flag;

		public CKYTreeNode(HashMap<String, ArrayList<CKYPRule>> pruleMap, boolean flag)
		{
			this.pruleMap = pruleMap;
			this.flag = flag;
		}

		public HashMap<String, ArrayList<CKYPRule>> getPruleMap()
		{
			return pruleMap;
		}

		public void setPruleMap(HashMap<String, ArrayList<CKYPRule>> pruleMap)
		{
			this.pruleMap = pruleMap;
		}

		public boolean isFlag()
		{
			return flag;
		}

		public void setFlag(boolean flag)
		{
			this.flag = flag;
		}
	}
}
