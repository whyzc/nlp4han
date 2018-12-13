package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;

import com.lc.nlp4han.constituent.pcfg.RewriteRule;
/**
 * 写入CFG模型的接口
 * @author qyl
 *
 */
public interface CFGModelWriter
{
	/**
	 * 写入规则
	 * @param entry 待写入的条目
	 * @throws IOException
	 */
	void writeCFGModelRule(RewriteRule rule) throws IOException;
	
	/**
	 * 写入字符串
	 * @param string 待写入的字符串
	 * @throws IOException
	 */
	void writeUTF(String string) throws IOException;
	
	/**
	 * 关闭写入流  
	 * @throws IOException
	 */
	void close() throws IOException;

	/**
	 * 保存模型 ，执行此方法后将自动关闭写入流
	 * @throws IOException
	 */
	void persist() throws IOException;
}
