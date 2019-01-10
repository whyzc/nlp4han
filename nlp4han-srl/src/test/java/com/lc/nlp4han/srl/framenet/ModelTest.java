package com.lc.nlp4han.srl.framenet;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.lc.nlp4han.srl.framenet.Event4Classify;
import com.lc.nlp4han.srl.framenet.Event4Recognize;
import com.lc.nlp4han.srl.framenet.SRLModel;

/**
 * 模型测试类
 * 识别和分类过程中只取一个role进行识别和分类，框架元素组的测测同样只取一个
 * @author qyl
 *
 */
public class ModelTest
{
	private SRLModel model;
	
	//特征
	//中心词
	private String headWord;
	//voice,此处特质主动/被动
	private boolean active;
	//位置，此处记录该角色在谓词的前后位置
	private boolean beforePred;
	//gov,只有两个值：S/VP,而且被限制于NP使用
	private boolean govVP;
	//Phrase Type,短语类型
	private String phraseType;
	//目标词/谓词
	private Predicate predicate;
	//path,在句法解析树中，谓词到role的路径
	private ArrayList<String> path;	
	
	//语义角色/框架元素
	private String role;
	
	//Frame Element Group,框架元素组
	private ArrayList<String> list;
	
	@Before
	public void setUp() {
		
		String[] sentences=new String[5];
		sentences[0]="[A I] have been COMMUNICATING(Target)  [B with the Minister] [C since 1988] [D on that problem]";

		ArrayList<String> list=new ArrayList<String>();
        for(String sentence:sentences) {
        	list.add(sentence);
        }
        
//		model=new ModelExtractor().generateModel(list);
		
		
		headWord=" ";
		
		active=false;
		
		beforePred=false;
		
		govVP=false;
		
		phraseType=" ";
		
		predicate=new Predicate();
				
		path=new ArrayList<>();
				
		role=" ";
		
		list=new ArrayList<>();
	}
	
	@Test
	public void getPro4RecognitionTest() {	
		
		double expectedPro=0.0;
		
		//识别所需特征
		Event4Recognize er=new Event4Recognize(headWord,path,predicate, 0.0);
        
		//获取概率
		double pro1=model.getPro4Recognize(er);
		
		assertEquals(expectedPro, pro1);
	}

	@Test
	public void getPro4FEGPriorTest() {	
		
		double expectedPro=0.0;
		
		//先验概率事件
		Event4PriorFEG ep=new Event4PriorFEG(list,predicate);
		
		//获取概率
		double pro1=model.getPro4FEGPrior(ep);
		
		assertEquals(expectedPro, pro1);
	}

	@Test
	public void getPro4ClassificationTest() {	
		
		double expectedPro=0.0;
		
		//分类所需特征
		Event4Classify ec=new Event4Classify(headWord, active, beforePred, govVP, phraseType,
				predicate, role,0.0);
		
		//获取概率
		double pro1=model.getPro4Classify(ec);
		
		assertEquals(expectedPro, pro1);
	}
	
	/**
	 * 目标词为条件获取role的概率的测试
	 */
	@Test
	public void getPro4PredicateTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getPro4Predicate(role, predicate);
		
		assertEquals(expectedPro, pro1);
	}

	/**
	 * 以短语类型pt，控制类型gov,目标词为条件获取role的概率测试
	 */
	@Test
	public void getPro4PtGovPredicateTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getPro4PtGovPredicate(phraseType, govVP, predicate);
		
		assertEquals(expectedPro, pro1);
	}

	/**
	 * 以短语类型pt，位置position,噪声voice为条件获取role的概率测试
	 */
	@Test
	public void getPro4PtPositionVoiceTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getPro4PtPositionVoice(role, phraseType, beforePred, active);
		
		assertEquals(expectedPro, pro1);
	}
	
	/**
	 * 以短语类型pt，位置position,噪声voice,目标词predicate为条件获取role的概率测试
	 */
	@Test
	public void getPro4PtPositionVoicePreTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getPro4getPro4PtPositionVoicePre(role, phraseType, beforePred, active, predicate);
		
		assertEquals(expectedPro, pro1);
	}
	
	/**
	 * 以中心词为条件获取role的概率测试
	 */
	@Test
	public void getPro4HeadTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getPro4Head(role, headWord);
		
		assertEquals(expectedPro, pro1);
	}
	
	/**
	 * 以中心词,目标词为条件获取role的概率测试
	 */
	@Test
	public void getPro4HeadPreTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getPro4HeadPre(role, headWord, predicate);
		
		assertEquals(expectedPro, pro1);
	}
	
	/**
	 * 以中心词,目标词,短语类型为条件获取role的概率测试
	 */
	@Test
	public void getPro4HeadPrePtTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getPro4HeadPrePt(role, headWord, predicate, phraseType);
		
		assertEquals(expectedPro, pro1);
	}

	
	/**
	 * 以路径为条件，获取在识别过程中的概率测试
	 */
	@Test
	public void getFePro4PathTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getFePro4Path(path);
		
		assertEquals(expectedPro, pro1);
	}
	
	/**
	 * 以路径和谓词为条件，获取在识别过程中的概率测试
	 */
	@Test
	public void getFePro4PathPreTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getFePro4PathPre(path, predicate);
		
		assertEquals(expectedPro, pro1);
	}
	
	/**
	 * 以头节点和谓词为条件，获取在识别过程中的概率测试
	 */
	@Test
	public void getFePro4HeadPreTest() {
		//参照概率
		double expectedPro=0.0;
		
		//预测概率
		double pro1=model.getFePro4HeadPre(headWord, predicate);
		
		assertEquals(expectedPro, pro1);
	}
}
