package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.nlp4han.sentiment.SentimentTextSample;
import org.nlp4han.sentiment.SentimentTextSampleStream;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;


/**
 * 情感分析器交叉验证工具类
 * @author lim
 *
 */

public class SentimentAnalyzerCrossValidationTool {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		if (args.length<1) {
			usage();
			return;
		}
		
		String dataPath="";
		String n = "10";
		String encoding = "gbk";
		String algorithm = "NAIVEBAYES";
		String xBase = "character";//character,word
		int nGram = 2;
		
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("-data")) {
				dataPath = args[i+1];
				i++;
			}
			if (args[i].equals("-n")) {
				n = args[i+1];
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
			if (args[i].equals("-encoding")) {
				encoding = args[i+1];
				i++;
			}
			if (args[i].equals("-algorithm")) {
				algorithm = args[i+1];
			}			
		}
		
		int folds = Integer.parseInt(n);
		File dataFile = new File(dataPath);
		
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new MarkableFileInputStreamFactory(dataFile),encoding);
		ObjectStream<SentimentTextSample> sampleStream = 
				new SentimentTextSampleStream(lineStream);
		
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
		SentimentAnalyzerMeasure measure = new SentimentAnalyzerMeasure();
		
		SentimentAnalyzerCrossValidation crossVal = 
				new SentimentAnalyzerCrossValidation(params);
		crossVal.evaluate(sampleStream, folds, contextGen, measure);
	}
	
	private static void usage() {
		System.out.println(SentimentAnalyzerCrossValidationTool.class.getName()
				+"-data <dataPath> -n <n-folds> -ng <nGramFeature> -flag <wordOrCharacter> -encoding <encoding> -algorithm <algorithm>");
	}
}
