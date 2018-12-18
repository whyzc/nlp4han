package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.lc.nlp4han.constituent.pcfg.CFG;
import com.lc.nlp4han.constituent.pcfg.RewriteRule;

/**
 * 以文本的形式写入text文件
 * 
 * @author qyl
 *
 */
public class TextFileCFGModelWriter extends AbstractCFGModelWriter
{
	private BufferedWriter bw;

	public TextFileCFGModelWriter(CFG cfg, File file) throws IOException
	{
		super(cfg);
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
	}

	public TextFileCFGModelWriter(CFG cfg, OutputStream out)
	{
		super(cfg);
		this.bw = new BufferedWriter(new OutputStreamWriter(out));
	}

	public TextFileCFGModelWriter(CFG cfg, BufferedWriter bw)
	{
		super(cfg);
		this.bw = bw;
	}

	@Override
	public void writeCFGModelRule(RewriteRule rule) throws IOException
	{
		bw.write(rule.toString());
		bw.newLine();
	}

	@Override
	public void writeUTF(String string) throws IOException
	{
		bw.write(string);
		bw.newLine();
	}

	@Override
	public void close() throws IOException
	{
		// 刷新此输出流并强制任何缓冲的输出字节被写出
		bw.flush();

		bw.close();
	}

}
