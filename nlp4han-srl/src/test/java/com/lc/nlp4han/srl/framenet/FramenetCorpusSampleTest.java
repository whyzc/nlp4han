package com.lc.nlp4han.srl.framenet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.lc.nlp4han.srl.framenet.FramenetCorpusSample;
import com.lc.nlp4han.srl.propbank.SemanticRoleStructure;

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
		
		  Predicate expectedPre=new Predicate();
		  
    	  Predicate predicate=fs.getPredicate();
    	  
		  assertEquals(expectedPre, predicate);  
      }
      
      @Test
      public void getFEGTest() {
    	
    	  HashSet<String> expectedSet=new HashSet<String>();
    	  expectedSet.add("A");
    	  expectedSet.add("B");
    	  
    	  HashSet<String> set=fs.getFEG();
    	  
		  assertEquals(expectedSet, set);
      }
      
      @Test
      public void getNoRoleSentenceTest() {
             String expectedSent=" ";
             
             String sentence=fs.getNoRoleSentence();
             
       	    assertEquals(expectedSent,sentence);              
      }
	@Test
      public void getFEStruecture() {
    	  SemanticRoleStructure[] expectedSrs=new SemanticRoleStructure[2];
    	  expectedSrs[0]=new SemanticRoleStructure(0,1,"A");
    	  expectedSrs[1]=new SemanticRoleStructure(8,13,"B");
    	  
    	  SemanticRoleStructure[] srs1=fs.getFEStruecture();
    	  
    	  assertArrayEquals(expectedSrs,srs1);
      }
}
