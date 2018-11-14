package com.lc.nlp4han.constituent.unlex;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 交叉验证应用
 * 
 * @author 王宁
 */
public class ParentLabelAddedCrossValidatorTool
{
	private static void usage()
	{
		System.out.println(ParentLabelAddedCrossValidatorTool.class.getName()
				+ " -train <corpusFile>  [-encoding <encoding>] [-folds <nFolds>] ");
	}

	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			usage();
			return;
		}
		String corpusFile = null;
		int folds = 10;
		String encoding = "utf-8";
		double pruneThreshold = 0.0001;
		boolean secondPrune = false;
		boolean prior = false;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-train"))
			{
				corpusFile = args[i + 1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			if (args[i].equals("-folds"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
		}
		try
		{
			ObjectStream<String> sentenceStream = new PlainTextByTreeStream(
					new FileInputStreamFactory(new File(corpusFile)), encoding);

			ConstituentMeasure measure = new ConstituentMeasure();
			ParentLabelAddedCrossValidator crossValidator = new ParentLabelAddedCrossValidator();
			crossValidator.evaluate(sentenceStream, folds, measure, pruneThreshold, secondPrune, prior);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
