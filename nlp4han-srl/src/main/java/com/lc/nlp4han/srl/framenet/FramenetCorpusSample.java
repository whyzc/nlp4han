package com.lc.nlp4han.srl.framenet;

import java.util.HashSet;

import com.lc.nlp4han.srl.tree.SemanticRoleStructure;

/**
 * 从Framnet中提取的样本表示
 * 
 * @author qyl
 *
 */
public class FramenetCorpusSample
{

	public FramenetCorpusSample()
	{

	}

	public FramenetCorpusSample(String sentence)
	{

	}

	/**
	 * 得到目标词/谓词
	 * 
	 * @return
	 */
	public String getTargetWord()
	{
		return null;
	}

	/**
	 * 得到框架元素组
	 * 
	 * @return
	 */
	public HashSet<String> getFEG()
	{
		return new HashSet<String>();
	}

	/**
	 * 得到框架元素结构数组（框架元素及该元素在句中开始和结束的位置）
	 * 
	 * @param FramenetElement
	 * @return
	 */
	public SemanticRoleStructure[] getFEStruecture()
	{
		return new SemanticRoleStructure[1];
	}
}
