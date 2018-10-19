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
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreePreprocessTool;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.srl.tree.AbstractParseStrategy;
import com.lc.nlp4han.srl.tree.SRLContextGenerator;
import com.lc.nlp4han.srl.tree.SRLContextGeneratorConf;
import com.lc.nlp4han.srl.tree.SRLParseNormalWithPruning;
import com.lc.nlp4han.srl.tree.SRLSample;
import com.lc.nlp4han.srl.tree.TreeNodeWrapper;

/**
 * 对生成特征进行单元测试(对只包含NULL类别，且剪枝的样本生成特征)
 * @author 王馨苇
 *
 */
public class SRLSampleNormalWithPruningEventStreamForOneStepTest {
	
	@Test
	public void test() throws IOException{
		TreeNode tree1 = BracketExpUtil.generateTreeNoTopBracket(""
				+ "((S(S(NP-SBJ(NNP Mr.)(NNP Spoon))(VP(VBD said)(SBAR (-NONE- 0)(S(NP-SBJ(DT the)(NN plan))"
				+ "(VP(VBZ is)(RB not)(NP-PRD(DT an)(NN attempt)(S(NP-SBJ(-NONE- *))(VP(TO to)(VP(VB shore)"
				+ "(PRT(RP up))(NP(NP(DT a)(NN decline))(PP-LOC(IN in)(NP(NN ad)(NNS pages)))(PP-TMP(IN in)"
				+ "(NP(NP(DT the)(JJ first)(CD nine)(NNS months))(PP(IN of)(NP(CD 1989)))))))))))))))"
				+ "(: ;)(S(NP-SBJ(NP(NNP Newsweek)(POS 's))(NN ad)(NNS pages))(VP(VBD totaled)(NP"
				+ "(NP(CD 1,620))(, ,)(NP(NP(DT a)(NN drop))(PP(IN of)(NP (CD 3.2)(NN %)))"
				+ "(PP-DIR(IN from)(NP(JJ last)(NN year)))))(, ,)(PP(VBG according)(PP(TO to)"
				+ "(NP(NNP Publishers)(NNP Information)(NNP Bureau))))))(. .)))");			
		TreePreprocessTool.deleteNone(tree1);
		
		AbstractParseStrategy<HeadTreeNode> ttss = new SRLParseNormalWithPruning();
		AbstractHeadGenerator ahg = new HeadGeneratorCollins(new HeadRuleSetPTB());
		String roles1 = "wsj/00/wsj0012.mrg 9 12 gold shore.01 i---a 4:1*10:0-ARG0 12:0,13:1-rel 14:2-ARG1";
		SRLSample<HeadTreeNode> sample = ttss.parse(tree1, roles1, ahg);

		TreeNodeWrapper<HeadTreeNode>[] argumenttree = sample.getArgumentTree();
		TreeNodeWrapper<HeadTreeNode>[] predicatetree = sample.getPredicateTree();
		String[] labelinfo = sample.getLabelInfo();
		
		Properties featureConf = new Properties();	
		InputStream featureStream = SRLSampleNormalWithPruningEventStreamForOneStepTest.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/srl/feature.properties");	
		featureConf.load(featureStream);
		SRLContextGenerator generator = new SRLContextGeneratorConf(featureConf);	
		
		List<Event> events = new ArrayList<Event>();
		for (int i = 0; i < argumenttree.length; i++) {
			String[] context = generator.getContext(i, argumenttree, labelinfo, predicatetree);
			events.add(new Event(labelinfo[i], context));
		}

		List<String> result1 = new ArrayList<>();
		result1.add("predicate=shore");
		result1.add("predicatepos=VB");
		result1.add("path=NP↑VP↓VB");
		result1.add("pathlength=3");
		result1.add("partialpath=NP↑VP");
		result1.add("phrasetype=NP");
		result1.add("position=after");
		result1.add("voice=a");	
		result1.add("headword=decline");
		result1.add("headwordpos=NN");
		result1.add("subcategorization=VP→VB PRT NP");
		result1.add("firstargument=a");
		result1.add("firstargumentpos=DT");
		result1.add("lastargument=1989");
		result1.add("lastargumentpos=CD");
		result1.add("positionAndvoice=after|a");
		result1.add("predicateAndpath=shore|NP↑VP↓VB");
		result1.add("pathAndpositionAndvoice=NP↑VP↓VB|after|a");
		result1.add("pathAndpositionAndvoiceAndpredicate=NP↑VP↓VB|after|a|shore");
		result1.add("headwordAndpredicateAndpath=decline|shore|NP↑VP↓VB");
		result1.add("headwordAndPhrasetype=decline|NP");
		result1.add("predicateAndHeadword=shore|decline");
		result1.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result2 = new ArrayList<>();
		result2.add("predicate=shore");
		result2.add("predicatepos=VB");
		result2.add("path=NP↑NP↑VP↓VB");
		result2.add("pathlength=4");
		result2.add("partialpath=NP↑NP↑VP");
		result2.add("phrasetype=NP");
		result2.add("position=after");
		result2.add("voice=a");	
		result2.add("headword=decline");
		result2.add("headwordpos=NN");
		result2.add("subcategorization=VP→VB PRT NP");
		result2.add("firstargument=a");
		result2.add("firstargumentpos=DT");
		result2.add("lastargument=decline");
		result2.add("lastargumentpos=NN");
		result2.add("positionAndvoice=after|a");
		result2.add("predicateAndpath=shore|NP↑NP↑VP↓VB");
		result2.add("pathAndpositionAndvoice=NP↑NP↑VP↓VB|after|a");
		result2.add("pathAndpositionAndvoiceAndpredicate=NP↑NP↑VP↓VB|after|a|shore");
		result2.add("headwordAndpredicateAndpath=decline|shore|NP↑NP↑VP↓VB");
		result2.add("headwordAndPhrasetype=decline|NP");
		result2.add("predicateAndHeadword=shore|decline");
		result2.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result27 = new ArrayList<>();
		result27.add("predicate=shore");
		result27.add("predicatepos=VB");
		result27.add("path=NP↑S↓VP↓NP↓VP↓VP↓VB");
		result27.add("pathlength=7");
		result27.add("partialpath=NP↑S");
		result27.add("phrasetype=NP");
		result27.add("position=before");
		result27.add("voice=a");	
		result27.add("headword=plan");
		result27.add("headwordpos=NN");
		result27.add("subcategorization=VP→VB PRT NP");
		result27.add("firstargument=the");
		result27.add("firstargumentpos=DT");
		result27.add("lastargument=plan");
		result27.add("lastargumentpos=NN");
		result27.add("positionAndvoice=before|a");
		result27.add("predicateAndpath=shore|NP↑S↓VP↓NP↓VP↓VP↓VB");
		result27.add("pathAndpositionAndvoice=NP↑S↓VP↓NP↓VP↓VP↓VB|before|a");
		result27.add("pathAndpositionAndvoiceAndpredicate=NP↑S↓VP↓NP↓VP↓VP↓VB|before|a|shore");
		result27.add("headwordAndpredicateAndpath=plan|shore|NP↑S↓VP↓NP↓VP↓VP↓VB");
		result27.add("headwordAndPhrasetype=plan|NP");
		result27.add("predicateAndHeadword=shore|plan");
		result27.add("predicateAndPhrasetype=shore|NP");
		
		List<String> result29 = new ArrayList<>();
		result29.add("predicate=shore");
		result29.add("predicatepos=VB");
		result29.add("path=NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB");
		result29.add("pathlength=9");
		result29.add("partialpath=NP↑S");
		result29.add("phrasetype=NP");
		result29.add("position=before");
		result29.add("voice=a");	
		result29.add("headword=Mr.");
		result29.add("headwordpos=NNP");
		result29.add("subcategorization=VP→VB PRT NP");
		result29.add("firstargument=Mr.");
		result29.add("firstargumentpos=NNP");
		result29.add("lastargument=Spoon");
		result29.add("lastargumentpos=NNP");
		result29.add("positionAndvoice=before|a");
		result29.add("predicateAndpath=shore|NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB");
		result29.add("pathAndpositionAndvoice=NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB|before|a");
		result29.add("pathAndpositionAndvoiceAndpredicate=NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB|before|a|shore");
		result29.add("headwordAndpredicateAndpath=Mr.|shore|NP↑S↓VP↓S↓VP↓NP↓VP↓VP↓VB");
		result29.add("headwordAndPhrasetype=Mr.|NP");
		result29.add("predicateAndHeadword=shore|Mr.");
		result29.add("predicateAndPhrasetype=shore|NP");
		
		List<Event> event1 = new ArrayList<Event>();
		List<Event> event2 = new ArrayList<Event>();
		List<Event> event27 = new ArrayList<Event>();
		List<Event> event29 = new ArrayList<Event>();
		event1.add(new Event("ARG1", result1.toArray(new String[result1.size()])));
		event2.add(new Event("NULL", result2.toArray(new String[result2.size()])));
		event27.add(new Event("ARG0", result27.toArray(new String[result27.size()])));
		event29.add(new Event("NULL", result29.toArray(new String[result29.size()])));
		
		HashSet<String> hs1 = new HashSet<>();
		for (int i = 0; i < labelinfo.length; i++) {
			hs1.add(labelinfo[i]);
		}
		HashSet<String> hs2 = new HashSet<>();
		hs2.add("ARG0");
		hs2.add("ARG1");
		hs2.add("NULL");
		
		assertEquals(hs1.toString(), hs2.toString());
		assertEquals(argumenttree. length,29);
		assertEquals(events.size(), 29);
		assertEquals(events.get(0).toString(), event1.get(0).toString());
		assertEquals(events.get(1).toString(), event2.get(0).toString());
		assertEquals(events.get(26).toString(), event27.get(0).toString());
		assertEquals(events.get(28).toString(), event29.get(0).toString());
	}
}
