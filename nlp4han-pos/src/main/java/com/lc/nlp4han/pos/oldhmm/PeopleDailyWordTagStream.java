package com.lc.nlp4han.pos.oldhmm;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * 读取人民日报词性标注语料库的输入流
 */
public class PeopleDailyWordTagStream extends WordTagStream {

    /**
     * @param corpusPath 语料路径
     * @param encoding   字符编码方式
     */
    public PeopleDailyWordTagStream(String corpusPath, String encoding) throws UnsupportedEncodingException, ClassNotFoundException, FileNotFoundException {
        this.corpusPath = corpusPath;
        this.encoding = encoding;
        this.openReadStream();
    }

    /**
     * 人民日报语料库分割的主要方式
     *
     * @param sentence 人民日报语料库中的一行句子
     * @return 一行句子对应的[word/tag]数组
     */
    @Override
    public WordTag[] segSentence(String sentence) throws IOException{
        String[] wordTags = sentence.split("\\s+");
        WordTag[] wt = new WordTag[wordTags.length];

        for (int i = 0; i < wordTags.length; i++) {

            String[] wordAndTag = wordTags[i].trim().split("/");
            if (wordAndTag.length != 2) {
                throw new IOException("不能有效分割单词标注序列："+wordTags[i].toString()+"，默认分割符为/");
            }
            wt[i] = new WordTag(wordAndTag[0], wordAndTag[1]);
        }
        return wt;
    }
}
