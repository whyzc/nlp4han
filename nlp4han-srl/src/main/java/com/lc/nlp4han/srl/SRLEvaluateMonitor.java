package com.lc.nlp4han.srl;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.EvaluationMonitor;

/**
 * 评估检测器
 * @author 王馨苇
 *
 */
public class SRLEvaluateMonitor implements EvaluationMonitor<SRLSample<HeadTreeNode>>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void correctlyClassified(SRLSample<HeadTreeNode> arg0, SRLSample<HeadTreeNode> arg1) {
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void missclassified(SRLSample<HeadTreeNode> arg0, SRLSample<HeadTreeNode> arg1) {
		
	}

}

