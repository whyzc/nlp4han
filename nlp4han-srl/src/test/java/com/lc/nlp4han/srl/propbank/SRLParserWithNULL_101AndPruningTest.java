package com.lc.nlp4han.srl.propbank;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreePreprocessTool;
import com.lc.nlp4han.srl.propbank.AbstractParseStrategy;
import com.lc.nlp4han.srl.propbank.SRLParseWithNULL_101AndPruning;
import com.lc.nlp4han.srl.propbank.SRLSample;

/**
 * 有剪枝的样本类解析,此时样本类标签增加NULL_1 NULL0 NULL1
 * @author 王馨苇
 *
 */
public class SRLParserWithNULL_101AndPruningTest {
	
	@Test
	public void test(){
		AbstractHeadGenerator ahg = new HeadGeneratorCollins(new HeadRuleSetPTB());
		AbstractParseStrategy<HeadTreeNode> parse = new SRLParseWithNULL_101AndPruning();
		
		String roles = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		TreeNode tree = BracketExpUtil.generateTreeNoTopBracket(""
				+ "((S(S(NP-SBJ(NNP Mr.)(NNP Spoon))(VP(VBD said)(SBAR (-NONE- 0)(S(NP-SBJ(DT the)(NN plan))"
				+ "(VP(VBZ is)(RB not)(NP-PRD(DT an)(NN attempt)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB shore)"
				+ "(PRT(RP up))(NP(NP(DT a)(NN decline))(PP-LOC(IN in)(NP(NN ad)(NNS pages)))(PP-TMP(IN in)"
				+ "(NP(NP(DT the)(JJ first)(CD nine)(NNS months))(PP(IN of)(NP(CD 1989)))))))))))))))"
				+ "(: ;)(S(NP-SBJ(NP(NNP Newsweek)(POS 's))(NN ad)(NNS pages))(VP(VBD totaled)(NP"
				+ "(NP(CD 1,620))(, ,)(NP(NP(DT a)(NN drop))(PP(IN of)(NP (CD 3.2)(NN %)))"
				+ "(PP-DIR(IN from)(NP(JJ last)(NN year)))))(, ,)(PP(VBG according)(PP(TO to)"
				+ "(NP(NNP Publishers)(NNP Information)(NNP Bureau))))))(. .)))");	
		TreePreprocessTool.deleteNone(tree);		

		List<String> srlinfo = new ArrayList<>();		
		srlinfo.add("14");
		srlinfo.add("14");
		srlinfo.add("14");
		srlinfo.add("15");
		srlinfo.add("16");
		srlinfo.add("16");
		srlinfo.add("17");
		srlinfo.add("17");
		srlinfo.add("18");
		srlinfo.add("19");
		srlinfo.add("19");
		srlinfo.add("20");
		srlinfo.add("20");
		srlinfo.add("20");
		srlinfo.add("21");
		srlinfo.add("22");
		srlinfo.add("23");
		srlinfo.add("24");
		srlinfo.add("24");
		srlinfo.add("25");
		srlinfo.add("25");		
		srlinfo.add("11");
		srlinfo.add("8");
		srlinfo.add("9");
		srlinfo.add("6");
		srlinfo.add("7");
		srlinfo.add("4");
		srlinfo.add("2");
		srlinfo.add("0");
		
		List<String> label = new ArrayList<>();
		label.add("ARG1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");
		label.add("NULL1");		
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("ARG0");
		label.add("NULL_1");
		label.add("NULL_1");
		
		String list = "(VB{shore[VB]} shore[12])";
		
		String roles1 = "wsj/00/wsj_0071.mrg 37 9 gold go.13 pn--a 7:1-ARG1 9:1-rel";
		TreeNode tree1 = BracketExpUtil.generateTreeNoTopBracket("((S(S(NP-SBJ(PRP We))(VP(VBD got)(NP(PRP$ our)(CD two)(NNS six-packs))))(: --) (CC and)(S(NP-SBJ(PRP they))(VP(VBP 're)(VP(VBN gone))))(. .)('' '')))");	
		TreePreprocessTool.deleteNone(tree1);
		
		List<String> srlinfo1 = new ArrayList<>();		
		srlinfo1.add("8");
		srlinfo1.add("7");
		
		List<String> label1 = new ArrayList<>();
		label1.add("NULL_1");		
		label1.add("ARG1");
		
		String list1 = "(VP{gone[VBN]}(VBN{gone[VBN]} gone[9]))";
		
		SRLSample<HeadTreeNode> sample = parse.parse(tree, roles, ahg);
		assertEquals(Arrays.asList(sample.getLabelInfo()), label);
		for (int i = 0; i < srlinfo.size(); i++) {
			assertEquals(sample.getArgumentTree()[i].getLeftLeafIndex() + "", srlinfo.get(i));
		}
		assertEquals(sample.getPredicateTree()[0].getTree().toString(), list);
		
		SRLSample<HeadTreeNode> sample1 = parse.parse(tree1, roles1, ahg);
		assertEquals(Arrays.asList(sample1.getLabelInfo()), label1);
		for (int i = 0; i < srlinfo1.size(); i++) {
			assertEquals(sample1.getArgumentTree()[i].getLeftLeafIndex() + "", srlinfo1.get(i));
		}
		assertEquals(sample1.getPredicateTree()[0].getTree().toString(), list1);
	}
}
