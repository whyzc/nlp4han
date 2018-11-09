package com.lc.nlp4han.constituent.lex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LexPCFGTest
{
	private LexPCFG lexpcfg;
	private RuleHeadChildGenerate rhcg0;

	@Before
	public void beforeTest() throws IOException
	{
		ArrayList<String> CTBList = new ArrayList<String>();
		String[] sentences = new String[3];
		sentences[0] = "(ROOT(NP(NN a)(NR b)(NN c)))";
		sentences[1] = "(ROOT(NP(NP(NR h))(VV d)(NP(NR e))))";
		sentences[2] = "(ROOT(IP(NP(NN b))(ADJP1(JJ f))(ADJP2(JJ g))))";
		for (String str : sentences)
		{
			CTBList.add(str);
		}
		lexpcfg = LexGrammarExtractor.getLexPCFG(CTBList);
	}

	@Test
	// 测试提取的文法内容
	public void testLexPCFGGrammar()
	{
		// 测试得到起始符
		String startSymbol = lexpcfg.getStartSymbol();
		Assert.assertEquals(startSymbol, "ROOT");

		// 测试得到词性标注的总集合
		String[] poses = { "NN", "VV", "NR", "JJ" };
		HashSet<String> posSet = new HashSet<String>();
		for (String pos : poses)
		{
			posSet.add(pos);
		}
		HashSet<String> set = lexpcfg.getPosSet();
		Assert.assertEquals(set, posSet);

		// 测试由headChild得到可行的Parent/或者向上延伸的单元规则
		// 在测试中发现当我使用以lael(pos,word)进行解析时数据过少，而以label(null,null)为条件进行搜索parent时，候选的父节点又过于多
		// 故，我以label(pos,null)为条件进行搜索，对效果并无影响
		rhcg0 = new RuleHeadChildGenerate("JJ", null, "JJ", null);
		HashSet<String> posSetOfParent = new HashSet<String>();
		posSetOfParent.add("ADJP1");
		posSetOfParent.add("ADJP2");
		HashSet<String> parentSet = lexpcfg.getParentSet(rhcg0);
		Assert.assertEquals(parentSet, posSetOfParent);

		// 生成头规则数据,以(NP (NN a) (NN b) (NN c))为例
		String headLabel = "NN";
		String headPos = "NN";
		String headWord = "c";
		String parentLabel = "NPB";
		RuleHeadChildGenerate rhcgs[] = new RuleHeadChildGenerate[6];
		rhcgs[0] = new RuleHeadChildGenerate(headLabel, parentLabel, headPos, headWord);
		rhcgs[3] = new RuleHeadChildGenerate(null, parentLabel, headPos, headWord);
		// 回退模型2使用
		rhcgs[1] = new RuleHeadChildGenerate(headLabel, parentLabel, headPos, null);
		rhcgs[4] = new RuleHeadChildGenerate(null, parentLabel, headPos, null);
		// 回退模型3使用
		rhcgs[2] = new RuleHeadChildGenerate(headLabel, parentLabel, null, null);
		rhcgs[5] = new RuleHeadChildGenerate(null, parentLabel, null, null);
		for (RuleHeadChildGenerate temprhcg : rhcgs)
		{
			Assert.assertTrue(lexpcfg.getHeadGenMap().keySet().contains(temprhcg));
		}

		// 生成stop规则数据,以(NP (NN a) (NR b) (NN c))为例,当父节点为NPB时，将中心孩子换为上一个孩子（以中心词下标为起点）
		Distance distance2 = new Distance(false, false);// 此时的距离变量将忽略
		RuleStopGenerate[] rsgs = new RuleStopGenerate[12];
		// 回退模型1
		rsgs[0] = new RuleStopGenerate(headLabel, parentLabel, headPos, headWord, 1, false, distance2);// 左侧第一个
		rsgs[1] = new RuleStopGenerate("NR", parentLabel, "NR", "b", 1, false, distance2);// 左侧第二个
		rsgs[2] = new RuleStopGenerate(headLabel, parentLabel, headPos, "a", 1, true, distance2);// 左侧第三个
		rsgs[3] = new RuleStopGenerate(headLabel, parentLabel, headPos, headWord, 2, true, distance2);// 右侧第一个
		// 回退模型2
		rsgs[4] = new RuleStopGenerate(headLabel, parentLabel, headPos, null, 1, false, distance2);// 左侧第一个
		rsgs[5] = new RuleStopGenerate("NR", parentLabel, "NR", null, 1, false, distance2);// 左侧第二个
		rsgs[6] = new RuleStopGenerate(headLabel, parentLabel, headPos, null, 1, true, distance2);// 左侧第三个
		rsgs[7] = new RuleStopGenerate(headLabel, parentLabel, headPos, null, 2, true, distance2);// 右侧第一个
		// 回退模型3
		rsgs[8] = new RuleStopGenerate(headLabel, parentLabel, null, null, 1, false, distance2);// 左侧第一个
		rsgs[9] = new RuleStopGenerate(headLabel, parentLabel, null, null, 1, false, distance2);// 左侧第二个
		rsgs[10] = new RuleStopGenerate(headLabel, parentLabel, null, null, 1, true, distance2);// 左侧第三个
		rsgs[11] = new RuleStopGenerate(headLabel, parentLabel, null, null, 2, true, distance2);// 右侧第一个
		for (RuleStopGenerate rsg0 : rsgs)
		{
			Assert.assertTrue(lexpcfg.getStopGenMap().keySet().contains(rsg0));
		}

		// 生成两侧规则数据，以(NP(NP(NR h))(VV d)(NP(NR e)))，也就是NP(,d)->NP(NR,h) VV(VV d) NP(NR
		// e)为例
		headLabel = "NPB";
		parentLabel = "NP";
		headPos = "NR";
		headWord = "e";
		String lsideLabel1 = "VV";
		String lsideLabel2 = "NPB";
		String lsideHeadPos1 = "VV";
		String lsideHeadPos2 = "NR";
		String lsideHeadWord1 = "d";
		String lsideHeadWord2 = "h";
		int direction = 1;
		int coor = 0;
		int pu = 0;
		Distance ldistance1 = new Distance(true, false);
		Distance ldistance2 = new Distance(false, true);
		// 左侧第一个符号
		RuleSidesGenerate[] rsg = new RuleSidesGenerate[21];
		// 生成两侧Label和pos的回退模型
		// 回退模型1
		rsg[1] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, lsideLabel1, lsideHeadPos1,
				null, coor, pu, ldistance1);
		rsg[2] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, null, null, null, coor, pu,
				ldistance1);
		// 回退模型二
		rsg[3] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, lsideLabel1, lsideHeadPos1,
				null, coor, pu, ldistance1);
		rsg[4] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, null, null, null, coor, pu,
				ldistance1);
		// 回退模型三
		rsg[5] = new RuleSidesGenerate(headLabel, parentLabel, null, null, direction, lsideLabel1, lsideHeadPos1, null,
				coor, pu, ldistance1);
		rsg[6] = new RuleSidesGenerate(headLabel, parentLabel, null, null, direction, null, null, null, coor, pu,
				ldistance1);

		// 生成两侧word的回退模型
		// 回退模型1
		rsg[7] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, lsideLabel1, lsideHeadPos1,
				lsideHeadWord1, coor, pu, ldistance1);
		rsg[8] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, lsideLabel1, lsideHeadPos1,
				null, coor, pu, ldistance1);
		// 回退模型2
		rsg[9] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, lsideLabel1, lsideHeadPos1,
				lsideHeadWord1, coor, pu, ldistance1);
		rsg[10] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, lsideLabel1, lsideHeadPos1,
				null, coor, pu, ldistance1);

		// 左侧第二个符号
		// 生成两侧Label和pos的回退模型
		// 回退模型1
		rsg[11] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, lsideLabel2,
				lsideHeadPos2, null, coor, pu, ldistance2);
		rsg[12] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, null, null, null, coor,
				pu, ldistance2);
		// 回退模型二
		rsg[13] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, lsideLabel2, lsideHeadPos2,
				null, coor, pu, ldistance2);
		rsg[14] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, null, null, null, coor, pu,
				ldistance2);
		// 回退模型三
		rsg[15] = new RuleSidesGenerate(headLabel, parentLabel, null, null, direction, lsideLabel2, lsideHeadPos2, null,
				coor, pu, ldistance2);
		rsg[16] = new RuleSidesGenerate(headLabel, parentLabel, null, null, direction, null, null, null, coor, pu,
				ldistance2);

		// 生成两侧word的回退模型
		// 回退模型1
		rsg[17] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, lsideLabel2,
				lsideHeadPos2, lsideHeadWord2, coor, pu, ldistance2);
		rsg[18] = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, lsideLabel2,
				lsideHeadPos2, null, coor, pu, ldistance2);
		// 回退模型2
		rsg[19] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, lsideLabel2, lsideHeadPos2,
				lsideHeadWord2, coor, pu, ldistance2);
		rsg[20] = new RuleSidesGenerate(headLabel, parentLabel, headPos, null, direction, lsideLabel2, lsideHeadPos2,
				null, coor, pu, ldistance2);

		for (int i = 1; i < rsg.length; i++)
		{
			Assert.assertTrue(lexpcfg.getSidesGeneratorMap().keySet().contains(rsg[i]));
		}
	}

	@Test
	// 测试求概率的方法（没有写出NPB和特殊（并列或者标点符号）规则情况的测试）
	public void testGetPro()
	{
		// 生成父节点,以(NP (NN a) (NN b) (NN c))为例,求生成父节点的概率
		String headLabel = "NN";
		String headPos = "NN";
		String headWord = "c";
		String parentLabel = "NPB";
		RuleHeadChildGenerate rhcg = new RuleHeadChildGenerate(headLabel, parentLabel, headPos, headWord);
		Double pro1 = lexpcfg.getGeneratePro(rhcg, "head");
		double e1 = 1.0;
		double w1 = 1.0 / (1 + 5 * 1);
		double e2 = 1.0;
		double w2 = 2.0 / (2 + 5 * 1);
		double e3 = 0.5;
		double pro = e1 * w1 + (1 - w1) * (e2 * w2 + (1 - w2) * e3);
		Assert.assertTrue(pro == pro1);

		// 生成STOP,以(NP(NP(NR h))(VV d)(NP(NR e)))为例,求生成stop的概率
		headLabel = "NPB";
		parentLabel = "NP";
		headPos = "NR";
		headWord = "e";
		Distance ldistance = new Distance(false, true);
		Distance rdistance = new Distance(true, false);
		RuleStopGenerate lrsg = new RuleStopGenerate(headLabel, parentLabel, headPos, headWord, 1, true, ldistance);
		RuleStopGenerate rrsg = new RuleStopGenerate(headLabel, parentLabel, headPos, headWord, 2, true, rdistance);
		pro1 = lexpcfg.getGeneratePro(lrsg, "stop") * lexpcfg.getGeneratePro(rrsg, "stop");
		pro = 0.5;
		Assert.assertTrue(pro1 == pro);

		// 生成两侧孩子，以(NP(NP(NR h))(VV d)(NP(NR e)))的左侧第二个孩子为例
		headLabel = "NPB";
		parentLabel = "NP";
		headPos = "NR";
		headWord = "e";
		int coor = 0;
		int pu = 0;
		String sideLabel = "NPB";
		String sideHeadPos = "NR";
		String sideHeadWord = "h";
		int direction = 1;
		Distance distance = new Distance(false, true);
		RuleSidesGenerate rsg = new RuleSidesGenerate(headLabel, parentLabel, headPos, headWord, direction, sideLabel,
				sideHeadPos, sideHeadWord, coor, pu, distance);
		pro1 = lexpcfg.getGeneratePro(rsg, "sides");
		pro = 0.7011316872427983;
		Assert.assertTrue(pro1 == pro);
	}
}