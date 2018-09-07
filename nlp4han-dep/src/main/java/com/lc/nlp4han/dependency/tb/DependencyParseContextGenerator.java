package com.lc.nlp4han.dependency.tb;

import com.lc.nlp4han.ml.util.BeamSearchContextGenerator;

/**
 * 特征生成的接口
 * 
 * @author 王宁
 * 
 *
 */
public interface DependencyParseContextGenerator extends BeamSearchContextGenerator<String>
{

	/**
	 * 获取特征
	 * 
	 * @param conf
	 *            arceager过程中的配置
	 * 
	 * @return
	 */
}
