package com.lc.nlp4han.pos.character;

import java.util.ArrayList;


/**
 * 解析OpenNLP词性标注语料
 * 
 * 词和词性间用_分割
 * 
 * @author 刘小峰
 *
 */
public class CharPOSParseOpen implements CharPOSSampleParser
{

    /**
     * 解析OpenNLP词性标注语料
     * 
     * @return WordSegPosSample对象
     */
    public CharPOSSample parse(String sampleSentence)
    {
    	String[] wordsAndPoses = sampleSentence.split("\\s+");

		ArrayList<String> characters = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> tagsAndposes = new ArrayList<String>();

		for (int i = 1; i < wordsAndPoses.length; i++) {
			String[] wordanspos = wordsAndPoses[i].split("_");
			
			String word = wordanspos[0];
			String pos = wordanspos[1];
			
			words.add(word);

			if (word.length() == 1) {
				characters.add(word + "_S");
				tagsAndposes.add("S_" + pos);
				continue;
			}
			for (int j = 0; j < word.length(); j++) {
				char c = word.charAt(j);
				if (j == 0) {
					characters.add(c + "_B");
					tagsAndposes.add("B_" + pos);
				} else if (j == word.length() - 1) {
					characters.add(c + "_E");
					tagsAndposes.add("E_" + pos);
				} else {
					characters.add(c + "_M");
					tagsAndposes.add("M_" + pos);
				}
			}
		}

		return new CharPOSSample(characters, words, tagsAndposes);
    }

}
