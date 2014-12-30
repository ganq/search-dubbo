package com.mysoft.b2b.search.api.weixin;

import com.mysoft.b2b.search.param.AnnouncementParam;
import com.mysoft.b2b.search.param.RecruitParam;

import java.util.Map;

/**
 * 招募搜索（微信专用）
 * 
 * @author ganq
 * 
 */
public interface RecruitSearchForWeixinService {
	/**
	 * 获取招标预告搜索结果
	 */
	Map<String,Object> getRecruitSearchResult(RecruitParam recruitParam);
	
}