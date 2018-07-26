package org.nlp4han.coref.hobbs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.nlp4han.coref.hobbs.MentionAttribute.Animacy;
import org.nlp4han.coref.hobbs.MentionAttribute.Gender;
import org.nlp4han.coref.hobbs.MentionAttribute.Number;

import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.util.DictionaryLoader;

public class AttributeGeneratorByDic implements AttributeGenerator
{

	@Override
	public MentionAttribute extractAttributes(TreeNode treeNode)
	{
		MentionAttribute result = new MentionAttribute();
		TreeNode head = TreeNodeUtil.getHead(treeNode);
		return null;
	}

	private Properties loadProperties(String fileName) throws IOException
	{
		Properties result = new Properties();
		InputStream stream = AttributeGeneratorByDic.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/coref/" + fileName);
		result.load(stream);

		return result;
	}

	private Set<String> loadTxt(String fileName) throws IOException
	{
		Set<String> result = null;
		InputStream dictIn = AttributeGeneratorByDic.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/segment/" + fileName);
		result = DictionaryLoader.getWords(dictIn, "UTF-8");

		return result;
	}

	/**
	 * 获取性别属性
	 * 
	 * @param treeNode
	 * @return
	 */
	public Gender getGender(TreeNode treeNode)
	{
		TreeNode head = TreeNodeUtil.getHead(treeNode);
		try
		{
			String value;
			if (treeNode.getNodeName().equals("PN"))
			{
				Properties genderDic = loadProperties("gender_PN.properties");
				value = genderDic.getProperty(TreeNodeUtil.getString(treeNode));
			}
			else
			{
				Properties genderDic = loadProperties("gender.properties");
				value = genderDic.getProperty(TreeNodeUtil.getString(head));
			}
			if (value.equalsIgnoreCase("female"))
				return Gender.FEMALE;
			else if (value.equalsIgnoreCase("male"))
				return Gender.MALE;
			else
				return Gender.UNKNOWN;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return Gender.UNKNOWN;
	}

	/**
	 * 获取单复数属性
	 * 
	 * @param treeNode
	 * @return
	 */
	public Number getNumber(TreeNode treeNode)
	{

		if (treeNode.getNodeName().equals("PN"))
		{
			String value;
			Properties numberDic;
			try
			{
				numberDic = loadProperties("number_PN.properties");
				value = numberDic.getProperty(treeNode.getChildName(0));
				if (value.equalsIgnoreCase("singular"))
					return Number.SINGULAR;
				else if (value.equalsIgnoreCase("plural"))
					return Number.PLURAL;
				else
					return Number.UNKNOWN;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		else
		{
			String stringOfLeafNodes;
			if (TreeNodeUtil.hasNodeName((List<TreeNode>) treeNode.getChildren(), "DP"))
			{// 含有DP结点，则查表number_DP.properties
				List<TreeNode> nodes = TreeNodeUtil.getChildNodeWithSpecifiedName(treeNode, new String[] { "DP" });
				TreeNode dpNode = nodes.get(0);
				stringOfLeafNodes = TreeNodeUtil.getString(treeNode);
				Properties numberDicDP;
				try
				{
					numberDicDP = loadProperties("number_DP.properties");
					Set keys = numberDicDP.keySet();
					Iterator it = keys.iterator();
					while (it.hasNext())
					{
						String key = (String) it.next();
						if (stringOfLeafNodes.contains(key))
						{
							String value = numberDicDP.getProperty(key);
							if (value.equalsIgnoreCase("singular"))
								return Number.SINGULAR;
							else if (value.equalsIgnoreCase("plural"))
								return Number.PLURAL;
							else
								return Number.UNKNOWN;
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (TreeNodeUtil.hasNodeName((List<TreeNode>) treeNode.getChildren(), "QP"))
			{
				List<TreeNode> qpNodes = TreeNodeUtil.getChildNodeWithSpecifiedName(treeNode, new String[] { "QP" });
				TreeNode qpNode = qpNodes.get(0);
				if (TreeNodeUtil.hasNodeName((List<TreeNode>) qpNode.getChildren(), "CD"))
				{
					List<TreeNode> cdNodes = TreeNodeUtil.getChildNodeWithSpecifiedName(treeNode,
							new String[] { "cd" });
					TreeNode cdNode = cdNodes.get(0);
					if (cdNode.getChild(0).getNodeName().equals("一") || cdNode.getChild(0).getNodeName().equals("1"))
					{
						return Number.SINGULAR;
					}
					return Number.PLURAL;
				}
			}
		}

		return Number.UNKNOWN;
	}

	/**
	 * 获取动物性属性
	 * 
	 * @param treeNode
	 * @return
	 */
	public Animacy getAnimacy(TreeNode treeNode)
	{
		TreeNode head = TreeNodeUtil.getHead(treeNode);
		try
		{
			String value;
			if (treeNode.getNodeName().equals("PN"))
			{
				Properties animacyDic = loadProperties("animacy_PN.properties");
				value = animacyDic.getProperty(TreeNodeUtil.getString(treeNode));
			}
			else
			{
				Properties animacyDic = loadProperties("animacy.properties");
				value = animacyDic.getProperty(TreeNodeUtil.getString(head));
			}
			if (value.equalsIgnoreCase("true"))
				return Animacy.TRUE;
			else if (value.equalsIgnoreCase("false"))
				return Animacy.FALSE;
			else
				return Animacy.UNKNOWN;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return Animacy.UNKNOWN;
	}

}
