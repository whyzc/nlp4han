package com.lc.nlp4han.constituent.maxent;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreePreprocessTool;

/**
 * 句法树预处理操作的测试
 * 说明：预处理指的是去掉空节点，和去掉功能标记
 * @author 王馨苇
 *
 */
public class TreePreprocessTest{

	/**
	 * 验证去除空节点和功能标记后输出的带换行的括号表达式是否正确
	 * @throws UnsupportedOperationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testPreTreatment() throws UnsupportedOperationException, FileNotFoundException, IOException{
		//测试用例：
		//(1)节点下只有一个子节点，且该子节点是空节点:节点和节点的子节点一起删除
		String treestr1 = "((S(NP-SBJ-17(NP(DT The)(ADJP(QP($ $)(CD 2.5)(CD billion))(-NONE- *U*))(NNP Byron)(CD 1)(NN plant))"
				+ "(PP-LOC(IN near)(NP(NP(NNP Rockford))(, ,)(NP(NNP Ill.))(, ,))))(VP(VBD was)(VP(VBN completed)"
				+ "(NP(-NONE- *-17))(PP-TMP(IN in)(NP(CD 1985)))))(. .)))";
		TreeNode tree1 = BracketExpUtil.generateTreeNoTopBracket(treestr1);
		TreePreprocessTool.deleteNone(tree1);
		String result1 = "(S(NP(NP(DT The)(QP($ $)(CD 2.5)(CD billion))(NNP Byron)(CD 1)(NN plant))"
				+ "(PP(IN near)(NP(NP(NNP Rockford))(, ,)(NP(NNP Ill.))(, ,))))(VP(VBD was)(VP(VBN completed)"
				+ "(PP(IN in)(NP(CD 1985)))))(. .))";
		//(2)节点下有多个节点，多个节点中只有一个节点是空节点
		//2.1多个节点是两个节点，删除空节点，且上提
		//2.2多个节点大于2，删除空节点，不用上提
		String treestr2 = "((S(PP(IN For)(NP(CD 1988)))(, ,)(NP-SBJ(NNP Commonwealth)(NNP Edison))(VP(VBD reported)"
		+"(NP(NP(NNS earnings))(PP(IN of)(NP(NP(QP($ $)(CD 737.5)(CD million))(-NONE- *U*))(, ,)"
		+ "(CC or)(NP(NP($ $)(CD 3.01)(-NONE- *U*))(NP-ADV(DT a)(NN share)))))))(. .)))";
		TreeNode tree2 = BracketExpUtil.generateTreeNoTopBracket(treestr2);
		TreePreprocessTool.deleteNone(tree2);
		String result2 = "(S(PP(IN For)(NP(CD 1988)))(, ,)(NP(NNP Commonwealth)(NNP Edison))(VP(VBD reported)"
				+"(NP(NP(NNS earnings))(PP(IN of)(NP(QP($ $)(CD 737.5)(CD million))(, ,)"
				+ "(CC or)(NP(NP($ $)(CD 3.01))(NP(DT a)(NN share)))))))(. .))";
		//(3)节点下有多个子节点，且多个子节点都是空节点：节点和所有的子节点一起删除
		String treestr3 = "((S(S-TPC-1(SBAR-TMP(IN Until)(S(NP-SBJ(NNP Congress))(VP(VBZ acts))))(, ,)(NP-SBJ(DT the)(NN government))"
				+ "(VP(VBZ has)(RB n't)(NP(DT any)(NN authority)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB issue)(NP(NP(JJ new)(NN debt)(NNS obligations))"
				+ "(PP(IN of)(NP(DT any)(NN kind))))))))))(, ,)(NP-SBJ(DT the)(NNP Treasury))(VP(VBD said)(SBAR(-NONE- 0)(S(-NONE- *T*-1))))(. .)))";
		TreeNode tree3 = BracketExpUtil.generateTreeNoTopBracket(treestr3);
		TreePreprocessTool.deleteNone(tree3);
		String result3 = "(S(S(SBAR(IN Until)(S(NP(NNP Congress))(VP(VBZ acts))))(, ,)(NP(DT the)(NN government))"
				+ "(VP(VBZ has)(RB n't)(NP(DT any)(NN authority)(VP(TO to)(VP(VB issue)(NP(NP(JJ new)(NN debt)(NNS obligations))"
				+ "(PP(IN of)(NP(DT any)(NN kind)))))))))(, ,)(NP(DT the)(NNP Treasury))(VBD said)(. .))";
		//(4)功能标记
		//4.1正常类型的标记：标记-功能标记-数字索引，此时只保留标记的部分
		//              标记-数字索引，此时保留标记
		//              标记-功能标记，此时保留标记
		//4.2功能标记是-LRB-或者是-RRB-，不能像(4)一样处理，要全部保留
		String treestr4 = "((S(ADVP-TMP(RB Then))(, ,)(SBAR-ADV(RB just)(IN as)(S(NP-SBJ-72(DT the)(NNP Tramp))(VP(VBZ is)"
				+ "(VP(VBN given)(NP(-NONE- *-72))(NP(NP(DT a)(JJ blind)(NN girl))(SBAR(WHNP-2(-NONE- 0))(S(NP-SBJ(-NONE- *))"
				+ "(VP(TO to)(VP(VB cure)(NP(-NONE- *T*-2)))))))(PP-LOC(IN in)(`` ``)(NP-TTL(NNP City)(NNP Lights)))))))(, ,)"
				+ "('' '')(NP-SBJ-73(DT the)(NNP Artist))(VP(VBZ is)(VP(VBN put)(NP(-NONE- *-73))(PP-PUT(IN in)(NP"
				+ "(NP(NN charge))(PP(IN of)(S-NOM(NP-SBJ(-NONE- *-73))(VP(VBG returning)(NP(NP(DT a)(JJ two-year-old)(NN waif))"
				+ "(PRN(-LRB- -LRB-)(NP(NNP Nicole)(NNP Alysia))(-RRB- -RRB-))(, ,)(SBAR(WHNP-1(WP$ whose)(NP(NN father)))"
				+ "(S(NP-SBJ-74(-NONE- *T*-1))(VP(VBZ has)(VP(VBN been)(VP(VBN murdered)(NP(-NONE- *-74))(PP(IN by)"
				+ "(NP-LGS(NNS thugs))))))))(, ,))(PP-CLR(TO to)(NP(PRP$ her)(NN mother))))))))))(. .)))";
		TreeNode tree4 = BracketExpUtil.generateTreeNoTopBracket(treestr4);
		TreePreprocessTool.deleteNone(tree4);
		String result4 = "(S(ADVP(RB Then))(, ,)(SBAR(RB just)(IN as)(S(NP(DT the)(NNP Tramp))(VP(VBZ is)"
				+ "(VP(VBN given)(NP(NP(DT a)(JJ blind)(NN girl))"
				+ "(VP(TO to)(VB cure)))(PP(IN in)(`` ``)(NP(NNP City)(NNP Lights)))))))(, ,)"
				+ "('' '')(NP(DT the)(NNP Artist))(VP(VBZ is)(VP(VBN put)(PP(IN in)(NP"
				+ "(NP(NN charge))(PP(IN of)(VP(VBG returning)(NP(NP(DT a)(JJ two-year-old)(NN waif))"
				+ "(PRN(-LRB- -LRB-)(NP(NNP Nicole)(NNP Alysia))(-RRB- -RRB-))(, ,)(SBAR(WHNP(WP$ whose)(NP(NN father)))"
				+ "(VP(VBZ has)(VP(VBN been)(VP(VBN murdered)(PP(IN by)"
				+ "(NP(NNS thugs)))))))(, ,))(PP(TO to)(NP(PRP$ her)(NN mother)))))))))(. .))";
		
		assertEquals(result1,tree1.toStringNoNone());	
		assertEquals(result2,tree2.toStringNoNone());	
		assertEquals(result3,tree3.toStringNoNone());	
		assertEquals(result4,tree4.toStringNoNone());	
	}
}
