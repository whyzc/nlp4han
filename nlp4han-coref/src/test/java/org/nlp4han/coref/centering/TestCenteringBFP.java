package org.nlp4han.coref.centering;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

public class TestCenteringBFP
{
	@Test
	public void testCenteringBFP()
	{
		String str;
		// 句一：小明的妈妈是一名教师。
		str = "((IP(NP(DNP(NP(NN 小明))(DEG 的))(NP(NN 妈妈)))(VP(VC 是)(NP(QP(CD 一)(CLP(M 名)))(NP(NN 教师))))(PU 。)))";
		TreeNode s1 = BracketExpUtil.generateTree(str);
		// 句二：家长们都很喜欢她。
		str = "((IP(IP(NP(NN 家长们))(VP(ADVP(AD 都))(ADVP(AD 很))(VP(VV 喜欢)(NP(PN 她)))))(PU 。)))";
		TreeNode s2 = BracketExpUtil.generateTree(str);
		
		List<Entity> es1 = Entity.entities(s1);
		List<Entity> es2 = Entity.entities(s2);
		
		List<List<Entity>> eou = new ArrayList<List<Entity>>();
		eou.add(Entity.sort(es1));
		eou.add(Entity.sort(es2));
		
		List<TreeNode> nou = new ArrayList<TreeNode>();
		nou.add(s1);
		nou.add(s2);
		
		CenteringBFP bfp = new CenteringBFP(eou, nou);
		
		List<List<Entity>> newEntities = bfp.run();
		List<String> result = CenteringBFP.analysisResult(eou, newEntities);

		List<String> goal = new ArrayList<String>();
		goal.add("她(2-5)->妈妈(1-3)");
		
		assertEquals(goal, result);
	}
	
	@Test
	public void testCenteringBFP_2()
	{
		String str;
		str = "((IP(NP(PN 我))(VP(PP(P在)(LCP(NP(DNP(NP(NP(NR 太平洋))(NP(NN 酒店)))(DEG 的))(NP(NN 咖啡厅)))(LC 里)))(VP(VV 看见)(AS 了)(NP(NR 庞德))))(PU 。)))";
		TreeNode s1 = BracketExpUtil.generateTree(str);
		str = "((IP(IP(NP(NP(PN 他))(CC 和)(NP(QP(CD 一)(CLP(M 个)))(NP(NN 姑娘))))(VP(VV 坐)(ADVP(AD 在一起))))(PU 。)))";
		TreeNode s2 = BracketExpUtil.generateTree(str);
		
		List<Entity> es1 = Entity.entities(s1);
		List<Entity> es2 = Entity.entities(s2);
		
		List<List<Entity>> eou = new ArrayList<List<Entity>>();
		eou.add(Entity.sort(es1));
		eou.add(Entity.sort(es2));
		
		List<TreeNode> nou = new ArrayList<TreeNode>();
		nou.add(s1);
		nou.add(s2);
		
		CenteringBFP bfp = new CenteringBFP(eou, nou);
		
		List<List<Entity>> newEntities = bfp.run();
		List<String> result = CenteringBFP.analysisResult(eou, newEntities);

		List<String> goal = new ArrayList<String>();
		goal.add("他(2-1)->庞德(1-10)");
		
		assertEquals(goal, result);
		
	}
	
}
