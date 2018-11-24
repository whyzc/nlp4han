package org.nlp4han.coref.centering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nlp4han.coref.AnaphoraResolution;
import org.nlp4han.coref.hobbs.AttributeFilter;
import org.nlp4han.coref.hobbs.AttributeGeneratorByDic;
import org.nlp4han.coref.hobbs.CandidateFilter;
import org.nlp4han.coref.hobbs.NodeNameFilter;
import org.nlp4han.coref.hobbs.PNFilter;

import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeNodeUtil;

/**
 * 中心理论的BFP算法
 * 
 * @author 杨智超
 *
 */
public class CenteringBFP implements AnaphoraResolution
{
	public static String SEPARATOR = "->"; // 指代结果中的分隔符
	private HashMap<String, List<String>> grammaticalRoleRuleSet = GrammaticalRoleRuleSet.getGrammaticalRoleRuleSet(); // 语法角色规则集
	private CandidateFilter attributeFilter;

	public CenteringBFP()
	{

	}

	/**
	 * 运行BFP算法
	 * 
	 * @param entitiesOfUtterances
	 *            话语的实体集
	 * @param rootNodesOfUtterances
	 *            话语的结构树集
	 * @return 生成消解后，新的实体集
	 */
	private List<List<Entity>> run(List<List<Entity>> entitiesOfUtterances, List<TreeNode> rootNodesOfUtterances)
	{
		List<Center> centersOfUtterances = new ArrayList<Center>(); // Utterances的Center集合
		if (entitiesOfUtterances.size() > 1)
		{
			Center ui = generateCenter(entitiesOfUtterances.get(0), null, null, rootNodesOfUtterances.get(0), null);
			centersOfUtterances.add(ui);
			for (int i = 1; i < entitiesOfUtterances.size(); i++)
			{
				List<Entity> utter = entitiesOfUtterances.get(i);
				ui = generateCenter(utter, entitiesOfUtterances.get(i-1), ui, rootNodesOfUtterances.get(i), rootNodesOfUtterances.get(i - 1));
				centersOfUtterances.add(ui);
			}
			return extractEntitySet(centersOfUtterances);
		}
		return entitiesOfUtterances;
	}

	/**
	 * 实体e是否为代词
	 * 
	 * @param e
	 *            待检测实体
	 * @param root
	 *            实体e对应结点的根结点
	 * @return
	 */
	private boolean isPronoun(Entity e, TreeNode root)
	{
		if (e == null)
			throw new RuntimeException("输入错误");
		TreeNode node = entity2Node(e, root);
		TreeNode pnNode = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(node, new String[] { "PN" });
		if (pnNode != null)
			return true;
		else
			return false;
	}

	/**
	 * 从Center列表中抽取对应的Entity集
	 * 
	 * @param centersOfUtterances
	 * @return
	 */
	private List<List<Entity>> extractEntitySet(List<Center> centersOfUtterances)
	{
		if (centersOfUtterances != null)
		{
			List<List<Entity>> result = new ArrayList<List<Entity>>();
			for (int i = 0; i < centersOfUtterances.size(); i++)
			{
				result.add(centersOfUtterances.get(i).getCf());
			}
			return result;
		}
		return null;
	}

	/**
	 * 设置语法角色规则集
	 * 
	 * @param grammaticalRoleRuleSet
	 */
	public void setGrammaticalRoleRuleSet(HashMap<String, List<String>> grammaticalRoleRuleSet)
	{
		this.grammaticalRoleRuleSet = grammaticalRoleRuleSet;
	}

	/**
	 * 设置属性过滤器
	 * 
	 * @param attributeFilter
	 */
	public void setAttributeFilter(AttributeFilter attributeFilter)
	{
		this.attributeFilter = attributeFilter;
	}

	/**
	 * 生成话语的最优的中心数值（Cb、Cf、Cp）
	 * 
	 * @param entitiesOfUi
	 *            当前会话的实体集
	 * @param centerOfUi_1
	 *            前句会话的实体中心
	 * @param rootOfUi
	 *            当前会话的根节点
	 * @param rootOfUi_1
	 *            前句会话的根节点
	 * @return
	 */
	public Center generateCenter(List<Entity> entitiesOfUi, List<Entity> entitiesOfUi_1, Center centerOfUi_1, TreeNode rootOfUi, TreeNode rootOfUi_1)
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
			List<List<Entity>> anaphorEntitiesList = generateAllAnaphorEntities(pronounEntities, entitiesOfUi_1, centerOfUi_1, rootOfUi,
					rootOfUi_1);
			List<Center> candidates = new ArrayList<Center>();
			List<String> transitions = new ArrayList<String>();
			for (int i = 0; i < anaphorEntitiesList.size(); i++)
			{
				List<Entity> newEntitiesOfUi = replaceEntity(entitiesOfUi, pronounEntities, anaphorEntitiesList.get(i));
				Center c = new Center(entitiesOfUi, newEntitiesOfUi); // 注意：第一个句子的时候，两个参数应该相同，Cb的值应为null
				candidates.add(c);
				String transition = getTransition(c, centerOfUi_1);
				transitions.add(transition);
			}
			if (!transitions.isEmpty())
			{
				int index = bestTransition(transitions);
				if (index > -1)
					return candidates.get(index);
			}
			return null;

		}
		else
		{// 两个参数相同，表示无指代，也表示第一个句子
			return new Center(entitiesOfUi, entitiesOfUi);
		}
	}

	/**
	 * 将实体集中的代词实体替换成回指的实体
	 * 
	 * @param entitiesOfUi
	 * @param pronounEntitiesOfUi
	 * @param anaphorEntitiesOfUi_1
	 * @return
	 */
	private List<Entity> replaceEntity(List<Entity> entitiesOfUi, List<Entity> pronounEntitiesOfUi,
			List<Entity> anaphorEntitiesOfUi_1)
	{
		if (entitiesOfUi == null || pronounEntitiesOfUi == null || anaphorEntitiesOfUi_1 == null)
		{
			throw new RuntimeException("输入错误");
		}
		List<Entity> result = new ArrayList<Entity>();
		int index;
		for (int i = 0; i < entitiesOfUi.size(); i++)
		{
			if ((index = pronounEntitiesOfUi.indexOf(entitiesOfUi.get(i))) != -1)
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
	 * 
	 * @param entitiesOfUi
	 * @param rootOfUi
	 * @return
	 */
	private List<Entity> getPronounEntities(List<Entity> entitiesOfUi, TreeNode rootOfUi)
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
	 */
	private List<List<Entity>> generateAllAnaphorEntities(List<Entity> pronounEntitiesOfUi, List<Entity> entitiesOfUi_1, Center centerOfUi_1,
			TreeNode rootOfUi, TreeNode rootOfUi_1)
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
		List<Entity> newEntitiesOfUi_1 = centerOfUi_1.getCf();
		List<List<Entity>> tmp = new ArrayList<List<Entity>>();

		for (int i = 0; i < pronounEntitiesOfUi.size(); i++)
		{
			List<Entity> candidates = getMatchingEntities(newEntitiesOfUi_1, entitiesOfUi_1, pronounEntitiesOfUi.get(i), attributeFilter,
					rootOfUi, rootOfUi_1);
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
		List<Integer> indexQueue = new ArrayList<Integer>(); // 记录entitiesList中每一组List<Entity>被访问的位置

		for (int i = 0; i < entitiesList.size(); i++)
		{// 初始化 indexQueue
			indexQueue.add(0);
		}

		while (indexQueue.get(indexQueue.size() - 1) < entitiesList.get(indexQueue.size() - 1).size())
		{
			List<Entity> list = new ArrayList<Entity>();
			for (int i = 0; i < indexQueue.size(); i++)
			{
				Entity e = entitiesList.get(i).get(indexQueue.get(i));
				list.add(e);
			}
			result.add(list);
			indexQueue.set(0, indexQueue.get(0) + 1);

			for (int i = 0; i < indexQueue.size() - 1; i++)
			{
				if (indexQueue.get(i) >= entitiesList.get(i).size())
				{
					indexQueue.set(i, 0);
					indexQueue.set(i + 1, indexQueue.get(i + 1) + 1);
				}
				else
					break;
			}
		}

		return result;
	}

	/**
	 * entity为Ui中的代词实体，在Ui-1的实体集entitiesOfUi_1中找出属性相容的实体
	 * filter为属性过滤器，rootOfUi为Ui的结构树根结点，rootOfUi_1为Ui-1的结构树根结点
	 */
	private static List<Entity> getMatchingEntities(List<Entity> newEntitiesOfUi_1, List<Entity> entitiesOfUi_1, Entity entity, CandidateFilter filter,
			TreeNode rootOfUi, TreeNode rootOfUi_1)
	{
		List<Entity> result = new ArrayList<Entity>();
		TreeNode leaf = entity2Node(entity, rootOfUi);
		TreeNode node = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(leaf, new String[] { "NP", "PN" });
		List<TreeNode> nodes = new LinkedList<TreeNode>();
		List<TreeNode> nodes_copy = new LinkedList<TreeNode>();
		for (int i = 0; i < entitiesOfUi_1.size(); i++)
		{
			leaf = entity2Node(entitiesOfUi_1.get(i), rootOfUi_1);
			TreeNode tmp = TreeNodeUtil.getFirstNodeUpWithSpecifiedName(leaf, new String[] { "NP", "PN" });
			nodes.add(tmp);
			nodes_copy.add(tmp);
		}

		if (filter != null)
		{
			filter.setReferenceConditions(node);
			filter.setFilteredNodes(nodes_copy);
			filter.filter();
		}

		for (TreeNode n : nodes_copy)
		{
			if (incompatible(n, node))
				continue;
			else
			{
				int index = nodes.indexOf(n);
				result.add(newEntitiesOfUi_1.get(index));
			}
		}

		return result;
	}

	/**
	 * node1与node2不相容的规则
	 * 
	 * @param node1
	 * @param node2
	 * @return
	 */
	private static boolean incompatible(TreeNode node1, TreeNode node2)
	{
		if (node1.getNodeName().equals("PN") && node2.getNodeName().equals("PN")
				&& !TreeNodeUtil.getString(node1).equals(TreeNodeUtil.getString(node2)))
		{// node1与node2都是"PN"（代词），但他们是不同的代词，则node1与node2不相容
			return true;
		}
		return false;
	}

	/**
	 * 在结构树root中找出实体entity对应的结点
	 */
	private static TreeNode entity2Node(Entity entity, TreeNode root)
	{
		if (entity == null && root == null)
			throw new RuntimeException("输入错误");
		TreeNode result = TreeNodeUtil.string2Node(entity.getEntityName(), entity.getSite(), root);
		return result;
	}

	/**
	 * 根据前后两句的Center，获得相应的Transition状态
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static String getTransition(Center centerOfUi, Center centerOfUi_1)
	{// 注意：若Ui-1为首句，其Cb为undefined(null)
		if (centerOfUi != null && centerOfUi_1 != null)
		{
			if (centerOfUi.getCb().equals(centerOfUi.getCp()))
			{
				if (centerOfUi.getCb().equals(centerOfUi_1.getCb()) || centerOfUi_1.getCb() == null)
					return "Continue";
				if (!centerOfUi.getCb().equals(centerOfUi_1.getCb()))
					return "Smooth-Shift";
			}
			else
			{
				if (centerOfUi.getCb().equals(centerOfUi_1.getCb()) || centerOfUi_1.getCb() == null)
					return "Retain";
				if (!centerOfUi.getCb().equals(centerOfUi_1.getCb()))
					return "Rough-Shift";
			}
		}
		return null;
	}

	/**
	 * 根据会话的实体集与将其中的代词替换成先行词的实体后的实体集，得出以字符串形式表示的指代消解结果
	 * 
	 * @param oldEntitiesSet
	 * @param newEntitiesSet
	 * @return
	 */
	public static List<String> analysisResult(List<List<Entity>> oldEntitiesSet, List<List<Entity>> newEntitiesSet)
	{
		if (newEntitiesSet == null || oldEntitiesSet == null || newEntitiesSet.size() != oldEntitiesSet.size())
			throw new RuntimeException("输入错误");
		if (newEntitiesSet.size() < 2)
			return new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		for (int i = 1; i < newEntitiesSet.size(); i++)
		{
			for (int j = 0; j < newEntitiesSet.get(i).size(); j++)
			{
				if (!newEntitiesSet.get(i).get(j).equals(oldEntitiesSet.get(i).get(j)))
				{
					String word1 = oldEntitiesSet.get(i).get(j).getEntityName();
					String size1 = "(" + (i + 1) + "-" + (oldEntitiesSet.get(i).get(j).getSite() + 1) + ")";
					String word2;
					String size2;
					int index = oldEntitiesSet.get(i - 1).indexOf(newEntitiesSet.get(i).get(j));
					Entity e = oldEntitiesSet.get(i - 1).get(index);
					word2 = e.getEntityName();
					size2 = "(" + i + "-" + (e.getSite() + 1) + ")";
					String str = word1 + size1 + SEPARATOR + word2 + size2;

					result.add(str);
				}
			}
		}
		return result;
	}

	public static Map<TreeNode, TreeNode> analysisResult(List<List<Entity>> oldEntitiesSet,
			List<List<Entity>> newEntitiesSet, List<TreeNode> rootNodesOfUtterances)
	{
		if (newEntitiesSet == null || oldEntitiesSet == null || newEntitiesSet.size() != oldEntitiesSet.size())
			throw new RuntimeException("输入错误");
		if (newEntitiesSet.size() < 2)
			return new HashMap<TreeNode, TreeNode>();
		Map<TreeNode, TreeNode> result = new HashMap<TreeNode, TreeNode>();
		for (int i = 1; i < newEntitiesSet.size(); i++)
		{
			for (int j = 0; j < newEntitiesSet.get(i).size(); j++)
			{
				if (!newEntitiesSet.get(i).get(j).equals(oldEntitiesSet.get(i).get(j)))
				{
					TreeNode root1 = rootNodesOfUtterances.get(i);
					TreeNode ponoun = TreeNodeUtil.getAllLeafNodes(root1).get(oldEntitiesSet.get(i).get(j).getSite());
					TreeNode antecedent = null;
					int k=i-1;
					int index;
					while (k>-1)
					{
						if ((index = oldEntitiesSet.get(k).indexOf(newEntitiesSet.get(i).get(j))) > -1)
						{
							Entity e = oldEntitiesSet.get(k).get(index);
							TreeNode root2 = rootNodesOfUtterances.get(k);
							antecedent = TreeNodeUtil.getAllLeafNodes(root2).get(e.getSite());
							break;
						}
						else
						{
							k--;
						}
					}

					result.put(ponoun, antecedent);
				}
			}
		}
		return result;
	}

	@Override
	public Map<TreeNode, TreeNode> resolve(List<TreeNode> sentences)
	{
		List<List<Entity>> eou = new ArrayList<List<Entity>>();

		if (attributeFilter == null)
		{
			AttributeFilter af = new AttributeFilter(new PNFilter(new NodeNameFilter())); // 组合过滤器

			af.setAttributeGenerator(new AttributeGeneratorByDic()); // 装入属性生成器
			attributeFilter = af;
		}

		for (int i = 0; i < sentences.size(); i++)
		{
			eou.add(Entity.sort(Entity.entities(sentences.get(i), grammaticalRoleRuleSet)));
		}

		List<List<Entity>> newEntities = run(eou, sentences);
		Map<TreeNode, TreeNode> result = analysisResult(eou, newEntities, sentences);
		return result;
	}

	@Override
	public TreeNode resolve(List<TreeNode> sentences, TreeNode pronoun)
	{
		Map<TreeNode, TreeNode> allResults = resolve(sentences);
		Set<Entry<TreeNode, TreeNode>> enteries = allResults.entrySet();
		for (Entry<TreeNode, TreeNode> e : enteries)
		{
			if (e.getKey().equals(TreeNodeUtil.getAllLeafNodes(pronoun).get(0)))
				return e.getValue();
		}
		return null;
	}

}
