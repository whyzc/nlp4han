package org.nlp4han.sentiment.nb;

import java.util.Iterator;

import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

public class SentimentAnalyzerEventStream extends AbstractEventStream<SentimentTextSample>{
	
	private SentimentAnalyzerContextGenerator contextGen;

	public SentimentAnalyzerEventStream(ObjectStream<SentimentTextSample> samples,
			SentimentAnalyzerContextGenerator contextGen) {
		super(samples);
		this.contextGen = contextGen;
	}

	@Override
	protected Iterator<Event> createEvents(final SentimentTextSample sample) {
		
		return new Iterator<Event>() {

		      private boolean isVirgin = true;

		      public boolean hasNext() {
		        return isVirgin;
		      }

		      public Event next() {	    	  

		        isVirgin = false;

		        return new Event(sample.getCategory(),
		            contextGen.getContext(sample.getText(), sample.getExtraInformation()));
		      }

		      public void remove() {
		        throw new UnsupportedOperationException();
		      }
		    };
	}

}
