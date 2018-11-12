package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 表示树库
 * 
 * @author 王宁
 */
public class TreeBank
{
	public static NonterminalTable nonterminalTable;
	private ArrayList<AnnotationTreeNode> treeBank;

	public TreeBank(ArrayList<AnnotationTreeNode> treeBank, NonterminalTable nonterminalTable)
	{
		this.treeBank = treeBank;
		this.nonterminalTable = nonterminalTable;
	}

	/**
	 * 根据树上任一节点内外向概率计算该句子的概率
	 * 
	 * @param node
	 * @return
	 */
	public static double calculateSentenceSocre(AnnotationTreeNode node)
	{
		if (node.getLabel().getInnerScores() == null || node.getLabel().getOuterScores() == null)
			throw new Error("没有计算树上节点的内外向概率。");
		if (node.isLeaf())
			throw new Error("不能利用叶子节点计算内外向概率。");
		double sentenceScore = 0.0;
		Double[] innerScore = node.getLabel().getInnerScores();
		Double[] outerScores = node.getLabel().getOuterScores();
		for (int i = 0; i < innerScore.length; i++)
		{
			sentenceScore += innerScore[i] * outerScores[i];
		}
		return sentenceScore;
	}

	/**
	 * 计算树上所有节点的所有隐藏节点的内向概率
	 * 
	 * @param g
	 *            语法
	 * @param tree
	 *            树根
	 */
	public static void calculateInnerScore(Grammar g, AnnotationTreeNode tree)
	{
		if (tree.isLeaf())
		{
			return;
		}
		for (AnnotationTreeNode child : tree.getChildren())
		{
			calculateInnerScore(g, child);
		}

		if (tree.isPreterminal())
		{
			final PreterminalRule tempPreRule = new PreterminalRule(tree.getLabel().getSymbol(),
					tree.getChildren().get(0).getLabel().getWord());
			if (g.lexicon.getPreRules().contains(tempPreRule))
			{
				int length = tree.getLabel().getNumSubSymbol();

				PreterminalRule realRule = g.preRuleBySameHead.get(tree.getLabel().getSymbol()).get(tempPreRule);
				tree.getLabel().setInnerScores(realRule.getScores().toArray(new Double[length]));
				// for (int i = 0; i < tree.getLabel().getInnerScores().length; i++)
				// {
				// System.out.println(nonterminalTable.stringValue(tree.getLabel().getSymbol())
				// + "["
				// + tree.getLabel().getSpanFrom() + "," + tree.getLabel().getSpanTo() + "]" +
				// "innerScore_"
				// + i + ":" + tree.getLabel().getInnerScores()[i]);
				// }

			}
			else
			{
				throw new Error("Error grammar: don't contains  preRule :" + tempPreRule.toString());
			}
		}
		else
		{
			switch (tree.getChildren().size())
			{
			case 1:
				final UnaryRule tempUnaryRule = new UnaryRule(tree.getLabel().getSymbol(),
						tree.getChildren().get(0).getLabel().getSymbol());
				if (g.uRules.contains(tempUnaryRule))
				{
					LinkedList<LinkedList<Double>> uRuleScores = g.uRuleBySameHead.get(tree.getLabel().getSymbol())
							.get(tempUnaryRule).getScores();
					Double[] innerScores = new Double[tree.getLabel().getNumSubSymbol()];
					tree.getLabel().setInnerScores(innerScores);
					for (int i = 0; i < innerScores.length; i++)
					{
						double innerScores_Ai = 0.0;
						for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
						{ // 规则A_i -> B_j的概率
							double A_i2B_j = uRuleScores.get(i).get(j);
							double B_jInnerScore = tree.getChildren().get(0).getLabel().getInnerScores()[j];
							innerScores_Ai = innerScores_Ai + (A_i2B_j * B_jInnerScore);
						}
						innerScores[i] = innerScores_Ai;
						// System.out.println(nonterminalTable.stringValue(tree.getLabel().getSymbol())
						// + "["
						// + tree.getLabel().getSpanFrom() + "," + tree.getLabel().getSpanTo() + "]"
						// + "innerScore_" + i + ":" + tree.getLabel().getInnerScores()[i]);
					}
					// tree.getLabel().setInnerScores(innerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  uRule :" + tempUnaryRule.toString());
				}
				break;
			case 2:
				final BinaryRule tempBRule = new BinaryRule(tree.getLabel().getSymbol(),
						tree.getChildren().get(0).getLabel().getSymbol(),
						tree.getChildren().get(1).getLabel().getSymbol());
				if (g.bRules.contains(tempBRule))
				{
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = g.bRuleBySameHead
							.get(tree.getLabel().getSymbol()).get(tempBRule).getScores();
					Double[] innerScores = new Double[tree.getLabel().getNumSubSymbol()];
					tree.getLabel().setInnerScores(innerScores);
					for (int i = 0; i < innerScores.length; i++)
					{
						double innerScores_Ai = 0.0;
						for (int j = 0; j < tree.getChildren().get(0).getLabel().getNumSubSymbol(); j++)
						{
							for (int k = 0; k < tree.getChildren().get(1).getLabel().getNumSubSymbol(); k++)
							{
								// 规则A_i -> B_j C_k的概率
								double A_i2B_jC_k = bRuleScores.get(i).get(j).get(k);
								double B_jInnerScore = tree.getChildren().get(0).getLabel().getInnerScores()[j];
								double C_kInnerScore = tree.getChildren().get(1).getLabel().getInnerScores()[k];
								innerScores_Ai = innerScores_Ai + (A_i2B_jC_k * B_jInnerScore * C_kInnerScore);
							}
						}
						innerScores[i] = innerScores_Ai;
						// System.out.println(nonterminalTable.stringValue(tree.getLabel().getSymbol())
						// + "["
						// + tree.getLabel().getSpanFrom() + "," + tree.getLabel().getSpanTo() + "]"
						// + "innerScore_" + i + ":" + tree.getLabel().getInnerScores()[i]);
					}
					// tree.getLabel().setInnerScores(innerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  bRule :" + tempBRule.toString());
				}
				break;
			default:
				throw new Error("Error tree: more than two children.");
			}
		}

	}

	/**
	 * tree的根标记为ROOT
	 * 
	 * @param tree
	 */
	public static void calculateOuterScore(Grammar g, AnnotationTreeNode tree)
	{
		if (tree == null)
			return;
		calculateOuterScoreHelper(g, tree, tree);
	}

	private static void calculateOuterScoreHelper(Grammar g, AnnotationTreeNode treeRoot, AnnotationTreeNode treeNode)
	{

		if (treeNode == null)
			return;
		if (treeNode.isLeaf())
			return;
		// 计算根节点的外向概率
		if (treeNode == treeRoot)
		{
			Double[] array = new Double[treeNode.getLabel().getNumSubSymbol()];
			Arrays.fill(array, 1.0);
			treeNode.getLabel().setOuterScores(array);
		}
		else
		{
			AnnotationTreeNode parent = treeNode.getParent();
			switch (parent.getChildren().size())
			{
			case 1:
				final UnaryRule tempUnaryRule = new UnaryRule(parent.getLabel().getSymbol(),
						treeNode.getLabel().getSymbol());
				if (g.uRules.contains(tempUnaryRule))
				{
					LinkedList<LinkedList<Double>> uRuleScores = g.uRuleBySameHead.get(parent.getLabel().getSymbol())
							.get(tempUnaryRule).getScores();
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int j = 0; j < outerScores.length; j++)
					{
						double outerScores_Bj = 0.0;
						for (int i = 0; i < parent.getLabel().getNumSubSymbol(); i++)
						{
							double A_i2B_j = uRuleScores.get(i).get(j);
							double A_iOuterScore = parent.getLabel().getOuterScores()[i];
							outerScores_Bj = outerScores_Bj + (A_i2B_j * A_iOuterScore);
						}
						outerScores[j] = outerScores_Bj;
						// System.out.println(nonterminalTable.stringValue(treeNode.getLabel().getSymbol())
						// + "["
						// + treeNode.getLabel().getSpanFrom() + "," + treeNode.getLabel().getSpanTo() +
						// "]"
						// + "outerScore_" + j + ":" + outerScores_Bj);
					}
					treeNode.getLabel().setOuterScores(outerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  uRule :" + tempUnaryRule.toString());
				}

				break;
			case 2:
				// 获取兄弟节点的内向概率
				Double[] siblingNode_InScore;
				final BinaryRule tempBRule;
				if (parent.getChildren().get(0) == treeNode)
				{
					siblingNode_InScore = parent.getChildren().get(1).getLabel().getInnerScores();
					tempBRule = new BinaryRule(parent.getLabel().getSymbol(), treeNode.getLabel().getSymbol(),
							parent.getChildren().get(1).getLabel().getSymbol());
				}
				else
				{
					siblingNode_InScore = parent.getChildren().get(0).getLabel().getInnerScores();
					tempBRule = new BinaryRule(parent.getLabel().getSymbol(),
							parent.getChildren().get(0).getLabel().getSymbol(), treeNode.getLabel().getSymbol());
				}

				if (g.bRules.contains(tempBRule))
				{
					LinkedList<LinkedList<LinkedList<Double>>> bRuleScores = g.bRuleBySameHead
							.get(parent.getLabel().getSymbol()).get(tempBRule).getScores();
					Double[] outerScores = new Double[treeNode.getLabel().getNumSubSymbol()];
					for (int i = 0; i < outerScores.length; i++)
					{
						double outerScoreB_i = 0.0;
						for (int j = 0; j < parent.getLabel().getNumSubSymbol(); j++)
						{
							double A_jOuterscore = parent.getLabel().getOuterScores()[j];
							for (int k = 0; k < siblingNode_InScore.length; k++)
							{
								double C_kInnerScore = siblingNode_InScore[k];
								if (parent.getChildren().get(0) == treeNode)
								{

									double A_j2B_iC_k = bRuleScores.get(j).get(i).get(k);
									outerScoreB_i = outerScoreB_i + (A_j2B_iC_k * A_jOuterscore * C_kInnerScore);

								}
								else
								{
									double A_j2C_kB_i = bRuleScores.get(j).get(k).get(i);
									outerScoreB_i = outerScoreB_i + (A_j2C_kB_i * A_jOuterscore * C_kInnerScore);
								}

							}
						}
						// System.out.println(nonterminalTable.stringValue(treeNode.getLabel().getSymbol())
						// + "["
						// + treeNode.getLabel().getSpanFrom() + "," + treeNode.getLabel().getSpanTo() +
						// "]"
						// + "outerScore_" + i + ":" + outerScoreB_i);
						outerScores[i] = outerScoreB_i;
					}
					treeNode.getLabel().setOuterScores(outerScores);
				}
				else
				{
					throw new Error("Error grammar: don't contains  bRule :" + tempBRule.toString());
				}

				break;
			default:
				throw new Error("error tree:more than two children.");
			}
		}
		for (AnnotationTreeNode childNode : treeNode.getChildren())
		{
			calculateOuterScoreHelper(g, treeRoot, childNode);
		}
	}

	public void forgetIOScore()
	{
		for (AnnotationTreeNode tree : treeBank)
		{
			tree.forgetIOScore();
		}
	}

	public NonterminalTable getNonterminalTable()
	{
		return nonterminalTable;
	}

	public void setNonterminalTable(NonterminalTable nonterminalTable)
	{
		this.nonterminalTable = nonterminalTable;
	}

	public ArrayList<AnnotationTreeNode> getTreeBank()
	{
		return treeBank;
	}

	public void setTreeBank(ArrayList<AnnotationTreeNode> treeBank)
	{
		this.treeBank = treeBank;
	}

}
