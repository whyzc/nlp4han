package com.lc.nlp4han.dependency.tb;

import java.io.IOException;

import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.ml.model.AbstractModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.Sequence;
import com.lc.nlp4han.ml.model.SequenceStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
* @author 作者
* @version 创建时间：2018年8月19日 上午9:56:19
* 类说明
*/
public class DependencySampleSequenceStream_ArcStandard implements SequenceStream
{
	private DependencyParseContextGenerator contextGenerator;
	private ObjectStream<DependencySample> samples;

	public DependencySampleSequenceStream_ArcStandard(ObjectStream<DependencySample> samples,
			DependencyParseContextGenerator contextGenerator)
	{
		this.contextGenerator = contextGenerator;
		this.samples = samples;
	}

	@Override
	public Sequence read() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Event[] updateContext(Sequence sequence, AbstractModel model) {
		// TODO Auto-generated method stub
		return null;
	}

}
