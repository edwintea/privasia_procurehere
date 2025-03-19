package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author ravi
 */
public class ErpGrnPojo implements Serializable {

	private static final long serialVersionUID = 1678519231573639563L;

	@ApiModelProperty(notes = "Grn Id", required = true)
	private String grnId;

	@ApiModelProperty(notes = "Reference No", required = false)
	private String referenceNumber;

	@ApiModelProperty(notes = "Created Date in YYYYMMDD format", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date createdDate;

	@ApiModelProperty(notes = "GRN Title", required = true)
	private String grnTitle;

	@ApiModelProperty(notes = "Currency", required = true)
	private String currency;

	@ApiModelProperty(notes = "Decimal", required = false)
	private Integer decimal;

	@ApiModelProperty(notes = "Cost Center Code", required = false)
	private String costCenter;

	@ApiModelProperty(notes = "Business Unit Code", required = true)
	private String businessUnit;

	@ApiModelProperty(notes = "Address Title", required = false)
	private String deliveryAddressTitle;

	@ApiModelProperty(notes = "Address Line 1", required = true)
	private String deliveryAddressLine1;

	@ApiModelProperty(notes = "Address Line 2", required = true)
	private String deliveryAddressLine2;

	@ApiModelProperty(notes = "City", required = false)
	private String deliveryAddressCity;

	@ApiModelProperty(notes = "State", required = false)
	private String deliveryAddressState;

	@ApiModelProperty(notes = "zipCode", required = false)
	private String deliveryAddressZip;

	@ApiModelProperty(notes = "Country Code", required = false)
	private String deliveryAddressCountry;

	@ApiModelProperty(notes = "Supplier Name", required = true)
	private String supplierName;

	@ApiModelProperty(notes = "Supplier Address", required = true)
	private String supplierAddress;

	@ApiModelProperty(notes = "Supplier contact number", required = false)
	private String supplierTelNumber;

	@ApiModelProperty(notes = "Supplier fax number", required = false)
	private String supplierFaxNumber;

	@ApiModelProperty(notes = "Supplier tax number", required = false)
	private String supplierTaxNumber;

	@ApiModelProperty(notes = "Supplier Code", required = false)
	private String supplierCode;

	@ApiModelProperty(notes = "Grand Total", required = true)
	private BigDecimal grandTotal;

	@ApiModelProperty(notes = "remarks", required = false)
	private String remarks;

	@ApiModelProperty(notes = "Terms And Conditions", required = false)
	private String termsAndConditions;

	@ApiModelProperty(notes = "List of Items", required = true)
	private List<ErpGrnItemsPojo> itemList;

	/**
	 * @return the grnId
	 */
	public String getGrnId() {
		return grnId;
	}

	/**
	 * @param grnId the grnId to set
	 */
	public void setGrnId(String grnId) {
		this.grnId = grnId;
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

	/**
	 * @return the grnTitle
	 */
	public String getGrnTitle() {
		return grnTitle;
	}

	/**
	 * @param grnTitle the grnTitle to set
	 */
	public void setGrnTitle(String grnTitle) {
		this.grnTitle = grnTitle;
	}

	/**
	 * @return the deliveryAddressTitle
	 */
	public String getDeliveryAddressTitle() {
		return deliveryAddressTitle;
	}

	/**
	 * @param deliveryAddressTitle the deliveryAddressTitle to set
	 */
	public void setDeliveryAddressTitle(String deliveryAddressTitle) {
		this.deliveryAddressTitle = deliveryAddressTitle;
	}

	/**
	 * @return the deliveryAddressLine1
	 */
	public String getDeliveryAddressLine1() {
		return deliveryAddressLine1;
	}

	/**
	 * @param deliveryAddressLine1 the deliveryAddressLine1 to set
	 */
	public void setDeliveryAddressLine1(String deliveryAddressLine1) {
		this.deliveryAddressLine1 = deliveryAddressLine1;
	}

	/**
	 * @return the deliveryAddressLine2
	 */
	public String getDeliveryAddressLine2() {
		return deliveryAddressLine2;
	}

	/**
	 * @param deliveryAddressLine2 the deliveryAddressLine2 to set
	 */
	public void setDeliveryAddressLine2(String deliveryAddressLine2) {
		this.deliveryAddressLine2 = deliveryAddressLine2;
	}

	/**
	 * @return the deliveryAddressCity
	 */
	public String getDeliveryAddressCity() {
		return deliveryAddressCity;
	}

	/**
	 * @param deliveryAddressCity the deliveryAddressCity to set
	 */
	public void setDeliveryAddressCity(String deliveryAddressCity) {
		this.deliveryAddressCity = deliveryAddressCity;
	}

	/**
	 * @return the deliveryAddressState
	 */
	public String getDeliveryAddressState() {
		return deliveryAddressState;
	}

	/**
	 * @param deliveryAddressState the deliveryAddressState to set
	 */
	public void setDeliveryAddressState(String deliveryAddressState) {
		this.deliveryAddressState = deliveryAddressState;
	}

	/**
	 * @return the deliveryAddressZip
	 */
	public String getDeliveryAddressZip() {
		return deliveryAddressZip;
	}

	/**
	 * @param deliveryAddressZip the deliveryAddressZip to set
	 */
	public void setDeliveryAddressZip(String deliveryAddressZip) {
		this.deliveryAddressZip = deliveryAddressZip;
	}

	/**
	 * @return the deliveryAddressCountry
	 */
	public String getDeliveryAddressCountry() {
		return deliveryAddressCountry;
	}

	/**
	 * @param deliveryAddressCountry the deliveryAddressCountry to set
	 */
	public void setDeliveryAddressCountry(String deliveryAddressCountry) {
		this.deliveryAddressCountry = deliveryAddressCountry;
	}

	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the supplierTelNumber
	 */
	public String getSupplierTelNumber() {
		return supplierTelNumber;
	}

	/**
	 * @param supplierTelNumber the supplierTelNumber to set
	 */
	public void setSupplierTelNumber(String supplierTelNumber) {
		this.supplierTelNumber = supplierTelNumber;
	}

	/**
	 * @return the supplierFaxNumber
	 */
	public String getSupplierFaxNumber() {
		return supplierFaxNumber;
	}

	/**
	 * @param supplierFaxNumber the supplierFaxNumber to set
	 */
	public void setSupplierFaxNumber(String supplierFaxNumber) {
		this.supplierFaxNumber = supplierFaxNumber;
	}

	/**
	 * @return the supplierTaxNumber
	 */
	public String getSupplierTaxNumber() {
		return supplierTaxNumber;
	}

	/**
	 * @param supplierTaxNumber the supplierTaxNumber to set
	 */
	public void setSupplierTaxNumber(String supplierTaxNumber) {
		this.supplierTaxNumber = supplierTaxNumber;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the decimal
	 */
	public Integer getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(Integer decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
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
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
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
	 * @return the termsAndConditions
	 */
	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	/**
	 * @param termsAndConditions the termsAndConditions to set
	 */
	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	/**
	 * @return the itemList
	 */
	public List<ErpGrnItemsPojo> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<ErpGrnItemsPojo> itemList) {
		this.itemList = itemList;
	}

	public String toLogString() {
		return "ErpGrnPojo [grnId=" + grnId + ", referenceNumber=" + referenceNumber + ", createdDate=" + createdDate + ", grnTitle=" + grnTitle + ", currency=" + currency + ", decimal=" + decimal + ", businessUnit=" + businessUnit + ", deliveryAddressTitle=" + deliveryAddressTitle + ", deliveryAddressLine1=" + deliveryAddressLine1 + ", deliveryAddressLine2=" + deliveryAddressLine2 + ", deliveryAddressCity=" + deliveryAddressCity + ", deliveryAddressState=" + deliveryAddressState + ", deliveryAddressZip=" + deliveryAddressZip + ", deliveryAddressCountry=" + deliveryAddressCountry + ", supplierName=" + supplierName + ", supplierAddress=" + supplierAddress + ", supplierTelNumber=" + supplierTelNumber + ", supplierFaxNumber=" + supplierFaxNumber + ", supplierTaxNumber=" + supplierTaxNumber + ", supplierCode=" + supplierCode + ", grandTotal=" + grandTotal + ", remarks=" + remarks + "]";
	}

	
}
