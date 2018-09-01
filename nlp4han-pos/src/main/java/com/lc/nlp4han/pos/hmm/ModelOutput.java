package com.lc.nlp4han.pos.hmm;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.hmm.io.BinaryFileHMMWriter;
import com.lc.nlp4han.ml.hmm.io.HMMWriter;
import com.lc.nlp4han.ml.hmm.io.ObjectFileHMMWriter;
import com.lc.nlp4han.ml.hmm.io.TextFileHMMWriter;
import com.lc.nlp4han.ml.hmm.model.HMModel;

public class ModelOutput
{
	public static void writeModel(HMModel model, File file, String type) throws IOException
	{
		HMMWriter writer = null;
		switch (type.toLowerCase())
		{
		case "text":
			writer = new TextFileHMMWriter(model, file);
			break;
		case "binary":
			writer = new BinaryFileHMMWriter(model, file);
			break;
		case "object":
			writer = new ObjectFileHMMWriter(model, file);
			break;
		default:
			throw new IllegalArgumentException("错误的文件类型:text/binary/object");
		}

		writer.persist();
	}
}
