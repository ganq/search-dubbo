package com.mysoft.b2b.search.provider;

import com.google.code.morphia.Datastore;
import com.mysoft.b2b.commons.exception.PlatformUncheckException;
import com.mysoft.b2b.search.api.SearchRecordService;
import com.mysoft.b2b.search.mongodb.MongoDBService;
import com.mysoft.b2b.search.param.BaseParam;
import com.mysoft.b2b.search.param.SearchRecordParam;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service("searchRecordService")
public class SearchRecordServiceImpl implements SearchRecordService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private MongoDBService mongoDBService;

	/**
	 * 增加一条搜索记录到Mongodb
	 */
	public void addSearchRecord(SearchRecordParam searchRecordParam) {
		Datastore ds = mongoDBService.getDatastore();
		if (ds == null) {
			throw new PlatformUncheckException("mongodb init exception", null);
		}
		searchRecordParam.setSearchTime(new Date());
		ds.save(searchRecordParam);
		logger.info("------------------------添加一条搜索记录成功!---------------------------------");
	}

	/**
	 * 执行 addSearchRecord
     *
	 */
	public void execAddSearchRecord(BaseParam baseParam, Set<String> analysisWords, long numFound) {

		SearchRecordParam searchRecordParam = new SearchRecordParam();
		searchRecordParam.setUserId(baseParam.getUserId());
		searchRecordParam.setUserType(baseParam.getUserType());
		searchRecordParam.setKeyword(baseParam.getKeyword());
		searchRecordParam.setAnalysisKeyword(StringUtils.join(analysisWords.toArray(), " "));
		searchRecordParam.setResultRows(numFound);
		searchRecordParam.setSearchModule(baseParam.getSearchModule().getDescription());
		searchRecordParam.setIpAddress(baseParam.getIpAddress());
		searchRecordParam.setSource(baseParam.getSearchSource().getDescription());
		addSearchRecord(searchRecordParam);

	}

}
