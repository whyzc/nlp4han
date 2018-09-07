package org.nlp4han.sentiment;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author lim
 *
 */

public class SentimentAnalyzerErrorPrinter extends SentimentAnalyzerEvaluationMonitor
{
	private PrintStream errOut;

	public SentimentAnalyzerErrorPrinter(OutputStream os)
	{
		this.errOut = new PrintStream(os);
	}

	
	/**
	 * 样本和预测的不一样的时候进行输出
	 * @param reference 参考的样本
	 * @param prediction 预测的结果
	 */
	public void missclassified(SentimentTextSample reference, SentimentTextSample prediction)
	{
		errOut.println(prediction.getCategory() +"	"+ reference);
	}

}
