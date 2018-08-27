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
		return null;
	}

	/**
	 * 是否为代词
	 * @param str
	 * @return
	 */
	private boolean isPronoun(String str)
	{
		String[] pronouns = { "我", "我们", "你", "你们", "她", "她们", "他", "他们", "它", "它们" };
		if (str != null)
			for (String pro : pronouns)
			{
				if (str.equals(pro))
					return true;
			}
		return false;
	}

	/**
	 * 从Center列表中抽取对应的Entity列表
	 * @param centersOfUtterances
	 * @return
	 */
	private List<List<Entity>> extractEntitySet(List<Center> centersOfUtterances)
	{
		if (centersOfUtterances.size() > 0)
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

	/**
	 * 生成话语的中心数值（Cb、Cf、Cp）
	 * 
	 * @param entities
	 * @param entity
	 * @return
	 */
	public Center generateCenter(List<Entity> entitiesOfUi, Center centerOfUi_1, TreeNode rootOfUi, TreeNode rootOfUi_1)
	{// 注意：第二个参数为null是表明是第一个句子；
		if (entitiesOfUi == null)
		{
			throw new RuntimeException("输入错误！");
		}
		if (centerOfUi_1 != null)
		{
			List<Entity> pronounEntities = getPronounEntities(entitiesOfUi);
			List<List<Entity>> anaphorEntitiesList = generateAllAnaphorEntities(pronounEntities, centerOfUi_1, rootOfUi, rootOfUi_1);
			List<Center> candidates = new ArrayList<Center>();
			List<String> transitions = new ArrayList<String>();
			for (int i=0 ; i<anaphorEntitiesList.size() ; i++)
			{
				List<Entity> newEntitiesOfUi = replaceEntity(entitiesOfUi, pronounEntities, anaphorEntitiesList.get(i));
				Center c = new Center(entitiesOfUi, newEntitiesOfUi);		//注意：第一个句子的时候，两个参数应该相同，Cb的值应为null
				candidates.add(c);
				String transition = getTransition(centerOfUi_1, c);
				transitions.add(transition);
			}
			int index = bestTransition(transitions);
			return candidates.get(index);
		}
		else 
		{
			return new Center(entitiesOfUi, null);
		}
	}
	

	/**
	 * 将实体集中的代词实体替换成回指的实体
	 * @param entitiesOfUi
	 * @param pronounEntitiesOfUi
	 * @param list
	 * @return
	 */
	private List<Entity> replaceEntity(List<Entity> entitiesOfUi, List<Entity> pronounEntitiesOfUi, List<Entity> anaphorEntitiesOfUi_1)
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
	private List<Entity> getPronounEntities(List<Entity> entitiesOfUi)
	{
		if (entitiesOfUi != null)
		{
			List<Entity> result = new ArrayList<Entity>();
			if (entitiesOfUi.size() > 0)
			{
				for (Entity e : entitiesOfUi)
				{
					if (isPronoun(e.getEntityName()))
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
	private int bestTransition(List<String> transitions)
	{
		if (transitions == null || transitions.size() < 1)
		{
			throw new RuntimeException("输入错误！");
		}
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
	private List<List<Entity>> generateAllAnaphorEntities(List<Entity> pronounEntitiesOfUi, Center centerOfUi_1, TreeNode rootOfUi, TreeNode rootOfUi_1)
	{	
		if (pronounEntitiesOfUi == null || centerOfUi_1 == null)
		{
			throw new RuntimeException("输入错误");
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
		result = transform(tmp);
		return result;
	}

	private List<List<Entity>> transform(List<List<Entity>> entitiesList)
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

	private List<Entity> getMatchingEntities(List<Entity> entitiesOfUi_1, Entity entity, AttributeFilter filter, TreeNode rootOfUi, TreeNode rootOfUi_1)
	{
		List<Entity> result = new ArrayList<Entity>();
		TreeNode node = transform2Node(entity, rootOfUi);
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		List<TreeNode> nodes_copy = new LinkedList<TreeNode>();
		for (int i=0 ; i<entitiesOfUi_1.size() ; i++)
		{
			TreeNode tmp = transform2Node(entitiesOfUi_1.get(i), rootOfUi_1);
			nodes.add(tmp);
			nodes_copy.add(tmp);
		}
		
		filter.setReferenceNode(node);
		filter.setUp(nodes_copy);
		filter.filtering();
		
		for (TreeNode n : nodes_copy)
		{
			int index = nodes.indexOf(n);
			result.add(entitiesOfUi_1.get(index));
		}
		
		return result;
	}

	private TreeNode transform2Node(Entity entity, TreeNode root)
	{
		if (entity == null && root == null)
			throw new RuntimeException("输入错误");
		TreeNode result;
		TreeNode leaf = TreeNodeUtil.getAllLeafNodes(root).get(entity.getSite());
		TreeNode NPnode = TreeNodeUtil.getFirstNPNodeUp(root);
		List<TreeNode> PNnode = TreeNodeUtil.getNodesWithSpecifiedNameBetween2Nodes(leaf, NPnode, new String[] {"PN"});
		if (PNnode != null && !PNnode.isEmpty())
		{
			result = PNnode.get(0);
		}
		else
			result = NPnode;
		return result;
	}

	/**
	 * 根据前后两句的Center，获得相应的Transition
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public String getTransition(Center centerOfUi, Center centerOfUi_1)
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
}
