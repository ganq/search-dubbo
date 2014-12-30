package com.mysoft.b2b.search.api;

import java.util.Map;

import com.mysoft.b2b.search.param.DeveloperParam;

/**
 * 开发商搜索
 * 
 * @author ganq
 * 
 */
public interface DeveloperSearchService {
	
	/**
	 * 获取搜索结果
	 * @param developerParam 查询参数
	 * @return List<AnnouncementsVO>
	 * 		   searchResult ： 搜索结果
	 * 		   totalRecordNum ： 总记录数 		
	 * 
	 */
	Map<String,Object> getSearchResult(DeveloperParam developerParam);
	  		
}