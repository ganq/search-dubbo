package com.mysoft.b2b.search.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 招标预告VO。
 * @author ganq
 * 
 */
public class AnnouncementsVO implements Serializable {
	private static final long serialVersionUID = 6165289100697434750L;
	
	/**
	 * 唯一id
	 */
	private String uid;
	
	/**
	 * 招标公告id
	 */
	private String biddingId;
	
	/**
	 * 所属开发商Id
	 */
	private String developerId;
	
	/**
	 * 所属开发商名称
	 */
	private String developerName;
	
	/**
	 * 开发商logo
	 */
	private String developerLogo;
	
	/**
	 * 所属开发商简称
	 */
	private String developerShortName;
		
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 短标题
	 */
	private String shortTitle;
	
	/**
	 * 详情
	 */
	private String detail;
	
	/**
	 * 所属基础分类
	 */
	private List basicCategoryName;
	
	/**
	 * 所属运营分类
	 */
	private List operationCategoryName;
	
	/**
	 * 采购方式
	 */
	private String procurementType;

	/**
	 * 预计发放招标文件开始时间
	 */
	private Date expectPublishFileBeginDate;

	/**
	 * 预计发放招标文件结束时间
	 */
	private Date expectPublishFileEndDate;

	/**
	 * 招标负责人
	 */
	private String biddingOwer;

	/**
	 * 手机
	 */
	private String mobile;

	/**
	 * 固定电话
	 */
	private String tel;

	/**
	 * 电子邮箱
	 */
	private String email;

	/**
	 * 报名截止时间
	 */
	private Date registerEndDate;

	/**
	 * 项目所在地
	 */
	private String projectProvince;

	/**
	 * 项目所在市
	 */
	private String projectCity;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 发布时间
	 */
	private Date publishTime;
	
	/**
	 * 是否可用
	 */
	private Integer isValid;
	
	/**
	 * 状态
	 */
	private Integer state;
	
	/**
	 * 投标保证金
	 */
	private Double deposit;
	
	/**
	 * 项目名称
	 */
	private String projectName;
	/**
	 * 中标单位
	 */
	private String biddingCompany;
	/**
	 * 报名条件-服务分类code
	 */
	private List scCategoryCode;
	/**
	 * 报名条件-服务分类name
	 */
	private List scCategoryName;
	/**
	 * 报名条件-服务区域code
	 */
	private List scServiceAreaCode;
	/**
	 * 报名条件-服务区域name
	 */
	private List scServiceAreaName;
	/**
	 * 报名条件-注册资本
	 */
	private int scRegCapital;
	/**
	 * 报名条件-资质code
	 */
	private List scQualificationCode;
	/**
	 * 报名条件-资质name
	 */
	private List scQualificationName;
	
	/**
	 * 报名条件-成立年限
	 */
	private int scBuildYears;
	/**
	 * 报名条件-业绩案例条数
	 */
	private int scCaseNum;
		
	/**
	 * 报名条件-仅限于服务区域的案例
	 */
	private Boolean scIsLimitServiceAreaCase;
	/**
	 * 招标范围
	 */
	private String biddingRange;
	
	/**
	 * 项目类型
	 */
	private String projectType;
	
	/**
	 * 项目图片
	 */
	private String projectImage;
	
	/**
	 * 占地面积
	 */
	private Double projectArea;
	/**
	 * 建筑面积
	 */
	private Double projectTotArea;
	/**
	 * 已报名供应商数量
	 */
	private int registerCount;
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getBiddingId() {
		return biddingId;
	}
	public void setBiddingId(String biddingId) {
		this.biddingId = biddingId;
	}
	public String getDeveloperId() {
		return developerId;
	}
	public void setDeveloperId(String developerId) {
		this.developerId = developerId;
	}
	public String getDeveloperName() {
		return developerName;
	}
	public void setDeveloperName(String developerName) {
		this.developerName = developerName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getProcurementType() {
		return procurementType;
	}
	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
	}
	
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Integer getIsValid() {
		return isValid;
	}
	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getExpectPublishFileBeginDate() {
		return expectPublishFileBeginDate;
	}
	public void setExpectPublishFileBeginDate(Date expectPublishFileBeginDate) {
		this.expectPublishFileBeginDate = expectPublishFileBeginDate;
	}
	public Date getExpectPublishFileEndDate() {
		return expectPublishFileEndDate;
	}
	public void setExpectPublishFileEndDate(Date expectPublishFileEndDate) {
		this.expectPublishFileEndDate = expectPublishFileEndDate;
	}
	public String getBiddingOwer() {
		return biddingOwer;
	}
	public void setBiddingOwer(String biddingOwer) {
		this.biddingOwer = biddingOwer;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public Date getRegisterEndDate() {
		return registerEndDate;
	}
	public void setRegisterEndDate(Date registerEndDate) {
		this.registerEndDate = registerEndDate;
	}
	public String getProjectProvince() {
		return projectProvince;
	}
	public void setProjectProvince(String projectProvince) {
		this.projectProvince = projectProvince;
	}
	public String getProjectCity() {
		return projectCity;
	}
	public void setProjectCity(String projectCity) {
		this.projectCity = projectCity;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}
	public List getBasicCategoryName() {
		return basicCategoryName;
	}
	public void setBasicCategoryName(List basicCategoryName) {
		this.basicCategoryName = basicCategoryName;
	}
	public List getOperationCategoryName() {
		return operationCategoryName;
	}
	public void setOperationCategoryName(List operationCategoryName) {
		this.operationCategoryName = operationCategoryName;
	}
	public String getDeveloperShortName() {
		return developerShortName;
	}
	public void setDeveloperShortName(String developerShortName) {
		this.developerShortName = developerShortName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getBiddingCompany() {
		return biddingCompany;
	}
	public void setBiddingCompany(String biddingCompany) {
		this.biddingCompany = biddingCompany;
	}
	public List getScCategoryCode() {
		return scCategoryCode;
	}
	public void setScCategoryCode(List scCategoryCode) {
		this.scCategoryCode = scCategoryCode;
	}
	
	public List getScServiceAreaCode() {
		return scServiceAreaCode;
	}
	public void setScServiceAreaCode(List scServiceAreaCode) {
		this.scServiceAreaCode = scServiceAreaCode;
	}
	
	public int getScRegCapital() {
		return scRegCapital;
	}
	public void setScRegCapital(int scRegCapital) {
		this.scRegCapital = scRegCapital;
	}
	public List getScQualificationCode() {
		return scQualificationCode;
	}
	public void setScQualificationCode(List scQualificationCode) {
		this.scQualificationCode = scQualificationCode;
	}
	public List getScQualificationName() {
		return scQualificationName;
	}
	public void setScQualificationName(List scQualificationName) {
		this.scQualificationName = scQualificationName;
	}
	
	public int getScBuildYears() {
		return scBuildYears;
	}
	public void setScBuildYears(int scBuildYears) {
		this.scBuildYears = scBuildYears;
	}
	public int getScCaseNum() {
		return scCaseNum;
	}
	public void setScCaseNum(int scCaseNum) {
		this.scCaseNum = scCaseNum;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	
	public Double getDeposit() {
		return deposit;
	}
	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}
	
	public String getDeveloperLogo() {
		return developerLogo;
	}
	public void setDeveloperLogo(String developerLogo) {
		this.developerLogo = developerLogo;
	}
	public String getBiddingRange() {
		return biddingRange;
	}
	public void setBiddingRange(String biddingRange) {
		this.biddingRange = biddingRange;
	}
	public String getProjectImage() {
		return projectImage;
	}
	public void setProjectImage(String projectImage) {
		this.projectImage = projectImage;
	}
	public double getProjectArea() {
		return projectArea;
	}
	public void setProjectArea(Double projectArea) {
		this.projectArea = projectArea;
	}
	public double getProjectTotArea() {
		return projectTotArea;
	}
	public void setProjectTotArea(Double projectTotArea) {
		this.projectTotArea = projectTotArea;
	}
	public int getRegisterCount() {
		return registerCount;
	}
	public void setRegisterCount(int registerCount) {
		this.registerCount = registerCount;
	}
	
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public Boolean getScIsLimitServiceAreaCase() {
		return scIsLimitServiceAreaCase;
	}
	public void setScIsLimitServiceAreaCase(Boolean scIsLimitServiceAreaCase) {
		this.scIsLimitServiceAreaCase = scIsLimitServiceAreaCase;
	}
	public List getScCategoryName() {
		return scCategoryName;
	}
	public void setScCategoryName(List scCategoryName) {
		this.scCategoryName = scCategoryName;
	}
	public List getScServiceAreaName() {
		return scServiceAreaName;
	}
	public void setScServiceAreaName(List scServiceAreaName) {
		this.scServiceAreaName = scServiceAreaName;
	}
	@Override
	public String toString() {
		return "AnnouncementsVO [uid=" + uid + ", biddingId=" + biddingId + ", developerId=" + developerId
				+ ", developerName=" + developerName + ", developerLogo=" + developerLogo + ", developerShortName="
				+ developerShortName + ", title=" + title + ", shortTitle=" + shortTitle + ", detail=" + detail
				+ ", basicCategoryName=" + basicCategoryName + ", operationCategoryName=" + operationCategoryName
				+ ", procurementType=" + procurementType + ", expectPublishFileBeginDate=" + expectPublishFileBeginDate
				+ ", expectPublishFileEndDate=" + expectPublishFileEndDate + ", biddingOwer=" + biddingOwer
				+ ", mobile=" + mobile + ", tel=" + tel + ", email=" + email + ", registerEndDate=" + registerEndDate
				+ ", projectProvince=" + projectProvince + ", projectCity=" + projectCity + ", createTime="
				+ createTime + ", publishTime=" + publishTime + ", isValid=" + isValid + ", state=" + state
				+ ", deposit=" + deposit + ", projectName=" + projectName + ", biddingCompany=" + biddingCompany
				+ ", scCategoryCode=" + scCategoryCode + ", scCategoryName=" + scCategoryName + ", scServiceAreaCode="
				+ scServiceAreaCode + ", scServiceAreaName=" + scServiceAreaName + ", scRegCapital=" + scRegCapital
				+ ", scQualificationCode=" + scQualificationCode + ", scQualificationName=" + scQualificationName
				+ ", scBuildYears=" + scBuildYears + ", scCaseNum=" + scCaseNum + ", scIsLimitServiceAreaCase="
				+ scIsLimitServiceAreaCase + ", biddingRange=" + biddingRange + ", projectType=" + projectType
				+ ", projectImage=" + projectImage + ", projectArea=" + projectArea + ", projectTotArea="
				+ projectTotArea + ", registerCount=" + registerCount + "]";
	}
		
}
