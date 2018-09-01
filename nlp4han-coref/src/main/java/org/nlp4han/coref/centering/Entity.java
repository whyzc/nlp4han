package org.nlp4han.coref.centering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nlp4han.coref.hobbs.NPHeadRuleSetPTB;
import org.nlp4han.coref.hobbs.TreeNodeUtil;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

public class Entity
{
	private String entityName;
	private String grammaticalRole;
	private int site;
	private static String[] grammaticalRolePriority = { "SBJ", "OBJ", "IO" };

	public Entity()
	{

	}
	
	public Entity(String entityName, String grammaticalRole, int site)
	{
		this.entityName = entityName;
		this.grammaticalRole = grammaticalRole;
		this.site = site;
	}

	public Entity(TreeNode node, String grammaticalRole)
	{
		if (node == null)
		{
			throw new RuntimeException("参数错误");
		}
		this.entityName = TreeNodeUtil.getString(node);

		List<TreeNode> leaves = TreeNodeUtil.getAllLeafNodes(TreeNodeUtil.getRootNode(node));
		TreeNode leaf = TreeNodeUtil.getAllLeafNodes(node).get(0);
		this.site = leaves.indexOf(leaf);

		this.grammaticalRole = grammaticalRole;

	}

	public String getEntityName()
	{
		return entityName;
	}

	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	public String getGrammaticalRole()
	{
		return grammaticalRole;
	}

	public void setGrammaticalRole(String grammaticalRole)
	{
		this.grammaticalRole = grammaticalRole;
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
	 * 
	 * @param sentence
	 * @return
	 */
	public static List<Entity> entities(String sentence)
	{
		// TODO
		return null;
	}

	/**
	 * 根据结构树生成其所有实体对象
	 * 
	 * @param root
	 * @return
	 */
	public static List<Entity> entities(TreeNode root)
	{
		if (root == null)
			throw new RuntimeException("输入错误");
		List<Entity> result = new ArrayList<Entity>();
		Map<String, List<Entity>> maps = Entity.generateEntitiesAndGrammaticalRole(root, GrammaticalRoleRuleSet.getGrammaticalRoleRuleSet());
		Set<String> keys = maps.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			result.addAll(maps.get(key));
		}
		return sort(result);
	}

	public static List<Entity> sort(List<Entity> entities)
	{
		if (entities != null && entities.size() > 0)
		{
			List<Entity> result = new ArrayList<Entity>();
			for (int i = 0; i < grammaticalRolePriority.length; i++)
			{
				for (int j = 0; j < entities.size(); j++)
				{
					if (grammaticalRolePriority[i].equals(entities.get(j).getGrammaticalRole()))
						result.add(entities.get(j));
				}
			}
			if (result.size() != entities.size())
				throw new RuntimeException("结果错误！");
			return result;
		}
		return null;
	}

	@Override
	public String toString()
	{
		return entityName;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
		result = prime * result + ((grammaticalRole == null) ? 0 : grammaticalRole.hashCode());
		result = prime * result + site;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (entityName == null)
		{
			if (other.entityName != null)
				return false;
		}
		else if (!entityName.equals(other.entityName))
			return false;
		if (grammaticalRole == null)
		{
			if (other.grammaticalRole != null)
				return false;
		}
		else if (!grammaticalRole.equals(other.grammaticalRole))
			return false;
		if (site != other.site)
			return false;
		return true;
	}

	public static Map<String, List<Entity>> generateEntitiesAndGrammaticalRole(TreeNode tree,
			HashMap<String, List<String>> grammaticalRoleRuleSet)
	{
		Map<String, List<Entity>> result = new HashMap<String, List<Entity>>();
		List<String> SBJRules = grammaticalRoleRuleSet.get("SBJ");
		if (SBJRules != null)
		{
			for (String rule : SBJRules)
			{
				List<TreeNode> nodes = parseOneRule(tree, rule);
				List<Entity> entities = getEntities(nodes, "SBJ");
				if (entities != null && !entities.isEmpty() && !result.containsKey("SBJ"))
				{
					result.put("SBJ", entities);
				}
				else if (entities != null && !entities.isEmpty() && result.containsKey("SBJ"))
				{
					List<Entity> e = result.get("SBJ");
					e.addAll(entities);
				}
			}
		}

		List<String> IORules = grammaticalRoleRuleSet.get("IO");
		if (IORules != null)
		{
			for (String rule : IORules)
			{
				List<TreeNode> nodes = parseOneRule(tree, rule);
				List<Entity> entities = getEntities(nodes, "IO");
				if (entities != null && !entities.isEmpty() && !result.containsKey("IO"))
				{
					result.put("IO", entities);
				}
				else if (entities != null && !entities.isEmpty() && result.containsKey("IO"))
				{
					List<Entity> e = result.get("IO");
					e.addAll(entities);
				}

			}
		}
		List<Entity> IOEntities = result.get("IO");

		List<String> OBJRules = grammaticalRoleRuleSet.get("OBJ");
		if (OBJRules != null)
		{
			for (String rule : OBJRules)
			{
				List<TreeNode> nodes = parseOneRule(tree, rule);
				List<Entity> entities = getEntities(nodes, "OBJ");

				if (IOEntities != null && !IOEntities.isEmpty())
				{// 间接宾语不能同时为直接宾语
					for (int i = 0; i < entities.size(); i++)
					{
						for (Entity ioe : IOEntities)
						{
							if (ioe.getEntityName().equals(entities.get(i).getEntityName())
									&& ioe.getSite() == entities.get(i).getSite())
							{
								entities.remove(i);
							}
						}
					}
				}

				if (entities != null && !entities.isEmpty() && !result.containsKey("OBJ"))
				{
					result.put("OBJ", entities);
				}
				else if (entities != null && !entities.isEmpty() && result.containsKey("OBJ"))
				{
					List<Entity> e = result.get("OBJ");
					e.addAll(entities);
				}

			}
		}

		return result;
	}

	private static List<Entity> getEntities(List<TreeNode> nodes, String grammaticalRole)
	{
		List<Entity> result = new ArrayList<Entity>();
		for (int i = 0; i < nodes.size(); i++)
		{
			List<Entity> tmp = getEntity(nodes.get(i), grammaticalRole);
			if (tmp != null)
				result.addAll(tmp);
		}
		return result;
	}

	private static List<Entity> getEntity(TreeNode treeNode, String grammaticalRole)
	{
		List<Entity> result = new ArrayList<Entity>();
		if (TreeNodeUtil.isCoordinatingNP(treeNode))
		{
			List<TreeNode> children = TreeNodeUtil.getChildNodeWithSpecifiedName(treeNode, new String[] {"NP", "NN", "NR"});
			for (int i=0 ; i<children.size() ; i++)
			{
				Entity e = new Entity(children.get(i), grammaticalRole);
				result.add(e);
			}
		}
		else
		{
			TreeNode node = TreeNodeUtil.getHead(treeNode, NPHeadRuleSetPTB.getNPRuleSet());
			Entity e = new Entity(node, grammaticalRole);
			result.add(e);
		}
		return result;
	}

	private static List<TreeNode> parseOneRule(TreeNode tree, String ruleStr)
	{
		if (tree == null || ruleStr == null)
			throw new RuntimeException("输入错误");
		String[] str = ruleStr.split("#");
		if (str.length != 2)
			throw new RuntimeException("规则错误");
		String targetNodeName = str[0];
		String rule = str[1];
		List<TreeNode> result = new ArrayList<TreeNode>();

		List<String> parts = BracketExpUtil.stringToList(rule);

		List<TreeNode> nodes = TreeNodeUtil.getNodesWithSpecified(tree, new String[] { parts.get(1) });

		for (int i = 0; i < nodes.size(); i++)
		{
			Map<Boolean, List<TreeNode>> r = parse(nodes.get(i), parts, 0, new EndSite(), targetNodeName);
			if (r.containsKey(true))
			{
				result.addAll(r.get(true));
			}
		}
		return result;
	}

	private static Map<Boolean, List<TreeNode>> parse(TreeNode node, List<String> parts, int startSite, EndSite endSite,
			String targetNodeName)
	{
		TreeNode currentNode = node;
		TreeNode father = null;
		int index = -1;
		TreeNode targetNode = null;
		Map<Boolean, List<TreeNode>> result = new HashMap<Boolean, List<TreeNode>>();
		for (int i = startSite + 2; i < parts.size(); i++)
		{
			if (parts.get(i).equals("("))
			{
				String tmp1 = parts.get(i + 1);
				if (tmp1.equals("_"))
				{
					tmp1 = targetNodeName;

				}
				List<TreeNode> tmp = TreeNodeUtil.getChildNodeWithSpecifiedName(currentNode, new String[] { tmp1 });
				boolean tag = false;
				EndSite end = new EndSite();
				for (int j = 0; j < tmp.size(); j++)
				{
					Map<Boolean, List<TreeNode>> map = parse(tmp.get(j), parts, i, end, targetNodeName);
					if (map.containsKey(true))
					{
						tag = true;
						if (result.containsKey(true))
						{
							if (!map.get(true).isEmpty())
							{
								List<TreeNode> nodes = result.get(true);
								nodes.addAll(map.get(true));
							}
						}
						else
						{
							result.put(true, map.get(true));
						}
					}

				}
				if (tag == false)
				{
					if (result.containsKey(true))
						result.remove(true);
					return result;
				}
				i = end.getEndSite();
			}
			else if (parts.get(i).equals(" "))
			{
			}
			else if (parts.get(i).equals(")"))
			{
				if (parts.get(startSite + 1).equals("_"))
					targetNode = node;
				if (!result.containsKey(true))
				{
					List<TreeNode> t = new ArrayList<TreeNode>();
					if (targetNode != null)
					{
						t.add(targetNode);
					}
					result.put(true, t);
				}
				else
				{
					if (targetNode != null)
					{
						result.get(true).add(targetNode);
					}
				}
				endSite.setEndSite(i);
				return result;
			}
			else
			{
				if (father == null)
				{
					father = node.getParent();
					index = TreeNodeUtil.getIndex(node);
				}

				if (parts.get(i).equals("_"))
				{
					if (index + 1 >= father.getChildrenNum() || !father.getChildName(index + 1).equals(targetNodeName))
					{
						if (result.containsKey(true))
							result.remove(true);
						return result;
					}
					index++;
					targetNode = father.getChild(index);
					currentNode = node.getParent().getChild(index);
				}
				else if (parts.get(i).equals("?"))
				{
					index++;
					currentNode = node.getParent().getChild(index);
				}
				else
				{
					if (index + 1 >= father.getChildrenNum() || !father.getChildName(index + 1).equals(parts.get(i)))
					{
						if (result.containsKey(true))
							result.remove(true);
						return result;
					}
					index++;
					currentNode = node.getParent().getChild(index);
				}
			}
		}
		return null;
	}

	private static class EndSite
	{
		private int endSite = 0;

		public EndSite()
		{

		}

		public int getEndSite()
		{
			return endSite;
		}

		public void setEndSite(int endSite)
		{
			this.endSite = endSite;
		}

		@Override
		public String toString()
		{
			return "[endSite=" + endSite + "]";
		}

	}

}
