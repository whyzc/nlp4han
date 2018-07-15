package org.nlp4han.sentiment.nb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

public class SentimentAnalyzerTrainTool {
	
	public static void main(String[] args) {
		
		String dataIn = "zh-sentiment.train";
		String modelFile = "zh-sentiment.model";
		String encoding = "GBK";
		try {
			File corpusFile = new File(dataIn);
			OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
			ObjectStream<String> lineStream = new PlainTextByLineStream(
					new MarkableFileInputStreamFactory(corpusFile),encoding);
			ObjectStream<SentimentTextSample> sampleStream =
					new SentimentTextSampleStream(lineStream);
			
			TrainingParameters params  = TrainingParameters.defaultParams();
			params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");
			
			FeatureGenerator fg = new NGramFeatureGenerator(2);
			SentimentAnalyzerContextGenerator contextGen = 
					new SentimentAnalyzerContextGeneratorConf(fg);
			
			ModelWrapper model = SentimentAnalyzerNB.train(sampleStream,params,contextGen);
			model.serialize(modelOut);
			modelOut.close();
		}catch(IOException e) {
			//异常处理
		}
	}

}
