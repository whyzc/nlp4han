package com.lc.nlp4han.chunk.svm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEO;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIEOS;
import com.lc.nlp4han.chunk.ChunkAnalysisMeasureBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosContextGeneratorConf;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEOS;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSampleStream;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

public class ChunkAnalysisSVMCrossValidatorTool
{
	public static final String USAGE = "Usage: ChunkAnalysisSVMCrossValidatorTool [options] -data training_set_file\n" 
			+ "options:\n"
			+ "-encoding encoding : set encoding" 
			+ "-label label : such as BIOE, BIOES"
			+ "-v n : n-fold cross validation mode\n"
			+"-s svm_type : set type of SVM (default 0)\n"
			+"	0 -- C-SVC		(multi-class classification)\n"
			+"	1 -- nu-SVC		(multi-class classification)\n"
			+"	2 -- one-class SVM\n"
			+"	3 -- epsilon-SVR	(regression)\n"
			+"	4 -- nu-SVR		(regression)\n"
			+"-t kernel_type : set type of kernel function (default 2)\n"
			+"	0 -- linear: u'*v\n"
			+"	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
			+"	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
			+"	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
			+"	4 -- precomputed kernel (kernel values in training_set_file)\n"
			+"-d degree : set degree in kernel function (default 3)\n"
			+"-g gamma : set gamma in kernel function (default 1/num_features)\n"
			+"-r coef0 : set coef0 in kernel function (default 0)\n"
			+"-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
			+"-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
			+"-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
			+"-m cachesize : set cache memory size in MB (default 100)\n"
			+"-e epsilon : set tolerance of termination criterion (default 0.001)\n"
			+"-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
			+"-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
			+"-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
			+"-q : quiet mode (no outputs)\n";
	
	private static final String[] TrainParameters = {"-s", "-t", "-d", "-g", "-r", "-c", "-n", "-p", "-m", "-e", "-h", "-b", "-wi", "-q"};
	
	public static void main(String[] args) throws IOException
	{
		int folds = 10;
		String scheme = "BIOE";
		String encoding = "UTF-8";
		String corpusFile = null;
		String[] trainArgs = null;
		
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-data"))
			{
				corpusFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-v"))
			{
				folds = Integer.parseInt(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-label"))
			{
				scheme = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		
		trainArgs = getTrainArgs(args, corpusFile);
		
		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(corpusFile)),
				encoding);
		AbstractChunkSampleParser parse = null;
		AbstractChunkAnalysisMeasure measure = null;

		if (scheme.equals("BIEOS"))
		{
			parse = new ChunkAnalysisWordPosParserBIEOS();
			measure = new ChunkAnalysisMeasureBIEOS();
		}
		else if (scheme.equals("BIEO"))
		{
			parse = new ChunkAnalysisWordPosParserBIEO();
			measure = new ChunkAnalysisMeasureBIEO();
		}
		else
		{
			parse = new ChunkAnalysisWordPosParserBIO();
			measure = new ChunkAnalysisMeasureBIO();
		}
		
		
		
		ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisWordPosSampleStream(lineStream, parse,
				scheme);
		Properties p =  SVMStandardInput.getDefaultConf();
		ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordPosContextGeneratorConf(p);
		ChunkAnalysisSVMCrossValidation crossValidator = new ChunkAnalysisSVMCrossValidation(trainArgs);
		crossValidator.evaluate(sampleStream, folds, contextGen, measure, p);
		
		sampleStream.close();
		deleteFile(trainArgs[trainArgs.length-1]);
		deleteFile(trainArgs[trainArgs.length-2]);
	}
	
	/**
	 * 获取训练参数
	 */
	private static String[] getTrainArgs(String[] args, String corpusFile)
	{
		List<String> result = new ArrayList<String>();
		for (int i=0 ; i<args.length ; i++)
		{
			for (int j=0 ; j<TrainParameters.length ; j++)
			{
				if (TrainParameters[j].equals(args[i]))
				{
					result.add(args[i]);
					result.add(args[i+1]);
					i++;
					break;
				}
			}
			
		}
		
		result.add(corpusFile+".svm.cv");
		result.add(corpusFile+".model.cv");
		return result.toArray(new String[result.size()]);
	}
	
	private static void deleteFile(String filePath)
	{
		 File file=new File(filePath);
         if(file.exists()&&file.isFile())
         {
             file.delete();
         }
	}
}
