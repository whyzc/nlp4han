package com.lc.nlp4han.dependency.tb;
/**
* @author 作者
* @version 创建时间：2018年8月19日 上午9:51:19
* 类说明
*/
public class DependencyParseContextGeneratorConf_ArcStandard implements DependencyParseContextGenerator
{

	@Override
	public String[] getContext(int index, String[] wordpos, String[] priorDecisions, Object[] additionalContext) {
		Configuration_ArcStandard conf = new Configuration_ArcStandard().generateConfByActions(wordpos, priorDecisions);
		return getContext(conf, priorDecisions, additionalContext);
	}
	public String[] getContext(Configuration_ArcStandard conf, String[] priorDecisions, Object[] additionalContext) {
		return null;
	}
}
