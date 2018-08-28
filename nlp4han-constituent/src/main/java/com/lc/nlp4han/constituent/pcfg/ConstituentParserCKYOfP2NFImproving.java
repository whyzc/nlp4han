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

public class ConstituentParserCKYOfP2NFImproving implements ConstituentParser
{
	private CKYTreeNode[][] table;// 存储在该点的映射表
	private PCFG pcnf;
	private ArrayList<String> resultList;

	public ConstituentParserCKYOfP2NFImproving(PCFG pcnf)
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
		for (int i = 0; i <= n; i++)
		{
			for (int j = 1; j <= n; j++)
			{
				// 矩阵的上三角才会用于存储数据
				if (j > i + 1)
				{
					table[i][j] = new CKYTreeNode(new HashMap<String, ArrayList<CKYPRule>>(), false);
				} // 对角线上的点的flag需要标记为true作为区别
				else if (j == i + 1)
				{
					table[i][j] = new CKYTreeNode(new HashMap<String, ArrayList<CKYPRule>>(), true);
				}

			}
		}
		// 开始剖析
		for (int j = 1; j <= n; j++)
		{// 从第一列开始，由左往右
			// 由分词结果反推得到规则，并进行table表对角线的初始化
			HashMap<String, ArrayList<CKYPRule>> ruleMap = table[j - 1][j].getPruleMap();
			if (pos == null)
			{
				ArrayList<String> rhs = new ArrayList<String>();
				rhs.add(words[j - 1]);
				Set<PRule> ruleSet = PCFG.convertRewriteRuleSetToPRuleSet(pcnf.getRuleByrhs(rhs));
				for (PRule rule : ruleSet)
				{
					ArrayList<CKYPRule> ckyPRulList = new ArrayList<CKYPRule>();
					// 此处延迟概率初始化至updateRuleMapOfTable
					ckyPRulList.add(new CKYPRule(1.0, rule.getLhs(), rule.getRhs(), 0, 0, 0));
					HashMap<String, Double> lhsAndProMap = new HashMap<String, Double>();
					updateRuleMapOfTable(rule, ruleMap, rule.getLhs(), ckyPRulList, numOfResulets, lhsAndProMap);
				}
			}
			else
			{
				// 根据分词和词性标注的结果进行table表对角线的j初始化
				ArrayList<CKYPRule> ckyPRulList = new ArrayList<CKYPRule>();
				ckyPRulList.add(new CKYPRule(1.0, pos[j - 1], words[j - 1], 0, 0, 0));
				PRule rule = new PRule(1.0, pos[j - 1], words[j - 1]);
				HashMap<String, Double> lhsAndProMap = new HashMap<String, Double>();
				updateRuleMapOfTable(rule, ruleMap, rule.getLhs(), ckyPRulList, numOfResulets, lhsAndProMap);
			}
			if (j <= 1)
			{
				continue;
			}
			for (int i = j - 2; i >= 0; i--)
			{// 从第j-2行开始，由下到上
				// System.out.println("i= " + i + " " + "j= " + j);
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
		else if (ruleMap.get(lhs).size() == numOfResulets && ruleMap.get(lhs).get(numOfResulets - 1)
				.getProOfRule() >= ckyPRuleList.get(0).getProOfRule() * rule.getProOfRule())
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
				ArrayList<CKYPRule> subPruleList = new ArrayList<CKYPRule>();
				for (int i = 0; i < numOfResulets; i++)
				{
					subPruleList.add(tempList.get(i));
				}
				ruleMap.put(lhs, subPruleList);
			}
			else
			{
				ruleMap.put(lhs, tempList);
			}
		}
		int size = ruleMap.get(lhs).size();
		lhsAndProMap.put(lhs, ruleMap.get(lhs).get(size - 1).getProOfRule());
		Set<RewriteRule> ruleSet = pcnf.getRuleByrhs(lhs);
		if (ruleSet != null)
		{
			for (PRule prule : PCFG.convertRewriteRuleSetToPRuleSet(ruleSet))
			{
				double pro1 = prule.getProOfRule() * rule.getProOfRule() * ckyPRuleList.get(0).getProOfRule();
				if (lhsAndProMap.containsKey(prule.getLhs()))
				{
					if (lhsAndProMap.get(prule.getLhs()) >= pro1)
					{
						continue;
					}
				}
				PRule prule1 = new PRule(prule.getProOfRule() * rule.getProOfRule(),
						prule.getLhs() + "@" + rule.getLhs(), rule.getRhs());
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
				tempList.add(new CKYPRule(ikCKYPRule.getProOfRule() * kjCKYPRule.getProOfRule(), lhs, prule.getRhs(), k,
						i, j));
			}
		}
		Collections.sort(tempList);
		/*
		 * 若结果集中多余k个，则截取其中的前k个
		 */
		if (tempList.size() > numOfResulets)
		{
			ArrayList<CKYPRule> subPruleList = new ArrayList<CKYPRule>();
			for (int i = 0; i < numOfResulets; i++)
			{
				subPruleList.add(tempList.get(i));
			}
			return subPruleList;
		}
		return tempList;

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
		ArrayList<CKYPRule> resultRuleList = table[0][n].getPruleMap().get(pcnf.getStartSymbol());
		resultList = new ArrayList<String>();
		if (resultRuleList == null)
		{
			return;
		}
		for (CKYPRule prule : resultRuleList)
		{
			StringBuilder strBuilder = new StringBuilder();
			// 从最后一个节点[0,n]开始回溯
			CreateStringBuilder(0, n, prule, strBuilder);
			resultList.add(strBuilder.toString());
		}
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
				// 含有起始符或者为伪词性标注则跳过
				if (lhs1.equals(pcnf.getStartSymbol()) || lhs1.contains("$"))
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
		AddString(i, prule.getK(), prule, 0, strBuilder);
		AddString(prule.getK(), j, prule, 1, strBuilder);
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
		// 获取对应的规则
		if (i == 0)
		{
			prule1 = table[n][m].getPruleMap().get(DuPos).get(prule.getI());
		}
		else
		{
			prule1 = table[n][m].getPruleMap().get(DuPos).get(prule.getJ());
		}

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

	private ArrayList<CKYPRule> makeNewArrayList(ArrayList<CKYPRule> ckyPRuleList, PRule rule)
	{
		ArrayList<CKYPRule> tempList = new ArrayList<CKYPRule>();
		for (int i = 0; i < ckyPRuleList.size(); i++)
		{
			CKYPRule ckyprule = ckyPRuleList.get(i);
			tempList.add(new CKYPRule(rule.getProOfRule() * ckyprule.getProOfRule(), rule.getLhs(), ckyprule.getRhs(),
					ckyprule.getK(), ckyprule.getI(), ckyprule.getJ()));
		}
		return tempList;
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
			super();
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
