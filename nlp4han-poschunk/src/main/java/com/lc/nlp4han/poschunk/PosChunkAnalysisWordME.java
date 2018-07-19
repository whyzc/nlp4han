package com.lc.nlp4han.poschunk;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.Chunk;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.Chunker;
import com.lc.nlp4han.chunk.word.ChunkAnalysisWordSampleEvent;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.Sequence;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.ml.util.TrainingParameters;


/**
 *<ul>
 *<li>Description: 基于词的词性标注和组块分析模型训练类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class PosChunkAnalysisWordME implements Chunker {
	
	public static final int DEFAULT_BEAM_SIZE = 33;
	private ChunkAnalysisContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
    private SequenceValidator<String> sequenceValidator;
    private String label;

    public PosChunkAnalysisWordME() {

    }
	
    /**
     * 构造方法
     * @param model			组块分析模型
     * @param contextGen	上下文生成器
     */
	public PosChunkAnalysisWordME(ModelWrapper model, SequenceValidator<String> sequenceValidator,
			ChunkAnalysisContextGenerator contextGen, String label) {
		this.sequenceValidator = sequenceValidator;
		this.label = label;
		init(model , contextGen);
	}
	
    /**
     * 初始化工作
     * @param model 		组块分析模型
     * @param contextGen	上下文生成器
     */
	private void init(ModelWrapper model, ChunkAnalysisContextGenerator contextGen) {
		int beamSize = PosChunkAnalysisWordME.DEFAULT_BEAM_SIZE;

        contextGenerator = contextGen;
        size = beamSize;
        
        this.model = model.getSequenceModel();
	}

	/**
	 * 训练模型
	 * @param languageCode 	编码
	 * @param sampleStream 	文件流
	 * @param contextGen 	特征
	 * @param encoding 		编码
	 * @return 				模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ModelWrapper train(ObjectStream<AbstractChunkAnalysisSample> sampleStream, TrainingParameters params,
			ChunkAnalysisContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = PosChunkAnalysisWordME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        ClassificationModel maxentModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> chunkClassificationModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new ChunkAnalysisWordSampleEvent(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            maxentModel = trainer.train(es);                       
        }

        if (maxentModel != null) {
			return new ModelWrapper(maxentModel, beamSize);
		} else {
			return new ModelWrapper(chunkClassificationModel);
		}
	}
	
	/**
	 * 得到最好的numTaggings个标记序列
	 * @param numTaggings 	个数
	 * @param words 		一个个词语
	 * @return 				分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] words) {
        Sequence[] bestSequences = model.bestSequences(numTaggings, words, null, contextGenerator, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        List<String> temp = new ArrayList<>();
        
        for (int si = 0; si < tags.length; si++) {
        	temp = bestSequences[si].getOutcomes();
        	tags[si] = temp.toArray(new String[temp.size()]);
        }
        
        return tags;
    }

	/**
	 * 返回词组的组块标注结果
	 * @param words	词组
	 * @return		词组的组块标注结果
	 */
	public String[] tag(String[] words){
		return tag(words, null);
	}
	
	/**
	 * 返回词组的组块标注结果
	 * @param words				词组
	 * @param additionaContext	其他上下文信息
	 * @return					词组的组块标注结果
	 */
	public String[] tag(String[] words, Object[] additionaContext){
		bestSequence = model.bestSequence(words, additionaContext, contextGenerator, sequenceValidator);
		List<String> temp = bestSequence.getOutcomes();
		
		return temp.toArray(new String[temp.size()]);
	}

	/**
	 * 根据给定词组，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @return		最优的K个标注序列
	 */
	public Sequence[] getTopKSequences(String[] words) {
		return getTopKSequences(words, null);
	}

	/**
	 * 根据给定词组及其词性，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @param additionaContext
	 * @return		最优的K个标注序列
	 */
    public Sequence[] getTopKSequences(String[] words, Object[] additionaContext) {
        return model.bestSequences(size, words, additionaContext, contextGenerator, sequenceValidator);
    }

    @Override
   	public Chunk[] parse(String sentence) {
    	String[] words = sentence.split("//s+");
		String[] posChunkTypes = tag(words);
		
		AbstractChunkAnalysisSample sample = new PosChunkAnalysisBasedWordSample(words, posChunkTypes);
		sample.setTagScheme(label);
		
		return sample.toChunk();
   	}

   	@Override
   	public Chunk[][] parse(String sentence, int k) {
   		String[] words = sentence.split("//s+");
		
		String[][] chunkTypes = tag(k, words);
		Chunk[][] chunks = new Chunk[chunkTypes.length][];
		for(int i = 0; i < chunkTypes.length; i++) {
			String[] chunkSequences = chunkTypes[i];
						
			AbstractChunkAnalysisSample sample = new PosChunkAnalysisBasedWordSample(words, chunkSequences);
			sample.setTagScheme(label);
			chunks[i] = sample.toChunk();
		}
		
		return chunks;
   	}
}

