package com.lc.nlp4han.pos.oldhmm;


/**
 * 交叉验证主函数
 */
public class HMMCrossValidationTool {

    /**
     * 指定语料库进行模型的验证
     */
    public static void main(String[] args) throws Exception{
        
        int folds = 10;
        String corpusFile = null;
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
            else if (args[i].equals("-folds"))
            {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
        }
        
        if(corpusFile==null)
            return;
              
        WordTagStream in = new OpenNLPWordTagStream(corpusFile, encoding);
        
        ModelEval modelEval = new CrossValidation(in, folds, nGram, holdOutRate, unk);
        modelEval.eval();
        
        System.out.println(modelEval.getScores());
//        logger.info("交加验证评分为： "+ modelScore.getScores().toString());
    }

}
