package com.lc.nlp4han.srl.framenet;

import org.junit.Before;
import org.junit.Test;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.srl.framenet.Predicate;
import com.lc.nlp4han.srl.framenet.PreprocessUtil;

/**
 * 语义角色标注器测试
 * @author qyl
 *
 */
public class FrameNetSRLaberTest
{
   private String sentence;
   private Predicate predicate;
   private HeadTreeNode headtree;
   
   @Before
   public void setUp() {
	   sentence="Fowler had been told not to press the old man , but parentage of Cissie 's baby had been the CAUSE of much of the gossip";
       
	   //得到谓词
	   Predicate predicate=PreprocessUtil.getPredicate(sentence);
	   //得到headTree
	   headtree=new HeadTreeNode(null);
   }
   
   @Test
   public void ClassificationTest() {
	   //单个role的分类测试
   }
   
   @Test
   public void RecognitionTest() {
	   //单个role的识别测试
   }
   
   @Test
   public void entiretyTest() {
	   //整个句子的整体测试
   }
}
