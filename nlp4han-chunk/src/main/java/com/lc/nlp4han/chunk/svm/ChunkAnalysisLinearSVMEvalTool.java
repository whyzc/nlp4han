package com.lc.nlp4han.chunk.svm;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEO;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEOS;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEOS;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIO;

public class ChunkAnalysisLinearSVMEvalTool
{
	private static final String USAGE = "Usage: ChunkAnalysisLinearSVMEvalTool [options] -model model_file -transform transformation_information_file -goal predicting_set_file\n"
			+ "options:\n" 
			+ "-label label : such as BIOE, BIOES\n"
			+ "-encoding encoding : set encoding form\n" 
			+ "-model model_file : set model path\n"
			+ "-transform transformation_file : set transformation file, end with '.info' \n"
			+ "-error error_messages_file : output error messages\n";
	public static void main(String[] args) throws IOException
	{
		String usage = USAGE;
		String encoding = "utf-8";
		String scheme = "BIEOS";
		String transformationFile = null;
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
			else if ("-label".equals(args[i]))
			{
				scheme = args[i + 1];
				i++;
			}
			else if ("-transform".equals(args[i]))
			{
				transformationFile = args[i + 1];
				i++;
			}
			else if ("-model".equals(args[i]))
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
			else
			{
				System.err.println(usage);
				System.exit(1);
			}
		}

		if (goldFile == null)
		{
			System.err.println(usage);
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
		ChunkAnalysisLinearSVMME tagger = new ChunkAnalysisLinearSVMME();

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
			ChunkAnalysisSVMEvalTool.eval(modelpath, goldFile, transformationFile, encoding, new File(errorFile),
					tagger, parse, measure, scheme);
		else
			ChunkAnalysisSVMEvalTool.eval(modelpath, goldFile, transformationFile, encoding, null, tagger, parse,
					measure, scheme);

	}
}
