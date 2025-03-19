package com.privasia.procurehere.core.pojo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.privasia.procurehere.core.enums.Status;

/**
 * @author Yogesh
 */
public class SupplierIntigrationPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9209619501887324007L;

	@ApiModelProperty(notes = "Supplier Company Name", required = true)
	private String companyName;

	@ApiModelProperty(notes = "Supplier Admin Mobile Number", required = true)
	private String mobileNumber;
	@ApiModelProperty(notes = "Supplier Company Contact Number", required = true)
	private String companyContactNumber;
	@ApiModelProperty(notes = "Supplier Company Country Code e.g IN", required = true)
	private String countryCode;

	@ApiModelProperty(notes = "Supplier Company Registration Number", required = true)
	private String companyRegistrationNumber;

	@ApiModelProperty(notes = "Supplier Full name", required = true)
	private String fullName;

	@ApiModelProperty(notes = "Supplier Login Email", required = true)
	private String loginEmail;

	@ApiModelProperty(notes = "Supplier Communication Email", required = false)
	private String communicationEmail;

	@ApiModelProperty(notes = "Supplier Designation", required = true)
	private String designation;

	@ApiModelProperty(notes = "Supplier Designation", required = false)
	private String remarks;

	@ApiModelProperty(notes = "Supplier Fax Number", required = false)
	private String faxNumber;

	@ApiModelProperty(notes = "Supplier Status, default is Active", required = false)
	private Status status;

	@ApiModelProperty(notes = "Industry Category e.g CODE-CATEGORY,CATEGORY,CODE", required = true)
	private String[] industryCategory;

	@ApiModelProperty(notes = "Product Category code e.g CODE-CATEGORY", required = false)
	private String productCategory;

	@ApiModelProperty(notes = "Supplier Vendor code", required = false)
	private String vendorCode;

	@ApiModelProperty(notes = "Supplier Rating", required = false)
	private String rating;

	@ApiModelProperty(notes = "Supplier Tags", required = false)
	private List<String> supplierTags;

	@ApiModelProperty(notes = "Supplier State code", required = false)
	private String stateCode;

	@ApiModelProperty(notes = "Business Coverage State", required = false)
	private List<String> businessCoverage;

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
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the companyContactNumber
	 */
	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	/**
	 * @param companyContactNumber the companyContactNumber to set
	 */
	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
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
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the loginEmail
	 */
	public String getLoginEmail() {
		return loginEmail;
	}

	/**
	 * @param loginEmail the loginEmail to set
	 */
	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	/**
	 * @return the communicationEmail
	 */
	public String getCommunicationEmail() {
		return communicationEmail;
	}

	/**
	 * @param communicationEmail the communicationEmail to set
	 */
	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
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
	 * @return the faxNumber
	 */
	public String getFaxNumber() {
		return faxNumber;
	}

	/**
	 * @param faxNumber the faxNumber to set
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the industryCategory
	 */
	public String[] getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */
	public void setIndustryCategory(String[] industryCategory) {
		this.industryCategory = industryCategory;
	}

	/**
	 * @return the productCategory
	 */
	public String getProductCategory() {
		return productCategory;
	}

	/**
	 * @param productCategory the productCategory to set
	 */
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * @return the vendorCode
	 */
	public String getVendorCode() {
		return vendorCode;
	}

	/**
	 * @param vendorCode the vendorCode to set
	 */
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}

	/**
	 * @return the supplierTags
	 */
	public List<String> getSupplierTags() {
		return supplierTags;
	}

	/**
	 * @param supplierTags the supplierTags to set
	 */
	public void setSupplierTags(List<String> supplierTags) {
		this.supplierTags = supplierTags;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public List<String> getBusinessCoverage() {
		return businessCoverage;
	}

	public void setBusinessCoverage(List<String> businessCoverage) {
		this.businessCoverage = businessCoverage;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SupplierIntigrationPojo [companyName=" + companyName + ", mobileNumber=" + mobileNumber + ", companyContactNumber=" + companyContactNumber + ", countryCode=" + countryCode + ", companyRegistrationNumber=" + companyRegistrationNumber + ", fullName=" + fullName + ", loginEmail=" + loginEmail + ", communicationEmail=" + communicationEmail + ", designation=" + designation + ", remarks=" + remarks + ", faxNumber=" + faxNumber + ", status=" + status + ", industryCategory=" + Arrays.toString(industryCategory) + ", productCategory=" + productCategory + ", vendorCode=" + vendorCode + "]";
	}

	
	public String toLogString() {
		return "SupplierIntigrationPojo [companyName=" + companyName + ",  companyContactNumber=" + companyContactNumber + ", countryCode=" + countryCode + ", companyRegistrationNumber=" + companyRegistrationNumber + ", loginEmail=" + loginEmail + ",  vendorCode=" + vendorCode + "]";
	}


	
}
