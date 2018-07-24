package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
/*
 * 从文件中将CFG文法读取出来
 */
public class GetPCFGFromFile { 
     public static PCFG getCFGFromFile(String fileName,String encoding) throws IOException {
    	PCFG pcfg=new PCFG();
    	InputStream file=new FileInputStream(new File(fileName));
		BufferedReader in = new BufferedReader(new InputStreamReader(file,encoding));
		String str=in.readLine().trim();
		
		if(str.equals("--起始符--")) {
		    pcfg.setStartSymbol(in.readLine().trim());
		}
		in.readLine();
        str=in.readLine().trim();
		while(!str.equals("--终结符集--")) {
				pcfg.addNonTerminal(str);
				str=in.readLine().trim();
			}
		str=in.readLine();
		while(!str.equals("--规则集--")) {
			pcfg.addTerminal(str);
			str=in.readLine().trim();
			}
		str=in.readLine();
		while(str!=null) {
			    str=str.trim();
				String[] strArray=str.split("->");
				String lhs=strArray[0];
				ArrayList<String> rhs=new ArrayList<String>();
				String[] rhsAndPro=strArray[1].split(" -概率- ");
				String rhs1=rhsAndPro[0];
				StringTokenizer st1=new StringTokenizer(rhs1," ");
				while(st1.hasMoreTokens()) {
					rhs.add(st1.nextToken());
				}
				double pro=Double.parseDouble(rhsAndPro[1]);
				pcfg.add(new PRule(pro,lhs,rhs));
				str=in.readLine();
		}
		in.close();
		return pcfg; 
     }
}

