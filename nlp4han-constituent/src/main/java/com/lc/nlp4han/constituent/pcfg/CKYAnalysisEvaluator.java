package com.lc.nlp4han.constituent.pcfg;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.Evaluator;

public class CKYAnalysisEvaluator extends Evaluator<ConstituentTree>
{
	/**
	 * 句法分析模型得到一颗句法树
	 */
	private ConstituentParserCKYOfP2NFImproving cky;
	
	/**
	 * 句法树中的短语分析评估
	 */
	private ConstituentMeasure measure;
	public ConstituentMeasure getMeasure()
	{
		return measure;
	}
	public void setMeasure(ConstituentMeasure measure)
	{
		this.measure = measure;
	}
	public CKYAnalysisEvaluator(ConstituentParserCKYOfP2NFImproving cky)
	{
		this.cky = cky;
	}
	@Override
	protected ConstituentTree processSample(ConstituentTree sample)
	{
		TreeNode rootNodeRef=sample.getRoot();
		String[] words=GetWordsFromTree.getetWordsFromTree(rootNodeRef);
		ConstituentTree treePre=cky.parseTree(words);	
		try
		{
			measure.update(rootNodeRef,treePre.getRoot());
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return treePre;
	}

}
