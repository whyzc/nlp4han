package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.Evaluator;

public class CKYParserEvaluator extends Evaluator<ConstituentTree>
{
	/**
	 * 句法分析模型得到一颗句法树
	 */
	private ConstituentParser cky;

	/**
	 * 句法树中的短语分析评估
	 */
	private ConstituentMeasure measure;
	
	private long count = 0;
	private long totalTime = 0;

	public ConstituentMeasure getMeasure()
	{
		return measure;
	}

	public void setMeasure(ConstituentMeasure measure)
	{
		this.measure = measure;
	}

	public CKYParserEvaluator(ConstituentParser cky)
	{
		this.cky = cky;
	}

	public CKYParserEvaluator(PCFG p2nf)
	{
		this.cky = new ConstituentParserCKYOfP2NFImproving(p2nf);
	}

	@Override
	protected ConstituentTree processSample(ConstituentTree sample)
	{
		TreeNode rootNodeRef = sample.getRoot();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> poses = new ArrayList<String>();
		TreeNodeUtil.getWordsAndPOSFromTree(words, poses, rootNodeRef);
		
		String[] words1 = new String[words.size()];
		String[] poses1 = new String[poses.size()];
		for (int i = 0; i < words.size(); i++)
		{
			words1[i] = words.get(i);
			poses1[i] = poses.get(i);
		}
		
		long start = System.currentTimeMillis();
		
		ConstituentTree treePre = cky.parseTree(words1, poses1);
		
		totalTime += (System.currentTimeMillis() - start);	
		count++;
		
		System.out.println("平均解析时间：" + (totalTime/count) + "ms");
		
		try
		{
			if (treePre == null)
			{
				measure.countNodeDecodeTrees(null);
				measure.update(rootNodeRef, new TreeNode());
			}
			else
			{
				measure.update(rootNodeRef, treePre.getRoot());
			}
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
		return treePre;
	}

}
