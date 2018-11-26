package com.lc.nlp4han.pos;

import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.pos.character.CharPOSContextGeneratorConf;
import com.lc.nlp4han.pos.character.CharPOSTaggerME;

public class POSTaggerFactory
{
	private static POSTagger posTagger;

	private POSTaggerFactory()
	{
	}

	/**
	 * 装入中文词性标注模型，并生成中文词性标注器
	 * 
	 * @return 中文词性标注器
	 * @throws IOException
	 */
	public static POSTagger getPOSTagger() throws IOException
	{
		if (posTagger != null)
			return posTagger;

		String modelName = "com/lc/nlp4han/pos/ctb8-pos-char.model";

		InputStream modelIn = POSTaggerFactory.class.getClassLoader().getResourceAsStream(modelName);
		ModelWrapper model = new ModelWrapper(modelIn);

		posTagger = new CharPOSTaggerME(model, new CharPOSContextGeneratorConf());

		return posTagger;
	}

	public static void main(String[] args) throws IOException
	{
		String[] words = new String[] {"我", "喜欢" , "你", "。"};

		POSTagger tagger = POSTaggerFactory.getPOSTagger();
		String[] tags = tagger.tag(words);
		for(int i=0; i<words.length; i++)
			System.out.println(words[i] + "_" + tags[i]);
	}
}
