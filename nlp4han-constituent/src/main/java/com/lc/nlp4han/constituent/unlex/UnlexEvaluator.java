package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.Arrays;

import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentParser;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.pcfg.ConstituentParserCKYP2NF;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.constituent.pcfg.TreeNodeUtil;
import com.lc.nlp4han.ml.util.Evaluator;

/**
 * @author 王宁
 * 
 */
public class UnlexEvaluator extends Evaluator<ConstituentTree>
{
	/**
	 * 句法分析模型得到一颗句法树
	 */
	private ConstituentParser cky;

	/**
	 * 句法树中的短语分析评估
	 */
	private ConstituentMeasure measure;

	private NonterminalTable nonterminalTable;
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

	public UnlexEvaluator(ConstituentParser cky, NonterminalTable nonterminalTable)
	{
		this.nonterminalTable = nonterminalTable;
		this.cky = cky;
	}

	public UnlexEvaluator(PCFG p2nf, NonterminalTable nonterminalTable, double pruneThreshold, boolean secondPrune,
			boolean prior)
	{
		this.cky = new ConstituentParserCKYP2NF(p2nf, pruneThreshold, secondPrune, prior);
		this.nonterminalTable = nonterminalTable;
	}

	@Override
	protected ConstituentTree processSample(ConstituentTree sample)
	{
		TreeNode rootNodeRef = sample.getRoot();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> poses = new ArrayList<String>();
		TreeNodeUtil.getWordsAndPOSFromTree(words, poses, rootNodeRef);
		if (poses.size() > 15)
			return null;
		String[] words1 = new String[words.size()];
		for (int i = 0; i < words.size(); i++)
		{
			words1[i] = words.get(i);
		}
		String[] poses1;
		String[][] allPoses = getAllPosesArr(poses.toArray(new String[poses.size()]));

		long start = System.currentTimeMillis();
		ArrayList<ConstituentTree> allTree = new ArrayList<ConstituentTree>();

		count++;
		for (String[] arr : allPoses)
		{
			poses1 = arr;
			ConstituentTree treePre = cky.parse(words1, poses1);
			if (treePre != null)
			{
				Binarization.recoverBinaryTree(treePre.getRoot());
				TreeUtil.removeLatentLabel(treePre.getRoot());
				allTree.add(treePre);
			}
		}
		long thisTime = System.currentTimeMillis() - start;
		totalTime += thisTime;
		System.out.println(
				"句子长度：" + words.size() + " 平均解析时间：" + (totalTime / count) + "ms" + " 本句解析时间：" + thisTime + "ms");
		try
		{
			if (allTree.size() == 0)
			{
				System.out.println("无法解析的句子： " + rootNodeRef.toString());
				measure.countNodeDecodeTrees(null);
				measure.update(rootNodeRef, new TreeNode());
			}
			else
			{
				measure.update(rootNodeRef, allTree.get(0).getRoot());
			}
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return allTree.get(0);
	}

	public String[][] getAllPosesArr(String[] poses)
	{
		ArrayList<ArrayList<String>> allPosesArr = new ArrayList<>();
		int[] nSubSymbols = new int[poses.length];
		for (int i = 0; i < nSubSymbols.length; i++)
		{
			nSubSymbols[i] = nonterminalTable.getNumSubsymbolArr().get(nonterminalTable.intValue(poses[i]));
		}
		getAllPosesArrHelper(allPosesArr, new ArrayList<String>(Arrays.asList(poses)), nSubSymbols);
		String arr[][] = new String[allPosesArr.size()][];
		int index = 0;
		for (ArrayList<String> list : allPosesArr)
		{
			arr[index] = list.toArray(new String[list.size()]);
			index++;
		}
		return arr;
	}

	public static void getAllPosesArrHelper(ArrayList<ArrayList<String>> allPosesArr, ArrayList<String> poses,
			int[] nSubSymbols)
	{
		if (poses.size() == 0)
			return;
		String currentPos = poses.get(poses.size() - 1);
		int nSubSymbol = nSubSymbols[poses.size() - 1];
		if (poses.size() == 1)
		{
			if (nSubSymbol == 1)
			{
				ArrayList<String> list = new ArrayList<>();
				list.add(currentPos);
			}
			else
			{
				for (int i = 0; i < nSubSymbol; i++)
				{
					ArrayList<String> list = new ArrayList<>();
					list.add(currentPos + "_" + i);
					allPosesArr.add(list);
				}
			}
			return;
		}
		else
		{
			ArrayList<String> tempPoses = new ArrayList<>(poses);
			tempPoses.remove(tempPoses.size() - 1);
			getAllPosesArrHelper(allPosesArr, tempPoses, nSubSymbols);
		}

		if (nSubSymbol == 1)
		{
			for (ArrayList<String> aList : allPosesArr)
			{
				aList.add(currentPos);
			}
		}
		else
		{
			int size = allPosesArr.size();
			for (int i = 1; i < nSubSymbol; i++)
			{
				for (int j = 0; j < size; j++)
				{
					ArrayList<String> newList = new ArrayList<>(allPosesArr.get(j));
					newList.add(currentPos + "_" + i);
					allPosesArr.add(newList);
				}
			}
			for (int i = 0; i < size; i++)
			{
				allPosesArr.get(i).add(currentPos + "_" + 0);
			}
		}
	}

	public static void main(String[] args)
	{
		String poses[] = { "A", "B", "C", "D" };
		ArrayList<ArrayList<String>> allPosesArr = new ArrayList<>();
		int[] nSubSymbols = { 3, 2, 1, 2 };
		getAllPosesArrHelper(allPosesArr, new ArrayList<String>(Arrays.asList(poses)), nSubSymbols);
		for (ArrayList<String> arr : allPosesArr)
		{
			System.out.println(arr);
		}
	}
}
