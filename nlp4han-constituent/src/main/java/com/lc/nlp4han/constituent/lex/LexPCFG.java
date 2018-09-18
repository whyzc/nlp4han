package com.lc.nlp4han.constituent.lex;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 中心词驱动的上下无关文法，也就是Collins Model1
 * 
 * @author qyl
 *
 */
public class LexPCFG
{
	// 词性标注集
	private HashSet<String> posSet = new HashSet<String>();
	// 句法树的起始符，该值为预处理句子时设置的起始符ROOT
	private String StartSymbol = null;

	// 词性标注和词，以及数目
	private HashMap<WordAndPOS, Integer> wordMap = new HashMap<WordAndPOS, Integer>();
	private HashMap<String, HashSet<String>> wordMapPos = new HashMap<String, HashSet<String>>();

	// P(H|P,t,w)）相关的统计数据
	private HashMap<RuleCollins, AmountAndSort> headGenMap = new HashMap<RuleCollins, AmountAndSort>();
	private HashMap<RuleCollins, HashSet<String>> parentList = new HashMap<RuleCollins, HashSet<String>>();

	// 用于生成headChild(包含其中心word和pos)相关统计数据
	private HashMap<RuleCollins, AmountAndSort> sidesGeneratorMap = new HashMap<RuleCollins, AmountAndSort>();
	
	// 用于生成Stop的相关统计数据
	private HashMap<RuleCollins, AmountAndSort> stopGenMap = new HashMap<RuleCollins, AmountAndSort>();

	// 用于生成并列结构连词（如CC或者逗号和冒号,为简略，我将生成修饰符pos和生成修饰符word都放入此规则
	private HashMap<RuleCollins, AmountAndSort> specialGenMap = new HashMap<RuleCollins, AmountAndSort>();

	public LexPCFG()
	{

	}

	/**
	 * 从流中读取数据
	 * 
	 * @param in
	 * @param encoding
	 */
	public LexPCFG(InputStream in, String encoding)
	{

	}

	/**
	 * 得到词性标注集合
	 * 
	 * @return
	 */
	public HashSet<String> getPosSet()
	{
		return posSet;
	}

	/**
	 * 得到词在训练数据集中出现的pos集合
	 * 
	 * @param word
	 * @return
	 */
	public HashSet<String> getposSetByword(String word)
	{
		return wordMapPos.get(word);
	}

	/**
	 * 由headChild得到可行的Parent/后者向上延伸的单元规则
	 * 
	 * @return
	 */
	public HashSet<String> getParentSet(RuleHeadChildGenerate rhcg)
	{
		return null;

	}

	/**
	 * 得到P（H|P,(pos,word)）的概率/可以用于计算单元规则的概率
	 * 
	 * @return
	 */
	public double getProForGenerateHead(RuleHeadChildGenerate rhcg)
	{
		return 1.0;
	}

	/**
	 * 得到生成Pl/Pr(Stop|P,H,(t,w),distance)的概率
	 * 
	 * @param rsg
	 * @return
	 */
	public double getProForGenerateStop(RuleStopGenerate rsg)
	{
		return 1.0;
	}

	/**
	 * 得到生成两侧即Pl/Pr(L(lpos,lword)c,p|P,H,(hpos,hword),distance)的概率c和p分别指CC、标点符号（此处专指顿号）
	 * 
	 * @param sidesRule
	 * @return
	 */
	public double getProForGenerateSides(RuleSidesGenerate sidesRule)
	{
		return 1.0;
	}

	/**
	 * 得到生成NPB(基本名词短语)两侧的概率 即Pl/Pr(L(lpos,lword)|P,preModifer,preM(pos,word))
	 * 
	 * @param sidesRule
	 * @return
	 */
	public double getProForGenerateNPBSides(RuleSidesGenerate sidesRule)
	{
		return 1.0;
	}

	/**
	 * 得到并列结构（CC）或者含有顿号结构的概率
	 * 即P(CC,word|P,leftLabel,rightLabel,leftWord,righrWord)的概率
	 * 
	 * @param specialRule
	 * @return
	 */
	public double getProForSpecialCase(RuleSpecialCase specialRule)
	{
		return 1.0;
	}

	/**
	 * 得到用于平滑运算的λ值
	 * 
	 * @param f
	 * @param u
	 * @return
	 */
	public double getProByPOS(int f, int u)
	{
		return 1.0;
	}

	public String getStartSymbol()
	{
		return StartSymbol;
	}

	public void setStartSymbol(String startSymbol)
	{
		StartSymbol = startSymbol;
	}

	public HashMap<WordAndPOS, Integer> getWordMap()
	{
		return wordMap;
	}

	public void setWordMap(HashMap<WordAndPOS, Integer> wordMap)
	{
		this.wordMap = wordMap;
	}

	public HashMap<String, HashSet<String>> getWordMapPos()
	{
		return wordMapPos;
	}

	public void setWordMapPos(HashMap<String, HashSet<String>> wordMapPos)
	{
		this.wordMapPos = wordMapPos;
	}

	public HashMap<RuleCollins, AmountAndSort> getHeadGenMap()
	{
		return headGenMap;
	}

	public void setHeadGenMap(HashMap<RuleCollins, AmountAndSort> headGenMap)
	{
		this.headGenMap = headGenMap;
	}

	public HashMap<RuleCollins, HashSet<String>> getParentList()
	{
		return parentList;
	}

	public void setParentList(HashMap<RuleCollins, HashSet<String>> parentList)
	{
		this.parentList = parentList;
	}

	public HashMap<RuleCollins, AmountAndSort> getSidesGeneratorMap()
	{
		return sidesGeneratorMap;
	}

	public void setSidesGeneratorMap(HashMap<RuleCollins, AmountAndSort> sidesGeneratorMap)
	{
		this.sidesGeneratorMap = sidesGeneratorMap;
	}

	public HashMap<RuleCollins, AmountAndSort> getStopGenMap()
	{
		return stopGenMap;
	}

	public void setStopGenMap(HashMap<RuleCollins, AmountAndSort> stopGenMap)
	{
		this.stopGenMap = stopGenMap;
	}

	public HashMap<RuleCollins, AmountAndSort> getSpecialGenMap()
	{
		return specialGenMap;
	}

	public void setSpecialGenMap(HashMap<RuleCollins, AmountAndSort> specialGenMap)
	{
		this.specialGenMap = specialGenMap;
	}

	public void setPosSet(HashSet<String> posSet)
	{
		this.posSet = posSet;
	} 
}
