package com.lc.nlp4han.constituent;

import java.util.HashMap;
import java.util.List;

/**
 * 具体的生成头结点的实现类 参考：Collins 1999论文 添加中文的特殊终结符的处理
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class HeadGeneratorCollins extends AbstractHeadGenerator {

	private HashMap<String, HeadRule> normalRules;
	private HashMap<String, List<HeadRule>> specialRules;

	public HeadGeneratorCollins(HeadRuleSet headRuleSet) {
		this.normalRules = headRuleSet.getNormalRuleSet();
		this.specialRules = headRuleSet.getSpecialRuleSet();
	}

	@Override
	protected String generateHeadPosForCordinator(HeadTreeNode node) {
		// 有些非终端节点需要进行处理，因为它可能是NP-SBJ的格式，我只需要拿NP的部分进行匹配操作
		String parentNonTerminal = node.getNodeName().split("-")[0];
		// 处理X-X CC X的情况
		boolean flag = false;
		int record = -1;
		// 先判断是不是这种结构
		for (int i = 0; i < node.getChildrenNum() - 2; i++) {
			if (node.getChildName(i).split("-")[0].equals(parentNonTerminal) && node.getChildName(i + 1).equals("CC")
					&& node.getChildName(i + 2).split("-")[0].equals(parentNonTerminal)) {
				flag = true;
				record = i;
				break;
			}
		}
		if (flag == true && record != -1) {
			return node.getChildHeadWord(record) + "_" + node.getChildHeadPos(record);
		}
		return null;
	}

	@Override
	protected String generateHeadPosForSpecialRules(HeadTreeNode node) {
		if (specialRules == null) {
			return null;
		}
		String currNodeName = node.getNodeName();
		// 如果最后一个是POS，返回最后一个
		if (node.getLastChildName().equals("POS")) {
			return node.getLastChildHeadWord() + "_" + node.getLastChildHeadPos();
		}
		if (specialRules.containsKey(currNodeName)) {
			for (int k = 0; k < specialRules.get(currNodeName).size(); k++) {
				if (specialRules.get(currNodeName).get(k).getDirection().equals("left")) {
					// 用所有的子节点从左向右匹配规则中每一个
					for (int i = 0; i < specialRules.get(currNodeName).get(k).getRightRulesSize(); i++) {
						for (int j = 0; j < node.getChildrenNum(); j++) {
							if (node.getChildName(j).equals(specialRules.get(currNodeName).get(k).getIRightRule(i))) {
								return node.getChildHeadWord(j) + "_" + node.getChildHeadPos(j);
							}
						}
					}
				} else if (specialRules.get(currNodeName).get(k).getDirection().equals("right")) {
					for (int i = 0; i < specialRules.get(currNodeName).get(k).getRightRulesSize(); i++) {
						for (int j = node.getChildrenNum() - 1; j >= 0; j--) {
							if (node.getChildName(j).equals(specialRules.get(currNodeName).get(k).getIRightRule(i))) {
								return node.getChildHeadWord(j) + "_" + node.getChildHeadPos(j);
							}
						}
					}
				}
			}
			// 否则返回最后一个
			return node.getLastChildHeadWord() + "_" + node.getLastChildHeadPos();
		} else {
			return null;
		}
	}

	@Override
	protected String generateHeadPosForNormalRules(HeadTreeNode node) {
		String currentNodeName = node.getNodeName();
		if (normalRules.containsKey(currentNodeName)) {
			if (normalRules.get(currentNodeName).getDirection().equals("left")) {
				// 用所有的子节点从左向右匹配规则中每一个
				for (int i = 0; i < normalRules.get(currentNodeName).getRightRulesSize(); i++) {
					for (int j = 0; j < node.getChildrenNum(); j++) {
						if (node.getChildName(j).equals(normalRules.get(currentNodeName).getIRightRule(i))) {
							return node.getChildHeadWord(j) + "_" + node.getChildHeadPos(j);
						}
					}
				}
				//决策列表中不存在，则从左方向开始找第一个
				return node.getFirstChildHeadWord() + "_" + node.getFirstChildHeadWordPos();
			} else if (normalRules.get(currentNodeName).getDirection().equals("right")) {
				for (int i = 0; i < normalRules.get(currentNodeName).getRightRulesSize(); i++) {
					for (int j = node.getChildrenNum() - 1; j >= 0; j--) {
						if (node.getChildName(j).equals(normalRules.get(currentNodeName).getIRightRule(i))) {
							return node.getChildHeadWord(j) + "_" + node.getChildHeadPos(j);
						}
					}
				}
				//决策列表中不存在，则从右方向开始找第一个
				return node.getLastChildHeadWord() + "_" + node.getLastChildHeadPos();
			}
			// 如果所有的规则都没有匹配，返回最左边的第一个
			return node.getFirstChildHeadWord() + "_" + node.getFirstChildHeadWordPos();
		} else {
			return node.getFirstChildHeadWord() + "_" + node.getFirstChildHeadWordPos();
		}
	}
}
