package com.mysoft.b2b.search.api;

import com.mysoft.b2b.search.param.AnnouncementParam;
import com.mysoft.b2b.search.vo.AnnouncementsVO;

import java.util.List;
import java.util.Map;

/**
 * 招标预告搜索
 * 
 * @author ganq
 * 
 */
public interface AnnouncementSearchService {
	
	/**
	 * 获取搜索结果
	 * @param announcementParam 查询参数
	 * @return Map<String,Object>
	 * 		   relatedCategory : 搜索结果分类
	 * 		   searchResult ： 搜索结果
	 * 		   totalRecordNum ： 总记录数
	 */
	Map<String,Object> getSearchResult(AnnouncementParam announcementParam);
	
	
	/**
	 * 通用搜索结果
	 * @param announcementParam 查询参数
	 */
	List<AnnouncementsVO> getCommonSearchResult(AnnouncementParam announcementParam);
	

}