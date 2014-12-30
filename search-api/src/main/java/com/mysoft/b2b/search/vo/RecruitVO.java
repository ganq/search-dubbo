package com.mysoft.b2b.search.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 招募VO。
 * @author ganq
 * 
 */
public class RecruitVO implements Serializable {
	private static final long serialVersionUID = 6165289100697434750L;

	/**
	 * 招募id
	 */
	private String recruitId;
	
	/**
	 * 所属开发商Id
	 */
	private String companyId;
	
	/**
	 * 所属开发商名称
	 */
	private String companyName;
	
	/**
	 * 开发商logo
	 */
	private String companyLogo;
	
	/**
	 * 所属开发商简称
	 */
	private String companyShortName;
		
	/**
	 * 标题
	 */
	private String subject;

	/**
	 * 所属基础分类
	 */
	private List basicCategoryName;
	
	/**
	 * 所属运营分类
	 */
	private List operationCategoryName;

	/**
	 * 报名截止时间
	 */
	private Date registerEndDate;

	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 发布时间
	 */
	private Date publishTime;

	/**
	 * 状态
	 */
	private Integer state;

	/**
	 * 报名条件
	 */
	private String registerCondition;

	/**
	 * 报名条件-注册资本
	 */
	private Integer registerFund;

    /**
     * 招募图片
     */
    private String image;

    /**
     * 招募地区
     */
    private List serviceAreaCityName;

    public String getRecruitId() {
        return recruitId;
    }

    public void setRecruitId(String recruitId) {
        this.recruitId = recruitId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyShortName() {
        return companyShortName;
    }

    public void setCompanyShortName(String companyShortName) {
        this.companyShortName = companyShortName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Date getRegisterEndDate() {
        return registerEndDate;
    }

    public void setRegisterEndDate(Date registerEndDate) {
        this.registerEndDate = registerEndDate;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getRegisterCondition() {
        return registerCondition;
    }

    public void setRegisterCondition(String registerCondition) {
        this.registerCondition = registerCondition;
    }

    public Integer getRegisterFund() {
        return registerFund;
    }

    public void setRegisterFund(Integer registerFund) {
        this.registerFund = registerFund;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List getServiceAreaCityName() {
        return serviceAreaCityName;
    }

    public void setServiceAreaCityName(List serviceAreaCityName) {
        this.serviceAreaCityName = serviceAreaCityName;
    }

    @Override
    public String toString() {
        return "RecruitVO{" +
                "recruitId='" + recruitId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyLogo='" + companyLogo + '\'' +
                ", companyShortName='" + companyShortName + '\'' +
                ", subject='" + subject + '\'' +
                ", basicCategoryName=" + basicCategoryName +
                ", operationCategoryName=" + operationCategoryName +
                ", registerEndDate=" + registerEndDate +
                ", createTime=" + createTime +
                ", publishTime=" + publishTime +
                ", state=" + state +
                ", registerCondition='" + registerCondition + '\'' +
                ", registerFund=" + registerFund +
                ", image='" + image + '\'' +
                ", serviceAreaCityName='" + serviceAreaCityName + '\'' +
                '}';
    }
}
