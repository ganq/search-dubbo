package com.mysoft.b2b.search.api.weixin;

import java.util.Map;

import com.mysoft.b2b.search.param.SupplierParam;

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