package com.lc.nlp4han.constituent.maxent;

import com.lc.nlp4han.ml.util.EvaluationMonitor;

/**
 * 评估的监测类
 * @author 王馨苇
 *
 */
public class ParserEvaluateMonitor implements EvaluationMonitor<ConstituentTreeSample>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void correctlyClassified(ConstituentTreeSample arg0, ConstituentTreeSample arg1) {
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void missclassified(ConstituentTreeSample arg0, ConstituentTreeSample arg1) {
		
	}

}
