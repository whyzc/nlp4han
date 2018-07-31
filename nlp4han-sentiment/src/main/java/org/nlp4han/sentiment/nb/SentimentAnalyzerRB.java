package org.nlp4han.sentiment.nb;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nlp4han.sentiment.SentimentAnalyzer;
import org.nlp4han.sentiment.SentimentPolarity;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * 基于组合规则来判断树的极性
 * 
 * @author lim
 *
 */
public class SentimentAnalyzerRB implements SentimentAnalyzer
{
	private Map<String, String> dictionary = new HashMap<>();

	public SentimentAnalyzerRB(String dicPath, String encoding) throws IOException
	{
		init(dicPath, encoding);
	}

	/**
	 * 初始化字典
	 * 
	 * @param dicPath
	 * @param encoding
	 * @throws IOException 
	 */
	private void init(String dicPath, String encoding) throws IOException
	{
			FileInputStream fr = new FileInputStream(dicPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fr,encoding));
			String str = "";
			while ((str = br.readLine()) != null)
			{
				String[] items = str.trim().split(",");
				dictionary.put(items[0], items[1]);
			}
			br.close();
	}

	/**
	 * 将括号表达式转换成情感极性树
	 * 
	 * @param bracketStr
	 *            括号表达式
	 * @return
	 */
	public TreeNode parse(String bracketStr)
	{
		TreeNode tree = BracketExpUtil.generateTree(bracketStr);
		return this.parse(tree);
	}

	/**
	 * 将短语结构树转换成情感极性树
	 * 
	 * @param phraseTree
	 *            短语结构树
	 * @return
	 */
	public TreeNode parse(TreeNode phraseTree)
	{
		int num = 0;
		if (phraseTree != null)
		{
			num = phraseTree.getChildrenNum();
			for (int i = 0; i < num; i++)
			{
				parse(phraseTree.getChild(i));
			}

			if (phraseTree.isLeaf())
			{

				String polarity = getNodePolarity(phraseTree.getNodeName());
				phraseTree.getParent().setNewName(polarity);// 对于叶子节点，则将其父节点的名称改为其极性
				phraseTree.setFlag(false);
				phraseTree.getParent().setFlag(false);// 借助flag变量来标记该节点的极性信息已经被解析了

			}
			else
			{
				int numPositive = 0;
				int numNegative = 0;
				
				boolean privative = false;

				if (phraseTree.getFlag())
				{
					for (int i = 0; i < num; i++)
					{
						String childPolarity = phraseTree.getChildName(i);
						if ("+1".equals(childPolarity))
						{
							numPositive++;
						}
						if ("-1".equals(childPolarity))
						{
							numNegative++;
						}
						//表否定
						if ("p".equals(childPolarity))
						{
							privative = true;
						}

					}

					if (numPositive > numNegative)
					{
						phraseTree.setNewName("+1");
						phraseTree.setFlag(false);
					}
					else if (numPositive == numNegative)
					{
						phraseTree.setNewName("0");
						phraseTree.setFlag(false);
					}
					else
					{
						phraseTree.setNewName("-1");
						phraseTree.setFlag(false);
					}
					
					//处理否定
					if (privative)
					{
						if (num==1 )
						{
							phraseTree.setNewName("p");
						}
						else
						{
							if ("-1".equals(phraseTree.getNodeName()))
							{
								phraseTree.setNewName("+1");
							}
							else
							{
								phraseTree.setNewName("-1");
							}
						}
					}
				}
			}

		}
		return phraseTree;
	}

	private String getNodePolarity(String content)
	{
		Set<String> key = dictionary.keySet();
		String nodePola = "0";
		if (key.contains(content)) 
		{
			nodePola = dictionary.get(content);
		}
		return nodePola;
	}

	@Override
	public SentimentPolarity analyze(String text, Map<String, Object> extraInformation)
	{
		TreeNode tn = this.parse(text);
		String polarity = tn.getNodeName();
		
		return new SentimentPolarity(polarity);
	}

	@Override
	public SentimentPolarity analyze(String text)
	{
		Map<String, Object> extraInfo = new HashMap<>();
		return this.analyze(text, extraInfo);
	}

}
