package com.lc.nlp4han.chunk.svm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.ObjectStream;

public class TestConversionInformation
{
	private static SVMFeatureLabelInfo tfi = null;

	@BeforeClass
	public static void before() throws IOException
	{
		String contents;
		contents = "[上海/NR 浦东/NR]NP [开发/NN 与/CC 法制/NN 建设/NN]NP [同步/VV]VP";

		Properties properties = new Properties();
		properties.setProperty("feature.w_2", "false");
		properties.setProperty("feature.w_1", "false");
		properties.setProperty("feature.w0", "true"); // true
		properties.setProperty("feature.w1", "false");
		properties.setProperty("feature.w2", "false");
		properties.setProperty("feature.af0", "false");
		properties.setProperty("feature.pf0", "false");
		properties.setProperty("feature.p_2", "false");
		properties.setProperty("feature.p_1", "false");
		properties.setProperty("feature.p0", "true"); // true
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

		tfi = new SVMFeatureLabelInfo(es);
	}

	@Test
	public void testGetFeatureIndex()
	{
		String key = "w0=上海";
		int actual = tfi.getFeatureIndex(key);

		assertEquals(1, actual);

		key = "abc";
		int actual2 = tfi.getFeatureIndex(key);

		assertEquals(-1, actual2);

		key = "p0=NR";
		int actual3 = tfi.getFeatureIndex(key);

		assertEquals(2, actual3);
	}

	@Test
	public void testGetClassificationValue()
	{
		String classification;
		classification = "NP_B";

		int actual1 = tfi.getClassIndex(classification);

		int expected1 = 1;

		assertEquals(expected1, actual1);

		classification = "VP_S";

		int actual2 = tfi.getClassIndex(classification);

		int expected2 = 4;

		assertEquals(expected2, actual2);

		classification = "asdf";

		int actual3 = tfi.getClassIndex(classification);

		int expected3 = -1;

		assertEquals(expected3, actual3);
	}

	@Test
	public void testContainsFeature()
	{
		String key1 = "w0=上海";

		boolean actual1 = tfi.containsFeature(key1);

		assertTrue(actual1);

		String key2 = "abc";

		boolean actual2 = tfi.containsFeature(key2);

		assertFalse(actual2);
	}

	@Test
	public void testContainsClassificationLabel()
	{
		String key1 = "VP_S";

		boolean actual1 = tfi.containsClassLabel(key1);

		assertTrue(actual1);

		String key2 = "abc";

		boolean actual2 = tfi.containsClassLabel(key2);

		assertFalse(actual2);
	}

	@Test
	public void testFeatureSet()
	{
		Set<String> features = tfi.featureSet();

		assertTrue(features.contains("w0=同步"));
		assertTrue(features.contains("p0=NR"));
		assertTrue(features.contains("w0=法制"));
		assertTrue(features.contains("w0=建设"));
		assertTrue(features.contains("w0=开发"));
		assertTrue(features.contains("w0=浦东"));
		assertTrue(features.contains("p0=NN"));
		assertTrue(features.contains("w0=与"));
		assertTrue(features.contains("p0=CC"));
		assertTrue(features.contains("p0=VV"));
		assertTrue(features.contains("w0=上海"));
	}

	@Test
	public void testGetFeaturesNumber()
	{
		int actual = tfi.getFeaturesNumber();

		assertEquals(11, actual);
	}

	@Test
	public void testGetClassificationLabelNumber()
	{
		int actual = tfi.getClassesNumber();
		assertEquals(4, actual);
	}

	@Test
	public void testGet()
	{
		int actual = tfi.getSamplesNumber();
		assertEquals(7, actual);
	}

	@Test
	public void testGetClassificationLabel()
	{
		int value = 2;
		String actual = tfi.getClassLabel(value);

		assertEquals("NP_E", actual);
	}

}
