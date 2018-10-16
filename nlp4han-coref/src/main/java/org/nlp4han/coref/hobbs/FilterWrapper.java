package org.nlp4han.coref.hobbs;

/**
 * 过滤器的装饰类
 * 
 * @author 杨智超
 *
 */
public abstract class FilterWrapper extends CandidateFilter
{
	protected CandidateFilter filter;

}
