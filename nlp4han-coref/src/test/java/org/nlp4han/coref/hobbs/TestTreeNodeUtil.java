package org.nlp4han.coref.hobbs;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

public class TestTreeNodeUtil
{
	@Test
	public void testGetHead()
	{
		TreeNode npNode = BracketExpUtil.generateTree("[(NP(IP(NP(NN 外商))(VP(VV 投资)))(NP(NN 企业)))]");
		TreeNode goal = BracketExpUtil.generateTree("[(NN 企业)]");
		TreeNode result = TreeNodeUtil.getHead(npNode, NPHeadRuleSetPTB.getNPRuleSet());
		assertEquals(goal, result);
		
		
		TreeNode npNode2 = BracketExpUtil.generateTree("[(NP (NN 改革) (CC 和) (NN 开放))]");
		TreeNode goal2 = BracketExpUtil.generateTree("[(NN 开放)]");
		TreeNode result2 = TreeNodeUtil.getHead(npNode2, NPHeadRuleSetPTB.getNPRuleSet());
		assertEquals(goal2, result2);
		
		
		TreeNode npNode3 = BracketExpUtil.generateTree("[(NP (NN 产值)(QP (CD 5亿)(CLP (M 元))))]");
		TreeNode goal3 = BracketExpUtil.generateTree("[(NN 产值)]");
		TreeNode result3 = TreeNodeUtil.getHead(npNode3, NPHeadRuleSetPTB.getNPRuleSet());
		assertEquals(goal3, result3);
		
		
		TreeNode npNode4 = BracketExpUtil.generateTree("[(NP(NP (NR 中国))(NP (NN 国民) (NN 经济) (NN 总产值)))]");
		TreeNode goal4 = BracketExpUtil.generateTree("[(NN 总产值)]");
		TreeNode result4 = TreeNodeUtil.getHead(npNode4, NPHeadRuleSetPTB.getNPRuleSet());
		assertEquals(goal4, result4);
	}
	
	@Test
	public void testGetNodesWithSpecifiedNameOnLeftOrRightOfPath()
	{
		String str;
		str = "((IP(IP(NP(NN 家长们))(VP(ADVP(AD 都))(ADVP(AD 很))(VP(VV 喜欢)(NP(PN 她)))))(PU 。)))";
		TreeNode s = BracketExpUtil.generateTree(str);
		TreeNode t1 = s.getChild(0).getChild(1).getChild(2).getChild(1).getChild(0);
		TreeNode t2 = s.getChild(0).getChild(1);
		
		Path path1 = new Path();
		path1.getPath(t1, t2);
		List<TreeNode> result = TreeNodeUtil.getNodesWithSpecifiedNameOnLeftOrRightOfPath(t2, path1, "LEfT", new String[]{"AD", "ADVP"});
		
		List<TreeNode> goal = new LinkedList<TreeNode>();
		goal.add(BracketExpUtil.generateTree("[(ADVP(AD 都))]"));
		goal.add(BracketExpUtil.generateTree("[(ADVP(AD 很))]"));
		goal.add(BracketExpUtil.generateTree("[(AD 都)]"));
		goal.add(BracketExpUtil.generateTree("[(AD 很)]"));
		
		assertEquals(goal, result);
	}
	
	@Test
	public void testGetString()
	{
		String str;
		TreeNode tree;
		String goal;
		String result;
		
		str = "((IP(IP(NP(NN 家长们))(VP(ADVP(AD 都))(ADVP(AD 很))(VP(VV 喜欢)(NP(PN 她)))))(PU 。)))";
		tree = BracketExpUtil.generateTree(str);
		goal = "家长们都很喜欢她。";
		result = TreeNodeUtil.getString(tree);
		assertEquals(goal, result);
		
		str = "((IP(NP(NR 萨曼))(VP(VP(VV 不得已))(PU ，)(IP(NP(CP(IP(VP(BA 把)(IP(NP(PN 他))(VP(VV 看到)))))(DEC 的))(NP(NN 情景)))(VP(ADVP(AD 一一))(VP(VV 讲出)))))(PU 。)))";
		tree = BracketExpUtil.generateTree(str);
		goal = "萨曼不得已，把他看到的情景一一讲出。";
		result = TreeNodeUtil.getString(tree);
		assertEquals(goal, result);
		
		str = "((IP(NP(NR 乔治))(VP(VV 雕刻)(AS 了)(NP(DNP(NP(QP(CD 一)(CLP(M 个)))(NP(NR 赫耳墨斯)))(DEG 的))(NP(NN 木像))))(PU 。)))";
		tree = BracketExpUtil.generateTree(str);
		goal = "乔治雕刻了一个赫耳墨斯的木像。";
		result = TreeNodeUtil.getString(tree);
		assertEquals(goal, result);
		
		str = "((IP(NP(NN 渔夫们))(VP(VP(VV 出去)(VP(VV 捕鱼)))(PU ，)(VP(ADVP(AD 辛苦))(VP(VV 劳累)(AS 了)(ADVP(AD 很久))))(PU ，)(VP(ADVP(AD 却))(VP(VV 一无所获))))(PU 。)))";
		tree = BracketExpUtil.generateTree(str);
		goal = "渔夫们出去捕鱼，辛苦劳累了很久，却一无所获。";
		result = TreeNodeUtil.getString(tree);
		assertEquals(goal, result);
		
	}
	
}
