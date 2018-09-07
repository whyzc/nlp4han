package com.lc.nlp4han.constituent.lex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lc.nlp4han.constituent.HeadRule;

/**
 * 用于PTB的生成头结点的规则集
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
// TODO: 创建适合于CTB的头规则集
public class HeadRuleSetPTB extends HeadRuleSet
{

	private static List<String> ADJP = new ArrayList<String>();
	private static List<String> ADVP = new ArrayList<String>();
	private static List<String> CONJP = new ArrayList<String>();
	private static List<String> LST = new ArrayList<String>();
	private static List<String> NAC = new ArrayList<String>();
	private static List<String> PP = new ArrayList<String>();
	private static List<String> PRT = new ArrayList<String>();
	private static List<String> QP = new ArrayList<String>();
	private static List<String> RRC = new ArrayList<String>();
	private static List<String> S = new ArrayList<String>();
	private static List<String> SBAR = new ArrayList<String>();
	private static List<String> SBARQ = new ArrayList<String>();
	private static List<String> SINV = new ArrayList<String>();
	private static List<String> SQ = new ArrayList<String>();
	private static List<String> VP = new ArrayList<String>();
	private static List<String> WHADJP = new ArrayList<String>();
	private static List<String> WHADVP = new ArrayList<String>();
	private static List<String> WHNP = new ArrayList<String>();
	private static List<String> WHPP = new ArrayList<String>();
	// NP
	private static List<String> LEFT2RIGHT = new ArrayList<String>();
	private static List<String> RIGHT2LEFT1 = new ArrayList<String>();
	private static List<String> RIGHT2LEFT2 = new ArrayList<String>();
	private static List<String> RIGHT2LEFT3 = new ArrayList<String>();
	private static List<String> RIGHT2LEFT4 = new ArrayList<String>();
	private static HashMap<String, HeadRule> normalRules = new HashMap<>();
	private static HashMap<String, List<HeadRule>> specialRules = new HashMap<>();
	// 静态代码块
	static
	{
		String[] ADJPStr = { "NNS", "QP", "NN", "$", "ADVP", "JJ", "VBN", "VBG", "ADJP", "JJR", "NP", "JJS", "DT", "FW",
				"RBR", "RBS", "SBAR", "RB" };
		for (int i = 0; i < ADJPStr.length; i++)
		{
			ADJP.add(ADJPStr[i]);
		}
		normalRules.put("ADJP", new HeadRule(ADJP, "left"));

		String[] ADVPStr = { "RB", "RBR", "RBS", "FW", "ADVP", "TO", "CD", "JJR", "JJ", "IN", "NP", "JJS", "NN" };
		for (int i = 0; i < ADVPStr.length; i++)
		{
			ADVP.add(ADVPStr[i]);
		}
		normalRules.put("ADVP", new HeadRule(ADVP, "right"));

		String[] CONJPStr = { "CC", "RB", "IN" };
		for (int i = 0; i < CONJPStr.length; i++)
		{
			CONJP.add(CONJPStr[i]);
		}
		normalRules.put("CONJP", new HeadRule(CONJP, "right"));

		normalRules.put("FRAG", new HeadRule(new ArrayList<>(), "right"));

		normalRules.put("INTJ", new HeadRule(new ArrayList<>(), "left"));

		String[] LSTStr = { "LS", ":" };
		for (int i = 0; i < LSTStr.length; i++)
		{
			LST.add(LSTStr[i]);
		}
		normalRules.put("LST", new HeadRule(LST, "right"));

		String[] NACStr = { "NN", "NNS", "NNP", "NNPS", "NP", "NAC", "EX", "$", "CD", "QP", "PRP", "VBG", "JJ", "JJS",
				"JJR", "ADJP", "FW" };
		for (int i = 0; i < NACStr.length; i++)
		{
			NAC.add(NACStr[i]);
		}
		normalRules.put("NAC", new HeadRule(NAC, "left"));

		String[] PPStr = { "IN", "TO", "VBG", "VBN", "RP", "FW" };
		for (int i = 0; i < PPStr.length; i++)
		{
			PP.add(PPStr[i]);
		}
		normalRules.put("PP", new HeadRule(PP, "right"));

		normalRules.put("PRN", new HeadRule(new ArrayList<>(), "left"));

		String[] PRTStr = { "RP" };
		for (int i = 0; i < PRTStr.length; i++)
		{
			PRT.add(PRTStr[i]);
		}
		normalRules.put("PRT", new HeadRule(PRT, "right"));

		String[] QPStr = { "$", "IN", "NNS", "NN", "JJ", "RB", "DT", "CD", "NCD", "QP", "JJR", "JJS" };
		for (int i = 0; i < QPStr.length; i++)
		{
			QP.add(QPStr[i]);
		}
		normalRules.put("QP", new HeadRule(QP, "left"));

		String[] RRCStr = { "VP", "NP", "ADVP", "ADJP", "PP" };
		for (int i = 0; i < RRCStr.length; i++)
		{
			RRC.add(RRCStr[i]);
		}
		normalRules.put("RRC", new HeadRule(RRC, "right"));

		String[] SStr = { "TO", "IN", "VP", "S", "SBAR", "ADJP", "UCP", "NP" };
		for (int i = 0; i < SStr.length; i++)
		{
			S.add(SStr[i]);
		}
		normalRules.put("S", new HeadRule(S, "left"));

		String[] SBARStr = { "WHNP", "WHPP", "WHADVP", "IN", "DT", "S", "SQ", "SINV", "SBAR", "FRAG" };
		for (int i = 0; i < SBARStr.length; i++)
		{
			SBAR.add(SBARStr[i]);
		}
		normalRules.put("SBAR", new HeadRule(SBAR, "left"));

		String[] SBARQStr = { "SQ", "S", "SINV", "SBARQ", "FRAG" };
		for (int i = 0; i < SBARQStr.length; i++)
		{
			SBARQ.add(SBARQStr[i]);
		}
		normalRules.put("SBARQ", new HeadRule(SBARQ, "left"));

		String[] SINVStr = { "VBZ", "VBD", "VBP", "VB", "MD", "VP", "S", "SINV", "ADJP", "NP" };
		for (int i = 0; i < SINVStr.length; i++)
		{
			SINV.add(SINVStr[i]);
		}
		normalRules.put("SINV", new HeadRule(SINV, "left"));

		String[] SQStr = { "VBZ", "VBD", "VBP", "VB", "MD", "VP", "SQ" };
		for (int i = 0; i < SQStr.length; i++)
		{
			SQ.add(SQStr[i]);
		}
		normalRules.put("SQ", new HeadRule(SQ, "left"));

		normalRules.put("UCP", new HeadRule(new ArrayList<>(), "right"));

		String[] VPStr = { "TO", "VBD", "VBN", "MD", "VBZ", "VB", "VBG", "VBP", "VP", "ADJP", "NN", "NNS", "NP" };
		for (int i = 0; i < VPStr.length; i++)
		{
			VP.add(VPStr[i]);
		}
		normalRules.put("VP", new HeadRule(VP, "left"));

		String[] WHADJPStr = { "CC", "WRB", "JJ", "ADJP" };
		for (int i = 0; i < WHADJPStr.length; i++)
		{
			WHADJP.add(WHADJPStr[i]);
		}
		normalRules.put("WHADJP", new HeadRule(WHADJP, "left"));

		String[] WHADVPStr = { "CC", "WRB" };
		for (int i = 0; i < WHADVPStr.length; i++)
		{
			WHADVP.add(WHADVPStr[i]);
		}
		normalRules.put("WHADVP", new HeadRule(WHADVP, "right"));

		String[] WHNPStr = { "WDT", "WP", "WP$", "WHADJP", "WHPP", "WHNP" };
		for (int i = 0; i < WHNPStr.length; i++)
		{
			WHNP.add(WHNPStr[i]);
		}
		normalRules.put("WHNP", new HeadRule(WHNP, "left"));

		String[] WHPPStr = { "IN", "TO", "FW" };
		for (int i = 0; i < WHPPStr.length; i++)
		{
			WHPP.add(WHPPStr[i]);
		}
		normalRules.put("WHPP", new HeadRule(WHPP, "right"));

		// 下面是NP的部分
		String[] NPStr1 = { "NN", "NNP", "NNPS", "NNS", "NX", "POS", "JJR" };
		for (int i = 0; i < NPStr1.length; i++)
		{
			RIGHT2LEFT1.add(NPStr1[i]);
		}
		String[] NPStr2 = { "NP" };
		for (int i = 0; i < NPStr2.length; i++)
		{
			LEFT2RIGHT.add(NPStr2[i]);
		}
		String[] NPStr3 = { "$", "ADJP", "PRN" };
		for (int i = 0; i < NPStr3.length; i++)
		{
			RIGHT2LEFT2.add(NPStr3[i]);
		}
		String[] NPStr4 = { "CD" };
		for (int i = 0; i < NPStr4.length; i++)
		{
			RIGHT2LEFT3.add(NPStr4[i]);
		}
		String[] NPStr5 = { "JJ", "JJS", "RB", "QP" };
		for (int i = 0; i < NPStr5.length; i++)
		{
			RIGHT2LEFT4.add(NPStr5[i]);
		}
		List<HeadRule> NPRule = new ArrayList<>();
		NPRule.add(new HeadRule(RIGHT2LEFT1, "left"));
		NPRule.add(new HeadRule(LEFT2RIGHT, "right"));
		NPRule.add(new HeadRule(RIGHT2LEFT2, "left"));
		NPRule.add(new HeadRule(RIGHT2LEFT3, "left"));
		NPRule.add(new HeadRule(RIGHT2LEFT4, "left"));
		specialRules.put("NP", NPRule);
	}

	/**
	 * 获取常规的规则
	 * 
	 * @return
	 */
	public HashMap<String, HeadRule> getNormalRuleSet()
	{
		return normalRules;
	}

	/**
	 * 获取特殊的规则
	 * 
	 * @return
	 */
	public HashMap<String, List<HeadRule>> getSpecialRuleSet()
	{
		return specialRules;
	}

}
