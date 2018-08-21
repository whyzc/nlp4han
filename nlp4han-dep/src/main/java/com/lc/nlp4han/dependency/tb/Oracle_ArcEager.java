package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.ModelWrapper;

/**
 * action预测
 * 
 * TODO: 通过Configuration得到ActionType
 * 
 * @author 王宁
 *
 */
public class Oracle_ArcEager
{
	private ClassificationModel model;
	private DependencyParseContextGenerator contextGenerator;
	
	public Oracle_ArcEager(ClassificationModel model, DependencyParseContextGenerator contextGenerator)
	{
		this.model = model;
		this.contextGenerator = contextGenerator;
	}

	public ActionType classify(Configuration_ArcEager currentConf, String[] priorDecisions,
			Object[] additionalContext)
	{// 将当前的Configuration分类

		String[] context = ((DependencyParseContextGeneratorConf_ArcEager)contextGenerator).getContext(currentConf,priorDecisions,null);
		double allPredicates[] = model.eval(context);
		String tempAllType[] = new String[allPredicates.length];// 存储所有的分类

		for (int k = 0; k < allPredicates.length; k++)
		{
			tempAllType[k] = model.getOutcome(k);
		}

		int indexOfBestOutcome = getBestIndexOfOutcome(allPredicates);
		while (!SimpleValidator.validate(currentConf, tempAllType[indexOfBestOutcome]))
		{// ActionType不符合依存转换关系
			allPredicates[indexOfBestOutcome] = -1;
			indexOfBestOutcome = getBestIndexOfOutcome(allPredicates);
		}

		ActionType action = ActionType.toType(tempAllType[indexOfBestOutcome]);
		return action;
	}

	private int getBestIndexOfOutcome(double[] scores)
	{// ActionType不符合依存转换关系，索引best上的值重置为-1
		int best = 0;
		for (int i = 1; i < scores.length; i++)
			if (scores[i] > scores[best])
				best = i;
		return best;
	}

}
