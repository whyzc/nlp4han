package com.lc.nlp4han.srl.framenet;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;

import com.lc.nlp4han.srl.framenet.FramenetCorpusSample;
import com.lc.nlp4han.srl.tree.SemanticRoleStructure;

/**
 * 从Framenet中提取的句子，也就是封装的语料类
 * @author qyl
 *
 */
public class FramenetCorpusSampleTest
{
	private FramenetCorpusSample fs;
	
	@Before
	public void setUp() {
	   fs=new FramenetCorpusSample("[A Altitude sickness] is probably the most common CAUSE(Target) [B of failure to reach a summit]");
	}
	
	@Test 
      public void getTargetWordTest(){
    	  String tg=fs.getTargetWord();
		  assertEquals("cause", tg);  
      }
      
      @Test
      public void getFEGTest() {
    	
    	  HashSet<String> set=fs.getFEG();
    	  
    	  HashSet<String> set1=new HashSet<String>();
    	  set1.add("A");
    	  set1.add("B");
		  assertEquals(set,set1);
      }
      
      @Test
      public void getFEStruecture() {
    	  SemanticRoleStructure[] srs=new SemanticRoleStructure[2];
    	  srs[0]=new SemanticRoleStructure(0,1,"A");
    	  srs[1]=new SemanticRoleStructure(8,13,"B");
    	  
    	  SemanticRoleStructure[] srs1=fs.getFEStruecture();
    	  
    	  assertEquals(srs1,srs);
      }
}
