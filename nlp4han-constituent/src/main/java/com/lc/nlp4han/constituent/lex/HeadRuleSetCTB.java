package com.lc.nlp4han.constituent.lex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lc.nlp4han.constituent.HeadRule;
import com.lc.nlp4han.constituent.HeadRuleSet;

/**
 * 
 * @author qyl
 *
 */
public class HeadRuleSetCTB extends HeadRuleSet
{
	private static List<String> ADJP = new ArrayList<String>();
	private static List<String> ADVP = new ArrayList<String>();
	private static List<String> CP = new ArrayList<String>();
	private static List<String> DNP = new ArrayList<String>();
	private static List<String> DP = new ArrayList<String>();
	private static List<String> INTJ = new ArrayList<String>();
	private static List<String> IP = new ArrayList<String>();
	private static List<String> LCP = new ArrayList<String>();
	private static List<String> NP = new ArrayList<String>();
	private static List<String> PP = new ArrayList<String>();
	private static List<String> QP = new ArrayList<String>();
	private static List<String> VP = new ArrayList<String>();
	private static List<String> VV = new ArrayList<String>();
	private static List<String> VA = new ArrayList<String>();
	private static List<String> VE = new ArrayList<String>();
	private static List<String> VC = new ArrayList<String>();
	private static List<String> VCD = new ArrayList<String>();
	private static List<String> VRD = new ArrayList<String>();
	private static List<String> VSB = new ArrayList<String>();
	private static List<String> VCP = new ArrayList<String>();
	private static List<String> VNV = new ArrayList<String>();
	
	private static List<String> CLP = new ArrayList<String>();
	private static List<String> PRN = new ArrayList<String>();// 插入句子
	private static List<String> LST = new ArrayList<String>();
	private static List<String> VPT = new ArrayList<String>();
	private static List<String> DVP = new ArrayList<String>();
	private static List<String> WHPP=new ArrayList<String>();
	private static List<String> FRAG=new ArrayList<String>();
	private static List<String> UCP= new ArrayList<String>();
	private static HashMap<String, HeadRule> headRulesOfCTB = new HashMap<String, HeadRule>();
	// 静态代码块
	static
	{
		String[] ADJPStr = { "ADJP", "JJ" };
		for (int i = 0; i < ADJPStr.length; i++)
		{
			ADJP.add(ADJPStr[i]);
		}
		headRulesOfCTB.put("ADJP", new HeadRule(ADJP, "left"));

		String[] ADVPStr = { "ADVP", "AD" };
		for (int i = 0; i < ADVPStr.length; i++)
		{
			ADVP.add(ADVPStr[i]);
		}
		headRulesOfCTB.put("ADVP", new HeadRule(ADVP, "right"));

		String[] CPStr = { "CP", "IP" };
		for (int i = 0; i < CPStr.length; i++)
		{
			CP.add(CPStr[i]);
		}
		headRulesOfCTB.put("CP", new HeadRule(CP, "right"));

		String[] DNPStr = { "DNP", "DEG" };
		for (int i = 0; i < DNPStr.length; i++)
		{
			DNP.add(DNPStr[i]);
		}
		headRulesOfCTB.put("DNP", new HeadRule(DNP, "right"));

		String[] DPStr = { "DP", "DT" };
		for (int i = 0; i < DPStr.length; i++)
		{
			DP.add(DPStr[i]);
		}
		headRulesOfCTB.put("DP", new HeadRule(DP, "left"));

		String[] INTJStr = { "INTJ", "JJ" };
		for (int i = 0; i < INTJStr.length; i++)
		{
			INTJ.add(INTJStr[i]);
		}
		headRulesOfCTB.put("INTJ", new HeadRule(INTJ, "left"));

		String[] IPStr = { "IP", "VP" };
		for (int i = 0; i < IPStr.length; i++)
		{
			IP.add(IPStr[i]);
		}
		headRulesOfCTB.put("IP", new HeadRule(IP, "right"));

		String[] LCPStr = { "LCP", "LC" };
		for (int i = 0; i < LCPStr.length; i++)
		{
			LCP.add(LCPStr[i]);
		}
		headRulesOfCTB.put("LCP", new HeadRule(LCP, "right"));

		String[] NPStr = { "NP", "NN", "NT", "NR", "QP" };
		for (int i = 0; i < NPStr.length; i++)
		{
			NP.add(NPStr[i]);
		}
		headRulesOfCTB.put("NP", new HeadRule(NP, "right"));

		String[] PPStr = { "PP", "P" };
		for (int i = 0; i < PPStr.length; i++)
		{
			PP.add(PPStr[i]);
		}
		headRulesOfCTB.put("PP", new HeadRule(PP, "left"));

		String[] QPStr = { "QP", "CD", "OD" };
		for (int i = 0; i < QPStr.length; i++)
		{
			QP.add(QPStr[i]);
		}
		headRulesOfCTB.put("QP", new HeadRule(QP, "right"));

		String[] VPStr = { "VP", "VA", "VC", "VE", "VV", "BA", "LB", "VCD", "VSB", "VRD", "VNV", "VCP" };
		for (int i = 0; i < VPStr.length; i++)
		{
			VP.add(VPStr[i]);
		}
		headRulesOfCTB.put("VP", new HeadRule(VP, "right"));

		String[] VVStr = { "VV" };
		for (int i = 0; i < VVStr.length; i++)
		{
			VV.add(VVStr[i]);
		}
		headRulesOfCTB.put("VV", new HeadRule(VV, "right"));

		String[] VAStr = { "VA" };
		for (int i = 0; i < VAStr.length; i++)
		{
			VA.add(VAStr[i]);
		}
		headRulesOfCTB.put("VA", new HeadRule(VA, "right"));
		String[] VEStr = { "VE" };
		for (int i = 0; i < VEStr.length; i++)
		{
			VE.add(VEStr[i]);
		}
		headRulesOfCTB.put("VE", new HeadRule(VE, "right"));

		String[] VCStr = { "VC" };
		for (int i = 0; i < VCStr.length; i++)
		{
			VC.add(VCStr[i]);
		}
		headRulesOfCTB.put("VC", new HeadRule(VC, "right"));

		String[] VCDStr = { "VCD", "VV", "VA", "VC", "VE" };
		for (int i = 0; i < VCDStr.length; i++)
		{
			VCD.add(VCDStr[i]);
		}
		headRulesOfCTB.put("VCD", new HeadRule(VCD, "right"));

		String[] VRDStr = { "VRD", "VV", "VA", "VC", "VE" };
		for (int i = 0; i < VRDStr.length; i++)
		{
			VRD.add(VRDStr[i]);
		}
		headRulesOfCTB.put("VRD", new HeadRule(VRD, "right"));

		String[] VSBStr = { "VSB", "VV", "VA", "VC", "VE" };
		for (int i = 0; i < VSBStr.length; i++)
		{
			VSB.add(VSBStr[i]);
		}
		headRulesOfCTB.put("VSB", new HeadRule(VSB, "right"));

		String[] VCPStr = { "VCP", "VV", "VA", "VC", "VE" };
		for (int i = 0; i < VCPStr.length; i++)
		{
			VCP.add(VCPStr[i]);
		}
		headRulesOfCTB.put("VCP", new HeadRule(VCP, "right"));

		String[] VNVStr = { "VNV", "VV", "VA", "VC", "VE" };
		for (int i = 0; i < VNVStr.length; i++)
		{
			VNV.add(VNVStr[i]);
		}
		headRulesOfCTB.put("VNV", new HeadRule(VNV, "right"));
		String[] CLPStr = { "CLP", "M" };
		for (int i = 0; i < CLPStr.length; i++)
		{
			CLP.add(CLPStr[i]);
		}
		headRulesOfCTB.put("CLP", new HeadRule(CLP, "right"));
		String[] LSTStr = { "LST", "CD","OD" };
		for (int i = 0; i < LSTStr.length; i++)
		{
			LST.add(LSTStr[i]);
		}
		headRulesOfCTB.put("LST", new HeadRule(LST, "left"));
		String[] VPTStr = { "VNV", "VV","VA","VC","VE" };
		for (int i = 0; i < VPTStr.length; i++)
		{
			VPT.add(VPTStr[i]);
		}
		headRulesOfCTB.put("VPT", new HeadRule(VPT, "right"));
		String[] PRNStr = { "NP", "IP", "VP","NT","NR","NN" };
		for (int i = 0; i < PRNStr.length; i++)
		{
			PRN.add(PRNStr[i]);
		}
		headRulesOfCTB.put("PRN", new HeadRule(PRN, "right"));
		String[] DVPStr = { "DVP","DEV" };
		for (int i = 0; i < DVPStr.length; i++)
		{
			DVP.add(DVPStr[i]);
		}
		headRulesOfCTB.put("DVP", new HeadRule(DVP, "right"));
		String[] WHPPStr = { "WHPP","PP","P" };
		for (int i = 0; i < DVPStr.length; i++)
		{
			WHPP.add(WHPPStr[i]);
		}
		headRulesOfCTB.put("WHPP", new HeadRule(WHPP, "left"));
		String[] FRAGStr = { "VV","NR","NN" };
		for (int i = 0; i < DVPStr.length; i++)
		{
			FRAG.add(FRAGStr[i]);
		}
		headRulesOfCTB.put("FRAG", new HeadRule(FRAG, "right"));
		String[] UCPStr = { "UCP"};
		for (int i = 0; i < UCPStr.length; i++)
		{
			UCP.add(UCPStr[i]);
		}
		headRulesOfCTB.put("UCP", new HeadRule(UCP, "right"));
	}

	/**
	 * 获取常规的规则
	 * 
	 * @return
	 */
	public HashMap<String, HeadRule> getNormalRuleSet()
	{
		return headRulesOfCTB;
	}

	@Override
	protected HashMap<String, List<HeadRule>> getSpecialRuleSet()
	{
		return null;
	}

}
