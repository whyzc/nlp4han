package com.lc.nlp4han.constituent.lex;

/**
 * 在模型中描述某实体类的数目和种类
 * 
 * @author qyl
 *
 */
public class AmountAndSort
{
	private int amount = 0;
	private int sort = 0;

	public void addAmount(int n)
	{
		this.amount += n;
	}

	public void addSort(int n)
	{
		this.sort += n;
	}

	public AmountAndSort(int amount, int sort)
	{
		this.amount = amount;
		this.sort = sort;
	}

	public int getAmount()
	{
		return amount;
	}

	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	public int getSort()
	{
		return sort;
	}

	public void setSort(int sort)
	{
		this.sort = sort;
	}

	@Override
	public String toString()
	{
		return amount + " " + sort;
	}

}
