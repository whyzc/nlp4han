package com.lc.nlp4han.segment.maxent;

import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.util.InvalidFormatException;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.segment.WordSegmenter;

/**
 * 中文分词工厂
 * 
 * @author 刘小峰
 *
 */
public class WordSegFactory
{
//    public final static byte MODEL_PKU = 0;
//    public final static byte MODEL_MSR = 1;
//    public final static byte MODEL_CTB = 2;
	private static WordSegmenter segmenter = null;
    
    private WordSegFactory(){}
    
    /**
     * 获得和给定模型对应的中文分词器
     * 
     * @param modelType 模型类型，MODEL_PKU或MODEL_MSR
     * @return 若模型类型不对，返回null
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static WordSegmenter getWordSegmenter() throws InvalidFormatException, IOException
    {
    	if(segmenter != null)
    		return segmenter;
    	
        String modelName = "com/lc/nlp4han/segment/ctb8-seg.model";
//        if(modelType == MODEL_MSR)
//            modelName = "com/lc/nlp4han/segment/ws-msr-utf8.model";
//        else if(modelType == MODEL_PKU)
//            modelName = "com/lc/nlp4han/segment/ws-pku-utf8.model";
//        else
//            return null;
        
        InputStream modelIn = WordSegFactory.class.getClassLoader().getResourceAsStream(modelName);
        ModelWrapper model = new ModelWrapper(modelIn);
        
        segmenter = new WordSegmenterME(model, new WordSegContextGeneratorConf());
        
        return segmenter;
    }
    
    public static void main(String[] args) throws InvalidFormatException, IOException
    {
        String text  = "这个句子分词难不难？";
        
        WordSegmenter segmenter = WordSegFactory.getWordSegmenter();
        String[] words = segmenter.segment(text);
        for(String w : words)
            System.out.println(w);
    }
}
