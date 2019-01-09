package com.lc.nlp4han.srl.framenet;

/**
 * 语义角色标注模型
 * @author qyl
 *
 */
public class SRLModel
{
	/**
	 * 获取角色分类的概率
	 * @return
	 */
	public double getPro4Classification() {
		return 0;		
	}
	
	/**
	 * 获取角色识别的概率
	 * @return
	 */
	public double getPro4Recognition() {
		return 0;	
	}

	/**
	 * 获取框架元素组的先验概率
	 * @return
	 */
	public double getPro4FEGPrior() {
		return 0;
	}
	
	/**
	 * 以目标词为条件获取role的概率
	 * @return
	 */
	public double getPro4Predicate() {
		return 0;	
	}
	
	/**
	 * 以短语类型pt，控制类型gov,目标词为条件获取role的概率
	 * @return
	 */
	public double getPro4PtGovPredicate() {
		return 0;
	}

	/**
	 * 以短语类型pt，位置position,噪声voice为条件获取role的概率
	 * @return
	 */
	public double getPro4PtPositionVoice() {
		return 0;
	}
	
	/**
	 * 以短语类型pt，位置position,噪声voice,目标词predicate为条件获取role的概率
	 * @return
	 */
	public double getPro4getPro4PtPositionVoicePre() {
		return 0;
	}
	
	/**
	 * 以中心词为条件获取role的概率
	 * @return
	 */
	public double getPro4Head() {
		return 0;
	}
	
	/**
	 *  以中心词,目标词为条件获取role的概率
	 * @return
	 */
	public double getPro4HeadPre() {
		return 0;
	}
	
	/**
	 *  以中心词,目标词,短语类型为条件获取role的概率
	 * @return
	 */
	public double getPro4HeadPrePt() {
		return 0;
	}
	
	/**
	 * 以路径为条件获，获取在识别过程中的概率
	 * @return
	 */
	public double getFePro4Path() {
		return 0;
	}
	
	/**
	 * 以路径和谓词为条件，获取在识别过程中的概率
	 * @return
	 */
	public double getFePro4PathPre() {
		return 0;
	}
	
	/**
	 * 以头节点和谓词为条件，获取在识别过程中的概率
	 * @return
	 */
   public double getFePro4HeadPre() {
	   return 0;
   }
}
