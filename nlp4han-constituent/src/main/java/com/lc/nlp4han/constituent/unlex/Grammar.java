package com.lc.nlp4han.constituent.unlex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import com.lc.nlp4han.constituent.pcfg.PCFG;
import com.lc.nlp4han.constituent.pcfg.PRule;

/**
 * 表示由树库得到的语法
 * 
 * @author 王宁
 * 
 */
public class Grammar
{

	protected String StartSymbol = "ROOT";

	protected HashSet<BinaryRule> bRules;
	protected HashSet<UnaryRule> uRules;
	protected Lexicon lexicon;// 包含preRules

	// 添加相同孩子为key的map
	protected HashMap<Integer, HashMap<PreterminalRule, PreterminalRule>> preRuleBySameChildren; // 外层map<word在字典中的索引,内map>
	protected HashMap<Short, HashMap<Short, HashMap<BinaryRule, BinaryRule>>> bRuleBySameChildren;
	protected HashMap<Short, HashMap<UnaryRule, UnaryRule>> uRuleBySameChildren;
	// 相同父节点的规则放在一个map中
	protected HashMap<Short, HashMap<PreterminalRule, PreterminalRule>> preRuleBySameHead; // 内map<ruleHashcode/rule>
	protected HashMap<Short, HashMap<BinaryRule, BinaryRule>> bRuleBySameHead;
	protected HashMap<Short, HashMap<UnaryRule, UnaryRule>> uRuleBySameHead;

	protected NonterminalTable nonterminalTable;
	// 使用规则数量的期望的比作为新的规则概率
	protected HashMap<Short, Double[]> sameParentRulesCount = new HashMap<>();// <parent,[ParentSubIndex,denominator]>
	public double mergeWeight[][];
	public static Random random = new Random(0);

	public Grammar(HashSet<BinaryRule> bRules, HashSet<UnaryRule> uRules, Lexicon lexicon,
			NonterminalTable nonterminalTable)
	{
		this.bRules = bRules;
		this.uRules = uRules;
		this.lexicon = lexicon;
		this.bRuleBySameChildren = new HashMap<Short, HashMap<Short, HashMap<BinaryRule, BinaryRule>>>();
		this.uRuleBySameChildren = new HashMap<Short, HashMap<UnaryRule, UnaryRule>>();
		this.preRuleBySameChildren = new HashMap<Integer, HashMap<PreterminalRule, PreterminalRule>>();
		this.bRuleBySameHead = new HashMap<Short, HashMap<BinaryRule, BinaryRule>>();
		this.uRuleBySameHead = new HashMap<Short, HashMap<UnaryRule, UnaryRule>>();
		this.preRuleBySameHead = new HashMap<Short, HashMap<PreterminalRule, PreterminalRule>>();
		this.nonterminalTable = nonterminalTable;
		init();
		grammarExam();
	}

	public void forgetRuleCountExpectation()
	{
		for (UnaryRule uRule : uRules)
		{
			uRule.setCountExpectation(null);
		}
		for (BinaryRule bRule : bRules)
		{
			bRule.setCountExpectation(null);
		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			preRule.setCountExpectation(null);
		}
	}

	/**
	 * 返回CFG，其中规则包含一元规则、二元规则
	 * 
	 * @return
	 */
	public PCFG getPCFG()
	{
		PCFG pcfg = new PCFG();
		for (UnaryRule uRule : uRules)
		{
			PRule pRule = new PRule(uRule.getScores().get(0).get(0), nonterminalTable.stringValue(uRule.parent),
					nonterminalTable.stringValue(uRule.getChild()));
			pcfg.add(pRule);
		}
		for (BinaryRule bRule : bRules)
		{
			PRule pRule = new PRule(bRule.getScores().get(0).get(0).get(0), nonterminalTable.stringValue(bRule.parent),
					nonterminalTable.stringValue(bRule.getLeftChild()),
					nonterminalTable.stringValue(bRule.getRightChild()));
			pcfg.add(pRule);
		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			PRule pRule = new PRule(preRule.getScores().get(0), nonterminalTable.stringValue(preRule.getParent()),
					preRule.getWord());
			pcfg.add(pRule);
		}
		pcfg.setStartSymbol("ROOT");
		return pcfg;
	}

	public void init()
	{
		for (BinaryRule bRule : bRules)
		{

			if (!bRuleBySameHead.containsKey(bRule.parent))
			{
				bRuleBySameHead.put(bRule.parent, new HashMap<BinaryRule, BinaryRule>());
			}
			bRuleBySameHead.get(bRule.parent).put(bRule, bRule);

			if (!bRuleBySameChildren.containsKey(bRule.getLeftChild()))
			{

				bRuleBySameChildren.put(bRule.getLeftChild(), new HashMap<Short, HashMap<BinaryRule, BinaryRule>>());
			}
			if (!bRuleBySameChildren.get(bRule.getLeftChild()).containsKey(bRule.getRightChild()))
			{
				bRuleBySameChildren.get(bRule.getLeftChild()).put(bRule.getRightChild(),
						new HashMap<BinaryRule, BinaryRule>());
			}
			bRuleBySameChildren.get(bRule.getLeftChild()).get(bRule.getRightChild()).put(bRule, bRule);
		}

		for (UnaryRule uRule : uRules)
		{
			if (!uRuleBySameHead.containsKey(uRule.parent))
			{
				uRuleBySameHead.put(uRule.parent, new HashMap<UnaryRule, UnaryRule>());
			}
			uRuleBySameHead.get(uRule.parent).put(uRule, uRule);

			if (!uRuleBySameChildren.containsKey(uRule.getChild()))
			{
				uRuleBySameChildren.put(uRule.getChild(), new HashMap<UnaryRule, UnaryRule>());
			}
			uRuleBySameChildren.get(uRule.getChild()).put(uRule, uRule);
		}

		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			if (!preRuleBySameHead.containsKey(preRule.parent))
			{
				preRuleBySameHead.put(preRule.parent, new HashMap<PreterminalRule, PreterminalRule>());
			}
			preRuleBySameHead.get(preRule.parent).put(preRule, preRule);

			if (!preRuleBySameChildren.containsKey(lexicon.getDictionary().get(preRule.getWord())))
			{
				preRuleBySameChildren.put(lexicon.getDictionary().get(preRule.getWord()),
						new HashMap<PreterminalRule, PreterminalRule>());
			}
			preRuleBySameChildren.get(lexicon.getDictionary().get(preRule.getWord())).put(preRule, preRule);
		}
	}

	public void grammarExam()
	{
		int berrcount = 0, uerrcount = 0, perrcount = 0;
		int berrcount1 = 0, uerrcount1 = 0, perrcount1 = 0;
		for (BinaryRule bRule : bRules)
		{
			if (bRule == bRuleBySameHead.get(bRule.parent).get(bRule))
			{
				// System.out.println(true);
			}
			else
			{
				berrcount++;
				System.err.println(false + "********************二元规则集bRuleBySameHead" + berrcount);
			}
			if (bRule != bRuleBySameChildren.get(bRule.getLeftChild()).get(bRule.getRightChild()).get(bRule))
			{
				berrcount1++;
				System.err.println(false + "********************二元规则集bRuleBySameChildren" + berrcount1);
			}
		}
		for (UnaryRule uRule : uRules)
		{
			if (uRule == uRuleBySameHead.get(uRule.parent).get(uRule))
			{
				// System.out.println(true);
			}
			else
			{
				uerrcount++;
				System.err.println(false + "********************一元规则uRuleBySameHead" + uerrcount);
			}
			if (uRule != uRuleBySameChildren.get(uRule.getChild()).get(uRule))
			{
				uerrcount1++;
				System.err.println(false + "********************一元规则uRuleBySameChildren" + uerrcount1);
			}
		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			if (preRule == preRuleBySameHead.get(preRule.parent).get(preRule))
			{
				// System.out.println(true);
			}
			else
			{
				perrcount++;
				System.err.println(false + "********************预终结符号规则preRuleBySameHead" + perrcount);
			}
			if (preRule != preRuleBySameChildren.get(lexicon.getDictionary().get(preRule.getWord())).get(preRule))
			{
				perrcount1++;
				System.err.println(false + "********************预终结符号规则preRuleBySameChildren" + perrcount1);
			}
		}
	}

	public HashSet<BinaryRule> getbRules()
	{
		return bRules;
	}

	public HashSet<UnaryRule> getuRules()
	{
		return uRules;
	}

	public Lexicon getLexicon()
	{
		return lexicon;
	}

	public HashSet<PreterminalRule> getPreRules()
	{
		return lexicon.getPreRules();
	}

	public NonterminalTable getNonterminalTable()
	{
		return nonterminalTable;
	}

	public String getStartSymbol()
	{
		return StartSymbol;
	}

	public void setStartSymbol(String startSymbol)
	{
		StartSymbol = startSymbol;
	}

}
