package org.nlp4han.coref.centering;

import java.util.ArrayList;
import java.util.List;

/**
 * 中心数值（Cb、Cf、Cp）
 * 
 * @author 杨智超
 *
 */
public class Center
{
	private Entity Cb; // 回指中心（backward looking center）
	private List<Entity> Cf; // 下指中心（forward looking center）
	private Entity Cp; // 优选中心（preferred center）

	/**
	 * 通过会话中的实体集和将其中的代词替换成先行词后的实体集创建会话的Center类
	 * 
	 * @param entities
	 * @param newEntities
	 */
	public Center(List<Entity> entities, List<Entity> newEntities)
	{// 注意：参数不能为null，当两个参数相等时，为第一句话，Cb为undefined(null) 此处需修改
		generateCf(newEntities);
		generateCb(entities, newEntities);
		generateCp(entities, newEntities);
	}

	public Entity getCb()
	{
		return Cb;
	}

	public List<Entity> getCf()
	{
		return Cf;
	}

	public Entity getCp()
	{
		return Cp;
	}

	/**
	 * 生成回指中心Cb
	 * 
	 * @param entities
	 * @param newEntities
	 */
	public void generateCb(List<Entity> entities, List<Entity> newEntities)
	{
		if (!entities.equals(newEntities))
		{
			List<Entity> anaphorEntities = new ArrayList<Entity>();
			for (int i = 0; i < newEntities.size(); i++)
			{
				if (!entities.get(i).equals(newEntities.get(i)))
					anaphorEntities.add(newEntities.get(i));
			}

			List<Entity> es = Entity.sort(anaphorEntities);
			if (es != null && es.size() > 0)
				Cb = es.get(0);
			else
				Cb = null;
		}
		else
			Cb = null;

	}

	/**
	 * 生成下指中心Cf
	 * 
	 * @param newEntity
	 */
	public void generateCf(List<Entity> newEntities)
	{
		if (newEntities == null)
			throw new RuntimeException("输入不能为null");
		Cf = newEntities;
	}

	/**
	 * 生成优选中心Cp
	 * 
	 * @param newEntities
	 * @param entities
	 */
	public void generateCp(List<Entity> entities, List<Entity> newEntities)
	{
		if (Cf == null || Cf.size() < 1)
			throw new RuntimeException("Cf错误");
		List<Entity> e = Entity.sort(entities);
		int index = entities.indexOf(e.get(0));
		Cp = newEntities.get(index);
	}

	@Override
	public String toString()
	{
		String strCf;
		if (Cf != null && Cf.size() > 0)
		{
			StringBuilder strbCf = new StringBuilder();
			strbCf.append("(");
			boolean isFirst = true;
			for (Entity e : Cf)
			{
				if (isFirst)
				{
					strbCf.append(e.getEntityName());
					isFirst = false;
					continue;
				}
				strbCf.append(", " + e.getEntityName());
			}
			strbCf.append(")");
			strCf = strbCf.toString();
		}
		else
			strCf = "( )";
		if (Cb != null)
			return "[Cb=" + Cb.getEntityName() + ", Cf=" + strCf + ", Cp=" + Cp.getEntityName() + "]";
		else
			return "[Cf=" + strCf + ", Cp=" + Cp.getEntityName() + "]";
	}

}
