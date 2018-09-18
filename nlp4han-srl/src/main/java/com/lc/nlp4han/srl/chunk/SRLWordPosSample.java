package com.lc.nlp4han.srl.chunk;

import java.util.List;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.Chunk;

public class SRLWordPosSample extends AbstractChunkAnalysisSample
{

	public SRLWordPosSample(List<String> tokens, List<String> tags, String[] additionalContext)
	{
		super(tokens, tags, additionalContext);
	}
	
	public SRLWordPosSample(List<String> words, List<String> poses, List<String> chunkTags)
	{
		super(words, chunkTags, poses.toArray(new String[poses.size()]));
	}
	
	public SRLWordPosSample(String[] words, String[] poses, String[] chunkTags)
	{
		super(words, chunkTags, poses);
	}



	@Override
	public Chunk[] toChunk()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return null;
	}

}
