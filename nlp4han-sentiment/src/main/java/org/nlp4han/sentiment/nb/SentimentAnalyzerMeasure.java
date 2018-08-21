package org.nlp4han.sentiment.nb;

import com.lc.nlp4han.ml.util.Mean;

public final class SentimentAnalyzerMeasure
{

	/**
	 * |selected| = true positives + false positives <br>
	 */
	private long selected;

	/**
	 * |target| = true positives + false negatives <br>
	 */
	private long target;

	/**
	 * 存储TP值
	 */
	private long truePositive;

	private Mean accuracy = new Mean();

	/**
	 * 获取查准率P
	 * 
	 * @return 查准率P
	 */
	public double getPrecisionScore()
	{
		return selected > 0 ? (double) truePositive / (double) selected : 0;
	}

	/**
	 * 获取查全率R
	 * 
	 * @return 查全率R
	 */
	public double getRecallScore()
	{
		return target > 0 ? (double) truePositive / (double) target : 0;
	}

	/**
	 * 获取f-measure值.
	 *
	 * f-measure = 2 * precision * recall / (precision + recall)
	 * 
	 * @return f-measure 或者 -1
	 */
	public double getFMeasure()
	{

		if (getPrecisionScore() + getRecallScore() > 0)
		{
			return 2 * (getPrecisionScore() * getRecallScore()) / (getPrecisionScore() + getRecallScore());
		}
		else
		{
			// cannot divide by zero, return error code
			return -1;
		}
	}

	/**
	 * 获取准确率
	 * 
	 * @return 准确率
	 */
	public double getAccuracy()
	{
		return accuracy.mean();
	}

	public Mean getMean()
	{
		return accuracy;
	}

	/**
	 * 根据预测值prediction和标准的参考值reference来更新评估指标
	 * 
	 * @param reference
	 *            标准的参考值
	 * @param prediction
	 *            系统的预测值
	 */
	public void updateScores(final String reference, final String prediction)
	{

		if (reference.equals(prediction))
		{
			accuracy.add(1);
		}
		else
		{
			accuracy.add(0);
		}

		truePositive += countTruePositives(reference, prediction);

		if (prediction.equals("+1"))
		{
			selected += 1;
		}
		if (reference.equals("+1"))
		{

			target += 1;
		}

	}

	/**
	 * 合并两个结果
	 * 
	 * @param measure
	 *            fmeasure
	 */
	public void mergeInto(final SentimentAnalyzerMeasure measure)
	{
		this.selected += measure.selected;
		this.target += measure.target;
		this.truePositive += measure.truePositive;
		this.accuracy.add(measure.getMean().mean(), measure.getMean().count());
	}

	/**
	 * 返回正确率的字符串表示
	 * 
	 * @return the results
	 */
	@Override
	public String toString()
	{
		return "Accuracy: " + accuracy.toString();
	}

	/**
	 * 返回正确率，查准率，查全率以及F值的字符串表示
	 * 
	 * @return 字符串表示
	 */
	public String printARPF()
	{
		return "Accuracy: " + accuracy.toString() + "\n" + "Precision: " + Double.toString(getPrecisionScore()) + "\n"
				+ "Recall: " + Double.toString(getRecallScore()) + "\n" + "F-Measure: "
				+ Double.toString(getFMeasure());
	}

	/**
	 * 计算TP值
	 * 
	 * @param reference
	 *            黄金标准
	 * @param predictions
	 *            预测值
	 * @return TP值
	 */
	static int countTruePositives(final String reference, final String prediction)
	{

		int truePositives = 0;

		if (reference.equals("+1") && prediction.equals("+1"))
		{
			++truePositives;
		}

		return truePositives;

	}

}
