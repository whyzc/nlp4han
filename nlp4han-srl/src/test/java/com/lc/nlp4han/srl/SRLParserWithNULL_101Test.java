package com.lc.nlp4han.srl;

import static org.junit.Assert.assertEquals;

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

/**
 * 转成样本类的测试，此时样本类标签增加NULL_1 NULL0 NULL1,没有剪枝
 * @author 王馨苇
 *
 */
public class SRLParserWithNULL_101Test {

	@Test
	public void test(){
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		AbstractParseStrategy<HeadTreeNode> parse = new SRLParseWithNULL_101();
		
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

		List<String> srlinfo = new ArrayList<>();		
		srlinfo.add("0");
		srlinfo.add("0");
		srlinfo.add("0");
		srlinfo.add("1");
		srlinfo.add("2");
		srlinfo.add("2");
		srlinfo.add("4");
		srlinfo.add("4");
		srlinfo.add("4");
		srlinfo.add("5");
		srlinfo.add("6");
		srlinfo.add("6");
		srlinfo.add("7");
		srlinfo.add("8");
		srlinfo.add("8");
		srlinfo.add("9");
		srlinfo.add("11");
		srlinfo.add("11");
		srlinfo.add("12");
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
		srlinfo.add("27");
		srlinfo.add("27");
		srlinfo.add("27");
		srlinfo.add("27");
		srlinfo.add("28");
		srlinfo.add("29");
		srlinfo.add("30");
		srlinfo.add("31");
		srlinfo.add("31");
		srlinfo.add("32");
		srlinfo.add("32");
		srlinfo.add("32");
		srlinfo.add("34");
		srlinfo.add("34");
		srlinfo.add("34");
		srlinfo.add("35");
		srlinfo.add("36");
		srlinfo.add("36");
		srlinfo.add("37");
		srlinfo.add("37");
		srlinfo.add("38");
		srlinfo.add("39");
		srlinfo.add("39");
		srlinfo.add("40");
		srlinfo.add("40");
		srlinfo.add("41");
		srlinfo.add("43");
		srlinfo.add("43");
		srlinfo.add("44");
		srlinfo.add("44");
		srlinfo.add("45");
		srlinfo.add("45");
		srlinfo.add("46");
		srlinfo.add("47");
		
		List<String> label = new ArrayList<>();
		label.add("NULL0");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL0");
		label.add("NULL_1");
		label.add("NULL0");
		label.add("ARG0");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL0");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL0");
		label.add("NULL_1");
		label.add("NULL_1");
		label.add("NULL0");
		label.add("NULL_1");
		label.add("NULL0");
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

		String list = "(VB{shore[VB]} shore[12])";
		
		assertEquals(Arrays.asList(sample.getLabelInfo()), label);
		for (int i = 0; i < srlinfo.size(); i++) {
			assertEquals(sample.getArgumentTree()[i].getLeftLeafIndex() + "", srlinfo.get(i));
		}
		assertEquals(sample.getPredicateTree()[0].getTree().toString(), list);
	}
}
