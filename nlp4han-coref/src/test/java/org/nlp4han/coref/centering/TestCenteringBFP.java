package org.nlp4han.coref.centering;

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
		
		List<Entity> es1 = Entity.entitys(s1);
		List<Entity> es2 = Entity.entitys(s2);
		
		List<List<Entity>> eou = new ArrayList<List<Entity>>();
		eou.add(es1);
		eou.add(es2);
		
		CenteringBFP bfp = new CenteringBFP(eou);
		
		List<List<Entity>> result = bfp.run();
		
		
	}
	
	
}
