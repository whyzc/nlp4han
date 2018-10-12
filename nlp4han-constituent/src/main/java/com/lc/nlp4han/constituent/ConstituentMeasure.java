package com.lc.nlp4han.constituent;

import java.util.List;

/**
 * 短语结构树评价指标计算
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ConstituentMeasure
{

	private long notDecodeTreeCount = 0;// 统计不能解析成一颗完整的树的个数
	private long selected;
	private long target;
	private long truePositive;
	private long sentences;
	private long trueSentence;
	private long CBs_0;
	private long CBs_2;
	private long CBs;

	/**
	 * 统计不能解析成一颗完整的树的个数
	 * 
	 * @param buildAndCheckTree
	 *            完整的树
	 */
	public void countNodeDecodeTrees(TreeNode buildAndCheckTree)
	{
		if (buildAndCheckTree == null)
		{
			notDecodeTreeCount++;
		}
	}

	/**
	 * 更新指标的计数
	 * 
	 * @param treeRef
	 *            参考的树
	 * @param treePre
	 *            预测的树
	 * @throws CloneNotSupportedException
	 */
	public void update(TreeNode treeRef, TreeNode treePre) throws CloneNotSupportedException
	{
		List<EvalStructure> etRef = EvalStructure.getEvalStructures(treeRef);
		List<EvalStructure> etPre = EvalStructure.getEvalStructures(treePre);
		int trueSentencetemp = 0;
		int CBs_2_temp = 0;
		for (int j = 0; j < etPre.size(); j++)
		{
			if (etRef.contains(etPre.get(j)))
			{
				truePositive++;
				trueSentencetemp++;
			}
		}

		for (int i = 0; i < etRef.size(); i++)
		{
			for (int j = 0; j < etPre.size(); j++)
			{
				// 判断是否是交叉结构
				if (etRef.get(i).getBegin() < etPre.get(j).getBegin()
						&& etRef.get(i).getEnd() > etPre.get(j).getBegin())
				{
					if (etPre.get(j).getEnd() > etRef.get(i).getEnd())
					{
						CBs_2_temp++;
						CBs++;
					}
				}
			}
		}

		if (trueSentencetemp == etPre.size())
		{
			trueSentence++;
		}
		
		if (CBs_2_temp == 0)
		{
			CBs_0++;
		}
		
		if (CBs_2_temp <= 2)
		{
			CBs_2++;
		}
		
		selected += etPre.size();
		target += etRef.size();
		
		sentences++;
	}

	@Override
	public String toString()
	{
		return "不能解析的树的个数：" + notDecodeTreeCount + "\n" + "Precision: " + Double.toString(getPrecisionScore()) + "\n"
				+ "Recall: " + Double.toString(getRecallScore()) + "\n" + "F-Measure: " + Double.toString(getMeasure())
				+ "\n" + "CBs:" + Double.toString(getCBs()) + "\n" + "CBs_0:" + Double.toString(getCBs_0()) + "\n"
				+ "CBs_2:" + Double.toString(getCBs_2()) + "\n" + "sentenceAccuracy:"
				+ Double.toString(getSentenceAccuracy()) + "\n";
	}

	/**
	 * 精确率
	 * 
	 * @return
	 */
	public double getPrecisionScore()
	{
		return selected > 0 ? (double) truePositive / (double) selected : 0;
	}

	/**
	 * 召回率
	 * 
	 * @return
	 */
	public double getRecallScore()
	{
		return target > 0 ? (double) truePositive / (double) target : 0;
	}

	/**
	 * 交叉括号数
	 * 
	 * @return
	 */
	public double getCBs()
	{
		return sentences > 0 ? (double) CBs / (double) sentences : 0;
	}

	/**
	 * 0交叉括号数的句子占测试集句子总数的比例
	 * 
	 * @return
	 */
	public double getCBs_0()
	{
		return sentences > 0 ? (double) CBs_0 / (double) sentences : 0;
	}

	/**
	 * 交叉括号数小于等于2的句子占测试集句子总数的比例
	 * 
	 * @return
	 */
	public double getCBs_2()
	{
		return sentences > 0 ? (double) CBs_2 / (double) sentences : 0;
	}

	/**
	 * 句子正确率
	 * 
	 * @return
	 */
	public double getSentenceAccuracy()
	{
		return trueSentence > 0 ? (double) trueSentence / (double) sentences : 0;
	}

	/**
	 * F值
	 * 
	 * @return
	 */
	public double getMeasure()
	{
		if (getPrecisionScore() + getRecallScore() > 0)
		{
			return 2 * (getPrecisionScore() * getRecallScore()) / (getPrecisionScore() + getRecallScore());
		}
		else
		{
			return -1;
		}
	}
}
