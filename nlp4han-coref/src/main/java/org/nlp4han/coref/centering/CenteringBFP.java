package org.nlp4han.coref.centering;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.nlp4han.coref.hobbs.AttributeFilter;
import org.nlp4han.coref.hobbs.AttributeGeneratorByDic;
import org.nlp4han.coref.hobbs.NodeNameFilter;
import org.nlp4han.coref.hobbs.PNFilter;
import org.nlp4han.coref.hobbs.TreeNodeUtil;

import com.lc.nlp4han.constituent.TreeNode;

public class CenteringBFP
{
	private List<List<Entity>> entitiesOfUtterances;	// 所有句子的实体集
	private List<TreeNode> rootNodesOfUtterances;		//所有句子的根结点集
	public static String SEPARATOR = "->";		//指代结果中的分隔符 


	public CenteringBFP()
	{

	}

	public CenteringBFP(List<List<Entity>> entitiesOfUtterances, List<TreeNode> rootNodesOfUtterances)
	{
		this.entitiesOfUtterances = entitiesOfUtterances;
		this.rootNodesOfUtterances = rootNodesOfUtterances;
	}

	/**
	 * 运行BFP算法
	 * 
	 * @return
	 */
	public List<List<Entity>> run()
	{
		List<Center> centersOfUtterances = new ArrayList<Center>(); 		// Utterances的Center集合
		if (entitiesOfUtterances.size() > 1)
		{
			Center ui = generateCenter(entitiesOfUtterances.get(0), null, rootNodesOfUtterances.get(0), null);
			centersOfUtterances.add(ui);
			for (int i = 1; i < entitiesOfUtterances.size(); i++)
			{
				List<Entity> utter = entitiesOfUtterances.get(i);
				ui = generateCenter(utter, ui, rootNodesOfUtterances.get(i), rootNodesOfUtterances.get(i-1));
				centersOfUtterances.add(ui);
			}
			return extractEntitySet(centersOfUtterances);
		}
		return entitiesOfUtterances;
	}

	/**
	 * 是否为代词
	 * @param str
	 * @return
	 */
	private static boolean isPronoun(Entity	e, TreeNode root)
	{
		if (e == null)
			throw new RuntimeException("输入错误");
		TreeNode node = entity2Node(e, root);
		TreeNode pnNode = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(node, new String[] {"PN"});
		if (pnNode != null)
			return true;
		else
			return false;
	}

	/**
	 * 从Center列表中抽取对应的Entity列表
	 * @param centersOfUtterances
	 * @return
	 */
	private static List<List<Entity>> extractEntitySet(List<Center> centersOfUtterances)
	{
		if (centersOfUtterances != null)
		{
			List<List<Entity>> result = new ArrayList<List<Entity>>();
			for (int i=0 ; i<centersOfUtterances.size() ; i++)
			{
				result.add(centersOfUtterances.get(i).getCf());
			}
			return result;
		}
		return null;
	}

	/**
	 * 设置实体集
	 * 
	 * @param entitiesOfUtterances
	 */
	public void setEntitiesOfUtterances(List<List<Entity>> entitiesOfUtterances)
	{
		this.entitiesOfUtterances = entitiesOfUtterances;
	}
	
	public void setRootNodesOfUtterances(List<TreeNode> rootNodesOfUtterances)
	{
		this.rootNodesOfUtterances = rootNodesOfUtterances;
	}

	/**
	 * 生成话语的中心数值（Cb、Cf、Cp）
	 * 
	 * @param entities
	 * @param entity
	 * @return
	 */
	public static Center generateCenter(List<Entity> entitiesOfUi, Center centerOfUi_1, TreeNode rootOfUi, TreeNode rootOfUi_1)
	{
		if (entitiesOfUi == null)
		{
			throw new RuntimeException("输入错误！");
		}
		if (centerOfUi_1 != null)
		{
			List<Entity> pronounEntities = getPronounEntities(entitiesOfUi, rootOfUi);
			if (pronounEntities.isEmpty())
				return new Center(entitiesOfUi, entitiesOfUi);
			List<List<Entity>> anaphorEntitiesList = generateAllAnaphorEntities(pronounEntities, centerOfUi_1, rootOfUi, rootOfUi_1);
			List<Center> candidates = new ArrayList<Center>();
			List<String> transitions = new ArrayList<String>();
			for (int i=0 ; i<anaphorEntitiesList.size() ; i++)
			{
				List<Entity> newEntitiesOfUi = replaceEntity(entitiesOfUi, pronounEntities, anaphorEntitiesList.get(i));
				Center c = new Center(entitiesOfUi, newEntitiesOfUi);		//注意：第一个句子的时候，两个参数应该相同，Cb的值应为null
				candidates.add(c);
				String transition = getTransition(c, centerOfUi_1);
				transitions.add(transition);
			}
			if (!transitions.isEmpty())
			{
				int index = bestTransition(transitions);
				if (index >-1)
					return candidates.get(index);
			}
			return null;
				
		}
		else 
		{//两个参数相同，表示无指代，也表示第一个句子
			return new Center(entitiesOfUi, entitiesOfUi);
		}
	}
	

	/**
	 * 将实体集中的代词实体替换成回指的实体
	 * @param entitiesOfUi
	 * @param pronounEntitiesOfUi
	 * @param list
	 * @return
	 */
	private static List<Entity> replaceEntity(List<Entity> entitiesOfUi, List<Entity> pronounEntitiesOfUi, List<Entity> anaphorEntitiesOfUi_1)
	{
		if (entitiesOfUi == null || pronounEntitiesOfUi == null || anaphorEntitiesOfUi_1 == null)
		{
			throw new RuntimeException("输入错误");
		}
		List<Entity> result = new ArrayList<Entity>();
		int index;
		for (int i=0 ; i<entitiesOfUi.size() ; i++)
		{
			if ((index=pronounEntitiesOfUi.indexOf(entitiesOfUi.get(i))) != -1)
			{
				result.add(anaphorEntitiesOfUi_1.get(index));
			}
			else
				result.add(entitiesOfUi.get(i));
		}
		return result;
	}

	/**
	 * 得到实体集中的代词实体
	 * @param entitiesOfUi
	 * @return
	 */
	private static List<Entity> getPronounEntities(List<Entity> entitiesOfUi, TreeNode rootOfUi)
	{
		if (entitiesOfUi != null)
		{
			List<Entity> result = new ArrayList<Entity>();
			if (entitiesOfUi.size() > 0)
			{
				for (Entity e : entitiesOfUi)
				{
					if (isPronoun(e, rootOfUi))
					{
						result.add(e);
					}
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * 获取最佳Transition的索引
	 */
	private static int bestTransition(List<String> transitions)
	{
		if (transitions == null)
		{
			throw new RuntimeException("输入错误！");
		}
		if (transitions.isEmpty())
			return -1;
		int result;
		if ((result = transitions.indexOf("Continue")) != -1)
		{
			return result;
		}
		else if ((result = transitions.indexOf("Retain")) != -1)
		{
			return result;
		}
		else if ((result = transitions.indexOf("Smooth-Shift")) != -1)
		{
			return result;
		}
		else if ((result = transitions.indexOf("Rough-Shift")) != -1)
		{
			return result;
		}
		return -1;
	}

	/**
	 * 生成所有的回指实体集，用以确定Cb
	 * @param entitiesOfUi
	 * @param centerOfUi_1
	 * @return
	 */
	private static List<List<Entity>> generateAllAnaphorEntities(List<Entity> pronounEntitiesOfUi, Center centerOfUi_1, TreeNode rootOfUi, TreeNode rootOfUi_1)
	{	
		if (pronounEntitiesOfUi == null || centerOfUi_1 == null)
		{
			throw new RuntimeException("输入错误");
		}
		if (pronounEntitiesOfUi.size() < 1)
		{
			return new ArrayList<List<Entity>>();
		}
		List<List<Entity>> result;
		List<Entity> entitiesOfUi_1 = centerOfUi_1.getCf();
		List<List<Entity>> tmp = new ArrayList<List<Entity>>();
		
		AttributeFilter attributeFilter = new AttributeFilter(new PNFilter(new NodeNameFilter())); // 组合过滤器
		attributeFilter.setAttributeGenerator(new AttributeGeneratorByDic()); // 装入属性生成器
		
		for (int i=0 ; i<pronounEntitiesOfUi.size() ; i++)
		{
			List<Entity> candidates = getMatchingEntities(entitiesOfUi_1, pronounEntitiesOfUi.get(i), attributeFilter, rootOfUi, rootOfUi_1);
			tmp.add(candidates);
		}
		if (tmp.size() < 1)
			return new ArrayList<List<Entity>>();
		result = transform(tmp);
		return result;
	}

	private static List<List<Entity>> transform(List<List<Entity>> entitiesList)
	{
		if (entitiesList == null)
			throw new RuntimeException("输入错误");
		List<List<Entity>> result = new ArrayList<List<Entity>>();
		List<Integer> indexQueue = new ArrayList<Integer>();  //记录entitiesList中每一组List<Entity>被访问的位置
		
		for (int i=0 ; i<entitiesList.size() ; i++)
		{//初始化 indexQueue
			indexQueue.add(0);
		}
		
		while (indexQueue.get(indexQueue.size()-1) < entitiesList.get(indexQueue.size()-1).size())
		{
			List<Entity> list = new ArrayList<Entity>();
			for (int i=0 ; i<indexQueue.size(); i++)
			{
				Entity e = entitiesList.get(i).get(indexQueue.get(i));
				list.add(e);
			}
			result.add(list);
			indexQueue.set(0, indexQueue.get(0)+1);
			
			for (int i=0 ; i<indexQueue.size()-1 ; i++)
			{
				if (indexQueue.get(i) >= entitiesList.get(i).size())
				{
					indexQueue.set(i, 0);
					indexQueue.set(i+1, indexQueue.get(i+1)+1);
				}
				else
					break;
			}
		}
		
		return result;
	}

	private static List<Entity> getMatchingEntities(List<Entity> entitiesOfUi_1, Entity entity, AttributeFilter filter, TreeNode rootOfUi, TreeNode rootOfUi_1)
	{
		List<Entity> result = new ArrayList<Entity>();
		TreeNode leaf = entity2Node(entity, rootOfUi);
		TreeNode node = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(leaf, new String[] {"NP", "PN"});
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		List<TreeNode> nodes_copy = new LinkedList<TreeNode>();
		for (int i=0 ; i<entitiesOfUi_1.size() ; i++)
		{
			leaf = entity2Node(entitiesOfUi_1.get(i), rootOfUi_1);
			TreeNode tmp = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(leaf, new String[] {"NP", "PN"});
			nodes.add(tmp);
			nodes_copy.add(tmp);
		}
		
		filter.setReferenceNode(node);
		filter.setUp(nodes_copy);
		filter.filtering();
		
		for (TreeNode n : nodes_copy)
		{
			if (incompatible(n, node))
				continue;
			else
			{
				int index = nodes.indexOf(n);
				result.add(entitiesOfUi_1.get(index));
			}
		}
		
		return result;
	}
	
	/**
	 * node1与node2不相容的规则
	 * @param node1
	 * @param node2
	 * @return
	 */
	private static boolean incompatible(TreeNode node1, TreeNode node2)
	{
		if (node1.getNodeName().equals("PN") && node2.getNodeName().equals("PN") && !TreeNodeUtil.getString(node1).equals(TreeNodeUtil.getString(node2)))
		{//node1与node2都是"PN"（代词），但他们是不同的代词，则node1与node2不相容
			return true;
		}
		return false;
	}


	private static TreeNode entity2Node(Entity entity, TreeNode root)
	{
		if (entity == null && root == null)
			throw new RuntimeException("输入错误");
		TreeNode result = TreeNodeUtil.string2Node(entity.getEntityName(), entity.getSite(), root);
		return result;
	}

	/**
	 * 根据前后两句的Center，获得相应的Transition
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static String getTransition(Center centerOfUi, Center centerOfUi_1)
	{//注意：若Ui-1为首句，其Cb为undefined(null)
		if (centerOfUi != null && centerOfUi_1 != null)
		{
			if (centerOfUi.getCb().equals(centerOfUi.getCp()))
			{
				if (centerOfUi.getCb().equals(centerOfUi_1.getCb()) || centerOfUi_1.getCb()==null)
					return "Continue";
				if (!centerOfUi.getCb().equals(centerOfUi_1.getCb()))
					return "Smooth-Shift";
			}
			else
			{
				if (centerOfUi.getCb().equals(centerOfUi_1.getCb()) || centerOfUi_1.getCb()==null)
					return "Retain";
				if (!centerOfUi.getCb().equals(centerOfUi_1.getCb()))
					return "Rough-Shift";
			}
		}
		return null;
	}
	
	public static List<String> analysisResult(List<List<Entity>> oldEntitiesSet, List<List<Entity>> newEntitiesSet)
	{//TODO
		if (newEntitiesSet == null || oldEntitiesSet == null || newEntitiesSet.size() != oldEntitiesSet.size())
			throw new RuntimeException("输入错误");
		if (newEntitiesSet.size() < 2)
			return new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		for (int i=1 ; i<newEntitiesSet.size() ; i++)
		{
			for (int j=0 ; j<newEntitiesSet.get(i).size() ; j++)
			{
				if (!newEntitiesSet.get(i).get(j).equals(oldEntitiesSet.get(i).get(j)))
				{
					String word1 = oldEntitiesSet.get(i).get(j).getEntityName();
					String size1 = "(" + (i+1) + "-"+(oldEntitiesSet.get(i).get(j).getSite()+1)+ ")";
					String word2;
					String size2;
					int index = oldEntitiesSet.get(i-1).indexOf(newEntitiesSet.get(i).get(j));
					Entity e = oldEntitiesSet.get(i-1).get(index);
					word2 = e.getEntityName();
					size2 = "(" + i + "-"+(e.getSite()+1)+ ")";
					String str = word1 + size1 + SEPARATOR + word2+ size2;
					
					result.add(str);
				}
			}
		}
		return result;
	}
}
