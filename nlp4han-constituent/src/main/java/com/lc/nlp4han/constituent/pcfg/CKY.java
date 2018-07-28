package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CKY
{
	private BackMap back[][];// 回溯使用，需要记住k和非终结符
	private CKYTreeNode[][] table;// 存储在该点的映射表
	private PCFG pcnf;
	private ArrayList<String> words;
	private ArrayList<String> pos;
	private int N;// 输出的结果个数
	private ArrayList<String> resultList;

	/*
	 * 利用CKY得到句子的解析结构
	 * 
	 * @param 分词结果，词性标注以及带概率的乔姆斯基范式
	 * 
	 * @return 输出K个句子解析结果
	 */
	public ArrayList<String> CreatParseByPOS(ArrayList<String> words, ArrayList<String> POS, PCFG pcnf, int N)
			throws IOException
	{
		this.pos = POS;
		this.words = words;
		this.pcnf = pcnf;
		this.N = N;
		return CKYParse();
	}

	/*
	 * 利用CKY得到句子的解析结构
	 * 
	 * @param 分词结果，带概率的乔姆斯基范式
	 * 
	 * @return 输出K个句子解析结果
	 */
	public ArrayList<String> CreatParseByWord(ArrayList<String> words, PCFG pcnf, int N) throws IOException
	{
		this.pcnf = pcnf;
		this.words = words;
		this.pos = null;
		this.N = N;
		return CKYParse();
	}

	/*
	 * CKY算法的具体函数
	 * 
	 * @return 输出k个句子解析结果
	 */
	private ArrayList<String> CKYParse()
	{
		int n = words.size();
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
			// 对角线上的点初始化
			/*
			 * 由分词结果反推得到规则，并进行table表对角线的初始化
			 */
			if (pos == null)
			{
				ArrayList<String> rhs = new ArrayList<String>();
				rhs.add(words.get(j - 1));
				Set<PRule> ruleSet = pcnf.getPRuleByrhs(rhs);
				HashMap<String, RewriteRule> map = table[j - 1][j].getPruleMap();
				for (PRule rule : ruleSet)
				{
					map.put(rule.getLhs(), rule);
				}
			}
			else
			{
				// 根据分词和词性标注的结果进行table表对角线的初始化
				table[j - 1][j].getPruleMap().put(pos.get(j - 1), new PRule(1.0, pos.get(j - 1), words.get(j - 1)));
			}
			if (j <= 1)
			{
				continue;
			}
			for (int i = j - 2; i >= 0; i--)
			{// 从第j-2行开始，由下到上
				for (int k = i + 1; k <= j - 1; k++)
				{// 遍历table[i][k]和table[k][j]中的映射表，更新table[i][j]和back[i][j]
					updateTableAndBack(i, k, j);
				}
			}
		}
		// 回溯并生成括号表达式列表
		CreatBracketStringList(n);
		return resultList;
	}

	/*
	 * 更新table表和Back表
	 */
	private void updateTableAndBack(int i, int k, int j)
	{
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
							PRule prule = (PRule) itr.next();
							PRule oldPRule = (PRule) table[i][j].getPruleMap().get(prule.getLhs());
							prule = new PRule(prule.getProOfRule() * ikPro * kjPro, prule.getLhs(), prule.getRhs());
							if ((oldPRule != null && prule.getProOfRule() > oldPRule.getProOfRule())
									|| oldPRule == null)
							{
								table[i][j].getPruleMap().put(prule.getLhs(), prule);
								table[i][j].setFlag(false);
								back[i][j].getBackMap().put(prule.getLhs(), k);
							}
						}
					}
				}
			}
		}
	}

	/*
	 * 生成括号表达式的列表
	 */
	private void CreatBracketStringList(int n)
	{
		ArrayList<PRule> resultRuleList = new PCFG().getHighestProRuleFromMap(table[0][n].getPruleMap(), "DuIP", N);
		/* System.out.println(resultRuleList.toString()); */
		resultList = new ArrayList<String>();
		for (PRule prule : resultRuleList)
		{
			StringBuilder strBuilder = new StringBuilder();
			// 从最后一个节点开始回溯
			int k = back[0][n].getBackMap().get(prule.getLhs());
			strBuilder.append("(");
			strBuilder.append("ROOT");
			CreateStringBuilder(0, k, n, prule, strBuilder);
			strBuilder.append(")");
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

	/*
	 * 添加左右括号和终结符与非终结符，i记录右侧的非终结符序号
	 */
	private void AddString(int n, int m, PRule prule, int i, StringBuilder strBuilder)
	{
		if (back[n][m].getBackMap().size() == 0)
		{// 叶子结点
			/* strBuilder.append(" "); */
			strBuilder.append("(");
			strBuilder.append(prule.getRhs().get(i));// 词性标注
			strBuilder.append(" ");
			strBuilder.append(words.get(m - 1));// 词
			strBuilder.append(")");
		}
		else
		{
			int k1 = back[n][m].getBackMap().get(prule.getRhs().get(i));
			PRule prule1 = (PRule) table[n][m].getPruleMap().get(prule.getRhs().get(i));
			CreateStringBuilder(n, k1, m, prule1, strBuilder);
		}
	}

	/*
	 * 内部类,table存储类,记录在table[i][j]点中的映射规则表，以及用于判断是否为对角线上点的flag
	 */
	class CKYTreeNode
	{
		private HashMap<String, RewriteRule> pruleMap;
		// flag用来判断是否为终结符
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
			this.flag = flag;// 判断是否为终结符节点
		}

		public CKYTreeNode(HashMap<String, RewriteRule> pruleMap, boolean flag)
		{
			this.pruleMap = pruleMap;
			this.flag = flag;
		}
	}

	/*
	 * 内部类,back存储类,包含非终结符，以及对应该该非终结符的k值，将[i,j]分裂为[i,k][k,j]
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
