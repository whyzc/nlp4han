package com.lc.nlp4han.srl.tree;

import static org.junit.Assert.*;

import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreePreprocessTool;
import com.lc.nlp4han.srl.tree.AbstractParseStrategy;
import com.lc.nlp4han.srl.tree.SRLParseNormalWithPruning;
import com.lc.nlp4han.srl.tree.SRLSample;
import com.lc.nlp4han.srl.tree.SRLTreeNode;
import com.lc.nlp4han.srl.tree.TreeToSRLTree;

/**
 * 将树转成语义角色标注树的单元测试
 * @author 王馨苇
 *
 */
public class TreeToSRLTreeTest {
	
	@Test
	public void test(){
		AbstractHeadGenerator ahg = new HeadGeneratorCollins(new HeadRuleSetPTB());
		AbstractParseStrategy<HeadTreeNode> parse = new SRLParseNormalWithPruning();
		
		String roles = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		String result = "(S(S(NP(NNP Mr.[0])(NNP Spoon[1]))(VP(VBD said[2])(S(NP{ARG0}(DT the[4])(NN plan[5]))"
				+ "(VP(VBZ is[6])(RB not[7])(NP(DT an[8])(NN attempt[9])(VP(TO to[11])(VP(VB shore[12])"
				+ "(PRT(RP up[13]))(NP{ARG1}(NP(DT a[14])(NN decline[15]))(PP(IN in[16])(NP(NN ad[17])(NNS pages[18])))(PP(IN in[19])"
				+ "(NP(NP(DT the[20])(JJ first[21])(CD nine[22])(NNS months[23]))(PP(IN of[24])(NP(CD 1989[25])))))))))))))"
				+ "(: ;[26])(S(NP(NP(NNP Newsweek[27])(POS 's[28]))(NN ad[29])(NNS pages[30]))(VP(VBD totaled[31])(NP"
				+ "(NP(CD 1,620[32]))(, ,[33])(NP(NP(DT a[34])(NN drop[35]))(PP(IN of[36])(NP(CD 3.2[37])(NN %[38])))"
				+ "(PP(IN from[39])(NP(JJ last[40])(NN year[41])))))(, ,[42])(PP(VBG according[43])(PP(TO to[44])"
				+ "(NP(NNP Publishers[45])(NNP Information[46])(NNP Bureau[47]))))))(. .[48]))";
		
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
		
		SRLSample<HeadTreeNode> sample = parse.parse(tree, roles, ahg);
		SRLTreeNode srltree = TreeToSRLTree.treeToSRLTree(tree, sample.getArgumentTree(), sample.getLabelInfo());
		
		assertEquals(result, srltree.toString());
	}
}
