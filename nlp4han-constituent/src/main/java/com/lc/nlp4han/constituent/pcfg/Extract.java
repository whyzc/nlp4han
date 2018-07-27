package com.lc.nlp4han.constituent.pcfg;

import java.io.IOException;



public class Extract {
     public static CFG getCFG(String fileName,String enCoding) throws IOException {
        
    	 return new ExtractCFG().CreateCFG(fileName, enCoding);
     }
     public static PCFG getPCFG(String fileName,String enCoding) throws IOException {

         return new ExtractPCFG().CreatePCFG(fileName, enCoding);
     }
}
