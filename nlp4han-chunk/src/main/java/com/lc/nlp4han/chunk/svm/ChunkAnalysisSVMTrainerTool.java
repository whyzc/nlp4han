package com.lc.nlp4han.chunk.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkAnalysisSVMTrainerTool
{
	
	
	public static final String USAGE = "Usage: ChunkAnalysisSVMTrainerTool [options] -data training_set_file\n" 
			+ "options:\n"
			+ "-encoding encoding : set encoding" 
			+ "-label label : such as BIOE, BIOES"
			+ SVMTrain.OPTIONS;
	
	
	public static void main(String[] args) throws IOException
	{
		long startTime = System.currentTimeMillis();
		Map<String, String[]> as = decompositionArgs(args);
		
		String[] inputArgs = as.get("input");
		SVMStandardInput.main(inputArgs);
		
		//System.out.println("样本总数：" + input.length);
		System.out.println("类别总数：" + SVMStandardInput.getClassificationResults().size());
		
		String[] trainArgs = as.get("train");
		
		trainArgs = addWeight(trainArgs, SVMStandardInput.getNumberOfClassification());
		//t.run(trainArgs, input, true, SVMStandardInput.getScaleInfo());
		//List l = SVMStandardInput.getNumberOfClassification();
		svm_train.main(trainArgs);

		
		long endTime = System.currentTimeMillis();
		System.out.println("共耗时：" + (endTime-startTime)*1.0/60000 + "mins");
	}
	
	/**
	 * 分解参数
	 */
	private static Map<String, String[]> decompositionArgs(String[] args)
	{
		Map<String, String[]> result = new HashMap<>();
		List<String> standardInputArgs = new ArrayList<String>();
		List<String> trainArgs = new ArrayList<String>();
		
		String dataPath = null;
		String modelPath = null;
		
		for (int i=1 ; i<args.length ; i++)
		{
			switch(args[i-1])
			{
			/***************格式转换****************/
				case "-data":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					dataPath = args[i];
					i++;
					break;
				case "-encoding":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					i++;
					break;
				case "-label":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					i++;
					break;
				case "-l":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					i++;
					break;
				case "-u":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					i++;
					break;
			/***************训练参数****************/
				case "-s":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-t":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-d":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-g":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-r":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-n":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-m":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-c":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-e":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-p":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-h":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-b":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-q":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					i++;
					break;
				case "-model":
					modelPath = args[i];
					i++;
					break;
				default :
					if (args[i-1].startsWith("-w"))
					{
						trainArgs.add(args[i-1]);
						trainArgs.add(args[i]);
					}
					
			}
		}
		
		
		trainArgs.add(dataPath+".svm");
		if (modelPath != null)
			trainArgs.add(modelPath);
		result.put("input", standardInputArgs.toArray(new String[standardInputArgs.size()]));
		result.put("train", trainArgs.toArray(new String[trainArgs.size()]));
		return result;
	}

	private static String[] addWeight(String[] trainArgs, List<Integer> numbers)
	{
		int index = -1;
		int min = numbers.get(0);
		int max = min;
		List<String> result = new ArrayList<String>();
		double c = 0;
		
		for (int i=0 ; i<trainArgs.length ; i++)
		{
			if (trainArgs[i].equals("-c"))
				c = atof(trainArgs[i+1]);
		}
		
		for (int i=trainArgs.length-1 ; i>0 ; i--)
		{
			if (trainArgs[i].startsWith("-"))
			{
				index = i+1;
				break;
			}
		}
		
		for (int i=0 ; i<=index ; i++)
		{
			result.add(trainArgs[i]);
		}
		
		for (Integer n : numbers)
		{
			if (n < min)
				min = n;
			if (n > max)
				max = n;
		}
		
		if (index > -1)
		{
			double d;
			for (int j= 0 ; j<numbers.size() ; j++)
			{
				if ( (d = calculation(numbers.get(j), min, max, c)) < 1)  
				{
					result.add("-w" + (j+1));
					result.add(String.format("%.5f", d));
				}
				
			}
		}
		
		for (int i=index+1 ; i<trainArgs.length ; i++)
		{
			result.add(trainArgs[i]);
		}
		
		return result.toArray(new String[result.size()]);
	}

	//y = 1- (c-1)*(x-min)/[c*(max-min)]
	private static double calculation(int x, int min, int max, double c)
	{
		int p = 1000;		// c/p 为最小惩罚项
		double result = 0;
		result = 1 - 1.0*(p-1)*(Math.sqrt(x)-Math.sqrt(min))/(p)/(Math.sqrt(max) - Math.sqrt(min));
		return result;
	}
	
	private static double atof(String s)
	{
		double d = Double.valueOf(s).doubleValue();
		if (Double.isNaN(d) || Double.isInfinite(d))
		{
			System.err.print("NaN or Infinity in input\n");
			System.exit(1);
		}
		return(d);
	}
	
}
