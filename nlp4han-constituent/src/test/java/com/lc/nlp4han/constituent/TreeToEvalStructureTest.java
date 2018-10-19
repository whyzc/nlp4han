package com.lc.nlp4han.constituent;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.EvalStructure;
import com.lc.nlp4han.constituent.TreeNode;


/**
 * 测试将一颗完整的树转成 nonterminal begin end 的形式
 * @author 王馨苇
 *
 */
public class TreeToEvalStructureTest {

	@Test
	public void testTreeToEvalStructure() throws CloneNotSupportedException{
		TreeNode tree1 = BracketExpUtil.generateTreeNoTopBracket("((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		TreeNode tree2 = BracketExpUtil.generateTreeNoTopBracket("((S(NP(EX There))(VP(VBZ is)(NP(DT no)(NN box))(PP(IN in)(NP(PRP$ our)(NNS box)))(ADVP (RB now)))(. .)('' '') ))");
		List<EvalStructure> pre1 = EvalStructure.getEvalStructures(tree1);
		List<EvalStructure> pre2 = EvalStructure.getEvalStructures(tree2);
		
		List<EvalStructure> result1 = new ArrayList<>(); 
		result1.add(new EvalStructure("NP", 0, 1));
		result1.add(new EvalStructure("NP", 2, 4));
		result1.add(new EvalStructure("VP", 1, 4));
		result1.add(new EvalStructure("NP", 5, 7));
		result1.add(new EvalStructure("PP", 4, 7));
		result1.add(new EvalStructure("VP", 1, 7));
		result1.add(new EvalStructure("S", 0, 7));
		
		List<EvalStructure> result2 = new ArrayList<>(); 
		result2.add(new EvalStructure("NP", 0,1));
		result2.add(new EvalStructure("NP", 2, 4));
		result2.add(new EvalStructure("NP", 5, 7));
		result2.add(new EvalStructure("PP", 4, 7));
		result2.add(new EvalStructure("ADVP", 7, 8));
		result2.add(new EvalStructure("VP", 1, 8));
		result2.add(new EvalStructure("S", 0, 10));
		assertEquals(pre1.toString(),result1.toString());
		assertEquals(pre2.toString(),result2.toString());
	}
}
