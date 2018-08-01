package org.nlp4han.sentiment.nb;

import java.io.IOException;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class TreeGenerator
{
	//private String model;
	private StanfordCoreNLP pipeline;
	private Annotation annotation;
	//private LexicalizedParser parser;
	//private String flag;
	
	public TreeGenerator() throws IOException {
		init();
	}
	

	public String getTree(String text)
	{		
		annotation = new Annotation(text);
		pipeline.annotate(annotation);
		String bracketStr = "";
		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class))
		{
			Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
			bracketStr = tree.toString();
		}
		return bracketStr;
	}

	private void init() throws IOException {
		Properties properties = new Properties();
		properties.load(TreeGenerator.class.getClassLoader().getResourceAsStream("StanfordCoreNLP-chinese.properties"));
		properties.put("annotators", "tokenize, ssplit,pos,parse");
		pipeline = new StanfordCoreNLP(properties);
	}

}
