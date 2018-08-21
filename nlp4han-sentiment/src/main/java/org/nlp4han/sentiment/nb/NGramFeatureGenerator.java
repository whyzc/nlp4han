package org.nlp4han.sentiment.nb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.lc.nlp4han.segment.WordSegFactory;
import com.lc.nlp4han.segment.WordSegmenter;

public class NGramFeatureGenerator implements FeatureGenerator {
	private int n;
	private boolean tokenizeFlag;//是否进行分词
	
	public NGramFeatureGenerator() {
		this.n = 2;
		this.tokenizeFlag = false;
	}
	
	public NGramFeatureGenerator(int n) {
		this.n= n;
		this.tokenizeFlag  = false;
	}
	
	public NGramFeatureGenerator(int n,boolean tokenize) {
		this.n = n;
		this.tokenizeFlag = tokenize;
	}
	
	public NGramFeatureGenerator(String n, String flag) {
		init(n,flag);
	}
	
	/**
	 * 初始化参数
	 * @param n
	 * @param flag
	 */
	private void init(String n, String flag) {
		this.n = Integer.parseInt(n);
		
		if (flag.equals("word")) {
			this.tokenizeFlag = true;
		}
		if (flag.equals("character")) {
			this.tokenizeFlag = false;
		}
		
	}
	

	@Override
	public Collection<String> extractFeatures(String text, Map<String, Object> extraInformation) {
		Collection<String> features = new ArrayList<String>();
		
		String[] tokens = null;
		if(tokenizeFlag) {
			try {
				tokens = tokenize(text);//分词操作
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}else {
			tokens = text.split("");
		}
		
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
	private String[] tokenize(String text) throws IOException {

		WordSegmenter segmenter = WordSegFactory.getWordSegmenter();
		String[] words = segmenter.segment(text);
		return words;
	}

}
