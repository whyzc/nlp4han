package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 * 从文件中将CFG文法读取出来
 */
public class GetGrammarFromFile { 
	/**
	 * 从文件中读取PCFG文法
	 * @param fileName
	 * @param enCoding
	 * @return
	 * @throws IOException
	 */
     public static PCFG getPCFGFromFile(String fileName,String enCoding) throws IOException {
    	PCFG pcfg=new PCFG();
		return (PCFG) ExtractGrammarFromFile(pcfg,fileName,enCoding,"PCFG"); 
     }
     /**
      * 从文件中读取CFG文法
     * @param fileName
     * @param enCoding
     * @return
     * @throws IOException
     */
    public static CFG getCFGFromFile(String fileName,String enCoding) throws IOException {
    	CFG cfg=new CFG();
		return  ExtractGrammarFromFile(cfg,fileName,enCoding,"CFG");	 
     }
     private static CFG ExtractGrammarFromFile(CFG cfg,String fileName,String enCoding,String type) throws IOException {
     	InputStream file=new FileInputStream(new File(fileName));
 		BufferedReader in = new BufferedReader(new InputStreamReader(file,enCoding));
 		String str=in.readLine().trim();		
 		if(str.equals("--起始符--")) {
 		    cfg.setStartSymbol(in.readLine().trim());
 		}
 		in.readLine();
         str=in.readLine().trim();
 		while(!str.equals("--终结符集--")) {
 				cfg.addNonTerminal(str);
 				str=in.readLine().trim();
 			}
 		str=in.readLine();
 		while(!str.equals("--规则集--")) {
 			cfg.addTerminal(str);
 			str=in.readLine().trim();
 			}
 		str=in.readLine();
 		while(str!=null) {
 			    str=str.trim();
 				String[] strArray=str.split("->");
 				String lhs=strArray[0];
				ArrayList<String> rhs=new ArrayList<String>();
 				if(type.equals("CFG")) {
 					String rhs1=strArray[1];
 					StringTokenizer st1=new StringTokenizer(rhs1," ");
 					while(st1.hasMoreTokens()) {
 						rhs.add(st1.nextToken());
 					}
 					cfg.add(new RewriteRule(lhs,rhs));
 				}else if(type.equals("PCFG")){
 	 				String[] rhsAndPro=strArray[1].split(" ---- ");
 	 				String rhs1=rhsAndPro[0];
 	 				StringTokenizer st1=new StringTokenizer(rhs1," ");
 	 				while(st1.hasMoreTokens()) {
 	 					rhs.add(st1.nextToken());
 	 				}
 	 				double pro=Double.parseDouble(rhsAndPro[1]);
 	 				cfg.add(new PRule(pro,lhs,rhs));				
 				}
 				str=in.readLine();
 		}
 		in.close();
 		return cfg;
     }
}

