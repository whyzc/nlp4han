package com.lc.nlp4han.constituent.lex;

import java.util.ArrayList;

import com.lc.nlp4han.constituent.lex.ConstituentParseLexPCFG.LexNode;

public class BracketexpressionGet
{
	private StringBuilder strBuilder;
	private LexNode[][] chart;
	private int n;

	public BracketexpressionGet(StringBuilder strBuilder, LexNode[][] chart, int n)
	{
		this.strBuilder = strBuilder;
		this.chart = chart;
		this.n = n;
	}

	public ArrayList<String> bracketexpressionGet()
	{
		ArrayList<String> resultList = new ArrayList<String>();

		// 查找概率最大的n个结果
		Edge edge = getBestTop(chart[0][n]);
		if (edge == null)
		{// 如果没有Parse结果则直接返回
			return resultList;
		}

		StringBuilder strBuilder = new StringBuilder();
		getParseResultString(edge);// 从最后一个节点[0,n]开始回溯

		resultList.add(strBuilder.toString());

		return resultList;
	}

	private void getParseResultString(Edge edge)
	{
		if (edge.isStop())
		{
			strBuilder.append("(");
			if (edge.getStart() + 1 == edge.getEnd() && edge.getChildren().size() == 0)
			{// 对角线上的点则需要判断是否为pos->词
				strBuilder.append(" " + edge.getHeadWord());
			}
			strBuilder.append(edge.getLabel());
			if (edge.getChildren().size() == 1)
			{// 单元规则
				getParseResultString(edge.getChildren().get(0));
			}
			else
			{
				for (Edge edge1 : edge.getChildren())
				{
					getParseResultString(edge1);
				}
			}
			strBuilder.append(")");
		}
		else
		{// 若该edge两侧的stop不为true,无论有几个孩子都直接忽略
			for (Edge edge1 : edge.getChildren())
			{
				getParseResultString(edge1);
			}
		}
	}

	/**
	 * 从chart的（0，n）出获得概率最大的结果树的根
	 * 
	 * @param node
	 * @return
	 */
	private Edge getBestTop(LexNode node)
	{
		Edge bestEdge = new Edge();
		Distance distance = new Distance(true, false);
		for (Edge edge : node.getEdgeMap().keySet())
		{
			if (edge.getLabel().equals("ROOT") && edge.isStop() && edge.getLc().equals(distance)
					&& edge.equals(distance) && edge.getPro() > bestEdge.getPro())
			{
				bestEdge = edge;
			}
		}
		return bestEdge;
	}
}
