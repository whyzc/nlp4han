package com.lc.nlp4han.constituent.maxent;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;
import com.lc.nlp4han.ml.model.Event;


/**
 * 测试事件的生成
 * @author 王馨苇
 *
 */
public class ConstituentSampleEventTest {

	private List<Event> events ;
	private AbstractHeadGenerator aghw ;
	private ParserContextGenerator generator ;
	private TreeNode tree ;
	private HeadTreeNode headTree ;
	private ConstituentTreeSample sample ;
	private List<String> words ;
	private List<HeadTreeNode> chunkTree ;
	private List<List<HeadTreeNode>> buildAndCheckTree ;
	private List<String> actions ;
	
	@Before
	public void setUp() throws IOException, CloneNotSupportedException{
		events = new ArrayList<Event>();
		aghw = new HeadGeneratorCollins(new HeadRuleSetPTB());
		generator = new ParserContextGeneratorConf();
		tree = BracketExpUtil.generateTreeNoTopBracket("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		headTree = TreeToHeadTree.treeToHeadTree(tree, aghw);
		sample = HeadTreeToSample.headTreeToSample(headTree, aghw);
		words = sample.getWords();
		chunkTree = sample.getChunkTree();
		buildAndCheckTree = sample.getBuildAndCheckTree();
		actions = sample.getActions();
		
		//chunk
		for (int i = words.size(); i < 2*words.size(); i++) {		
			String[] context = generator.getContextForChunk(i - words.size(), chunkTree, actions, null);
		    events.add(new Event(actions.get(i), context));
		}
		
		//buildAndCheck 两个变量i j   i控制第几个list  j控制list中的第几个
		int j = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {
			String[] buildContext = generator.getContextForBuild(j,buildAndCheckTree.get(i - 2*words.size()), actions, null);
			events.add(new Event(actions.get(i), buildContext));
			if(actions.get(i+1).equals("yes")){
				int record = j-1;
				for (int k = record; k >= 0; k--) {
					if(buildAndCheckTree.get(i - 2*words.size()).get(k).getNodeNameLeftPart().equals("start")){
						j = k;
						break;
					}
				}
			}else if(actions.get(i+1).equals("no")){            	
				j++;
			}  
		}
		
		j = 0;
		for (int i = 2*words.size(); i < actions.size(); i=i+2) {    
			if(actions.get(i+1).equals("yes")){
				String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i + 1-2*words.size()), actions, null);
				int record = j-1;
				for (int k = record; k >= 0; k--) {		    
					if(buildAndCheckTree.get(i - 2*words.size()).get(k).getNodeNameLeftPart().equals("start")){			    
						j = k;
						break;
						}
				}
				events.add(new Event(actions.get(i+1), checkContext));
			}else if(actions.get(i+1).equals("no")){            	
				String[] checkContext = generator.getContextForCheck(j,buildAndCheckTree.get(i + 1-2*words.size()), actions, null);
				events.add(new Event(actions.get(i+1), checkContext));
				j++;
			}  
		}		
	}
	
	/**
	 * 对chunk步骤生成的事件进行测试
	 * @throws CloneNotSupportedException
	 * @throws IOException
	 */
	@Test
	public void testChunkEvent() throws CloneNotSupportedException, IOException{

		String[] chunk0 = new String[]{"chunkandpostag0=PRP|I","chunkandpostag0*=PRP","chunkandpostag1=VBD|saw","chunkandpostag1*=VBD","chunkandpostag2=DT|the",
				"chunkandpostag2*=DT","chunkandpostag01=PRP|I;VBD|saw","chunkandpostag01*=PRP|I;VBD","chunkandpostag0*1=PRP;VBD|saw","chunkandpostag0*1*=PRP;VBD"};
		String[] chunk1 = new String[]{"chunkandpostag0=VBD|saw","chunkandpostag0*=VBD","chunkandpostag_1=start_NP|PRP|I","chunkandpostag_1*=start_NP|PRP","chunkandpostag1=DT|the",
				"chunkandpostag1*=DT","chunkandpostag2=NN|man","chunkandpostag2*=NN","chunkandpostag_10=start_NP|PRP|I;VBD|saw","chunkandpostag_10*=start_NP|PRP|I;VBD",
				"chunkandpostag_1*0=start_NP|PRP;VBD|saw","chunkandpostag_1*0*=start_NP|PRP;VBD","chunkandpostag01=VBD|saw;DT|the",
				"chunkandpostag01*=VBD|saw;DT","chunkandpostag0*1=VBD;DT|the","chunkandpostag0*1*=VBD;DT"};
		String[] chunk2 = new String[]{"chunkandpostag0=DT|the","chunkandpostag0*=DT","chunkandpostag_1=other|VBD|saw","chunkandpostag_1*=other|VBD","chunkandpostag_2=start_NP|PRP|I",
				"chunkandpostag_2*=start_NP|PRP","chunkandpostag1=NN|man","chunkandpostag1*=NN","chunkandpostag2=IN|with","chunkandpostag2*=IN",
				"chunkandpostag_10=other|VBD|saw;DT|the","chunkandpostag_10*=other|VBD|saw;DT","chunkandpostag_1*0=other|VBD;DT|the","chunkandpostag_1*0*=other|VBD;DT",
				"chunkandpostag01=DT|the;NN|man","chunkandpostag01*=DT|the;NN","chunkandpostag0*1=DT;NN|man","chunkandpostag0*1*=DT;NN"};
		String[] chunk5 = new String[]{"chunkandpostag0=DT|the","chunkandpostag0*=DT","chunkandpostag_1=other|IN|with","chunkandpostag_1*=other|IN","chunkandpostag_2=join_NP|NN|man",
				"chunkandpostag_2*=join_NP|NN","chunkandpostag1=NN|telescope","chunkandpostag1*=NN","chunkandpostag_10=other|IN|with;DT|the","chunkandpostag_10*=other|IN|with;DT",
				"chunkandpostag_1*0=other|IN;DT|the","chunkandpostag_1*0*=other|IN;DT","chunkandpostag01=DT|the;NN|telescope",
				"chunkandpostag01*=DT|the;NN","chunkandpostag0*1=DT;NN|telescope","chunkandpostag0*1*=DT;NN"};
		String[] chunk4 = new String[]{"chunkandpostag0=IN|with","chunkandpostag0*=IN","chunkandpostag_1=join_NP|NN|man","chunkandpostag_1*=join_NP|NN","chunkandpostag_2=start_NP|DT|the",
				"chunkandpostag_2*=start_NP|DT","chunkandpostag1=DT|the","chunkandpostag1*=DT","chunkandpostag2=NN|telescope","chunkandpostag2*=NN",
				"chunkandpostag_10=join_NP|NN|man;IN|with","chunkandpostag_10*=join_NP|NN|man;IN","chunkandpostag_1*0=join_NP|NN;IN|with","chunkandpostag_1*0*=join_NP|NN;IN",
				"chunkandpostag01=IN|with;DT|the","chunkandpostag01*=IN|with;DT","chunkandpostag0*1=IN;DT|the","chunkandpostag0*1*=IN;DT"};
		String[] chunk3 = new String[]{"chunkandpostag0=NN|man","chunkandpostag0*=NN","chunkandpostag_1=start_NP|DT|the","chunkandpostag_1*=start_NP|DT",
				"chunkandpostag_2=other|VBD|saw","chunkandpostag_2*=other|VBD","chunkandpostag1=IN|with","chunkandpostag1*=IN",
				"chunkandpostag2=DT|the","chunkandpostag2*=DT",
				"chunkandpostag_10=start_NP|DT|the;NN|man","chunkandpostag_10*=start_NP|DT|the;NN","chunkandpostag_1*0=start_NP|DT;NN|man","chunkandpostag_1*0*=start_NP|DT;NN",
				"chunkandpostag01=NN|man;IN|with","chunkandpostag01*=NN|man;IN","chunkandpostag0*1=NN;IN|with","chunkandpostag0*1*=NN;IN"};
		String[] chunk6 = new String[]{"chunkandpostag0=NN|telescope","chunkandpostag0*=NN","chunkandpostag_1=start_NP|DT|the","chunkandpostag_1*=start_NP|DT","chunkandpostag_2=other|IN|with",
				"chunkandpostag_2*=other|IN","chunkandpostag_10=start_NP|DT|the;NN|telescope","chunkandpostag_10*=start_NP|DT|the;NN","chunkandpostag_1*0=start_NP|DT;NN|telescope","chunkandpostag_1*0*=start_NP|DT;NN"};
				
		List<Event> event1 = new ArrayList<>();
		List<Event> event2 = new ArrayList<>();
		List<Event> event3 = new ArrayList<>();
		List<Event> event4 = new ArrayList<>();
		List<Event> event5 = new ArrayList<>();
		List<Event> event6 = new ArrayList<>();
		List<Event> event7 = new ArrayList<>();
		
		event1.add(new Event("start_NP",chunk0));
		event2.add(new Event("other",chunk1));
		event3.add(new Event("start_NP",chunk2));
		event4.add(new Event("join_NP",chunk3));
		event5.add(new Event("other",chunk4));
		event6.add(new Event("start_NP",chunk5));
		event7.add(new Event("join_NP",chunk6));
				
		assertEquals(events.get(0).toString(),event1.get(0).toString());
		assertEquals(events.get(1).toString(),event2.get(0).toString());
		assertEquals(events.get(2).toString(),event3.get(0).toString());
		assertEquals(events.get(3).toString(),event4.get(0).toString());
		assertEquals(events.get(4).toString(),event5.get(0).toString());
		assertEquals(events.get(5).toString(),event6.get(0).toString());
		assertEquals(events.get(6).toString(),event7.get(0).toString());
	}
	
	/**
	 * 对build步骤生成的事件进行测试
	 */
	@Test
	public void testBuildEvent(){

		String[] build0 = new String[]{"cons0=NP|I","cons0*=NP","cons1=VBD|saw","cons1*=VBD","cons2=NP|man","cons2*=NP",
				"cons01=NP|I;VBD|saw","cons01*=NP|I;VBD","cons0*1=NP;VBD|saw","cons0*1*=NP;VBD",
				"cons012=NP|I;VBD|saw;NP|man","cons01*2*=NP|I;VBD;NP","cons01*2=NP|I;VBD;NP|man","cons012*=NP|I;VBD|saw;NP","cons0*1*2*=NP;VBD;NP"};
		String[] build1 = new String[]{"cons0=VBD|saw","cons0*=VBD","cons_1=start_S|NP|I","cons_1*=start_S|NP","cons1=NP|man","cons1*=NP",
				"cons2=IN|with","cons2*=IN","cons_10=start_S|NP|I;VBD|saw","cons_10*=start_S|NP|I;VBD","cons_1*0=start_S|NP;VBD|saw","cons_1*0*=start_S|NP;VBD",
				"cons01=VBD|saw;NP|man","cons01*=VBD|saw;NP","cons0*1=VBD;NP|man","cons0*1*=VBD;NP",
				"cons012=VBD|saw;NP|man;IN|with","cons01*2*=VBD|saw;NP;IN","cons01*2=VBD|saw;NP;IN|with","cons012*=VBD|saw;NP|man;IN","cons0*1*2*=VBD;NP;IN",
				"cons_101=start_S|NP|I;VBD|saw;NP|man","cons_1*01*=start_S|NP;VBD|saw;NP","cons_1*01=start_S|NP;VBD|saw;NP|man","cons_101*=start_S|NP|I;VBD|saw;NP","cons_1*0*1*=start_S|NP;VBD;NP"};
		String[] build2 = new String[]{"cons0=NP|man","cons0*=NP","cons_1=start_VP|VBD|saw","cons_1*=start_VP|VBD","cons_2=start_S|NP|I","cons_2*=start_S|NP",
				"cons1=IN|with","cons1*=IN","cons2=NP|telescope","cons2*=NP",
				"cons_10=start_VP|VBD|saw;NP|man","cons_10*=start_VP|VBD|saw;NP","cons_1*0=start_VP|VBD;NP|man","cons_1*0*=start_VP|VBD;NP",
				"cons01=NP|man;IN|with","cons01*=NP|man;IN","cons0*1=NP;IN|with","cons0*1*=NP;IN",
				"cons_2_10=start_S|NP|I;start_VP|VBD|saw;NP|man","cons_2*_1*0=start_S|NP;start_VP|VBD;NP|man","cons_2*_10=start_S|NP;start_VP|VBD|saw;NP|man","cons_2_1*0=start_S|NP|I;start_VP|VBD;NP|man","cons_2*_1*0*=start_S|NP;start_VP|VBD;NP",
				"cons012=NP|man;IN|with;NP|telescope","cons01*2*=NP|man;IN;NP","cons01*2=NP|man;IN;NP|telescope","cons012*=NP|man;IN|with;NP","cons0*1*2*=NP;IN;NP",
				"cons_101=start_VP|VBD|saw;NP|man;IN|with","cons_1*01*=start_VP|VBD;NP|man;IN","cons_1*01=start_VP|VBD;NP|man;IN|with","cons_101*=start_VP|VBD|saw;NP|man;IN","cons_1*0*1*=start_VP|VBD;NP;IN"};
		String[] build3 = new String[]{"cons0=VP|saw","cons0*=VP","cons_1=start_S|NP|I","cons_1*=start_S|NP","cons1=IN|with","cons1*=IN",
				"cons2=NP|telescope","cons2*=NP","cons_10=start_S|NP|I;VP|saw","cons_10*=start_S|NP|I;VP","cons_1*0=start_S|NP;VP|saw","cons_1*0*=start_S|NP;VP",
				"cons01=VP|saw;IN|with","cons01*=VP|saw;IN","cons0*1=VP;IN|with","cons0*1*=VP;IN",
				"cons012=VP|saw;IN|with;NP|telescope","cons01*2*=VP|saw;IN;NP","cons01*2=VP|saw;IN;NP|telescope","cons012*=VP|saw;IN|with;NP","cons0*1*2*=VP;IN;NP",
				"cons_101=start_S|NP|I;VP|saw;IN|with","cons_1*01*=start_S|NP;VP|saw;IN","cons_1*01=start_S|NP;VP|saw;IN|with","cons_101*=start_S|NP|I;VP|saw;IN","cons_1*0*1*=start_S|NP;VP;IN"};
		String[] build4 = new String[]{"cons0=IN|with","cons0*=IN","cons_1=start_VP|VP|saw","cons_1*=start_VP|VP","cons_2=start_S|NP|I","cons_2*=start_S|NP",
				"cons1=NP|telescope","cons1*=NP","cons_10=start_VP|VP|saw;IN|with","cons_10*=start_VP|VP|saw;IN","cons_1*0=start_VP|VP;IN|with","cons_1*0*=start_VP|VP;IN",
				"cons01=IN|with;NP|telescope","cons01*=IN|with;NP","cons0*1=IN;NP|telescope","cons0*1*=IN;NP",
				"cons_2_10=start_S|NP|I;start_VP|VP|saw;IN|with","cons_2*_1*0=start_S|NP;start_VP|VP;IN|with","cons_2*_10=start_S|NP;start_VP|VP|saw;IN|with","cons_2_1*0=start_S|NP|I;start_VP|VP;IN|with","cons_2*_1*0*=start_S|NP;start_VP|VP;IN",
				"cons_101=start_VP|VP|saw;IN|with;NP|telescope","cons_1*01*=start_VP|VP;IN|with;NP","cons_1*01=start_VP|VP;IN|with;NP|telescope","cons_101*=start_VP|VP|saw;IN|with;NP","cons_1*0*1*=start_VP|VP;IN;NP"};
		String[] build5 = new String[]{"cons0=NP|telescope","cons0*=NP","cons_1=start_PP|IN|with","cons_1*=start_PP|IN","cons_2=start_VP|VP|saw","cons_2*=start_VP|VP",
				"cons_10=start_PP|IN|with;NP|telescope","cons_10*=start_PP|IN|with;NP","cons_1*0=start_PP|IN;NP|telescope","cons_1*0*=start_PP|IN;NP","cons_2_10=start_VP|VP|saw;start_PP|IN|with;NP|telescope",
				"cons_2*_1*0=start_VP|VP;start_PP|IN;NP|telescope","cons_2*_10=start_VP|VP;start_PP|IN|with;NP|telescope","cons_2_1*0=start_VP|VP|saw;start_PP|IN;NP|telescope","cons_2*_1*0*=start_VP|VP;start_PP|IN;NP"};
		String[] build6 = new String[]{"cons0=PP|with","cons0*=PP","cons_1=start_VP|VP|saw","cons_1*=start_VP|VP","cons_2=start_S|NP|I","cons_2*=start_S|NP",
				"cons_10=start_VP|VP|saw;PP|with","cons_10*=start_VP|VP|saw;PP","cons_1*0=start_VP|VP;PP|with","cons_1*0*=start_VP|VP;PP",
				"cons_2_10=start_S|NP|I;start_VP|VP|saw;PP|with","cons_2*_1*0=start_S|NP;start_VP|VP;PP|with","cons_2*_10=start_S|NP;start_VP|VP|saw;PP|with","cons_2_1*0=start_S|NP|I;start_VP|VP;PP|with","cons_2*_1*0*=start_S|NP;start_VP|VP;PP"};
		String[] build7 = new String[]{"cons0=VP|saw","cons0*=VP","cons_1=start_S|NP|I","cons_1*=start_S|NP","cons_10=start_S|NP|I;VP|saw",
				"cons_10*=start_S|NP|I;VP","cons_1*0=start_S|NP;VP|saw","cons_1*0*=start_S|NP;VP"};
		
		List<Event> event8 = new ArrayList<>();
		List<Event> event9 = new ArrayList<>();
		List<Event> event10 = new ArrayList<>();
		List<Event> event11 = new ArrayList<>();
		List<Event> event12 = new ArrayList<>();
		List<Event> event13 = new ArrayList<>();
		List<Event> event14 = new ArrayList<>();
		List<Event> event15 = new ArrayList<>();
				
		event8.add(new Event("start_S",build0));
		event9.add(new Event("start_VP",build1));
		event10.add(new Event("join_VP",build2));
		event11.add(new Event("start_VP",build3));
		event12.add(new Event("start_PP",build4));
		event13.add(new Event("join_PP",build5));
		event14.add(new Event("join_VP",build6));
		event15.add(new Event("join_S",build7));
				
		assertEquals(events.get(7).toString(),event8.get(0).toString());
		assertEquals(events.get(8).toString(),event9.get(0).toString());
		assertEquals(events.get(9).toString(),event10.get(0).toString());
		assertEquals(events.get(10).toString(),event11.get(0).toString());
		assertEquals(events.get(11).toString(),event12.get(0).toString());
		assertEquals(events.get(12).toString(),event13.get(0).toString());
		assertEquals(events.get(13).toString(),event14.get(0).toString());
		assertEquals(events.get(14).toString(),event15.get(0).toString());
	}
	
	/**
	 * 对check步骤生成的事件进行测试
	 */
	@Test
	public void testCheckEvent(){

		String[] check0 = new String[]{"checkcons_begin=S|NP|I","checkcons_begin*=S|NP","checkcons_last=S|NP|I","checkcons_last*=S|NP",
				"production=S→NP","surround1=VBD|saw","surround1*=VBD","surround2=DT|the;NN|man","surround2*=DT;NN"};
		String[] check1 = new String[]{"checkcons_begin=VP|VBD|saw","checkcons_begin*=VP|VBD","checkcons_last=VP|VBD|saw","checkcons_last*=VP|VBD","production=VP→VBD",
				"surround_1=PRP|I","surround_1*=PRP","surround1=DT|the;NN|man","surround1*=DT;NN","surround2=IN|with","surround2*=IN"};
		String[] check2 = new String[]{"checkcons_begin=VP|VBD|saw","checkcons_begin*=VP|VBD","checkcons_last=VP|NP|man","checkcons_last*=VP|NP","checkcons_0last=VP|VBD|saw;VP|NP|man",
				"production=VP→VBD,NP","surround_1=PRP|I","surround_1*=PRP","surround1=IN|with","surround1*=IN",
				"surround2=DT|the;NN|telescope","surround2*=DT;NN"};
		String[] check3 = new String[]{"checkcons_begin=VP|VP|saw","checkcons_begin*=VP|VP","checkcons_last=VP|VP|saw","checkcons_last*=VP|VP","production=VP→VP",
				"surround_1=PRP|I","surround_1*=PRP","surround1=IN|with","surround1*=IN","surround2=DT|the;NN|telescope","surround2*=DT;NN"};
		String[] check4 = new String[]{"checkcons_begin=PP|IN|with","checkcons_begin*=PP|IN","checkcons_last=PP|IN|with","checkcons_last*=PP|IN","production=PP→IN",
				"surround_1=VBD|saw;DT|the;NN|man","surround_1*=VBD;DT;NN","surround_2=PRP|I","surround_2*=PRP","surround1=DT|the;NN|telescope","surround1*=DT;NN"};
		String[] check5 = new String[]{"checkcons_begin=PP|IN|with","checkcons_begin*=PP|IN","checkcons_last=PP|NP|telescope","checkcons_last*=PP|NP",
				"checkcons_0last=PP|IN|with;PP|NP|telescope","production=PP→IN,NP","surround_1=VBD|saw;DT|the;NN|man","surround_1*=VBD;DT;NN",
				"surround_2=PRP|I","surround_2*=PRP"};
		String[] check6 = new String[]{"checkcons_begin=VP|VP|saw","checkcons_begin*=VP|VP","checkcons_last=VP|PP|with","checkcons_last*=VP|PP",
				"checkcons_0last=VP|VP|saw;VP|PP|with","production=VP→VP,PP","surround_1=PRP|I","surround_1*=PRP"};
		String[] check7 = new String[]{"checkcons_begin=S|NP|I","checkcons_begin*=S|NP","checkcons_last=S|VP|saw","checkcons_last*=S|VP","checkcons_0last=S|NP|I;S|VP|saw",
				"production=S→NP,VP"};
		
		List<Event> event16 = new ArrayList<>();
		List<Event> event17 = new ArrayList<>();
		List<Event> event18 = new ArrayList<>();
		List<Event> event19 = new ArrayList<>();
		List<Event> event20 = new ArrayList<>();
		List<Event> event21 = new ArrayList<>();
		List<Event> event22 = new ArrayList<>();
		List<Event> event23 = new ArrayList<>();
		
		event16.add(new Event("no",check0));
		event17.add(new Event("no",check1));
		event18.add(new Event("yes",check2));
		event19.add(new Event("no",check3));
		event20.add(new Event("no",check4));
		event21.add(new Event("yes",check5));
		event22.add(new Event("yes",check6));
		event23.add(new Event("yes",check7));

		assertEquals(events.get(16).toString(),event17.get(0).toString());
		assertEquals(events.get(17).toString(),event18.get(0).toString());
		assertEquals(events.get(18).toString(),event19.get(0).toString());
		assertEquals(events.get(19).toString(),event20.get(0).toString());
		assertEquals(events.get(20).toString(),event21.get(0).toString());
		assertEquals(events.get(21).toString(),event22.get(0).toString());
		assertEquals(events.get(22).toString(),event23.get(0).toString());
	}
}
