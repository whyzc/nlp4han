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

	public static String[] getStandardInput(String[] args) throws IOException
	{
		String[] params = parseArgs(args);

		Properties featureConf = getDefaultConf();

		setFeatureStructure(featureConf);

		ObjectStream<Event> es = getEventStream(params[0], params[1], params[2], featureConf);

		List<String> inputList = standardInput(es);

		String[] input = new String[inputList.size()];

		inputList.toArray(input);

		return input;
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
			Map<String, Map<String, Integer>> features)
	{
		StringBuilder result = new StringBuilder();
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
							result.append(" " + (i + 1) + ":" + temp.get(strs[1]));
						}
						else
						{ // 没有的特征，赋0值
							result.append(" " + (i + 1) + ":" + 0);
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
		if (result.length() > 1)
			return result.substring(1);
		else
			return null;
	}

	/**
	 * 
	 */
	private static void setFeatureStructure(Properties featureConf)
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
	private static Properties getDefaultConf() throws IOException
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
			else if ("-tag".equals(args[i]))
			{
				scheme = args[i + 1];
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

		String[] result = new String[3];
		result[0] = docPath;
		result[1] = scheme;
		result[2] = encoding;
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
	private static List<String> standardInput(ObjectStream<Event> es)
	{
		List<String> result = new ArrayList<String>();

		Event temp = null;

		try
		{
			while ((temp = es.read()) != null)
			{
				String sample = convert2StandardFormat(temp, Features);
				result.add(sample);
			}

			es.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{

			if (es != null)
			{

				try
				{
					es.close();
				}
				catch (IOException e1)
				{

				}

			}

		}

		return result;
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
			}
			else
			{
				result.append(ClassificationResults.indexOf(event.getOutcome()) + 1);
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
		/*
		 * for (int i=0 ; i<FeatureStructure.length ; i++) { String[] strs =
		 * features[i].split("="); int in = FeatureStructure.get("feature." + strs[0]);
		 * if (fs.containsKey(parseName(strs[0]))) { Map<String, Integer> temp =
		 * fs.get(parseName(strs[0])); if (temp.containsKey(strs[1])) {
		 * result.append(" " + in + ":" + temp.get(strs[1])); } else { temp.put(strs[1],
		 * temp.size()+1); result.append(" " + in + ":" + temp.size()); } } else {
		 * result.append(" " + in + ":" + 1);
		 * 
		 * Map<String, Integer> temp = new HashMap<String, Integer>(); temp.put(strs[1],
		 * 1); fs.put(parseName(strs[0]), temp); } }
		 */
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

}
