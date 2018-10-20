package com.lc.nlp4han.chunk.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.AbstractChunkSampleParser;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosContextGeneratorConf;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIEOS;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosParserBIO;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSampleEvent;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSampleStream;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

public class SVMStandardInput
{
	private static List<String> FeatureStructure = new ArrayList<String>(); // 存储用到的特征，如w_2,
																			// p_2，用以记录各特征对应的序号，特征的（index+1）为SVM标准输入格式中该特征的序号
	private static List<String> ClassificationResults = new ArrayList<String>(); // 存储分类结果，如BNP_B, BNP_I, BNP_E,
																					// O，（index+1）为SVM标准分类结果
	private static Map<String, Map<String, Integer>> Features = new HashMap<String, Map<String, Integer>>(); // 记录所有具体的特征，为每一特征赋值

	private static List<Integer> NumberOfClassification = new ArrayList<Integer>(); //记录各分类结果的数量
	
	private static ScaleInfo scaleInfo = null;
	

	public static ScaleInfo getScaleInfo()
	{
		return scaleInfo;
	}
	
	public static void clear()
	{
		FeatureStructure = new ArrayList<String>();
		ClassificationResults = new ArrayList<String>();
		Features = new HashMap<String, Map<String, Integer>>();
		NumberOfClassification = new ArrayList<Integer>();
		scaleInfo = null;
	}

	public static String[] getStandardInput(String[] args) throws IOException
	{
		String[] params = parseArgs(args);

		Properties featureConf = getDefaultConf();

		setFeatureStructure(featureConf);

		ObjectStream<Event> es = getEventStream(params[0], params[1], params[2], featureConf);

		String[] input = standardInput(es);
		
		if (scaleInfo != null)		//需要scale
		{
			scaleInfo.ranges =getScaleRanges(FeatureStructure, Features);
			scale(input, scaleInfo, FeatureStructure);
		}
		
		es.close();

		return input;
	}

	/**
	 * 获取每个index对应value的最大值
	 * @param input
	 * @param FeatureStructure
	 * @param Features
	 * @return
	 */
	public static int[] getScaleRanges(List<String> FeatureStructure, Map<String, Map<String, Integer>> Features)
	{
		int[] ranges = new int[FeatureStructure.size()];
		for (int i=0 ; i<ranges.length ; i++)
		{
			String key = parseName(FeatureStructure.get(i));
			ranges[i] = Features.get(key).size();
		}
		return ranges;
	}

	public static void scale(String[] input, ScaleInfo scaleInfo, List<String> featureStructure)
	{
		for (int i=0 ; i<input.length ; i++)
		{
			String temp = scaleOneLine(input[i], scaleInfo, featureStructure);
			input[i] = temp;
		}
	}

	private static String scaleOneLine(String string, ScaleInfo scaleInfo, List<String> featureStructure)
	{
		String[] strs = string.split(" +");
		StringBuilder result = new StringBuilder();
		result.append(strs[0]);
		
		int currentIndex = 1;
		for (int i=1 ; i<strs.length ; i++)
		{
			String[] temp = strs[i].split(":");
			if (Integer.parseInt(temp[0]) != currentIndex)
			{
				for ( ; currentIndex<Integer.parseInt(temp[0]) ; currentIndex++)
				{
					String tempStr = output(currentIndex, 0, scaleInfo);
					result.append(tempStr);
				}
			}
			
			String tempStr = output(currentIndex, Integer.parseInt(temp[1]), scaleInfo);
			result.append(tempStr);
			currentIndex++;

		}
		
		for ( ; currentIndex <= featureStructure.size() ; currentIndex++)
		{
			String tempStr = output(currentIndex, 0,scaleInfo);
			result.append(tempStr);
		}
		
		return result.toString();
	}
	
	private static String output(int index, int value, ScaleInfo scaleInfo)
	{
		/* skip single-valued attribute */
		if(scaleInfo.ranges[index-1] == 0)
			return "";
		double result;
		if(value == 1)
			result = scaleInfo.lower;
		else if(value == scaleInfo.ranges[index-1])
			result = scaleInfo.upper;
		else
			result = scaleInfo.lower + 1.0*(scaleInfo.upper-scaleInfo.lower) *(value-1)/(scaleInfo.ranges[index-1]-1);

		if(result != 0)
		{
			return (" " + index + ":" + String.format("%.8f", result));
		}
		else
			return "";
	}

	/**
	 * 将特征转换成SVM标准输入中的特征部分
	 * 
	 * @param context
	 *            特征，存储内容为"w_1=job", "p1=n",....
	 * @param featureStructure
	 *            特征序列，记录各特征对应的序号
	 * @return SVM标准输入中的特征部分
	 */
	public static String getSVMStandardFeaturesInput(String[] context, List<String> featureStructure,
			Map<String, Map<String, Integer>> features, ScaleInfo scaleInfo)
	{
		//int[] newRanges = scaleInfo.ranges.clone();		//用于为样本中未出现的样本赋值，当出现新的特征，对应的值+1
		StringBuilder IntInput = new StringBuilder();
		
		for (int i = 0; i < featureStructure.size(); i++)
		{
			String indexStr = featureStructure.get(i);

			for (int j = 0; j < context.length; j++)
			{
				String[] strs = context[j].split("=");
				if (strs[0].equals(indexStr))
				{
					if (features.containsKey(parseName(strs[0])))
					{
						Map<String, Integer> temp = features.get(parseName(indexStr));
						if (temp.containsKey(strs[1]))
						{
							IntInput.append(" " + (i + 1) + ":" + temp.get(strs[1]));
						}
						else
						{ // 没有的特征，赋0值
							int size = temp.size();
							temp.put(strs[1], size+1);
							
							IntInput.append(" " + (i + 1) + ":" + (size+1));
						}
					}
					else
					{
						System.err.println("训练集中无此特征：" + strs[0]);
						System.exit(1);
					}
				}
			}
		}
		if (IntInput.length() > 1)
		{
			if (scaleInfo == null)
			{
				return IntInput.substring(1);
			}
			else
			{
				String s = scaleOneLine("1" + IntInput.toString(), scaleInfo, featureStructure);
				return s.substring(2);
			}
		}
		else
			return null;
	}

	/**
	 * 从配置文件中得到特征类型
	 */
	public static void setFeatureStructure(Properties featureConf)
	{
		Set<Object> keys = featureConf.keySet();
		Iterator<Object> it = keys.iterator();
		while (it.hasNext())
		{
			String str = (String) it.next();
			if (featureConf.getProperty(str).equals("true"))
			{
				FeatureStructure.add(str.substring(8));
			}
		}
	}
	

	/**
	 * 获取默认的特征配置文件
	 */
	public static Properties getDefaultConf() throws IOException
	{
		Properties featureConf = new Properties();
		InputStream featureStream = ChunkAnalysisWordPosContextGeneratorConf.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/chunk/svm/feature.properties");
		featureConf.load(featureStream);
		return featureConf;
	}

	/**
	 * 解析输入命令行
	 */
	private static String[] parseArgs(String[] args)
	{
		String usage = ChunkAnalysisSVMTrainerTool.USAGE;

		String encoding = "utf-8";

		String docPath = null;

		String scheme = "BIEOS";
		
		int lower = 1;
		int upper = 1;
		boolean scaleFlag = false;
		
		for (int i = 0; i < args.length; i++)
		{
			if ("-encoding".equals(args[i]))
			{
				encoding = args[i + 1];
				i++;
			}
			else if ("-data".equals(args[i]))
			{
				docPath = args[i + 1];
				i++;
			}
			else if ("-label".equals(args[i]))
			{
				scheme = args[i + 1];
				i++;
			}
			else if ("-l".equals(args[i]))
			{
				lower = Integer.parseInt(args[i+1]);
				scaleFlag = true;
				i++;
			}
			else if ("-u".equals(args[i]))
			{
				upper = Integer.parseInt(args[i+1]);
				scaleFlag = true;
				i++;
			}
		}

		if (docPath == null)
		{
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final java.nio.file.Path docDir = java.nio.file.Paths.get(docPath);

		if (!java.nio.file.Files.isReadable(docDir))
		{
			System.out.println("Document directory '" + docDir.toAbsolutePath()
					+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		String[] result = null;
		
		result = new String[3];
		result[0] = docPath;
		result[1] = scheme;
		result[2] = encoding;
		
		if (scaleFlag)
			scaleInfo = new ScaleInfo(lower, upper);
			
		
			
		return result;
	}

	/**
	 * 获取事件流
	 * 
	 * @param path
	 *            训练文件路径
	 * @param scheme
	 *            组块标记格式，BIEO，BIEOS等
	 * @param encoding
	 *            训练文件编码格式
	 * @param properties
	 *            特征配置文件
	 * @return 事件流
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static ObjectStream<Event> getEventStream(String path, String scheme, String encoding,
			Properties properties) throws FileNotFoundException, IOException
	{
		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(path)),
				encoding);
		AbstractChunkSampleParser parse = null;

		if (scheme.equals("BIEOS"))
			parse = new ChunkAnalysisWordPosParserBIEOS();
		else if (scheme.equals("BIEO"))
			parse = new ChunkAnalysisWordPosParserBIEO();
		else
			parse = new ChunkAnalysisWordPosParserBIO();

		ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisWordPosSampleStream(lineStream, parse,
				scheme);
		ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisWordPosContextGeneratorConf(properties);
		ObjectStream<Event> es = new ChunkAnalysisWordPosSampleEvent(sampleStream, contextGen);
		return es;
	}

	/**
	 * 生成SVM标准的输入格式
	 */
	public static String[] standardInput(ObjectStream<Event> es)
	{
		List<String> inputList = new ArrayList<String>();

		Event temp = null;

		try
		{
			while ((temp = es.read()) != null)
			{
				String sample = convert2StandardFormat(temp, Features);
				inputList.add(sample);
			}


		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		String[] input = new String[inputList.size()];

		inputList.toArray(input);

		return input;
	}

	/**
	 * 将一个event转成标准的输入格式，如 1 3:3 5:1 6:3 7:2 8:1 fs记录所有的特征，用于赋值
	 */
	private static String convert2StandardFormat(Event event, Map<String, Map<String, Integer>> fs)
	{

		StringBuilder result = new StringBuilder();
		String[] features = event.getContext();
		if (event != null)
		{
			if (!ClassificationResults.contains(event.getOutcome()))
			{
				ClassificationResults.add(event.getOutcome());
				result.append(ClassificationResults.size());
				
				NumberOfClassification.add(1);
			}
			else
			{
				int index = ClassificationResults.indexOf(event.getOutcome());
				result.append(index + 1);
				
				NumberOfClassification.set(index, NumberOfClassification.get(index)+1);
			}
		}

		for (int i = 0; i < FeatureStructure.size(); i++)
		{
			String indexStr = FeatureStructure.get(i);

			for (int j = 0; j < features.length; j++)
			{
				String[] strs = features[j].split("=");
				if (strs[0].equals(indexStr))
				{
					if (fs.containsKey(parseName(strs[0])))
					{
						Map<String, Integer> temp = fs.get(parseName(indexStr));
						if (temp.containsKey(strs[1]))
						{
							result.append(" " + (i + 1) + ":" + temp.get(strs[1]));
						}
						else
						{
							temp.put(strs[1], temp.size() + 1);
							result.append(" " + (i + 1) + ":" + temp.size());
						}
					}
					else
					{
						result.append(" " + (i + 1) + ":" + 1);

						Map<String, Integer> temp = new HashMap<String, Integer>();
						temp.put(strs[1], 1);
						fs.put(parseName(strs[0]), temp);
					}
				}
			}
		}
		
		return result.toString();
	}

	private static String parseName(String name)
	{
		if (name != null)
			return name.replaceAll("[^a-z^A-Z]", "");
		else
			return null;
	}

	public static void main(String[] args) throws IOException
	{
		String path = parseArgs(args)[0] + ".svm";
		String[] input = getStandardInput(args);
		writeToFile(path, input, false, "utf-8");
		ScaleInfo s = scaleInfo;
		save_data_format_conversion(FeatureStructure, ClassificationResults, Features, parseArgs(args)[0]+".dfc", "utf-8", s);

	}

	private static void writeToFile(String file, String[] msg, boolean append, String enconding)
	{
		Writer fw = null;
		BufferedWriter bw = null;
		try
		{
			fw = new FileWriter(file, append);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), enconding));
			for (int i = 0; i < msg.length; i++)
			{
				fw.write(msg[i]);
				fw.write("\n");
			}

			fw.flush();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (null != bw)
				{
					bw.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static List<String> getFeatureStructure()
	{
		return FeatureStructure;
	}

	public static List<String> getClassificationResults()
	{
		return ClassificationResults;
	}

	public static Map<String, Map<String, Integer>> getFeatures()
	{
		return Features;
	}
	
	public static List<Integer> getNumberOfClassification()
	{
		return NumberOfClassification;
	}

	private static void save_data_format_conversion(List<String> featureStructure, List<String> classificationResults, Map<String, Map<String, Integer>> features, String filePath, String encoding, ScaleInfo scaleInfo) throws IOException
	{
		BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding));;
		
		if (scaleInfo == null)
		{
			bf.write("scale=false\n");
			bf.write("\n\n\n");
		}
		else
		{
			bf.write("scale=true\n");
			bf.write("lower=" + scaleInfo.lower +"\n");
			bf.write("upper=" + scaleInfo.upper +"\n");
			StringBuilder sb = new StringBuilder();
			for (int i=0 ; i<scaleInfo.ranges.length ; i++)
			{
				sb.append(scaleInfo.ranges[i] + " ");
			}
			bf.write(sb.toString() +"\n");
		}
		
		
		bf.write("featureNum=" + featureStructure.size() + "\n");
		for (int i=0 ; i<featureStructure.size() ; i++)
		{
			bf.write(featureStructure.get(i)+"\n");
		}
		
		bf.write("classificationNum=" + classificationResults.size() + "\n");
		for (int i=0 ; i<classificationResults.size() ; i++)
		{
			bf.write(classificationResults.get(i)+"\n");
		}
		
		Set<Entry<String, Map<String, Integer>>> entry = features.entrySet();
		for (Entry<String, Map<String, Integer>> e : entry)
		{
			String k = e.getKey();
			Map<String, Integer> m = e.getValue();
			
			bf.write(k + " " + m.size() + "\n");
			
			int i = 1;
			for (Entry<String, Integer> en : m.entrySet())
			{
				bf.write(en.getKey() + "=" + en.getValue());
				if (i>=100)
				{
					bf.write("\n");
					i = 1;
				}
				else
				{
					bf.write(" ");
					i++;
				}
			}
			bf.write("\n");
		}
		bf.close();
	}
}
