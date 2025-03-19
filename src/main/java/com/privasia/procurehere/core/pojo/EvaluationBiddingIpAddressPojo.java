package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

public class EvaluationBiddingIpAddressPojo implements Serializable {

	/**
	 * @author sudesha
	 */
	
	private static final long serialVersionUID = -8650204823048365415L;
	private String ipAddress;
	private String supplierCompanyName;
	private String ipAddress1;
	private String supplierCompanyName1;

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the supplierCompanyName
	 */
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	/**
	 * @param supplierCompanyName the supplierCompanyName to set
	 */
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	public String getIpAddress1() {
		return ipAddress1;
	}

	public void setIpAddress1(String ipAddress1) {
		this.ipAddress1 = ipAddress1;
	}

	public String getSupplierCompanyName1() {
		return supplierCompanyName1;
	}

	public void setSupplierCompanyName1(String supplierCompanyName1) {
		this.supplierCompanyName1 = supplierCompanyName1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationBiddingIpAddressPojo [ipAddress=" + ipAddress + ", supplierCompanyName=" + supplierCompanyName + ", ipAddress1=" + ipAddress1 + ", supplierCompanyName1=" + supplierCompanyName1 + "]";
	}

	
	
}
