package com.lc.nlp4han.chunk;

import com.lc.nlp4han.ml.util.BeamSearchContextGenerator;

/**
 * 基于词的组块分析模型特征生成接口
 */
public interface ChunkAnalysisContextGenerator extends BeamSearchContextGenerator<String>
{

	@Override
	String[] getContext(int index, String[] words, String[] chunkTags, Object[] additionalContext);
}
