package com.lc.nlp4han.pos.oldhmm;

import java.io.*;

/**
 * 读取文本语料库的接口
 */
public abstract class WordTagStream
{

    /**
     * 语料库路径
     */
    protected String corpusPath;

    /**
     * 辅助读取语料库的输入流
     */
    protected BufferedReader br;

    /**
     * 语料编码方式
     */
    protected String encoding;

    /**
     * 分割一个句子得到一个WordTag数组
     */
    public abstract WordTag[] segSentence(String sentence) throws IOException;

    /**
     * 打开构造器中传入的的语料库
     */
    public void openReadStream() throws FileNotFoundException, UnsupportedEncodingException
    {
        FileInputStream fis = new FileInputStream(this.corpusPath);
        InputStreamReader isr = new InputStreamReader(fis, this.encoding);
        this.br = new BufferedReader(isr);

    }

    /**
     * 输入流迭代读取每行语料的方法
     */
    public WordTag[] readSentence() throws IOException
    {
        String line = null;

        line = this.br.readLine();
        if (line == null)
        {
            return null;
        }
        if (line.trim().equals(""))
        {
            return this.readSentence();
        }

        return this.segSentence(line.trim());
    }

    /**
     * 关闭流的方法
     */
    public void close() throws IOException
    {
        this.br.close();
    }

    /**
     * @return 返回构造器传入的语料的路径
     */
    public String getCorpusPath()
    {
        return corpusPath;
    }

}
