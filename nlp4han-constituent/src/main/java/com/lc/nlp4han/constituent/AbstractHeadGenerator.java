package com.lc.nlp4han.constituent;

/**
 * 生成头结点
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public abstract class AbstractHeadGenerator
{
	/**
	 * 为并列结构生成头结点和词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	protected abstract String generateHeadPosForCordinator(HeadTreeNode node);

	/**
	 * 为特殊规则生成头结点和词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	protected abstract String generateHeadPosForSpecialRules(HeadTreeNode node);

	/**
	 * 为一般规则生成头结点和词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	protected abstract String generateHeadPosForNormalRules(HeadTreeNode node);

	/**
	 * 提取头结点和头结点对应的词性
	 * 
	 * 头节点词和词性间_分隔
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return 头结点和头结点对应的词性
	 */
	private String extractHeadWordAndPos(HeadTreeNode node)
	{
		String headWithPOS = null;
		headWithPOS = generateHeadPosForCordinator(node);

		if (headWithPOS == null)
		{
			headWithPOS = generateHeadPosForSpecialRules(node);
		}

		if (headWithPOS == null)
		{
			headWithPOS = generateHeadPosForNormalRules(node);
		}

		return headWithPOS;
	}

	/**
	 * 提取头结点词
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return 头结点词
	 */
	public String extractHeadWord(HeadTreeNode node)
	{

		return extractHeadWordAndPos(node).split("_")[0];
	}

	/**
	 * 提取头结点的词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return 头结点的词性
	 */
	public String extractHeadPos(HeadTreeNode node)
	{
		return extractHeadWordAndPos(node).split("_")[1];
	}

	/**
	 * 提取头结点的索引
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return 头结点的索引
	 */
	public int extractHeadIndex(HeadTreeNode node)
	{
		return Integer.parseInt(extractHeadWordAndPos(node).split("_")[2]);
	}
}
