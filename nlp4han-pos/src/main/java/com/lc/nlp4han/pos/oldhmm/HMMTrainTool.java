package com.lc.nlp4han.pos.oldhmm;

import java.io.IOException;

/**
 * 模型训练主函数
 */
public class HMMTrainTool
{
    public static void main(String[] args) throws IOException, NumberFormatException, IllegalAccessException, ClassNotFoundException
    {
        String corpusFile = null;
        String modelFile = null;
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
            if (args[i].equals("-model"))
            {
                modelFile = args[i + 1];
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
        }
        
        if(corpusFile==null || modelFile==null)
            return;
              
        WordTagStream trainStream = new OpenNLPWordTagStream(corpusFile, encoding);
        
        AbstractParams paras = null;
        HMM hmm = null;
  
        if (nGram==NGram.BiGram)
        {
            paras = new BigramParams(trainStream, holdOutRate, unk);
            hmm = new HMM1st(paras);
        }
        else if (nGram==NGram.TriGram)
        {
            paras = new TrigramParams(trainStream, holdOutRate, unk);
            hmm = new HMM2nd(paras);
        }
        else
        {
            // logger.severe("n-gram参数形式不合法：参数值应为 2 或 3 。");
            return ;
        }

        hmm.writeHMM(modelFile);
    }
}
