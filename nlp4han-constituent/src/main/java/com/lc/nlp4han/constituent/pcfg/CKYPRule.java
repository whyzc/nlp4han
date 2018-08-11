package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;

public class CKYPRule extends PRule
{
	private int k;// 记录CKY算法中table表中的某节点的分裂位置
	private int i;// 记录CKY算法中table表中的某节点的分裂的第一个rhs中对应非终结符的ArrayList的位置
	private int j;// 记录CKY算法中table表中的某节点的分裂的第二个rhs中对应非终结符的ArrayList的位置

	public CKYPRule()
	{
		super();
	}

	public CKYPRule(double pro, String lhs, ArrayList<String> rhs, int k, int i, int j)
	{
		super(pro, lhs, rhs);
		this.k = k;
		this.i = i;
		this.j = j;
	}

	public CKYPRule(double pro, String lhs, String rhs, int k, int i, int j)
	{
		super(pro, lhs, rhs);
		this.k = k;
		this.i = i;
		this.j = j;
	}

	public CKYPRule(PRule prule, int k, int i, int j)
	{
		super.setlhs(prule.getLhs());
		super.setRhs(prule.getRhs());
		super.setProOfRule(prule.getProOfRule());
		this.k = k;
		this.i = i;
		this.j = j;
	}

	public int getK()
	{
		return k;
	}

	public void setK(int k)
	{
		this.k = k;
	}

	public int getI()
	{
		return i;
	}

	public void setI(int i)
	{
		this.i = i;
	}

	public int getJ()
	{
		return j;
	}

	public void setJ(int j)
	{
		this.j = j;
	}

	/**
	 * 快速排序
	 */
	public static void SortCKYPRuleList(ArrayList<CKYPRule> pruleList, int low, int high)
	{
		if (low >= high)
		{
			return;
		}
		int first = low;
		int last = high;
		CKYPRule key = pruleList.get(first);/* 用规则表的第一个记录作为枢轴 */

		while (first < last)
		{
			while (first < last && pruleList.get(last).getProOfRule() <= key.getProOfRule())
			{
				--last;
			}

			pruleList.set(first, pruleList.get(last));/* 将比第一个小的移到低端 */

			while (first < last && pruleList.get(first).getProOfRule() >= key.getProOfRule())
			{
				++first;
			}

			pruleList.set(last, pruleList.get(first)); /* 将比第一个大的移到高端 */
		}
		pruleList.set(first, key);/* 枢轴记录到位 */
		SortCKYPRuleList(pruleList, low, first - 1);
		SortCKYPRuleList(pruleList, first + 1, high);
	}

	@Override
	public String toString()
	{
		return super.getLhs() + "->" + super.getRhs() + " k=" + k + " i=" + i + " j=" + j + " " + super.getProOfRule()
				+ "\n";
	}

}
