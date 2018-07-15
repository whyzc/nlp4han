package org.nlp4han.sentiment.nb;

import java.io.IOException;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;


public class SentimentTextSampleStream extends FilterObjectStream<String, SentimentTextSample> {

	  public SentimentTextSampleStream(ObjectStream<String> samples) {
		  super(samples);//验证输入的文本不为空
		  }

	  public SentimentTextSample read() throws IOException {
	    String sampleString = samples.read();

	    if (sampleString != null) {  
	    	String category = sampleString.substring(0, 2);
	    	String text = sampleString.substring(2).trim();
	    	

	      // 对文本进行分词，此处修改为进行停用词处理等操作;;;此处可以省略操作，等到特征提取出进行操作；
	      //String[] parts = sampleString.trim().split("\\t");//基于字的

	      SentimentTextSample sample;	  
	      sample = new SentimentTextSample(category,text);

	      /*if (parts.length == 2) {
	        String category = parts[0];
	        String text = parts[1];
	        String[] docTokens = new String[tokens.length - 1];
	        System.arraycopy(tokens, 1, docTokens, 0, tokens.length - 1);

	        sample = new SentimentTextSample(category, text);
	      }
	      else {
	    	  System.out.println(sampleString);
	        throw new IOException("Empty lines, or lines with only a category string are not allowed!");
	      }*/

	      return sample;
	    }

	    return null;
	  }
	}
