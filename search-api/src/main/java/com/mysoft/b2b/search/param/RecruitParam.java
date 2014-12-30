package com.mysoft.b2b.search.param;

import java.io.Serializable;
import java.util.Date;

/**
 * 招标预告搜索查询参数
 * @author ganq
 *
 */
public class RecruitParam extends BaseParam implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public RecruitParam(){
		super();
		super.setSearchModule(SearchModule.RECRUIT);
		super.setSearchSource(SearchSource.WEBSITE);
	}

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
	 * 状态
	 */
	private String state;
	/**
	 * 注册资本
	 */
	private String regcapital;


    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getSdatesort() {
        return sdatesort;
    }

    public void setSdatesort(String sdatesort) {
        this.sdatesort = sdatesort;
    }

    public String getPdatesort() {
        return pdatesort;
    }

    public void setPdatesort(String pdatesort) {
        this.pdatesort = pdatesort;
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

    @Override
    public String toString() {
        return "RecruitParam{" +
                "sdate='" + sdate + '\'' +
                ", auditTime=" + auditTime +
                ", sdatesort='" + sdatesort + '\'' +
                ", pdatesort='" + pdatesort + '\'' +
                ", state='" + state + '\'' +
                ", regcapital='" + regcapital + '\'' +
                "} " + super.toString();
    }
}
