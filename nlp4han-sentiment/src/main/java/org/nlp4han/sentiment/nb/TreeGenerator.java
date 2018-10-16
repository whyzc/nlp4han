package org.nlp4han.sentiment.nb;

import java.io.IOException;

public class TreeGenerator
{
//	private StanfordCoreNLP pipeline;
//	private Annotation annotation;
	
	public TreeGenerator() throws IOException {
		init();
	}
	

	public String getTree(String text)
	{		
//		annotation = new Annotation(text);
//		pipeline.annotate(annotation);
		String bracketStr = "";
//		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class))
//		{
//			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
//			bracketStr = tree.toString();
//		}
		return bracketStr;
	}

	private void init() throws IOException {
//		Properties properties = new Properties();
//		properties.load(TreeGenerator.class.getClassLoader().getResourceAsStream("StanfordCoreNLP-chinese.properties"));
//		properties.put("annotators", "tokenize, ssplit,pos,parse");
//		pipeline = new StanfordCoreNLP(properties);
	}

}
