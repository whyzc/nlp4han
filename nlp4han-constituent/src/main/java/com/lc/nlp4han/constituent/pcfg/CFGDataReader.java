package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;

/**
 * cfg模型数据读取接口
 * @author qyl
 *
 */
public interface CFGDataReader
{
	/**
	 * 返回读取的String类型数据
	 * @return 读取的String类型数据
	 * @throws IOException
	 */
	public String readUTF() throws IOException;
	
	
	/**
	 * <li>关闭数据读取  
	 * @throws IOException
	 */
	public void close() throws IOException;
}
