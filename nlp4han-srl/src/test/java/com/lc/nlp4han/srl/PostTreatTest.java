package com.lc.nlp4han.srl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreePreprocessTool;
import com.lc.nlp4han.ml.util.Sequence;
import com.lc.nlp4han.srl.tree.AbstractParseStrategy;
import com.lc.nlp4han.srl.tree.PostTreatmentUtil;
import com.lc.nlp4han.srl.tree.SRLParseNormal;
import com.lc.nlp4han.srl.tree.SRLParseNormalWithPruning;
import com.lc.nlp4han.srl.tree.SRLSample;

/**
 * 对没有剪枝得到的结果后处理的测试
 * 后处理是为了防止嵌套情况的出现，当一颗树的根被标记成论元，根的子节点也有被标记成论元的，就是嵌套结构
 * 解决：保留一棵树中概率最大的论元
 * @author 王馨苇
 *
 */
public class PostTreatTest {

	private String roles;
	private String[] pre;
	private TreeNode tree;
	
	@Before
	public void setUp(){
		roles = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		tree = BracketExpUtil.generateTree(""
				+ "((S(S(NP-SBJ(NNP Mr.)(NNP Spoon))(VP(VBD said)(SBAR (-NONE- 0)(S(NP-SBJ(DT the)(NN plan))"
				+ "(VP(VBZ is)(RB not)(NP-PRD(DT an)(NN attempt)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB shore)"
				+ "(PRT(RP up))(NP(NP(DT a)(NN decline))(PP-LOC(IN in)(NP(NN ad)(NNS pages)))(PP-TMP(IN in)"
				+ "(NP(NP(DT the)(JJ first)(CD nine)(NNS months))(PP(IN of)(NP(CD 1989)))))))))))))))"
				+ "(: ;)(S(NP-SBJ(NP(NNP Newsweek)(POS 's))(NN ad)(NNS pages))(VP(VBD totaled)(NP"
				+ "(NP(CD 1,620))(, ,)(NP(NP(DT a)(NN drop))(PP(IN of)(NP (CD 3.2)(NN %)))"
				+ "(PP-DIR(IN from)(NP(JJ last)(NN year)))))(, ,)(PP(VBG according)(PP(TO to)"
				+ "(NP(NNP Publishers)(NNP Information)(NNP Bureau))))))(. .)))");	
		TreePreprocessTool.deleteNone(tree);
	}
	
	/**
	 * 为没有剪枝操作的数据得到的结果进行后处理的单元测试
	 */
	@Test
	public void test(){
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		AbstractParseStrategy<HeadTreeNode> parse = new SRLParseNormal();
		SRLSample<HeadTreeNode> sample = parse.parse(tree, roles, ahg);
		
		List<String> label = new ArrayList<>();
		label.add("NULL1");
		label.add("NULL2");
		label.add("NULL3");
		label.add("NULL");		
		label.add("NULL");		
		label.add("NULL");	
		label.add("NULL");	
		label.add("A");
		label.add("B");
		label.add("C");
		label.add("NULL");
		label.add("NULL");
		label.add("F");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("M");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("E");
		
		List<Double> prop = new ArrayList<>();
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(5.0);
		prop.add(4.0);
		prop.add(7.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(3.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(6.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(8.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(5.0);
		
		List<String> result = new ArrayList<>();
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("C");
		result.add("NULL");
		result.add("NULL");
		result.add("F");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("M");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("E");
		
		Sequence sequence = new Sequence();
		
		for (int i = 0; i < label.size() && i < prop.size(); i++) {
			sequence.add(label.get(i), prop.get(i));
		}
		if(sample.getIsPruning() == true){
			pre = PostTreatmentUtil.postTreat(sample.getArgumentTree(), sequence, PostTreatmentUtil.getSonTreeCount(sample.getPredicateTree()[0].getTree()));
		}else{
			pre = PostTreatmentUtil.postTreat(sample.getArgumentTree(), sequence, PostTreatmentUtil.getSonTreeCount(sample.getArgumentTree()[0].getTree().getParent())-1);
		}
		
		assertEquals(Arrays.asList(pre), result);
	}
	
	/**
	 * 为剪枝的数据得到的结果进行后处理的单元测试
	 */
	@Test
	public void testForPruning(){
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		AbstractParseStrategy<HeadTreeNode> parse = new SRLParseNormalWithPruning();
		SRLSample<HeadTreeNode> sample = parse.parse(tree, roles, ahg);
		
		List<String> label = new ArrayList<>();
		label.add("NULL1");
		label.add("ARG1");
		label.add("NULL3");
		label.add("A");
		label.add("B");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("A");
		label.add("B");
		label.add("C");
		label.add("NULL");
		label.add("F");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("W");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("NULL");
		label.add("E");
		
		List<Double> prop = new ArrayList<>();
		prop.add(0.0);
		prop.add(5.0);
		prop.add(7.0);
		prop.add(6.0);
		prop.add(1.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(5.0);
		prop.add(4.0);
		prop.add(7.0);
		prop.add(0.0);
		prop.add(3.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);	
		prop.add(0.0);
		prop.add(7.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(0.0);
		prop.add(5.0);
		
		List<String> result = new ArrayList<>();
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("A");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("A");
		result.add("NULL");
		result.add("C");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("W");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("NULL");
		result.add("E");
		
	    Sequence sequence = new Sequence();
		
		for (int i = 0; i < label.size() && i < prop.size(); i++) {
			sequence.add(label.get(i), prop.get(i));
		}
		if(sample.getIsPruning() == true){
			pre = PostTreatmentUtil.postTreat(sample.getArgumentTree(), sequence, PostTreatmentUtil.getSonTreeCount(sample.getPredicateTree()[0].getTree().getParent()));
		}else{
			pre = PostTreatmentUtil.postTreat(sample.getArgumentTree(), sequence, sample.getArgumentTree().length);
		}
	}
}
