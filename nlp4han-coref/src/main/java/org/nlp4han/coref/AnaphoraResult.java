package org.nlp4han.coref;

import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeNodeUtil;

/**
 * 指代消解的结果
 * 
 * 包括代词和先行词信息
 *
 */
public class AnaphoraResult
{
	private TreeNode pronNode;
	private TreeNode antecedentNode;

	private String pronString;
	private String antecedentString;
	
	public AnaphoraResult(TreeNode pronNode, TreeNode antecedentNode)
	{
		this.pronNode = pronNode;
		this.antecedentNode = antecedentNode;
		
		this.pronString = TreeNodeUtil.getetWordString(pronNode);
		this.antecedentString = TreeNodeUtil.getetWordString(antecedentNode);
	}

	@Override
	public String toString()
	{
		return pronString + "->" + antecedentString;
	}

	public TreeNode getPronNode()
	{
		return pronNode;
	}

	public void setPronNode(TreeNode pronNode)
	{
		this.pronNode = pronNode;
	}

	public TreeNode getAntecedentNode()
	{
		return antecedentNode;
	}

	public void setAntecedentNode(TreeNode antecedentNode)
	{
		this.antecedentNode = antecedentNode;
	}

	public String getPronString()
	{
		return pronString;
	}

	public void setPronString(String pronString)
	{
		this.pronString = pronString;
	}

	public String getAntecedentString()
	{
		return antecedentString;
	}

	public void setAntecedentString(String antecedentString)
	{
		this.antecedentString = antecedentString;
	}

}
