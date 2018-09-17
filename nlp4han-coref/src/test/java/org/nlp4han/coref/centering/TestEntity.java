package org.nlp4han.coref.centering;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

public class TestEntity
{
	@Test
	public void testEntities_1()
	{
		String str;
		// 句一：小明的妈妈是一名教师。
		str = "((IP(NP(DNP(NP(NN 小明))(DEG 的))(NP(NN 妈妈)))(VP(VC 是)(NP(QP(CD 一)(CLP(M 名)))(NP(NN 教师))))(PU 。)))";
		TreeNode s1 = BracketExpUtil.generateTree(str);
		List<Entity> result = Entity.entities(s1, GrammaticalRoleRuleSet.getGrammaticalRoleRuleSet());
		List<Entity> goal = new ArrayList<Entity>();

		Entity e0 = new Entity("小明", "SBJ", 0);
		Entity e1 = new Entity("妈妈", "SBJ", 2);
		Entity e2 = new Entity("教师", "OBJ", 6);
		goal.add(e0);
		goal.add(e1);
		goal.add(e2);
		goal = Entity.sort(goal);
		assertEquals(goal, result);
	}
	
	@Test
	public void testEntities_2()
	{
		String str;
		str = "((IP(IP(NP(NN 父亲))(VP(ADVP(AD 原本))(ADVP(AD 没有))(VP(VV 做)(AS 过)(NP(NN 大事)))))(PU ，)(IP(NP(PN 他)(NN 一生))(VP(ADVP(AD 都))(PP(P在)(LCP(NP(NN 澡堂))(LC 中)))(VP(VV 替人)(IP(VP(VV 按摩))))))(PU 。)))";
		TreeNode s = BracketExpUtil.generateTree(str);
		List<Entity> result = Entity.entities(s, GrammaticalRoleRuleSet.getGrammaticalRoleRuleSet());
		List<Entity> goal = new ArrayList<Entity>();
		
		Entity e0 = new Entity("父亲", "SBJ", 0);
		Entity e1 = new Entity("大事", "OBJ", 5);
		Entity e2 = new Entity("他", "SBJ", 7);
		Entity e3 = new Entity("一生", "SBJ", 8);
		goal.add(e0);
		goal.add(e1);
		goal.add(e2);
		goal.add(e3);
		goal = Entity.sort(result);
		assertEquals(goal, result);
	}
	
	@Test
	public void testEntities_3()
	{
		String str;
		str = "((IP(IP(NP(NN 小芳))(VP(ADVP(AD 完全))(VP(VV 抛弃)(AS 了)(NP(NR 李明)))))(PU ，)(IP(NP(NN 背))(VP(PP(P 对着)(NP(PN 他)))(DVP(VP(VA 激动))(DEV 地))(PP(P 和)(NP(NR 吴刚)))(VP(VV 说话))))(PU 。)))";
		TreeNode s = BracketExpUtil.generateTree(str);
		List<Entity> result = Entity.entities(s, GrammaticalRoleRuleSet.getGrammaticalRoleRuleSet());
		List<Entity> goal = new ArrayList<Entity>();
		
		Entity e0 = new Entity("小芳", "SBJ", 0);
		Entity e1 = new Entity("李明", "OBJ", 4);
		Entity e2 = new Entity("他", "SBJ", 8);
		Entity e3 = new Entity("吴刚", "SBJ", 12);
		goal.add(e0);
		goal.add(e1);
		goal.add(e2);
		goal.add(e3);
		goal = Entity.sort(result);
		assertEquals(goal, result);
	}
}
