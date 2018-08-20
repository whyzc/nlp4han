package org.nlp4han.coref.centering;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;

public class Entity
{
	private String entity;
	private String GrammaticalRole;
	private int site;

	public Entity(TreeNode node)
	{

	}

	public String getEntity()
	{
		return entity;
	}

	public void setEntity(String entity)
	{
		this.entity = entity;
	}

	public String getGrammaticalRole()
	{
		return GrammaticalRole;
	}

	public void setGrammaticalRole(String grammaticalRole)
	{
		GrammaticalRole = grammaticalRole;
	}

	public int getSite()
	{
		return site;
	}

	public void setSite(int site)
	{
		this.site = site;
	}

	/**
	 * 根据字符串生成其所有实体对象
	 * @param sentence
	 * @return
	 */
	public static List<Entity> entitys(String sentence)
	{
		return null;
	}

	/**
	 * 根据结构树生成其所有实体对象
	 * @param tree
	 * @return
	 */
	public static List<Entity> entitys(TreeNode tree)
	{
		return null;
	}
}
