package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.enums.PrStatus;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Nitin Otageri
 */
public class PoPojo implements Serializable {

	private static final long serialVersionUID = 1678519231573639563L;

	@ApiModelProperty(notes = "Document Type", required = true)
	private String docType;

	@ApiModelProperty(notes = "Reference No", required = true)
	private String referenceNumber;

	@ApiModelProperty(notes = "Created Date in YYYYMMDD format", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date createdDate;

	@ApiModelProperty(notes = "Name", required = true)
	private String name;

	@ApiModelProperty(notes = "Created By", required = true)
	private String createdBy;

	@ApiModelProperty(notes = "Requester", required = true)
	private String requester;

	@ApiModelProperty(notes = "Correspondence Address", required = false)
	private AddressPojo correspondenceAddress;

	@ApiModelProperty(notes = "Currency", required = true)
	private String currency;

	@ApiModelProperty(notes = "Decimal", required = true)
	private Integer decimal;

	@ApiModelProperty(notes = "Cost Center Code", required = false)
	private String costCenter;

	@ApiModelProperty(notes = "Business Unit Code", required = false)
	private String businessUnit;

	@ApiModelProperty(notes = "Available Budget", required = false)
	private BigDecimal availableBudget;

	@ApiModelProperty(notes = "Payment Term", required = true)
	private String paymentTerm;

	@ApiModelProperty(notes = "Delivery Address", required = false)
	private AddressPojo deliveryAddress;

	@ApiModelProperty(notes = "Delivery Date in YYYYMMDD format", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date deliveryDate;

	@ApiModelProperty(notes = "Delivery Receiver", required = false)
	private String deliveryReceiver;

	@ApiModelProperty(notes = "Supplier Name", required = false)
	private String supplierName;

	@ApiModelProperty(notes = "Supplier Code", required = true)
	private String supplierCode;

	@ApiModelProperty(notes = "Grand Total", required = true)
	private BigDecimal grandTotal;

	@ApiModelProperty(notes = "remarks", required = false)
	private String remarks;

	@ApiModelProperty(notes = "Terms And Conditions", required = false)
	private String termsAndConditions;

	@ApiModelProperty(notes = "List of Items", required = true)
	private List<PoItemsPojo> itemList;

	private int srNo;

	private String id;

	private String poId;

	private PrStatus status;

	private String description;

	private String unitName;

	private String postatus;

	private String poName;

	private FavouriteSupplier supplier;

	private String poNumber;

	private String prId;

	// private String openSupplierName;

	private String poCreatedBy;

	private Currency currencyCode;

	private String orderedBy;

	private Date orderedDate;

	private Date actionDate;

	private String buyer;

	private String createdDateStr;

	private String orderedDateStr;

	private String actionDateStr;

	private Date poRevisedDate;

	private String poRevisedDateStr;

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
	 * @return the poStatus
	 */
	public String getPoStatus() {
		return postatus;
	}

	/**
	 * @param poStatus the poStatus to set
	 */
	public void setPoStatus(String prStatus) {
		this.postatus = prStatus;
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
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
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
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}

	/**
	 * @param requester the requester to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
	}

	/**
	 * @return the correspondenceAddress
	 */
	public AddressPojo getCorrespondenceAddress() {
		return correspondenceAddress;
	}

	/**
	 * @param correspondenceAddress the correspondenceAddress to set
	 */
	public void setCorrespondenceAddress(AddressPojo correspondenceAddress) {
		this.correspondenceAddress = correspondenceAddress;
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
	 * @return the availableBudget
	 */
	public BigDecimal getAvailableBudget() {
		return availableBudget;
	}

	/**
	 * @param availableBudget the availableBudget to set
	 */
	public void setAvailableBudget(BigDecimal availableBudget) {
		this.availableBudget = availableBudget;
	}

	/**
	 * @return the paymentTerm
	 */
	public String getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @param paymentTerm the paymentTerm to set
	 */
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	/**
	 * @return the deliveryAddress
	 */
	public AddressPojo getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(AddressPojo deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the deliveryReceiver
	 */
	public String getDeliveryReceiver() {
		return deliveryReceiver;
	}

	/**
	 * @param deliveryReceiver the deliveryReceiver to set
	 */
	public void setDeliveryReceiver(String deliveryReceiver) {
		this.deliveryReceiver = deliveryReceiver;
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
	public List<PoItemsPojo> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<PoItemsPojo> itemList) {
		this.itemList = itemList;
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
	 * @return the currencyCode
	 */
	public Currency getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currency the currencyCode to set
	 */
	public void setCurrency(Currency currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the poCreatedBy
	 */
	public String getPoCreatedBy() {
		return poCreatedBy;
	}

	/**
	 * @param poCreatedBy the createdBy to set
	 */
	public void setPoCreatedBy(String poCreatedBy) {
		this.poCreatedBy = poCreatedBy;
	}

	/**
	 * @return the poName
	 */
	public String getPoName() {
		return poName;
	}

	/**
	 * @param poName the createdBy to set
	 */
	public void setPoName(String poName) {
		this.poName = poName;
	}

	/**
	 * @return the orderedDate
	 */
	public Date getOrderedDate() {
		return orderedDate;
	}

	/**
	 * @param orderedDate the orderedDate to set
	 */
	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
	}

	public void setOrderedBy(String orderedBy) {
		this.orderedBy = orderedBy;
	}

	public String getOrderedBy() {
		return orderedBy;
	}

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
	 * @return the poId
	 */
	public String getPoId() {
		return poId;
	}

	/**
	 * @param poId the poId to set
	 */
	public void setPoId(String poId) {
		this.poId = poId;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @return the buyer
	 */
	public String getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getPostatus() {
		return postatus;
	}

	public void setPostatus(String postatus) {
		this.postatus = postatus;
	}

	public void setCurrencyCode(Currency currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCreatedDateStr() {
		return createdDateStr;
	}

	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

	public String getOrderedDateStr() {
		return orderedDateStr;
	}

	public void setOrderedDateStr(String orderedDateStr) {
		this.orderedDateStr = orderedDateStr;
	}

	public String getActionDateStr() {
		return actionDateStr;
	}

	public void setActionDateStr(String actionDateStr) {
		this.actionDateStr = actionDateStr;
	}

	public Date getPoRevisedDate() {
		return poRevisedDate;
	}

	public void setPoRevisedDate(Date poRevisedDate) {
		this.poRevisedDate = poRevisedDate;
	}

	public String getPoRevisedDateStr() {
		return poRevisedDateStr;
	}

	public void setPoRevisedDateStr(String poRevisedDateStr) {
		this.poRevisedDateStr = poRevisedDateStr;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PoPojo [docType=" + docType + ", referenceNumber=" + referenceNumber + ", createdDate=" + createdDate + ", name=" + name + ", createdBy=" + createdBy + ", requester=" + requester + ", correspondenceAddress=" + correspondenceAddress + ", currency=" + currency + ", decimal=" + decimal + ", costCenter=" + costCenter + ", businessUnit=" + businessUnit + ", availableBudget=" + availableBudget + ", paymentTerm=" + paymentTerm + ", deliveryAddress=" + deliveryAddress + ", deliveryDate=" + deliveryDate + ", deliveryReceiver=" + deliveryReceiver + ", supplierName=" + supplierName + ", supplierCode=" + supplierCode + ", grandTotal=" + grandTotal + ", remarks=" + remarks + ", termsAndConditions=" + termsAndConditions + ", itemList=" + itemList + "]";
	}

}
