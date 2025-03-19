package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;

public class SupplierSearchPojo implements Serializable {

	private static final long serialVersionUID = -4381347918992667333L;

	private String id;
	private String companyName;
	private String companyRegistrationNumber;
	private String industryCategories;
	private String naicsCode;
	private String projectName;
	private Boolean globalSearch;
	private String order;
	private FavouriteSupplierStatus status;
	private String productCategory;
	private Boolean registered;
	private long totalSupplierCount;
	private long pendingSupplierCount;

	private String[] supplierTagName;

	private String[] coverage;

	public SupplierSearchPojo(Long totalSupplierCount, Long pendingSupplierCount) {
		this.totalSupplierCount = (totalSupplierCount == null ? 0 : totalSupplierCount);
		this.pendingSupplierCount = (pendingSupplierCount == null ? 0 : pendingSupplierCount);
	}

	private String registrationOfCountry;
	private String[] state;
	private String communicationEmail;
	private String companyContactNumber;

	public SupplierSearchPojo(String id, String companyName, String communicationEmail, String companyContactNumber) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.communicationEmail = communicationEmail;
		this.companyContactNumber = companyContactNumber;
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

	public SupplierSearchPojo() {
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

	public String getCompanyRegistrationNumber() {
		return companyRegistrationNumber;
	}

	public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
		this.companyRegistrationNumber = companyRegistrationNumber;
	}

	public String getIndustryCategories() {
		return industryCategories;
	}

	public void setIndustryCategories(String industryCategories) {
		this.industryCategories = industryCategories;
	}

	public String getNaicsCode() {
		return naicsCode;
	}

	public void setNaicsCode(String naicsCode) {
		this.naicsCode = naicsCode;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the globalSearch
	 */
	public Boolean getGlobalSearch() {
		return globalSearch;
	}

	/**
	 * @param globalSearch the globalSearch to set
	 */
	public void setGlobalSearch(Boolean globalSearch) {
		this.globalSearch = globalSearch;
	}

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * @return the status
	 */
	public FavouriteSupplierStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(FavouriteSupplierStatus status) {
		this.status = status;
	}

	public Boolean getRegistered() {
		return registered;
	}

	public void setRegistered(Boolean registered) {
		this.registered = registered;
	}

	/**
	 * @return the registrationOfCountry
	 */
	public String getRegistrationOfCountry() {
		return registrationOfCountry;
	}

	/**
	 * @param registrationOfCountry the registrationOfCountry to set
	 */
	public void setRegistrationOfCountry(String registrationOfCountry) {
		this.registrationOfCountry = registrationOfCountry;
	}

	/**
	 * @return the state
	 */
	public String[] getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String[] state) {
		this.state = state;
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
	 * @return the totalSupplierCount
	 */
	public long getTotalSupplierCount() {
		return totalSupplierCount;
	}

	/**
	 * @param totalSupplierCount the totalSupplierCount to set
	 */
	public void setTotalSupplierCount(long totalSupplierCount) {
		this.totalSupplierCount = totalSupplierCount;
	}

	/**
	 * @return the pendingSupplierCount
	 */
	public long getPendingSupplierCount() {
		return pendingSupplierCount;
	}

	/**
	 * @param pendingSupplierCount the pendingSupplierCount to set
	 */
	public void setPendingSupplierCount(long pendingSupplierCount) {
		this.pendingSupplierCount = pendingSupplierCount;
	}

	/**
	 * @return the supplierTagName
	 */
	public String[] getSupplierTagName() {
		return supplierTagName;
	}

	/**
	 * @param supplierTagName the supplierTagName to set
	 */
	public void setSupplierTagName(String[] supplierTagName) {
		this.supplierTagName = supplierTagName;
	}

	public String[] getCoverage() {
		return coverage;
	}

	public void setCoverage(String[] coverage) {
		this.coverage = coverage;
	}

	public String toString() {
		return "SupplierSearchPojo [id=" + id + ", companyName=" + companyName + ", companyRegistrationNumber=" + companyRegistrationNumber + ", industryCategories=" + industryCategories + ", naicsCode=" + naicsCode + ", projectName=" + projectName + ", globalSearch=" + globalSearch + "]";
	}

}
