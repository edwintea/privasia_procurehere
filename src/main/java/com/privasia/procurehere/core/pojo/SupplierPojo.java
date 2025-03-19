package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.entity.SupplierBoardOfDirectors;
import com.privasia.procurehere.core.entity.SupplierProjects;
import com.privasia.procurehere.core.entity.SupplierTags;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author yogesh created for supplier profile pdf
 */

/**
 * @author silicon1
 */
public class SupplierPojo implements Serializable {

	private static final long serialVersionUID = -4081304002746630127L;

	private String id;

	private String companyName;

	private String mobileNumber;

	private String companyContactNumber;

	private String countryName;

	private String companyRegistrationNumber;

	private String fullName;

	private String fsFullName;

	private String loginEmail;

	private String communicationEmail;

	private String designation;

	private String remarks;

	private String status;

	private String fsStatus;

	private Integer yearOfEstablished;

	private String faxNumber;
	private String companyWebsite;

	private String companystatus;

	private String line1;
	private String line2;

	private String city;
	private String state;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date registrationCompleteDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date registrationDate;

	private String registrationCompleteDateS;

	private List<NaicsCodes> naicsCodeslist;

	private List<Coverage> coverages;

	private List<SupplierProjects> supplierProjects;

	private boolean registrationComplete;

	private boolean favourite;

	private String taxRegistrationNumber;

	private String currencyCode;

	private String paidUpCapital;

	private List<SupplierFinanicalDocuments> supplierFinancialDocuments;

	private List<SupplierBoardOfDirectors> supplierBoardOfDirectors;

	private Date createdDate;

	private String countryCode;

	private String vendorCode;

	private BigDecimal ratings;

	private List<IndustryCategory> industryCategoryList;

	private List<ProductCategory> productCategoryList;

	private List<SupplierTags> supplierTagsList;

	private String industryCategory;

	private String productCategory;

	private String supplierTags;

	private String teanantType;

	private String registrationCompleteDateStr;

	private Long invited = 0l;

	private Long submited = 0l;

	public SupplierPojo() {
	}

	public SupplierPojo(String id, String companyName) {
		this.id = id;
		this.companyName = companyName;
	}

	public SupplierPojo(String id, String fFullName, FavouriteSupplierStatus fStatus) {
		this.id = id;
		this.fsFullName = fFullName;
		this.fsStatus = fStatus.name();
	}

	public SupplierPojo(String id, String companyName, String countryName, Integer yearOfEstablished, Date registrationDate, FavouriteSupplierStatus fStatus, SupplierStatus status, String fFullName, String fullName, boolean registrationComplete) {
		this.id = id;
		this.companyName = companyName;
		this.countryName = countryName;
		this.yearOfEstablished = yearOfEstablished;
		this.registrationCompleteDate = registrationDate;
		this.fullName = fFullName;
		this.status = fStatus.name();
		// this.fullName = fullName;
		// this.status = status.name();
		this.favourite = true;
		this.registrationComplete = registrationComplete;
	}

	public SupplierPojo(String id, String companyName, String countryName, Integer yearOfEstablished, Date registrationDate, FavouriteSupplierStatus fStatus, SupplierStatus status, String fFullName, String fullName, boolean registrationComplete, Date createdDate) {
		this.id = id;
		this.companyName = companyName;
		this.countryName = countryName;
		this.yearOfEstablished = yearOfEstablished;
		this.registrationCompleteDate = registrationDate;
		this.fullName = fFullName;
		this.status = fStatus.name();
		// this.fullName = fullName;
		// this.status = status.name();
		this.favourite = true;
		this.registrationComplete = registrationComplete;
		this.createdDate = createdDate;
	}

	public SupplierPojo(String id, String companyName, String countryName, Integer yearOfEstablished, Date registrationDate, SupplierStatus status, String fullName, String communicationEmail, String designation, String companyContactNumber, String faxNumber, boolean registrationComplete) {
		this.id = id;
		this.companyName = companyName;
		this.countryName = countryName;
		this.yearOfEstablished = yearOfEstablished;
		this.registrationCompleteDate = registrationDate;
		this.fullName = fullName;
		this.status = Status.ACTIVE.name();
		this.communicationEmail = communicationEmail;
		this.designation = designation;
		this.companyContactNumber = companyContactNumber;
		this.faxNumber = faxNumber;
		this.registrationComplete = registrationComplete;

	}

	public SupplierPojo(String companyName, String countryName, Date registrationDate, String companyRegistrationNumber) {
		this.companyName = companyName;
		this.countryName = countryName;
		this.registrationCompleteDate = registrationDate;
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	public SupplierPojo(String id, String companyName, String countryName, SupplierStatus status, Date registrationDate, String companyRegistrationNumber, String fullName, String mobileNumber, String line1, String line2) {
		this.id = id;
		this.companyName = companyName;
		this.countryName = countryName;
		this.status = status.name();
		this.registrationDate = registrationDate;
		this.companyRegistrationNumber = companyRegistrationNumber;
		this.fullName = fullName;
		this.mobileNumber = mobileNumber;
		this.line1 = line1;
		this.line2 = line2;

	}

	/**
	 * @return the industryCategoryList
	 */
	public List<IndustryCategory> getIndustryCategoryList() {
		return industryCategoryList;
	}

	/**
	 * @param industryCategoryList the industryCategoryList to set
	 */
	public void setIndustryCategoryList(List<IndustryCategory> industryCategoryList) {
		this.industryCategoryList = industryCategoryList;
	}

	/**
	 * @return the productCategoryList
	 */
	public List<ProductCategory> getProductCategoryList() {
		return productCategoryList;
	}

	/**
	 * @param productCategoryList the productCategoryList to set
	 */
	public void setProductCategoryList(List<ProductCategory> productCategoryList) {
		this.productCategoryList = productCategoryList;
	}

	/**
	 * @return the supplierTagsList
	 */
	public List<SupplierTags> getSupplierTagsList() {
		return supplierTagsList;
	}

	/**
	 * @param supplierTagsList the supplierTagsList to set
	 */
	public void setSupplierTagsList(List<SupplierTags> supplierTagsList) {
		this.supplierTagsList = supplierTagsList;
	}

	/**
	 * @return the industryCategory
	 */
	public String getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */
	public void setIndustryCategory(String industryCategory) {
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
	 * @return the supplierTags
	 */
	public String getSupplierTags() {
		return supplierTags;
	}

	/**
	 * @param supplierTags the supplierTags to set
	 */
	public void setSupplierTags(String supplierTags) {
		this.supplierTags = supplierTags;
	}

	/**
	 * @return the ratings
	 */
	public BigDecimal getRatings() {
		return ratings;
	}

	/**
	 * @param ratings the ratings to set
	 */
	public void setRatings(BigDecimal ratings) {
		this.ratings = ratings;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCompanyRegistrationNumber() {
		return companyRegistrationNumber;
	}

	public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getLoginEmail() {
		return loginEmail;
	}

	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	public String getCommunicationEmail() {
		return communicationEmail;
	}

	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getYearOfEstablished() {
		return yearOfEstablished;
	}

	public void setYearOfEstablished(Integer yearOfEstablished) {
		this.yearOfEstablished = yearOfEstablished;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public String getCompanystatus() {
		return companystatus;
	}

	public void setCompanystatus(String companystatus) {
		this.companystatus = companystatus;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getRegistrationCompleteDate() {
		return registrationCompleteDate;
	}

	public void setRegistrationCompleteDate(Date registrationCompleteDate) {
		this.registrationCompleteDate = registrationCompleteDate;
	}

	public List<NaicsCodes> getNaicsCodeslist() {
		return naicsCodeslist;
	}

	public void setNaicsCodeslist(List<NaicsCodes> naicsCodeslist) {
		this.naicsCodeslist = naicsCodeslist;
	}

	public List<Coverage> getCoverages() {
		return coverages;
	}

	public void setCoverages(List<Coverage> coverages) {
		this.coverages = coverages;
	}

	public List<SupplierProjects> getSupplierProjects() {
		return supplierProjects;
	}

	public void setSupplierProjects(List<SupplierProjects> supplierProjects) {
		this.supplierProjects = supplierProjects;
	}

	public String getRegistrationCompleteDateS() {
		return registrationCompleteDateS;
	}

	public void setRegistrationCompleteDateS(String registrationCompleteDateS) {
		this.registrationCompleteDateS = registrationCompleteDateS;
	}

	/**
	 * @return the fsFullName
	 */
	public String getFsFullName() {
		return fsFullName;
	}

	/**
	 * @param fsFullName the fsFullName to set
	 */
	public void setFsFullName(String fsFullName) {
		this.fsFullName = fsFullName;
	}

	/**
	 * @return the fsStatus
	 */
	public String getFsStatus() {
		return fsStatus;
	}

	/**
	 * @param fsStatus the fsStatus to set
	 */
	public void setFsStatus(String fsStatus) {
		this.fsStatus = fsStatus;
	}

	/**
	 * @return the favourite
	 */
	public boolean isFavourite() {
		return favourite;
	}

	/**
	 * @param favourite the favourite to set
	 */
	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * @return the registrationComplete
	 */
	public boolean isRegistrationComplete() {
		return registrationComplete;
	}

	/**
	 * @param registrationComplete the registrationComplete to set
	 */
	public void setRegistrationComplete(boolean registrationComplete) {
		this.registrationComplete = registrationComplete;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPojo other = (SupplierPojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getTaxRegistrationNumber() {
		return taxRegistrationNumber;
	}

	public void setTaxRegistrationNumber(String taxRegistrationNumber) {
		this.taxRegistrationNumber = taxRegistrationNumber;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPaidUpCapital() {
		return paidUpCapital;
	}

	public void setPaidUpCapital(String paidUpCapital) {
		this.paidUpCapital = paidUpCapital;
	}

	public List<SupplierFinanicalDocuments> getSupplierFinancialDocuments() {
		return supplierFinancialDocuments;
	}

	public void setSupplierFinancialDocuments(List<SupplierFinanicalDocuments> supplierFinancialDocuments) {
		this.supplierFinancialDocuments = supplierFinancialDocuments;
	}

	public List<SupplierBoardOfDirectors> getSupplierBoardOfDirectors() {
		return supplierBoardOfDirectors;
	}

	public void setSupplierBoardOfDirectors(List<SupplierBoardOfDirectors> supplierBoardOfDirectors) {
		this.supplierBoardOfDirectors = supplierBoardOfDirectors;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getTeanantType() {
		return teanantType;
	}

	public void setTeanantType(String teanantType) {
		this.teanantType = teanantType;
	}

	public String getRegistrationCompleteDateStr() {
		return registrationCompleteDateStr;
	}

	public void setRegistrationCompleteDateStr(String registrationCompleteDateStr) {
		this.registrationCompleteDateStr = registrationCompleteDateStr;
	}

	/**
	 * @return the invited
	 */
	public Long getInvited() {
		return invited;
	}

	/**
	 * @param invited the invited to set
	 */
	public void setInvited(Long invited) {
		this.invited = invited;
	}

	/**
	 * @return the submited
	 */
	public Long getSubmited() {
		return submited;
	}

	/**
	 * @param submited the submited to set
	 */
	public void setSubmited(Long submited) {
		this.submited = submited;
	}

}
