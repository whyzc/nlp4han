package org.nlp4han.coref.hobbs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nlp4han.coref.centering.CenteringBFP;
import org.nlp4han.coref.centering.EvaluationBFP;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * 评价类
 * 
 * @author 杨智超
 *
 */
public class EvaluationHobbs extends AbstractEvaluation
{
	public static int total = 0;				//所有样本的数量,公有属性，子类可继承使用
	public static int correctNumber = 0;		//符合预期的数量,公有属性，子类可继承使用
	private Hobbs hobbs = new Hobbs();
	private static AttributeFilter attributeFilter = new AttributeFilter(new PNFilter(new NodeNameFilter())); // 组合过滤器
	
	static
	{
		attributeFilter.setAttributeGenerator(new AttributeGeneratorByDic()); // 装入属性生成器
	}
	public static void main(String[] args)
	{
		EvaluationHobbs e = new EvaluationHobbs();
		e.parse(args);
		System.out.println("成功：" + correctNumber + "/" + total);
	}
	

//	public void processingData(String oneLine)
//	{
//
//		if (oneLine.startsWith("#"))
//		{
//			int num = oneInput.size();
//			for (int i = 0; i < num - 2; i++)
//			{
//				String str = oneInput.get(i);
//				TreeNode si = BracketExpUtil.generateTree("(" + str + ")");
//				constituentTrees.add(si);
//			}
//			TreeNode pronoun = getPronoun(constituentTrees, oneInput.get(num - 2));
//			TreeNode goal = BracketExpUtil.generateTree("(" + oneInput.get(num - 1) + ")");
//
//			AttributeFilter attributeFilter = new AttributeFilter(new PNFilter(new NodeNameFilter())); // 组合过滤器
//			attributeFilter.setAttributeGenerator(new AttributeGeneratorByDic()); // 装入属性生成器
//			attributeFilter.setReferenceNode(pronoun);
//			hobbs.setFilter(attributeFilter);
//			TreeNode result = hobbs.hobbs(constituentTrees, pronoun);
//			total++;
//			String strOfGoal = TreeNodeUtil.getString(goal);
//			String strOfResult = TreeNodeUtil.getString(result);
//			String headStrOfGoal = TreeNodeUtil.getString(TreeNodeUtil.getHead(goal, NPHeadRuleSetPTB.getNPRuleSet()));
//			String headStrOfResult = TreeNodeUtil
//					.getString(TreeNodeUtil.getHead(result, NPHeadRuleSetPTB.getNPRuleSet()));
//			if (strOfResult.contains(strOfGoal) && headStrOfGoal.equals(headStrOfResult))
//				correctNumber++;
//			else
//			{
//				System.out.println("第" + total + "个未通过：");
//				System.out.println("\t\tHobbs结果：" + strOfResult);
//				System.out.println("\t\t预期结果：" + strOfGoal);
//			}
//
//			constituentTrees.clear();
//			oneInput.clear();
//		}
//		else
//			oneInput.add(oneLine);
//	}
//
//	private TreeNode getPronoun(List<TreeNode> trees, String siteInfor)
//	{
//		String[] str1 = siteInfor.split("-");
//		int site1 = Integer.valueOf(str1[0]);
//		int site2 = Integer.valueOf(str1[1]);
//		String word = str1[2].trim();
//		List<TreeNode> candidates = TreeNodeUtil.getNodesWithSpecified(trees.get(site1), new String[] { "PN" });
//		TreeNode result = null;
//
//		for (TreeNode node : candidates)
//		{
//			if (node.getChildName(0).equals(word))
//			{
//				site2--;
//				if (site2 < 0)
//				{
//					result = node;
//					break;
//				}
//			}
//		}
//		return result;
//	}


	@Override
	public void processAnEvaluationSample(List<String> information1, List<String> information2,
			List<String> information3)
	{
		List<TreeNode> constituentTrees = new ArrayList<TreeNode>();
		List<TreeNode> prons = new ArrayList<TreeNode>();
		List<String> results = new ArrayList<String>();
		
		Pattern r;
		Matcher m;
		
		for (int i=0 ; i<information1.size() ; i++)
		{
			String str = information1.get(i);
			TreeNode ui = BracketExpUtil.generateTree("(" + str + ")");
			constituentTrees.add(ui);
		}
		
		for (int i=0 ; i<information2.size() ; i++)
		{//找出所有需要进行消解的代词结点
			String infor = (information2.get(i).split(CenteringBFP.SEPARATOR, 2))[0];
			r = Pattern.compile(".*(?=\\()");
			m = r.matcher(infor);
			String word = null;
			if (m.find( )) {
				 word = m.group(0);
			}
			r = Pattern.compile("(?<=\\().*-.*(?=\\))");
			m = r.matcher(infor);
			String[] sites = null;
			if (m.find( )) {
		         sites = m.group(0).split("-");
			}
			int site1 = Integer.parseInt(sites[0]);
			int site2 = Integer.parseInt(sites[1]);
			TreeNode pronLeaf = TreeNodeUtil.string2Node(word, site2-1, constituentTrees.get(site1-1));
			TreeNode pronNode = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(pronLeaf, "PN");
			prons.add(pronNode);
		}
		
		for (int i=0 ; i<prons.size() ; i++)
		{
			
			attributeFilter.setReferenceNode(prons.get(i));
			hobbs.setFilter(attributeFilter);
			TreeNode result = hobbs.hobbs(constituentTrees, prons.get(i));
			String resultStr = hobbs.resultStr(prons.get(i), result);
			results.add(resultStr);
		}
		total++;
		if (EvaluationBFP.compare(results, information2, total))
			correctNumber++;
		
	}


	@Override
	public boolean extractAnEvaluationSample(String oneLine, List<String> information1, List<String> information2,
			List<String> information3)
	{
		if (!oneLine.startsWith("#"))
		{
			if (oneLine.startsWith("("))
			{// 括号表达式
				information1.add(oneLine);
			}
			else if (oneLine.matches(".+"+CenteringBFP.SEPARATOR+".+"))
			{// 指代结果
				information2.add(oneLine);
			}
			return false;
		}
		else
			return true;
	}
}
