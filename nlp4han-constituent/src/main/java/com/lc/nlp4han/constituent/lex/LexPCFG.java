package com.lc.nlp4han.constituent.lex;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	// 词在训练集中的pos集合
	private HashMap<String, HashSet<String>> posesOfWord = new HashMap<String, HashSet<String>>();

	// P(H|P,t,w)）相关的统计数据
	private HashMap<RuleCollins, AmountAndSort> headGenMap = new HashMap<RuleCollins, AmountAndSort>();
	private HashMap<RuleCollins, HashSet<String>> parentList = new HashMap<RuleCollins, HashSet<String>>();

	// 用于生成SidesChild(包含其中心word和pos)相关统计数据
	private HashMap<RuleCollins, AmountAndSort> sidesGenMap = new HashMap<RuleCollins, AmountAndSort>();

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
		return posesOfWord.get(word);
	}

	/**
	 * 由headChild得到可行的Parent/后者向上延伸的单元规则
	 * 
	 * @return
	 */
	public HashSet<String> getParentSet(RuleHeadChildGenerate rhcg)
	{
		return parentList.get(rhcg);

	}

	/**
	 * 获取概率的通用方法
	 * 
	 * @param rule
	 * @param type
	 * @return
	 */
	public double getGeneratePro(RuleCollins rule, String type)
	{
		if (type.equals("head"))
		{
			return getProForGenerateHead((RuleHeadChildGenerate) rule);
		}
		else if (type.equals("sides"))
		{
			return getProForGenerateSides((RuleSidesGenerate) rule);
		}
		else
		{
			return getProForGenerateStop((RuleStopGenerate) rule);
		}
	}

	/**
	 * 得到P（H|P,(pos,word)）的概率/可以用于计算单元规则的概率
	 * 
	 * @return
	 */
	private double getProForGenerateHead(RuleHeadChildGenerate rhcg)
	{
		return getProOfBackOff(rhcg, headGenMap, "head");
	}

	/**
	 * 得到生成Pl/Pr(Stop|P,H,(t,w),distance)的概率
	 * 
	 * @param rsg
	 * @return
	 */
	private double getProForGenerateStop(RuleStopGenerate rsg)
	{
		return getProOfBackOff(rsg, stopGenMap, "stop");
	}

	/**
	 * 得到生成两侧即Pl/Pr(L(lpos,lword)c,p|P,H,(hpos,hword),distance)的概率c和p分别指CC、标点符号（此处专指顿号）
	 * 
	 * @param sidesRule
	 * @return
	 */
	private double getProForGenerateSides(RuleSidesGenerate sr)
	{
		// 需要进行平滑运算将其分为两部分

		String parentLabel = sr.getParentLabel();// 父节点的非终结符标记
		String headPOS = sr.getHeadPOS();// 中心词词性标记,在NPB中为上一个修饰符的pos
		String headWord = sr.getHeadWord();// 中心词,在NPB中为上一个修饰符的word
		String headLabel = sr.getHeadLabel();// 中心节点标记
		int direction = sr.getDirection();// 头结点为0,左侧为1，右侧为2
		String sideLabel = sr.getSideLabel();// 所求孩子节点的标记
		String sideHeadPOS = sr.getSideHeadPOS();// 所求孩子节点的中心词词标记
		String sideHeadWord = sr.getSideHeadWord();// 所求的孩子节点的中心词
		int coor = sr.getCoor();// 并列结构
		int pu = sr.getPu();// 标点符号，由于只保留了顿号所以我们可以把它当做并列结构
		Distance distance = sr.getDistance();// 距离度量

		RuleSidesGenerate rule1 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, headWord, direction, sideLabel,
				sideHeadPOS, null, coor, pu, distance);
		RuleSidesGenerate rule2 = new RuleSidesGenerate(headLabel, parentLabel, headPOS, headWord, direction, sideLabel,
				sideHeadPOS, sideHeadWord, coor, pu, distance);
		return getProOfBackOff(rule1, sidesGenMap, "1side") * getProOfBackOff(rule2, sidesGenMap, "2side");
	}

	/**
	 * 通过回退模型得到概率
	 * 
	 * @param rule
	 * @param map
	 * @return
	 */
	private double getProOfBackOff(RuleCollins rule, HashMap<RuleCollins, AmountAndSort> map, String type)
	{
		double e1, e2, e3, w1, w2;
		double[] pw1 = getProAndWeight(rule, map, type);
		e1 = pw1[0];
		w1 = pw1[1];

		rule.setHeadPOS(null);
		double[] pw2 = getProAndWeight(rule, map, type);
		e2 = pw2[0];
		w2 = pw2[1];

		rule.setHeadWord(null);
		if (type.equals("2side"))
		{
			RuleSidesGenerate sideRule = (RuleSidesGenerate) rule;
			e3 = wordMap.get(new WordAndPOS(sideRule.getHeadWord(), sideRule.getHeadPOS()))
					/ wordMap.get(new WordAndPOS(null, sideRule.getHeadPOS()));
		}
		else
		{
			double[] pw3 = getProAndWeight(rule, map, type);
			e3 = pw3[0];
		}

		return w1 * e1 + (1 - w1) * (w2 * e2 + (1 - w2) * e3);
	}

	/**
	 * 得到某个回退模型的概率和权重
	 * 
	 * @return
	 */
	private double[] getProAndWeight(RuleCollins rhcg, HashMap<RuleCollins, AmountAndSort> map, String type)
	{
		int x,y,u;
		double[] pw = new double[2];
		if (map.get(rhcg) == null)
		{
			pw[0] = 0;
			pw[1] = 0;
		}
		else
		{
			x = map.get(rhcg).getAmount();
			switch(type) {
			case "head":
				   RuleHeadChildGenerate rhcg1=(RuleHeadChildGenerate)rhcg;
				   rhcg1.setHeadLabel(null);
					y = map.get(rhcg1).getAmount();
					u = map.get(rhcg1).getSort();
				   break;
			case "1side":
				RuleSidesGenerate rsg1=(RuleSidesGenerate)rhcg;
				   rsg1.setSideLabel(null);
				   rsg1.setSideHeadPOS(null);
					y = map.get(rsg1).getAmount();
					u = map.get(rsg1).getSort();
				   break;
			case "2side":
				RuleSidesGenerate rsg2=(RuleSidesGenerate)rhcg;
				  rsg2.setSideHeadWord(null);
					y = map.get(rsg2).getAmount();
					u = map.get(rsg2).getSort();
				   break;
			case "stop":
				RuleStopGenerate rsg3=(RuleStopGenerate)rhcg;
				   rsg3.setHeadLabel(null);
					y = map.get(rsg3).getAmount();
					u = map.get(rsg3).getSort();
				   break;
			default:
				y=0;
				u=0;
			}
			pw[0] = 1.0 * x / (y + 5 * u);
			pw[1] = 1.0 * x / y;
		}
		return pw;
	}

	/**
	 * 得到生成NPB(基本名词短语)两侧的概率 即Pl/Pr(L(lpos,lword)|P,preModifer,preM(pos,word))
	 * 
	 * @param sidesRule
	 * @return
	 */
	public double getProForGenerateNPBSides(RuleSidesGenerate srOfNPB)
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

	public HashMap<String, HashSet<String>> getPosesOfWord()
	{
		return posesOfWord;
	}

	public void setPosesOfWord(HashMap<String, HashSet<String>> posesOfWord)
	{
		this.posesOfWord = posesOfWord;
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
		return sidesGenMap;
	}

	public void setSidesGeneratorMap(HashMap<RuleCollins, AmountAndSort> sidesGenMap)
	{
		this.sidesGenMap = sidesGenMap;
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

	@Override
	public String toString()
	{
		StringBuilder stb = new StringBuilder();
		stb.append("--起始符--" + '\n');
		stb.append(this.getStartSymbol() + '\n');

		Iterator<String> itr1 = posSet.iterator();
		stb.append("--词性标注集--" + '\n');
		while (itr1.hasNext())
		{
			stb.append(itr1.next() + '\n');
		}

		Set<WordAndPOS> wap = wordMap.keySet();
		stb.append("--POS-Word集--" + '\n');
		for (WordAndPOS wap1 : wap)
		{
			stb.append(wap1.toString() + " " + wordMap.get(wap1) + '\n');
		}
		stb.append("--生成头结点的规则集--" + '\n');
		Set<RuleCollins> set = headGenMap.keySet();
		for (RuleCollins rule : set)
		{
			RuleHeadChildGenerate rule1 = (RuleHeadChildGenerate) rule;
			stb.append(rule1.toString() + " " + headGenMap.get(rule1).toString() + '\n');
		}

		stb.append("--头结点向上延伸的标记集--" + '\n');
		Set<RuleCollins> set1 = parentList.keySet();
		for (RuleCollins rule : set1)
		{
			RuleHeadChildGenerate rule1 = (RuleHeadChildGenerate) rule;
			stb.append(rule1.toString() + " " + parentList.get(rule1).toString() + '\n');
		}

		stb.append("--生成两侧孩子的规则集--" + '\n');
		Set<RuleCollins> set2 = sidesGenMap.keySet();
		for (RuleCollins rule : set2)
		{
			RuleHeadChildGenerate rule1 = (RuleHeadChildGenerate) rule;
			stb.append(rule1.toString() + " " + sidesGenMap.get(rule1).toString() + '\n');
		}

		stb.append("--生成两侧Stop的规则集--" + '\n');
		Set<RuleCollins> set3 = stopGenMap.keySet();
		for (RuleCollins rule : set3)
		{
			RuleHeadChildGenerate rule1 = (RuleHeadChildGenerate) rule;
			stb.append(rule1.toString() + " " + stopGenMap.get(rule1).toString() + '\n');
		}

		stb.append("--特殊规则集--" + '\n');
		Set<RuleCollins> set4 = specialGenMap.keySet();
		for (RuleCollins rule : set4)
		{
			RuleHeadChildGenerate rule1 = (RuleHeadChildGenerate) rule;
			stb.append(rule1.toString() + " " + specialGenMap.get(rule1).toString() + '\n');
		}
		return stb.toString();
	}
}
