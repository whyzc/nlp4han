package com.lc.nlp4han.poschunk;

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
 * <ul>
 * <li>Description: 模型训练工具类
 * <li>Company: HUST
 * <li>@author Sonly
 * <li>Date: 2017年12月18日
 * </ul>
 */
public class PosChunkAnalysisTrainerTool {

	private static void usage() {
		System.out.println(PosChunkAnalysisTrainerTool.class.getName()
				+ " -data <corpusFile> -type <type> -label <label> -model <modelFile> -encoding <encoding> "
				+ " [-cutoff <num>] [-iters <num>]");
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		if (args.length < 1) {
			usage();
			return;
		}

		int cutoff = 3;
		int iters = 100;

		// Maxent,Perceptron,MaxentQn,NaiveBayes
		String type = "Maxent";
		String label = "BIEO";
		File corpusFile = null;
		File modelFile = null;
		String encoding = "UTF-8";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-data")) {
				corpusFile = new File(args[i + 1]);
				i++;
			} else if (args[i].equals("-type")) {
				type = args[i + 1];
				i++;
			} else if (args[i].equals("-label")) {
				label = args[i + 1];
				i++;
			} else if (args[i].equals("-model")) {
				modelFile = new File(args[i + 1]);
				i++;
			} else if (args[i].equals("-encoding")) {
				encoding = args[i + 1];
				i++;
			} else if (args[i].equals("-cutoff")) {
				cutoff = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("-iters")) {
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

		if (label.equals("BIEOS"))
			parse = new PosChunkAnalysisParseWithBIEOS();
		else if (label.equals("BIEO"))
			parse = new PosChunkAnalysisParseWithBIEO();
		else
			parse = new PosChunkAnalysisParseWithBIO();

		ObjectStream<AbstractChunkAnalysisSample> sampleStream = new PosChunkAnalysisBasedWordSampleStream(
				lineStream, parse, label);
		PosChunkAnalysisWordME me = new PosChunkAnalysisWordME();
		ChunkAnalysisContextGenerator contextGen = new PosChunkAnalysisContextGeneratorConf();
		ModelWrapper model = me.train(sampleStream, params, contextGen);
		model.serialize(modelOut);
	}
}
