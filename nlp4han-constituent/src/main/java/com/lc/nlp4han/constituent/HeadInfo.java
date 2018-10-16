package com.lc.nlp4han.constituent;

/**
 * 头结点信息类
 *
 */
public class HeadInfo
{
	private String headWord;
	private String headPos;
	
	private int index;
	
	public HeadInfo()
	{
		
	}

	public HeadInfo(String headWord, String headPos, int index)
	{
		super();
		this.headWord = headWord;
		this.headPos = headPos;
		this.index = index;
	}

	public HeadInfo(String headWord, String headPos)
	{
		super();
		this.headWord = headWord;
		this.headPos = headPos;
	}

	public String getHeadWord()
	{
		return headWord;
	}

	public void setHeadWord(String headWord)
	{
		this.headWord = headWord;
	}

	public String getHeadPos()
	{
		return headPos;
	}

	public void setHeadPos(String headPos)
	{
		this.headPos = headPos;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	@Override
	public String toString()
	{
		return "HeadInfo [headWord=" + headWord + ", headPos=" + headPos + ", index=" + index + "]";
	}	
	
}
