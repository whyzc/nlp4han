package com.lc.nlp4han.pos.oldhmm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.lc.nlp4han.pos.POSTaggerProb;


/**
 * 基于HMM的词性标注器
 */
public class POSTaggerHMM implements POSTaggerProb {

    /**
     * 隐马尔科夫模型
     */
    private HMM hmm;

    /**
     * @param hmm 隐马尔科夫模型
     */
    public POSTaggerHMM(HMM hmm) {
        this.hmm = hmm;
    }

    /**
     * @param HMMPath 序列化隐马尔科夫模型路径
     */
    public POSTaggerHMM(String HMMPath) throws IOException,ClassNotFoundException{
        this.hmm = this.readHMM(HMMPath);
    }

    /**
     * 返回最可能的标注序列
     *
     * @param sentences 未标注句子
     * @return 标注结果
     */
    public WordTag[] tag(String sentences) {
        return tagTopK(sentences, 1)[0];
    }


    /**
     * 返回最可能的标注序列
     *
     * @param words 未标注句子
     * @return 标注结果
     */
    public String[] tag(String[] words) {
        WordTag[] wordTags = tagTopK(words, 1)[0];
        String[] tags = new String[wordTags.length];
        for(int i=0; i<wordTags.length; i++)
            tags[i] = wordTags[i].getTag();
        
        return tags;
    }

    /**
     * 返回k个最可能的标注序列
     *
     * @param sentences 未标注句子
     * @param k         得到k个局部最优标注，其中排名第一的标注是全局最优
     * @return          k个局部最优标注
     */
    public WordTag[][] tagTopK(String sentences, int k) {

        //处理k大于标注集大小的边界问题
        int sizeOfTags = this.hmm.getHmmParas().getDictionary().getSizeOfTags();
        if (k > sizeOfTags) {
            return this.tagTopK(sentences, sizeOfTags);
        }

        String[] words = sentences.split("\\s+");
        return this.tagTopK(words, k);
    }

    /**
     * 返回k个最可能的标注序列
     *
     * @param words 未标注句子
     * @param k     得到k个局部最优标注，其中排名第一的标注是全局最优
     * @return      k个局部最优标注
     */
    public WordTag[][] tagTopK(String[] words, int k) {

        //处理k大于标注集大小的边界问题
        int sizeOfTags = this.hmm.getHmmParas().getDictionary().getSizeOfTags();
        if (k > sizeOfTags) {
            return this.tagTopK(words, sizeOfTags);
        }

        int wordLen = words.length;
        WordTag[][] wts = new WordTag[k][wordLen];
        int[][] tagIds = this.hmm.decode(words, k);
        for (int i = 0; i < k; ++i) {
            wts[i] = this.matching(words, tagIds[i]);
        }
        return wts;
    }

    /**
     * 词与标注配对
     *
     * @param words  单词序列
     * @param tagIds 标注序列
     * @return [Word/Tag]数组
     */
    private WordTag[] matching(String[] words, int[] tagIds) {
        int wordLen = words.length;
        WordTag[] wts = new WordTag[wordLen];
        for (int index = 0; index < wordLen; ++index) {
            wts[index] = new WordTag(words[index], this.hmm.getHmmParas().getDictionary().getTag(tagIds[index]));
        }
        return wts;
    }

    /**
     * 模型反序列化
     *
     * @param path 列化模型序的路径
     * @return HMM对象
     */
    private HMM readHMM(String path) throws ClassNotFoundException, IOException {
        HMM hmm = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(path));
            hmm = (HMM) ois.readObject();
        } catch (IOException e) {
//            logger.severe("模型序列化路径异常，" + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
//            logger.severe("模型对象不存在，" + e.getMessage());
            throw e;
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
//                logger.severe("模型序列化流关闭异常，" + e.getMessage());
                throw e;
            }
        }
        return hmm;
    }

    @Override
    public String[][] tag(String[] sentence, int k)
    {
        WordTag[][] sequences = tagTopK(sentence, k);
        
        String[][] tagsList = new String[sequences.length][];
        for(int i=0; i<sequences.length; i++)
        {
            WordTag[] wordTags = sequences[i];
            
            String[] tags = new String[wordTags.length];
            for(int j=0; j<wordTags.length; j++)
                tags[j] = wordTags[j].getTag();
            
            tagsList[i] = tags;
        }
        
        return tagsList;
    }
}
