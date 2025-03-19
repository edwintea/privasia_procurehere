package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.PlanType;

public class BuyerSearchFilterPojo implements Serializable {

	private static final long serialVersionUID = -6560512936324388511L;

	private String id;

	private String buyerIds;

	private String companyname;

	private String registrationnumber;

	private BuyerStatus accountstatus;

	private PlanType plantype;

	private String companytype;

	private String country;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBuyerIds() {
		return buyerIds;
	}

	public void setBuyerIds(String buyerIds) {
		this.buyerIds = buyerIds;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getRegistrationnumber() {
		return registrationnumber;
	}

	public void setRegistrationnumber(String registrationnumber) {
		this.registrationnumber = registrationnumber;
	}

	public BuyerStatus getAccountstatus() {
		return accountstatus;
	}

	public void setAccountstatus(BuyerStatus accountstatus) {
		this.accountstatus = accountstatus;
	}

	public PlanType getPlantype() {
		return plantype;
	}

	public void setPlantype(PlanType plantype) {
		this.plantype = plantype;
	}

	public String getCompanytype() {
		return companytype;
	}

	public void setCompanytype(String companytype) {
		this.companytype = companytype;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
