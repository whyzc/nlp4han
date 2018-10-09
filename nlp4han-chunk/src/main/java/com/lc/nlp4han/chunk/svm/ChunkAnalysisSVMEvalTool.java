package com.lc.nlp4han.chunk.svm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.ChunkAnalysisErrorPrinter;
import com.lc.nlp4han.chunk.ChunkAnalysisEvaluateMonitor;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEO;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEOS;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIO;
import com.lc.nlp4han.chunk.svm.libsvm.svm;
import com.lc.nlp4han.chunk.svm.libsvm.svm_model;
import com.lc.nlp4han.chunk.word.ChunkAnalysisSequenceValidatorBIEO;
import com.lc.nlp4han.chunk.word.ChunkAnalysisSequenceValidatorBIEOS;
import com.lc.nlp4han.chunk.word.ChunkAnalysisSequenceValidatorBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosContextGeneratorConf;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEOS;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSampleStream;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.SequenceValidator;

public class ChunkAnalysisSVMEvalTool
{
	public static final String USAGE = "Usage: ChunkAnalysisSVMEvalTool [options] -data training_set_file\n"
			+"options:\n"
			+"-data training_set_file : set training set file path\n"
			+"-encoding encoding : set encoding form\n"
			+"-model model_file : set model path\n"
			;
	
	public static void eval(String modelFile, String goldFile, String path, String encoding, File errorFile,
			AbstractChunkSampleParser parse,
			AbstractChunkAnalysisMeasure measure, String label) throws IOException
	{
		long start = System.currentTimeMillis();

		svm_model model = svm.svm_load_model(modelFile);

		ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordPosContextGeneratorConf();
		ChunkAnalysisSVMME tagger = new ChunkAnalysisSVMME(contextGen, model, path, label);
		ChunkAnalysisSVMEvaluator evaluator = null;

		if (errorFile != null)
		{
			ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
			evaluator = new ChunkAnalysisSVMEvaluator(tagger, measure, errorMonitor);
		}
		else
			evaluator = new ChunkAnalysisSVMEvaluator(tagger);

		evaluator.setMeasure(measure);

		ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(goldFile)),
				encoding);
		ObjectStream<AbstractChunkAnalysisSample> testStream = new ChunkAnalysisWordPosSampleStream(goldStream, parse,
				label);

		start = System.currentTimeMillis();
		evaluator.evaluate(testStream);
		System.out.println("标注时间： " + (System.currentTimeMillis() - start));

		System.out.println(evaluator.getMeasure());
	}
	
	public static void main(String[] args) throws IOException
	{
		String usage = USAGE;
		String encoding = "utf-8";
		String scheme = "BIEOS";
		String format = null;
		String modelpath = null;
		String errorFile = null;
		String goldFile = null;

		for (int i = 0; i < args.length; i++)
		{
			if ("-encoding".equals(args[i]))
			{
				encoding = args[i + 1];
				i++;
			}
			else if ("-tag".equals(args[i]))
			{
				scheme = args[i + 1];
				i++;
			}
			else if ("-format".equals(args[i]))
			{
				format = args[i + 1];
				i++;
			}
			else if ("-modelpath".equals(args[i]))
			{
				modelpath = args[i + 1];
				i++;
			}
			else if ("-error".equals(args[i]))
			{
				errorFile = args[i + 1];
				i++;
			}
			else if ("-gold".equals(args[i]))
			{
				goldFile = args[i + 1];
				i++;
			}
		}

		if (goldFile == null)
		{
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final java.nio.file.Path docDir = java.nio.file.Paths.get(goldFile);

		if (!java.nio.file.Files.isReadable(docDir))
		{
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		AbstractChunkSampleParser parse;
		AbstractChunkAnalysisMeasure measure;

		if (scheme.equals("BIEOS"))
		{
			parse = new ChunkAnalysisWordPosParserBIEOS();
			measure = new ChunkAnalysisMeasureBIEOS();
		}
		else if (scheme.equals("BIEO"))
		{
			parse = new ChunkAnalysisWordPosParserBIEO();
			measure = new ChunkAnalysisMeasureBIEO();
		}
		else
		{
			parse = new ChunkAnalysisWordPosParserBIO();
			measure = new ChunkAnalysisMeasureBIO();
		}

		if (errorFile != null)
			eval(modelpath, goldFile, format, encoding, new File(errorFile), parse, measure, scheme);
		else
			eval(modelpath, goldFile, format, encoding, null, parse, measure, scheme);

	}
}
