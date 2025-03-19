package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;

public class SupplierSearchFilterPojo implements Serializable {

	private static final long serialVersionUID = -6560512936324388511L;

	private String id;

	private String supplierIds;

	private String companyname;

	private String registrationnumber;

	private SupplierStatus accountstatus;

	private SubscriptionStatus subscriptionstatus;

	private String companytype;

	private String country;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSupplierIds() {
		return supplierIds;
	}

	public void setSupplierIds(String supplierIds) {
		this.supplierIds = supplierIds;
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

	public SupplierStatus getAccountstatus() {
		return accountstatus;
	}

	public void setAccountstatus(SupplierStatus accountstatus) {
		this.accountstatus = accountstatus;
	}

	public SubscriptionStatus getSubscriptionstatus() {
		return subscriptionstatus;
	}

	public void setSubscriptionstatus(SubscriptionStatus subscriptionstatus) {
		this.subscriptionstatus = subscriptionstatus;
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
