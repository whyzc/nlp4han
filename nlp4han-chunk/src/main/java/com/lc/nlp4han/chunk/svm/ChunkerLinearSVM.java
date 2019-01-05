package com.lc.nlp4han.chunk.svm;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.chunk.svm.liblinear.InvalidInputDataException;
import com.lc.nlp4han.chunk.svm.liblinear.Linear;
import com.lc.nlp4han.chunk.svm.liblinear.Model;
import com.lc.nlp4han.chunk.svm.liblinear.PredictLinear;
import com.lc.nlp4han.chunk.svm.liblinear.Train;

public class ChunkerLinearSVM extends ChunkerSVM
{
	Model model = null;

	@Override
	public double predictOneLine(String line, Object model) throws IOException
	{
		Model modelLinear = (Model) model;
		return PredictLinear.doPredict(line, modelLinear);

	}

	@Override
	public void train(String[] arg) throws IOException
	{
		try
		{
			Train.main(arg);
		}
		catch (IOException | InvalidInputDataException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public void setModel(String modelPath) throws IOException
	{
		this.model = Linear.loadModel(new File(modelPath));
	}

	@Override
	public void setModel(Object model)
	{
		this.model = (Model) model;
	}

	@Override
	public Object getModel()
	{
		return this.model;
	}
}
