package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;

public class ConstituentParserCKY implements ConstituentParser
{
	private BackMap back[][];// 回溯使用，需要记住k和非终结符
	private CKYTreeNode[][] table;// 存储在该点的映射表
	private PCFG pcnf;
	private ArrayList<String> resultList;
	private HashMap<RewriteRule, Integer> resultMap;

	public ConstituentParserCKY(PCFG pcnf)
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
	 *           分词序列
	 * 
	 * @param pse
	 *           词性标注
	 * 
	 * @param numOfResulets
	 *           需要求的结果数
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
				if (j >= i + 1)
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
				System.out.println(rhs);
				Set<PRule> ruleSet = PCFG.convertRewriteRuleSetToPRuleSet(pcnf.getRuleByrhs(rhs));
				HashMap<String, RewriteRule> map = table[j - 1][j].getPruleMap();
				for (PRule rule : ruleSet)
				{
					map.put(rule.getLhs(), rule);
				}
			}
			else
			{
				// 根据分词和词性标注的结果进行table表对角线的初始化
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
		// 回溯并生成括号表达式列表
		CreatBracketStringList(n, numOfResulets);
		return resultList;
	}

	/**
	 * @param i
	 *        table表横坐标点
	 * @param k
	 *        分裂的值
	 * @param j
	 *        table表纵坐标点
	 * @param n
	 *        words的长度
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
							if (i == 0 && j == n)
							{//在最终节点
								PRule prule = (PRule) itr.next();
								if (prule.getLhs().equals(pcnf.getStartSymbol()))
								{
									PRule newPrule = new PRule(prule.getProOfRule() * ikPro * kjPro, prule.getLhs(),
											prule.getRhs());
									resultMap.put(newPrule, k);
								}
							}
							else
							{
								PRule prule = (PRule) itr.next();
								PRule oldPRule = (PRule) table[i][j].getPruleMap().get(prule.getLhs());
								PRule newPrule = new PRule(prule.getProOfRule() * ikPro * kjPro, prule.getLhs(),
										prule.getRhs());
								if ((oldPRule != null && newPrule.getProOfRule() > oldPRule.getProOfRule())
										|| oldPRule == null)
								{
									table[i][j].getPruleMap().put(newPrule.getLhs(), newPrule);
									table[i][j].setFlag(false);
									back[i][j].getBackMap().put(newPrule.getLhs(), k);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 生成括号表达式的列表
	 * @param n
	 *        句子长度
	 * @param numOfResulets
	 *                 需要获得的句子分析结果个数
	 */
	private void CreatBracketStringList(int n, int numOfResulets)
	{
		// 查找概率最大的n个结果
		ArrayList<PRule> resultRuleList = new PCFG().getHighestProRuleFromMap(resultMap, numOfResulets);
		System.out.println(resultRuleList.toString());
		resultList = new ArrayList<String>();
		for (PRule prule : resultRuleList)
		{
			StringBuilder strBuilder = new StringBuilder();
			// 从最后一个节点开始回溯
			int k = resultMap.get(prule);
			CreateStringBuilder(0, k, n, prule, strBuilder);
			resultList.add(strBuilder.toString());
		}
	}

	// 递归table和back生成StringBuilder
	private void CreateStringBuilder(int i, int k, int j, PRule prule, StringBuilder strBuilder)
	{
		strBuilder.append("(");
		strBuilder.append(prule.getLhs());
		AddString(i, k, prule, 0, strBuilder);
		AddString(k, j, prule, 1, strBuilder);
		strBuilder.append(")");
	}

	/**
	 * 添加左右括号和终结符与非终结符，i记录prule右侧的非终结符序号
	 */
	private void AddString(int n, int m, PRule prule, int i, StringBuilder strBuilder)
	{
		if (back[n][m].getBackMap().size() == 0)
		{// 叶子结点
			String pos = prule.getRhs().get(i);
			strBuilder.append("(");
			strBuilder.append(pos);// 词性标注
			strBuilder.append(" ");
			strBuilder.append(table[n][m].getPruleMap().get(pos).getRhs().get(0));// 词
			strBuilder.append(")");
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