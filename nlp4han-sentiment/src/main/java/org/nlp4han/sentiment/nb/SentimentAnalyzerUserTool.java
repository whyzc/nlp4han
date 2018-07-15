package org.nlp4han.sentiment.nb;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.util.ModelWrapper;

public class SentimentAnalyzerUserTool {
	
	public static void main(String[] args) throws IOException {
		String modelPath = "E:\\codeprac\\zh-sentiment.model";
		File modelFile = new File(modelPath);
		ModelWrapper model = null;
		
		model = new ModelWrapper(modelFile);
		
		String inputText ="酒店根本没有服务。所有服务人员态度很差，根本不像一家度假酒店";
		FeatureGenerator fg = new NGramFeatureGenerator(2);
		SentimentAnalyzerContextGenerator contextGen = new SentimentAnalyzerContextGeneratorConf(fg);
		
		SentimentAnalyzerNB myAnalyzer = new SentimentAnalyzerNB(model,contextGen);
		SentimentPolarity sp = myAnalyzer.analyze(inputText);
		System.out.println(sp);
	}

}
