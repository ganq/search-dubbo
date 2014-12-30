package com.mysoft.b2b.search.api;

import com.mysoft.b2b.search.param.RecruitParam;

import java.util.Map;

/**
 * 招标预告搜索
 * 
 * @author ganq
 * 
 */
public interface RecruitSearchService {
	
	/**
	 * 获取搜索结果
	 * @param recruitParam 查询参数
	 * @return Map<String,Object>
	 * 		   relatedCategory : 搜索结果分类
	 * 		   searchResult ： 搜索结果
	 * 		   totalRecordNum ： 总记录数
	 */
	Map<String,Object> getSearchResult(RecruitParam recruitParam);

}