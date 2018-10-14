package com.lc.nlp4han.chunk.svm;

import java.io.IOException;
import java.util.ArrayList;
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
		String[] input = SVMStandardInput.getStandardInput(inputArgs);
		
		System.out.println("样本总数：" + input.length);
		System.out.println("类别总数：" + SVMStandardInput.getClassificationResults().size());
		
		String[] trainArgs = as.get("train");
		SVMTrain t = new SVMTrain();
		
		t.run(trainArgs, input, true, SVMStandardInput.getScaleInfo());

		
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
		for (int i=1 ; i<args.length ; i++)
		{
			switch(args[i-1])
			{
			/***************格式转换****************/
				case "-data":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-encoding":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					break;
				case "-label":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					break;
				case "-l":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					break;
				case "-u":
					standardInputArgs.add(args[i-1]);
					standardInputArgs.add(args[i]);
					break;
			/***************训练参数****************/
				case "-s":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-t":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-d":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-g":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-r":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-n":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-m":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-c":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-e":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-p":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-h":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-b":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-q":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
				case "-model":
					trainArgs.add(args[i-1]);
					trainArgs.add(args[i]);
					break;
					
			}
		}
		
		result.put("input", standardInputArgs.toArray(new String[standardInputArgs.size()]));
		result.put("train", trainArgs.toArray(new String[trainArgs.size()]));
		return result;
	}
}
