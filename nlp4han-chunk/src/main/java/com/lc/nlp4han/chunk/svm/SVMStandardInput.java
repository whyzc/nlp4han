package com.lc.nlp4han.chunk.svm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	private static Map<String, Integer> FeatureStructure = new HashMap<String, Integer>();	//存储特征对应的序号，如w_2=1, p_2=2
	private static Map<String, Integer> ClassificationResults = new HashMap<String, Integer>();	//存储分类结果对应的序号，如BNP_B=2, BNP_I=4, BNP_E=3, O=1
	
	public static List<String> getStandarInput(String[] args) throws IOException
	{
		String[] params = parseArgs(args);
		
		Properties featureConf = getDefaultConf();
		
		setFeatureStructure(featureConf);
		
		ObjectStream<Event> es =  getEventStream(params[0], params[1], params[2], featureConf);
		
		List<String> input = standarInput(es);
		
		return input;
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
			String str = (String)it.next();
			if (featureConf.getProperty(str).equals("true"))
			{
				FeatureStructure.put(str, FeatureStructure.size()+1);
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
	public static String[] parseArgs(String[] args)
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
	private static ObjectStream<Event> getEventStream(String path, String scheme, String encoding, Properties properties)
			throws FileNotFoundException, IOException
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
	private static List<String> standarInput(ObjectStream<Event> es)
	{
		List<String> result = new ArrayList<String>();
		Map<String, Map<String, Integer>> fs = new HashMap<String, Map<String, Integer>>();
		
		Event temp = null;
		
		try
		{
			while ((temp=es.read()) != null)
			{
				String sample = convert2StandardFormat(temp, fs);
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
	 * 将一个event转成标准的输入格式，如 1 5:1 8:1 4:2 7:2 3:3 6:3
	 * fs记录所有的特征，用于赋值
	 */
	private static String convert2StandardFormat(Event event,Map<String, Map<String, Integer>> fs)
	{
		
		StringBuilder result = new StringBuilder();
		String[] features = event.getContext();
		if (event != null)
		{
			if (!ClassificationResults.containsKey(event.getOutcome()))
			{
				ClassificationResults.put(event.getOutcome(), ClassificationResults.size()+1);
				result.append(ClassificationResults.size());
			}
			else
			{
				result.append(ClassificationResults.get(event.getOutcome()));
			}
		}
		for (int i=0 ; i<features.length ; i++)
		{
			String[] strs = features[i].split("=");
			int in = FeatureStructure.get("feature." + strs[0]);
			if (fs.containsKey(parseName(strs[0])))
			{
				Map<String, Integer> temp = fs.get(parseName(strs[0]));
				if (temp.containsKey(strs[1]))
				{
					result.append(" " + in + ":" + temp.get(strs[1]));
				}
				else
				{
					temp.put(strs[1], temp.size()+1);
					result.append(" " + in + ":" + temp.size());
				}
			}
			else
			{
				result.append(" " + in + ":" + 1);
				
				Map<String, Integer> temp = new HashMap<String, Integer>();
				temp.put(strs[1], 1);
				fs.put(parseName(strs[0]), temp);
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
}
