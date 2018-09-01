package com.lc.nlp4han.pos.hmm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;

/**
 * 基于隐马尔科夫的词性标注器的交叉验证工具类
 * @author lim
 *
 */
public class POSTaggerHMMCrossValTool
{
	public static void main(String[] args) throws IOException
	{

		if (args.length < 1)
		{
			usage();
			return;
		}

		int order = 1;
		int folds = 10;
		double ratio = 0.1;
		String decodeAlgo = "A";
		String smooth = "add";
		String encoding = "UTF-8";
		String dataPath = "E:\\codeprac\\HMM\\pos.train";

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-smooth"))
			{
				smooth = args[i + 1];
				i++;
			}
			if (args[i].equals("-data"))
			{
				dataPath = args[i + 1];
				i++;
			}
			if (args[i].equals("-decode"))
			{
				decodeAlgo = args[i + 1];
				i++;
			}
			if (args[i].equals("-folds"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
			if (args[i].equals("-order"))
			{
				order = Integer.parseInt(args[i + 1]);
				i++;
			}
			if (args[i].equals("-ratio"))
			{
				ratio = Double.parseDouble(args[i + 1]);
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}

		File crossValFile = new File(dataPath);
		List<SupervisedHMMSample> corssValSamples = POSTaggerHMMSamplesReader.readSupervisedHMMSamples(crossValFile,
				order, encoding);

		POSTaggerHMMCrossValidation crossVal = new POSTaggerHMMCrossValidation(decodeAlgo);
		crossVal.evaluate(corssValSamples, folds, smooth, order, ratio);
	}

	private static void usage()
	{
		System.out.println(POSTaggerHMMCrossValTool.class.getName()
				+ "-smooth <smoothType> -data <dataPath> -decode <decodeAlgorithm> "
				+ "-folds <k-folds> -order <orderOfHMM> -ratio <ratio>" 
				+ "-encoding <encoding>");
	}

}
