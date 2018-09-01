package com.lc.nlp4han.constituent.maxent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.lc.nlp4han.constituent.HeadTreeNode;

/**
 * 根据配置文件生成特征
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class ParserContextGeneratorConf implements ParserContextGenerator
{

	// chunk
	private boolean chunkandpostag0Set;
	private boolean chunkandpostag_1Set;
	private boolean chunkandpostag_2Set;
	private boolean chunkandpostag1Set;
	private boolean chunkandpostag2Set;
	private boolean chunkandpostag0ASet;
	private boolean chunkandpostag_1ASet;
	private boolean chunkandpostag_2ASet;
	private boolean chunkandpostag1ASet;
	private boolean chunkandpostag2ASet;
	private boolean chunkandpostag_10Set;
	private boolean chunkandpostag_1A0Set;
	private boolean chunkandpostag_1A0ASet;
	private boolean chunkandpostag_10ASet;
	private boolean chunkandpostag01Set;
	private boolean chunkandpostag0A1Set;
	private boolean chunkandpostag0A1ASet;
	private boolean chunkandpostag01ASet;
	private boolean chunkdefaultSet;

	// build
	private boolean cons0Set;
	private boolean cons_1Set;
	private boolean cons_2Set;
	private boolean cons1Set;
	private boolean cons2Set;
	private boolean cons0ASet;
	private boolean cons_1ASet;
	private boolean cons_2ASet;
	private boolean cons1ASet;
	private boolean cons2ASet;
	private boolean cons_10Set;
	private boolean cons_1A0Set;
	private boolean cons_1A0ASet;
	private boolean cons_10ASet;
	private boolean cons01Set;
	private boolean cons0A1Set;
	private boolean cons0A1ASet;
	private boolean cons01ASet;
	private boolean cons_2_10Set;
	private boolean cons_2A_1A0ASet;
	private boolean cons_2A_1A0Set;
	private boolean cons_2A_10Set;
	private boolean cons_2_1A0Set;
	private boolean cons012Set;
	private boolean cons0A1A2ASet;
	private boolean cons01A2ASet;
	private boolean cons01A2Set;
	private boolean cons012ASet;
	private boolean cons_101Set;
	private boolean cons_1A0A1ASet;
	private boolean cons_1A01ASet;
	private boolean cons_101ASet;
	private boolean cons_1A01Set;
	private boolean punctuationSet;
	private boolean builddefaultSet;

	// check
	private boolean checkcons_lastSet;
	private boolean checkcons_lastASet;
	private boolean checkcons_beginSet;
	private boolean checkcons_beginASet;
	private boolean checkcons_ilastSet;
	private boolean checkcons_iAlastSet;
	private boolean checkcons_ilastASet;
	private boolean checkcons_iAlastASet;
	private boolean productionSet;
	private boolean surround1Set;
	private boolean surround1ASet;
	private boolean surround2Set;
	private boolean surround2ASet;
	private boolean surround_1Set;
	private boolean surround_1ASet;
	private boolean surround_2Set;
	private boolean surround_2ASet;
	private boolean checkdefaultSet;

	/**
	 * 无参构造
	 * 
	 * @throws IOException
	 */
	public ParserContextGeneratorConf() throws IOException
	{

		Properties featureConf = new Properties();
		InputStream featureStream = ParserContextGeneratorConf.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/constituent/maxent/feature.properties");
		featureConf.load(featureStream);
		init(featureConf);
	}

	/**
	 * 有参构造
	 * 
	 * @param properties
	 *            配置文件
	 */
	public ParserContextGeneratorConf(Properties properties)
	{
		init(properties);
	}

	/**
	 * 根据配置文件中的信息初始化变量
	 * 
	 * @param properties
	 */
	private void init(Properties config)
	{

		// chunk
		chunkandpostag0Set = (config.getProperty("tree.chunkandpostag0", "true").equals("true"));
		chunkandpostag1Set = (config.getProperty("tree.chunkandpostag1", "true").equals("true"));
		chunkandpostag2Set = (config.getProperty("tree.chunkandpostag2", "true").equals("true"));
		chunkandpostag_1Set = (config.getProperty("tree.chunkandpostag_1", "true").equals("true"));
		chunkandpostag_2Set = (config.getProperty("tree.chunkandpostag_2", "true").equals("true"));
		chunkandpostag0ASet = (config.getProperty("tree.chunkandpostag0*", "true").equals("true"));
		chunkandpostag1ASet = (config.getProperty("tree.chunkandpostag1*", "true").equals("true"));
		chunkandpostag2ASet = (config.getProperty("tree.chunkandpostag2*", "true").equals("true"));
		chunkandpostag_1ASet = (config.getProperty("tree.chunkandpostag_1*", "true").equals("true"));
		chunkandpostag_2ASet = (config.getProperty("tree.chunkandpostag_2*", "true").equals("true"));
		chunkandpostag_10Set = (config.getProperty("tree.chunkandpostag_10", "true").equals("true"));
		chunkandpostag_1A0Set = (config.getProperty("tree.chunkandpostag_1*0", "true").equals("true"));
		chunkandpostag_1A0ASet = (config.getProperty("tree.chunkandpostag_1*0*", "true").equals("true"));
		chunkandpostag_10ASet = (config.getProperty("tree.chunkandpostag_10*", "true").equals("true"));
		chunkandpostag01Set = (config.getProperty("tree.chunkandpostag01", "true").equals("true"));
		chunkandpostag0A1Set = (config.getProperty("tree.chunkandpostag0*1", "true").equals("true"));
		chunkandpostag0A1ASet = (config.getProperty("tree.chunkandpostag0*1*", "true").equals("true"));
		chunkandpostag01ASet = (config.getProperty("tree.chunkandpostag0*1", "true").equals("true"));
		chunkdefaultSet = (config.getProperty("tree.chunkdefault", "true").equals("true"));

		// build
		cons0Set = (config.getProperty("tree.cons0", "true").equals("true"));
		cons_1Set = (config.getProperty("tree.cons_1", "true").equals("true"));
		cons_2Set = (config.getProperty("tree.cons_2", "true").equals("true"));
		cons1Set = (config.getProperty("tree.cons1", "true").equals("true"));
		cons2Set = (config.getProperty("tree.cons2", "true").equals("true"));
		cons0ASet = (config.getProperty("tree.cons0*", "true").equals("true"));
		cons_1ASet = (config.getProperty("tree.cons_1*", "true").equals("true"));
		cons_2ASet = (config.getProperty("tree.cons_2*", "true").equals("true"));
		cons1ASet = (config.getProperty("tree.cons1*", "true").equals("true"));
		cons2ASet = (config.getProperty("tree.cons2*", "true").equals("true"));
		cons_10Set = (config.getProperty("tree.cons_10", "true").equals("true"));
		cons_1A0Set = (config.getProperty("tree.cons_1*0", "true").equals("true"));
		cons_1A0ASet = (config.getProperty("tree.cons_1*0*", "true").equals("true"));
		cons_10ASet = (config.getProperty("tree.cons_10*", "true").equals("true"));
		cons01Set = (config.getProperty("tree.cons01", "true").equals("true"));
		cons0A1Set = (config.getProperty("tree.cons0*1", "true").equals("true"));
		cons0A1ASet = (config.getProperty("tree.cons0*1*", "true").equals("true"));
		cons01ASet = (config.getProperty("tree.cons01*", "true").equals("true"));
		cons_2_10Set = (config.getProperty("tree.cons_2_10", "true").equals("true"));
		cons_2A_1A0ASet = (config.getProperty("tree.cons_2*_1*0*", "true").equals("true"));
		cons_2A_1A0Set = (config.getProperty("tree.cons_2*_1*0", "true").equals("true"));
		cons_2A_10Set = (config.getProperty("tree.cons_2*_10", "true").equals("true"));
		cons_2_1A0Set = (config.getProperty("tree.cons_2_1*0", "true").equals("true"));
		cons012Set = (config.getProperty("tree.cons012", "true").equals("true"));
		cons0A1A2ASet = (config.getProperty("tree.cons0*1*2*", "true").equals("true"));
		cons01A2ASet = (config.getProperty("tree.cons01*2*", "true").equals("true"));
		cons01A2Set = (config.getProperty("tree.cons01*2", "true").equals("true"));
		cons012ASet = (config.getProperty("tree.cons012*", "true").equals("true"));
		cons_101Set = (config.getProperty("tree.cons_101", "true").equals("true"));
		cons_1A0A1ASet = (config.getProperty("tree.cons_1*0*1*", "true").equals("true"));
		cons_1A01ASet = (config.getProperty("tree.cons_1*01*", "true").equals("true"));
		cons_101ASet = (config.getProperty("tree.cons_101*", "true").equals("true"));
		cons_1A01Set = (config.getProperty("tree.cons_1*01", "true").equals("true"));
		punctuationSet = (config.getProperty("tree.punctuation", "true").equals("true"));
		builddefaultSet = (config.getProperty("tree.builddefault", "true").equals("true"));

		// check
		checkcons_lastSet = (config.getProperty("tree.checkcons_last", "true").equals("true"));
		checkcons_lastASet = (config.getProperty("tree.checkcons_last*", "true").equals("true"));
		checkcons_beginSet = (config.getProperty("tree.checkcons_begin", "true").equals("true"));
		checkcons_beginASet = (config.getProperty("tree.checkcons_begin*", "true").equals("true"));
		checkcons_ilastSet = (config.getProperty("tree.checkcons_ilast", "true").equals("true"));
		productionSet = (config.getProperty("tree.production", "true").equals("true"));
		surround1Set = (config.getProperty("tree.surround1", "true").equals("true"));
		surround2Set = (config.getProperty("tree.surround2", "true").equals("true"));
		surround1ASet = (config.getProperty("tree.surround1*", "true").equals("true"));
		surround2ASet = (config.getProperty("tree.surround2*", "true").equals("true"));
		surround_1Set = (config.getProperty("tree.surround_1", "true").equals("true"));
		surround_2Set = (config.getProperty("tree.surround_2", "true").equals("true"));
		surround_1ASet = (config.getProperty("tree.surround_1*", "true").equals("true"));
		surround_2ASet = (config.getProperty("tree.surround_2*", "true").equals("true"));
		checkdefaultSet = (config.getProperty("tree.checkdefault", "true").equals("true"));
	}

	/**
	 * chunk步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param chunkTree
	 *            子树序列
	 * @param actions
	 *            动作序列
	 * @return
	 */
	public String[] getContextForChunk(int index, List<HeadTreeNode> chunkTree, List<String> actions)
	{
		List<String> features = new ArrayList<String>();
		HeadTreeNode tree0, tree1, tree2, tree_1, tree_2;
		tree0 = tree1 = tree2 = tree_1 = tree_2 = null;
		tree0 = chunkTree.get(index);

		if (chunkTree.size() > index + 1)
		{
			tree1 = chunkTree.get(index + 1);
			if (chunkTree.size() > index + 2)
			{
				tree2 = chunkTree.get(index + 2);
			}
		}

		if (index - 1 >= 0)
		{
			tree_1 = chunkTree.get(index - 1);
			if (index - 2 >= 0)
			{
				tree_2 = chunkTree.get(index - 2);
			}
		}
		
		// 这里的特征是word pos chunk组成的
		// 当前位置的时候是没有chunk标记的
		if (tree0 != null)
		{
			if (chunkandpostag0Set)
			{
				features.add("chunkandpostag0=" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag0ASet)
			{
				features.add("chunkandpostag0*=" + tree0.getFirstChildName());
			}
		}
		
		// 当前位置之前的时候有chunk标记
		if (tree_1 != null)
		{
			if (chunkandpostag_1Set)
			{
				features.add("chunkandpostag_1=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag_1ASet)
			{
				features.add("chunkandpostag_1*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName());
			}
		}
		
		if (tree_2 != null)
		{
			if (chunkandpostag_2Set)
			{
				features.add("chunkandpostag_2=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + "|"
						+ tree_2.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag_2ASet)
			{
				features.add("chunkandpostag_2*=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName());
			}
		}
		
		// 当前位置之后的也没有chunk标记
		if (tree1 != null)
		{
			if (chunkandpostag1Set)
			{
				features.add("chunkandpostag1=" + tree1.getFirstChildName() + "|"
						+ tree1.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag1ASet)
			{
				features.add("chunkandpostag1*=" + tree1.getFirstChildName());
			}
		}
		
		if (tree2 != null)
		{
			if (chunkandpostag2Set)
			{
				features.add("chunkandpostag2=" + tree2.getFirstChildName() + "|"
						+ tree2.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag2ASet)
			{
				features.add("chunkandpostag2*=" + tree2.getFirstChildName());
			}
		}

		if (tree_1 != null && tree0 != null)
		{
			if (chunkandpostag_10Set)
			{
				features.add("chunkandpostag_10=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChild().getFirstChildName() + ";" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag_10ASet)
			{
				features.add("chunkandpostag_10*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChild().getFirstChildName() + ";" + tree0.getFirstChildName());
			}
			
			if (chunkandpostag_1A0Set)
			{
				features.add("chunkandpostag_1*0=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getFirstChildName() + "|" + tree0.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag_1A0ASet)
			{
				features.add("chunkandpostag_1*0*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getFirstChildName());
			}
		}

		if (tree0 != null && tree1 != null)
		{
			if (chunkandpostag01Set)
			{
				features.add("chunkandpostag01=" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChild().getFirstChildName() + ";" + tree1.getFirstChildName() + "|"
						+ tree1.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag01ASet)
			{
				features.add("chunkandpostag01*=" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChild().getFirstChildName() + ";" + tree1.getFirstChildName());
			}
			
			if (chunkandpostag0A1Set)
			{
				features.add("chunkandpostag0*1=" + tree0.getFirstChildName() + ";" + tree1.getFirstChildName() + "|"
						+ tree1.getFirstChild().getFirstChildName());
			}
			
			if (chunkandpostag0A1ASet)
			{
				features.add("chunkandpostag0*1*=" + tree0.getFirstChildName() + ";" + tree1.getFirstChildName());
			}
		}
		
		String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}

	/**
	 * build步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param buildAndCheckTree
	 *            子树序列
	 * @param actions
	 *            动作序列
	 * @return
	 */
	public String[] getContextForBuild(int index, List<HeadTreeNode> buildAndCheckTree, List<String> actions)
	{

		List<String> features = new ArrayList<String>();
		HeadTreeNode tree0, tree1, tree2, tree_1, tree_2;
		tree0 = tree1 = tree2 = tree_1 = tree_2 = null;
		tree0 = buildAndCheckTree.get(index);

		if (buildAndCheckTree.size() > index + 1)
		{
			tree1 = buildAndCheckTree.get(index + 1);
			if (buildAndCheckTree.size() > index + 2)
			{
				tree2 = buildAndCheckTree.get(index + 2);
			}
		}

		if (index - 1 >= 0)
		{
			tree_1 = buildAndCheckTree.get(index - 1);
			if (index - 2 >= 0)
			{
				tree_2 = buildAndCheckTree.get(index - 2);
			}
		}
		// 这里的标记由head words , constituent, build
		// 当前位置有build标记
		if (tree0 != null)
		{
			if (cons0Set)
			{
				features.add("cons0=" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord());
			}
			if (cons0ASet)
			{
				features.add("cons0*=" + tree0.getFirstChildName());
			}
		}
		// 当前位置之前的有build标记
		if (tree_1 != null)
		{
			if (cons_1Set)
			{
				features.add("cons_1=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord());
			}
			if (cons_1ASet)
			{
				features.add("cons_1*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName());
			}
		}
		if (tree_2 != null)
		{
			if (cons_2Set)
			{
				features.add("cons_2=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + "|"
						+ tree_2.getFirstChildHeadWord());
			}
			if (cons_2ASet)
			{
				features.add("cons_2*=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName());
			}
		}
		// 当前位置之后的也没有build标记
		if (tree1 != null)
		{
			if (cons1Set)
			{
				features.add("cons1=" + tree1.getNodeName() + "|" + tree1.getHeadWord());
			}
			if (cons1ASet)
			{
				features.add("cons1*=" + tree1.getNodeName());
			}
		}
		if (tree2 != null)
		{
			if (cons2Set)
			{
				features.add("cons2=" + tree2.getNodeName() + "|" + tree2.getHeadWord());
			}
			if (cons2ASet)
			{
				features.add("cons2*=" + tree2.getNodeName());
			}
		}

		if (tree_1 != null && tree0 != null)
		{
			if (cons_10Set)
			{
				features.add("cons_10=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChildHeadWord());
			}
			if (cons_10ASet)
			{
				features.add("cons_10*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getFirstChildName());
			}
			if (cons_1A0Set)
			{
				features.add("cons_1*0=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord());
			}
			if (cons_1A0ASet)
			{
				features.add("cons_1*0*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getFirstChildName());
			}
		}

		if (tree0 != null && tree1 != null)
		{
			if (cons01Set)
			{
				features.add("cons01=" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";"
						+ tree1.getNodeName() + "|" + tree1.getHeadWord());
			}
			if (cons01ASet)
			{
				features.add("cons01*=" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";"
						+ tree1.getNodeName());
			}
			if (cons0A1Set)
			{
				features.add(
						"cons0*1=" + tree0.getFirstChildName() + ";" + tree1.getNodeName() + "|" + tree1.getHeadWord());
			}
			if (cons0A1ASet)
			{
				features.add("cons0*1*=" + tree0.getFirstChildName() + ";" + tree1.getNodeName());
			}
		}

		if (tree_2 != null && tree_1 != null && tree0 != null)
		{
			if (cons_2_10Set)
			{
				features.add("cons_2_10=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + "|"
						+ tree_2.getFirstChildHeadWord() + ";" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName()
						+ "|" + tree_1.getFirstChildHeadWord() + ";" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChildHeadWord());
			}
			if (cons_2A_1A0Set)
			{
				features.add("cons_2*_1*0=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + ";"
						+ tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";" + tree0.getFirstChildName()
						+ "|" + tree0.getFirstChildHeadWord());
			}
			if (cons_2A_10Set)
			{
				features.add("cons_2*_10=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + ";"
						+ tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|" + tree_1.getFirstChildHeadWord()
						+ ";" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord());
			}
			if (cons_2_1A0Set)
			{
				features.add("cons_2_1*0=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + "|"
						+ tree_2.getFirstChildHeadWord() + ";" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName()
						+ ";" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord());
			}
			if (cons_2A_1A0ASet)
			{
				features.add("cons_2*_1*0*=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + ";"
						+ tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";" + tree0.getFirstChildName());
			}
		}

		if (tree0 != null && tree1 != null && tree2 != null)
		{
			if (cons012Set)
			{
				features.add("cons012=" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";"
						+ tree1.getNodeName() + "|" + tree1.getHeadWord() + ";" + tree2.getNodeName() + "|"
						+ tree2.getHeadWord());
			}
			if (cons01A2ASet)
			{
				features.add("cons01*2*=" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";"
						+ tree1.getNodeName() + ";" + tree2.getNodeName());
			}
			if (cons01A2Set)
			{
				features.add("cons01*2=" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";"
						+ tree1.getNodeName() + ";" + tree2.getNodeName() + "|" + tree2.getHeadWord());
			}
			if (cons012ASet)
			{
				features.add("cons012*=" + tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";"
						+ tree1.getNodeName() + "|" + tree1.getHeadWord() + ";" + tree2.getNodeName());
			}
			if (cons0A1A2ASet)
			{
				features.add("cons0*1*2*=" + tree0.getFirstChildName() + ";" + tree1.getNodeName() + ";"
						+ tree2.getNodeName());
			}
		}

		if (tree_1 != null && tree0 != null && tree1 != null)
		{
			if (cons_101Set)
			{
				features.add("cons_101=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChildHeadWord() + ";" + tree1.getNodeName() + "|" + tree1.getHeadWord());
			}
			if (cons_1A01ASet)
			{
				features.add("cons_1*01*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";" + tree1.getNodeName());
			}
			if (cons_1A01Set)
			{
				features.add("cons_1*01=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getFirstChildName() + "|" + tree0.getFirstChildHeadWord() + ";" + tree1.getNodeName()
						+ "|" + tree1.getHeadWord());
			}
			if (cons_101ASet)
			{
				features.add("cons_101*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getFirstChildName() + "|"
						+ tree0.getFirstChildHeadWord() + ";" + tree1.getNodeName());
			}
			if (cons_1A0A1ASet)
			{
				features.add("cons_1*0*1*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getFirstChildName() + ";" + tree1.getNodeName());
			}
		}

		// 标点符号
		for (int i = index - 1; i >= 0; i--)
		{
			if (buildAndCheckTree.get(i).getFirstChildName().equals("["))
			{
				if (tree0.getFirstChildName().equals("]"))
				{
					if (punctuationSet)
					{
						features.add("punctuation=" + "bracketsmatch");
					}
				}
			}
		}

		for (int i = index - 1; i >= 0; i--)
		{
			if (buildAndCheckTree.get(i).getFirstChildName().equals(","))
			{
				if (tree0.getFirstChildName().equals(","))
				{
					if (punctuationSet)
					{
						features.add("punctuation=" + "comma");
					}
				}
			}
		}

		if (tree0.getFirstChildName().equals("."))
		{
			if (punctuationSet)
			{
				features.add("punctuation=" + "endofsentence");
			}
		}

		String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}

	/**
	 * build步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param buildAndCheckTree
	 *            子树序列
	 * @param actions
	 *            动作序列
	 * @return
	 */
	public String[] getContextForCheck(int index, List<HeadTreeNode> buildAndCheckTree, List<String> actions)
	{
		List<String> features = new ArrayList<String>();
		String rightRule = "";
		int record = -1;
		for (int i = index; i >= 0; i--)
		{
			if (buildAndCheckTree.get(i).getNodeNameLeftPart().equals("start"))
			{
				record = i;
				break;
			}
		}
		if (checkcons_beginSet)
		{
			features.add("checkcons_begin=" + buildAndCheckTree.get(record).getNodeNameRightPart() + "|"
					+ buildAndCheckTree.get(record).getFirstChildName() + "|"
					+ buildAndCheckTree.get(record).getFirstChildHeadWord());
		}
		if (checkcons_beginASet)
		{
			features.add("checkcons_begin*=" + buildAndCheckTree.get(record).getNodeNameRightPart() + "|"
					+ buildAndCheckTree.get(record).getFirstChildName());
		}

		if (checkcons_lastSet)
		{
			features.add("checkcons_last=" + buildAndCheckTree.get(index).getNodeNameRightPart() + "|"
					+ buildAndCheckTree.get(index).getFirstChildName() + "|"
					+ buildAndCheckTree.get(index).getFirstChildHeadWord());
		}
		if (checkcons_lastASet)
		{
			features.add("checkcons_last*=" + buildAndCheckTree.get(index).getNodeNameRightPart() + "|"
					+ buildAndCheckTree.get(index).getFirstChildName());
		}

		int count = 0;
		for (int i = record; i < index; i++)
		{
			if (checkcons_ilastSet)
			{
				features.add("checkcons_" + (count++) + "last=" + buildAndCheckTree.get(i).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(i).getFirstChildName() + "|"
						+ buildAndCheckTree.get(i).getFirstChildHeadWord() + ";"
						+ buildAndCheckTree.get(index).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(index).getFirstChildName() + "|"
						+ buildAndCheckTree.get(index).getFirstChildHeadWord());
			}
			if (checkcons_iAlastSet)
			{
				features.add("checkcons_" + (count++) + "*last=" + buildAndCheckTree.get(i).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(i).getFirstChildName() + ";"
						+ buildAndCheckTree.get(index).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(index).getFirstChildName() + "|"
						+ buildAndCheckTree.get(index).getFirstChildHeadWord());
			}
			if (checkcons_ilastASet)
			{
				features.add("checkcons_" + (count++) + "last*=" + buildAndCheckTree.get(i).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(i).getFirstChildName() + "|"
						+ buildAndCheckTree.get(i).getFirstChildHeadWord() + ";"
						+ buildAndCheckTree.get(index).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(index).getFirstChildName());
			}
			if (checkcons_iAlastASet)
			{
				features.add("checkcons_" + (count++) + "*last*=" + buildAndCheckTree.get(i).getNodeNameRightPart()
						+ "|" + buildAndCheckTree.get(i).getFirstChildName() + ";"
						+ buildAndCheckTree.get(index).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(index).getFirstChildName());
			}
		}

		for (int i = record; i < index + 1; i++)
		{
			if (i == index)
			{
				rightRule += buildAndCheckTree.get(i).getFirstChildName();
			}
			else
			{
				rightRule += buildAndCheckTree.get(i).getFirstChildName() + ",";
			}
		}

		if (buildAndCheckTree.get(index).getNodeName().contains("start")
				|| buildAndCheckTree.get(index).getNodeName().contains("join"))
		{
			if (productionSet)
			{
				features.add("production=" + buildAndCheckTree.get(index).getNodeNameRightPart() + "→" + rightRule);
			}
		}

		List<HeadTreeNode> left_1posAndWordTree = null;
		List<HeadTreeNode> left_2posAndWordTree = null;
		List<HeadTreeNode> right1posAndWordTree = null;
		List<HeadTreeNode> right2posAndWordTree = null;

		if (record != 0)
		{
			if (record - 1 >= 0)
			{
				left_1posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(record - 1));
			}
			if (record - 2 >= 0)
			{
				left_2posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(record - 2));
			}
		}

		if (index != buildAndCheckTree.size() - 1)
		{
			if (index + 1 < buildAndCheckTree.size())
			{
				right1posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(index + 1));
			}
			if (index + 2 < buildAndCheckTree.size())
			{
				right2posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(index + 2));
			}
		}

		String right1 = "";
		String right2 = "";
		String left_1 = "";
		String left_2 = "";
		String right1A = "";
		String right2A = "";
		String left_1A = "";
		String left_2A = "";

		if (left_1posAndWordTree != null)
		{
			for (int i = 0; i < left_1posAndWordTree.size(); i++)
			{
				if (i == left_1posAndWordTree.size() - 1)
				{
					left_1 += left_1posAndWordTree.get(i).getNodeName() + "|"
							+ left_1posAndWordTree.get(i).getFirstChildName();
					left_1A += left_1posAndWordTree.get(i).getNodeName();
				}
				else
				{
					left_1 += left_1posAndWordTree.get(i).getNodeName() + "|"
							+ left_1posAndWordTree.get(i).getFirstChildName() + ";";
					left_1A += left_1posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround_1Set)
			{
				features.add("surround_1=" + left_1);
			}
			if (surround_1ASet)
			{
				features.add("surround_1*=" + left_1A);
			}
		}

		if (left_2posAndWordTree != null)
		{
			for (int i = 0; i < left_2posAndWordTree.size(); i++)
			{
				if (i == left_2posAndWordTree.size() - 1)
				{
					left_2 += left_2posAndWordTree.get(i).getNodeName() + "|"
							+ left_2posAndWordTree.get(i).getFirstChildName();
					left_2A += left_2posAndWordTree.get(i).getNodeName();
				}
				else
				{
					left_2 += left_2posAndWordTree.get(i).getNodeName() + "|"
							+ left_2posAndWordTree.get(i).getFirstChildName() + ";";
					left_2A += left_2posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround_2Set)
			{
				features.add("surround_2=" + left_2);
			}
			if (surround_2ASet)
			{
				features.add("surround_2*=" + left_2A);
			}
		}

		if (right1posAndWordTree != null)
		{
			for (int i = 0; i < right1posAndWordTree.size(); i++)
			{
				if (i == right1posAndWordTree.size() - 1)
				{
					right1 += right1posAndWordTree.get(i).getNodeName() + "|"
							+ right1posAndWordTree.get(i).getFirstChildName();
					right1A += right1posAndWordTree.get(i).getNodeName();
				}
				else
				{
					right1 += right1posAndWordTree.get(i).getNodeName() + "|"
							+ right1posAndWordTree.get(i).getFirstChildName() + ";";
					right1A += right1posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround1Set)
			{
				features.add("surround1=" + right1);
			}
			if (surround1ASet)
			{
				features.add("surround1*=" + right1A);
			}
		}

		if (right2posAndWordTree != null)
		{
			for (int i = 0; i < right2posAndWordTree.size(); i++)
			{
				if (i == right2posAndWordTree.size() - 1)
				{
					right2 += right2posAndWordTree.get(i).getNodeName() + "|"
							+ right2posAndWordTree.get(i).getFirstChildName();
					right2A += right2posAndWordTree.get(i).getNodeName();
				}
				else
				{
					right2 += right2posAndWordTree.get(i).getNodeName() + "|"
							+ right2posAndWordTree.get(i).getFirstChildName() + ";";
					right2A += right2posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround2Set)
			{
				features.add("surround2=" + right2);
			}
			if (surround2ASet)
			{
				features.add("surround2*=" + right2A);
			}
		}

		String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}

	/**
	 * 得到一棵树中的所有词性标记和词语序列
	 * 
	 * @param tree
	 *            输入的一棵树
	 * @return
	 */
	private List<HeadTreeNode> getPosAndWordTree(HeadTreeNode tree)
	{
		List<HeadTreeNode> posAndWordTree = new ArrayList<>();
		if (tree.getChildrenNum() == 1 && tree.getFirstChild().getChildrenNum() == 0)
		{
			posAndWordTree.add(tree);
			return posAndWordTree;
		}
		else
		{
			for (HeadTreeNode treeNode : tree.getChildren())
			{
				posAndWordTree.addAll(getPosAndWordTree(treeNode));
			}
			return posAndWordTree;
		}
	}

	/**
	 * chunk步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param chunkTree
	 *            子树序列
	 * @param actions
	 *            动作序列
	 * @param ac
	 * @return
	 */
	@Override
	public String[] getContextForChunk(int index, List<HeadTreeNode> chunkTree, List<String> actions, Object[] ac)
	{

		return getContextForChunk(index, chunkTree, actions);
	}

	/**
	 * build步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param buildAndCheckTree
	 *            子树序列
	 * @param actions
	 *            动作序列
	 * @param ac
	 * @return
	 */
	@Override
	public String[] getContextForBuild(int index, List<HeadTreeNode> buildAndCheckTree, List<String> actions,
			Object[] ac)
	{

		return getContextForBuild(index, buildAndCheckTree, actions);
	}

	/**
	 * build步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param buildAndCheckTree
	 *            子树序列
	 * @param actions
	 *            动作序列
	 * @param ac
	 * @return
	 */
	@Override
	public String[] getContextForCheck(int index, List<HeadTreeNode> buildAndCheckTree, List<String> actions,
			Object[] ac)
	{

		return getContextForCheck(index, buildAndCheckTree, actions);
	}

	/**
	 * 用于训练句法树模型的特征
	 */
	@Override
	public String toString()
	{
		return  this.getClass().getSimpleName() + "{" + "chunkandpostag0Set=" + chunkandpostag0Set
				+ ", chunkandpostag_1Set=" + chunkandpostag_1Set + ", chunkandpostag_2Set=" + chunkandpostag_2Set
				+ ", chunkandpostag1Set=" + chunkandpostag1Set + ", chunkandpostag2Set=" + chunkandpostag2Set
				+ ", chunkandpostag0*Set=" + chunkandpostag0ASet + ", chunkandpostag_1*Set=" + chunkandpostag_1ASet
				+ ", chunkandpostag_2*Set=" + chunkandpostag_2ASet + ", chunkandpostag1*Set=" + chunkandpostag1ASet
				+ ", chunkandpostag2*Set=" + chunkandpostag2ASet + ", chunkandpostag_10Set=" + chunkandpostag_10Set
				+ ", chunkandpostag_1*0Set=" + chunkandpostag_1A0Set + ", chunkandpostag_1*0ASet="
				+ chunkandpostag_1A0ASet + ", chunkandpostag_10*Set=" + chunkandpostag_10ASet + ", chunkandpostag01Set="
				+ chunkandpostag01Set + ", chunkandpostag0*1Set=" + chunkandpostag0A1Set + ", chunkandpostag0*1ASet="
				+ chunkandpostag0A1ASet + ", chunkandpostag01*Set=" + chunkandpostag01ASet + ", chunkdefaultSet="
				+ chunkdefaultSet + ", cons0Set=" + cons0Set + ", cons_1Set=" + cons_1Set + ", cons_2Set=" + cons_2Set
				+ ", cons1Set=" + cons1Set + ", cons2Set=" + cons2Set + ", cons0*Set=" + cons0ASet + ", cons_1*Set="
				+ cons_1ASet + ", cons_2*Set=" + cons_2ASet + ", cons1*Set=" + cons1ASet + ", cons2*Set=" + cons2ASet
				+ ", cons_10Set=" + cons_10Set + ", cons_1*0Set=" + cons_1A0Set + ", cons_1*0*Set=" + cons_1A0ASet
				+ ", cons_10*Set=" + cons_10ASet + ", cons01Set=" + cons01Set + ", cons0*1Set=" + cons0A1Set
				+ ", cons0*1*Set=" + cons0A1ASet + ", cons01*Set=" + cons01ASet + ", cons_2_10Set=" + cons_2_10Set
				+ ", cons_2*_1*0*Set=" + cons_2A_1A0ASet + ", cons_2*_1*0Set=" + cons_2A_1A0Set + ", cons_2*_10Set="
				+ cons_2A_10Set + ", cons_2_1*0Set=" + cons_2_1A0Set + ", cons012Set=" + cons012Set + ", cons0*1*2*Set="
				+ cons0A1A2ASet + ", cons01*2*Set=" + cons01A2ASet + ", cons01*2Set=" + cons01A2Set + ", cons012*Set="
				+ cons012ASet + ", cons_101Set=" + cons_101Set + ", cons_1*0*1*Set=" + cons_1A0A1ASet
				+ ", cons_1*01*Set=" + cons_1A01ASet + ", cons_101*Set=" + cons_101ASet + ", cons_1*01Set="
				+ cons_1A01Set + ", punctuationSet=" + punctuationSet + ", builddefaultSet=" + builddefaultSet
				+ ", checkcons_lastSet=" + checkcons_lastSet + ", checkcons_last*Set=" + checkcons_lastASet
				+ ", checkcons_beginSet=" + checkcons_beginSet + ", checkcons_begin*Set=" + checkcons_beginASet
				+ ", checkcons_ilastSet=" + checkcons_ilastSet + ", checkcons_i*lastSet=" + checkcons_iAlastSet
				+ ", checkcons_ilast*Set=" + checkcons_ilastASet + ", checkcons_i*last*Set=" + checkcons_iAlastASet
				+ ", productionSet=" + productionSet + ", surround1Set=" + surround1Set + ", surround2Set="
				+ surround2Set + ", surround_1Set=" + surround_1Set + ", surround_2Set=" + surround_2Set
				+ ", surround1*Set=" + surround1ASet + ", surround2*Set=" + surround2ASet + ", surround_1*Set="
				+ surround_1ASet + ", surround_2*Set=" + surround_2ASet + ", checkdefaultSet=" + checkdefaultSet + '}';
	}

	/**
	 * 为测试语料的chunk步骤生成上下文特征
	 * 
	 * @param index
	 *            索引位置
	 * @param posTree
	 *            词性标注的子树，【区别于训练语料中的chunkTree，这里chunkTree还包含了动作序列作为根节点】
	 * @param actions
	 *            动作序列
	 * @param ac
	 * @return
	 */
	@Override
	public String[] getContextForChunkForTest(int index, List<HeadTreeNode> posTree, List<String> actions, Object[] ac)
	{

		return getContextForChunkForTest(index, posTree, actions);
	}

	/**
	 * 为测试语料的chunk步骤生成上下文特征
	 * 
	 * @param index
	 *            索引位置
	 * @param posTree
	 *            词性标注的子树，【区别于训练语料中的chunkTree，这里chunkTree还包含了动作序列作为根节点】
	 * @return
	 */
	private String[] getContextForChunkForTest(int index, List<HeadTreeNode> posTree, List<String> actions)
	{
		List<String> features = new ArrayList<String>();
		HeadTreeNode tree0, tree1, tree2, tree_1, tree_2;
		tree0 = tree1 = tree2 = tree_1 = tree_2 = null;
		String action_1, action_2;
		action_1 = action_2 = null;
		tree0 = posTree.get(index);

		if (posTree.size() > index + 1)
		{
			tree1 = posTree.get(index + 1);
			if (posTree.size() > index + 2)
			{
				tree2 = posTree.get(index + 2);
			}
		}

		if (index - 1 >= 0)
		{
			tree_1 = posTree.get(index - 1);
			action_1 = actions.get(index - 1);
			if (index - 2 >= 0)
			{
				tree_2 = posTree.get(index - 2);
				action_2 = actions.get(index - 2);
			}
		}
		// 这里的特征是word pos chunk组成的
		// 当前位置的时候是没有chunk标记的
		if (tree0 != null)
		{
			if (chunkandpostag0Set)
			{
				features.add("chunkandpostag0=" + tree0.getNodeName() + "|" + tree0.getFirstChildName());
			}
			if (chunkandpostag0ASet)
			{
				features.add("chunkandpostag0*=" + tree0.getNodeName());
			}
		}
		// 当前位置之前的时候有chunk标记
		if (tree_1 != null)
		{
			if (chunkandpostag_1Set)
			{
				features.add(
						"chunkandpostag_1=" + action_1 + "|" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName());
			}
			if (chunkandpostag_1ASet)
			{
				features.add("chunkandpostag_1*=" + action_1 + "|" + tree_1.getNodeName());
			}
		}
		if (tree_2 != null)
		{
			if (chunkandpostag_2Set)
			{
				features.add(
						"chunkandpostag_2=" + action_2 + "|" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName());
			}
			if (chunkandpostag_2ASet)
			{
				features.add("chunkandpostag_2*=" + action_2 + "|" + tree_2.getNodeName());
			}
		}
		// 当前位置之后的也没有chunk标记
		if (tree1 != null)
		{
			if (chunkandpostag1Set)
			{
				features.add("chunkandpostag1=" + tree1.getNodeName() + "|" + tree1.getFirstChildName());
			}
			if (chunkandpostag1ASet)
			{
				features.add("chunkandpostag1*=" + tree1.getNodeName());
			}
		}
		if (tree2 != null)
		{
			if (chunkandpostag2Set)
			{
				features.add("chunkandpostag2=" + tree2.getNodeName() + "|" + tree2.getFirstChildName());
			}
			if (chunkandpostag2ASet)
			{
				features.add("chunkandpostag2*=" + tree2.getNodeName());
			}
		}

		if (tree_1 != null && tree0 != null)
		{
			if (chunkandpostag_10Set)
			{
				features.add("chunkandpostag_10=" + action_1 + "|" + tree_1.getNodeName() + "|"
						+ tree_1.getFirstChildName() + ";" + tree0.getNodeName() + "|" + tree0.getFirstChildName());
			}
			if (chunkandpostag_10ASet)
			{
				features.add("chunkandpostag_10*=" + action_1 + "|" + tree_1.getNodeName() + "|"
						+ tree_1.getFirstChildName() + ";" + tree0.getNodeName());
			}
			if (chunkandpostag_1A0Set)
			{
				features.add("chunkandpostag_1*0=" + action_1 + "|" + tree_1.getNodeName() + ";" + tree0.getNodeName()
						+ "|" + tree0.getFirstChildName());
			}
			if (chunkandpostag_1A0ASet)
			{
				features.add(
						"chunkandpostag_1*0*=" + action_1 + "|" + tree_1.getNodeName() + ";" + tree0.getNodeName());
			}
		}

		if (tree0 != null && tree1 != null)
		{
			if (chunkandpostag01Set)
			{
				features.add("chunkandpostag01=" + tree0.getNodeName() + "|" + tree0.getFirstChildName() + ";"
						+ tree1.getNodeName() + "|" + tree1.getFirstChildName());
			}
			if (chunkandpostag01ASet)
			{
				features.add("chunkandpostag01*=" + tree0.getNodeName() + "|" + tree0.getFirstChildName() + ";"
						+ tree1.getNodeName());
			}
			if (chunkandpostag0A1Set)
			{
				features.add("chunkandpostag0*1=" + tree0.getNodeName() + ";" + tree1.getNodeName() + "|"
						+ tree1.getFirstChildName());
			}
			if (chunkandpostag0A1ASet)
			{
				features.add("chunkandpostag0*1*=" + tree0.getNodeName() + ";" + tree1.getNodeName());
			}
		}
		String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}

	/**
	 * 为测试语料的build步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param chunkTree
	 *            子树序列
	 * @param ac
	 * @return
	 */
	@Override
	public String[] getContextForBuildForTest(int index, List<HeadTreeNode> chunkTree, Object[] ac)
	{

		return getContextForBuildForTest(index, chunkTree);
	}

	/**
	 * 为测试语料的build步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param chunkTree
	 *            子树序列
	 * @return
	 */
	private String[] getContextForBuildForTest(int index, List<HeadTreeNode> chunkTree)
	{
		List<String> features = new ArrayList<String>();
		HeadTreeNode tree0, tree1, tree2, tree_1, tree_2;
		tree0 = tree1 = tree2 = tree_1 = tree_2 = null;
		tree0 = chunkTree.get(index);
		if (chunkTree.size() > index + 1)
		{
			tree1 = chunkTree.get(index + 1);
			if (chunkTree.size() > index + 2)
			{
				tree2 = chunkTree.get(index + 2);
			}
		}

		if (index - 1 >= 0)
		{
			tree_1 = chunkTree.get(index - 1);
			if (index - 2 >= 0)
			{
				tree_2 = chunkTree.get(index - 2);
			}
		}
		// 这里的标记由head words , constituent, build
		// 当前位置没有build标记
		if (tree0 != null)
		{
			if (cons0Set)
			{
				features.add("cons0=" + tree0.getNodeName() + "|" + tree0.getHeadWord());
			}
			if (cons0ASet)
			{
				features.add("cons0*=" + tree0.getNodeName());
			}
		}
		// 当前位置之前的有build标记
		if (tree_1 != null)
		{
			if (cons_1Set)
			{
				features.add("cons_1=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord());
			}
			if (cons_1ASet)
			{
				features.add("cons_1*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName());
			}
		}
		if (tree_2 != null)
		{
			if (cons_2Set)
			{
				features.add("cons_2=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + "|"
						+ tree_2.getFirstChildHeadWord());
			}
			if (cons_2ASet)
			{
				features.add("cons_2*=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName());
			}
		}
		// 当前位置之后的也没有build标记
		if (tree1 != null)
		{
			if (cons1Set)
			{
				features.add("cons1=" + tree1.getNodeName() + "|" + tree1.getHeadWord());
			}
			if (cons1ASet)
			{
				features.add("cons1*=" + tree1.getNodeName());
			}
		}
		if (tree2 != null)
		{
			if (cons2Set)
			{
				features.add("cons2=" + tree2.getNodeName() + "|" + tree2.getHeadWord());
			}
			if (cons2ASet)
			{
				features.add("cons2*=" + tree2.getNodeName());
			}
		}

		if (tree_1 != null && tree0 != null)
		{
			if (cons_10Set)
			{
				features.add("cons_10=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getNodeName() + "|" + tree0.getHeadWord());
			}
			if (cons_10ASet)
			{
				features.add("cons_10*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getNodeName());
			}
			if (cons_1A0Set)
			{
				features.add("cons_1*0=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getNodeName() + "|" + tree0.getHeadWord());
			}
			if (cons_1A0ASet)
			{
				features.add("cons_1*0*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getNodeName());
			}
		}

		if (tree0 != null && tree1 != null)
		{
			if (cons01Set)
			{
				features.add("cons01=" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName()
						+ "|" + tree1.getHeadWord());
			}
			if (cons01ASet)
			{
				features.add("cons01*=" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName());
			}
			if (cons0A1Set)
			{
				features.add("cons0*1=" + tree0.getNodeName() + ";" + tree1.getNodeName() + "|" + tree1.getHeadWord());
			}
			if (cons0A1ASet)
			{
				features.add("cons0*1*=" + tree0.getNodeName() + ";" + tree1.getNodeName());
			}
		}

		if (tree_2 != null && tree_1 != null && tree0 != null)
		{
			if (cons_2_10Set)
			{
				features.add("cons_2_10=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + "|"
						+ tree_2.getFirstChildHeadWord() + ";" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName()
						+ "|" + tree_1.getFirstChildHeadWord() + ";" + tree0.getNodeName() + "|" + tree0.getHeadWord());
			}
			if (cons_2A_1A0Set)
			{
				features.add("cons_2*_1*0=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + ";"
						+ tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";" + tree0.getNodeName() + "|"
						+ tree0.getHeadWord());
			}
			if (cons_2A_10Set)
			{
				features.add("cons_2*_10=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + ";"
						+ tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|" + tree_1.getFirstChildHeadWord()
						+ ";" + tree0.getNodeName() + "|" + tree0.getHeadWord());
			}
			if (cons_2_1A0Set)
			{
				features.add("cons_2_1*0=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + "|"
						+ tree_2.getFirstChildHeadWord() + ";" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName()
						+ ";" + tree0.getNodeName() + "|" + tree0.getHeadWord());
			}
			if (cons_2A_1A0ASet)
			{
				features.add("cons_2*_1*0*=" + tree_2.getNodeName() + "|" + tree_2.getFirstChildName() + ";"
						+ tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";" + tree0.getNodeName());
			}
		}

		if (tree0 != null && tree1 != null && tree2 != null)
		{
			if (cons012Set)
			{
				features.add("cons012=" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName()
						+ "|" + tree1.getHeadWord() + ";" + tree2.getNodeName() + "|" + tree2.getHeadWord());
			}
			if (cons01A2ASet)
			{
				features.add("cons01*2*=" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName()
						+ ";" + tree2.getNodeName());
			}
			if (cons01A2Set)
			{
				features.add("cons01*2=" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName()
						+ ";" + tree2.getNodeName() + "|" + tree2.getHeadWord());
			}
			if (cons012ASet)
			{
				features.add("cons012*=" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName()
						+ "|" + tree1.getHeadWord() + ";" + tree2.getNodeName());
			}
			if (cons0A1A2ASet)
			{
				features.add(
						"cons0*1*2*=" + tree0.getNodeName() + ";" + tree1.getNodeName() + ";" + tree2.getNodeName());
			}
		}

		if (tree_1 != null && tree0 != null && tree1 != null)
		{
			if (cons_101Set)
			{
				features.add("cons_101=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";"
						+ tree1.getNodeName() + "|" + tree1.getHeadWord());
			}
			if (cons_1A01ASet)
			{
				features.add("cons_1*01*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName());
			}
			if (cons_1A01Set)
			{
				features.add("cons_1*01=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getNodeName() + "|" + tree0.getHeadWord() + ";" + tree1.getNodeName() + "|"
						+ tree1.getHeadWord());
			}
			if (cons_101ASet)
			{
				features.add("cons_101*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + "|"
						+ tree_1.getFirstChildHeadWord() + ";" + tree0.getNodeName() + "|" + tree0.getHeadWord() + ";"
						+ tree1.getNodeName());
			}
			if (cons_1A0A1ASet)
			{
				features.add("cons_1*0*1*=" + tree_1.getNodeName() + "|" + tree_1.getFirstChildName() + ";"
						+ tree0.getNodeName() + ";" + tree1.getNodeName());
			}
		}

		// 标点符号
		for (int i = index - 1; i >= 0; i--)
		{
			if (chunkTree.get(i).getFirstChildName().equals("["))
			{
				if (tree0.getNodeName().equals("]"))
				{
					if (punctuationSet)
					{
						features.add("punctuation=" + "bracketsmatch");
					}
				}
			}
		}

		for (int i = index - 1; i >= 0; i--)
		{
			if (chunkTree.get(i).getFirstChildName().equals(","))
			{
				if (tree0.getNodeName().equals(","))
				{
					if (punctuationSet)
					{
						features.add("punctuation=" + "comma");
					}
				}
			}
		}

		if (tree0.getNodeName().equals("."))
		{
			if (punctuationSet)
			{
				features.add("punctuation=" + "endofsentence");
			}
		}

		String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}

	/**
	 * 为测试语料的check步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param chunkTree
	 *            子树序列
	 * @param out
	 *            当前的动作序列
	 * @param ac
	 * @return
	 */
	@Override
	public String[] getContextForCheckForTest(int index, List<HeadTreeNode> chunkTree, String out, Object[] ac)
	{

		return getContextForCheckForTest(index, chunkTree, out);
	}

	/**
	 * 为测试语料的check步的上下文特征
	 * 
	 * @param index
	 *            当前位置
	 * @param buildAndCheckTree
	 *            子树序列
	 * @param out
	 *            当前的动作序列
	 * @return
	 */
	private String[] getContextForCheckForTest(int index, List<HeadTreeNode> buildAndCheckTree, String out)
	{
		List<String> features = new ArrayList<String>();
		String rightRule = "";

		int record = -1;
		if (out.split("_")[0].equals("start"))
		{
			record = index;
		}
		else
		{
			for (int i = index - 1; i >= 0; i--)
			{
				if (buildAndCheckTree.get(i).getNodeNameLeftPart().equals("start"))
				{
					record = i;
					break;
				}
			}
		}

		if (record == index)
		{
			if (checkcons_beginSet)
			{
				features.add("checkcons_begin=" + out.split("_")[1] + "|" + buildAndCheckTree.get(record).getNodeName()
						+ "|" + buildAndCheckTree.get(record).getHeadWord());
			}
			if (checkcons_beginASet)
			{
				features.add(
						"checkcons_begin*=" + out.split("_")[1] + "|" + buildAndCheckTree.get(record).getNodeName());
			}
		}
		else
		{
			if (checkcons_beginSet)
			{
				features.add("checkcons_begin=" + buildAndCheckTree.get(record).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(record).getFirstChildName() + "|"
						+ buildAndCheckTree.get(record).getFirstChildHeadWord());
			}
			if (checkcons_beginASet)
			{
				features.add("checkcons_begin*=" + buildAndCheckTree.get(record).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(record).getFirstChildName());
			}
		}

		if (checkcons_lastSet)
		{
			features.add("checkcons_last=" + out.split("_")[1] + "|" + buildAndCheckTree.get(index).getNodeName() + "|"
					+ buildAndCheckTree.get(index).getHeadWord());
		}
		if (checkcons_lastASet)
		{
			features.add("checkcons_last*=" + out.split("_")[1] + "|" + buildAndCheckTree.get(index).getNodeName());
		}
		int count = 0;
		for (int i = record; i < index; i++)
		{
			if (checkcons_ilastSet)
			{
				features.add("checkcons_" + (count++) + "last=" + buildAndCheckTree.get(i).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(i).getFirstChildName() + "|"
						+ buildAndCheckTree.get(i).getFirstChildHeadWord() + ";" + out.split("_")[1] + "|"
						+ buildAndCheckTree.get(index).getNodeName() + "|"
						+ buildAndCheckTree.get(index).getHeadWord());
			}

			if (checkcons_iAlastSet)
			{
				features.add("checkcons_" + (count++) + "*last=" + buildAndCheckTree.get(i).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(i).getFirstChildName() + ";" + out.split("_")[1] + "|"
						+ buildAndCheckTree.get(index).getNodeName() + "|"
						+ buildAndCheckTree.get(index).getHeadWord());
			}

			if (checkcons_ilastASet)
			{
				features.add("checkcons_" + (count++) + "last*=" + buildAndCheckTree.get(i).getNodeNameRightPart() + "|"
						+ buildAndCheckTree.get(i).getFirstChildName() + "|"
						+ buildAndCheckTree.get(i).getFirstChildHeadWord() + ";" + out.split("_")[1] + "|"
						+ buildAndCheckTree.get(index).getNodeName());
			}

			if (checkcons_iAlastASet)
			{
				features.add("checkcons_" + (count++) + "*last*=" + buildAndCheckTree.get(i).getNodeNameRightPart()
						+ "|" + buildAndCheckTree.get(i).getFirstChildName() + ";" + out.split("_")[1] + "|"
						+ buildAndCheckTree.get(index).getNodeName());
			}
		}
		if (record == index)
		{
			rightRule = buildAndCheckTree.get(index).getNodeName();

		}
		else
		{
			for (int i = record; i < index + 1; i++)
			{

				if (i == index)
				{
					rightRule += buildAndCheckTree.get(i).getNodeName();
				}
				else
				{
					rightRule += buildAndCheckTree.get(i).getFirstChildName() + ",";
				}
			}
		}

		if (out.contains("start") || out.contains("join"))
		{
			if (productionSet)
			{
				features.add("production=" + out.split("_")[1] + "→" + rightRule);
			}
		}

		List<HeadTreeNode> left_1posAndWordTree = null;
		List<HeadTreeNode> left_2posAndWordTree = null;
		List<HeadTreeNode> right1posAndWordTree = null;
		List<HeadTreeNode> right2posAndWordTree = null;
		if (record != 0)
		{
			if (record - 1 >= 0)
			{
				left_1posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(record - 1));
			}
			if (record - 2 >= 0)
			{
				left_2posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(record - 2));
			}
		}
		if (index != buildAndCheckTree.size() - 1)
		{
			if (index + 1 < buildAndCheckTree.size())
			{
				right1posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(index + 1));
			}
			if (index + 2 < buildAndCheckTree.size())
			{
				right2posAndWordTree = getPosAndWordTree(buildAndCheckTree.get(index + 2));
			}
		}

		String right1 = "";
		String right2 = "";
		String left_1 = "";
		String left_2 = "";
		String right1A = "";
		String right2A = "";
		String left_1A = "";
		String left_2A = "";

		if (left_1posAndWordTree != null)
		{
			for (int i = 0; i < left_1posAndWordTree.size(); i++)
			{
				if (i == left_1posAndWordTree.size() - 1)
				{
					left_1 += left_1posAndWordTree.get(i).getNodeName() + "|"
							+ left_1posAndWordTree.get(i).getFirstChildName();
					left_1A += left_1posAndWordTree.get(i).getNodeName();
				}
				else
				{
					left_1 += left_1posAndWordTree.get(i).getNodeName() + "|"
							+ left_1posAndWordTree.get(i).getFirstChildName() + ";";
					left_1A += left_1posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround_1Set)
			{
				features.add("surround_1=" + left_1);
			}
			if (surround_1ASet)
			{
				features.add("surround_1*=" + left_1A);
			}
		}

		if (left_2posAndWordTree != null)
		{
			for (int i = 0; i < left_2posAndWordTree.size(); i++)
			{
				if (i == left_2posAndWordTree.size() - 1)
				{
					left_2 += left_2posAndWordTree.get(i).getNodeName() + "|"
							+ left_2posAndWordTree.get(i).getFirstChildName();
					left_2A += left_2posAndWordTree.get(i).getNodeName();
				}
				else
				{
					left_2 += left_2posAndWordTree.get(i).getNodeName() + "|"
							+ left_2posAndWordTree.get(i).getFirstChildName() + ";";
					left_2A += left_2posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround_2Set)
			{
				features.add("surround_2=" + left_2);
			}
			if (surround_2ASet)
			{
				features.add("surround_2*=" + left_2A);
			}
		}

		if (right1posAndWordTree != null)
		{
			for (int i = 0; i < right1posAndWordTree.size(); i++)
			{
				if (i == right1posAndWordTree.size() - 1)
				{
					right1 += right1posAndWordTree.get(i).getNodeName() + "|"
							+ right1posAndWordTree.get(i).getFirstChildName();
					right1A += right1posAndWordTree.get(i).getNodeName();
				}
				else
				{
					right1 += right1posAndWordTree.get(i).getNodeName() + "|"
							+ right1posAndWordTree.get(i).getFirstChildName() + ";";
					right1A += right1posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround1Set)
			{
				features.add("surround1=" + right1);
			}
			if (surround1ASet)
			{
				features.add("surround1*=" + right1A);
			}
		}

		if (right2posAndWordTree != null)
		{
			for (int i = 0; i < right2posAndWordTree.size(); i++)
			{
				if (i == right2posAndWordTree.size() - 1)
				{
					right2 += right2posAndWordTree.get(i).getNodeName() + "|"
							+ right2posAndWordTree.get(i).getFirstChildName();
					right2A += right2posAndWordTree.get(i).getNodeName();
				}
				else
				{
					right2 += right2posAndWordTree.get(i).getNodeName() + "|"
							+ right2posAndWordTree.get(i).getFirstChildName() + ";";
					right2A += right2posAndWordTree.get(i).getNodeName() + ";";
				}
			}
			if (surround2Set)
			{
				features.add("surround2=" + right2);
			}
			if (surround2ASet)
			{
				features.add("surround2*=" + right2A);
			}
		}
		String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}
}
