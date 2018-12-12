package com.lc.nlp4han.constituent.pcfg;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/**
 * 从二进制文件中读取模型
 * @author qyl
 *
 */
public class BinaryFileCFGModelReader extends CFGModelReader
{
	
	public BinaryFileCFGModelReader(File file) throws IOException {
		super(new BinaryCFGDataReader(file));
	}
	
	public BinaryFileCFGModelReader(DataInputStream dis) {
		super(new BinaryCFGDataReader(dis));
	}
	
	public BinaryFileCFGModelReader(InputStream in) {
		super(new BinaryCFGDataReader(in));
	}

}
