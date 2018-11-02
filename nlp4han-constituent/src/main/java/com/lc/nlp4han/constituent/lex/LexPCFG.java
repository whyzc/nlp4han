package com.lc.nlp4han.constituent.lex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 中心词驱动的上下无关文法（Collins Model1）
 * 
 * @author qyl
 *
 */
public class LexPCFG
{
	// 句法树的起始符，该值为预处理句子时设置的起始符ROOT
	private String StartSymbol = null;

	// 词性标注集
	private HashSet<String> posSet = new HashSet<String>();

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
	 * @throws IOException
	 */
	public LexPCFG(InputStream in, String encoding) throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in, encoding));
		String str = buffer.readLine().trim();
		if (str.equals("--起始符--"))
		{
			setStartSymbol(buffer.readLine().trim());
		}
		buffer.readLine();
		str = buffer.readLine().trim();
		while (!str.equals("--POS-Word集--"))
		{// 添加词性标注集
			posSet.add(str);
			str = buffer.readLine().trim();
		}
		str = buffer.readLine();
		while (!str.equals("--生成头结点的规则集--"))
		{// POS-Word集
			String[] strs = str.split(" ");
			wordMap.put(new WordAndPOS(strs[0], strs[1]), Integer.parseInt(strs[2]));
			str = buffer.readLine().trim();
		}
		str = buffer.readLine();
		while (!str.equals("--头结点向上延伸的标记集--"))
		{// 生成头结点的规则集
			String[] strs = str.split(" ");
			int amount = Integer.parseInt(strs[strs.length - 2]);
			int sort = Integer.parseInt(strs[strs.length - 1]);
			headGenMap.put(new RuleHeadChildGenerate(strs), new AmountAndSort(amount, sort));
			str = buffer.readLine();
		}
		str = buffer.readLine();
		while (!str.equals("--生成两侧孩子的规则集--"))
		{// 头结点向上延伸的标记集
			String[] strs = str.split(" ");
			HashSet<String> set = new HashSet<String>();
			for (int i = 4; i < strs.length; i++)
			{
				set.add(strs[i]);
			}
			parentList.put(new RuleHeadChildGenerate(strs), set);
			str = buffer.readLine();
		}
		str = buffer.readLine();
		while (!str.equals("--生成两侧Stop的规则集--"))
		{// 生成两侧孩子的规则集
			String[] strs = str.split(" ");
			int amount = Integer.parseInt(strs[strs.length - 2]);
			int sort = Integer.parseInt(strs[strs.length - 1]);
			sidesGenMap.put(new RuleSidesGenerate(strs), new AmountAndSort(amount, sort));
			str = buffer.readLine();
		}
		str = buffer.readLine();
		while (!str.equals("--特殊规则集--"))
		{// 生成两侧Stop的规则集
			String[] strs = str.split(" ");
			int amount = 1;
			try
			{
				amount = Integer.parseInt(strs[strs.length - 2]);
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				for (String str1 : strs)
				{
					System.out.print(str1);
				}
			}
			int sort = Integer.parseInt(strs[strs.length - 1]);
			stopGenMap.put(new RuleStopGenerate(strs), new AmountAndSort(amount, sort));
			str = buffer.readLine();
		}
		str = buffer.readLine();
		while (str != null)
		{// 特殊规则集
			System.out.println("空的出现错误");
			String[] strs = str.split(" ");
			int amount = Integer.parseInt(strs[strs.length - 2]);
			int sort = Integer.parseInt(strs[strs.length - 1]);
			specialGenMap.put(new RuleSpecialCase(strs), new AmountAndSort(amount, sort));
			str = buffer.readLine();
		}
		buffer.close();
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
		else if (type.equals("stop"))
		{
			return getProForGenerateStop((RuleStopGenerate) rule);
		}
		else
		{
			return getProForSpecialCase((RuleSpecialCase) rule);
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
	 * 得到并列结构（CC）或者含有顿号结构的概率
	 * 即P(CC,word|P,leftLabel,rightLabel,leftWord,righrWord)的概率
	 * 
	 * @param specialRule
	 * @return
	 */
	public double getProForSpecialCase(RuleSpecialCase specialRule)
	{
		return getProOfBackOff(specialRule, specialGenMap, "special");
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
		e3 = 0;

		double[] pw1 = getProAndWeight(rule, map, type);
		e1 = pw1[1];
		w1 = pw1[0];

		rule.setHeadWord(null);
		double[] pw2 = getProAndWeight(rule, map, type);
		e2 = pw2[1];
		w2 = pw2[0];

		rule.setHeadPOS(null);
		if (type.equals("2side"))
		{
			RuleSidesGenerate rs = (RuleSidesGenerate) rule;
			double i = 1;
			WordAndPOS wop = new WordAndPOS(rs.getSideHeadWord(), rs.getSideHeadPOS());
			if (wordMap.keySet().contains(wop))
			{
				i = wordMap.get(wop);
			}
			if (wordMap.containsKey(new WordAndPOS(null, rs.getSideHeadPOS())))
			{
				e3 = i / wordMap.get(new WordAndPOS(null, rs.getSideHeadPOS()));
			}
		}
		else
		{
			double[] pw3 = getProAndWeight(rule, map, type);
			e3 = pw3[1];
		}

		// 因为在收集文法时，规则相同，则计数累加，故需要乘以0.5
		if (type.equals("lside"))
		{
			e1 *= 0.5;
			e2 *= 0.5;
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
		int x, u, y;
		double[] pw = new double[2];
		y = 0;
		if (map.get(rhcg) == null)
		{
			// pw[0] = 0;
			pw[1] = 0;
		}
		else
		{
			x = map.get(rhcg).getAmount();
			switch (type)
			{
			case "head":
				RuleHeadChildGenerate rhcg1 = (RuleHeadChildGenerate) rhcg;
				rhcg1 = new RuleHeadChildGenerate(null, rhcg1.getParentLabel(), rhcg1.getHeadPOS(),
						rhcg1.getHeadWord());
				y = map.get(rhcg1).getAmount();
				u = map.get(rhcg1).getSort();
				break;
			case "1side":
				RuleSidesGenerate rsg1 = (RuleSidesGenerate) rhcg;
				rsg1 = new RuleSidesGenerate(rsg1.getHeadLabel(), rsg1.getParentLabel(), rsg1.getHeadPOS(),
						rsg1.getHeadWord(), rsg1.getDirection(), null, null, rsg1.getSideHeadWord(), rsg1.getCoor(),
						rsg1.getPu(), rsg1.getDistance());
				y = map.get(rsg1).getAmount();
				u = map.get(rsg1).getSort();
				break;
			case "2side":
				RuleSidesGenerate rsg2 = (RuleSidesGenerate) rhcg;
				rsg2 = new RuleSidesGenerate(rsg2.getHeadLabel(), rsg2.getParentLabel(), rsg2.getHeadPOS(),
						rsg2.getHeadWord(), rsg2.getDirection(), rsg2.getSideLabel(), rsg2.getSideHeadPOS(), null,
						rsg2.getCoor(), rsg2.getPu(), rsg2.getDistance());

				// 因为此处统计的次数为1side的分子和2side的分母，故统计了两次，所以此处需要乘以0.5
				y = map.get(rsg2).getAmount() * 1 / 2;
				u = map.get(rsg2).getSort();
				break;
			case "stop":
				y = 0;
				RuleStopGenerate rsg3 = (RuleStopGenerate) rhcg;
				rsg3 = new RuleStopGenerate(rsg3.getHeadLabel(), rsg3.getParentLabel(), rsg3.getHeadPOS(),
						rsg3.getHeadWord(), rsg3.getDirection(), true, rsg3.getDistance());
				if (map.containsKey(rsg3))
				{
					y += map.get(rsg3).getAmount();
				}
				rsg3.setStop(false);
				if (map.containsKey(rsg3))
				{
					y += map.get(rsg3).getAmount();
				}
				u = 2;
				break;
			case "special":
				RuleSpecialCase rsg4 = (RuleSpecialCase) rhcg;
				rsg4 = new RuleSpecialCase(rsg4.getParentLabel(), null, null, rsg4.getLeftLabel(), rsg4.getRightLabel(),
						rsg4.getLheadWord(), rsg4.getRheadWord(), rsg4.getLheadPOS(), rsg4.getRheadPOS());
				y = map.get(rsg4).getAmount();
				u = map.get(rsg4).getSort();
				break;
			default:
				y = 0;
				u = 0;
			}
			// 若果分母为零，则回退模型的权重值位零
			if (y == 0)
			{
				pw[0] = 0;
			}
			else
			{
				pw[0] = 1.0 * y / (y + 5 * u);
			}
			pw[1] = 1.0 * x / y;
		}
		return pw;
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
			stb.append(rule1.toString() + " ");
			for (String str : parentList.get(rule1))
			{
				stb.append(str.toString() + " ");
			}
			stb.append('\n');
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((StartSymbol == null) ? 0 : StartSymbol.hashCode());
		result = prime * result + ((headGenMap == null) ? 0 : headGenMap.hashCode());
		result = prime * result + ((parentList == null) ? 0 : parentList.hashCode());
		result = prime * result + ((posSet == null) ? 0 : posSet.hashCode());
		result = prime * result + ((posesOfWord == null) ? 0 : posesOfWord.hashCode());
		result = prime * result + ((sidesGenMap == null) ? 0 : sidesGenMap.hashCode());
		result = prime * result + ((specialGenMap == null) ? 0 : specialGenMap.hashCode());
		result = prime * result + ((stopGenMap == null) ? 0 : stopGenMap.hashCode());
		result = prime * result + ((wordMap == null) ? 0 : wordMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LexPCFG other = (LexPCFG) obj;
		if (StartSymbol == null)
		{
			if (other.StartSymbol != null)
				return false;
		}
		else if (!StartSymbol.equals(other.StartSymbol))
			return false;
		if (headGenMap == null)
		{
			if (other.headGenMap != null)
				return false;
		}
		else if (!headGenMap.equals(other.headGenMap))
			return false;
		if (parentList == null)
		{
			if (other.parentList != null)
				return false;
		}
		else if (!parentList.equals(other.parentList))
			return false;
		if (posSet == null)
		{
			if (other.posSet != null)
				return false;
		}
		else if (!posSet.equals(other.posSet))
			return false;
		if (posesOfWord == null)
		{
			if (other.posesOfWord != null)
				return false;
		}
		else if (!posesOfWord.equals(other.posesOfWord))
			return false;
		if (sidesGenMap == null)
		{
			if (other.sidesGenMap != null)
				return false;
		}
		else if (!sidesGenMap.equals(other.sidesGenMap))
			return false;
		if (specialGenMap == null)
		{
			if (other.specialGenMap != null)
				return false;
		}
		else if (!specialGenMap.equals(other.specialGenMap))
			return false;
		if (stopGenMap == null)
		{
			if (other.stopGenMap != null)
				return false;
		}
		else if (!stopGenMap.equals(other.stopGenMap))
			return false;
		if (wordMap == null)
		{
			if (other.wordMap != null)
				return false;
		}
		else if (!wordMap.equals(other.wordMap))
			return false;
		return true;
	}

}
