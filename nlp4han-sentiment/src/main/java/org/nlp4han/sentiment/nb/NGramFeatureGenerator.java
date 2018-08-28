package org.nlp4han.sentiment.nb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public abstract class NGramFeatureGenerator implements FeatureGenerator {
private int n;
	
	public NGramFeatureGenerator(int n) {
		this.n = n;
	}
	
	@Override
	public Collection<String> extractFeatures(String text, Map<String, Object> extraInformation) {
		Collection<String> features = new ArrayList<String>();
		
		String[] tokens = tokenize(text);
		
		for(int i = 0;i<tokens.length;i++) {
			
			StringBuilder sb = new StringBuilder("ng=");
			
			for (int j = 0;i+j<tokens.length && j<n;j++) {
				sb.append(":");
				sb.append(tokens[i+j]);
				int gramCount = j+1;
				if (gramCount == n) {					
					features.add(sb.toString());
				}
			}
		}
		return features;
	}
	
	/**
	 * 对文本进行分词
	 * @param text
	 * @return
	 * @throws IOException 
	 */
	public abstract String[] tokenize(String text);


}
