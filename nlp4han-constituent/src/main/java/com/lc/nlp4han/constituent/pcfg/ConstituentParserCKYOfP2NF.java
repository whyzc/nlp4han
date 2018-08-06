package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;

public class ConstituentParserCKYOfP2NF implements ConstituentParser
{
	private BackMap back[][];// 回溯使用，需要记住k和非终结符
	private CKYTreeNode[][] table;// 存储在该点的映射表
	private PCFG pcnf;
	private ArrayList<String> resultList;
	private HashMap<RewriteRule, Integer> resultMap;

	public ConstituentParserCKYOfP2NF(PCFG pcnf)
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
	public ConstituentTree parseTree(String[] words, String[] poses)
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
	@Override
	public ConstituentTree parseTree(String[] words)
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
	public ConstituentTree[] parseKTree(String[] words, String[] poses, int k)
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
	@Override
	public ConstituentTree[] parseKTree(String[] words, int k)
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
		ConstituentTree[] treeArray = new ConstituentTree[k];
		ArrayList<String> bracketList = CKYParser(words, poses, k);
		int i = 0;
		for (String bracketString : bracketList)
		{
			TreeNode rootNode = BracketExpUtil.generateTreeNotDeleteBracket(bracketString);
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
	private ArrayList<String> CKYParser(String[] words, String[] pos, int numOfResulets)
	{
		int n = words.length;
		table = new CKYTreeNode[n + 1][n + 1];
		back = new BackMap[n + 1][n + 1];
		for (int i = 0; i <= n; i++)
		{
			for (int j = 1; j <= n; j++)
			{
				// 矩阵的上三角才会用于存储数据
				if (j > i + 1)
				{
					table[i][j] = new CKYTreeNode(new HashMap<String, RewriteRule>(), false);
					back[i][j] = new BackMap(new HashMap<String, Integer>());
				} // 对角线上的点的flag需要标记为true作为区别
				else if (j == i + 1)
				{
					table[i][j] = new CKYTreeNode(new HashMap<String, RewriteRule>(), true);
					back[i][j] = new BackMap(new HashMap<String, Integer>());
				}

			}
		}
		// 开始剖析
		for (int j = 1; j <= n; j++)
		{// 从第一列开始，由左往右
			/*
			 * 由分词结果反推得到规则，并进行table表对角线的初始化
			 */
			if (pos == null)
			{
				ArrayList<String> rhs = new ArrayList<String>();
				rhs.add(words[j - 1]);
				Set<PRule> ruleSet = PCFG.convertRewriteRuleSetToPRuleSet(pcnf.getRuleByrhs(rhs));
				HashMap<String, RewriteRule> map = table[j - 1][j].getPruleMap();
				for (PRule rule : ruleSet)
				{
					HashMap<String, Double> lhsAndProMap = new HashMap<String, Double>();
					updateRuleMapOfDiagonal(rule, map, rule.getLhs(), lhsAndProMap, j - 1, j);
				}
			}
			else
			{
				// 根据分词和词性标注的结果进行table表对角线的j初始化
				table[j - 1][j].getPruleMap().put(pos[j - 1], new PRule(1.0, pos[j - 1], words[j - 1]));
			}
			if (j <= 1)
			{
				continue;
			}
			for (int i = j - 2; i >= 0; i--)
			{// 从第j-2行开始，由下到上
				for (int k = i + 1; k <= j - 1; k++)
				{// 遍历table[i][k]和table[k][j]中的映射表，更新table[i][j]和back[i][j]
					updateTableAndBack(i, k, j, n);
				}
			}
		}
		for (int i = 0; i <= n; i++)
		{
			for (int j = 1; j <= n; j++)
			{
				// 矩阵的上三角才会用于存储数据
				if (j >= i + 1)
				{
					System.out.println("i==" + i + " j==" + j + " " + table[i][j].getPruleMap().toString());
				}
			}
		}
		// 回溯并生成括号表达式列表
		CreatBracketStringList(n, numOfResulets);
		return resultList;
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
	 */
	private void updateTableAndBack(int i, int k, int j, int n)
	{
		resultMap = new HashMap<RewriteRule, Integer>();
		HashMap<String, RewriteRule> ikRuleMap = table[i][k].getPruleMap();
		HashMap<String, RewriteRule> kjRuleMap = table[k][j].getPruleMap();
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
					double ikPro = ((PRule) ikRuleMap.get(ikStr)).getProOfRule();
					double kjPro = ((PRule) kjRuleMap.get(kjStr)).getProOfRule();
					Iterator<RewriteRule> itr = ruleSet.iterator();
					if (ikPro > 0 && kjPro > 0)
					{
						while (itr.hasNext())
						{
							double pro = ikPro * kjPro;
							PRule prule = (PRule) itr.next();
							if (i == 0 && j == n)
							{// 若为最终节点，则添加左侧为起始符的规则

								updateResultMap(prule, resultMap, prule.getLhs(), pro, k);
							}
							else
							{
								HashMap<String, Double> lhsAndProMap = new HashMap<String, Double>();
								HashMap<String, RewriteRule> ruleMap = table[i][j].getPruleMap();
								updateRuleMapOfTable(prule, ruleMap, pro, prule.getLhs(), lhsAndProMap, i, j, k);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 更新对角线上的点 在遇到单位产品时，新建规则，pro=P1*P2,LHS=prule.getLhs() + "@" +
	 * rule.getLhs()，RHS=rule.getRhs()。并在Map中以prule.getLhs()作为key值，
	 * 取出时通过ruleMap中规则左侧获取整个消除过程，例如：A->B->S将赋值为 A@B@S
	 * 
	 * @param rule
	 *            由ik点和kj点结合的rhs逆推得到的规则
	 * @param ruleMap
	 *            本节点，也就是ij点的规则映射
	 * @param lhs
	 *            映射的key值
	 * @param lhsAndProMap
	 *            记录映射中作为key值的非终结符及其概率，作为过滤掉重复规则的基础
	 * @param i
	 *            table表和back表横坐标
	 * @param j
	 *            table表和back表纵坐标
	 */
	private void updateRuleMapOfDiagonal(PRule rule, HashMap<String, RewriteRule> ruleMap, String lhs,
			HashMap<String, Double> lhsAndProMap, int i, int j)
	{
		PRule oldPRule = (PRule) ruleMap.get(lhs);
		lhsAndProMap.put(lhs, rule.getProOfRule());
		if ((oldPRule != null && rule.getProOfRule() > oldPRule.getProOfRule()) || oldPRule == null)
		{
			ruleMap.put(lhs, new PRule(rule.getProOfRule(), rule.getLhs(), rule.getRhs()));
		}
		Set<RewriteRule> ruleSet = pcnf.getRuleByrhs(lhs);
		if (ruleSet != null)
		{
			for (PRule prule : PCFG.convertRewriteRuleSetToPRuleSet(ruleSet))
			{
				double pro1 = rule.getProOfRule() * prule.getProOfRule();
				if (lhsAndProMap.containsKey(prule.getLhs()))
				{
					if (lhsAndProMap.get(prule.getLhs()) >= pro1)
					{
						continue;
					}
				}
				PRule prule1 = new PRule(pro1, prule.getLhs() + "@" + rule.getLhs(), rule.getRhs());
				updateRuleMapOfDiagonal(prule1, ruleMap, prule.getLhs(), lhsAndProMap, i, j);
			}
		}
	}

	/**
	 * 更新table和back表中的数据
	 * 
	 * @param rule
	 *            由ik点和kj点结合的rhs逆推得到的规则
	 * @param ruleMap
	 *            本节点，也就是ij点的规则映射
	 * @param pro
	 *            ik点和kj点的概率相乘，在后序遍历中赋值为1.0
	 * @param lhs
	 *            映射的key值
	 * @param lhsAndProMap
	 *            记录映射中作为key值的非终结符及其概率，作为过滤掉重复规则的基础
	 * @param i
	 *            table表和back表横坐标
	 * @param j
	 *            table表和back表纵坐标
	 * @param k
	 *            存储规则的右侧点的分裂位置，ij->ik kj
	 */
	private void updateRuleMapOfTable(PRule rule, HashMap<String, RewriteRule> ruleMap, double pro, String lhs,
			HashMap<String, Double> lhsAndProMap, int i, int j, int k)
	{
		PRule oldPRule = (PRule) ruleMap.get(lhs);
		if (pro != 1.0)
		{// 递归的第一层
			pro = pro * rule.getProOfRule();
			rule = new PRule(pro, rule.getLhs(), rule.getRhs());
		}
		lhsAndProMap.put(lhs, rule.getProOfRule());
		if ((oldPRule != null && rule.getProOfRule() > oldPRule.getProOfRule()) || oldPRule == null)
		{
			ruleMap.put(lhs, rule);
			back[i][j].getBackMap().put(lhs, k);
		}
		Set<RewriteRule> ruleSet = pcnf.getRuleByrhs(lhs);
		if (ruleSet != null)
		{
			for (PRule prule : PCFG.convertRewriteRuleSetToPRuleSet(ruleSet))
			{
				double pro1 = rule.getProOfRule() * prule.getProOfRule();
				if (lhsAndProMap.containsKey(prule.getLhs()))
				{
					if (lhsAndProMap.get(prule.getLhs()) >= pro1)
					{
						continue;
					}
				}
				PRule prule1 = new PRule(pro1, prule.getLhs() + "@" + rule.getLhs(), rule.getRhs());
				updateRuleMapOfTable(prule1, ruleMap, 1.0, prule.getLhs(), lhsAndProMap, i, j, k);
			}
		}
	}

	/**
	 * 更新结果集的数据
	 * 
	 * @param rule
	 *            当前rule
	 * @param resultMap
	 *            结果规则集
	 * @param lhs
	 *            映射表中规则的key值
	 * @param lhsAndProMap
	 *            存储映射表中规则的key值及其概率
	 * @param pro
	 *            由上层函数调用时赋值为ik点和kj点的概率相乘，在后序遍历中赋值为1.0
	 * @param k
	 *            存储规则的右侧点的位置，ij->ik kj
	 */
	private void updateResultMap(PRule rule, HashMap<RewriteRule, Integer> resultMap, String lhs, double pro, int k)
	{
		pro = pro * rule.getProOfRule();
		if (rule.getLhs().contains(pcnf.getStartSymbol()))
		{
			resultMap.put(rule, k);
		}
		Set<RewriteRule> ruleSet = pcnf.getRuleByrhs(lhs);
		if (ruleSet != null)
		{
			for (PRule prule : PCFG.convertRewriteRuleSetToPRuleSet(ruleSet))
			{
				double pro1 = pro * prule.getProOfRule();
				if (rule.getLhs().contains(prule.getLhs()))
				{
					continue;
				}
				PRule prule1 = new PRule(pro1, prule.getLhs() + "@" + rule.getLhs(), rule.getRhs());
				updateResultMap(prule1, resultMap, prule.getLhs(), 1.0, k);
			}
		}
	}

	/**
	 * 生成结果括号表达式的列表
	 * 
	 * @param n
	 *            句子长度
	 * @param numOfResulets
	 *            需要获得的句子分析结果个数
	 */
	private void CreatBracketStringList(int n, int numOfResulets)
	{
		// 查找概率最大的n个结果
		System.out.println(resultMap.toString());
		ArrayList<PRule> resultRuleList = new PCFG().getHighestProRuleFromMap(resultMap, numOfResulets);
		resultList = new ArrayList<String>();
		for (PRule prule : resultRuleList)
		{
			StringBuilder strBuilder = new StringBuilder();
			// 从最后一个节点[0,n]开始回溯
			int k = resultMap.get(prule);
			CreateStringBuilder(0, k, n, prule, strBuilder);
			resultList.add(strBuilder.toString());
		}
	}

	// 递归table和back生成StringBuilder
	private void CreateStringBuilder(int i, int k, int j, PRule prule, StringBuilder strBuilder)
	{
		int count = 1;
		String lhs = prule.getLhs();
		if (prule.getLhs().contains("@"))
		{// 存在单位产品则恢复
			strBuilder.append("(");
			String[] strArray = lhs.split("@");
			count += strArray.length;
			for (String lhs1 : strArray)
			{
				// 含有起始符或者为伪词性标注则跳过
				if (lhs1.equals(pcnf.getStartSymbol()) || lhs1.contains("$"))
				{
					count--;
					continue;
				}
				strBuilder.append("(");
				strBuilder.append(lhs1);
			}
			/*
			 * 当含有&符号时，则为两个非终结符在中间过程合成的，故不处理此非终结符，直接跳过
			 */
		}
		else if (prule.getLhs().contains("&"))
		{
			AddString(i, k, prule, 0, strBuilder);
			AddString(k, j, prule, 1, strBuilder);
			return;
		}
		else
		{
			strBuilder.append("(");
			strBuilder.append(lhs);
		}
		AddString(i, k, prule, 0, strBuilder);
		AddString(k, j, prule, 1, strBuilder);
		while (count > 0)
		{
			strBuilder.append(")");
			count--;
		}
	}

	/**
	 * 添加左右括号和终结符与非终结符，i记录prule右侧的非终结符序号
	 */
	private void AddString(int n, int m, PRule prule, int i, StringBuilder strBuilder)
	{
		if (table[n][m].isFlag())
		{// 叶子结点
			int count = 1;
			String DuPos = prule.getRhs().get(i);
			String pos = table[n][m].getPruleMap().get(DuPos).getLhs();
			strBuilder.append("(");
			// 为终结符，类似"$中国$"，直接跳过
			if (pos.contains("$"))
			{

			}
			// 恢复单位产品
			else if (pos.contains("@"))
			{
				String[] strArray = pos.split("@");
				count += strArray.length;
				for (String pos1 : strArray)
				{
					strBuilder.append("(");// 此处多打一个括号没有关系，因为在生成树的时候会格式化，去掉空的括号表达式
					strBuilder.append(pos1);
				}
			}
			else
			{
				strBuilder.append(pos);// 词性标注
			}
			strBuilder.append(" ");
			strBuilder.append(table[n][m].getPruleMap().get(DuPos).getRhs().get(0));// 词
			while (count > 0)
			{
				strBuilder.append(")");
				count--;
			}
		}
		else
		{
			int k1 = back[n][m].getBackMap().get(prule.getRhs().get(i));
			PRule prule1 = (PRule) table[n][m].getPruleMap().get(prule.getRhs().get(i));
			CreateStringBuilder(n, k1, m, prule1, strBuilder);
		}
	}

	/**
	 * 内部类,table存储类,记录在table[i][j]点中的映射规则表，以及用于判断是否为对角线上点的flag
	 */
	class CKYTreeNode
	{
		private HashMap<String, RewriteRule> pruleMap;
		// flag用来判断是否为对角线上的点
		private boolean flag;

		public HashMap<String, RewriteRule> getPruleMap()
		{
			return pruleMap;
		}

		public void setPruleMap(HashMap<String, RewriteRule> pruleMap)
		{
			this.pruleMap = pruleMap;
		}

		public boolean isFlag()
		{
			return flag;
		}

		public void setFlag(boolean flag)
		{
			this.flag = flag;// 判断是否为对角线上的点
		}

		public CKYTreeNode(HashMap<String, RewriteRule> pruleMap, boolean flag)
		{
			this.pruleMap = pruleMap;
			this.flag = flag;
		}
	}

	/**
	 * 内部类,back存储类,包含非终结符，以及对应该非终结符的k值，将[i,j]分裂为[i,k][k,j]
	 */
	class BackMap
	{
		private HashMap<String, Integer> backMap;

		public HashMap<String, Integer> getBackMap()
		{
			return backMap;
		}

		public void setBackMap(HashMap<String, Integer> backMap)
		{
			this.backMap = backMap;
		}

		public BackMap(HashMap<String, Integer> backMap)
		{
			this.backMap = backMap;
		}
	}
}
