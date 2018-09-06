package com.lc.nlp4han.pos.oldhmm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import com.lc.nlp4han.pos.WordPOSMeasure;

public class CrossValidation implements ModelEval {

    /**
     * 标明使用的n-gram
     */
    private NGram nGram;

    /**
     * 生成的标注器
     */

    private POSTaggerHMM tagger;

    /**
     * 读入特定形式的语料
     */
    private WordTagStream stream;

    /**
     * 每一折交叉验证中，生成的验证语料
     */
    private String unknownSentence;

    /**
     * 验证语料的正确标注
     */
    private String[] expectedTags;

    /**
     * 评估器
     */
    private WordPOSMeasure measure;

    /**
     * 折数
     */
    private int fold;

    /**
     * 映射词典
     */
    private HashSet<String> wordDict;

    /**
     * 留存数据比例
     */
    private int holdOutRatio;

    /**
     * 未登录词处理方式
     */
    private int unkHandle;

    /**
     * @param wordTagStream 包含特点语料路径的语料读取流
     * @param fold          交叉验证折数
     * @param nGram         语法参数
     */
    public CrossValidation(WordTagStream wordTagStream, int fold, NGram nGram,int holdOutRatio,int unkHandle) {
        this.stream = wordTagStream;
        this.fold = fold;
        this.nGram = nGram;
        this.measure = new WordPOSMeasure();
        this.holdOutRatio=holdOutRatio;
        this.unkHandle=unkHandle;
    }

    @Override
    public void eval() throws Exception {
        for (int i = 0; i < this.fold; ++i) {
            System.out.println("训练模型...");
            long start = System.currentTimeMillis();
            this.tagger = this.getTagger(i);
            System.out.println("训练时间:\t" + (System.currentTimeMillis()-start));
            
            this.stream.openReadStream();
            
            System.out.println("词性标注...");
            start = System.currentTimeMillis();
            WordPOSMeasure m = this.tag(i);
            System.out.println("标注时间:\t" + (System.currentTimeMillis()-start));
            System.out.println(m);
            this.measure.mergeInto(m);
            this.stream.openReadStream();
        }
    }

    /**
     * 获得指定训练集上训练的隐藏状态标注器
     *
     * @param taggerNO 代表训练集的编号
     * @return 隐藏状态标注器
     */
    private POSTaggerHMM getTagger(int taggerNO) throws Exception {
        WordTag[] wts = null;
        POSTaggerHMM tagger = null;
        Random random = new Random(11);

        AbstractParams paras = null;
        DictFactory dictFactory=new DictFactory();
        HMM hmm = null;

        int num = 0;
        while ((wts = this.stream.readSentence()) != null) {
            if (num % this.fold != taggerNO) {
                dictFactory.addIndex(wts);
            }
            ++num;
        }

        if (this.nGram == NGram.BiGram) {
            paras = new BigramParams(dictFactory,this.unkHandle);
            hmm = new HMM1st(paras);
        } else if (this.nGram == NGram.TriGram) {
            paras = new TrigramParams(dictFactory,this.unkHandle);
            hmm = new HMM2nd(paras);
        }
        num = 0;
        this.stream.openReadStream();
        if (this.holdOutRatio > 1) {
            while ((wts = this.stream.readSentence()) != null) {
                if (num % this.fold != taggerNO) {
                    //相比于第一次扫描，因为划分了训练集和留存，训练参数中可能有些状态没有记录到
                    int randNum = random.nextInt(this.holdOutRatio);
                    if (randNum == 1) {
                        paras.addHoldOut(wts);
                    } else {
                        paras.addCorpus(wts);
                    }
                }
                ++num;
            }
        } else {
            while ((wts = stream.readSentence()) != null) {
                if (num % fold != taggerNO) {
                    paras.addCorpus(wts);
                }
                ++num;
            }
        }
        paras.calcProbs();
        tagger = new POSTaggerHMM(hmm);
        this.wordDict = dictFactory.getWordSet();
        return tagger;
    }

    /**
     * 指定验证集，进行一次交叉验证，并返回评估值
     *
     * @return 一折验证的评分
     */
    private WordPOSMeasure tag(int taggerNo) throws IOException {
        WordPOSMeasure posMeasure = new WordPOSMeasure(this.wordDict);
        WordTag[] wts = null;
        int num = 0;
        String[] predictTags = null;


        while ((wts = this.stream.readSentence()) != null) {
            if (num % this.fold == taggerNo) {
                //验证语料不能直接放入内存
                this.getTagOfValidation(wts);
                String[]words=this.unknownSentence.trim().split("\\s+");
                WordTag[] predict = this.tagger.tag(this.unknownSentence);
                predictTags = new String[predict.length];
                for (int j = 0; j < predict.length; ++j) {
                    predictTags[j] = predict[j].getTag();
                }
                posMeasure.updateScores(words, this.expectedTags,predictTags);
            }
            ++num;
        }
        return posMeasure;
    }

    /**
     * 分割验证集观察状态和隐藏状态
     *
     * @param wts 带标注的句子
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

    /**
     * @return 返回此次验证评分
     */
    public WordPOSMeasure getScores() {
        return this.measure;
    }
}
