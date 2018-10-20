package com.lc.nlp4han.chunk.svm;

import java.io.*;
import java.util.*;


import com.lc.nlp4han.chunk.svm.libsvm.*;

class SVMPredict  extends svm_predict{



	private static double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s)
	{
		return Integer.parseInt(s);
	}

	/**
	 * @param line 2 1:5 3:6 ......
	 * @param model
	 * @param predict_probability 暂不支持概率预测，此处设为0
	 * @throws IOException
	 */
	public static String predict(String line, svm_model model, int predict_probability) throws IOException
	{

		int svm_type=svm.svm_get_svm_type(model);
		
		

		StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
		
		double target = atof(st.nextToken());
		
		int m = st.countTokens()/2;
		svm_node[] x = new svm_node[m];
		for(int j=0;j<m;j++)
		{
			x[j] = new svm_node();
			x[j].index = atoi(st.nextToken());
			x[j].value = atof(st.nextToken());
		}

		double v;
		if (predict_probability==1 && (svm_type==svm_parameter.C_SVC || svm_type==svm_parameter.NU_SVC))
		{ 
			System.out.println("暂不支持概率预测！");
			System.exit(1);
		}
		else
		{
			v = svm.svm_predict(model,x);
			return Double.toString(v);
		}
		
		return null;
	}
}
