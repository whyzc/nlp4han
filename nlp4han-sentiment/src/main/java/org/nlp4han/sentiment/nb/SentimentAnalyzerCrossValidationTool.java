package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
		String dataPath="zh-sentiment.train";
		String encoding = "gbk";
		//String iter = null;
		//String cutoff = null;
		int folds = 10;
		
		File dataFile = new File(dataPath);
		
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(
				new MarkableFileInputStreamFactory(dataFile),encoding);
		ObjectStream<SentimentTextSample> sampleStream = 
				new SentimentTextSampleStream(lineStream);
		
		FeatureGenerator featureGen = new NGramFeatureGenerator(2);//基于字的二元
		SentimentAnalyzerContextGenerator contextGen = 
				new SentimentAnalyzerContextGeneratorConf(featureGen);
		
		SentimentAnalyzerCrossValidation crossVal = 
				new SentimentAnalyzerCrossValidation(params);
		crossVal.evaluate(sampleStream, folds, contextGen);
		double theAccuracy = crossVal.getTextAccuracy();
		System.out.println(theAccuracy);
	}
}
