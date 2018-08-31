package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.Evaluator;

/**
 * 对分步骤训练句法分析模型进行评估的类
 * 
 * @author 王馨苇
 *
 */
public class ParserEvaluatorForByStep extends Evaluator<ConstituentTreeSample>
{

	private Logger logger = Logger.getLogger(ParserEvaluatorForByStep.class.getName());
	
	private POSTaggerForParser<HeadTreeNode> postagger;
	private ChunkerForParserME chunktagger;
	private BuilderAndCheckerME buildAndChecktagger;
	
	private ConstituentMeasure measure;
	
	private AbstractHeadGenerator headGen;

	public ParserEvaluatorForByStep(POSTaggerForParser<HeadTreeNode> postagger, ChunkerForParserME chunktagger,
			BuilderAndCheckerME buildAndChecktagger, AbstractHeadGenerator aghw)
	{
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
		this.headGen = aghw;
	}

	public ParserEvaluatorForByStep(POSTaggerForParser<HeadTreeNode> postagger, ChunkerForParserME chunktagger,
			BuilderAndCheckerME buildAndChecktagger, AbstractHeadGenerator aghw,
			ParserEvaluateMonitor... evaluateMonitors)
	{
		super(evaluateMonitors);
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
		this.headGen = aghw;
	}

	/**
	 * 设置评估指标的对象
	 * 
	 * @param measure
	 *            评估指标计算的对象
	 */
	public void setMeasure(ConstituentMeasure measure)
	{
		this.measure = measure;
	}

	/**
	 * 得到评估的指标
	 * 
	 * @return
	 */
	public ConstituentMeasure getMeasure()
	{
		return this.measure;
	}

	@Override
	protected ConstituentTreeSample processSample(ConstituentTreeSample sample)
	{
		ConstituentTreeSample samplePre = null;
		HeadTreeNode treePre = null;
		// 在验证的过程中，有些配ignore的句子，也会来验证，这是没有意义的，为了防止这种情况，就加入判断
		if (sample.getActions().size() == 0 && sample.getWords().size() == 0)
		{
			return new ConstituentTreeSample(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
					new ArrayList<>());
		}
		else
		{
			try
			{
				List<String> words = sample.getWords();
				List<String> actionsRef = sample.getActions();
				// 参考样本没有保存完整的一棵树，需要将动作序列转成一颗完整的树
				TreeNode treeRef = ActionsToTree.actionsToTree(words, actionsRef);
				List<List<HeadTreeNode>> posTree = postagger.posTree(words.toArray(new String[words.size()]), 20);
				List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(20, posTree, null);
				treePre = buildAndChecktagger.tagBuildAndCheck(chunkTree, null);

				if (treePre == null)
				{
					samplePre = new ConstituentTreeSample(new ArrayList<>(), new ArrayList<>(),
							new ArrayList<>(), new ArrayList<>());
					measure.countNodeDecodeTrees(treePre);
				}
				else
				{
					samplePre = HeadTreeToActions.headTreeToSample(treePre, headGen);
					measure.update(treeRef, treePre);
				}
			}
			catch (Exception e)
			{
				if (logger.isLoggable(Level.WARNING))
				{
					logger.warning("Error during parsing, ignoring sentence: " + treePre.toStringWordIndex());
				}
				samplePre = new ConstituentTreeSample(new ArrayList<>(), new ArrayList<>(),
						new ArrayList<>(), new ArrayList<>());
			}
			return samplePre;
		}
	}
}
