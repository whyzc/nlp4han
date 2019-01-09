package com.lc.nlp4han.srl.framenet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.lc.nlp4han.srl.framenet.Event4Classification;
import com.lc.nlp4han.srl.framenet.Event4Recognition;
import com.lc.nlp4han.srl.framenet.ModelExtractor;
import com.lc.nlp4han.srl.framenet.SRLModel;

public class ModelTest
{
	private SRLModel model;
	@Before
	public void setUp() {
		ModelExtractor modelExtractor=new ModelExtractor();
		ArrayList<String> list=new ArrayList<String>();
		
		String sentence="[A I] have been COMMUNICATING(Target)  [B with the Minister] [C since 1988] [D on that problem]";
	    list.add(sentence);
	    
		model=modelExtractor.generateModel(list);
	}
	
	@Test
	public void getPro4RecognitionTest() {	
		
		double referencePro=0.0;
		//识别所需特征
		Event4Recognition er=new Event4Recognition();
		er.setHeadWord(null);
		er.setPath(null);
		er.setPredicate(null);
        er.setRole(false);		
        
		//获取概率
		double pro1=model.getPro4Recognition();
		
		assertEquals(pro1,referencePro);
	}

	@Test
	public void getPro4FEGPriorTest() {	
		
		double referencePro=0.0;
		
		//获取概率
		double pro1=model.getPro4FEGPrior();
		
		assertEquals(pro1,referencePro);
	}

	@Test
	public void getPro4ClassificationTest() {	
		
		double referencePro=0.0;
		//识别所需特征
		Event4Classification ec=new Event4Classification(null ,false,false,false,null,null,null,0.0);
		//获取概率
		double pro1=model.getPro4Classification();
		
		assertEquals(pro1,referencePro);
	}
	
	/**
	 * 目标词为条件获取role的概率的测试
	 */
	@Test
	public void getPro4PredicateTest() {
		
	}

	/**
	 * 以短语类型pt，控制类型gov,目标词为条件获取role的概率测试
	 */
	@Test
	public void getPro4PtGovPredicateTest() {
		
	}

	/**
	 * 以短语类型pt，位置position,噪声voice为条件获取role的概率测试
	 */
	@Test
	public void getPro4PtPositionVoiceTest() {
		
	}
	
	/**
	 * 以短语类型pt，位置position,噪声voice,目标词predicate为条件获取role的概率测试
	 */
	@Test
	public void getPro4getPro4PtPositionVoicePreTest() {
		
	}
	
	/**
	 * 以中心词为条件获取role的概率测试
	 */
	@Test
	public void getPro4HeadTest() {
		
	}
	
	/**
	 * 以中心词,目标词为条件获取role的概率测试
	 */
	@Test
	public void getPro4HeadPreTest() {
		
	}
	
	/**
	 * 以中心词,目标词,短语类型为条件获取role的概率测试
	 */
	@Test
	public void getPro4HeadPrePtTest() {
		
	}

	
	/**
	 * 以路径为条件获，获取在识别过程中的概率测试
	 */
	@Test
	public void getFePro4PathTest() {
		
	}
	
	/**
	 * 以路径和谓词为条件，获取在识别过程中的概率测试
	 */
	@Test
	public void getFePro4PathPreTest() {
		
	}
	
	/**
	 * 以头节点和谓词为条件，获取在识别过程中的概率测试
	 */
	@Test
	public void getFePro4HeadPreTest() {
		
	}
}
