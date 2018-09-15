package com.lc.nlp4han.constituent;

import static org.junit.Assert.*;

import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;

/**
 * 测试生成头结点的方法
 * 
 * @author 王馨苇
 *
 */
public class HeadGeneratorTest
{

	@Test
	public void testGenerateHeadWords()
	{

		AbstractHeadGenerator headGen = new HeadGeneratorCollins(new HeadRuleSetPTB());
		TreeNode tree1 = BracketExpUtil.generateTree(
				"((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))");
		HeadTreeNode headTree1 = TreeToHeadTree.treeToHeadTree(tree1, headGen);
		String result1 = "(S{saw[VBD]}(NP{I[PRP]}(PRP{I[PRP]} I[0]))(VP{saw[VBD]}(VP{saw[VBD]}(VBD{saw[VBD]} saw[1])(NP{man[NN]}(DT{the[DT]} the[2])"
				+ "(NN{man[NN]} man[3])))(PP{with[IN]}(IN{with[IN]} with[4])(NP{telescope[NN]}(DT{the[DT]} the[5])(NN{telescope[NN]} telescope[6])))))";

		TreeNode tree2 = BracketExpUtil.generateTree(
				"((S(NP(EX There))(VP(VBZ is)(NP(DT no)(NN asbestos))(PP(IN in)(NP(PRP$ our)(NNS products)))(ADVP (RB now)))(. .)('' '') ))");
		HeadTreeNode headTree2 = TreeToHeadTree.treeToHeadTree(tree2, headGen);
		String result2 = "(S{is[VBZ]}(NP{There[EX]}(EX{There[EX]} There[0]))(VP{is[VBZ]}(VBZ{is[VBZ]} is[1])"
				+ "(NP{asbestos[NN]}(DT{no[DT]} no[2])(NN{asbestos[NN]} asbestos[3]))(PP{in[IN]}"
				+ "(IN{in[IN]} in[4])(NP{products[NNS]}(PRP${our[PRP$]} our[5])(NNS{products[NNS]} products[6])))"
				+ "(ADVP{now[RB]}(RB{now[RB]} now[7])))(.{.[.]} .[8])(''{''['']} ''[9]))";

		assertEquals(headTree1.toString(), result1);
		assertEquals(headTree2.toString(), result2);
	}
}
