package com.lc.nlp4han.pos.oldhmm;

/**
 * word/tag类。
 */
public class WordTag {

    /**
     * 单词字符串
     */
    private String word;

    /**
     * 标注字符串
     */
    private String tag;

    /**
     * @param word 合法单词字符串
     * @param tag 合法标注字符串
     */
    public WordTag(String word, String tag) {
        this.word = word;
        this.tag = tag;
    }

    /**
     * @return word/tag中word的字符串形式
     */
    public String getWord() {
        return word;
    }

    /**
     * @return word/tag中tag的字符串形式
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return WordTag的格式化字符串
     */
    @Override
    public String toString() {
        return this.word + "/" + this.tag;
    }
    
    public static String[] getWords(WordTag[] wordTags)
    {
        String[] words =  new String[wordTags.length];
        for(int i=0; i<wordTags.length; i++)
            words[i] = wordTags[i].getWord();
        
        return words;
    }
    
    public static String[] geTags(WordTag[] wordTags)
    {
        String[] tags =  new String[wordTags.length];
        for(int i=0; i<wordTags.length; i++)
            tags[i] = wordTags[i].getTag();
        
        return tags;
    }

}
