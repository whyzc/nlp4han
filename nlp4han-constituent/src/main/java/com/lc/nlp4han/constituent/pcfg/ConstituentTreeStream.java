package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;

public class ConstituentTreeStream extends FilterObjectStream<String, ConstituentTree>
{

	private Logger logger = Logger.getLogger(ConstituentTreeStream.class.getName());

	/**
	 * 构造
	 * 
	 * @param samples
	 *            样本流
	 */
	public ConstituentTreeStream(ObjectStream<String> samples)
	{
		super(samples);
	}

	/**
	 * 读取样本进行解析
	 * 
	 * @return
	 */
	@Override
	public ConstituentTree read() throws IOException
	{
		String sentence = samples.read();
		ConstituentTree sample = null;
		if (sentence != null)
		{
			if (sentence.compareTo("") != 0)
			{
				try
				{
					TreeNode tree = BracketExpUtil.generateTree(sentence);
					sample = new ConstituentTree(tree);
				}
				catch (Exception e)
				{
					if (logger.isLoggable(Level.WARNING))
					{
						logger.warning("Error during parsing, ignoring sentence: " + sentence);
					}
					sample = new ConstituentTree();
				}
				return sample;
			}
			else
			{
				sample = new ConstituentTree();
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
