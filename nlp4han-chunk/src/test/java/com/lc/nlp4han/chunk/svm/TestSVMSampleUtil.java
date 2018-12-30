package com.lc.nlp4han.chunk.svm;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.ObjectStream;

public class TestSVMSampleUtil
{
	private static ConversionInformation tfi = null;
	
	@BeforeClass
	public static void before()
	{
		String contents;
		contents = "[上海/NR 浦东/NR]NP [开发/NN 与/CC 法制/NN 建设/NN]NP [同步/VV]VP ";
		
		Properties properties = new Properties();
		properties.setProperty("feature.w_2", "false");
		properties.setProperty("feature.w_1", "false");
		properties.setProperty("feature.w0", "true");  // true
		properties.setProperty("feature.w1", "false");
		properties.setProperty("feature.w2", "false");
		properties.setProperty("feature.af0", "false");
		properties.setProperty("feature.pf0", "false");
		properties.setProperty("feature.p_2", "false");
		properties.setProperty("feature.p_1", "false");
		properties.setProperty("feature.p0", "true");  // true
		properties.setProperty("feature.p1", "false");
		properties.setProperty("feature.p2", "false");
		properties.setProperty("feature.c_1", "false");
		properties.setProperty("feature.c_2", "false");
		properties.setProperty("feature.w_2w_1", "false");
		properties.setProperty("feature.w_1w0", "false");
		properties.setProperty("feature.w0w1", "false");
		properties.setProperty("feature.w1w2", "false");
		properties.setProperty("feature.w_1w1", "false");
		properties.setProperty("feature.w_2w_1w0", "false");
		properties.setProperty("feature.w_1w0w1", "false");
		properties.setProperty("feature.w0w1w2", "false");
		properties.setProperty("feature.p_2p_1", "false");
		properties.setProperty("feature.p_2p0", "false");
		properties.setProperty("feature.p_2p1", "false");
		properties.setProperty("feature.p_2p2", "false");
		properties.setProperty("feature.p_1p0", "false");
		properties.setProperty("feature.p_1p1", "false");
		properties.setProperty("feature.p_1p2", "false");
		properties.setProperty("feature.p0p1", "false");
		properties.setProperty("feature.p0p2", "false");
		properties.setProperty("feature.p1p2", "false");
		properties.setProperty("feature.p_2p_1p0", "false");
		properties.setProperty("feature.p_1p0p1", "false");
		properties.setProperty("feature.p0p1p2", "false");
		properties.setProperty("feature.p_2p0p1", "false");
		properties.setProperty("feature.p_1p1p2", "false");
		properties.setProperty("feature.c_2c_1", "false");
		properties.setProperty("feature.w_1p_2", "false");
		properties.setProperty("feature.w_1p_1", "false");
		properties.setProperty("feature.w_1p0", "false");
		properties.setProperty("feature.w_1p1", "false");
		properties.setProperty("feature.w_1p2", "false");
		properties.setProperty("feature.w0p_2", "false");
		properties.setProperty("feature.w0p_1", "false");
		properties.setProperty("feature.w0p0", "false");
		properties.setProperty("feature.w0p1", "false");
		properties.setProperty("feature.w0p2", "false");
		properties.setProperty("feature.w1p_2", "false");
		properties.setProperty("feature.w1p_1", "false");
		properties.setProperty("feature.w1p0", "false");
		properties.setProperty("feature.w1p1", "false");
		properties.setProperty("feature.w1p2", "false");
		properties.setProperty("feature.w_2c_2", "false");
		properties.setProperty("feature.w_2c_1", "false");
		properties.setProperty("feature.w_1c_2", "false");
		properties.setProperty("feature.w_1c_1", "false");
		properties.setProperty("feature.w0c_2", "false");
		properties.setProperty("feature.w0c_1", "false");
		properties.setProperty("feature.w1c_2", "false");
		properties.setProperty("feature.w1c_1", "false");
		properties.setProperty("feature.w2c_2", "false");
		properties.setProperty("feature.w2c_1", "false");
		properties.setProperty("feature.p_2c_2", "false");
		properties.setProperty("feature.p_2c_1", "false");
		properties.setProperty("feature.p_1c_2", "false");
		properties.setProperty("feature.p_1c_1", "false");
		properties.setProperty("feature.p0c_2", "false");
		properties.setProperty("feature.p0c_1", "false");
		properties.setProperty("feature.p1c_2", "false");
		properties.setProperty("feature.p1c_1", "false");
		properties.setProperty("feature.p2c_2", "false");
		properties.setProperty("feature.p2c_1", "false");
		properties.setProperty("feature.w_2p_1p0", "false");
		properties.setProperty("feature.w0p_1p0", "false");
		properties.setProperty("feature.w0p0p1", "false");
		properties.setProperty("feature.w1p0p1", "false");
		properties.setProperty("feature.p_1p0c_1", "false");
		properties.setProperty("feature.w2p2", "false");
		properties.setProperty("feature.p0p1c_1", "false");
		properties.setProperty("feature.w1p_1p0", "false");
		properties.setProperty("feature.w0w1p1", "false");
		properties.setProperty("feature.w0w2p2", "false");
		properties.setProperty("feature.w_1w0p_1", "false");
		
		ObjectStream<Event> es = null;
		try
		{
			es = SVMSampleUtil.getEventStream(contents, "BIEOS", properties);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		tfi = new ConversionInformation(es);
	}
	
	@Test
	public void testOneSample()
	{
		String[] context = new String[2];
		context[0] = "w0=浦东";
		context[1] = "p0=NR";
		
		String actual = SVMSampleUtil.oneSample(context, tfi);
		
		assertEquals("2:1 3:1", actual);
	}
	
	@Test
	public void testOneSample_2()
	{
		String[] context = new String[2];
		context[0] = "w0=浦东";
		context[1] = "p0=NR";
		Event event = new Event("NP_E", context);
		
		String actual = SVMSampleUtil.oneSample(event, tfi);
		
		assertEquals("2 2:1 3:1", actual); 
	}
}
