package com.lc.nlp4han.chunk.svm;

import com.lc.nlp4han.chunk.svm.libsvm.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class svm_train {
	private svm_parameter param;		// set by parse_command_line
	private svm_problem prob;			// set by read_problem
	private svm_model model;
	private String input_file_name;		// set by parse_command_line
	private String model_file_name;		// set by parse_command_line
	private String error_msg;
	private int cross_validation;
	private int nr_fold;

	private static svm_print_interface svm_print_null = new svm_print_interface()
	{
		public void print(String s) {}
	};

	private static void exit_with_help()
	{
		System.out.print(ChunkAnalysisSVMTrainerTool.USAGE);
		System.exit(1);
	}

	private void do_cross_validation()
	{
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		svm.svm_cross_validation(prob,param,nr_fold,target);
		if(param.svm_type == svm_parameter.EPSILON_SVR ||
		   param.svm_type == svm_parameter.NU_SVR)
		{
			for(i=0;i<prob.l;i++)
			{
				double y = prob.y[i];
				double v = target[i];
				total_error += (v-y)*(v-y);
				sumv += v;
				sumy += y;
				sumvv += v*v;
				sumyy += y*y;
				sumvy += v*y;
			}
			System.out.print("Cross Validation Mean squared error = "+total_error/prob.l+"\n");
			System.out.print("Cross Validation Squared correlation coefficient = "+
				((prob.l*sumvy-sumv*sumy)*(prob.l*sumvy-sumv*sumy))/
				((prob.l*sumvv-sumv*sumv)*(prob.l*sumyy-sumy*sumy))+"\n"
				);
		}
		else
		{
			for(i=0;i<prob.l;i++)
				if(target[i] == prob.y[i])
					++total_correct;
			System.out.print("Cross Validation Accuracy = "+100.0*total_correct/prob.l+"%\n");
		}
	}

	public void run(String argv[], String[] standardInput) throws IOException
	{
		parse_command_line(argv);
		read_problem(standardInput);
		error_msg = svm.svm_check_parameter(prob,param);

		if(error_msg != null)
		{
			System.err.print("ERROR: "+error_msg+"\n");
			System.exit(1);
		}

		if(cross_validation != 0)
		{
			do_cross_validation();
		}
		else
		{
			model = svm.svm_train(prob,param);
			svm.svm_save_model(model_file_name,model);
			save_data_format_conversion(SVMStandardInput.getFeatureStructure() ,SVMStandardInput.getClassificationResults(), SVMStandardInput.getFeatures(), model_file_name+".dfc", "utf-8");
		}
	}

	private void save_data_format_conversion(List<String> featureStructure, List<String> classificationResults, Map<String, Map<String, Integer>> features, String filePath, String encoding) throws IOException
	{
		BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoding));;
		
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

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	private void parse_command_line(String argv[])
	{
		int i;
		svm_print_interface print_func = null;	// default printing to stdout

		param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.gamma = 0;	// 1/num_features
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		cross_validation = 0;

		// parse options
		for(i=0;i<argv.length;i++)
		{
			if(argv[i].charAt(0) != '-') break;
			if(++i>=argv.length)
				exit_with_help();
			switch(argv[i-1])
			{
				case "-s":
					param.svm_type = atoi(argv[i]);
					break;
				case "-t":
					param.kernel_type = atoi(argv[i]);
					break;
				case "-d":
					param.degree = atoi(argv[i]);
					break;
				case "-g":
					param.gamma = atof(argv[i]);
					break;
				case "-r":
					param.coef0 = atof(argv[i]);
					break;
				case "-n":
					param.nu = atof(argv[i]);
					break;
				case "-m":
					param.cache_size = atof(argv[i]);
					break;
				case "-c":
					param.C = atof(argv[i]);
					break;
				case "-e":
					param.eps = atof(argv[i]);
					break;
				case "-p":
					param.p = atof(argv[i]);
					break;
				case "-h":
					param.shrinking = atoi(argv[i]);
					break;
				case "-b":
					param.probability = atoi(argv[i]);
					break;
				case "-q":
					print_func = svm_print_null;
					i--;
					break;
				case "-v":
					cross_validation = 1;
					nr_fold = atoi(argv[i]);
					if(nr_fold < 2)
					{
						System.err.print("n-fold cross validation: n must >= 2\n");
						exit_with_help();
					}
					break;
				case "-w":
					++param.nr_weight;
					{
						int[] old = param.weight_label;
						param.weight_label = new int[param.nr_weight];
						System.arraycopy(old,0,param.weight_label,0,param.nr_weight-1);
					}

					{
						double[] old = param.weight;
						param.weight = new double[param.nr_weight];
						System.arraycopy(old,0,param.weight,0,param.nr_weight-1);
					}

					param.weight_label[param.nr_weight-1] = atoi(argv[i-1].substring(2));
					param.weight[param.nr_weight-1] = atof(argv[i]);
					break;
				case "-model":
					model_file_name = argv[i];
					break;
				case "-data":
					input_file_name = argv[i];
					break;
					
				default:
					System.err.print("Unknown option: " + argv[i-1] + "\n");
					exit_with_help();
			}
		}

		
		svm.svm_set_print_string_function(print_func);

		if (model_file_name == null)
		{
			int p = input_file_name.lastIndexOf('/');
			++p;	// whew...
			model_file_name = input_file_name.substring(p)+".model";
		}
		
	}

	// read in a problem (in svmlight format)

	private void read_problem(String[] standardInput) throws IOException
	{
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		int max_index = 0;

		for (int i=0 ; i<standardInput.length ; i++)
		{
			String line = standardInput[i];

			StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");

			vy.addElement(atof(st.nextToken()));
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}
			if(m>0) max_index = Math.max(max_index, x[m-1].index);
			vx.addElement(x);
		}

		prob = new svm_problem();
		prob.l = vy.size();
		prob.x = new svm_node[prob.l][];
		for(int i=0;i<prob.l;i++)
			prob.x[i] = vx.elementAt(i);
		prob.y = new double[prob.l];
		for(int i=0;i<prob.l;i++)
			prob.y[i] = vy.elementAt(i);

		if(param.gamma == 0 && max_index > 0)
			param.gamma = 1.0/max_index;

		if(param.kernel_type == svm_parameter.PRECOMPUTED)
			for(int i=0;i<prob.l;i++)
			{
				if (prob.x[i][0].index != 0)
				{
					System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
					System.exit(1);
				}
				if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > max_index)
				{
					System.err.print("Wrong input format: sample_serial_number out of range\n");
					System.exit(1);
				}
			}

	}
}
