package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;

public class ConstituentParseCKYPCNF implements ConstituentParser
{
	private CKYTreeNode[][] table;// 存储在该点的映射表
	private PCFG pcnf;
	private ArrayList<String> resultList;

	public ConstituentParseCKYPCNF(PCFG pcnf)
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
	private ArrayList<String> CKYParser(String[] words, String[] pos, Integer numOfResulets)
	{
		int n = words.length;
		table = new CKYTreeNode[n + 1][n + 1];
		for (int i = 0; i <= n; i++)
		{
			for (int j = 1; j <= n; j++)
			{

				if (j > i + 1)
				{// 矩阵的上三角才会用于存储数据
					table[i][j] = new CKYTreeNode(new HashMap<String, CKYPRule>(), false);
				}
				else if (j == i + 1)
				{// 对角线上的点的flag需要标记为true作为区别
					table[i][j] = new CKYTreeNode(new HashMap<String, CKYPRule>(), true);
				}

			}
		}
		// 开始剖析
		for (int j = 1; j <= n; j++)
		{// 从第一列开始，由左往右
			HashMap<String, CKYPRule> ruleMap = table[j - 1][j].getPruleMap();
			if (pos == null)
			{// 由分词结果反推得到规则，并进行table表对角线的初始化
				for (RewriteRule rule0 : pcnf.getRuleByrhs(words[j - 1]))
				{
					PRule rule = (PRule) rule0;
					String lhs = rule.getLhs().split("@")[0];
					CKYPRule ckyrule = new CKYPRule(rule.getProOfRule(), rule.getLhs(), rule.getRhs(), 0, 0, 0);
					ruleMap.put(lhs, ckyrule);
					updateRuleMap(rule.getProOfRule(), ruleMap, rule.getLhs(), words[j - 1], null, 0);
				}
			}
			else
			{// 根据分词和词性标注的结果进行table表对角线的j初始化
				CKYPRule ckyrule = new CKYPRule(1.0, pos[j - 1], words[j - 1], 0, 0, 0);
				ruleMap.put(pos[j - 1], ckyrule);
				updateRuleMap(1.0, ruleMap, pos[j - 1], words[j - 1], null, 0);
			}
			if (j <= 1)
			{
				continue;
			}
			for (int i = j - 2; i >= 0; i--)
			{// 从第j-2行开始，由下到上
				for (int k = i + 1; k <= j - 1; k++)
				{// 遍历table[i][k]和table[k][j]中的映射表，更新table[i][j]和back[i][j]
					updateTable(i, k, j, n, numOfResulets);
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
	 * @param numOfResulets
	 *            所需的最佳结果数
	 * @return count 进行更新操作的次数
	 */
	private void updateTable(int i, int k, int j, int n, int numOfResulets)
	{
		HashMap<String, CKYPRule> ikRuleMap = table[i][k].getPruleMap();
		HashMap<String, CKYPRule> kjRuleMap = table[k][j].getPruleMap();
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
					double pro = ikRuleMap.get(ikStr).getProOfRule() * kjRuleMap.get(kjStr).getProOfRule();
					updateRuleMap(pro, table[i][j].getPruleMap(), null, ikStr, kjStr, k);
				}
			}
		}
	}

	/**
	 * 更新table表中的RuleMap
	 * 
	 * @param pro
	 *            起始概率
	 * @param ruleMap
	 *            table[i][j]的规则映射表
	 * @param lhs0
	 *            规则左侧，用于更新对角线的节点，若为用于更新非对角线节点则为null
	 * @param rhs1
	 *            规则右侧第一个，用于更新对角线的节点（中文词）,用于更新非对角线节点（rhs的第一个）
	 * @param rhs2
	 *            规则右侧第二个，用于更新非对角线节点（rhs的第一个）,若为用于更新对角线节点则为null
	 * @param k
	 *            分裂的位置
	 */
	private void updateRuleMap(double pro, HashMap<String, CKYPRule> ruleMap, String lhs0, String rhs1, String rhs2,
			int k)
	{
		Set<RewriteRule> ruleSet = null;
		ArrayList<String> rhs = new ArrayList<String>();
		if (lhs0 == null)
		{
			rhs.add(rhs1);
			rhs.add(rhs2);
			ruleSet = pcnf.getRuleByrhs(rhs);
		}
		else
		{
			rhs.add(rhs1);
			ruleSet = pcnf.getRuleByrhs(lhs0);
		}
		if (ruleSet != null)
		{
			for (RewriteRule rule : ruleSet)
			{
				PRule prule = (PRule) rule;

				String lhsOfckyrule1 = prule.getLhs();
				if (lhs0 != null)
				{
					lhsOfckyrule1 += "@" + lhs0;
				}
				CKYPRule ckyrule1 = new CKYPRule(prule.getProOfRule() * pro, lhsOfckyrule1, rhs, k, 0, 0);
				String lhs = prule.getLhs().split("@")[0];// 取左侧第一个为ruleMap的key值，如NP@NN中的NP
				if (!ruleMap.keySet().contains(lhs))
				{// 该非终结符对应的规则不存在，直接添加
					ruleMap.put(lhs, ckyrule1);
				}
				else if (ruleMap.get(lhs).getProOfRule() < ckyrule1.getProOfRule())
				{
					ruleMap.put(lhs, ckyrule1);
				}
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
		CKYPRule resultRule = table[0][n].getPruleMap().get(pcnf.getStartSymbol());
		resultList = new ArrayList<String>();
		if (resultRule == null)
		{// 如果没有Parse结果则直接返回
			return;
		}
		StringBuilder strBuilder = new StringBuilder();
		CreateStringBuilder(0, n, resultRule, strBuilder);// 从最后一个节点[0,n]开始回溯
		resultList.add(strBuilder.toString());
	}

	// 递归table和back生成StringBuilder
	private void CreateStringBuilder(int i, int j, CKYPRule prule, StringBuilder strBuilder)
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
				// 含有为伪词性标注则跳过
				if (lhs1.contains("$"))
				{
					count--;
					continue;
				}
				strBuilder.append("(");
				strBuilder.append(lhs1);
			}
		} // * 当含有&符号时，则为两个非终结符在中间过程合成的，故不处理此非终结符，直接跳过
		else if (prule.getLhs().contains("&"))
		{
			AddString(i, prule.getK(), prule, 0, strBuilder);
			AddString(prule.getK(), j, prule, 1, strBuilder);
			return;
		}
		else
		{
			strBuilder.append("(");
			strBuilder.append(lhs);
		}
		if (table[i][j].isFlag())
		{
			strBuilder.append(" ");
			strBuilder.append(prule.getRhs().get(0));
		}
		else
		{
			AddString(i, prule.getK(), prule, 0, strBuilder);
			AddString(prule.getK(), j, prule, 1, strBuilder);
		}
		while (count > 0)
		{
			strBuilder.append(")");
			count--;
		}
	}

	/**
	 * 添加左右括号和终结符与非终结符，i记录prule右侧的非终结符序号
	 */
	private void AddString(int n, int m, CKYPRule prule, int i, StringBuilder strBuilder)
	{
		CKYPRule prule1;
		String DuPos = prule.getRhs().get(i);
		prule1 = table[n][m].getPruleMap().get(DuPos);// 获取对应的规则
		if (table[n][m].isFlag())
		{// 叶子结点
			int count = 1;
			String pos;
			pos = prule1.getLhs();
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
			strBuilder.append(prule1.getRhs().get(0));// 词
			while (count > 0)
			{
				strBuilder.append(")");
				count--;
			}
		}
		else
		{
			CreateStringBuilder(n, m, prule1, strBuilder);
		}
	}

	/**
	 * 内部类,table存储类,记录在table[i][j]点中的映射规则表，以及用于判断是否为对角线上点的flag
	 */
	class CKYTreeNode
	{
		private HashMap<String, CKYPRule> pruleMap;
		// flag用来判断是否为对角线上的点
		private boolean flag;

		public CKYTreeNode(HashMap<String, CKYPRule> pruleMap, boolean flag)
		{
			super();
			this.pruleMap = pruleMap;
			this.flag = flag;
		}

		public HashMap<String, CKYPRule> getPruleMap()
		{
			return pruleMap;
		}

		public void setPruleMap(HashMap<String, CKYPRule> pruleMap)
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
