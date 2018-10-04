package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;

/**
 * 成分句法分析用的评价结构
 * 
 * 一棵子树由根节点非终结符和该子树在句子中的跨度构成
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class EvalStructure{

	private String nonterminal;
	private int begin;
	private int end;
	
	public EvalStructure(String nonterminal, int begin, int end){
		this.nonterminal = nonterminal;
		this.begin = begin;
		this.end = end;
	}

	public void setNonterminal(String nonterminal){
		this.nonterminal = nonterminal;
	}
	
	/**
	 * 获得非终结符
	 * @return
	 */
	public String getNonTerminal(){
		return this.nonterminal;
	}
	
	/**
	 * 获得开始的序号
	 * @return
	 */
	public int getBegin(){
		return this.begin;
	}
	
	/**
	 * 获得结束序号
	 * @return
	 */
	public int getEnd(){
		return this.end;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		result = prime * result + ((nonterminal == null) ? 0 : nonterminal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EvalStructure other = (EvalStructure) obj;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		if (nonterminal == null) {
			if (other.nonterminal != null)
				return false;
		} else if (!nonterminal.equals(other.nonterminal))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.nonterminal+"-("+this.begin+":"+this.end+")";
	}
	
	/**
	 * 将一颗完整的树转成非终端节点和包含的词语的开始结束序号的一个序列，用于比较
	 * 
	 *  预终结符（词性非终结符）不生成评价跨度
	 * 
	 * @param node
	 *            一颗完整的树
	 * @return
	 */
	public static List<EvalStructure> getEvalStructures(TreeNode node)
	{
		List<EvalStructure> list = new ArrayList<>();
		for (int i = 0; i < node.getChildrenNum(); i++)
		{
			list.addAll(getEvalStructures(node.getChild(i)));
		}

		if (node.getChildrenNum() != 0)
		{
			if (node.getChildrenNum() == 1 && node.getFirstChild().getChildrenNum() == 0)
			{
				 // 不添加预终结符，这比添加会大大降低效果
			}
			else
			{
				TreeNode left = node;
				TreeNode right = node;
				while (left.getChildrenNum() != 0)
				{
					left = left.getFirstChild();
				}

				while (right.getChildrenNum() != 0)
				{
					right = right.getLastChild();
				}

				list.add(new EvalStructure(node.getNodeName(), left.getWordIndex(), (right.getWordIndex()) + 1));
			}
		}

		return list;
	}
}
