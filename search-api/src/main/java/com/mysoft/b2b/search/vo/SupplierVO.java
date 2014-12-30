package com.mysoft.b2b.search.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 供应商信息VO。
 * @author ganq
 * 
 */
public class SupplierVO implements Serializable {
	private static final long serialVersionUID = 6165289100697434750L;
	/**
	 * 供应商id
	 */
	private String supplierId;
	
	/**
	 * 供应商名称
	 */
	private String companyName;
	
	/**
	 * 供应商旗舰店url
	 */
	private String supplierUrl;
	
	/**
	 * 供应商简称
	 */
	private String shortName;
	
	/**
	 * 被关注次数
	 */
	private int followCount;
	
	/**
	 * 成立年限
	 */
	private Integer establishYear;
	
	/**
	 * 入库次数
	 */
	private Integer inStorageCount;
	
	/**
	 * 7天内登录次数
	 */
	private Integer loginCount; 
	
	/**
	 * 资料完整度
	 */
	private Integer dataCount; 
	
	/**
	 * 中标次数
	 */
	private Integer awardBidCount; 

	/**
	 * 品牌拥有方式列表
	 */
	private List brandOwnTypeList;
	
	/**
	 * 资质等级名称
	 */
	private List qualificationLevelName;
	
	/**
	 * 服务区域
	 */
	private List projectLocation;
	
	/**
	 * 服务区域所属大区
	 */
	private List projectArea;
	
	private List basicCategoryName;
	
	/**
	 * 认证
	 */
	private List authenticationName;
	
	/**
	 * 注册所在省
	 */
	private String regProvinceName;
	
	/**
	 * 注册所在市
	 */
	private String regCityName;
	
	/**
	 * 注册详细地址
	 */
	private String regAddress;
	
	/**
	 * 服务区域
	 */
	private List serviceAreaName;
	
	/**
	 * 注册资金（未转人民币汇率前）
	 */
	private Integer regCapital;
	
	/**
	 * 是否显示“明源审核”
	 */
	private boolean authTag;
	
	/**
	 * 币种
	 */
	private String currency;
	/**
	 * 勋章
	 */
	private Integer medalLevel;
	
	/**
	 * 供应商类型
	 */
	private String supplierType;
	
	/**
	 * 业务范围
	 */
	private String businessScope;
	
	/**
	 * 默认荣誉
	 */
	private String defaultAward;
	
	/**
	 * 项目案例名称
	 */
	private List projectName;

	/**
	 * 项目数量
	 */
	private Integer projectCount;
	/**
	 * 产品名称 
	 */
	private List productName;
	
	/**
	 * 产品数量
	 */
	private Integer productCount;
	
	/**
	 * 公司法人
	 */
	private String legalName;
	
	/**
	 * 排序分值
	 */
	private Double sortScore;
	
	/**
	 * 搜索用基础分类名称
	 */
	private List searchBasicCategoryName;
	
	/**
	 * 供应商背书信息
	 */
	private List endorsementList;

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getFollowCount() {
		return followCount;
	}

	public void setFollowCount(int followCount) {
		this.followCount = followCount;
	}

	public Integer getEstablishYear() {
		return establishYear;
	}

	public void setEstablishYear(Integer establishYear) {
		this.establishYear = establishYear;
	}

	public Integer getInStorageCount() {
		return inStorageCount;
	}

	public void setInStorageCount(Integer inStorageCount) {
		this.inStorageCount = inStorageCount;
	}

	public Integer getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	public Integer getAwardBidCount() {
		return awardBidCount;
	}

	public void setAwardBidCount(Integer awardBidCount) {
		this.awardBidCount = awardBidCount;
	}

	public List getBrandOwnTypeList() {
		return brandOwnTypeList;
	}

	public void setBrandOwnTypeList(List brandOwnTypeList) {
		this.brandOwnTypeList = brandOwnTypeList;
	}

	public List getQualificationLevelName() {
		return qualificationLevelName;
	}

	public void setQualificationLevelName(List qualificationLevelName) {
		this.qualificationLevelName = qualificationLevelName;
	}

	public List getProjectLocation() {
		return projectLocation;
	}

	public void setProjectLocation(List projectLocation) {
		this.projectLocation = projectLocation;
	}

	public List getAuthenticationName() {
		return authenticationName;
	}

	public void setAuthenticationName(List authenticationName) {
		this.authenticationName = authenticationName;
	}

	public String getRegProvinceName() {
		return regProvinceName;
	}

	public void setRegProvinceName(String regProvinceName) {
		this.regProvinceName = regProvinceName;
	}

	public String getRegCityName() {
		return regCityName;
	}

	public void setRegCityName(String regCityName) {
		this.regCityName = regCityName;
	}

	public String getRegAddress() {
		return regAddress;
	}

	public void setRegAddress(String regAddress) {
		this.regAddress = regAddress;
	}

	public List getServiceAreaName() {
		return serviceAreaName;
	}

	public void setServiceAreaName(List serviceAreaName) {
		this.serviceAreaName = serviceAreaName;
	}

	public Integer getRegCapital() {
		return regCapital;
	}

	public void setRegCapital(Integer regCapital) {
		this.regCapital = regCapital;
	}
	
	public Integer getMedalLevel() {
		return medalLevel;
	}

	public void setMedalLevel(Integer medalLevel) {
		this.medalLevel = medalLevel;
	}

	public List getBasicCategoryName() {
		return basicCategoryName;
	}

	public void setBasicCategoryName(List basicCategoryName) {
		this.basicCategoryName = basicCategoryName;
	}

	public String getSupplierUrl() {
		return supplierUrl;
	}

	public void setSupplierUrl(String supplierUrl) {
		this.supplierUrl = supplierUrl;
	}

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	public String getBusinessScope() {
		return businessScope;
	}

	public void setBusinessScope(String businessScope) {
		this.businessScope = businessScope;
	}

	public String getDefaultAward() {
		return defaultAward;
	}

	public void setDefaultAward(String defaultAward) {
		this.defaultAward = defaultAward;
	}

	public List getProjectName() {
		return projectName;
	}

	public void setProjectName(List projectName) {
		this.projectName = projectName;
	}

	public List getProductName() {
		return productName;
	}

	public void setProductName(List productName) {
		this.productName = productName;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public List getSearchBasicCategoryName() {
		return searchBasicCategoryName;
	}

	public void setSearchBasicCategoryName(List searchBasicCategoryName) {
		this.searchBasicCategoryName = searchBasicCategoryName;
	}

	public Double getSortScore() {
		return sortScore;
	}

	public void setSortScore(Double sortScore) {
		this.sortScore = sortScore;
	}

	public List getProjectArea() {
		return projectArea;
	}

	public void setProjectArea(List projectArea) {
		this.projectArea = projectArea;
	}

	public boolean isAuthTag() {
		return authTag;
	}

	public void setAuthTag(boolean authTag) {
		this.authTag = authTag;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getProjectCount() {
		return projectCount;
	}

	public void setProjectCount(Integer projectCount) {
		this.projectCount = projectCount;
	}

	public Integer getProductCount() {
		return productCount;
	}

	public void setProductCount(Integer productCount) {
		this.productCount = productCount;
	}

	
	public List getEndorsementList() {
		return endorsementList;
	}

	public void setEndorsementList(List endorsementList) {
		this.endorsementList = endorsementList;
	}

	@Override
	public String toString() {
		return "SupplierVO [supplierId=" + supplierId + ", companyName=" + companyName + ", supplierUrl=" + supplierUrl
				+ ", shortName=" + shortName + ", followCount=" + followCount + ", establishYear=" + establishYear
				+ ", inStorageCount=" + inStorageCount + ", loginCount=" + loginCount + ", dataCount=" + dataCount
				+ ", awardBidCount=" + awardBidCount + ", brandOwnTypeList=" + brandOwnTypeList
				+ ", qualificationLevelName=" + qualificationLevelName + ", projectLocation=" + projectLocation
				+ ", projectArea=" + projectArea + ", basicCategoryName=" + basicCategoryName + ", authenticationName="
				+ authenticationName + ", regProvinceName=" + regProvinceName + ", regCityName=" + regCityName
				+ ", regAddress=" + regAddress + ", serviceAreaName=" + serviceAreaName + ", regCapital=" + regCapital
				+ ", authTag=" + authTag + ", currency=" + currency + ", medalLevel=" + medalLevel + ", supplierType="
				+ supplierType + ", businessScope=" + businessScope + ", defaultAward=" + defaultAward
				+ ", projectName=" + projectName + ", projectCount=" + projectCount + ", productName=" + productName
				+ ", productCount=" + productCount + ", legalName=" + legalName + ", sortScore=" + sortScore
				+ ", searchBasicCategoryName=" + searchBasicCategoryName + ", endorsementList=" + endorsementList + "]";
	}
	
	
}
