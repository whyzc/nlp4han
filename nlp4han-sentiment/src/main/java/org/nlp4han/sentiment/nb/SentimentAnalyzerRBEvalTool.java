package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.nlp4han.sentiment.SentimentAnalyzerErrorPrinter;
import org.nlp4han.sentiment.SentimentTextSample;
import org.nlp4han.sentiment.SentimentTextSampleStream;

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
		String errFilePath = "";
		String encoding="gbk";
		
		for (int i=0; i<args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				dataPath = args[i+1];
				i++;
			}
			if (args[i].equals("-err"))
			{
				errFilePath = args[i+1];
				i++;
			}
			if (args[i].equals("-encoding"))
			{
				encoding = args[i+1];
				i++;
			}
			
		}
		
		long startTime = System.currentTimeMillis();
		File dataFile = new File(dataPath);
		
		ObjectStream<String> lineStream =new PlainTextByLineStream(
				new MarkableFileInputStreamFactory(dataFile),encoding); 
		ObjectStream<SentimentTextSample> sampleStream = 
				new SentimentTextSampleStream(lineStream); 
		
		TreeGenerator treeGen = new TreeGenerator();
		SentimentAnalyzerRB analyzer = new SentimentAnalyzerRB( treeGen);
		
		SentimentAnalyzerEvaluator evaluator = null;
		if (!errFilePath.equals("")) {
			SentimentAnalyzerErrorPrinter errOut = 
					new SentimentAnalyzerErrorPrinter(new FileOutputStream(new File(errFilePath)));
			evaluator = new SentimentAnalyzerEvaluator(analyzer, errOut);						
		}else {
			evaluator = new SentimentAnalyzerEvaluator(analyzer);
		}
		
		evaluator.evaluate(sampleStream);		
		
		SentimentAnalyzerMeasure measure = evaluator.getMeasure();
		System.out.println(measure);
		System.out.println("评估时间："+(System.currentTimeMillis()-startTime)+"毫秒");
	}
	
	private static void usage()
	{
		System.out.println(SentimentAnalyzerRBEvalTool.class.getName()
				+ "-data <testDataPath> -err <errFile>  -encoding <encoding>");
	}
	

}
