package com.mysoft.b2b.search.api;

import java.util.Set;

import com.mysoft.b2b.search.param.BaseParam;
import com.mysoft.b2b.search.param.SearchRecordParam;

/**
 * 搜索记录
 * @author ganq
 *
 */
public interface SearchRecordService {

	/**
	 * 增加一条搜索记录到db
	 * @param searchRecordParam
	 */
	public void addSearchRecord(SearchRecordParam searchRecordParam);
	
	/**
	 * 执行 addSearchRecord 方法
	 * @param baseParam
	 * @param analysisWords
	 * @param numFound
	 */
	public void execAddSearchRecord(BaseParam baseParam, Set<String> analysisWords,long numFound);

}