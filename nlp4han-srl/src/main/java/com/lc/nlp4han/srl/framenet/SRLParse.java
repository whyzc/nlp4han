package com.lc.nlp4han.srl.framenet;

import com.lc.nlp4han.srl.tree.SRLSample;

public interface SRLParse
{
	      /**
	       *角色标注过程进行剪枝
	       */
          public void  prune();
          
          /**
           * 语义角色识别
           */
          public void RoleRecognition();
          
          /**
           * 语义角色分类
           */
          public void RoleClassification();
          
          /**
           * 根据单个语义角色识别和分类的计算整体的最优解
           */
          public SRLSample[] EntiretyParse();
}
