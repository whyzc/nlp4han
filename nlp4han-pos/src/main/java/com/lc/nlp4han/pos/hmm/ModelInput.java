package com.lc.nlp4han.pos.hmm;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.hmm.io.AbstractHMMReader;
import com.lc.nlp4han.ml.hmm.io.BinaryFileHMMReader;
import com.lc.nlp4han.ml.hmm.io.ObjectFileHMMReader;
import com.lc.nlp4han.ml.hmm.io.TextFileHMMReader;
import com.lc.nlp4han.ml.hmm.model.HMModel;

public class ModelInput
{
	public static HMModel loadModel(File modelFile, String type) throws IOException, ClassNotFoundException
	{
		AbstractHMMReader reader = new TextFileHMMReader(modelFile);
		switch (type.toLowerCase())
		{
		case "text":
			reader = new TextFileHMMReader(modelFile);
			break;
		case "binary":
			reader = new BinaryFileHMMReader(modelFile);
			break;
		case "object":
			reader = new ObjectFileHMMReader(modelFile);
			break;
		default:
			throw new IllegalArgumentException("错误的文件类型:text/binary/object");
		}
		return reader.readModel();
	}

}
