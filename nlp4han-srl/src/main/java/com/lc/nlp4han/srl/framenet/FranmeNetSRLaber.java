package com.lc.nlp4han.srl.framenet;

/**
 * 基于FrameNet训练的语义角色标注器
 * @author qyl
 *
 */

import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.srl.tree.SRLTree;
import com.lc.nlp4han.srl.tree.SemanticRoleLabeler;

public class FranmeNetSRLaber implements SemanticRoleLabeler
{
   private SRLModel model;
    
	@Override
	public SRLTree srltree(TreeNode tree, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRLTree srltree(String treeStr, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRLTree[] kSrltree(int k, TreeNode tree, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRLTree[] kSrltree(int k, String treeStr, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String srlstr(TreeNode tree, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String srlstr(String treeStr, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] kSrlstr(int k, TreeNode tree, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] kSrlstr(int k, String treeStr, String[] predicateinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRLTree srltree(TreeNode tree, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRLTree srltree(String treeStr, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRLTree[] kSrltree(int k, TreeNode tree, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SRLTree[] kSrltree(int k, String treeStr, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String srlstr(TreeNode tree, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String srlstr(String treeStr, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] kSrlstr(int k, TreeNode tree, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] kSrlstr(int k, String treeStr, int[] predicateindexinfo)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
