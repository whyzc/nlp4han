package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
		String testDataPath = "zh-sentiment.test";
		String modelPath = "zh-sentiment.model";
		String encoding = "gbk";
		
		File testFile = new File(testDataPath);
		
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.ALGORITHM_PARAM,"NAIVEBAYES");
		
		ObjectStream<String> testLineStream = new PlainTextByLineStream(
				new MarkableFileInputStreamFactory(testFile),encoding);
		ObjectStream<SentimentTextSample> testSampleStream = 
				new SentimentTextSampleStream(testLineStream);
		
		InputStream modelFile = new FileInputStream(modelPath);
		ModelWrapper model = new ModelWrapper(modelFile);
		
		FeatureGenerator featureGen = new NGramFeatureGenerator(2);//
		SentimentAnalyzerContextGenerator contextGen =
				new SentimentAnalyzerContextGeneratorConf(featureGen);
		
		SentimentAnalyzerNB analyzer = new SentimentAnalyzerNB(model,contextGen);
		
		SentimentAnalyzerEvaluator evaluator = 
				new SentimentAnalyzerEvaluator(analyzer);
		
		long startTime = System.currentTimeMillis();
		evaluator.evaluate(testSampleStream);
		double accuracy =  evaluator.getAccuracy();
		System.out.println(accuracy);
		System.out.println("共耗时："+(System.currentTimeMillis() - startTime));
	}

}
