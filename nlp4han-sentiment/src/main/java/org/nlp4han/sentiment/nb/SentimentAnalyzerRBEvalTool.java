package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

public class SentimentAnalyzerRBEvalTool
{
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		if (args.length < 0)
		{
			usage();
			return;
		}
		
		String dataPath = "";
		String dicPath ="";
		String encoding="gbk";
		
		for (int i=0; i<args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				dataPath = args[i+1];
				i++;
			}
			if (args[i].equals("-dic"))
			{
				dicPath = args[i+1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i+1];
				i++;
			}
			
		}		
		
		File dataFile = new File(dataPath);
		
		ObjectStream<String> lineStream =new PlainTextByLineStream(
				new MarkableFileInputStreamFactory(dataFile),encoding); 
		ObjectStream<SentimentTextSample> sampleStream = 
				new SentimentTextSampleStream(lineStream); 
		
		SentimentAnalyzerRB analyzer = new SentimentAnalyzerRB(dicPath, encoding);
		SentimentAnalyzerEvaluator evaluator = 
				new SentimentAnalyzerEvaluator(analyzer);
		
		evaluator.evaluate(sampleStream);
		
		long startTime = System.currentTimeMillis();
		SentimentAnalyzerMeasure measure = evaluator.getMeasure();
		System.out.println(measure);
		System.out.println("评估时间："+(System.currentTimeMillis()-startTime));
	}
	
	private static void usage()
	{
		System.out.println(SentimentAnalyzerRBEvalTool.class.getName()
				+ "-data <testDataPath> -dic <dictionaryPath> -encoding <encoding>");
	}
	

}
