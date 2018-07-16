package org.nlp4han.sentiment.nb;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.util.ModelWrapper;
/**
 * 情感分析器用户使用接口
 * @author lim
 *
 */
public class SentimentAnalyzerUserTool {
	
	public static void main(String[] args) throws IOException {
		
		if (args.length<1) {
			usage();
			return;
		}
		
		String modelPath = "";
		String inputText ="";
		
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-model")) {
				modelPath = args[i+1];
				i++;
			}
			if(args[i].equals("-text")) {
				inputText = args[i+1];
				i++;
			}
		}
		
		File modelFile = new File(modelPath);
		ModelWrapper model = new ModelWrapper(modelFile);		
		
		FeatureGenerator fg = new NGramFeatureGenerator(2);
		SentimentAnalyzerContextGenerator contextGen = new SentimentAnalyzerContextGeneratorConf(fg);
		
		SentimentAnalyzerNB myAnalyzer = new SentimentAnalyzerNB(model,contextGen);
		SentimentPolarity sp = myAnalyzer.analyze(inputText);
		System.out.println(sp);
	}
	
	private static void usage() {
		System.out.println(SentimentAnalyzerUserTool.class.getName()
				+"-model <modelPath> -text <textToBeAnalyzed>");
	}

}
