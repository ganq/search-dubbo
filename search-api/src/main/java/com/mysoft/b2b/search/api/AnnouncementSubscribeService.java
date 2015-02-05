package com.mysoft.b2b.search.api;

import com.mysoft.b2b.search.param.AnnouncementParam;

import java.util.Map;

/**
 * 招标预告订阅搜索
 * 
 * @author ganq
 * 
 */
public interface AnnouncementSubscribeService {
	
	/**
	 * 获取搜索结果
	 * @param announcementParam 查询参数
	 * @return Map<String,Object>
	 * 		   searchResult ： 搜索结果
	 * 		   totalRecordNum ： 总记录数 		
	 */
	Map<String,Object> getSearchResult(AnnouncementParam announcementParam);

}