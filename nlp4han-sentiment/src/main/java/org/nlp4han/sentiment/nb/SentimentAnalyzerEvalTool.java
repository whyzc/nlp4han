package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nlp4han.sentiment.SentimentAnalyzerErrorPrinter;
import org.nlp4han.sentiment.SentimentTextSample;
import org.nlp4han.sentiment.SentimentTextSampleStream;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 情感分析模型评估工具类
 * @author lim
 *
 */
public class SentimentAnalyzerEvalTool {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		if (args.length<1) {
			usage();
			return;
		}
		
		String testDataPath = "";
		String modelPath = "";
		String encoding = "gbk";
		String algorithm = "NAIVEBAYES";
		String xBase = "character";
		String errFilePath = "";
		int nGram = 2;
		
		for(int i=0; i<args.length; i++) {
			if (args[i].equals("-data")) {
				testDataPath = args[i+1];
				i++;
			}
			if (args[i].equals("-model")) {
				modelPath = args[i+1];
				i++;
			}
			if (args[i].equals("-ng")) {
				nGram = Integer.parseInt(args[i+1]);
				i++;
			}
			if (args[i].equals("-flag")) {
				xBase = args[i+1];
				i++;
			}
			if (args[i].equals("-err"))
			{
				errFilePath = args[i+1];
				i++;
			}
			if(args[i].equals("-encoding")) {
				encoding = args[i+1];
				i++;
			}
			if (args[i].equals("-algorithm")) {
				algorithm = args[i+1];
				i++;
			}
		}
		
		long startTime = System.currentTimeMillis();
		File testFile = new File(testDataPath);
		
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.ALGORITHM_PARAM,algorithm);
		
		ObjectStream<String> testLineStream = new PlainTextByLineStream(
				new MarkableFileInputStreamFactory(testFile),encoding);
		ObjectStream<SentimentTextSample> testSampleStream = 
				new SentimentTextSampleStream(testLineStream);
		
		InputStream modelFile = new FileInputStream(modelPath);
		ModelWrapper model = new ModelWrapper(modelFile);
		
		FeatureGenerator fg =null;
		switch(xBase) {
		case "word":
			fg = new NGramWordFeatureGenerator(nGram);
			break;
		case "character":
			fg = new NGramCharFeatureGenerator(nGram);
			break;
		}
		SentimentAnalyzerContextGenerator contextGen = 
				new SentimentAnalyzerContextGeneratorConf(fg);
		
		SentimentAnalyzerNB analyzer = new SentimentAnalyzerNB(model,contextGen);
		
		SentimentAnalyzerEvaluator evaluator = null;
		if (!errFilePath.equals("")) {
			SentimentAnalyzerErrorPrinter errOut = 
					new SentimentAnalyzerErrorPrinter(new FileOutputStream(new File(errFilePath)));
			evaluator = new SentimentAnalyzerEvaluator(analyzer, errOut);						
		}else {
			evaluator = new SentimentAnalyzerEvaluator(analyzer);
		}
		
		evaluator.evaluate(testSampleStream);
		SentimentAnalyzerMeasure measure = evaluator.getMeasure();
		
		System.out.println(measure);
		System.out.println("评估时间："+(System.currentTimeMillis() - startTime)+"毫秒");
	}
	
	private static void usage() {
		System.out.println(SentimentAnalyzerEvalTool.class.getName()
				+"-data <testDataPath> -model <modelPath> -flag <wordOrCharacter> -ng <nGramFeature> -err <errFile> -encoding <encoding> -algorithm <algorithm>");
	}

}
