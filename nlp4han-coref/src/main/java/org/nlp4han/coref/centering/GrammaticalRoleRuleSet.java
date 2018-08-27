package org.nlp4han.coref.centering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lc.nlp4han.constituent.HeadRule;

/**
 * 语法成分规则，确切地说，此规则是结构上的
 * @author 杨智超
 *
 */
public class GrammaticalRoleRuleSet
{
	private static HashMap<String, List<String>> grammaticalRoleRuleSet = new HashMap<String, List<String>>();
	
	private static List<String> SBJRules = new ArrayList<String>();
	
	private static List<String> OBJRules = new ArrayList<String>();
	
	private static List<String> IORules = new ArrayList<String>();
	
	static
	{
		/***************主语规则集**************/
		String SBJRule1 = "NP#(IP(_ VP))";
		SBJRules.add(SBJRule1);
		
		grammaticalRoleRuleSet.put("SBJ", SBJRules);
		
		/***************直接宾语规则集**************/
		String OBJRule1 = "NP#(VP(VV _))";
		String OBJRule2 = "NP#(VP(VV AS _))";
		String OBJRule3 = "NP#(VP(VV NP _))";
		String OBJRule4 = "NP#(VP(VC _))";
		String OBJRule5 = "NP#(VP(VV AS NP _))";
		OBJRules.add(OBJRule1);
		OBJRules.add(OBJRule2);
		OBJRules.add(OBJRule3);
		OBJRules.add(OBJRule4);
		OBJRules.add(OBJRule5);
		
		grammaticalRoleRuleSet.put("OBJ", OBJRules);
		
		/***************间接宾语规则集**************/
		String IORule1 = "NP#(VP(VV _ NP))";
		String IORule2 = "NP#(VP(VV AS _ NP))";
		
		IORules.add(IORule1);
		IORules.add(IORule2);
		
		grammaticalRoleRuleSet.put("IO", IORules);
		
	}
	
	public static HashMap<String, List<String>> getGrammaticalRoleRuleSet()
	{
		return grammaticalRoleRuleSet;
	}
}
