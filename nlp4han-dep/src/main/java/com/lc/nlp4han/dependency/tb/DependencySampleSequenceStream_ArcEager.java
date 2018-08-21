package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.ml.model.AbstractModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.Sequence;
import com.lc.nlp4han.ml.model.SequenceStream;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * @author 王宁
 * @version 创建时间：2018年7月25日 上午2:34:58 样本序列流
 */
public class DependencySampleSequenceStream_ArcEager implements SequenceStream
{
	private DependencyParseContextGenerator contextGenerator;
	private ObjectStream<DependencySample> samples;

	public DependencySampleSequenceStream_ArcEager(ObjectStream<DependencySample> samples,
			DependencyParseContextGenerator contextGenerator)
	{
		this.contextGenerator = contextGenerator;
		this.samples = samples;
	}

	@Override
	public Event[] updateContext(Sequence sequence, AbstractModel model)// 感知机算法使用
	{
//		System.out.println("使用了updateContext方法");
		Sequence<DependencySample> pss = sequence;
		try
		{
			DependencyParser_ArcEager parseTB = new DependencyParser_ArcEager(new ModelWrapper(model));
		
		DependencySample sample = (DependencySample)sequence.getSource();
		String[] words = sample.getWords();
		String[] poses = sample.getPos();
		String[][] ac = sample.getAditionalContext();
		
		DependencySample newSample = parseTB.parse(words, poses, 1)[0].getSample();
		String[] dependency = newSample.getDependency();
		String[] dependencyWords = newSample.getDependencyWords();
		String[] dependencyIndices = newSample.getDependencyIndices();
		List<Event> events =DependencySampleEventStream_ArcEager.generateEvents(words, poses, dependency, dependencyWords, dependencyIndices, ac);
		Event[] allEvents = new Event[events.size()];
		allEvents = events.toArray(allEvents);
		return allEvents;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Sequence<DependencySample> read() throws IOException
	{
		DependencySample sentenceSample = samples.read();
		if (sentenceSample != null)
		{
			DependencySampleEventStream_ArcEager es = new DependencySampleEventStream_ArcEager(samples, contextGenerator);
			List<Event> events = es.generateEvents(sentenceSample.getWords(), sentenceSample.getPos(),
					sentenceSample.getDependency(), sentenceSample.getDependencyWords(),
					sentenceSample.getDependencyIndices(), sentenceSample.getAditionalContext());
			Event[] allEvents = events.toArray(new Event[events.size()]);
			Sequence<DependencySample> sequence = new Sequence<DependencySample>(allEvents, sentenceSample);
			return sequence;
		}

		return null;
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException
	{

	}

	@Override
	public void close() throws IOException
	{

	}

}
