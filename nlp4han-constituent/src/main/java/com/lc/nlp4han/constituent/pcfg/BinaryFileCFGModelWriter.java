package com.lc.nlp4han.constituent.pcfg;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lc.nlp4han.constituent.pcfg.CFG;
import com.lc.nlp4han.constituent.pcfg.RewriteRule;
/**
 * 以二进制格式将模型写入文件中
 * @author qyl
 *
 */
public class BinaryFileCFGModelWriter extends AbstractCFGModelWriter
{
	private DataOutputStream dos;

	public BinaryFileCFGModelWriter(CFG cfg, File file) throws IOException
	{
		super(cfg);
		dos = new DataOutputStream(new FileOutputStream(file));
	}
	
	public BinaryFileCFGModelWriter(CFG cfg, OutputStream dos) throws IOException
	{
		super(cfg);
		this.dos =new DataOutputStream(dos) ;
	}

	public BinaryFileCFGModelWriter(CFG cfg, DataOutputStream dos) throws IOException
	{
		super(cfg);
		this.dos = dos;
	}

	@Override
	public void writeCFGModelRule(RewriteRule rule) throws IOException
	{
        dos.writeUTF(rule.toString());
	}

	@Override
	public void writeUTF(String string) throws IOException
	{
		dos.writeUTF(string);
	}

	@Override
	public void close() throws IOException
	{
		//刷新此输出流并强制任何缓冲的输出字节被写出
		dos.flush();
		
		dos.close();		
	}

}
