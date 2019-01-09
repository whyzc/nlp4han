package com.lc.nlp4han.srl.framenet;

import java.util.ArrayList;

/**
 * 识别过程的事件
 * @author qyl
 *
 */
public class Event4Recognition
{
	//特征
	//中心词
	private String headWord;
	//path,在句法解析树中，谓词到role的路径
	private ArrayList<String> path;
	//t,谓词/目标词
	private String predicate;
	
	//是否为role
	private boolean role;
	
	//概率值
	private boolean value;

	public String getHeadWord()
	{
		return headWord;
	}

	public void setHeadWord(String headWord)
	{
		this.headWord = headWord;
	}

	public ArrayList<String> getPath()
	{
		return path;
	}

	public void setPath(ArrayList<String> path)
	{
		this.path = path;
	}

	public String getPredicate()
	{
		return predicate;
	}

	public void setPredicate(String predicate)
	{
		this.predicate = predicate;
	}

	public boolean isRole()
	{
		return role;
	}

	public void setRole(boolean role)
	{
		this.role = role;
	}

	public boolean isValue()
	{
		return value;
	}

	public void setValue(boolean value)
	{
		this.value = value;
	}
}
