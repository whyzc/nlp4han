package com.lc.nlp4han.srl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.lc.nlp4han.srl.tree.SRLMeasure;

/**
 * 指标计算类的测试
 * @author 王馨苇
 *
 */
public class SRLMeasureTest {
	
	@Test
	public void testMeasure(){	
		SRLMeasure measure = new SRLMeasure();
		String[] ref = new String[]{"ARG0","NULL","ARGM-DIS","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL"};
		String[] pre = new String[]{"NULL","NULL","ARGM-DIS","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL","NULL"};
		measure.update(ref, pre);
		
		assertEquals(measure.getPrecisionScore(), 1.0, 0.001);
		assertEquals(measure.getRecallScore(), 0.5, 0.001);
		assertEquals(measure.getMeasure(), 0.666, 0.001);
	}
}
