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

	private BackNode[][] back = null;

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
		initializeChart(words, poses);
		int n = words.length;
		for (int span = 2; span < words.length; span++)
		{
			for (int i = 1; i < n - span + 1; i++)
			{
				int j = i + span - 1;
				fillEdgeOfChart(i, j);
			}
		}
		return null;
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
		back = new BackNode[n + 1][n + 1];
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
					Edge edge = new Edge(poses[i], null, words[i], poses[i], i + 1, i + 1, lc, rc, true, 1.0, null);
					chart[i][j].setFlag(true);
					addSinglesAndStops(i, j);
					chart[i][j].getEdgeMap().put(edge, 1.0);
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
			for (Edge edge : map.keySet())
			{
				addSingle(edge);
				addStop(edge);
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
		for (String str : parentSet)
		{
			ArrayList<Edge> children = new ArrayList<Edge>();
			children.add(edge);
			Distance lc = new Distance(true, false);
			Distance rc = new Distance(true, false);
			int start = edge.getStart();
			int end = edge.getEnd();
			double pro = lexpcfg.getGeneratePro(rhcg, "head");
			Edge e1 = new Edge(str, edge.getLabel(), edge.getHeadWord(), edge.getHeadPOS(), start, end, lc, rc, false,
					pro, children);
			addEdge(e1, start, end);
		}
	}

	/**
	 * 添加新的边
	 * 
	 * @param edge
	 * @param start
	 * @param end
	 */
	private boolean addEdge(Edge edge, int start, int end)
	{
		if (chart[start][end].getEdgeMap().get(edge) < edge.getPro())
		{
			chart[start][end].getEdgeMap().put(edge, edge.getPro());
			return true;
		}
		return false;

	}

	/**
	 * 在规则两侧添加Stop符号
	 * 
	 * @param edge
	 */
	private void addStop(Edge edge)
	{
		RuleStopGenerate rsg1 = new RuleStopGenerate(edge.getHeadLabel(), edge.getLabel(), edge.getHeadPOS(),
				edge.getHeadWord(), 1, true, edge.getLc());
		RuleStopGenerate rsg2 = new RuleStopGenerate(edge.getHeadLabel(), edge.getLabel(), edge.getHeadPOS(),
				edge.getHeadWord(), 2, true, edge.getRc());
		double pro = edge.getPro() * lexpcfg.getGeneratePro(rsg1, "stop") * lexpcfg.getGeneratePro(rsg2, "stop");
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
		for (int split = i; split < j; split++)
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
		int index;
		if (direction == 2)
		{
			lc = e2.getLc();
			boolean rcVerb = (e1.getRc().isCrossVerb() || e2.getLc().isCrossVerb() || e2.getRc().isCrossVerb()
					|| verbs.contains(e2.getHeadPOS()));
			rc = new Distance(false, rcVerb);
			children.addAll(e1.getChildren());
			index = e1.getChildren().size();
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
			index = 0;
			rsg = new RuleSidesGenerate(e2.getHeadLabel(), e2.getLabel(), e2.getHeadPOS(), e2.getHeadWord(), direction,
					e1.getLabel(), e1.getHeadPOS(), e1.getHeadWord(), 0, 0, rc);
			pro += lexpcfg.getGeneratePro(rsg, "sides");
			edge = new Edge(e2.getLabel(), e2.getHeadLabel(), e2.getHeadWord(), e2.getHeadPOS(), e1.getStart(),
					e2.getEnd(), lc, rc, false, pro, children);
		}
		if (addEdge(edge, edge.getStart(), edge.getEnd()))
		{
			back[e1.start][e2.getEnd()].getMap().put(edge, new BackInfor(direction, e1.getEnd(), index));
		}
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

	class Edge
	{
		private String label;
		private String headLabel;
		private String headWord;
		private String headPOS;
		private int start;
		private int end;
		private Distance lc;
		private Distance rc;
		private boolean stop;
		private double pro;
		private ArrayList<Edge> children;

		public String getLabel()
		{
			return label;
		}

		public void setLabel(String label)
		{
			this.label = label;
		}

		public String getHeadLabel()
		{
			return headLabel;
		}

		public void setHeadLabel(String headLabel)
		{
			this.headLabel = headLabel;
		}

		public String getHeadWord()
		{
			return headWord;
		}

		public void setHeadWord(String headWord)
		{
			this.headWord = headWord;
		}

		public String getHeadPOS()
		{
			return headPOS;
		}

		public void setHeadPOS(String headPOS)
		{
			this.headPOS = headPOS;
		}

		public int getStart()
		{
			return start;
		}

		public void setStart(int start)
		{
			this.start = start;
		}

		public int getEnd()
		{
			return end;
		}

		public void setEnd(int end)
		{
			this.end = end;
		}

		public Distance getLc()
		{
			return lc;
		}

		public void setLc(Distance lc)
		{
			this.lc = lc;
		}

		public Distance getRc()
		{
			return rc;
		}

		public void setRc(Distance rc)
		{
			this.rc = rc;
		}

		public boolean isStop()
		{
			return stop;
		}

		public void setStop(boolean stop)
		{
			this.stop = stop;
		}

		public double getPro()
		{
			return pro;
		}

		public void setPro(double pro)
		{
			this.pro = pro;
		}

		public ArrayList<Edge> getChildren()
		{
			return children;
		}

		public void setChildren(ArrayList<Edge> children)
		{
			this.children = children;
		}

		public Edge()
		{
		}

		public Edge(String label, String headLabel, String headWord, String headPOS, int start, int end, Distance lc,
				Distance rc, boolean stop, double pro, ArrayList<Edge> children)
		{
			this.label = label;
			this.headLabel = headLabel;
			this.headWord = headWord;
			this.headPOS = headPOS;
			this.start = start;
			this.end = end;
			this.lc = lc;
			this.rc = rc;
			this.stop = stop;
			this.pro = pro;
			this.children = children;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((headLabel == null) ? 0 : headLabel.hashCode());
			result = prime * result + ((headPOS == null) ? 0 : headPOS.hashCode());
			result = prime * result + ((headWord == null) ? 0 : headWord.hashCode());
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result + ((lc == null) ? 0 : lc.hashCode());
			result = prime * result + ((rc == null) ? 0 : rc.hashCode());
			result = prime * result + (stop ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Edge other = (Edge) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (headLabel == null)
			{
				if (other.headLabel != null)
					return false;
			}
			else if (!headLabel.equals(other.headLabel))
				return false;
			if (headPOS == null)
			{
				if (other.headPOS != null)
					return false;
			}
			else if (!headPOS.equals(other.headPOS))
				return false;
			if (headWord == null)
			{
				if (other.headWord != null)
					return false;
			}
			else if (!headWord.equals(other.headWord))
				return false;
			if (label == null)
			{
				if (other.label != null)
					return false;
			}
			else if (!label.equals(other.label))
				return false;
			if (lc == null)
			{
				if (other.lc != null)
					return false;
			}
			else if (!lc.equals(other.lc))
				return false;
			if (rc == null)
			{
				if (other.rc != null)
					return false;
			}
			else if (!rc.equals(other.rc))
				return false;
			if (stop != other.stop)
				return false;
			return true;
		}

		private ConstituentParseLexPCFG getOuterType()
		{
			return ConstituentParseLexPCFG.this;
		}
	}

	class BackNode
	{
		private HashMap<Edge, BackInfor> map;

		public BackNode()
		{
		}

		public BackNode(HashMap<Edge, BackInfor> map)
		{
			this.map = map;
		}

		public HashMap<Edge, BackInfor> getMap()
		{
			return map;
		}

		public void setMap(HashMap<Edge, BackInfor> map)
		{
			this.map = map;
		}
	}

	class BackInfor
	{
		private int direction;// 分裂方向
		private int split;// 分裂节点
		private int index;// 在此节点时被合并的两侧孩子的索引值

		public BackInfor(int direction, int split, int index)
		{
			this.direction = direction;
			this.split = split;
			this.index = index;
		}

		public int getDirection()
		{
			return direction;
		}

		public void setDirection(int direction)
		{
			this.direction = direction;
		}

		public int getSplit()
		{
			return split;
		}

		public void setSplit(int split)
		{
			this.split = split;
		}

		public int getIndex()
		{
			return index;
		}

		public void setIndex(int index)
		{
			this.index = index;
		}
	}
}
