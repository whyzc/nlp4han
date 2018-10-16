package com.lc.nlp4han.constituent;

import static org.junit.Assert.*;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.TreeNode;


/**
 * 测试计算指标是否正确
 * @author 王馨苇
 *
 */
public class ConstituentMeasureTest {

	@Test
	public void test() throws CloneNotSupportedException{
		//两棵树不同
		TreeNode treeRef1 = BracketExpUtil.generateTreeNoTopBracket("((S(NP(NN Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NNS tablespoons)))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))(. .)))");
		TreeNode treePre1 = BracketExpUtil.generateTreeNoTopBracket("((S(NP(VBG Measuring)(NNS cups))(VP(MD may)(ADVP(RB soon))(VP(VB be)(VP(VBN replaced)(PP(IN by)(NP(NP(NNS tablespoons))(PP(IN in)(NP(DT the)(NN laundry)(NN room))))))))(. .)))");
		ConstituentMeasure measure1 = new ConstituentMeasure();
		measure1.update(treeRef1, treePre1);
		assertEquals(measure1.getPrecisionScore(), 0.8181, 0.001);
		assertEquals(measure1.getRecallScore(), 0.9, 0.001);
		assertEquals(measure1.getMeasure(), 0.8570, 0.001);
		assertEquals(measure1.getCBs(), 1, 0.001);
		assertEquals(measure1.getCBs_0(), 0, 0.001);
		assertEquals(measure1.getCBs_2(), 1, 0.001);
		assertEquals(measure1.getSentenceAccuracy(), 0, 0.001);
		
		//两颗树相同的
		TreeNode treeRef2 = BracketExpUtil.generateTreeNoTopBracket("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		TreeNode treePre2 = BracketExpUtil.generateTreeNoTopBracket("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		ConstituentMeasure measure2 = new ConstituentMeasure();
		measure2.update(treeRef2, treePre2);
		assertEquals(measure2.getPrecisionScore(), 1.0, 0.001);
		assertEquals(measure2.getRecallScore(), 1.0, 0.001);
		assertEquals(measure2.getMeasure(), 1.0, 0.001);
		assertEquals(measure2.getCBs(), 0, 0.001);
		assertEquals(measure2.getCBs_0(), 1, 0.001);
		assertEquals(measure2.getCBs_2(), 1, 0.001);
		assertEquals(measure2.getSentenceAccuracy(), 1, 0.001);
	}
}
