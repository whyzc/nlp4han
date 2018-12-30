package com.lc.nlp4han.chunk.svm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.ObjectStream;

public class ConversionInformation
{
	private Map<String, Integer> features = new HashMap<String, Integer>();  // key为特征，如"w0=上海"；vaule为该特征对应于"<label> <index1>:<value1> <index2>:<value2>..."中的index，从1开始

	private List<Integer> numberOfFeatures = new ArrayList<Integer>();  // 记录各特征的数量，features中特征的value值-1对应该列表的索引

	private Map<String, Integer> classificationLabels = new HashMap<String, Integer>();  // key为分类的标签，如"NN_B"，"VP_I"，vaule为该特征对应于"<label> <index1>:<value1> <index2>:<value2>..."中的label，从1开始

	private List<Integer> numberOfClassification = new ArrayList<Integer>();  // 记录各分类标签的数量，classificationLabels中分类标签value值-1对应该列表的索引

	private int totalSamplesNumber = -1;
	
	public ConversionInformation(ObjectStream<Event> es)
	{
		try
		{
			init(es);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	

	public ConversionInformation(String filePath, String encoding)
	{
		try
		{
			deserialization(filePath, encoding);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public  ConversionInformation(String filePath)
	{
		this(filePath, "UTF-8");
	}

	private void init(ObjectStream<Event> es) throws IOException
	{
		Event event = null;
		while ((event = es.read()) != null)
		{
			Integer index = null;
			if ((index = classificationLabels.get(event.getOutcome())) == null)
			{
				classificationLabels.put(event.getOutcome(), classificationLabels.size()+1);

				numberOfClassification.add(1);
			}
			else
			{
				numberOfClassification.set(index-1, numberOfClassification.get(index-1) + 1);
			}

			String[] contexts = event.getContext();

			for (int i = 0; i < contexts.length; i++)
			{

				if ((index = features.get(contexts[i])) == null)
				{
					features.put(contexts[i], features.size() + 1);

					numberOfFeatures.add(1);
				}
				else
				{
					numberOfFeatures.set(index - 1, numberOfFeatures.get(index - 1) + 1);
				}

			}
		}
	}
	
	/**
	 * 根据特征（如"w0=上海"），获取其对应SVM输入中的index值；若不存在该特征，返回-1
	 * @param feature 特征
	 * @return SVM输入中对应的index值；若不存在该特征，返回-1
	 */
	public int getFeatureIndex(String feature)
	{
		Integer result = this.features.get(feature);
		if (result != null)
			return result;
		else
			return -1;
	}

	/**
	 * 根据类别标签（如"NP_B"），获取其对应SVM输入中的label值；若不存在该特征，返回-1
	 * @param classificationLable 类别标签
	 * @return SVM输入中对应的label值；若不存在该特征，返回-1
	 */
	public int getClassificationValue(String classificationLable)
	{
		Integer result = classificationLabels.get(classificationLable);
		if (result != null)
			return result;
		else
			return -1;
	}
	
	/**
	 * 获取类别标签的种类数
	 * @return
	 */
	public int getClassificationLabelNumber()
	{
		return classificationLabels.size();
	}

	/**
	 * 是否包含特征
	 * @param featureKey 特征
	 * @return 包含，则返回true；否则，返回false
	 */
	public boolean containsFeature(String featureKey)
	{
		return features.containsKey(featureKey);
	}
	
	/**
	 * 是否包含类别标签
	 * @param classificationLabel 类别标签
	 * @return 包含，则返回true；否则，返回false
	 */
	public boolean containsClassificationLabel(String classificationLabel)
	{
		return this.classificationLabels.containsKey(classificationLabel);
	}

	/**
	 * 获取特征集合
	 * @return 特征集合
	 */
	public Set<String> featureSet()
	{
		return this.features.keySet();
	}
	
	/**
	 * 获取类别标签集合
	 * @return 类别标签集合
	 */
	public Set<String> classificationSet()
	{
		return this.classificationLabels.keySet();
	}

	/**
	 * 特征总个数
	 * @return 特征总个数
	 */
	public int getFeaturesNumber()
	{
		return this.features.size();
	}
	
	/**
	 * 获取某个特征数量
	 * @param feature 特征
	 * @return 特征数量
	 */
	public int getFeatureNumber(String feature)
	{
		int index = getFeatureIndex(feature);
		if (index == -1)
			return 0;
		return numberOfFeatures.get(index-1);
		
	}
	
	/**
	 * 获取某个类别标签的数量
	 * @param classificationLabel 类别标签 
	 * @return 类别标签的数量
	 */
	public int getClassificationNumber(String classificationLabel)
	{
		int index = getClassificationValue(classificationLabel);
		if (index == -1)
			return 0;
		return numberOfClassification.get(index-1);
	}

	/**
	 * 根据SVM输入中的label值获取类别标签字符串
	 * @param classificationValue SVM输入中的label值
	 * @return 类别标签字符串
	 */
	public String getClassificationLabel(int classificationValue)
	{
		Set<Entry<String, Integer>> labelSet = this.classificationLabels.entrySet();
		
		for (Entry<String, Integer> e : labelSet)
		{
			if (classificationValue == e.getValue())
				return e.getKey();
		}
		return null;
	}
	
	/**
	 * 获取所有的样本数
	 * @return
	 */
	public int getTotalSamplesNumber()
	{
		if (this.totalSamplesNumber == -1)
		{	
			int sum = 0;
			
			for (int n : numberOfClassification)
			{
				sum += n;
			}
			
			this.totalSamplesNumber = sum;
		}
		
		return this.totalSamplesNumber;
	}
	
	/**
	 * 序列化
	 * @param filePath
	 * @param encoding
	 * @throws IOException 
	 */
	public void serialization(String filePath, String encoding) throws IOException
	{
		Path path = Paths.get(filePath);
		Writer out = Files.newBufferedWriter(path, Charset.forName(encoding));
		
		final int oneLineNumber = 500; // 一行放元素的个数
		
		out.write("oneLineNumber="+oneLineNumber+"\n");
		
		out.write("features=" + features.size() + "\n");
		writeMap(features, oneLineNumber, out);
		
		out.write("classificationLabels=" + classificationLabels.size() + "\n");
		writeMap(classificationLabels, oneLineNumber, out);
		
		out.write("numberOfFeatures=" + numberOfFeatures.size() + "\n");
		writeList(numberOfFeatures, oneLineNumber, out);
		
		out.write("numberOfClassification=" + numberOfClassification.size() + "\n");
		writeList(numberOfClassification, oneLineNumber, out);
		
		out.flush();
		out.close();
	}
	
	/**
	 * 序列化
	 * @param filePath 
	 * @throws IOException
	 */
	public void serialization(String filePath) throws IOException
	{
		serialization(filePath, "utf-8");
	}
	
	/**
	 * 根据序列化文件，进行反序列化
	 */
	private void deserialization(String filePath, String encoding) throws IOException
	{
		Path path = Paths.get(filePath);
		
		if (!Files.exists(path))
		{
			System.err.println("文件不存在！");
			System.exit(1);
		}
		
		BufferedReader in = Files.newBufferedReader(path, Charset.forName(encoding));
		
		String temp = null;
		int oneLineNumber = 0;
		
		if ((temp = in.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("oneLineNumber"))
			{
				System.err.println("序列化文件格式错误！");
				System.exit(1);
			}
			oneLineNumber = Integer.parseInt(str[1]);
		}
		
		if ((temp = in.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("features"))
			{
				System.err.println("序列化文件格式错误！");
				System.exit(0);
			}
			this.features = readMap(Integer.valueOf(str[1]), oneLineNumber, in);
		}
		
		if ((temp = in.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("classificationLabels"))
			{
				System.err.println("序列化文件格式错误！");
				System.exit(0);
			}
			this.classificationLabels = readMap(Integer.valueOf(str[1]), oneLineNumber, in);
		}
		
		if ((temp = in.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("numberOfFeatures"))
			{
				System.err.println("序列化文件格式错误！");
				System.exit(0);
			}
			this.numberOfFeatures = readIntList(Integer.valueOf(str[1]), oneLineNumber, in);
		}
		
		if ((temp = in.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("numberOfClassification"))
			{
				System.err.println("序列化文件格式错误！");
				System.exit(0);
			}
			this.numberOfClassification = readIntList(Integer.valueOf(str[1]), oneLineNumber, in);
		}
		
		in.close();
	}
	
	private void writeMap(Map<String, Integer> map, int numberOfOneLine, Writer writer) throws IOException
	{
		Set<Entry<String, Integer>> entries = map.entrySet();
		
		int count = 0;
		
		StringBuilder sb = new StringBuilder();
		
		for (Entry<String, Integer> entry : entries)
		{
			sb.append(entry.getKey() + "=" + entry.getValue());
			count++;
			if (count < numberOfOneLine)
			{
				sb.append(" ");
			}
			else
			{
				writer.write(sb.toString() + "\n");
				count = 0;
				sb = new StringBuilder();
			}
		}
		if (count > 0)
		{
			writer.write(sb.toString().trim() + "\n");
		}
	}
	
	private void writeList(List<Integer> numList, int numberOfOneLine, Writer out) throws IOException
	{
		int count = 0;
		StringBuilder sb = new StringBuilder();
		
		for (int n : numList)
		{
			sb.append(n);
			count++;
			if (count < numberOfOneLine)
			{
				sb.append(" ");
			}
			else
			{
				out.write(sb.toString() + "\n");
				count = 0;
				sb = new StringBuilder();
			}
		}
		if (count > 0)
		{
			out.write(sb.toString().trim() + "\n");
		}
	}
	
	private Map<String, Integer> readMap(int total, int numberOfOneLine, BufferedReader reader) throws IOException
	{
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		int n = total / numberOfOneLine;
		if (total % numberOfOneLine != 0)
			n++;

		for (int i = 0; i < n; i++)
		{
			String line = reader.readLine();
			String[] strs = line.split(" +");
			for (String str : strs)
			{
				int index = str.lastIndexOf("=");
				result.put(str.substring(0, index), Integer.parseInt(str.substring(index+1)));
			}
		}
		
		return result;
	}
	
	private List<Integer> readIntList(int total, int numberOfOneLine, BufferedReader reader) throws IOException
	{
		List<Integer> result = new ArrayList<Integer>();
		int n = total / numberOfOneLine;
		if (total % numberOfOneLine != 0)
			n++;

		for (int i = 0; i < n; i++)
		{
			String line = reader.readLine();
			String[] strs = line.split(" +");
			for (String str : strs)
			{
				result.add(Integer.valueOf(str));
			}
		}
		return result;
	}

}
