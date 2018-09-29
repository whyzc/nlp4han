package com.lc.nlp4han.chunk.svm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.Chunk;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.Chunker;
import com.lc.nlp4han.chunk.svm.libsvm.svm_model;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSample;

public class ChunkAnalysisSVMME implements Chunker
{
	private List<String> FeatureStructure = new ArrayList<String>();		//存储用到的特征，如w_2, p_2，用以记录各特征对应的序号，特征的（index+1）为SVM标准输入格式中该特征的序号
	private List<String> ClassificationResults = new ArrayList<String>();	//存储分类结果，如BNP_B, BNP_I, BNP_E, O，（index+1）为SVM标准分类结果
	private Map<String, Map<String, Integer>> Features = new HashMap<String, Map<String, Integer>>();	//记录所有具体的特征，为每一特征赋值
	private ChunkAnalysisContextGenerator contextgenerator;		//ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordPosContextGeneratorConf(properties);
	private svm_model model;	//svm_model model = svm.svm_load_model(model_file);
	private String label;
	
	public ChunkAnalysisSVMME(ChunkAnalysisContextGenerator contextgenerator, svm_model model, String filePath, String label)
	{
		super();
		this.contextgenerator = contextgenerator;
		this.model = model;
		this.label = label;
		init(filePath);
	}

	private void init(String filePath)
	{
		FileInputStream fis = null;
		BufferedReader reader = null;

		try
		{
			fis = new FileInputStream(new File(filePath));

			reader = new BufferedReader(new InputStreamReader(fis, "utf-8"));

			String tempString = null;
			
			int lineNum = 1;
			int num1 = 0;
			int num2 = 0;
			String key = null;
			int num3 = 0;
			Map<String, Integer> value = null;

			while ((tempString = reader.readLine()) != null)
			{
				String[] strs = null;
				if (lineNum == 1)
				{
					 strs = tempString.split("=");
					 num1 = str2int(strs[1]) + 1;
					 num2 = num1 +1;
					 lineNum++;
				}
				else if (lineNum <= num1)
				{
					FeatureStructure.add(tempString);
					lineNum++;
				}
				else if (lineNum == num1 + 1)
				{
					strs = tempString.split("=");
					num2 = str2int(strs[1]) + num1 + 1;
					num3 = num2;
					lineNum++;
				}
				else if (lineNum <= num2)
				{
					ClassificationResults.add(tempString);
					lineNum++;
				}
				else
				{
					if (lineNum == num3 + 1)
					{
						strs = tempString.split(" ");
						if (strs.length == 2 && strs[0].matches("[a-z]+") && strs[1].matches("[0-9]+"))
						{
							value = new HashMap<String, Integer>();
							key = strs[0];
							int tmp = str2int(strs[1]);
							if (tmp % 100 == 0)
							{
								num3 += tmp/100 + 1;
							}
							else
							{
								num3 += tmp/100 + 2;
							}
						}
						lineNum++;
					}
					else
					{
						strs = tempString.split(" ");
						for (int j=0 ; j<strs.length ; j++)
						{
							//TODO: 注意，此处的值为int型
							value.put(strs[j].split("=")[0], str2int(strs[j].split("=")[1]));
						}
						if (lineNum == num3)
						{
							Features.put(key, value);
						}
						lineNum++;
					}
				}
			}

			reader.close();

		}
		catch (IOException e)
		{

			e.printStackTrace();

		}
		finally
		{

			if (reader != null)
			{

				try
				{
					reader.close();
				}
				catch (IOException e1)
				{

				}

			}

		}
		
	}

	@Override
	public Chunk[] parse(String sentence)
	{
		String[] wordTags = sentence.split(" +");
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();

		
		for (String wordTag : wordTags)
		{
			words.add(wordTag.split("/")[0]);
			poses.add(wordTag.split("/")[1]);
		}

		String[] chunkTypes = null;
		try
		{
			chunkTypes = tag(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AbstractChunkAnalysisSample sample = new ChunkAnalysisWordPosSample(words.toArray(new String[words.size()]),
				poses.toArray(new String[poses.size()]), chunkTypes);
		sample.setTagScheme(label);

		return sample.toChunk();
	}

	@Override
	public Chunk[][] parse(String sentence, int k)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public String[] tag(String[] words, String[] poses) throws IOException
	{
		String[] chunkTags = new String[words.length];
		String line = null;
		
		for (int i=0 ; i<words.length ; i++)
		{
			String[] context = contextgenerator.getContext(i, words, chunkTags, poses);
			line = "1 " + SVMStandardInput.getSVMStandardFeaturesInput(context, FeatureStructure, Features);	//<label> <index1>:<value1> <index2>:<value2> ... 预测时，label可以为任意值
			String tag = predict(line, model);
			chunkTags[i] = tag;
		}
		return chunkTags;
	}
	
	
	/**
	 * 根据模型model，调用libsvm预测line的结果，line为libsvm的标准输入格式，形如 2 4:5 7:3 8:2....
	 */
	public String predict(String line, svm_model model) throws IOException
	{
		//TODO
		String v = svm_predict.predict(line, model, 0);
		String result = transform(v);
		return result;
	}

	/**
	 * 将libsvm预测的结果（数字）转换成组块标注
	 */
	private String transform(String v)
	{
		int t = str2int(v);
		String result = ClassificationResults.get(t-1);
		return result;
	}
	
	private int str2int(String str)
	{
		return Integer.valueOf(str.trim().split("\\.")[0]);
	}
	
	private double str2dou(String str)
	{
		return Double.valueOf(str.trim());
	}
	

}
