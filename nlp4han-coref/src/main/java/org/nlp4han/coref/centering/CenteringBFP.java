package org.nlp4han.coref.centering;

import java.util.List;

public class CenteringBFP
{
	private List<List<Entity>> entitysOfUtterances;
	
	public CenteringBFP()
	{
		
	}
	
	public CenteringBFP(List<List<Entity>> entitysOfUtterances)
	{
		this.entitysOfUtterances = entitysOfUtterances;
	}
	
	/**
	 * 运行BFP算法
	 * @return
	 */
	public List<List<Entity>> run()
	{
		return null;
	}
	
	/**
	 * 设置实体集
	 * @param entitysOfUtterances
	 */
	public void setEntitysOfUtterances(List<List<Entity>> entitysOfUtterances)
	{
		this.entitysOfUtterances = entitysOfUtterances;
	}

	/**
	 * 生成话语的中心数值（Cb、Cf、Cp）
	 * @param entitys
	 * @param entity
	 * @return
	 */
	public Center generateCenter(List<Entity> entitys, Entity entity)
	{
		return null;
	}
	
	/**
	 * 根据前后两句的Center，获得相应的Transition
	 * @param c1
	 * @param c2
	 * @return
	 */
	public String getTransition(Center c1, Center c2)
	{
		return null;
	}
}
