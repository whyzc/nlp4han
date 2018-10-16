package com.lc.nlp4han.constituent.lex;

/**
 * 虽然在处理基本名词短语与一般的生成stop方式不同，但是其数据形式相同
 * 
 * @author qyl
 *
 */
public class RuleStopGenerate extends RuleHeadChildGenerate
{
	private int direction = 0;// 终止符生成方向,左侧为1，右侧为2
	private boolean stop;
	private Distance distance = null;

	public RuleStopGenerate(String[] strs)
	{
		super(strs);
		this.direction = Integer.parseInt(strs[4]);
		this.stop = Boolean.parseBoolean(strs[5]);
		this.distance = new Distance(Boolean.parseBoolean(strs[6]), Boolean.parseBoolean(strs[7]));
	}

	public RuleStopGenerate(String headLabel, String parentLabel, String headPOS, String headWord, int direction,
			boolean stop, Distance distance)
	{
		super(headLabel, parentLabel, headPOS, headWord);
		this.direction = direction;
		this.stop = stop;
		this.distance = distance;
	}

	public int getDirection()
	{
		return direction;
	}

	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	public boolean isStop()
	{
		return stop;
	}

	public void setStop(boolean stop)
	{
		this.stop = stop;
	}

	public Distance getDistance()
	{
		return distance;
	}

	public void setDistance(Distance distance)
	{
		this.distance = distance;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + direction;
		result = prime * result + ((distance == null) ? 0 : distance.hashCode());
		result = prime * result + (stop ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleStopGenerate other = (RuleStopGenerate) obj;
		if (direction != other.direction)
			return false;
		if (distance == null)
		{
			if (other.distance != null)
				return false;
		}
		else if (!distance.equals(other.distance))
			return false;
		if (stop != other.stop)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return super.toString() + " " + direction + " " + stop + " " + distance;
	}

}
