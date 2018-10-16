package com.lc.nlp4han.dependency.tb;

import com.lc.nlp4han.dependency.DependencyParser;

/**
 * 基于转换的依存关系验证
 */
public class DependencyTBValidator
{
	/**
	 * @param conf
	 *            当前的配置
	 * @param outCome
	 *            基于当前配置的预测结果
	 * @return 返回预测结果的合法性
	 */
	public static boolean validate(ConfigurationArcEager conf, String outCome)// 需要检查
	{
		// System.out.println("执行了validate方法");
		Action preAct = Action.toType(outCome);
		if (preAct != null)
		{
			if (preAct.getBaseAction().equals("LEFTARC_REDUCE"))
			{// 确保一个单词的中心词只能有一个
				if (conf.getStack().peek().getIndexOfWord() == 0)
					return false;
				if (conf.getWordsBuffer().size() == 0)
					return false;
				for (Arc arc : conf.getArcs())
				{
					if (arc.getDependent() == conf.getStack().peek())
					{
						// System.out.println("因为栈顶单词已经有中心词故不能有LEFTARC_REDUCE操作");
						return false;
					}
				}
				return true;
			}

			if (preAct.getBaseAction().equals("RIGHTARC_SHIFT"))
			{
				if (conf.getWordsBuffer().isEmpty())
				{
					// System.out.println("因为buffer位空，故不能有RIGHTARC_SHIFT操作");
					return false;
				}
				if (preAct.getRelation().equals(DependencyParser.RootDep))// 分类中只有rightarc才有可能是核心成分
				{// 确保“核心”只能作为一个词语的中心词
					if (conf.getStack().peek().getIndexOfWord() != 0)
					{
						return false;
					}
					for (Arc arc : conf.getArcs())
					{
						if (arc.getRelation().equals(DependencyParser.RootDep))
							return false;
					}
					return true;
				}
				else
				{
					return true;
				}
			}

			if (preAct.getBaseAction().equals("SHIFT"))
			{
				if (conf.getWordsBuffer().isEmpty())
				{
					// System.out.println("因为buffer位空，故不能有SHIFT操作");
					return false;
				}
				else
					return true;
			}

			if (preAct.getBaseAction().equals("REDUCE"))
			{
				if (conf.getStack().peek().getIndexOfWord() != 0 && conf.getWordsBuffer().size() == 0)
					return true;
				if (conf.getStack().peek().getIndexOfWord() == 0)
				{
					// System.out.println("因为栈顶是ROOT，故不能有REDUCE操作");
					return false;
				}

				for (Arc arc : conf.getArcs())
				{
					if (arc.getHead().getIndexOfWord() == conf.getStack().peek().getIndexOfWord()
							|| arc.getDependent().getIndexOfWord() == conf.getStack().peek().getIndexOfWord())
					{
						return true;
					}
				}
				// System.out.println("因为栈顶单词还没有建立依存关系，故不能有REDUCE操作");
				return false;
			}

		}
		return false;
	}

	public static boolean validate(ConfigurationArcStandard conf, String outCome)
	{
		Action preAct = Action.toType(outCome);
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
