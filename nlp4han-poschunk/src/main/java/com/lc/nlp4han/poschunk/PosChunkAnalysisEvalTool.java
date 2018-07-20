package com.lc.nlp4han.poschunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.ChunkAnalysisErrorPrinter;
import com.lc.nlp4han.chunk.ChunkAnalysisEvaluateMonitor;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEO;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEOS;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIO;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;

/**
 * <ul>
 * <li>Description: 模型评估工具类
 * <li>Company: HUST
 * <li>@author Sonly
 * <li>Date: 2017年12月18日
 * </ul>
 */
public class PosChunkAnalysisEvalTool {

	/**
	 * 依据黄金标准评价基于词的词性标注和组块分析效果, 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
	 * 
	 * @param modelFile
	 *            模型文件
	 * @param goldFile
	 *            黄标准文件
	 * @param encoding
	 *            黄金标准文件编码
	 * @param errorFile
	 *            错误输出文件
	 * @throws IOException
	 */
	public static void eval(File modelFile, File goldFile, String encoding, File errorFile,
			AbstractChunkSampleParser parse, 
			SequenceValidator<String> sequenceValidator, 
			AbstractChunkAnalysisMeasure measure,
			String label)
			throws IOException {
		long start = System.currentTimeMillis();

		InputStream modelIn = new FileInputStream(modelFile);
        ModelWrapper model = new ModelWrapper(modelIn);

		System.out.println("评价模型...");
		ChunkAnalysisContextGenerator contextGen = new PosChunkAnalysisContextGeneratorConf();
		PosChunkAnalysisWordME tagger = new PosChunkAnalysisWordME(model, sequenceValidator, contextGen,
				label);
		PosChunkAnalysisEvaluator evaluator = null;

		if (errorFile != null) {
			ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
			evaluator = new PosChunkAnalysisEvaluator(tagger, measure, errorMonitor);
		} else
			evaluator = new PosChunkAnalysisEvaluator(tagger);

		evaluator.setMeasure(measure);

		ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile),
				encoding);
		ObjectStream<AbstractChunkAnalysisSample> testStream = new PosChunkAnalysisBasedWordSampleStream(goldStream,
				parse, label);

		start = System.currentTimeMillis();
		evaluator.evaluate(testStream);
		System.out.println("标注时间： " + (System.currentTimeMillis() - start));

		System.out.println(evaluator.getMeasure());
	}

	private static void usage() {
		System.out.println(PosChunkAnalysisEvalTool.class.getName()
				+ " -model <modelFile> -type <type> -label <label> -gold <goldFile> -encoding <encoding> [-error <errorFile>]");
	}

	public static void main(String[] args)
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (args.length < 1) {
			usage();
			return;
		}

		// Maxent,Perceptron,MaxentQn,NaiveBayes
		String type = "Maxent";
		String label = "BIEO";
		String modelFile = null;
		String goldFile = null;
		String errorFile = null;
		String encoding = null;

		int cutoff = 3;
		int iters = 100;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-model")) {
				modelFile = args[i + 1];
				i++;
			} else if (args[i].equals("-type")) {
				type = args[i + 1];
				i++;
			} else if (args[i].equals("-label")) {
				label = args[i + 1];
				i++;
			} else if (args[i].equals("-gold")) {
				goldFile = args[i + 1];
				i++;
			} else if (args[i].equals("-error")) {
				errorFile = args[i + 1];
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
		
		AbstractChunkSampleParser parse;
		SequenceValidator<String> sequenceValidator;
		AbstractChunkAnalysisMeasure measure;

		if (label.equals("BIEOS")) {
			sequenceValidator = new PosChunkAnalysisSequenceValidatorWithBIEOS();
			parse = new PosChunkAnalysisParseWithBIEOS();
			measure = new ChunkAnalysisMeasureBIEOS();
		} else if (label.equals("BIEO")) {
			sequenceValidator = new PosChunkAnalysisSequenceValidatorWithBIEO();
			parse = new PosChunkAnalysisParseWithBIEO();
			measure = new ChunkAnalysisMeasureBIEO();
		} else {
			sequenceValidator = new PosChunkAnalysisSequenceValidatorWithBIO();
			parse = new PosChunkAnalysisParseWithBIO();
			measure = new ChunkAnalysisMeasureBIO();
		}

		if (errorFile != null)
			eval(new File(modelFile), new File(goldFile), encoding, new File(errorFile), parse, sequenceValidator, measure, label);
		else
			eval(new File(modelFile), new File(goldFile), encoding, null, parse, sequenceValidator, measure, label);

	}
}