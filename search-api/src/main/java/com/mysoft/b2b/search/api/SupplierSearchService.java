package com.mysoft.b2b.search.api;

import com.mysoft.b2b.search.param.SupplierParam;

import java.util.List;
import java.util.Map;

/**
 * 供应商搜索
 * 
 * @author ganq
 * 
 */
public interface SupplierSearchService {
	
	/**
	 * 获取供应商搜索结果
	 * @param supplierParam 查询参数
	 * @return List<AnnouncementsVO>
	 * 		   searchResult ： 搜索结果
	 * 		   totalRecordNum ： 总记录数 		
	 * 
	 */
	Map<String,Object> getSearchResult(SupplierParam supplierParam);

	/**
	 * 获取需要推送的供应商id列表
	 * @param paramMap：（下列为map中传递的参数）
	 * 			category(List<String>) ：服务分类编码
	 * 			qualify(List<Map<String,String>>):资质列表
	 * 			serviceArea(List<String>) : 服务区域
	 * 			registerFund(Integer) : 注册资本
	 * 			buildYears(Integer) : 成立年限
	 * 			caseNum(Integer) : 案例数量
	 * 			page(Integer) :页码
	 * 			pageSize(Integer):每页数据条数
	 * @return List<String>供应商id列表
	 */
	List<String> getPushSupplierIds(Map<String,Object> paramMap);
	  	
}