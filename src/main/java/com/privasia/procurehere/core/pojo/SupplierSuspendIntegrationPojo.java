package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Nitin Otageri
 */
public class SupplierSuspendIntegrationPojo implements Serializable {

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

	@ApiModelProperty(notes = "Reason for Suspend/Blacklist", required = true)
	private String remarks;

	// @ApiModelProperty(notes = "Suspend Start Date in YYYYMMDD format", required = true, hidden = false)
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	// private Date startDate;
	//
	// @ApiModelProperty(notes = "Suspend End Date in YYYYMMDD format", required = true, hidden = false)
	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+08:00")
	// private Date endDate;

	@ApiModelProperty(notes = "Suspend Start Date in dd/MM/yyyy HH:mm:ss format", required = true, hidden = false)
	private String startDateStr;

	@ApiModelProperty(notes = "Suspend End Date in dd/MM/yyyy HH:mm:ss  format", required = true, hidden = false)
	private String endDateStr;

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

	/**
	 * @return the startDateStr
	 */
	public String getStartDateStr() {
		return startDateStr;
	}

	/**
	 * @param startDateStr the startDateStr to set
	 */
	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}

	/**
	 * @return the endDateStr
	 */
	public String getEndDateStr() {
		return endDateStr;
	}

	/**
	 * @param endDateStr the endDateStr to set
	 */
	public void setEndDateStr(String endDateStr) {
		this.endDateStr = endDateStr;
	}

}
