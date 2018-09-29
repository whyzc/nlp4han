package com.lc.nlp4han.dependency.tb;

import com.lc.nlp4han.dependency.DependencyParser;
import com.lc.nlp4han.ml.util.SequenceValidator;

/**
 * @author 作者
 * @version 创建时间：2018年8月27日 上午6:34:42 类说明
 */
public class DependencyParseSequenceValidatorArcStandard implements SequenceValidator<String>
{

	@Override
	public boolean validSequence(int indexOfCurrentConf, String[] wordpos, String[] priorOutcomes, String preOutcome)
	{
		ConfigurationArcStandard conf = new ConfigurationArcStandard();
		conf.generateConfByActions(wordpos, priorOutcomes);

		Action preAct = Action.toType(preOutcome);
		if (preAct != null)
		{
			if (preAct.getBaseAction().equals("LEFTARC_REDUCE"))
			{
				if (conf.getStack().size() <= 2)
				{
					// System.out.println("LEFTARC_REDUCE返回false");
					return false;
				}

				else
				{
					// System.out.println("LEFTARC_REDUCE返回true");
					return true;
				}

			}
			else if (preAct.getBaseAction().equals("RIGHTARC_REDUCE"))
			{

				if (conf.getStack().size() <= 1)
				{
					// System.out.println("RIGHTARC_REDUCE返回false");
					return false;
				}
				else if (conf.getStack().size() == 2)
				{
					if (conf.getWordsBuffer().size() != 0)// 因为ArcStanfard最后一次操作必然是 核心成分/RIGHTARC_REDUCE
						return false;
					for (int i = 0; i < conf.getArcs().size(); i++)
					{
						if (conf.getArcs().get(i).getHead().getWord().equals(DependencyParser.RootWord))
						{
							// System.out.println("RIGHTARC_REDUCE返回false");
							return false;
						}
					}
					// System.out.println("RIGHTARC_REDUCE返回true");
					return true;
				}
				else
				{
					if (preAct.getRelation().equals(DependencyParser.RootDep))
					{
						return false;
					}

					return true;
				}
			}
			else if (preAct.getBaseAction().equals("SHIFT"))
			{
				if (conf.getWordsBuffer().isEmpty())
				{
					// System.out.println("因为buffer位空，故不能有SHIFT操作");
					return false;
				}
				else
				{
					// System.out.println("shift返回true");
					return true;
				}

			}
			else
			{
				// System.out.println("错误");
				// System.out.println(preAct.getBaseAction());
				return false;
			}
		}
		return false;
	}

}
