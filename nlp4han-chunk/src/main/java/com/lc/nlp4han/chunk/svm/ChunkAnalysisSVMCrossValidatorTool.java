package com.lc.nlp4han.chunk.svm;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEO;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEOS;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosContextGeneratorConf;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEOS;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSampleStream;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

public class ChunkAnalysisSVMCrossValidatorTool
{
	
	public static void main(String[] args) throws IOException
	{
		int folds = 10;
		String scheme = "BIOE";
		String encoding = "UTF-8";
		File corpusFile = null;
		
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = new File(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-v"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-label"))
			{
				scheme = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile),
				encoding);
		AbstractChunkSampleParser parse = null;
		AbstractChunkAnalysisMeasure measure = null;

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

		ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisWordPosSampleStream(lineStream, parse,
				scheme);
		Properties p =  SVMStandardInput.getDefaultConf();
		ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordPosContextGeneratorConf(p);
		ChunkAnalysisSVMCrossValidation crossValidator = new ChunkAnalysisSVMCrossValidation(args);
		crossValidator.evaluate(sampleStream, folds, contextGen, measure, p);
	}
}
