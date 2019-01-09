package com.lc.nlp4han.srl.framenet;

import java.util.ArrayList;

/**
 * Frame Element Group关于目标词的先验事件
 * @author qyl
 *
 */
public class Event4PriorFEG
{
	//Frame Element Group,框架元素组
	private ArrayList<String> list=new ArrayList<String>();
	
	//目标词
	private String tagertWord;

	
	public Event4PriorFEG()
	{
	}


	public Event4PriorFEG(ArrayList<String> list, String tagertWord)
	{
		this.list = list;
		this.tagertWord = tagertWord;
	}


	public ArrayList<String> getList()
	{
		return list;
	}


	public void setList(ArrayList<String> list)
	{
		this.list = list;
	}


	public String getTagertWord()
	{
		return tagertWord;
	}


	public void setTagertWord(String tagertWord)
	{
		this.tagertWord = tagertWord;
	}
	
    
}
