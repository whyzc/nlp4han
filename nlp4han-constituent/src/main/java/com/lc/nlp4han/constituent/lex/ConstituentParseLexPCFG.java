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
			TreeNode rootNode = BracketExpUtil.generateTree(bracketString);
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
		// 初始化
		initializeChart(words, poses);
		int n = words.length;

		// 填充chart图中的边
		for (int span = 2; span <= words.length; span++)
		{
			for (int i = 0; i <= n - span; i++)
			{
				int j = i + span;
				fillEdgeOfChart(i, j);
			}
		}
		// 调试时查看具体的分析过程
		/*
		 * for (int i = 0; i < words.length; i++) { for (int j = 1; j <= words.length;
		 * j++) { if (j >= i + 1) { if(chart[i][j].getEdgeMap().keySet().size()==0) {
		 * System.out.println("x=" + i + " y" + j+" 处没有边"); } System.out.println("x=" +
		 * i + " y" + j); for (Edge edge : chart[i][j].getEdgeMap().keySet()) {
		 * System.out.println(edge.toString()); } } } }
		 */
		/*
		 * for (Edge edge : chart[0][24].getEdgeMap().keySet()) {
		 * System.out.println("0-24: "+edge.toString()); }
		 */
		return new BracketexpressionGet(chart, words.length, k).bracketexpressionGet();
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
		// 为避免迭代器的冲突，添加一个临时的边的列表
		ArrayList<Edge> tempEdgeList = new ArrayList<Edge>();
		HashMap<Edge, Double> map = chart[i][j].getEdgeMap();
		for (Edge edge : map.keySet())
		{
			if (!edge.isStop())
			{
				addStop(edge, tempEdgeList);
			}
		}
		// 将成功添加stop的边添加进映射表中，并清除临时表中的数据
		addNewEdge(map, tempEdgeList);

		// 添加单元规则
		for (i = 1; i < 3; i++)
		{
			for (Edge edge : map.keySet())
			{
				if (edge.isStop())
				{
					addSingle(edge, tempEdgeList);
				}
			}
			addNewEdge(map, tempEdgeList);
		}

	}

	/**
	 * 将新生成的边添加到对应点的映射表中
	 * 
	 * @param map
	 * @param tempEdgeList
	 */
	private void addNewEdge(HashMap<Edge, Double> map, ArrayList<Edge> tempEdgeList)
	{
		for (Edge edge : tempEdgeList)
		{
            addEdge(edge,edge.getStart(),edge.getEnd(),null);
		}
		tempEdgeList.clear();
	}

	/**
	 * 添加单元规则
	 * 
	 * @param edge
	 */
	private void addSingle(Edge edge, ArrayList<Edge> tempEdgeList)
	{
		RuleHeadChildGenerate rhcg = new RuleHeadChildGenerate(edge.getLabel(), null, edge.getHeadPOS(), null);
		// RuleHeadChildGenerate rhcg = new RuleHeadChildGenerate(edge.getLabel(), null,
		// null, null);
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

			addEdge(e1, start, end, tempEdgeList);
			addStop(e1, tempEdgeList);
		}
	}

	/**
	 * 添加新的边
	 * 
	 * @param edge
	 * @param start
	 * @param end
	 */
	private void addEdge(Edge edge, int start, int end, ArrayList<Edge> tempEdgeList)
	{
		if (chart[start][end].getEdgeMap().containsKey(edge)
				&& chart[start][end].getEdgeMap().get(edge) > edge.getPro())
		{
			return;
		}
		else
		{
			if (tempEdgeList != null)
			{
				tempEdgeList.add(edge);
			}
			else
			{
				chart[start][end].getEdgeMap().remove(edge);
				chart[start][end].getEdgeMap().put(edge, edge.getPro());
			}
		}
	}

	/**
	 * 在规则两侧添加Stop符号
	 * 
	 * @param edge
	 */
	private void addStop(Edge edge, ArrayList<Edge> tempEdgeList)
	{
		// 分别初始化两侧的stop规则
		RuleStopGenerate rsg1 = new RuleStopGenerate(edge.getHeadLabel(), edge.getLabel(), edge.getHeadPOS(),
				edge.getHeadWord(), 1, true, edge.getLc());
		RuleStopGenerate rsg2 = new RuleStopGenerate(edge.getHeadLabel(), edge.getLabel(), edge.getHeadPOS(),
				edge.getHeadWord(), 2, true, edge.getRc());

		if (edge.getLabel().equals("NPB"))
		{
			Edge edge1 = edge.getFirstChild();
			Edge edge2 = edge.getLastChild();
			rsg1 = new RuleStopGenerate(edge1.getLabel(), edge.getLabel(), edge1.getHeadPOS(), edge1.getHeadWord(), 1,
					true, new Distance());
			rsg2 = new RuleStopGenerate(edge2.getLabel(), edge.getLabel(), edge2.getHeadPOS(), edge.getHeadWord(), 2,
					true, new Distance());
		}
		// 将原始概率与两侧规则的概率相乘得到新的概率
		double pro = edge.getPro() * lexpcfg.getGeneratePro(rsg1, "stop") * lexpcfg.getGeneratePro(rsg2, "stop");
		// 如果概率为零则不添加
		if (pro == 0.0)
		{
			return;
		}

		Edge e1 = new Edge(edge.getLabel(), edge.getHeadLabel(), edge.getHeadWord(), edge.getHeadPOS(), edge.getStart(),
				edge.getEnd(), edge.getLc(), edge.getRc(), true, pro, edge.getChildren());
		addEdge(e1, e1.getStart(), e1.getEnd(), tempEdgeList);
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
		for (int split = i + 1; split < j; split++)
		{
			HashMap<Edge, Double> map1 = chart[i][split].getEdgeMap();
			HashMap<Edge, Double> map2 = chart[split][j].getEdgeMap();
			if (map1.size() != 0 && map2.size() != 0)
			{
				ArrayList<Edge> tempEdgeList = new ArrayList<Edge>();
				for (Edge edge1 : map1.keySet())
				{
					for (Edge edge2 : map2.keySet())
					{
						if (!edge1.isStop() && edge2.isStop())
						{
							mergeEdge(edge1, edge2, 2, tempEdgeList);
						}
						else if (edge1.isStop() && !edge2.isStop())
						{
							mergeEdge(edge1, edge2, 1, tempEdgeList);
						}
					}
				}
				for (Edge edge : tempEdgeList)
				{
					addEdge(edge, i, j, null);
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
	private void mergeEdge(Edge e1, Edge e2, int direction, ArrayList<Edge> tempEdgeList)
	{
		// 动词集合
		String[] verbArray = { "VA", "VC", "VE", "VV", "BA", "LB" };
		HashSet<String> verbs = new HashSet<String>();
		for (String verb : verbArray)
		{
			verbs.add(verb);
		}

		Distance lc, rc;
		ArrayList<Edge> children = new ArrayList<Edge>();

		RuleSidesGenerate rsg;
		Edge edge;

		double pro = e1.getPro() * e2.getPro();

		if (direction == 2)// 若添加head右侧的孩子
		{
			// 此刻的距离
			lc = e1.getLc();
			boolean rcVerb = (e1.getRc().isCrossVerb() || e2.getLc().isCrossVerb() || e2.getRc().isCrossVerb()
					|| verbs.contains(e2.getHeadPOS()));
			rc = new Distance(false, rcVerb);
			// 为新的边添加孩子
			children.addAll(e1.getChildren());
			children.add(e2);

			// 获得概率
			rsg = new RuleSidesGenerate(e1.getHeadLabel(), e1.getLabel(), e1.getHeadPOS(), e1.getHeadWord(), direction,
					e2.getLabel(), e2.getHeadPOS(), e2.getHeadWord(), 0, 0, e1.getRc());

			// 若为NPB,则其概率计算方式不变，但是生成规则中的headChild变为前一个孩子
			if (e1.getLabel().equals("NPB"))
			{
				Edge lastChild = e1.getLastChild();
				rsg = new RuleSidesGenerate(lastChild.getLabel(), e1.getLabel(), lastChild.getHeadPOS(),
						lastChild.getHeadWord(), direction, e2.getLabel(), e2.getHeadPOS(), e2.getHeadWord(), 0, 0,
						new Distance());
			}
			pro = pro * lexpcfg.getGeneratePro(rsg, "sides");

			edge = new Edge(e1.getLabel(), e1.getHeadLabel(), e1.getHeadWord(), e1.getHeadPOS(), e1.getStart(),
					e2.getEnd(), lc, rc, false, pro, children);
		}
		else
		{
			rc = e2.getRc();
			boolean lcVerb = (e1.getLc().isCrossVerb() || e1.getRc().isCrossVerb() || e2.getLc().isCrossVerb()
					|| verbs.contains(e1.getHeadPOS()));
			lc = new Distance(false, lcVerb);

			children.add(e1);
			children.addAll(e2.getChildren());

			rsg = new RuleSidesGenerate(e2.getHeadLabel(), e2.getLabel(), e2.getHeadPOS(), e2.getHeadWord(), direction,
					e1.getLabel(), e1.getHeadPOS(), e1.getHeadWord(), 0, 0, e2.getLc());

			// 若为NPB,则其概率计算方式不变，但是生成规则中的headChild变为前一个孩子
			if (e2.getLabel().equals("NPB"))
			{
				Edge firstChild = e2.getFirstChild();
				rsg = new RuleSidesGenerate(firstChild.getLabel(), e2.getLabel(), firstChild.getHeadPOS(),
						firstChild.getHeadWord(), direction, e1.getLabel(), e1.getHeadPOS(), e1.getHeadWord(), 0, 0,
						new Distance());
			}
			pro = pro * lexpcfg.getGeneratePro(rsg, "sides");

			edge = new Edge(e2.getLabel(), e2.getHeadLabel(), e2.getHeadWord(), e2.getHeadPOS(), e1.getStart(),
					e2.getEnd(), lc, rc, false, pro, children);
		}

		if (pro == 0.0)
		{
			return;
		}
		addEdge(edge, edge.getStart(), edge.getEnd(), tempEdgeList);
	}
}
