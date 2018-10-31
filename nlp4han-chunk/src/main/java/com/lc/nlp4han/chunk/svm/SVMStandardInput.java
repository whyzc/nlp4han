package com.lc.nlp4han.chunk.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
	private static final String USAGE = "Usage: SVMStandardInput [options] -data data_file\n"
			+ "options:\n" 
			+ "-label label : such as BIOE, BIOES\n"
			+ "-encoding encoding : set encoding form\n" 
			+ "-save save_file : set save file path\n"
			;
	
	public static final String SEPARATOR = ".";

	private Map<String, Integer> features = new HashMap<String, Integer>();

	private List<Integer> numberOfFeatures = new ArrayList<Integer>();

	private List<String> classificationResults = new ArrayList<String>();

	private List<Integer> numberOfClassification = new ArrayList<Integer>();

	private Map<String, Integer> numberOfFeatureCategory = new HashMap<String, Integer>();

	private final int NUMBER = 500; // 一行放特征的个数

	public Map<String, Integer> getFeatures()
	{
		return features;
	}

	public List<Integer> getNumberOfFeatures()
	{
		return numberOfFeatures;
	}

	public List<String> getClassificationResults()
	{
		return classificationResults;
	}

	public List<Integer> getNumberOfClassification()
	{
		return numberOfClassification;
	}

	public Map<String, Integer> getNumberOfFeatureCategory()
	{
		return numberOfFeatureCategory;
	}

	/**
	 * 将特征转换成SVM标准输入中的特征部分（index1:value1 index2:value2 ...）
	 * 
	 * @param context
	 *            特征，存储内容为"w_1=job", "p1=n",....
	 * @param features
	 *            特征序列，记录各特征对应的序号
	 * @param numberOfFeatures
	 *            记录各特征的数量
	 * @param append
	 *            features是否可增加
	 * @return index1:value1 index2:value2 ...即 1:1 3:1 5:1 ......
	 */
	public static String getSVMStandardFeaturesInput(String[] context, SVMStandardInput ssi)
	{
		Map<String, Integer> features = ssi.getFeatures();

		StringBuilder intInput = new StringBuilder();

		int[] order = new int[context.length]; // 用来记录每个特征对应的index

		Integer index = null;

		for (int i = 0; i < context.length; i++)
		{
			String[] strs = context[i].split("=");

			if ((index = features.get(strs[0] + SEPARATOR + strs[1])) == null)
			{
				index = -1;
			}

			order[i] = index;
		}

		Arrays.sort(order);

		for (int i = 0; i < order.length; i++)
		{
			if (order[i] != -1)
			{
				intInput.append(order[i] + ":1 "); // 此处为赋value值
			}
		}

		return intInput.toString().trim();
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
		String usage = USAGE;

		String encoding = "utf-8";

		String docPath = null;

		String scheme = "BIEOS";

		String savePath = null;

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
			else if ("-save".equals(args[i]))
			{
				savePath = args[i + 1];
				i++;
			}
			else
			{
				System.err.println(usage);
				System.exit(1);
			}

		}

		if (docPath == null)
		{
			System.err.println(usage);
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

		result = new String[4];
		result[0] = docPath;
		result[1] = scheme;
		result[2] = encoding;
		if (savePath != null)
			result[3] = savePath;
		else
			result[3] = docPath + ".svm";

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
	public static String[] standardInput(ObjectStream<Event> es, SVMStandardInput ssi)
	{
		List<String> inputList = new ArrayList<String>();

		Event temp = null;

		try
		{
			while ((temp = es.read()) != null)
			{
				String sample = convert2StandardFormat(temp, ssi);
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
	 * 将一个event转成标准的输入格式，如 1 3:3 5:1 6:3 7:2 8:1
	 */
	public static String convert2StandardFormat(Event event, SVMStandardInput ssi)
	{

		StringBuilder result = new StringBuilder();
		String[] contexts = event.getContext();
		if (event != null)
		{
			Integer index = ssi.getClassificationResults().indexOf(event.getOutcome()) + 1;
			result.append(index);

			String str = getSVMStandardFeaturesInput(contexts, ssi);
			result.append(" " + str);
		}
		return result.toString();
	}

	public void init(ObjectStream<Event> es) throws IOException
	{
		Event event = null;
		while ((event = es.read()) != null)
		{
			if (!classificationResults.contains(event.getOutcome()))
			{
				classificationResults.add(event.getOutcome());

				numberOfClassification.add(1);
			}
			else
			{
				int index = classificationResults.indexOf(event.getOutcome());

				numberOfClassification.set(index, numberOfClassification.get(index) + 1);
			}

			String[] contexts = event.getContext();

			Integer index = null;

			for (int i = 0; i < contexts.length; i++)
			{
				String[] strs = contexts[i].split("=");

				if ((index = features.get(strs[0] + SEPARATOR + strs[1])) == null)
				{
					index = features.size() + 1;
					features.put(strs[0] + SEPARATOR + strs[1], index);

					numberOfFeatures.add(1);
				}
				else
				{
					numberOfFeatures.set(index - 1, numberOfFeatures.get(index - 1) + 1);
				}

				String key = parseName(strs[0]);
				if (numberOfFeatureCategory.containsKey(key))
				{
					numberOfFeatureCategory.put(key, numberOfFeatureCategory.get(key) + 1);
				}
				else
				{
					numberOfFeatureCategory.put(key, 1);
				}
			}
		}
	}

	private String parseName(String name)
	{
		if (name != null)
			return name.replaceAll("[^a-z^A-Z]", "");
		else
			return null;
	}

	public static void main(String[] args) throws IOException
	{
		run(args);
	}

	/**
	 * 根据参数args，读取文件，转换成svm标准输入格式并保存，同时生成转换的信息文件，返回转换信息类SVMStandardInput
	 */
	public static SVMStandardInput run(String[] args) throws IOException
	{
		String[] params = parseArgs(args);

		Properties featureConf = getDefaultConf();

		ObjectStream<Event> es = getEventStream(params[0], params[1], params[2], featureConf);

		SVMStandardInput ssi = new SVMStandardInput();
		ssi.init(es);

		es.reset();
		String[] input = standardInput(es, ssi);

		es.close();

		String savePath = params[3];
		writeToFile(savePath, input, "utf-8");

		ssi.saveConversionInfo(savePath + ".info", "utf-8");

		return ssi;
	}

	/**
	 * 将字符数组msg按行写出到filePath文件
	 * 
	 * @param filePath
	 *            文件地址
	 * @param msg
	 *            文件内容
	 * @param encoding
	 *            编码格式
	 */
	public static void writeToFile(String filePath, String[] msg, String encoding)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding));
			for (int i = 0; i < msg.length; i++)
			{
				bw.write(msg[i]);
				bw.write("\n");
			}

			bw.flush();
			bw.close();
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

	/**
	 * 将SVMStandardInput序列化
	 */
	private void saveConversionInfo(String filePath, String encoding) throws IOException
	{
		BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding));
		;

		bf.write("features=" + features.size() + "\n");
		writeMap(features, NUMBER, bf);

		bf.write("classificationResults=" + classificationResults.size() + "\n");
		writeList(classificationResults, NUMBER, bf);

		bf.write("numberOfFeatures=" + numberOfFeatures.size() + "\n");
		writeList(numberOfFeatures, NUMBER, bf);

		bf.write("numberOfClassification=" + numberOfClassification.size() + "\n");
		writeList(numberOfClassification, NUMBER, bf);

		bf.write("numberOfFeatureCategory=" + numberOfFeatureCategory.size() + "\n");
		writeMap(numberOfFeatureCategory, NUMBER, bf);

		bf.flush();
		bf.close();
	}

	private <T> void writeList(List<T> numList, int numberOfOneLine, BufferedWriter bf) throws IOException
	{
		int count = 0;
		StringBuilder sb = new StringBuilder();
		for (T n : numList)
		{
			sb.append(n);
			count++;
			if (count < numberOfOneLine)
			{
				sb.append(" ");
			}
			else
			{
				bf.write(sb.toString() + "\n");
				count = 0;
				sb = new StringBuilder();
			}
		}
		if (count > 0)
		{
			bf.write(sb.toString().trim() + "\n");
		}
	}

	private void writeMap(Map<String, Integer> map, int numberOfOneLine, BufferedWriter bf) throws IOException
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
				bf.write(sb.toString() + "\n");
				count = 0;
				sb = new StringBuilder();
			}
		}
		if (count > 0)
		{
			bf.write(sb.toString().trim() + "\n");
		}
	}

	/**
	 * 将SVMStandardInput反序列化
	 */
	public SVMStandardInput readConversionInfo(String filePath, String encoding) throws IOException
	{
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File(filePath)), encoding));

		String temp = null;

		if ((temp = reader.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("features"))
			{
				System.err.println(".info文件格式错误！");
				System.exit(0);
			}
			this.features = readMap(Integer.valueOf(str[1]), NUMBER, reader);
		}

		if ((temp = reader.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("classificationResults"))
			{
				System.err.println(".info文件格式错误！");
				System.exit(0);
			}
			this.classificationResults = readStringList(Integer.valueOf(str[1]), NUMBER, reader);
		}

		if ((temp = reader.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("numberOfFeatures"))
			{
				System.err.println(".info文件格式错误！");
				System.exit(0);
			}
			this.numberOfFeatures = readIntList(Integer.valueOf(str[1]), NUMBER, reader);
		}

		if ((temp = reader.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("numberOfClassification"))
			{
				System.err.println(".info文件格式错误！");
				System.exit(0);
			}
			this.numberOfClassification = readIntList(Integer.valueOf(str[1]), NUMBER, reader);
		}

		if ((temp = reader.readLine()) != null)
		{
			String[] str = temp.split("=");
			if (!str[0].equals("numberOfFeatureCategory"))
			{
				System.err.println(".info文件格式错误！");
				System.exit(0);
			}
			this.numberOfFeatureCategory = readMap(Integer.valueOf(str[1]), NUMBER, reader);
		}

		return null;
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
				String[] s = str.split("=");
				result.put(s[0], Integer.valueOf(s[1]));
			}
		}
		return result;
	}

	private List<String> readStringList(Integer total, int numberOfOneLine, BufferedReader reader) throws IOException
	{
		List<String> result = new ArrayList<String>();
		int n = total / numberOfOneLine;
		if (total % numberOfOneLine != 0)
			n++;

		for (int i = 0; i < n; i++)
		{
			String line = reader.readLine();
			String[] strs = line.split(" +");
			for (String str : strs)
			{
				result.add(str);
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
