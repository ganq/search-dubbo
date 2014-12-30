package com.mysoft.b2b.search.api;

import java.util.Map;

/**
 * 搜索提示
 * @author ganq
 *
 */
public interface SearchSuggestService {

	
	/**
	 * 获取搜索建议
	 * @param keyword
	 * @param module
	 * @return
	 */
	public Map<String, Long> getSearchSuggestion(String keyword,String module) ;

}