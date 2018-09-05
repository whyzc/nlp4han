package com.lc.nlp4han.pos.oldhmm;


import java.io.IOException;
import java.util.HashSet;

import com.lc.nlp4han.pos.ConfusionMatrix;
import com.lc.nlp4han.pos.WordPOSMeasure;

public class ModelEvaluator implements ModelEval {

    /**
     * 标明使用的n-gram
     */
    private NGram nGram;

    /**
     * 生成的标注器
     */
    private POSTaggerHMM tagger;

    /**
     * 读入特定形式的训练语料
     */
    private WordTagStream training;


    /**
     * 读入特定形式的测试语料
     */
    private WordTagStream testing;

    /**
     * 每一折交叉验证中，生成的验证语料
     */
    private String unknownSentence;

    /**
     * 验证语料的正确标注
     */
    private String[] expectedTags;

    /**
     * 词典
     */
    private HashSet<String> dict;

    /**
     * 评估器
     */
    private WordPOSMeasure measure;
    
    private ConfusionMatrix matrix;

    /**
     * 留存数据比例
     */
    private int holdOutRatio;

    /**
     * 未登录词处理方式
     */
    private int unkHandle;

    public ModelEvaluator(WordTagStream training, WordTagStream testing, NGram nGram,int holdOutRatio,int unkHandle) {
        this.training = training;
        this.testing = testing;
        this.nGram = nGram;
        this.holdOutRatio=holdOutRatio;
        this.unkHandle=unkHandle;
    }

    @Override
    public void eval() throws Exception {
        System.out.println("训练模型...");
        long start = System.currentTimeMillis();
        this.getTagger();
        System.out.println("训练时间:\t" + (System.currentTimeMillis()-start));
        
        System.out.println("词性标注...");
        start = System.currentTimeMillis();
        this.tag();
        System.out.println("标注时间:\t" + (System.currentTimeMillis()-start));
    }

    /**
     * 通过训练集获得隐藏状态标注器
     */
    private void getTagger() throws Exception {
        AbstractParams paras = null;
        HMM hmm = null;
        if (this.nGram == NGram.BiGram) {
            paras = new BigramParams(this.training,this.holdOutRatio,this.unkHandle);
            hmm = new HMM1st(paras);
        } else if (this.nGram == NGram.TriGram) {
            paras = new TrigramParams(this.training,this.holdOutRatio,this.unkHandle);
            hmm = new HMM2nd(paras);
        }
        this.tagger = new POSTaggerHMM(hmm);
        this.dict = paras.getDictionary().getWordSet();
        this.measure = new WordPOSMeasure(this.dict);
    }
    
    public void setConfusionMatrix(ConfusionMatrix m)
    {
        this.matrix = m;
    }
    
    public ConfusionMatrix getConfusionMatrix()
    {
        return this.matrix;
    }

    /**
     * 在测试集上进行测试
     *
     */
    private void tag() throws IOException {
        WordTag[] wts = null;

        while ((wts = this.testing.readSentence()) != null) {
            this.getTagOfValidation(wts);
            WordTag[] predict = this.tagger.tag(this.unknownSentence);
            String[] words =new String[predict.length];
            String[] predictTags = new String[predict.length];
            for (int j = 0; j < predict.length; ++j) {
                predictTags[j] = predict[j].getTag();
                words[j]=predict[j].getWord();
            }
            this.measure.updateScores(words,this.expectedTags,predictTags);
            
            if(matrix!=null)
                matrix.add(this.expectedTags, predictTags);
        }
    }


    /**
     * 分割验证集观察状态和隐藏状态
     *
     */
    private void getTagOfValidation(WordTag[] wts) {
        String sentence = "";
        this.expectedTags = new String[wts.length];
        for (int j = 0; j < wts.length; ++j) {
            this.expectedTags[j] = wts[j].getTag();
            sentence = sentence + wts[j].getWord() + " ";
        }
        this.unknownSentence = sentence.trim();
    }

    @Override
    public WordPOSMeasure getScores() {
        return this.measure;
    }

}
