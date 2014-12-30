package com.mysoft.b2b.search.param;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 搜索参数基类
 * @author ganq
 *
 */
public class BaseParam {

    /**
     * Id 主键
     */
    private String id;

    /**
	 * 搜索关键字
	 */
	private String keyword;
	/**
	 * ----- 用于存储搜索记录
	 */
	// 用户id
	private String userId;
	// 用户类型 ：游客，供应商，开发商
	private String userType;
	// 用户访问ip
	private String ipAddress;
	// 搜索来源 ：website 网站端，weixin：微信端
	private SearchSource searchSource;
	// 搜索模块
	private SearchModule searchModule;
	/**
	 * ----- 用于存储搜索记录
	 */
	/**
	 * 一级运营分类
	 */
	private String codelevel1;
	/**
	 * 二级运营分类
	 */
	private String codelevel2;
	/**
	 * 三级运营分类
	 */
	private String codelevel3;
	/**
	 * 页码
	 */
	private String page;
	
	/**
	 * 记录行号
	 */
	private int rowNum;
	
	/**
	 * 每页记录条数
	 */
	private int pageSize = 20;
	
	/**
	 * 分类编码
	 */
	private String categorycode;
	
	/**
	 * 分类名称
	 */
	private String categoryname;
	
	/**
	 * 地区
	 */
	private String location;
	
	/**
	 * 一级分类的key
	 */
	private String codeKeyLevel1;
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
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
	
	public String getCategoryname() {
		return categoryname;
	}
	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	
	public String getCategorycode() {
		return categorycode;
	}
	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}
	public int getRowNum() {
		int p = 1;
		if (NumberUtils.isNumber(this.page)) {
			p = Integer.parseInt(this.page);
			p = p < 1 ? 1 : p;
			
		}
		setPage(p+"");
		this.rowNum = (p-1) * this.pageSize;
		return rowNum;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getCodelevel1() {
		return codelevel1;
	}
	public void setCodelevel1(String codelevel1) {
		this.codelevel1 = codelevel1;
	}
	public String getCodelevel2() {
		return codelevel2;
	}
	public void setCodelevel2(String codelevel2) {
		this.codelevel2 = codelevel2;
	}
	public String getCodelevel3() {
		return codelevel3;
	}
	public void setCodelevel3(String codelevel3) {
		this.codelevel3 = codelevel3;
	}
	public String getCodeKeyLevel1() {
		return codeKeyLevel1;
	}
	public void setCodeKeyLevel1(String codeKeyLevel1) {
		this.codeKeyLevel1 = codeKeyLevel1;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public SearchSource getSearchSource() {
		return searchSource;
	}
	public void setSearchSource(SearchSource searchSource) {
		this.searchSource = searchSource;
	}
	public SearchModule getSearchModule() {
		return searchModule;
	}
	public void setSearchModule(SearchModule searchModule) {
		this.searchModule = searchModule;
	}


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BaseParam{" +
                "id='" + id + '\'' +
                ", keyword='" + keyword + '\'' +
                ", userId='" + userId + '\'' +
                ", userType='" + userType + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", searchSource=" + searchSource +
                ", searchModule=" + searchModule +
                ", codelevel1='" + codelevel1 + '\'' +
                ", codelevel2='" + codelevel2 + '\'' +
                ", codelevel3='" + codelevel3 + '\'' +
                ", page='" + page + '\'' +
                ", rowNum=" + rowNum +
                ", pageSize=" + pageSize +
                ", categorycode='" + categorycode + '\'' +
                ", categoryname='" + categoryname + '\'' +
                ", location='" + location + '\'' +
                ", codeKeyLevel1='" + codeKeyLevel1 + '\'' +
                '}';
    }
}
