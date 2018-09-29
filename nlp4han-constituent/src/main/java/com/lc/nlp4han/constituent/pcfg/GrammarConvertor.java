package com.lc.nlp4han.constituent.pcfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrammarConvertor
{
	private CFG cnf;
	private HashSet<RewriteRule> deletePRuleSet;// 将要被消除的单元规则的集合
	private String type;// 将要转换的类型
	private HashSet<String> posSet = new HashSet<String>();// 词性标注集

	public CFG convertCFGToCNF(CFG cfg)
	{
		this.cnf = new CFG();
		convertGrammar("CNF", cfg);
		return cnf;
	}

	public PCFG convertPCFGToP2NF(PCFG pcfg)
	{
		this.cnf = new PCFG();
		convertGrammar("P2NF", pcfg);
		return (PCFG) cnf;
	}

	/**
	 * 这里的转换为PCNF在消除单元规则时,消除到POS层次即停止
	 * 
	 * @param pcfg
	 * @return
	 */
	public PCFG convertPCFGToPCNF(PCFG pcfg)
	{
		this.cnf = new PCFG();
		convertGrammar("PCNF", pcfg);
		return (PCFG) cnf;
	}

	/**
	 * 转换的通用类
	 * 
	 * @param type
	 * @param cfg
	 */
	private void convertGrammar(String type, CFG cfg)
	{
		this.type = type;
		convertTo2NF(cfg);
		if (type.contains("P"))
		{
			this.deletePRuleSet = new HashSet<RewriteRule>();
			getPOSSet();
			removeUnitProduction();
		}
	}

	/**
	 * 将规则转换为2nf形式（即不消除单元规则的乔姆斯基范式）
	 * 
	 * @param cfg
	 */
	private void convertTo2NF(CFG cfg)
	{
		cnf.setNonTerminalSet(cfg.getNonTerminalSet());
		cnf.setTerminalSet(cfg.getTerminalSet());
		cnf.setStartSymbol(cfg.getStartSymbol());

		// 前期处理，遍历pcfg将规则加入pcnf
		priorDisposal(cfg);
	}

	/**
	 * 前期处理，遍历的将规则加入pcnf 将字符串个数多于两个的递归的减为两个 将终结符和非终结符混合转换为两个非终结符 直接添加右侧只有一个字符串的规则
	 */
	private void priorDisposal(CFG cfg)
	{
		for (RewriteRule rule : cfg.getRuleSet())
		{
			if (rule.getRhs().size() >= 3)
			{
				// 如果右侧中有终结符，则转换为伪非终结符
				if (!cnf.getNonTerminalSet().containsAll(rule.getRhs()))
				{
					ConvertToNonTerRHS(rule);
				}

				reduceRHSNum(rule);
			}

			// 先检测右侧有两个字符串的规则是否为终结符和非终结符混合，若混合则先将终结符转换为非终结符
			if (rule.getRhs().size() == 2)
			{
				// 如果右侧中有终结符，则转换为伪非终结符
				if (!cnf.getNonTerminalSet().containsAll(rule.getRhs()))
				{
					ConvertToNonTerRHS(rule);
				}

				cnf.add(rule);
			}

			// 先添加进cnf随后处理
			if (rule.getRhs().size() == 1)
			{
				cnf.add(rule);
			}
		}
	}

	/**
	 * 将右侧全部转换为非终结符，并添加新的非终结符，新的规则
	 */
	private void ConvertToNonTerRHS(RewriteRule rule)
	{
		ArrayList<String> rhs = new ArrayList<String>();
		for (String string : rule.getRhs())
		{
			if (!cnf.getNonTerminalSet().contains(string))
			{
				String newString = "$" + string + "$";
				cnf.addNonTerminal(newString);// 添加新的伪非终结符

				// 添加新的规则
				if (type.contains("P"))
				{
					cnf.add(new PRule(1.0, newString, string));
				}
				else
				{
					cnf.add(new RewriteRule(newString, string));
				}
				rhs.add(newString);
			}
			else
			{
				rhs.add(string);
			}
		}

		rule.setRhs(rhs);
	}

	/**
	 * 每次选择最右侧字符串的两个为新的规则的右侧字符串，以&联接两个非终结符，如此，方便在P2NF转回为CFG
	 */
	private void reduceRHSNum(RewriteRule rule)
	{
		if (rule.getRhs().size() == 2)
		{
			cnf.add(rule);
			return;
		}

		List<String> list = rule.getRhs();
		int size = list.size();
		String str = list.get(size - 2) + "&" + list.get(size - 1);// 新规则的左侧

		// 最右侧的两个非终结符合成一个，并形成新的规则
		if (type.contains("P"))
		{
			cnf.add(new PRule(1.0, str, list.get(size - 2), list.get(size - 1)));
		}
		else
		{
			cnf.add(new RewriteRule(str, list.get(size - 2), list.get(size - 1)));
		}
		cnf.addNonTerminal(str);// 添加新的合成非终结符

		ArrayList<String> rhsList = new ArrayList<String>();
		rhsList.addAll(rule.getRhs().subList(0, rule.getRhs().size() - 2));
		rhsList.add(str);
		rule.setRhs(rhsList);

		// 递归，直到rhs的个数为2时
		reduceRHSNum(rule);
	}

	/**
	 * 消除单元规则
	 */
	private void removeUnitProduction()
	{
		Set<String> nonterSet = cnf.getNonTerminalSet();
		for (String nonTer : cnf.getNonTerminalSet())
		{
			for (RewriteRule rule : cnf.getRuleBylhs(nonTer))
			{
				if (rule.getRhs().size() == 1)
				{
					String rhs = rule.getRhs().get(0);
					if (posSet.contains(rhs))
					{// 消除单元规则终止与POS层次
						continue;
					}
					if (nonterSet.contains(rhs))
					{
						deletePRuleSet.add(rule);
						removeUPAndAddNewRule(rule);
					}
				}
			}
		}
		DeletePRuleSet();
	}

	private void removeUPAndAddNewRule(RewriteRule rule)
	{
		String lhs = rule.getLhs();
		String rhs = rule.getRhs().get(0);

		String[] lhs1 = lhs.split("@");
		if (lhs1.length >= 3)
		{
			return;// 如果单元规则迭代有3次以上，则返回
		}
		if (posSet.contains(rule.getRhs().get(0)))
		{
			cnf.add(rule);// 若该规则右侧为词性标注则直接添加
			return;
		}
		for (String lhs2 : lhs1)
		{
			if (lhs2.equals(rhs))
			{
				return;// 如果出现循环非终结符则返回
			}
		}
		for (RewriteRule rule1 : cnf.getRuleBylhs(rule.getRhs().get(0)))
		{
			RewriteRule rule2;
			if (type.contains("P"))
			{
				PRule prule1 = (PRule) rule1;
				PRule prule = (PRule) rule;

				rule2 = new PRule(prule.getProb() * prule1.getProb(), prule.getLhs() + "@" + prule1.getLhs(),
						prule1.getRhs());
			}
			else
			{
				rule2 = new RewriteRule(rule.getLhs() + "@" + rule1.getLhs(), rule1.getRhs());
			}
			if (rule1.getRhs().size() == 2 || !cnf.getNonTerminalSet().contains(rule1.getRhs().get(0)))
			{
				cnf.add(rule2);
			}
			else
			{
				removeUPAndAddNewRule(rule2);
			}
		}
	}

	private void DeletePRuleSet()
	{
		for (RewriteRule rule : deletePRuleSet)
		{
			cnf.getRuleSet().remove(rule);
			cnf.getRuleBylhs(rule.getLhs()).remove(rule);
			cnf.getRuleByrhs(rule.getRhs()).remove(rule);
		}
	}

	/**
	 * 得到词性标注
	 */
	private void getPOSSet()
	{
		Set<String> nonTer = cnf.getNonTerminalSet();
		for (RewriteRule rule : cnf.getRuleSet())
		{
			if (rule.getRhs().size() == 1 && !nonTer.contains(rule.getRhs().get(0)))
			{
				posSet.add(rule.getLhs());
			}
		}
	}
}
