package com.mysoft.b2b.search.api.weixin;

import java.util.Map;

import com.mysoft.b2b.search.param.AnnouncementParam;

/**
 * 招标预告搜索（微信专用）
 * 
 * @author ganq
 * 
 */
public interface BiddingSearchForWeixinService {
	/**
	 * 获取招标预告搜索结果
	 * @param announcementParam
	 * @return
	 */
	Map<String,Object> getBiddingSearchResult(AnnouncementParam announcementParam);
	
}