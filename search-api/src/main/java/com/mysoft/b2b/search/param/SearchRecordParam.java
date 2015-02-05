package com.mysoft.b2b.search.param;

import com.google.code.morphia.annotations.Entity;

import java.util.Date;

/**
 * 搜索记录参数
 * @author ganq
 *
 */

@Entity(value = "searchRecord", noClassnameStored = true)
public class SearchRecordParam {
	
	private String searchModule;
	private String source;
	private String keyword;
	private String analysisKeyword;
	private Long resultRows;
	private Date searchTime;
	private String userId;
	private String userType;
	private String ipAddress;
	
	public String getSearchModule() {
		return searchModule;
	}
	public void setSearchModule(String searchModule) {
		this.searchModule = searchModule;
	}
	
	public String getAnalysisKeyword() {
		return analysisKeyword;
	}
	public void setAnalysisKeyword(String analysisKeyword) {
		this.analysisKeyword = analysisKeyword;
	}
	public Long getResultRows() {
		return resultRows;
	}
	public void setResultRows(Long resultRows) {
		this.resultRows = resultRows;
	}
	public Date getSearchTime() {
		return searchTime;
	}
	public void setSearchTime(Date searchTime) {
		this.searchTime = searchTime;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	
	
}
