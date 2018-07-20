package com.lc.nlp4han.chunk.word;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEO;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEOS;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIO;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * <ul>
 * <li>Description: 交叉验证工具类
 * <li>Company: HUST
 * <li>@author Sonly
 * <li>Date: 2017年12月18日
 * </ul>
 */
public class ChunkAnalysisWordCrossValidatorTool {

	private static void usage() {
		System.out.println(ChunkAnalysisWordCrossValidatorTool.class.getName()
				+ " -data <corpusFile> -type <type> -label <label> -encoding <encoding> [-folds <nFolds>] [-cutoff <num>] [-iters <num>]");
	}

	public static void main(String[] args)
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
		if (args.length < 1) {
			usage();
			return;
		}

		int cutoff = 3;
		int iters = 100;
		int folds = 10;
		// Maxent, Perceptron, MaxentQn, NaiveBayes
		String type = "Maxent";
		String label = "BIEO";
		File corpusFile = null;
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
			} else if (args[i].equals("-encoding")) {
				encoding = args[i + 1];
				i++;
			} else if (args[i].equals("-cutoff")) {
				cutoff = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("-iters")) {
				iters = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].equals("-folds")) {
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
		}

		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
		params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
		params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());

		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile),
				encoding);
		AbstractChunkSampleParser parse = null;
		AbstractChunkAnalysisMeasure measure = null;
		SequenceValidator<String> sequenceValidator = null;

		if (label.equals("BIEOS")) {
			parse = new ChunkAnalysisWordSampleParserBIEOS();
			measure = new ChunkAnalysisMeasureBIEOS();
			sequenceValidator = new ChunkAnalysisSequenceValidatorBIEOS();
		} else if (label.equals("BIEO")) {
			parse = new ChunkAnalysisWordSampleParserBIEO();
			measure = new ChunkAnalysisMeasureBIEO();
			sequenceValidator = new ChunkAnalysisSequenceValidatorBIEO();
		} else {
			parse = new ChunkAnalysisWordSampleParserBIO();
			measure = new ChunkAnalysisMeasureBIO();
			sequenceValidator = new ChunkAnalysisSequenceValidatorBIO();
		}
		
		ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordContextGeneratorConf();
		ChunkAnalysisWordCrossValidation crossValidator = new ChunkAnalysisWordCrossValidation(params);
		ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisWordSampleStream(lineStream,
				parse, label);

		crossValidator.evaluate(sampleStream, folds, contextGen, measure, sequenceValidator);

	}
}
