package com.lc.nlp4han.chunk.wordpos;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 模型训练工具类
 */
public class ChunkAnalysisWordPosTrainerTool
{

	private static void usage()
	{
		System.out.println(ChunkAnalysisWordPosTrainerTool.class.getName()
				+ " -data <corpusFile> -type <type> -label <label> -model <modelFile> -encoding <encoding> "
				+ " [-cutoff <num>] [-iters <num>]");
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
	{
		if (args.length < 1)
		{
			usage();
			return;
		}

		int cutoff = 3;
		int iters = 100;

		// Maxent,Perceptron,MaxentQn,NaiveBayes
		String type = "Maxent";
		String scheme = "BIEO";
		File corpusFile = null;
		File modelFile = null;
		String encoding = "UTF-8";

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-type"))
			{
				type = args[i + 1];
				i++;
			}
			else if (args[i].equals("-label"))
			{
				scheme = args[i + 1];
				i++;
			}
			else if (args[i].equals("-model"))
			{
				modelFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-cutoff"))
			{
				cutoff = Integer.parseInt(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-iters"))
			{
				iters = Integer.parseInt(args[i + 1]);
				i++;
			}
		}

		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
		params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
		params.put(TrainingParameters.ALGORITHM_PARAM, type);

		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile),
				encoding);
		OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
		AbstractChunkSampleParser parse = null;

		if (scheme.equals("BIEOS"))
			parse = new ChunkAnalysisWordPosParserBIEOS();
		else if (scheme.equals("BIEO"))
			parse = new ChunkAnalysisWordPosParserBIEO();
		else
			parse = new ChunkAnalysisWordPosParserBIO();

		ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisWordPosSampleStream(lineStream, parse,
				scheme);
		ChunkAnalysisWordPosME me = new ChunkAnalysisWordPosME();
		ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordPosContextGeneratorConf();

		ModelWrapper model = me.train(sampleStream, params, contextGen);
		model.serialize(modelOut);
		
		modelOut.close();
	}
}
