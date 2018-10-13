package com.lc.nlp4han.constituent.maxent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;
import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 短语结构树样本流
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ConstituentTreeSampleStream extends FilterObjectStream<String, ConstituentTreeSample>
{

	private Logger logger = Logger.getLogger(ConstituentTreeSampleStream.class.getName());
	private AbstractHeadGenerator headGenerator;

	/**
	 * 构造
	 * 
	 * @param samples
	 *            样本流
	 */
	public ConstituentTreeSampleStream(ObjectStream<String> samples, AbstractHeadGenerator headGen)
	{
		super(samples);
		this.headGenerator = headGen;
	}

	/**
	 * 读取样本进行解析
	 * 
	 * @return
	 */
	@Override
	public ConstituentTreeSample read() throws IOException
	{
		String sentence = samples.read();
		ConstituentTreeSample sample = null;
		if (sentence != null)
		{
			if (sentence.compareTo("") != 0)
			{
				try
				{
					TreeNode tree = BracketExpUtil.generateTreeNoTopBracket(sentence);
					
					HeadTreeNode headtree = TreeToHeadTree.treeToHeadTree(tree, headGenerator);
					
					sample = HeadTreeToSample.headTreeToSample(headtree, headGenerator);
				}
				catch (Exception e)
				{
					if (logger.isLoggable(Level.WARNING))
					{
						logger.warning("Error during parsing, ignoring sentence: " + sentence);
					}
					sample = new ConstituentTreeSample(new ArrayList<>(), new ArrayList<>(),
							new ArrayList<>(), new ArrayList<>());
				}
				
				return sample;
			}
			else
			{
				sample = new ConstituentTreeSample(new ArrayList<>(), new ArrayList<>(),
						new ArrayList<>(), new ArrayList<>());
				return sample;
			}
		}
		else
		{
			return null;
		}
	}
}
