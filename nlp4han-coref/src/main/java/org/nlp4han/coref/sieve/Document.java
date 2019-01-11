package org.nlp4han.coref.sieve;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

public class Document
{
	private String content;
	
	private List<TreeNode> trees;
	
	private List<CorefCluster> corefClusters;
	private List<List<Mention>> mentionsBySentences;
	private List<List<Entity>> entitiesBySentences;
	
	public Document()
	{
		
	}
	
	public Document(List<TreeNode> trees)
	{
		this.trees = trees;
	}
	
	public Document(String content)
	{
		// TODO Auto-generated constructor stub
	}

	public List<List<Mention>> getMentions()
	{
		// TODO Auto-generated method stub
		return mentionsBySentences;
	}
	
}
