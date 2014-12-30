package com.mysoft.b2b.search.param;

import java.io.Serializable;

/**
 * 供应商搜索查询参数
 * 
 * @author ganq
 * 
 */
public class SupplierParam extends BaseParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SupplierParam() {
		super();
		super.setSearchModule(SearchModule.SUPPLIER);
		super.setSearchSource(SearchSource.WEBSITE);
	}

	/**
	 * 注册资本
	 */
	private String registeredcapital;
	
	/**
	 * 成立年限
	 */
	private String year;

	
	/**
	 * 资质
	 */
	private String qualification;
	
	/**
	 * 资质等级
	 */
	private String qualificationLevel;
	
	
	/**
	 * 按注册资金排序
	 */
	private String regsort;
	
	/**
	 * 按成立年份排序
	 */
	private String yearsort;
	
	// ------ 微信用 --------
	/**
	 * 省份编码
	 */
	private String province;
	
	/**
	 * 区域
	 */
	private String area;	
	// ------ 微信用 --------
	
	public String getRegisteredcapital() {
		return registeredcapital;
	}

	public void setRegisteredcapital(String registeredcapital) {
		this.registeredcapital = registeredcapital;		
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getQualificationLevel() {
		return qualificationLevel;
	}

	public void setQualificationLevel(String qualificationLevel) {
		this.qualificationLevel = qualificationLevel;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getRegsort() {
		return regsort;
	}

	public void setRegsort(String regsort) {
		this.regsort = regsort;
	}

	public String getYearsort() {
		return yearsort;
	}

	public void setYearsort(String yearsort) {
		this.yearsort = yearsort;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@Override
	public String toString() {
		return "SupplierParam [registeredcapital=" + registeredcapital + ", year=" + year + ", qualification="
				+ qualification + ", qualificationLevel=" + qualificationLevel + ", regsort=" + regsort + ", yearsort="
				+ yearsort + ", province=" + province + ", area=" + area + ", toString()=" + super.toString() + "]";
	}

}
