package com.lc.nlp4han.srl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreePreprocessTool;
import com.lc.nlp4han.srl.tree.AbstractParseStrategy;
import com.lc.nlp4han.srl.tree.SRLParseNormalWithPruning;
import com.lc.nlp4han.srl.tree.SRLSample;
import com.lc.nlp4han.srl.tree.TreeNodeWrapper;

/**
 * 将样本解析成分类阶段要用的样本的样式
 * 有剪枝
 * 分类阶段只有论元标签
 * @author 王馨苇
 *
 */
public class SRLParseWithPruningForClassificationNoNULLTest {

	@SuppressWarnings("static-access")
	@Test
	public void test(){
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		AbstractParseStrategy<HeadTreeNode> parse = new SRLParseNormalWithPruning();
		
		String roles = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		TreeNode tree = BracketExpUtil.generateTree(""
				+ "((S(S(NP-SBJ(NNP Mr.)(NNP Spoon))(VP(VBD said)(SBAR (-NONE- 0)(S(NP-SBJ(DT the)(NN plan))"
				+ "(VP(VBZ is)(RB not)(NP-PRD(DT an)(NN attempt)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB shore)"
				+ "(PRT(RP up))(NP(NP(DT a)(NN decline))(PP-LOC(IN in)(NP(NN ad)(NNS pages)))(PP-TMP(IN in)"
				+ "(NP(NP(DT the)(JJ first)(CD nine)(NNS months))(PP(IN of)(NP(CD 1989)))))))))))))))"
				+ "(: ;)(S(NP-SBJ(NP(NNP Newsweek)(POS 's))(NN ad)(NNS pages))(VP(VBD totaled)(NP"
				+ "(NP(CD 1,620))(, ,)(NP(NP(DT a)(NN drop))(PP(IN of)(NP (CD 3.2)(NN %)))"
				+ "(PP-DIR(IN from)(NP(JJ last)(NN year)))))(, ,)(PP(VBG according)(PP(TO to)"
				+ "(NP(NNP Publishers)(NNP Information)(NNP Bureau))))))(. .)))");	
		TreePreprocessTool.deleteNone(tree);		
		SRLSample<HeadTreeNode> sample = parse.parse(tree, roles, ahg);
		
		List<Integer> index = new ArrayList<>();
		index.add(0);
		index.add(26);
		
		@SuppressWarnings("unchecked")
		TreeNodeWrapper<HeadTreeNode>[] wrapper = new TreeNodeWrapper[2];
		wrapper[0] = sample.getArgumentTree()[0];
		wrapper[1] = sample.getArgumentTree()[26];
		
		List<String> label = new ArrayList<>();
		label.add("ARG1");
		label.add("ARG0");

		String list = "(VB{shore[VB]} shore[12])";
		
		assertEquals(index, SRLSample.filterNotNULLLabelIndex(sample.getIdentificationLabelInfo()));	
		assertEquals(Arrays.asList(sample.getLabelFromIndex(sample.getLabelInfo(), SRLSample.filterNotNULLLabelIndex(sample.getIdentificationLabelInfo()))), label);
		assertTrue(sample.getArgumentTreeFromIndex(sample.getArgumentTree(), SRLSample.filterNotNULLLabelIndex(sample.getIdentificationLabelInfo())).length == wrapper.length);
		assertEquals(sample.getArgumentTreeFromIndex(sample.getArgumentTree(), SRLSample.filterNotNULLLabelIndex(sample.getIdentificationLabelInfo()))[0], wrapper[0]);
		assertEquals(sample.getArgumentTreeFromIndex(sample.getArgumentTree(), SRLSample.filterNotNULLLabelIndex(sample.getIdentificationLabelInfo()))[1], wrapper[1]);
		assertEquals(sample.getPredicateTree()[0].getTree().toString(), list);
	}
}
