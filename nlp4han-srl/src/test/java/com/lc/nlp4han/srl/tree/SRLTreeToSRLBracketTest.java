package com.lc.nlp4han.srl.tree;

import static org.junit.Assert.assertEquals;

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
 * 将一颗语义角色树输出为中括号形式的测试
 * @author 王馨苇
 *
 */
public class SRLTreeToSRLBracketTest {
	
	@Test
	public void test(){
		AbstractHeadGenerator ahg = new HeadGeneratorCollins(new HeadRuleSetPTB());
		AbstractParseStrategy<HeadTreeNode> parse = new SRLParseNormalWithPruning();
		
		String roles = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		TreeNode tree = BracketExpUtil.generateTree(""
				+ "((S(S(NP-SBJ(NNP Mr.)(NNP Spoon))(VP(VBD said)(SBAR (-NONE- 0)(S(NP-SBJ(DT the)(NN plan))"
				+ "(VP(VBZ is)(RB not)(NP-PRD(DT an)(NN attempt)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB shore)"
				+ "(PRT(RP up))(NP(NP(DT a)(NN decline))(PP-LOC(IN in)(NP(NN ad)(NNS pages)))(PP-TMP(IN in)"
				+ "(NP(NP(DT the)(JJ first)(CD nine)(NNS months))(PP(IN in)(NP(CD 1989)))))))))))))))"
				+ "(: ;)(S(NP-SBJ(NP(NNP Newsweek)(POS 's))(NN ad)(NNS pages))(VP(VBD totaled)(NP"
				+ "(NP(CD 1,620))(, ,)(NP(NP(DT a)(NN drop))(PP(IN of)(NP (CD 3.2)(NN %)))"
				+ "(PP-DIR(IN from)(NP(JJ last)(NN year)))))(, ,)(PP(VBG according)(PP(TO to)"
				+ "(NP(NNP Publishers)(NNP Information)(NNP Bureau))))))(. .)))");	
		TreePreprocessTool.deleteNone(tree);
		
		SRLSample<HeadTreeNode> sample = parse.parse(tree, roles, ahg);
		SRLTreeNode srltree = TreeToSRLTree.treeToSRLTree(tree, sample.getArgumentTree(), sample.getLabelInfo());
		String result = "Mr. Spoon said [ the plan ]ARG0 is not an attempt to shore up [ a decline in ad pages in the first nine months in 1989 ]ARG1"
				+ " ; Newsweek 's ad pages totaled 1,620 , a drop of 3.2 % from last year , according to Publishers Information Bureau . ";
		
		String roles1 = "wsj/00/wsj_0071.mrg 37 9 gold go.13 pn--a 7:1-ARG1 9:1-rel";
		TreeNode tree1 = BracketExpUtil.generateTree("((S(S(NP-SBJ (PRP We))(VP (VBD got)(NP(PRP$ our)(CD two)(NNS six-packs))))(: --)(CC and)(S(NP-SBJ(PRP they))(VP (VBP 're) (VP (VBN gone) )))(. .)('' '')))");	
		TreePreprocessTool.deleteNone(tree1);	
		
		SRLSample<HeadTreeNode> sample1 = parse.parse(tree1, roles1, ahg);
		SRLTreeNode srltree1 = TreeToSRLTree.treeToSRLTree(tree1, sample1.getArgumentTree(), sample1.getLabelInfo());
		String result1 = "We got our two six-packs -- and [ they ]ARG1 're gone . '' ";
		
		assertEquals(result,SRLTreeNode.printSRLBracket(srltree));
		assertEquals(result1,SRLTreeNode.printSRLBracket(srltree1));
	}
}
