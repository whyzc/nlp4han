package com.lc.nlp4han.srl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.maxent.TreePreTreatment;
import com.lc.nlp4han.ml.model.Event;

/**
 * 对识别阶段生成特征进行单元测试(对包含NULL_101类别，没有剪枝的样本生成特征)
 * @author 王馨苇
 *
 */
public class SRLSampleWithNULL_101EventStreamForIdentificationTest {

	@Test
	public void test() throws IOException{
		TreeNode tree1 = BracketExpUtil.generateTree(""
				+ "((S(S(NP-SBJ(NNP Mr.)(NNP Spoon))(VP(VBD said)(SBAR (-NONE- 0)(S(NP-SBJ(DT the)(NN plan))"
				+ "(VP(VBZ is)(RB not)(NP-PRD(DT an)(NN attempt)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB shore)"
				+ "(PRT(RP up))(NP(NP(DT a)(NN decline))(PP-LOC(IN in)(NP(NN ad)(NNS pages)))(PP-TMP(IN in)"
				+ "(NP(NP(DT the)(JJ first)(CD nine)(NNS months))(PP(IN of)(NP(CD 1989)))))))))))))))"
				+ "(: ;)(S(NP-SBJ(NP(NNP Newsweek)(POS 's))(NN ad)(NNS pages))(VP(VBD totaled)(NP"
				+ "(NP(CD 1,620))(, ,)(NP(NP(DT a)(NN drop))(PP(IN of)(NP (CD 3.2)(NN %)))"
				+ "(PP-DIR(IN from)(NP(JJ last)(NN year)))))(, ,)(PP(VBG according)(PP(TO to)"
				+ "(NP(NNP Publishers)(NNP Information)(NNP Bureau))))))(. .)))");	
		TreePreTreatment.deleteNone(tree1);
		
		AbstractParseStrategy<HeadTreeNode> ttss = new SRLParseWithNULL_101();
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		String roles1 = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		SRLSample<HeadTreeNode> sample = ttss.parse(tree1, roles1, ahg);

		TreeNodeWrapper<HeadTreeNode>[] argumenttree = sample.getArgumentTree();
		TreeNodeWrapper<HeadTreeNode>[] predicatetree = sample.getPredicateTree();
		String[] labelinfo = sample.getIdentificationLabelInfo();
		SRLContextGenerator generator = new SRLContextGeneratorConfForIdentification();	
		List<Event> events = new ArrayList<Event>();
		for (int i = 0; i < argumenttree.length; i++) {
			String[] context = generator.getContext(i, argumenttree, labelinfo, predicatetree);
			events.add(new Event(labelinfo[i],context));
		}

		List<String> result21 = new ArrayList<>();
		result21.add("path=NP↑VP↓VB");
		result21.add("pathlength=3");
		result21.add("headword=decline");
		result21.add("headwordpos=NN");
		result21.add("predicateAndHeadword=shore|decline");
		result21.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result22 = new ArrayList<>();
		result22.add("path=NP↑NP↑VP↓VB");
		result22.add("pathlength=4");
		result22.add("headword=decline");
		result22.add("headwordpos=NN");
		result22.add("predicateAndHeadword=shore|decline");
		result22.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result8 = new ArrayList<>();
		result8.add("path=NP↑S↓VP↓NP↓S↓VP↓VP↓VB");
		result8.add("pathlength=8");
		result8.add("headword=plan");
		result8.add("headwordpos=NN");
		result8.add("predicateAndHeadword=shore|plan");
		result8.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result1 = new ArrayList<>();
		result1.add("path=NP↑S↓VP↓SBAR↓S↓VP↓NP↓S↓VP↓VP↓VB");
		result1.add("pathlength=11");
		result1.add("headword=Mr.");
		result1.add("headwordpos=NNP");
		result1.add("predicateAndHeadword=shore|Mr.");
		result1.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result4 = new ArrayList<>();
		result4.add("path=VP↓SBAR↓S↓VP↓NP↓S↓VP↓VP↓VB");
		result4.add("pathlength=9");
		result4.add("headword=said");
		result4.add("headwordpos=VBD");
		result4.add("predicateAndHeadword=shore|said");
		result4.add("predicateAndPhrasetype=shore|VP");
		
		List<Event> event21 = new ArrayList<Event>();
		List<Event> event22 = new ArrayList<Event>();
		List<Event> event8 = new ArrayList<Event>();
		List<Event> event1 = new ArrayList<Event>();
		List<Event> event4 = new ArrayList<Event>();
		event21.add(new Event("YES",result21.toArray(new String[result21.size()])));
		event22.add(new Event("NULL1",result22.toArray(new String[result22.size()])));
		event8.add(new Event("YES",result8.toArray(new String[result8.size()])));
		event1.add(new Event("NULL_1",result1.toArray(new String[result1.size()])));
		event4.add(new Event("NULL0",result4.toArray(new String[result4.size()])));
		
		assertEquals(argumenttree.length,76);
		assertEquals(events.size(),76);
		assertEquals(events.get(1).toString(),event1.get(0).toString());
		assertEquals(events.get(8).toString(),event8.get(0).toString());
		assertEquals(events.get(21).toString(),event21.get(0).toString());
		assertEquals(events.get(22).toString(),event22.get(0).toString());
		assertEquals(events.get(4).toString(),event4.get(0).toString());
	}
}
