package com.lc.nlp4han.clustering;

import java.util.List;

/**
 * 更新簇的中心
 * @author 杨智超
 *
 */
public interface UpdateGroupCenter
{
	public boolean updateCenter(List<Group> grps);
}
