package com.lc.nlp4han.chunk.svm;

import java.io.IOException;
import com.lc.nlp4han.chunk.svm.libsvm.svm;
import com.lc.nlp4han.chunk.svm.libsvm.svm_model;

public class ChunkerLibSVM extends ChunkerSVM
{
	private svm_model model = null;
	
	@Override
	public double predictOneLine(String line, Object model) throws IOException
	{
		svm_model svmModel = (svm_model)model;
		return SVMPredict.predict(line, svmModel, 0);
	}

	@Override
	public void train(String[] arg)
	{
		try
		{
			svm_train.main(arg);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void setModel(String modelPath)
	{
		try
		{
			this.model = svm.svm_load_model(modelPath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void setModel(Object model)
	{
		this.model = (svm_model)model;
	}
	
	@Override
	public Object getModel()
	{
		return this.model;
	}
}
