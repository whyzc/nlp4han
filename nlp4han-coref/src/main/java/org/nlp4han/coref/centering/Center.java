package org.nlp4han.coref.centering;

import java.util.List;

/**
 * 中心数值（Cb、Cf、Cp）
 * @author 杨智超
 *
 */
public class Center
{
	private String[] Cb;		//回指中心（backward looking center）
	private String Cf;			//下指中心（forward looking center）
	private String Cp;			//优选中心（preferred center）
	
	/**
	 * 生成回指中心Cb
	 * @param entitys
	 */
	public void generateCb(List<Entity> entitys)
	{
		
	}
	
	/**
	 * 生成下指中心Cf
	 * @param entity
	 */
	public void generateCf(Entity entity)
	{
		
	}
	
	/**
	 * 生成优选中心Cp
	 */
	public void generateCp()
	{
		
	}
	
}
