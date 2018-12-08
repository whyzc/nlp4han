package com.lc.nlp4han.clustering;

public class Count
{
	public int tn = 0;  //Term Number总词条数，该词在所有文档中出现的次数
	public int dn = 0;	//Document Number文档数，含有该词的文档数
	
	public Count(int tn, int dn)
	{
		this.tn = tn;
		this.dn = dn;
	}

	@Override
	public String toString()
	{
		return "[tn=" + tn + ", dn=" + dn + "]";
	}
}