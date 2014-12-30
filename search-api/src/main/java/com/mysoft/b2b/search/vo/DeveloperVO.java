package com.mysoft.b2b.search.vo;

import java.io.Serializable;

/**
 * 开发商信息VO。
 * @author ganq
 * 
 */
public class DeveloperVO implements Serializable {
	private static final long serialVersionUID = 6165289100697434750L;
	
	/**
	 * 开发商id
	 */
	private String developerId;
	
	/**
	 * 开发商名称
	 */
	private String developerName;
	
	/**
	 * 开发商旗舰店url
	 */
	private String developerUrl;
	
	/**
	 * 开发商logo
	 */
	private String companyLogo;
	
	/**
	 * 开发商简介
	 */
	private String developerIntro;
		
	/**
	 * 开发商简称
	 */
	private String shortName;
	
	/**
	 * 正在招标中的招标预告数量
	 */
	private int biddingCount;
		

	/**
	 *历史招标数量
	 */
	private int biddingHistoryCount;
	/**
	 * 注册所在省
	 */
	private String regProvinceName;
	
	/**
	 * 注册所在省code
	 */
	private String regProvinceCode;
	
	/**
	 * 注册所在市
	 */
	private String regCityName;
	
	/**
	 * 注册所在市Code
	 */
	private String regCityCode;
	
	/**
	 * 注册地址字符串
	 */
	private String regLocation;
	
	/**
	 * 注册详细地址
	 */
	private String regAddress;
	
	/**
	 * 项目信息
	 */
	private String projectInfo;
	
	/**
	 * 项目数量
	 */
	private int projectCount;
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
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

	public String getDeveloperUrl() {
		return developerUrl;
	}

	public void setDeveloperUrl(String developerUrl) {
		this.developerUrl = developerUrl;
	}

	public int getBiddingCount() {
		return biddingCount;
	}

	public void setBiddingCount(int biddingCount) {
		this.biddingCount = biddingCount;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getDeveloperIntro() {
		return developerIntro;
	}

	public void setDeveloperIntro(String developerIntro) {
		this.developerIntro = developerIntro;
	}


	public String getRegProvinceCode() {
		return regProvinceCode;
	}

	public void setRegProvinceCode(String regProvinceCode) {
		this.regProvinceCode = regProvinceCode;
	}

	public String getRegCityCode() {
		return regCityCode;
	}

	public void setRegCityCode(String regCityCode) {
		this.regCityCode = regCityCode;
	}

	public String getProjectInfo() {
		return projectInfo;
	}

	public void setProjectInfo(String projectInfo) {
		this.projectInfo = projectInfo;
	}

	public String getRegLocation() {
		return regLocation;
	}

	public void setRegLocation(String regLocation) {
		this.regLocation = regLocation;
	}

	public int getProjectCount() {
		return projectCount;
	}

	public void setProjectCount(int projectCount) {
		this.projectCount = projectCount;
	}

	public int getBiddingHistoryCount() {
		return biddingHistoryCount;
	}

	public void setBiddingHistoryCount(int biddingHistoryCount) {
		this.biddingHistoryCount = biddingHistoryCount;
	}

	@Override
	public String toString() {
		return "DeveloperVO [developerId=" + developerId + ", developerName=" + developerName + ", developerUrl="
				+ developerUrl + ", companyLogo=" + companyLogo + ", developerIntro=" + developerIntro + ", shortName="
				+ shortName + ", biddingCount=" + biddingCount + ", biddingHistoryCount=" + biddingHistoryCount
				+ ", regProvinceName=" + regProvinceName + ", regProvinceCode=" + regProvinceCode + ", regCityName="
				+ regCityName + ", regCityCode=" + regCityCode + ", regLocation=" + regLocation + ", regAddress="
				+ regAddress + ", projectInfo=" + projectInfo + ", projectCount=" + projectCount + "]";
	}

	
	
}
