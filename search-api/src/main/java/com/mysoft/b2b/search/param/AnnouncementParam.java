package com.mysoft.b2b.search.param;

import java.io.Serializable;
import java.util.Date;

/**
 * 招标预告搜索查询参数
 * @author ganq
 *
 */
public class AnnouncementParam extends BaseParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AnnouncementParam(){
		super();
		super.setSearchModule(SearchModule.BIDDING);
		super.setSearchSource(SearchSource.WEBSITE);
	}
	
	/**
	 * 城市查询
	 */
	private String city;
	
	/**
	 * 省份查询
	 */
	private String province;
	
	/**
	 * 城市查询
	 */
	private String cityId;
	
	/**
	 * 省份查询
	 */
	private String provinceId;
		
	/**
	 * 开发商id
	 */
	private String devid;
	/**
	 * 开发商名称
	 */
	private String devname;

	/**
	 * 报名截止日期
	 */
	private String sdate;
	
	/**
	 * 招标预告审核时间
	 */
	private Date auditTime;
	
	/**
	 * 报名截止日期排序
	 */
	private String sdatesort;
	
	/**
	 * 发布时间排序
	 */
	private String pdatesort;
	
	/**
	 * facet结果分类编码
	 */
	private String fccode;
	
	/**
	 * 状态
	 */
	private String state;
	/**
	 * 注册资本
	 */
	private String regcapital;
	
	/**
	 * 资质
	 */
	private String qualification;
	
	/**
	 * 三级分类下的资质
	 */
	private String qualificationLevel;
	
	/**
	 * 查询所有
	 */
	private boolean queryAll;
	
	
	
	
	
	public String getSdate() {
		return sdate;
	}
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}
	public String getFccode() {
		return fccode;
	}
	public void setFccode(String fccode) {
		this.fccode = fccode;
	}
	public String getSdatesort() {
		return sdatesort;
	}
	public void setSdatesort(String sdatesort) {
		this.sdatesort = sdatesort;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getDevname() {
		return devname;
	}
	public void setDevname(String devname) {
		this.devname = devname;
	}
	public String getDevid() {
		return devid;
	}
	public void setDevid(String devid) {
		this.devid = devid;
	}
	public String getPdatesort() {
		return pdatesort;
	}
	public void setPdatesort(String pdatesort) {
		this.pdatesort = pdatesort;
	}
	public boolean isQueryAll() {
		return queryAll;
	}
	public void setQueryAll(boolean queryAll) {
		this.queryAll = queryAll;
	}
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getRegcapital() {
		return regcapital;
	}
	public void setRegcapital(String regcapital) {
		this.regcapital = regcapital;
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
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}


    @Override
    public String toString() {
        return "AnnouncementParam{" +
                "superParam:" + super.toString() +
                "city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", cityId='" + cityId + '\'' +
                ", provinceId='" + provinceId + '\'' +
                ", devid='" + devid + '\'' +
                ", devname='" + devname + '\'' +
                ", sdate='" + sdate + '\'' +
                ", auditTime=" + auditTime +
                ", sdatesort='" + sdatesort + '\'' +
                ", pdatesort='" + pdatesort + '\'' +
                ", fccode='" + fccode + '\'' +
                ", state='" + state + '\'' +
                ", regcapital='" + regcapital + '\'' +
                ", qualification='" + qualification + '\'' +
                ", qualificationLevel='" + qualificationLevel + '\'' +
                ", queryAll=" + queryAll +
                '}';
    }
}
