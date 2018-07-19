package com.lc.nlp4han.chunk;

import java.util.Arrays;
import java.util.List;

/**
 * 组块信息类
 */
public class Chunk
{

	private String type;

	private String[] tokens;

	private String str;

	private int start;

	private int end;

	public Chunk()
	{

	}

	public Chunk(String type, String token, int start, int end)
	{
		this(type, new String[] { token }, start, end);
	}

	public Chunk(String type, List<String> tokens, int start, int end)
	{
		this(type, tokens.toArray(new String[tokens.size()]), start, end);
	}

	public Chunk(String type, String[] tokens, int start, int end)
	{
		this.type = type;
		this.tokens = tokens;
		this.start = start;
		this.end = end;

		str = "";
		for (String token : tokens)
			str += token + "  ";
		str = str.trim();
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setWords(String[] tokens)
	{
		this.tokens = tokens;
	}

	public void setString(String string)
	{
		this.str = string;
	}

	public void setStart(int start)
	{
		this.start = start;
	}

	public void setEnd(int end)
	{
		this.end = end;
	}

	/**
	 * 返回组块类型
	 * 
	 * @return 组块类型
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * 或取组块中的词组
	 * 
	 * @return
	 */
	public String[] getTokens()
	{
		return tokens;
	}

	/**
	 * 返回组块字符串
	 * 
	 * @return 组块字符串
	 */
	public String getString()
	{
		return str;
	}

	/**
	 * 返回组块起始位置
	 * 
	 * @return 组块起始位置
	 */
	public int getStart()
	{
		return start;
	}

	/**
	 * 返回组块结束位置
	 * 
	 * @return 组块结束位置
	 */
	public int getEnd()
	{
		return end;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
		result = prime * result + ((str == null) ? 0 : str.hashCode());
		result = prime * result + Arrays.hashCode(tokens);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Chunk other = (Chunk) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		if (str == null)
		{
			if (other.str != null)
				return false;
		}
		else if (!str.equals(other.str))
			return false;
		if (!Arrays.equals(tokens, other.tokens))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		if (type.equals("O"))
			return str;

		return "[" + str + "]" + type;
	}
}
