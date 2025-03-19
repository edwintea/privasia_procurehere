package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Yogesh
 */
public class PrPojo implements Serializable {

	private static final long serialVersionUID = -5706067632082706741L;

	private String id;

	private String prId;

	private String name;

	private String referenceNumber;

	private String description;

	private BusinessUnit businessUnit;

	private String supplierName;

	private PrStatus status;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date prCreatedDate;

	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date prApprovedDate;

	private User ApprovedBy;

	private BigDecimal grandTotal;

	private FavouriteSupplier supplier;

	private String decimal;

	private Currency currency;

	private int srNo;

	private String createdByName;

	private String unitName;

	private String prStatus;

	private String prSupplier;

	private String approvedByName;

	private String currencyName;

	private String prCreatedDateStr;

	private String prApprovedDateStr;
	
	private String approvePrRemark;

	private String poNumber;

	/**
	 * @return the poNumber
	 */
	public String getPoNumber() {
		return poNumber;
	}

	/**
	 * @param poNumber the poNumber to set
	 */
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	/**
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}

	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	/**
	 * @return the prStatus
	 */
	public String getPrStatus() {
		return prStatus;
	}

	/**
	 * @param prStatus the prStatus to set
	 */
	public void setPrStatus(String prStatus) {
		this.prStatus = prStatus;
	}

	/**
	 * @return the prSupplier
	 */
	public String getPrSupplier() {
		return prSupplier;
	}

	/**
	 * @param prSupplier the prSupplier to set
	 */
	public void setPrSupplier(String prSupplier) {
		this.prSupplier = prSupplier;
	}

	/**
	 * @return the approvedByName
	 */
	public String getApprovedByName() {
		return approvedByName;
	}

	/**
	 * @param approvedByName the approvedByName to set
	 */
	public void setApprovedByName(String approvedByName) {
		this.approvedByName = approvedByName;
	}

	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param unitName the unitName to set
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

 
	/**
	 * @return the createdByName
	 */
	public String getCreatedByName() {
		return createdByName;
	}

	/**
	 * @param createdByName the createdByName to set
	 */
	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	/**
	 * @return the srNo
	 */
	public int getSrNo() {
		return srNo;
	}

	/**
	 * @param srNo the srNo to set
	 */
	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the supplier
	 */
	public FavouriteSupplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(FavouriteSupplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the prId
	 */
	public String getPrId() {
		return prId;
	}

	/**
	 * @param prId the prId to set
	 */
	public void setPrId(String prId) {
		this.prId = prId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the businessUnit
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the status
	 */
	public PrStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(PrStatus status) {
		this.status = status;
	}

	/**
	 * @return the prCreatedDate
	 */
	public Date getPrCreatedDate() {
		return prCreatedDate;
	}

	/**
	 * @param prCreatedDate the prCreatedDate to set
	 */
	public void setPrCreatedDate(Date prCreatedDate) {
		this.prCreatedDate = prCreatedDate;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the prApprovedDate
	 */
	public Date getPrApprovedDate() {
		return prApprovedDate;
	}

	/**
	 * @param prApprovedDate the prApprovedDate to set
	 */
	public void setPrApprovedDate(Date prApprovedDate) {
		this.prApprovedDate = prApprovedDate;
	}

	/**
	 * @return the approvedBy
	 */
	public User getApprovedBy() {
		return ApprovedBy;
	}

	/**
	 * @param approvedBy the approvedBy to set
	 */
	public void setApprovedBy(User approvedBy) {
		ApprovedBy = approvedBy;
	}

	/**
	 * @return the grandTotal
	 */
	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getPrCreatedDateStr() {
		return prCreatedDateStr;
	}

	public void setPrCreatedDateStr(String prCreatedDateStr) {
		this.prCreatedDateStr = prCreatedDateStr;
	}

	public String getPrApprovedDateStr() {
		return prApprovedDateStr;
	}

	public void setPrApprovedDateStr(String prApprovedDateStr) {
		this.prApprovedDateStr = prApprovedDateStr;
	}

	public String getApprovePrRemark() {
		return approvePrRemark;
	}

	public void setApprovePrRemark(String approvePrRemark) {
		this.approvePrRemark = approvePrRemark;
	}
	

}
