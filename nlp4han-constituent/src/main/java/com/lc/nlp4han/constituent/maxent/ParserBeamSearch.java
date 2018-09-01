package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.HeadRuleSetPTB;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.util.Cache;

/**
 * 得到最好的K个结果的实现类
 * 
 * @author 王馨苇
 *
 */
public class ParserBeamSearch implements ParserSequenceClassificationModel
{

	private AbstractHeadGenerator aghw;
	public static final String BEAM_SIZE_PARAMETER = "BeamSize";
	private static final Object[] EMPTY_ADDITIONAL_CONTEXT = new Object[0];
	protected int size;
	protected ClassificationModel buildmodel;
	protected ClassificationModel checkmodel;
	protected ClassificationModel chunkmodel;
	protected ClassificationModel model;// 一步训练得到的模型
	private double[] buildprobs;
	private double[] chunkprobs;
	private Cache<String[], double[]> contextsCache;

	public ParserBeamSearch(int size, ClassificationModel buildmodel, ClassificationModel checkmodel,
			AbstractHeadGenerator aghw)
	{
		this(size, buildmodel, checkmodel, 0, aghw);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ParserBeamSearch(int size, ClassificationModel buildmodel, ClassificationModel checkmodel,
			int cacheSize, AbstractHeadGenerator aghw)
	{
		this.size = size;
		this.buildmodel = buildmodel;
		this.checkmodel = checkmodel;
		if (cacheSize > 0)
		{
			this.contextsCache = new Cache(cacheSize);
		}

		this.buildprobs = new double[buildmodel.getNumOutcomes()];
		this.aghw = aghw;
	}

	public ParserBeamSearch(int size, ClassificationModel chunkmodel)
	{
		this(size, chunkmodel, 0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ParserBeamSearch(int size, ClassificationModel chunkmodel, int cacheSize)
	{
		this.size = size;
		this.chunkmodel = chunkmodel;
		if (cacheSize > 0)
		{
			this.contextsCache = new Cache(cacheSize);
		}

		this.chunkprobs = new double[chunkmodel.getNumOutcomes()];
	}

	/**
	 * 为一步训练得到的模型生成的构造函数
	 * 
	 * @param size
	 * @param model
	 * @param cacheSize
	 * @param flag
	 *            字符串类型的标记，证明是一步训练的句法模型
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ParserBeamSearch(int size, ClassificationModel model, int cacheSize, String flag)
	{
		this.size = size;
		this.chunkmodel = model;
		this.buildmodel = model;
		this.checkmodel = model;
		if (cacheSize > 0)
		{
			this.contextsCache = new Cache(cacheSize);
		}

		this.chunkprobs = new double[model.getNumOutcomes()];
		this.buildprobs = new double[model.getNumOutcomes()];
	}

	/**
	 * 得到最好的结果
	 * 
	 * @param posTree
	 *            pos步得到的最好的K棵树
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	@Override
	public ChunkSequence bestSequenceForChunk(List<List<HeadTreeNode>> posTree, Object[] ac,
			ParserContextGenerator generator, ParserSequenceValidator validator)
	{
		ChunkSequence[] sequences = this.bestSequencesForChunk(1, posTree, ac, generator,
				validator);
		return sequences.length > 0 ? sequences[0] : null;
	}

	/**
	 * 得到最好的num个结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param posTree
	 *            pos步得到的最好的K棵
	 * @param ac
	 *            额外的信息
	 * @param minSequenceScore
	 *            得分最低的限制
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	public ChunkSequence[] bestSequencesForChunk(int num, List<List<HeadTreeNode>> posTree,
			Object[] ac, double minSequenceScore, ParserContextGenerator generator,
			ParserSequenceValidator validator)
	{
		// 用于存放输入的K个结果中每一个得到的K个结果
		PriorityQueue<ChunkSequence> kRes = new PriorityQueue<>(this.size);
		// 遍历posTree中的K个结果
		for (int i = 0; i < posTree.size(); i++)
		{
			PriorityQueue<ChunkSequence> prev = new PriorityQueue<>(this.size);
			PriorityQueue<ChunkSequence> next = new PriorityQueue<>(this.size);
			prev.add(new ChunkSequence(i));
			if (ac == null)
			{
				ac = EMPTY_ADDITIONAL_CONTEXT;
			}

			int numSeq;
			int seqIndex;
			for (numSeq = 0; numSeq < posTree.get(i).size(); ++numSeq)
			{// 遍历其中的序列的长度
				// 前一个结果如果小于beam size的大小，就取前一个结果的大小
				// 如果前一个结果大于beam size的大小，就取beam size 的大小
				int topSequences = Math.min(this.size, prev.size());
				// 遍历前面的结果的所有可能
				for (seqIndex = 0; prev.size() > 0 && seqIndex < topSequences; ++seqIndex)
				{
					ChunkSequence top = prev.remove();// 取出beam size个结果中的第一个
					List<String> tmpOutcomes = top.getOutcomes();// 取出beam size个结果中的第一个中的结果序列
					String[] contexts = generator.getContextForChunkForTest(numSeq, posTree.get(i), tmpOutcomes, ac);
					double[] scores;
					// 得到每个类别的分数
					if (this.contextsCache != null)
					{
						scores = (double[]) this.contextsCache.get(contexts);
						if (scores == null)
						{
							scores = this.chunkmodel.eval(contexts, this.chunkprobs);
							this.contextsCache.put(contexts, scores);
						}
					}
					else
					{
						scores = this.chunkmodel.eval(contexts, this.chunkprobs);
					}
					// temp_scores的作用就是取出第前beam size个的分数的界限
					double[] temp_scores = new double[scores.length];
					System.arraycopy(scores, 0, temp_scores, 0, scores.length);// 数组的复制
					Arrays.sort(temp_scores);// 排序
					// 取beam size位置的值，保证取的分数值在前beam size个
					double min = temp_scores[Math.max(0, scores.length - this.size)];

					int p;
					String out;
					ChunkSequence ns = null;
					for (p = 0; p < scores.length; ++p)
					{
						if (scores[p] >= min)
						{
							out = this.chunkmodel.getOutcome(p);
							if (validator.validSequenceForChunk(numSeq, posTree.get(i), tmpOutcomes, out))
							{
								ns = new ChunkSequence(top, out, scores[p], i);
								if (ns.getScore() > minSequenceScore)
								{
									next.add(ns);
								}
							}
						}
					}
					if (next.size() == 0)
					{
						for (p = 0; p < scores.length; ++p)
						{
							out = this.chunkmodel.getOutcome(p);
							if (validator.validSequenceForChunk(numSeq, posTree.get(i), tmpOutcomes, out))
							{
								ns = new ChunkSequence(top, out, scores[p], i);
								if (ns.getScore() > minSequenceScore)
								{
									next.add(ns);
								}
							}
						}
					}
				}
				prev.clear();
				PriorityQueue<ChunkSequence> tmp = prev;
				prev = next;
				next = tmp;
			}

			for (seqIndex = 0; seqIndex < prev.size(); ++seqIndex)
			{
				if (prev.peek().getOutcomes().size() == 0)
				{
					prev.remove();
				}
				else
				{
					kRes.add(prev.remove());
				}
			}
		}

		if (kRes.size() == 0)
		{
			return null;
		}
		else
		{
			int trueResultNum = Math.min(kRes.size(), num);// 防止得到的最终结果小于你需要得到的结果
			ChunkSequence[] result = new ChunkSequence[trueResultNum];
			for (int j = 0; j < trueResultNum; j++)
			{
				result[j] = kRes.remove();
			}
			return result;
		}
	}

	/**
	 * 得到最好的num个结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param posTree
	 *            pos步得到的最好的K棵
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	@Override
	public ChunkSequence[] bestSequencesForChunk(int num, List<List<HeadTreeNode>> posTree,
			Object[] ac, ParserContextGenerator generator,
			ParserSequenceValidator validator)
	{

		return this.bestSequencesForChunk(num, posTree, ac, -1000.0D, generator, validator);
	}

	/**
	 * 得到最好的BuildAndCheck结果
	 * 
	 * @param comnineChunkTree
	 *            chunk步得到的最好的K棵树合并之后
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	@Override
	public BuildAndCheckSequence<HeadTreeNode> bestSequenceForBuildAndCheck(
			List<List<HeadTreeNode>> comnineChunkTree, Object[] ac, ParserContextGenerator generator,
			ParserSequenceValidator validator)
	{
		BuildAndCheckSequence<HeadTreeNode>[] sequences = this.bestSequencesForBuildAndCheck(1,
				comnineChunkTree, ac, generator, validator);
		return sequences.length > 0 ? sequences[0] : null;
	}

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param comnineChunkTree
	 *            chunk步得到的最好的K棵树合并之后
	 * @param ac
	 *            额外的信息
	 * @param min
	 *            得分最低的限制
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BuildAndCheckSequence<HeadTreeNode>[] bestSequencesForBuildAndCheck(int num,
			List<List<HeadTreeNode>> comnineChunkTree, Object[] ac, double minSequenceScore,
			ParserContextGenerator generator, ParserSequenceValidator validator)
	{
		PriorityQueue<BuildAndCheckSequence<HeadTreeNode>> kRes = new PriorityQueue<>(this.size);
		// 遍历K个结果
		for (int i = 0; i < comnineChunkTree.size(); i++)
		{
//			System.out.println("Build+Check" + i);
			PriorityQueue<BuildAndCheckSequence<HeadTreeNode>> prev = new PriorityQueue<>(
					this.size);
			PriorityQueue<BuildAndCheckSequence<HeadTreeNode>> next = new PriorityQueue<>(
					this.size);

			// 存在这样的情况，chunk步得到的不是子树序列，而是一棵子树，这种情况就不能用下面的方法处理了
			// chunk得到一颗子树的时候，prev不加入任何东西，size是0,不会进入下面的那种情况进行处理
			// 此时是一颗子树，进行build和check，处理完加入kRes代表最终的结果，如果不能build和check将chunk子树加入结果
			if (comnineChunkTree.get(i).size() == 1)
			{
				BuildAndCheckSequence<HeadTreeNode> top = new BuildAndCheckSequence<HeadTreeNode>(
						comnineChunkTree.get(i));// 取出beam size个结果中的第一个
				double temScore = top.getScore();
				String[] contextsForBuild = generator.getContextForBuildForTest(0, top.getTree(), ac);
				double[] scoresForBuild;
				// 得到每个类别的分数
				if (this.contextsCache != null)
				{
					scoresForBuild = (double[]) this.contextsCache.get(contextsForBuild);
					if (scoresForBuild == null)
					{
						scoresForBuild = this.buildmodel.eval(contextsForBuild, this.buildprobs);
						this.contextsCache.put(contextsForBuild, scoresForBuild);
					}
				}
				else
				{
					scoresForBuild = this.buildmodel.eval(contextsForBuild, this.buildprobs);
				}
				// temp_scores的作用就是取出第前beam size个的分数的界限
				double[] temp_scoresscoresForBuild = new double[scoresForBuild.length];
				System.arraycopy(scoresForBuild, 0, temp_scoresscoresForBuild, 0, scoresForBuild.length);// 数组的复制
				Arrays.sort(temp_scoresscoresForBuild);// 排序
				// 取beam size位置的值，保证取的分数值在前beam size个
				double min = temp_scoresscoresForBuild[Math.max(0, scoresForBuild.length - this.size)];

				int p;
				String out;
				for (p = 0; p < scoresForBuild.length; ++p)
				{
					if (scoresForBuild[p] >= min)
					{
						out = this.buildmodel.getOutcome(p);
						if (validator.validSequenceForBuildAndCheck(0, top.getTree(), out))
						{

							List<HeadTreeNode> copy = new ArrayList<>(top.getTree());
							String[] contextsForCheck = generator.getContextForCheckForTest(0, top.getTree(), out, ac);
							double[] scoresForCheck = this.checkmodel.eval(contextsForCheck);
							// 找到yes no的概率
							double yes = 0;
							double no = 0;
							for (int j = 0; j < scoresForCheck.length; j++)
							{
								String outCheck = this.checkmodel.getOutcome(j);
								if (outCheck.equals("yes"))
								{
									yes = scoresForCheck[j];
								}
								else if (outCheck.equals("no"))
								{
									no = scoresForCheck[j];
								}
							}

							if (yes >= no)
							{
								if (temScore + scoresForBuild[p] + yes > minSequenceScore)
								{
									// 新出的动作，加入树
									HeadTreeNode outnode = new HeadTreeNode(out);
									outnode.addChild(copy.get(0));
									copy.set(0, outnode);

									// 下面开始合并
									if (out.split("_")[0].equals("start"))
									{
										HeadTreeNode combine = new HeadTreeNode(out.split("_")[1]);
										combine.setHeadWord(copy.get(0).getHeadWord());
										combine.setHeadPos(copy.get(0).getHeadPos());
										combine.addChild(copy.get(0).getFirstChild());
										copy.get(0).getFirstChild().setParent(combine);
										copy.set(0, combine);
										kRes.add(new BuildAndCheckSequence<HeadTreeNode>(top, copy,
												scoresForBuild[p], yes, 0));
									}
								}
							}
							else if (yes < no)
							{
								// 为no的时候，且要处理的树的编号是最后一棵树，且标记为no，证明没法构建出一颗完整的树就退出
								if (0 == top.getTree().size() - 1)
								{
									continue;
								}
							}
						}
					}
				}

				if (next.size() == 0)
				{
					for (p = 0; p < scoresForBuild.length; ++p)
					{
						out = this.buildmodel.getOutcome(p);
						if (validator.validSequenceForBuildAndCheck(0, top.getTree(), out))
						{
							List<HeadTreeNode> copy = new ArrayList<>(top.getTree());
							String[] contextsForCheck = generator.getContextForCheckForTest(0, top.getTree(), out, ac);
							double[] scoresForCheck = this.checkmodel.eval(contextsForCheck);
							// 排序
							double[] temp_scoresForCheck = new double[scoresForCheck.length];
							System.arraycopy(scoresForCheck, 0, temp_scoresForCheck, 0, scoresForCheck.length);// 数组的复制
							Arrays.sort(temp_scoresForCheck);// 排序
							// 找到yes no的概率
							double yes = 0;
							double no = 0;
							for (int j = 0; j < scoresForCheck.length; j++)
							{
								String outCheck = this.checkmodel.getOutcome(j);
								if (outCheck.equals("yes"))
								{
									yes = scoresForCheck[j];
								}
								else if (outCheck.equals("no"))
								{
									no = scoresForCheck[j];
								}
							}

							if (yes >= no)
							{
								if (temScore + scoresForBuild[p] + yes > minSequenceScore)
								{
									// 新出的动作，加入树
									HeadTreeNode outnode = new HeadTreeNode(out);
									outnode.addChild(copy.get(0));
									copy.set(0, outnode);

									// 下面开始合并
									if (out.split("_")[0].equals("start"))
									{
										HeadTreeNode combine = new HeadTreeNode(out.split("_")[1]);
										combine.setHeadWord(copy.get(0).getHeadWord());
										combine.setHeadPos(copy.get(0).getHeadPos());
										combine.addChild(copy.get(0).getFirstChild());
										copy.get(0).getFirstChild().setParent(combine);
										copy.set(0, combine);
										kRes.add(new BuildAndCheckSequence<HeadTreeNode>(top, copy,
												scoresForBuild[p], yes, 0));
									}
								}
							}
							else if (yes < no)
							{
								// 为no的时候，且要处理的树的编号是最后一棵树，且标记为no，证明没法构建出一颗完整的树就退出
								if (0 == top.getTree().size() - 1)
								{
									continue;
								}
							}
						}
					}
				}
			}
			else
			{
				prev.add(new BuildAndCheckSequence<HeadTreeNode>(comnineChunkTree.get(i)));
			}
			// 此时kRes如果size为0，证明这一棵chunk子树没有build和check步骤，然后直接将chunk步骤加入
			if (kRes.size() == 0 && comnineChunkTree.get(i).size() == 1)
			{
				BuildAndCheckSequence<HeadTreeNode> top = new BuildAndCheckSequence<HeadTreeNode>(
						comnineChunkTree.get(i));
				kRes.add(new BuildAndCheckSequence<HeadTreeNode>(top, top.getTree(), 0, 0, 0));
			}

			// 下面处理的情况是chunk步骤得到的是子树序列，不是一颗子树的情况
			if (ac == null)
			{
				ac = EMPTY_ADDITIONAL_CONTEXT;
			}
			int numSeq;
			int seqIndex;
			int topSequences = Math.min(this.size, prev.size());
			// 遍历前topSequences个序列
			for (seqIndex = 0; prev.size() > 0 && seqIndex < topSequences; ++seqIndex)
			{

				if (prev.peek().getTree().size() != 1)
				{// 不是一颗完整的树的时候需要处理
					BuildAndCheckSequence<HeadTreeNode> top = prev.remove();// 取出beam size个结果中的第一个
					double temScore = top.getScore();
					numSeq = top.getBegin();// 要处理的树的编号
					String[] contextsForBuild = generator.getContextForBuildForTest(numSeq, top.getTree(), ac);
					double[] scoresForBuild;
					// 得到每个类别的分数
					if (this.contextsCache != null)
					{
						scoresForBuild = (double[]) this.contextsCache.get(contextsForBuild);
						if (scoresForBuild == null)
						{
							scoresForBuild = this.buildmodel.eval(contextsForBuild, this.buildprobs);
							this.contextsCache.put(contextsForBuild, scoresForBuild);
						}
					}
					else
					{
						scoresForBuild = this.buildmodel.eval(contextsForBuild, this.buildprobs);
					}
					// temp_scores的作用就是取出第前beam size个的分数的界限
					double[] temp_scoresscoresForBuild = new double[scoresForBuild.length];
					System.arraycopy(scoresForBuild, 0, temp_scoresscoresForBuild, 0, scoresForBuild.length);// 数组的复制
					Arrays.sort(temp_scoresscoresForBuild);// 排序
					// 取beam size位置的值，保证取的分数值在前beam size个
					double min = temp_scoresscoresForBuild[Math.max(0, scoresForBuild.length - this.size)];

					int p;
					String out;
					BuildAndCheckSequence<HeadTreeNode> ns = null;
					for (p = 0; p < scoresForBuild.length; ++p)
					{
						if (scoresForBuild[p] >= min)
						{
							out = this.buildmodel.getOutcome(p);
							if (validator.validSequenceForBuildAndCheck(numSeq, top.getTree(), out))
							{
								List<HeadTreeNode> copy = new ArrayList<>(top.getTree());
								String[] contextsForCheck = generator.getContextForCheckForTest(numSeq, top.getTree(),
										out, ac);
								double[] scoresForCheck = this.checkmodel.eval(contextsForCheck);
								// 找到yes no的概率
								double yes = 0;
								double no = 0;
								for (int j = 0; j < scoresForCheck.length; j++)
								{
									String outCheck = this.checkmodel.getOutcome(j);
									if (outCheck.equals("yes"))
									{
										yes = scoresForCheck[j];
									}
									else if (outCheck.equals("no"))
									{
										no = scoresForCheck[j];
									}
								}
								if (yes >= no)
								{
									if (temScore + scoresForBuild[p] + yes > minSequenceScore)
									{
										// 新出的动作，加入树
										HeadTreeNode outnode = new HeadTreeNode(out);
										outnode.addChild(copy.get(numSeq));
										copy.set(numSeq, outnode);

										int record = -1;
										// 下面开始合并
										// 如果标记为start就要合并
										if (out.split("_")[0].equals("start"))
										{
											HeadTreeNode combine = new HeadTreeNode(out.split("_")[1]);
											combine.setHeadWord(copy.get(numSeq).getHeadWord());
											combine.setHeadPos(copy.get(numSeq).getHeadPos());
											combine.addChild(copy.get(numSeq).getFirstChild());
											copy.get(numSeq).getFirstChild().setParent(combine);
											copy.set(numSeq, combine);
											ns = new BuildAndCheckSequence<HeadTreeNode>(top, copy,
													scoresForBuild[p], yes, numSeq);
											next.add(ns);
										}
										else
										{
											for (int k = numSeq - 1; k >= 0; k--)
											{
												if (copy.get(k).getNodeNameLeftPart().equals("start"))
												{
													record = k;
													break;
												}
											}
											HeadTreeNode combine = new HeadTreeNode(out.split("_")[1]);
											for (int k = record; k <= numSeq; k++)
											{
												combine.addChild(copy.get(k).getFirstChild());
												copy.get(k).getFirstChild().setParent(combine);
											}
											// 设置头结点
											combine.setHeadWord(aghw.extractHeadWord(combine,
													HeadRuleSetPTB.getNormalRuleSet(), HeadRuleSetPTB.getSpecialRuleSet()));
											combine.setHeadPos(aghw.extractHeadPos(combine,
													HeadRuleSetPTB.getNormalRuleSet(), HeadRuleSetPTB.getSpecialRuleSet()));
											copy.set(record, combine);
											// 删除用于合并的那些位置上的
											for (int k = numSeq; k >= record + 1; k--)
											{
												copy.remove(k);
											}
											ns = new BuildAndCheckSequence<HeadTreeNode>(top, copy,
													scoresForBuild[p], yes, record);
											next.add(ns);
										}
									}
								}
								else if (yes < no)
								{
									// 为no的时候，且要处理的树的编号是最后一棵树，且标记为no，证明没法构建出一颗完整的树就退出
									if (numSeq == top.getTree().size() - 1)
									{
										continue;
									}
									if (temScore + scoresForBuild[p] + no > minSequenceScore)
									{
										// 为no的时候不合并
										HeadTreeNode outnode = new HeadTreeNode(out);
										outnode.addChild(copy.get(numSeq));
										copy.set(numSeq, outnode);
										ns = new BuildAndCheckSequence<HeadTreeNode>(top, copy,
												scoresForBuild[p], no, numSeq + 1);
										next.add(ns);
									}
								}
							}
						}
					}
					if (next.size() == 0)
					{
						for (p = 0; p < scoresForBuild.length; ++p)
						{
							out = this.buildmodel.getOutcome(p);
							if (validator.validSequenceForBuildAndCheck(numSeq, top.getTree(), out))
							{
								List<HeadTreeNode> copy = new ArrayList<>(top.getTree());
								String[] contextsForCheck = generator.getContextForCheckForTest(numSeq, top.getTree(),
										out, ac);
								double[] scoresForCheck = this.checkmodel.eval(contextsForCheck);
								// 排序
								double[] temp_scoresForCheck = new double[scoresForCheck.length];
								System.arraycopy(scoresForCheck, 0, temp_scoresForCheck, 0, scoresForCheck.length);// 数组的复制
								Arrays.sort(temp_scoresForCheck);// 排序
								// 找到yes no的概率
								double yes = 0;
								double no = 0;
								for (int j = 0; j < scoresForCheck.length; j++)
								{
									String outCheck = this.checkmodel.getOutcome(j);
									if (outCheck.equals("yes"))
									{
										yes = scoresForCheck[j];
									}
									else if (outCheck.equals("no"))
									{
										no = scoresForCheck[j];
									}
								}
								if (yes >= no)
								{
									if (temScore + scoresForBuild[p] + yes > minSequenceScore)
									{
										// 新出的动作，加入树
										HeadTreeNode outnode = new HeadTreeNode(out);
										outnode.addChild(copy.get(numSeq));
										copy.set(numSeq, outnode);

										int record = -1;
										// 下面开始合并
										// 如果标记为start就要合并
										if (out.split("_")[0].equals("start"))
										{
											HeadTreeNode combine = new HeadTreeNode(out.split("_")[1]);
											combine.setHeadWord(copy.get(numSeq).getHeadWord());
											combine.setHeadPos(copy.get(numSeq).getHeadPos());
											combine.addChild(copy.get(numSeq).getFirstChild());
											copy.get(numSeq).getFirstChild().setParent(combine);
											copy.set(numSeq, combine);
											ns = new BuildAndCheckSequence<HeadTreeNode>(top, copy,
													scoresForBuild[p], yes, numSeq);
											next.add(ns);
										}
										else
										{
											for (int k = numSeq - 1; k >= 0; k--)
											{
												if (copy.get(k).getNodeNameLeftPart().equals("start"))
												{
													record = k;
													break;
												}
											}
											HeadTreeNode combine = new HeadTreeNode(out.split("_")[1]);
											for (int k = record; k <= numSeq; k++)
											{
												combine.addChild(copy.get(k).getFirstChild());
												copy.get(k).getFirstChild().setParent(combine);
											}
											// 设置头结点
											combine.setHeadWord(aghw.extractHeadWord(combine,
													HeadRuleSetPTB.getNormalRuleSet(), HeadRuleSetPTB.getSpecialRuleSet()));
											combine.setHeadPos(aghw.extractHeadPos(combine,
													HeadRuleSetPTB.getNormalRuleSet(), HeadRuleSetPTB.getSpecialRuleSet()));
											copy.set(record, combine);
											// 删除用于合并的那些位置上的
											for (int k = numSeq; k >= record + 1; k--)
											{
												copy.remove(k);
											}
											ns = new BuildAndCheckSequence<HeadTreeNode>(top, copy,
													scoresForBuild[p], yes, record);
											next.add(ns);
										}
									}
								}
								else if (yes < no)
								{
									// 为no的时候，且要处理的树的编号是最后一棵树，且标记为no，证明没法构建出一颗完整的树就退出
									if (numSeq == top.getTree().size() - 1)
									{
										continue;
									}
									if (temScore + scoresForBuild[p] + no > minSequenceScore)
									{
										// 为yes的时候要进行合并，合并的过程就是更改comnineChunkTree.get(i)
										HeadTreeNode outnode = new HeadTreeNode(out);
										outnode.addChild(copy.get(numSeq));
										copy.set(numSeq, outnode);
										ns = new BuildAndCheckSequence<HeadTreeNode>(top, copy,
												scoresForBuild[p], no, numSeq + 1);
										next.add(ns);
									}
								}
							}
						}
					}
				}
				else if (prev.peek().getTree().size() == 1)
				{// 此时说明已经组成一颗整的树了
					kRes.add(prev.remove());
				}

				if (seqIndex == topSequences - 1)
				{// 此时一个队列处理完毕
					prev.clear();
					PriorityQueue<BuildAndCheckSequence<HeadTreeNode>> tmp = prev;
					prev = next;
					next = tmp;
					seqIndex = -1;
					topSequences = Math.min(this.size, prev.size());
				}
			}
		}

		if (kRes.size() == 0)
		{
			return null;
		}
		else
		{
			int trueResultNum = Math.min(kRes.size(), num);// 防止得到的最终结果小于你需要得到的结果
			BuildAndCheckSequence<HeadTreeNode>[] result = new BuildAndCheckSequence[trueResultNum];
			for (int j = 0; j < trueResultNum; j++)
			{
				result[j] = kRes.remove();
			}
			return result;
		}
	}

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param comnineChunkTree
	 *            chunk步得到的最好的K棵树合并之后
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	public BuildAndCheckSequence<HeadTreeNode>[] bestSequencesForBuildAndCheck(int num,
			List<List<HeadTreeNode>> comnineChunkTree, Object[] ac, ParserContextGenerator generator,
			ParserSequenceValidator validator)
	{
		return this.bestSequencesForBuildAndCheck(num, comnineChunkTree, ac, -1000.0D, generator, validator);
	}
}
