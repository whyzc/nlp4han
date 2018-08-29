package com.lc.nlp4han.pos.oldhmm;

import com.lc.nlp4han.pos.ConfusionMatrix;

/**
 * 模型测试主函数。
 */
public class HMMEvaluatorTool {

    /**
     * 指定语料库进行模型的验证
     */
    public static void main(String[] args) throws Exception{
        
        String corpusFile = null;
        String goldFile = null;
        String encoding ="UTF-8";
        NGram nGram = NGram.BiGram;
        int holdOutRate = 0;
        int unk = 1;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-gold"))
            {
                goldFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-ngram"))
            {
                if (args[i + 1].equals("2")) {
                    nGram = NGram.BiGram;
                } else if (args[i + 1].equals("3")) {
                    nGram = NGram.TriGram;
                } 
                
                i++;
            }
            else if (args[i].equals("-holdout"))
            {
                holdOutRate = Integer.parseInt(args[i + 1]);
                i++;
            }
        }
        
        if(corpusFile==null || goldFile == null)
            return;
              
        WordTagStream trainStream = new OpenNLPWordTagStream(corpusFile, encoding);
        WordTagStream goldStream = new OpenNLPWordTagStream(goldFile, encoding);
        
        ConfusionMatrix matrix = new ConfusionMatrix();

        ModelEvaluator modelEval = new ModelEvaluator(trainStream, goldStream, nGram, holdOutRate, unk);
        modelEval.setConfusionMatrix(matrix);
        modelEval.eval();
        System.out.println(modelEval.getScores());
        System.out.println(modelEval.getConfusionMatrix());
//        logger.info("交加验证评分为：\n"+ modelScore.getScores().toString());
    }
}
