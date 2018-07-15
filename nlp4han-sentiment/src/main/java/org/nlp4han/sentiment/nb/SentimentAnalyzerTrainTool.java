package org.nlp4han.sentiment.nb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

public class SentimentAnalyzerTrainTool {
	
	public static void main(String[] args) {
		
		if (args.length<1) {
			usage();
			return;
		}			
		
		String dataIn = "";
		String modelFile = "";
		String encoding = "GBK";
		String algrithm = "NAIVEBAYES";
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-data")) {
				dataIn = args[i+1];
				i++;
			}
			if (args[i].equals("-model")) {
				modelFile = args[i+1];
				i++;
			}
			if(args[i].equals("-encoding")) {
				encoding = args[i+1];
				i++;
			}
			if(args[i].equals("-algorithm")) {
				algrithm = args[i+1];
				i++;
			}
		}		
		
		
		try {
			File corpusFile = new File(dataIn);
			OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
			ObjectStream<String> lineStream = new PlainTextByLineStream(
					new MarkableFileInputStreamFactory(corpusFile),encoding);
			ObjectStream<SentimentTextSample> sampleStream =
					new SentimentTextSampleStream(lineStream);
			
			TrainingParameters params  = TrainingParameters.defaultParams();
			params.put(TrainingParameters.ALGORITHM_PARAM, algrithm);
			
			FeatureGenerator fg = new NGramFeatureGenerator(2);
			SentimentAnalyzerContextGenerator contextGen = 
					new SentimentAnalyzerContextGeneratorConf(fg);
			
			ModelWrapper model = SentimentAnalyzerNB.train(sampleStream,params,contextGen);
			model.serialize(modelOut);
			modelOut.close();
		}catch(IOException e) {
			e.printStackTrace();
		}			
	}
	
	private static void usage() {
		System.out.println(SentimentAnalyzerTrainTool.class.getName()
				+"-data <tarinData> -model <modelOutPath> -encoding <encoding> -algorithm <algorithm>");
	}

}
