package com.lc.nlp4han.constituent.pcfg;

import java.io.FileNotFoundException;
import java.io.IOException;



public class Extract {
	 private String fileName;
	 private String enCoding;
	 private ExtractGrammar eg=new ExtractGrammar();
     public Extract(String fileName,String enCoding) {
    	 this.fileName=fileName;
    	 this.enCoding=enCoding;
     }
     public CFG getCFG() throws UnsupportedOperationException, FileNotFoundException, IOException {
         eg.CreateGrammar(fileName, enCoding,"CFG");
         return eg.getGrammar();
     }
}
