package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Ravi
 */
public class SupplierActivationIntegrationPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9209619501887324007L;

	@ApiModelProperty(notes = "Supplier Company Name", required = true)
	private String companyName;

	@ApiModelProperty(notes = "Supplier Company Country Code e.g IN", required = true)
	private String countryCode;

	@ApiModelProperty(notes = "Supplier Company Registration Number", required = true)
	private String companyRegistrationNumber;

	@ApiModelProperty(notes = "Reason for Suspend/Blacklist", required = false)
	private String remarks;

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the companyRegistrationNumber
	 */
	public String getCompanyRegistrationNumber() {
		return companyRegistrationNumber;
	}

	/**
	 * @param companyRegistrationNumber the companyRegistrationNumber to set
	 */
	public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String toLogString() {
		return "SupplierActivationIntegrationPojo [companyName=" + companyName + ", countryCode=" + countryCode + ", companyRegistrationNumber=" + companyRegistrationNumber + ", remarks=" + remarks + "]";
	}

	
}
