package com.lc.nlp4han.dependency.mst;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.dependency.DependencyParseContextGenerator;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 依存分析交叉验证应用
 * 
 * @author 刘小峰
 *
 */
public class DependencyCrossValidatorTool
{
    private static void usage()
    {
        System.out.println(DependencyCrossValidatorTool.class.getName() + " -data <corpusFile> -encoding <encoding> [-folds <nFolds>] " + "[-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        int cutoff = 3;
        int iters = 100;
        int folds = 10;
        File corpusFile = null;
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-folds"))
            {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));


        ObjectStream<String> linesStream = new PlainTextBySpaceLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        // TODO: 根据命令行参数选择不同的样本解析器
        DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
        ObjectStream<DependencySample> sampleStream = new DependencySampleStream(linesStream, sampleParser);

        // 交叉验证
        DependencyParseCrossValidator crossValidator = new DependencyParseCrossValidator(params);
        DependencyParseContextGenerator contextGen = new DependencyParseContextGeneratorConf();
        crossValidator.evaluate(sampleStream, folds, contextGen);
    }
}
