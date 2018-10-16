package com.lc.nlp4han.constituent.lex;

import java.util.ArrayList;
import java.util.Collections;

public class BracketexpressionGet
{
	private StringBuilder strBuilder;
	private LexNode[][] chart;
	private int n;

	public BracketexpressionGet(LexNode[][] chart, int n)
	{
		this.chart = chart;
		this.n = n;
	}

	public ArrayList<String> bracketexpressionGet()
	{
		ArrayList<String> resultList = new ArrayList<String>();

		// 查找概率最大的n个可行结果
		Edge edge = getBestTop(chart[0][n]);
		if (edge.getPro() == -1)
		{// 如果没有Parse结果则直接返回
			return resultList;
		}
		strBuilder = new StringBuilder();
		getParseResultString(edge);// 从最后一个节点[0,n]开始回溯

		resultList.add(strBuilder.toString());

		return resultList;
	}

	private void getParseResultString(Edge edge)
	{
		if (edge.isStop())
		{
			strBuilder.append("(");
			
			//将NPB还原为NP
			String label=edge.getLabel();
			if(label.equals("NPB")) {
				label="NP";
			}
			strBuilder.append(label);
			
			if (edge.getStart() + 1 == edge.getEnd() && edge.getChildren() == null)
			{// 对角线上的点而且为pos->词，添加词汇
				strBuilder.append(" " + edge.getHeadWord());
				strBuilder.append(")");
				return;
			}

			if (edge.getChildren().size() == 1)
			{// 单元规则
				getParseResultString(edge.getChildren().get(0));
			}
			else
			{
				Collections.sort(edge.getChildren());
				for (Edge edge1 : edge.getChildren())
				{
					getParseResultString(edge1);
				}
			}
			strBuilder.append(")");
		}
		else
		{// 若该edge两侧的stop为false,无论有几个孩子都直接忽略
			Collections.sort(edge.getChildren());
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
					&& edge.getRc().equals(distance) && edge.getPro() > bestEdge.getPro())
			{
				bestEdge = edge;
			}
		}
		return bestEdge;
	}
}
