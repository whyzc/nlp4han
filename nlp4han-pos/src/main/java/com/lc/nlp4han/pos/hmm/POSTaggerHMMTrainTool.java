package com.lc.nlp4han.pos.hmm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.lc.nlp4han.ml.hmm.model.HMModel;
import com.lc.nlp4han.ml.hmm.stream.SupervisedHMMSample;

/**
 * 基于隐马尔科夫的词性标注器的训练工具类
 * 
 * @author lim
 *
 */
public class POSTaggerHMMTrainTool
{
	public static void main(String[] args) throws IOException
	{

		if (args.length < 1)
		{
			usage();
			return;
		}

		int order = 1;
		double ratio = 0.1;
		String smooth = "add";
		String modelFileType = "object";
		String encoding = "UTF-8";
		String dataPath = "E:\\codeprac\\HMM\\pos.train";
		String modelPath = "E:\\codeprac\\HMM\\pos-hmm.model";

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
			if (args[i].equals("-model"))
			{
				modelPath = args[i + 1];
				i++;
			}
			if (args[i].equals("-type"))
			{
				modelFileType = args[i + 1];
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

		File trainFile = new File(dataPath);
		File modelFile = new File(modelPath);

		long startTime = System.currentTimeMillis();

		List<SupervisedHMMSample> trainSamples = POSTaggerHMMSamplesReader.readSupervisedHMMSamples(trainFile, order,
				encoding);

		HMModel model = POSTaggerHMM.train(trainSamples, smooth, order, ratio);
		ModelOutput.writeModel(model, modelFile, modelFileType);
		
		System.out.println("训练时间：" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
	}

	private static void usage()
	{
		System.out.println(
				POSTaggerHMMTrainTool.class.getName() + "-smooth <smoothType> -data <trainData> -model <modelOutPath> "
						+ "-type <modelFileType> -order <orderOfHMM> -ratio <ratio> -encoding <encoding>");
	}

}
