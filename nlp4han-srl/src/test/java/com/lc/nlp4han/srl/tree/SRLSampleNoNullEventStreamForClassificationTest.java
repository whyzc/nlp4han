package com.lc.nlp4han.srl.tree;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreePreprocessTool;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.srl.tree.AbstractParseStrategy;
import com.lc.nlp4han.srl.tree.SRLContextGenerator;
import com.lc.nlp4han.srl.tree.SRLContextGeneratorConfForClassification;
import com.lc.nlp4han.srl.tree.SRLParseNormal;
import com.lc.nlp4han.srl.tree.SRLSample;
import com.lc.nlp4han.srl.tree.TreeNodeWrapper;

/**
 * 对分类阶段生成特征进行单元测试
 * (样本包含NULL类别或者包含NULL_101类别，没有剪枝或者有剪枝的样本都适用)
 * 将样本类转成分类阶段需要的样式，分类阶段没有类别NULL
 * @author 王馨苇
 *
 */
public class SRLSampleNoNullEventStreamForClassificationTest {
	
	@Test
	public void test() throws IOException{
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		String roles1 = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		AbstractParseStrategy<HeadTreeNode> ttss = new SRLParseNormal();
		
		Properties featureConf = new Properties();	
		InputStream featureStream = SRLSampleNoNullEventStreamForClassificationTest.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/srl/feature.properties");	
		featureConf.load(featureStream);
		SRLContextGenerator generator = new SRLContextGeneratorConfForClassification(featureConf);	
			
		TreeNode tree1 = BracketExpUtil.generateTree(""
				+ "((S(S(NP-SBJ(NNP Mr.)(NNP Spoon))(VP(VBD said)(SBAR (-NONE- 0)(S(NP-SBJ(DT the)(NN plan))"
				+ "(VP(VBZ is)(RB not)(NP-PRD(DT an)(NN attempt)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB shore)"
				+ "(PRT(RP up))(NP(NP(DT a)(NN decline))(PP-LOC(IN in)(NP(NN ad)(NNS pages)))(PP-TMP(IN in)"
				+ "(NP(NP(DT the)(JJ first)(CD nine)(NNS months))(PP(IN of)(NP(CD 1989)))))))))))))))"
				+ "(: ;)(S(NP-SBJ(NP(NNP Newsweek)(POS 's))(NN ad)(NNS pages))(VP(VBD totaled)(NP"
				+ "(NP(CD 1,620))(, ,)(NP(NP(DT a)(NN drop))(PP(IN of)(NP (CD 3.2)(NN %)))"
				+ "(PP-DIR(IN from)(NP(JJ last)(NN year)))))(, ,)(PP(VBG according)(PP(TO to)"
				+ "(NP(NNP Publishers)(NNP Information)(NNP Bureau))))))(. .)))");			
		TreePreprocessTool.deleteNone(tree1);
		
		SRLSample<HeadTreeNode> sample = ttss.parse(tree1, roles1, ahg);
		TreeNodeWrapper<HeadTreeNode>[] argumenttree = sample.getArgumentTree();
		TreeNodeWrapper<HeadTreeNode>[] predicatetree = sample.getPredicateTree();
		String[] labelinfo = sample.getLabelInfo();
		String[] labelinfoIden = sample.getIdentificationLabelInfo();
		
		List<Integer> index = SRLSample.filterNotNULLLabelIndex(labelinfoIden);
		String[] labelfortrain = SRLSample.getLabelFromIndex(labelinfo, index);
		TreeNodeWrapper<HeadTreeNode>[] argumenttreefortrain = SRLSample.getArgumentTreeFromIndex(argumenttree, index);
		
		List<Event> events = new ArrayList<Event>();
		for (int i = 0; i < argumenttreefortrain.length; i++) {
			String[] context = generator.getContext(i, argumenttreefortrain, labelfortrain, predicatetree);
			events.add(new Event(labelfortrain[i], context));
		}

		List<String> result1 = new ArrayList<>();
		result1.add("path=NP↑VP↓VB");
		result1.add("phrasetype=NP");	
		result1.add("headword=decline");
		result1.add("headwordpos=NN");
		result1.add("subcategorization=VP→VB PRT NP");
		result1.add("firstargument=a");
		result1.add("firstargumentpos=DT");
		result1.add("lastargument=1989");
		result1.add("lastargumentpos=CD");
		result1.add("positionAndvoice=after|a");
		result1.add("predicateAndHeadword=shore|decline");
		result1.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result0 = new ArrayList<>();
		result0.add("path=NP↑S↓VP↓NP↓VP↓VP↓VB");
		result0.add("phrasetype=NP");
		result0.add("headword=plan");
		result0.add("headwordpos=NN");
		result0.add("subcategorization=VP→VB PRT NP");
		result0.add("firstargument=the");
		result0.add("firstargumentpos=DT");
		result0.add("lastargument=plan");
		result0.add("lastargumentpos=NN");
		result0.add("positionAndvoice=before|a");
		result0.add("predicateAndHeadword=shore|plan");
		result0.add("predicateAndPhrasetype=shore|NP");

		List<Event> event1 = new ArrayList<Event>();
		List<Event> event0 = new ArrayList<Event>();
		event0.add(new Event("ARG0", result0.toArray(new String[result0.size()])));
		event1.add(new Event("ARG1", result1.toArray(new String[result1.size()])));
		
		assertEquals(events.size(), 2);
		assertEquals(events.get(1).toString(), event1.get(0).toString());
		assertEquals(events.get(0).toString(), event0.get(0).toString());
	}
}
