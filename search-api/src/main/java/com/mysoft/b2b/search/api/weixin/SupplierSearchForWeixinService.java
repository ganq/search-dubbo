package com.mysoft.b2b.search.api.weixin;

import com.mysoft.b2b.search.param.SupplierParam;

import java.util.Map;

/**
 * 供应商搜索（微信专用）
 * 
 * @author ganq
 * 
 */
public interface SupplierSearchForWeixinService {
		
	/**
	 * 获取供应商搜索结果
	 * @param supplierParam
	 * @return
	 */
	Map<String,Object> getSupplierSearchResult(SupplierParam supplierParam);
	
	/**
	 * 获取供应商统计
	 * 
	 * @return
	 */
	Map<String,Object> getSupplierStat();
	
	
	
}