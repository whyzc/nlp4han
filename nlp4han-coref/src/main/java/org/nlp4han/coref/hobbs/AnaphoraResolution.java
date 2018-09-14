package org.nlp4han.coref.hobbs;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 代词消解接口
 * 
 * @author 杨智超
 *
 */
public interface AnaphoraResolution
{
	/**
	 * 代词消解
	 * @param sentences 需要进行消解的句子
	 * @return 返回代词消解的结果，字符串形式
	 */
	public List<String> anaph(List<TreeNode> sentences);
}
