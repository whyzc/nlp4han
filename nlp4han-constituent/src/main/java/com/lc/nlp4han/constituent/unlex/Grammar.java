package com.lc.nlp4han.constituent.unlex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;

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
	public static Random random = new Random(0);
	private String StartSymbol = "ROOT";

	private HashSet<BinaryRule> bRules;
	private HashSet<UnaryRule> uRules;
	private Lexicon lexicon;// 包含preRules

	// 添加相同孩子为key的map
	private HashMap<Integer, HashMap<PreterminalRule, PreterminalRule>> preRuleBySameChildren; // 外层map<word在字典中的索引,内map>
	private HashMap<Short, HashMap<Short, HashMap<BinaryRule, BinaryRule>>> bRuleBySameChildren;
	private HashMap<Short, HashMap<UnaryRule, UnaryRule>> uRuleBySameChildren;
	// 相同父节点的规则放在一个map中
	private HashMap<Short, HashMap<PreterminalRule, PreterminalRule>> preRuleBySameHead; // 内map<ruleHashcode/rule>
	private HashMap<Short, HashMap<BinaryRule, BinaryRule>> bRuleBySameHead;
	private HashMap<Short, HashMap<UnaryRule, UnaryRule>> uRuleBySameHead;

	private NonterminalTable nonterminalTable;

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

	public Grammar()
	{
		this.bRules = new HashSet<>();
		this.uRules = new HashSet<>();
		this.lexicon = new Lexicon(null, null, 0);
		this.bRuleBySameChildren = new HashMap<Short, HashMap<Short, HashMap<BinaryRule, BinaryRule>>>();
		this.uRuleBySameChildren = new HashMap<Short, HashMap<UnaryRule, UnaryRule>>();
		this.preRuleBySameChildren = new HashMap<Integer, HashMap<PreterminalRule, PreterminalRule>>();
		this.bRuleBySameHead = new HashMap<Short, HashMap<BinaryRule, BinaryRule>>();
		this.uRuleBySameHead = new HashMap<Short, HashMap<UnaryRule, UnaryRule>>();
		this.preRuleBySameHead = new HashMap<Short, HashMap<PreterminalRule, PreterminalRule>>();
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
			for (String aRule : uRule.toStringRules(this))
			{
				String[] arr = aRule.split(" ");
				double score = Double.parseDouble(arr[arr.length - 1]);
				if (score != 0)
				{
					PRule pRule = new PRule(Double.parseDouble(arr[arr.length - 1]), arr[0], arr[2]);
					pcfg.add(pRule);
				}
			}
		}
		for (BinaryRule bRule : bRules)
		{
			for (String aRule : bRule.toStringRules(this))
			{
				String[] arr = aRule.split(" ");
				double score = Double.parseDouble(arr[arr.length - 1]);
				if (score != 0)
				{
					PRule pRule = new PRule(score, arr[0], arr[2], arr[3]);
					pcfg.add(pRule);
				}
			}

		}
		for (PreterminalRule preRule : lexicon.getPreRules())
		{
			for (String aRule : preRule.toStringRules(this))
			{
				String[] arr = aRule.split(" ");
				double score = Double.parseDouble(arr[arr.length - 1]);
				if (score != 0)
				{
					PRule pRule = new PRule(score, arr[0], arr[2]);
					pcfg.add(pRule);
				}
			}
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

	public TreeMap<String, Double> calculateSameParentRuleScoreSum()
	{
		TreeMap<String, Double> sameParentRuleScoreSum = new TreeMap<>();
		for (short symbol : this.nonterminalTable.getInt_strMap().keySet())
		{
			if (this.bRuleBySameHead.containsKey(symbol))
			{
				for (BinaryRule bRule : this.bRuleBySameHead.get(symbol).keySet())
				{
					for (Map.Entry<String, Double> entry : bRule.getParent_i_ScoceSum(this).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								new BiFunction<Double, Double, Double>()
								{
									@Override
									public Double apply(Double t, Double u)
									{
										return t + u;
									}
								});
					}
				}
			}
			if (this.uRuleBySameHead.containsKey(symbol))
			{
				for (UnaryRule uRule : this.uRuleBySameHead.get(symbol).keySet())
				{
					for (Map.Entry<String, Double> entry : uRule.getParent_i_ScoceSum(this).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}

			if (this.preRuleBySameHead.containsKey(symbol))
			{
				for (PreterminalRule preRule : this.preRuleBySameHead.get(symbol).keySet())
				{
					for (Map.Entry<String, Double> entry : preRule.getParent_i_ScoceSum(this).entrySet())
					{
						sameParentRuleScoreSum.merge(entry.getKey(), entry.getValue(),
								(score, newScore) -> score + newScore);
					}
				}
			}
		}
		return sameParentRuleScoreSum;
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

	/**
	 * @param symbol
	 * @return 该符号对应的String
	 */
	public String symbolStrValue(short symbol)
	{
		return nonterminalTable.stringValue(symbol);
	}

	/**
	 * @param symbol
	 * @return 该符号对应的short值
	 */
	public short symbolIntValue(String symbol)
	{
		return nonterminalTable.intValue(symbol);
	}

	public void add(BinaryRule bRule)
	{
		bRules.add(bRule);
	}

	public void add(UnaryRule uRule)
	{
		uRules.add(uRule);
	}

	public void add(PreterminalRule preRule)
	{
		lexicon.add(preRule);
	}

	public BinaryRule readBRule(String[] rule)
	{

		String parent = rule[0].split("_")[0];
		String lChild = rule[2].split("_")[0];
		String rChild = rule[3].split("_")[0];
		double score = Double.parseDouble(rule[4]);
		short index_pSubSym;
		short index_lCSubSym;
		short index_rCSubSym;
		short numSymP = nonterminalTable.getNumSubsymbolArr().get(symbolIntValue(parent));
		short numSymLC = nonterminalTable.getNumSubsymbolArr().get(symbolIntValue(lChild));
		short numSymRC = nonterminalTable.getNumSubsymbolArr().get(symbolIntValue(rChild));
		if (numSymP == 1)
			index_pSubSym = 0;
		else
			index_pSubSym = Short.parseShort(rule[0].split("_")[1]);
		if (numSymLC == 1)
			index_lCSubSym = 0;
		else
			index_lCSubSym = Short.parseShort(rule[2].split("_")[1]);
		if (numSymRC == 1)
			index_rCSubSym = 0;
		else
			index_rCSubSym = Short.parseShort(rule[3].split("_")[1]);
		BinaryRule bRule = new BinaryRule(symbolIntValue(parent), symbolIntValue(lChild), symbolIntValue(rChild));
		if (bRules.contains(bRule))
		{
			for (BinaryRule theRule : bRules)
			{
				if (bRule.equals(theRule))
				{
					bRule = theRule;
					break;
				}
			}
		}
		else
		{
			bRule.initScores(numSymP, numSymLC, numSymRC);
			bRules.add(bRule);
		}
		bRule.setScore(index_pSubSym, index_lCSubSym, index_rCSubSym, score);
		return bRule;
	}

	public UnaryRule readURule(String[] rule)
	{
		String parent = rule[0].split("_")[0];
		String child = rule[2].split("_")[0];
		double score = Double.parseDouble(rule[3]);
		short index_pSubSym;
		short index_cSubSym;
		short numSymP = nonterminalTable.getNumSubsymbolArr().get(symbolIntValue(parent));
		short numSymC = nonterminalTable.getNumSubsymbolArr().get(symbolIntValue(child));
		if (numSymP == 1)
			index_pSubSym = 0;
		else
			index_pSubSym = Short.parseShort(rule[0].split("_")[1]);
		if (numSymC == 1)
			index_cSubSym = 0;
		else
			index_cSubSym = Short.parseShort(rule[2].split("_")[1]);
		UnaryRule uRule = new UnaryRule(symbolIntValue(parent), symbolIntValue(child));
		if (uRules.contains(uRule))
		{
			for (UnaryRule theRule : uRules)
			{
				if (uRule.equals(theRule))
				{
					uRule = theRule;
					break;
				}
			}
		}
		else
		{
			uRule.initScores(numSymP, numSymC);
			uRules.add(uRule);
		}
		uRule.setScore(index_pSubSym, index_cSubSym, score);
		return uRule;
	}

	public PreterminalRule readPreRule(String[] rule)
	{
		String parent = rule[0].split("_")[0];
		String word = rule[2];
		double score = Double.parseDouble(rule[3]);
		short index_pSubSym;
		short numSymP = nonterminalTable.getNumSubsymbolArr().get(symbolIntValue(parent));
		if (numSymP == 1)
			index_pSubSym = 0;
		else
			index_pSubSym = Short.parseShort(rule[0].split("_")[1]);
		PreterminalRule preRule = new PreterminalRule(symbolIntValue(parent), word);
		if (lexicon.getPreRules().contains(preRule))
		{
			for (PreterminalRule theRule : lexicon.getPreRules())
			{
				if (preRule.equals(theRule))
				{
					preRule = theRule;
					break;
				}
			}
		}
		else
		{
			preRule.initScores(numSymP);
			lexicon.getPreRules().add(preRule);
		}
		preRule.setScore(index_pSubSym, score);
		return preRule;
	}

	public void printRules()
	{
		for (BinaryRule rule : this.getbRules())
		{
			for (String str : rule.toStringRules(this))
			{
				System.out.println(str);
			}
		}
		for (UnaryRule rule : this.getuRules())
		{
			for (String str : rule.toStringRules(this))
			{
				System.out.println(str);
			}
		}
		for (PreterminalRule rule : this.getLexicon().getPreRules())
		{
			for (String str : rule.toStringRules(this))
			{
				System.out.println(str);
			}
		}
	}

	public short getNumSubSymbol(short symbol)
	{
		return nonterminalTable.getNumSubsymbolArr().get(symbol);
	}

	public boolean hasBRule(BinaryRule bRule)
	{
		return bRules.contains(bRule);
	}

	public boolean hasURule(UnaryRule uRule)
	{
		return uRules.contains(uRule);
	}

	public boolean hasPreRule(PreterminalRule preRule)
	{
		return lexicon.getPreRules().contains(preRule);
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

	public void setNontermianalTable(NonterminalTable nonterminalTable)
	{
		this.nonterminalTable = nonterminalTable;
	}

	public boolean hasNonterminalTable()
	{
		return !(this.nonterminalTable == null);
	}

	public String getStartSymbol()
	{
		return StartSymbol;
	}

	public void setStartSymbol(String startSymbol)
	{
		StartSymbol = startSymbol;
	}

	public short getNumSymbol()
	{
		return nonterminalTable.getNumSymbol();
	}

	public Short[] allNonterminalIntValArr()
	{
		return nonterminalTable.getInt_strMap().keySet().toArray(new Short[nonterminalTable.getInt_strMap().size()]);
	}

	public ArrayList<Short> allPreterminal()
	{
		return nonterminalTable.getIntValueOfPreterminalArr();
	}

	public ArrayList<Short> getNumSubsymbolArr()
	{
		return nonterminalTable.getNumSubsymbolArr();
	}

	public void setNumSubsymbolArr(ArrayList<Short> newNumSubsymbolArr)
	{
		nonterminalTable.setNumSubsymbolArr(newNumSubsymbolArr);
	}

	public void setbRules(HashSet<BinaryRule> bRules)
	{
		this.bRules = bRules;
	}

	public void setuRules(HashSet<UnaryRule> uRules)
	{
		this.uRules = uRules;
	}

	public void setLexicon(Lexicon lexicon)
	{
		this.lexicon = lexicon;
	}

	public static Random getRandom()
	{
		return random;
	}

	public HashMap<Integer, HashMap<PreterminalRule, PreterminalRule>> getPreRuleBySameChildren()
	{
		return preRuleBySameChildren;
	}

	public HashMap<Short, HashMap<Short, HashMap<BinaryRule, BinaryRule>>> getbRuleBySameChildren()
	{
		return bRuleBySameChildren;
	}

	public HashMap<Short, HashMap<UnaryRule, UnaryRule>> getuRuleBySameChildren()
	{
		return uRuleBySameChildren;
	}

	public HashMap<Short, HashMap<PreterminalRule, PreterminalRule>> getPreRuleBySameHead()
	{
		return preRuleBySameHead;
	}

	public Set<BinaryRule> getbRuleSetBySameHead(short parent)
	{
		if (bRuleBySameHead.containsKey(parent))
			return bRuleBySameHead.get(parent).keySet();
		else
			return null;
	}

	public BinaryRule getRule(BinaryRule tempBRule)
	{
		short parent = tempBRule.getParent();
		if (bRuleBySameHead.containsKey(parent))
			return bRuleBySameHead.get(parent).get(tempBRule);
		else
			return null;
	}

	public Set<UnaryRule> getuRuleSetBySameHead(short parent)
	{
		if (uRuleBySameHead.containsKey(parent))
			return uRuleBySameHead.get(parent).keySet();
		else
			return null;
	}

	public UnaryRule getRule(UnaryRule tempURule)
	{
		short parent = tempURule.getParent();
		if (uRuleBySameHead.containsKey(parent))
			return uRuleBySameHead.get(parent).get(tempURule);
		else
			return null;
	}

	public NonterminalTable getNonterminalTable()
	{
		return nonterminalTable;
	}

	public void setSubTag2UNKScores(double[][] subTag2UNKScores)
	{
		lexicon.setSubTag2UNKScores(subTag2UNKScores);
	}

	public double getSubtag2UNKScores(short tag, short subT)
	{
		return getTag2UNKScores(tag)[subT];
	}

	public double[] getTag2UNKScores(short tag)
	{
		return lexicon.getSubTag2UNKScores()[tag];
	}

	public boolean isRareWord(String word)
	{
		return lexicon.getRareWord().contains(word);
	}

	public boolean hasPreterminalSymbol(short preterminalSymbol)
	{
		return nonterminalTable.hasPreterminalSymbol(preterminalSymbol);
	}
}
