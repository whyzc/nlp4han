package com.lc.nlp4han.srl;

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
import com.lc.nlp4han.srl.tree.SRLContextGeneratorConf;
import com.lc.nlp4han.srl.tree.SRLParseNormal;
import com.lc.nlp4han.srl.tree.SRLSample;
import com.lc.nlp4han.srl.tree.TreeNodeWrapper;

/**
 * 对生成特征进行单元测试(对只包含NULL类别，没有剪枝的样本生成特征)
 * @author 王馨苇
 *
 */
public class SRLSampleNormalEventStreamForOneStepTest {
	
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
		TreePreprocessTool.deleteNone(tree1);
		
		AbstractParseStrategy<HeadTreeNode> ttss = new SRLParseNormal();
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		String roles1 = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		SRLSample<HeadTreeNode> sample = ttss.parse(tree1, roles1, ahg);

		TreeNodeWrapper<HeadTreeNode>[] argumenttree = sample.getArgumentTree();
		TreeNodeWrapper<HeadTreeNode>[] predicatetree = sample.getPredicateTree();
		String[] labelinfo = sample.getLabelInfo();
		
		Properties featureConf = new Properties();	
		InputStream featureStream = SRLSampleNormalEventStreamForOneStepTest.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/srl/feature.properties");	
		featureConf.load(featureStream);
		SRLContextGenerator generator = new SRLContextGeneratorConf(featureConf);	
		
		List<Event> events = new ArrayList<Event>();
		for (int i = 0; i < argumenttree.length; i++) {
			String[] context = generator.getContext(i, argumenttree, labelinfo, predicatetree);
			events.add(new Event(labelinfo[i], context));
		}

		List<String> result21 = new ArrayList<>();
		result21.add("predicate=shore");
		result21.add("predicatepos=VB");
		result21.add("path=NP↑VP↓VB");
		result21.add("pathlength=3");
		result21.add("partialpath=NP↑VP");
		result21.add("phrasetype=NP");
		result21.add("position=after");
		result21.add("voice=a");	
		result21.add("headword=decline");
		result21.add("headwordpos=NN");
		result21.add("subcategorization=VP→VB PRT NP");
		result21.add("firstargument=a");
		result21.add("firstargumentpos=DT");
		result21.add("lastargument=1989");
		result21.add("lastargumentpos=CD");
		result21.add("positionAndvoice=after|a");
		result21.add("predicateAndpath=shore|NP↑VP↓VB");
		result21.add("pathAndpositionAndvoice=NP↑VP↓VB|after|a");
		result21.add("pathAndpositionAndvoiceAndpredicate=NP↑VP↓VB|after|a|shore");
		result21.add("headwordAndpredicateAndpath=decline|shore|NP↑VP↓VB");
		result21.add("headwordAndPhrasetype=decline|NP");
		result21.add("predicateAndHeadword=shore|decline");
		result21.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result22 = new ArrayList<>();
		result22.add("predicate=shore");
		result22.add("predicatepos=VB");
		result22.add("path=NP↑NP↑VP↓VB");
		result22.add("pathlength=4");
		result22.add("partialpath=NP↑NP↑VP");
		result22.add("phrasetype=NP");
		result22.add("position=after");
		result22.add("voice=a");	
		result22.add("headword=decline");
		result22.add("headwordpos=NN");
		result22.add("subcategorization=VP→VB PRT NP");
		result22.add("firstargument=a");
		result22.add("firstargumentpos=DT");
		result22.add("lastargument=decline");
		result22.add("lastargumentpos=NN");
		result22.add("positionAndvoice=after|a");
		result22.add("predicateAndpath=shore|NP↑NP↑VP↓VB");
		result22.add("pathAndpositionAndvoice=NP↑NP↑VP↓VB|after|a");
		result22.add("pathAndpositionAndvoiceAndpredicate=NP↑NP↑VP↓VB|after|a|shore");
		result22.add("headwordAndpredicateAndpath=decline|shore|NP↑NP↑VP↓VB");
		result22.add("headwordAndPhrasetype=decline|NP");
		result22.add("predicateAndHeadword=shore|decline");
		result22.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result8 = new ArrayList<>();
		result8.add("predicate=shore");
		result8.add("predicatepos=VB");
		result8.add("path=NP↑S↓VP↓NP↓VP↓VP↓VB");
		result8.add("pathlength=7");
		result8.add("partialpath=NP↑S");
		result8.add("phrasetype=NP");
		result8.add("position=before");
		result8.add("voice=a");	
		result8.add("headword=plan");
		result8.add("headwordpos=NN");
		result8.add("subcategorization=VP→VB PRT NP");
		result8.add("firstargument=the");
		result8.add("firstargumentpos=DT");
		result8.add("lastargument=plan");
		result8.add("lastargumentpos=NN");
		result8.add("positionAndvoice=before|a");
		result8.add("predicateAndpath=shore|NP↑S↓VP↓NP↓VP↓VP↓VB");
		result8.add("pathAndpositionAndvoice=NP↑S↓VP↓NP↓VP↓VP↓VB|before|a");
		result8.add("pathAndpositionAndvoiceAndpredicate=NP↑S↓VP↓NP↓VP↓VP↓VB|before|a|shore");
		result8.add("headwordAndpredicateAndpath=plan|shore|NP↑S↓VP↓NP↓VP↓VP↓VB");
		result8.add("headwordAndPhrasetype=plan|NP");
		result8.add("predicateAndHeadword=shore|plan");
		result8.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result1 = new ArrayList<>();
		result1.add("predicate=shore");
		result1.add("predicatepos=VB");
		result1.add("path=NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB");
		result1.add("pathlength=9");
		result1.add("partialpath=NP↑S");
		result1.add("phrasetype=NP");
		result1.add("position=before");
		result1.add("voice=a");	
		result1.add("headword=Mr.");
		result1.add("headwordpos=NNP");
		result1.add("subcategorization=VP→VB PRT NP");
		result1.add("firstargument=Mr.");
		result1.add("firstargumentpos=NNP");
		result1.add("lastargument=Spoon");
		result1.add("lastargumentpos=NNP");
		result1.add("positionAndvoice=before|a");
		result1.add("predicateAndpath=shore|NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB");
		result1.add("pathAndpositionAndvoice=NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB|before|a");
		result1.add("pathAndpositionAndvoiceAndpredicate=NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB|before|a|shore");
		result1.add("headwordAndpredicateAndpath=Mr.|shore|NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB");
		result1.add("headwordAndPhrasetype=Mr.|NP");
		result1.add("predicateAndHeadword=shore|Mr.");
		result1.add("predicateAndPhrasetype=shore|NP");
		
		List<Event> event19 = new ArrayList<Event>();
		List<Event> event20 = new ArrayList<Event>();
		List<Event> event7 = new ArrayList<Event>();
		List<Event> event1 = new ArrayList<Event>();
		event19.add(new Event("ARG1", result21.toArray(new String[result21.size()])));
		event20.add(new Event("NULL", result22.toArray(new String[result22.size()])));
		event7.add(new Event("ARG0", result8.toArray(new String[result8.size()])));
		event1.add(new Event("NULL", result1.toArray(new String[result1.size()])));
		
		assertEquals(argumenttree.length, 74);
		assertEquals(events.size(), 74);
		assertEquals(events.get(1).toString(), event1.get(0).toString());
		assertEquals(events.get(7).toString(), event7.get(0).toString());
		assertEquals(events.get(19).toString(), event19.get(0).toString());
		assertEquals(events.get(20).toString(), event20.get(0).toString());
	}
}
