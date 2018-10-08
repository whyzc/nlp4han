package com.lc.nlp4han.constituent.lex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;

public class ConstituentParseLexPCFG implements ConstituentParser
{
	private LexNode[][] chart = null;
	private LexPCFG lexpcfg = null;
	private ArrayList<Edge> tempEdgeList = new ArrayList<Edge>();

	public ConstituentParseLexPCFG(LexPCFG lexpcfg)
	{
		this.lexpcfg = lexpcfg;
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
	 * 获取成分树的通用方法
	 * 
	 * @param words
	 * @param poses
	 * @param k
	 * @return
	 */
	private ConstituentTree[] getParseResult(String[] words, String[] poses, int k)
	{
		ConstituentTree[] treeArray = new ConstituentTree[k];
		ArrayList<String> bracketList = parseLex(words, poses, k);
		int i = 0;
		for (String bracketString : bracketList)
		{
			TreeNode rootNode = BracketExpUtil.generateTreeNotDeleteBracket(bracketString);
			treeArray[i++] = new ConstituentTree(rootNode);
		}
		return treeArray;
	}

	/**
	 * 剖析的具体过程
	 * 
	 * @param words
	 * @param poses
	 * @param k
	 * @return
	 */
	private ArrayList<String> parseLex(String[] words, String[] poses, int k)
	{
		//初始化
		initializeChart(words, poses);
		int n = words.length;
        
		//填充chart图中的边
		for (int span = 2; span < words.length; span++)
		{
			for (int i = 0; i < n - span; i++)
			{
				int j = i + span - 1;
				fillEdgeOfChart(i, j);
			}
		}

		/*
		 * for (int i = 0; i < n; i++) { for (int j = 1; j <= n; j++) { if (j >= i + 1)
		 * { for (Edge edge : chart[i][j].getEdgeMap().keySet()) {
		 * System.out.println(edge.toString()); } } } }
		 */
		return new BracketexpressionGet(chart, words.length).bracketexpressionGet();
	}

	/**
	 * 初始化chart，并将对应的内容填充对角线节点中
	 * 
	 * @param words
	 * @param poses
	 */
	private void initializeChart(String[] words, String[] poses)
	{
		int n = words.length;
		chart = new LexNode[n + 1][n + 1];
		for (int i = 0; i < n; i++)
		{
			for (int j = 1; j <= n; j++)
			{
				if (j >= i + 1)
				{
					chart[i][j] = new LexNode(false, new HashMap<Edge, Double>());
				}
				if (j == i + 1)
				{
					Distance lc = new Distance(true, false);
					Distance rc = new Distance(true, false);
					Edge edge = new Edge(poses[i], null, words[i], poses[i], i, i + 1, lc, rc, true, 1.0, null);
					chart[i][j].setFlag(true);
					chart[i][j].getEdgeMap().put(edge, 1.0);
					addSinglesAndStops(i, j);
				}
			}
		}
	}

	/**
	 * 添加单元规则和Stop符号
	 * 
	 * @param i
	 * @param j
	 */
	private void addSinglesAndStops(int i, int j)
	{
		HashMap<Edge, Double> map = chart[i][j].getEdgeMap();
		for (Edge edge : map.keySet())
		{
			if (!edge.isStop())
			{
				addStop(edge);
			}
		}
		// 添加单元规则
		for (i = 1; i < 4; i++)
		{
			for (Edge edge : tempEdgeList)
			{
				map.put(edge, edge.getPro());
			}
			tempEdgeList.removeAll(tempEdgeList);
			for (Edge edge : map.keySet())
			{
				if (edge.isStop())
				{
					addSingle(edge);
				}
			}
		}
	}

	/**
	 * 添加单元规则
	 * 
	 * @param edge
	 */
	private void addSingle(Edge edge)
	{
		RuleHeadChildGenerate rhcg = new RuleHeadChildGenerate(edge.getLabel(), null, edge.getHeadPOS(),
				edge.getHeadWord());
		HashSet<String> parentSet = lexpcfg.getParentSet(rhcg);
		if (parentSet == null)
		{// 若没有可以向上延伸的则直接返回
			return;
		}
		for (String str : parentSet)
		{
			ArrayList<Edge> children = new ArrayList<Edge>();
			children.add(edge);
			Distance lc = new Distance(true, false);
			Distance rc = new Distance(true, false);
			int start = edge.getStart();
			int end = edge.getEnd();
			rhcg.setParentLabel(str);
			double pro = lexpcfg.getGeneratePro(rhcg, "head") * edge.getPro();
			Edge e1 = new Edge(str, edge.getLabel(), edge.getHeadWord(), edge.getHeadPOS(), start, end, lc, rc, false,
					pro, children);
			addEdge(e1, start, end);
			addStop(e1);
		}
	}

	/**
	 * 添加新的边
	 * 
	 * @param edge
	 * @param start
	 * @param end
	 */
	private void addEdge(Edge edge, int start, int end)
	{
		if (!chart[start][end].getEdgeMap().containsKey(edge)
				|| chart[start][end].getEdgeMap().get(edge) < edge.getPro())
		{
			tempEdgeList.add(edge);
		}
	}

	/**
	 * 在规则两侧添加Stop符号
	 * 
	 * @param edge
	 */
	private void addStop(Edge edge)
	{
		// 分别初始化两侧的stop规则
		RuleStopGenerate rsg1 = new RuleStopGenerate(edge.getHeadLabel(), edge.getLabel(), edge.getHeadPOS(),
				edge.getHeadWord(), 1, true, edge.getLc());
		RuleStopGenerate rsg2 = new RuleStopGenerate(edge.getHeadLabel(), edge.getLabel(), edge.getHeadPOS(),
				edge.getHeadWord(), 2, true, edge.getRc());

		// 将原始概率与两侧规则的概率相乘即可得
		double pro = edge.getPro() * lexpcfg.getGeneratePro(rsg1, "stop") * lexpcfg.getGeneratePro(rsg2, "stop");
		// 如果概率为零则不添加
		if (pro == 0)
		{
			return;
		}

		Edge e1 = new Edge(edge.getLabel(), edge.getHeadLabel(), edge.getHeadWord(), edge.getHeadPOS(), edge.getStart(),
				edge.getEnd(), edge.getLc(), edge.getRc(), true, pro, edge.getChildren());
		addEdge(e1, e1.getStart(), e1.getEnd());
	}

	/**
	 * 填充Chart中的边
	 * 
	 * @param i
	 *            横坐标值
	 * @param j
	 *            纵坐标值
	 */
	private void fillEdgeOfChart(int i, int j)
	{
		if ((j - i) <= 1)
		{// 矩阵中下三角不用合并两侧
			return;
		}
		for (int split = i + 1; split < j; split++)
		{
			HashMap<Edge, Double> map1 = chart[i][split].getEdgeMap();
			HashMap<Edge, Double> map2 = chart[split][j].getEdgeMap();
			if (map1.size() != 0 && map2.size() != 0)
			{
				for (Edge edge1 : map1.keySet())
				{
					for (Edge edge2 : map1.keySet())
					{
						if (edge1.isStop() == false && edge2.isStop() == true)
						{
							mergeEdge(edge1, edge2, 2);
						}
						else if (edge1.isStop() == true && edge2.isStop() == false)
						{
							mergeEdge(edge1, edge2, 1);
						}
					}
				}
			}
		}
		addSinglesAndStops(i, j);
	}

	/**
	 * 合并规则的两侧和中心child
	 * 
	 * @param e1
	 * @param e2
	 * @param direction
	 *            1为左侧，2为右侧
	 */
	private void mergeEdge(Edge e1, Edge e2, int direction)
	{
		String[] verbArray = { "VA", "VC", "VE", "VV", "BA", "LB" };
		HashSet<String> verbs = new HashSet<String>();
		for (String verb : verbArray)
		{
			verbs.add(verb);
		}
		Distance lc, rc;
		ArrayList<Edge> children = new ArrayList<Edge>();
		double pro = e1.getPro() + e2.getPro();
		RuleSidesGenerate rsg;
		Edge edge;
		if (direction == 2)
		{
			lc = e2.getLc();
			boolean rcVerb = (e1.getRc().isCrossVerb() || e2.getLc().isCrossVerb() || e2.getRc().isCrossVerb()
					|| verbs.contains(e2.getHeadPOS()));
			rc = new Distance(false, rcVerb);
			children.addAll(e1.getChildren());
			children.add(e2);
			rsg = new RuleSidesGenerate(e1.getHeadLabel(), e1.getLabel(), e1.getHeadPOS(), e1.getHeadWord(), direction,
					e2.getLabel(), e2.getHeadPOS(), e2.getHeadWord(), 0, 0, rc);
			pro += lexpcfg.getGeneratePro(rsg, "sides");
			edge = new Edge(e1.getLabel(), e1.getHeadLabel(), e1.getHeadWord(), e1.getHeadPOS(), e1.getStart(),
					e2.getEnd(), lc, rc, false, 0.0, children);
		}
		else
		{
			rc = e2.getRc();
			boolean lcVerb = (e1.getRc().isCrossVerb() || e2.getLc().isCrossVerb() || e2.getRc().isCrossVerb()
					|| verbs.contains(e2.getHeadPOS()));
			lc = new Distance(false, lcVerb);
			children.addAll(e2.getChildren());
			children.add(e1);
			rsg = new RuleSidesGenerate(e2.getHeadLabel(), e2.getLabel(), e2.getHeadPOS(), e2.getHeadWord(), direction,
					e1.getLabel(), e1.getHeadPOS(), e1.getHeadWord(), 0, 0, rc);
			pro += lexpcfg.getGeneratePro(rsg, "sides");
			edge = new Edge(e2.getLabel(), e2.getHeadLabel(), e2.getHeadWord(), e2.getHeadPOS(), e1.getStart(),
					e2.getEnd(), lc, rc, false, pro, children);
		}
		addEdge(edge, edge.getStart(), edge.getEnd());
	}

	class LexNode
	{
		private boolean flag;
		private HashMap<Edge, Double> edgeMap;

		public LexNode(boolean flag, HashMap<Edge, Double> edgeMap)
		{
			this.flag = flag;
			this.edgeMap = edgeMap;
		}

		public LexNode()
		{
		}

		public boolean isFlag()
		{
			return flag;
		}

		public void setFlag(boolean flag)
		{
			this.flag = flag;
		}

		public HashMap<Edge, Double> getEdgeMap()
		{
			return edgeMap;
		}

		public void setEdgeMap(HashMap<Edge, Double> edgeMap)
		{
			this.edgeMap = edgeMap;
		}
	}
}
