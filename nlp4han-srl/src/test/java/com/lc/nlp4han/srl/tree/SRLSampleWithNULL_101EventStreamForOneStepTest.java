package com.lc.nlp4han.srl.tree;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.lc.nlp4han.srl.tree.SRLParseWithNULL_101;
import com.lc.nlp4han.srl.tree.SRLSample;
import com.lc.nlp4han.srl.tree.TreeNodeWrapper;

/**
 * 对生成特征进行单元测试(对只包含NULL类别，没且剪枝的样本生成特征)
 * @author 王馨苇
 *
 */
public class SRLSampleWithNULL_101EventStreamForOneStepTest {

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
		
		AbstractParseStrategy<HeadTreeNode> ttss = new SRLParseWithNULL_101();
		AbstractHeadGenerator ahg = new HeadGeneratorCollins();
		String roles1 = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		SRLSample<HeadTreeNode> sample = ttss.parse(tree1, roles1, ahg);

		TreeNodeWrapper<HeadTreeNode>[] argumenttree = sample.getArgumentTree();
		TreeNodeWrapper<HeadTreeNode>[] predicatetree = sample.getPredicateTree();
		String[] labelinfo = sample.getLabelInfo();
		
		Properties featureConf = new Properties();	
		InputStream featureStream = SRLSampleWithNULL_101EventStreamForOneStepTest.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/srl/feature.properties");	
		featureConf.load(featureStream);
		SRLContextGenerator generator = new SRLContextGeneratorConf(featureConf);
		
		List<Event> events = new ArrayList<Event>();
		for (int i = 0; i < argumenttree.length; i++) {
			String[] context = generator.getContext(i, argumenttree, labelinfo, predicatetree);
			events.add(new Event(labelinfo[i], context));
		}

		List<String> result19 = new ArrayList<>();
		result19.add("predicate=shore");
		result19.add("predicatepos=VB");
		result19.add("path=NP↑VP↓VB");
		result19.add("pathlength=3");
		result19.add("partialpath=NP↑VP");
		result19.add("phrasetype=NP");
		result19.add("position=after");
		result19.add("voice=a");	
		result19.add("headword=decline");
		result19.add("headwordpos=NN");
		result19.add("subcategorization=VP→VB PRT NP");
		result19.add("firstargument=a");
		result19.add("firstargumentpos=DT");
		result19.add("lastargument=1989");
		result19.add("lastargumentpos=CD");
		result19.add("positionAndvoice=after|a");
		result19.add("predicateAndpath=shore|NP↑VP↓VB");
		result19.add("pathAndpositionAndvoice=NP↑VP↓VB|after|a");
		result19.add("pathAndpositionAndvoiceAndpredicate=NP↑VP↓VB|after|a|shore");
		result19.add("headwordAndpredicateAndpath=decline|shore|NP↑VP↓VB");
		result19.add("headwordAndPhrasetype=decline|NP");
		result19.add("predicateAndHeadword=shore|decline");
		result19.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result20 = new ArrayList<>();
		result20.add("predicate=shore");
		result20.add("predicatepos=VB");
		result20.add("path=NP↑NP↑VP↓VB");
		result20.add("pathlength=4");
		result20.add("partialpath=NP↑NP↑VP");
		result20.add("phrasetype=NP");
		result20.add("position=after");
		result20.add("voice=a");	
		result20.add("headword=decline");
		result20.add("headwordpos=NN");
		result20.add("subcategorization=VP→VB PRT NP");
		result20.add("firstargument=a");
		result20.add("firstargumentpos=DT");
		result20.add("lastargument=decline");
		result20.add("lastargumentpos=NN");
		result20.add("positionAndvoice=after|a");
		result20.add("predicateAndpath=shore|NP↑NP↑VP↓VB");
		result20.add("pathAndpositionAndvoice=NP↑NP↑VP↓VB|after|a");
		result20.add("pathAndpositionAndvoiceAndpredicate=NP↑NP↑VP↓VB|after|a|shore");
		result20.add("headwordAndpredicateAndpath=decline|shore|NP↑NP↑VP↓VB");
		result20.add("headwordAndPhrasetype=decline|NP");
		result20.add("predicateAndHeadword=shore|decline");
		result20.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result7 = new ArrayList<>();
		result7.add("predicate=shore");
		result7.add("predicatepos=VB");
		result7.add("path=NP↑S↓VP↓NP↓VP↓VP↓VB");
		result7.add("pathlength=7");
		result7.add("partialpath=NP↑S");
		result7.add("phrasetype=NP");
		result7.add("position=before");
		result7.add("voice=a");	
		result7.add("headword=plan");
		result7.add("headwordpos=NN");
		result7.add("subcategorization=VP→VB PRT NP");
		result7.add("firstargument=the");
		result7.add("firstargumentpos=DT");
		result7.add("lastargument=plan");
		result7.add("lastargumentpos=NN");
		result7.add("positionAndvoice=before|a");
		result7.add("predicateAndpath=shore|NP↑S↓VP↓NP↓VP↓VP↓VB");
		result7.add("pathAndpositionAndvoice=NP↑S↓VP↓NP↓VP↓VP↓VB|before|a");
		result7.add("pathAndpositionAndvoiceAndpredicate=NP↑S↓VP↓NP↓VP↓VP↓VB|before|a|shore");
		result7.add("headwordAndpredicateAndpath=plan|shore|NP↑S↓VP↓NP↓VP↓VP↓VB");
		result7.add("headwordAndPhrasetype=plan|NP");
		result7.add("predicateAndHeadword=shore|plan");
		result7.add("predicateAndPhrasetype=shore|NP");
		
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
		
		List<String> result4 = new ArrayList<>();
		result4.add("predicate=shore");
		result4.add("predicatepos=VB");
		result4.add("path=VP↓S↓VP↓NP↓VP↓VP↓VB");
		result4.add("pathlength=7");
		result4.add("partialpath=VP");
		result4.add("phrasetype=VP");
		result4.add("position=before");
		result4.add("voice=a");	
		result4.add("headword=said");
		result4.add("headwordpos=VBD");
		result4.add("subcategorization=VP→VB PRT NP");
		result4.add("firstargument=said");
		result4.add("firstargumentpos=VBD");
		result4.add("lastargument=1989");
		result4.add("lastargumentpos=CD");
		result4.add("positionAndvoice=before|a");
		result4.add("predicateAndpath=shore|VP↓S↓VP↓NP↓VP↓VP↓VB");
		result4.add("pathAndpositionAndvoice=VP↓S↓VP↓NP↓VP↓VP↓VB|before|a");
		result4.add("pathAndpositionAndvoiceAndpredicate=VP↓S↓VP↓NP↓VP↓VP↓VB|before|a|shore");
		result4.add("headwordAndpredicateAndpath=said|shore|VP↓S↓VP↓NP↓VP↓VP↓VB");
		result4.add("headwordAndPhrasetype=said|VP");
		result4.add("predicateAndHeadword=shore|said");
		result4.add("predicateAndPhrasetype=shore|VP");
		
		List<Event> event21 = new ArrayList<Event>();
		List<Event> event22 = new ArrayList<Event>();
		List<Event> event8 = new ArrayList<Event>();
		List<Event> event1 = new ArrayList<Event>();
		List<Event> event4 = new ArrayList<Event>();
		event21.add(new Event("ARG1", result19.toArray(new String[result19.size()])));
		event22.add(new Event("NULL1", result20.toArray(new String[result20.size()])));
		event8.add(new Event("ARG0", result7.toArray(new String[result7.size()])));
		event1.add(new Event("NULL_1", result1.toArray(new String[result1.size()])));
		event4.add(new Event("NULL0", result4.toArray(new String[result4.size()])));
		
		HashSet<String> hs1 = new HashSet<>();
		for (int i = 0; i < labelinfo.length; i++) {
			hs1.add(labelinfo[i]);
		}
		HashSet<String> hs2 = new HashSet<>();
		hs2.add("ARG0");
		hs2.add("ARG1");
		hs2.add("NULL_1");
		hs2.add("NULL1");
		hs2.add("NULL0");
		
		assertEquals(hs1.toString(), hs2.toString());
		assertEquals(argumenttree.length, 74);
		assertEquals(events.size(), 74);
		assertEquals(events.get(1).toString(), event1.get(0).toString());
		assertEquals(events.get(7).toString(), event8.get(0).toString());
		assertEquals(events.get(19).toString(), event21.get(0).toString());
		assertEquals(events.get(20).toString(), event22.get(0).toString());
		assertEquals(events.get(4).toString(), event4.get(0).toString());
	}
}
