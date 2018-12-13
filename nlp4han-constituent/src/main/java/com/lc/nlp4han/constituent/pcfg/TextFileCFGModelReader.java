package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/**
 * 从文本文件中读取CFG模型
 * @author qyl
 *
 */
public class TextFileCFGModelReader extends CFGModelReader
{
	public TextFileCFGModelReader(File file) throws IOException {
		super(new TextCFGDataReader(file));
	}
	
	public TextFileCFGModelReader(InputStream in) {
		super(new TextCFGDataReader(in));
	}

}
