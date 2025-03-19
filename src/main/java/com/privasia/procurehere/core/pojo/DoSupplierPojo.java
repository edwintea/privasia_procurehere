package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author pooja
 */
public class DoSupplierPojo implements Serializable {

	private static final long serialVersionUID = 1238593349274070591L;

	private String id;

	private String deliveryId;

	private String referenceNumber;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	private String name;

	private String createdBy;

	private String currency;

	private String decimal;

	private String businessUnit;

	private BigDecimal grandTotal;

	private String poNumber;

	private DoStatus status;

	private String description;

	private String buyerCompanyName;

	private String supplierCompanyName;

	private String doIds;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date actionDate;

	private String actionBy;

	private String donumber;

	private String referencenumber;

	private String doname;

	private String ponumber;

	private String buyer;

	private String businessunit;

	private String docreatedby;

	private String supplier;

	private String dostatus;

	private String createdby;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date sendDate;

	private String sendDateStr;

	private String actionDateStr;

	private String createdDateStr;
	

	public DoSupplierPojo() {
		super();
	}

	/**
	 * constructor Supplier DO List
	 */
	public DoSupplierPojo(String id, String deliveryId, String name, String createdBy, Date createdDate, BigDecimal grandTotal, String decimal, String referenceNumber, String poNumber, String buyerCompanyName, DoStatus status, String businessUnit, String currency, String actionBy, Date actionDate, Date sendDate) {
		this.id = id;
		this.deliveryId = deliveryId;
		this.name = name;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.poNumber = poNumber;
		this.buyerCompanyName = buyerCompanyName;
		this.status = status;
		this.businessUnit = businessUnit;
		this.currency = currency;
		this.actionDate = actionDate;
		this.sendDate = sendDate;
	}

	/**
	 * constructor Buyer DO List
	 */
	public DoSupplierPojo(String id, String deliveryId, String name, Date createdDate, BigDecimal grandTotal, String decimal, String poNumber, String supplierCompanyName, DoStatus status, String businessUnit, String currency, String referenceNumber, String actionBy, Date actionDate, Date sendDate) {
		this.id = id;
		this.deliveryId = deliveryId;
		this.name = name;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.decimal = decimal;
		this.poNumber = poNumber;
		this.supplierCompanyName = supplierCompanyName;
		this.status = status;
		this.businessUnit = businessUnit;
		this.currency = currency;
		this.referenceNumber = referenceNumber;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.sendDate = sendDate;
	}

	public DoSupplierPojo(String id, String deliveryId, String name, Date createdDate, BigDecimal grandTotal, String poNumber, DoStatus status, String referenceNumber, String createdBy, String decimal, Date actionDate, Date sendDate) {
		this.id = id;
		this.deliveryId = deliveryId;
		this.name = name;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.poNumber = poNumber;
		this.status = status;
		this.referenceNumber = referenceNumber;
		this.createdBy = createdBy;
		this.decimal = decimal;
		this.actionDate = actionDate;
		this.sendDate = sendDate;

	}

	// PH-1889 Supplier Csv
	public DoSupplierPojo(String id, Date actionDate, Date sendDate, Date createdDate, BigDecimal grandTotal, String deliveryId, String name, String createdBy, String decimal, String referenceNumber, String poNumber, String buyerCompanyName, DoStatus status, String unitName, String currencyCode, String actionBy) {
		this.id = id;
		this.actionDate = actionDate;
		this.sendDate = sendDate;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.deliveryId = deliveryId;
		this.name = name;
		this.docreatedby = createdBy;
		this.decimal = decimal;
		this.referencenumber = referenceNumber;
		this.ponumber = poNumber;
		this.buyerCompanyName = buyerCompanyName;
		this.status = status;
		this.businessunit = unitName;
		this.currency = currencyCode;
		this.actionBy = actionBy;
	}

	// PH-1889 Buyer Csv
	public DoSupplierPojo(String id, Date actionDate, String deliveryId, Date createdDate, String name, BigDecimal grandTotal, String decimal, String poNumber, String supplierCompanyName, DoStatus status, String unitName, String currencyCode, Date sendDate, String referenceNumber, String actionBy) {
		this.id = id;
		this.actionDate = actionDate;
		this.deliveryId = deliveryId;
		this.createdDate = createdDate;
		this.name = name;
		this.grandTotal = grandTotal;
		this.decimal = decimal;
		this.ponumber = poNumber;
		this.supplierCompanyName = supplierCompanyName;
		this.status = status;
		this.businessunit = unitName;
		this.currency = currencyCode;
		this.sendDate = sendDate;
		this.referencenumber = referenceNumber;
		this.actionBy = actionBy;
	}

	/**
	 * @return the createdby
	 */
	public String getCreatedby() {
		return createdby;
	}

	/**
	 * @param createdby the createdby to set
	 */
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getDostatus() {
		return dostatus;
	}

	public void setDostatus(String dostatus) {
		this.dostatus = dostatus;
	}

	/**
	 * @return the donumber
	 */
	public String getDonumber() {
		return donumber;
	}

	/**
	 * @param donumber the donumber to set
	 */
	public void setDonumber(String donumber) {
		this.donumber = donumber;
	}

	/**
	 * @return the referencenumber
	 */
	public String getReferencenumber() {
		return referencenumber;
	}

	/**
	 * @param referencenumber the referencenumber to set
	 */
	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	/**
	 * @return the doname
	 */
	public String getDoname() {
		return doname;
	}

	/**
	 * @param doname the doname to set
	 */
	public void setDoname(String doname) {
		this.doname = doname;
	}

	/**
	 * @return the ponumber
	 */
	public String getPonumber() {
		return ponumber;
	}

	/**
	 * @param ponumber the ponumber to set
	 */
	public void setPonumber(String ponumber) {
		this.ponumber = ponumber;
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

	/**
	 * @return the businessunit
	 */
	public String getBusinessunit() {
		return businessunit;
	}

	/**
	 * @param businessunit the businessunit to set
	 */
	public void setBusinessunit(String businessunit) {
		this.businessunit = businessunit;
	}

	/**
	 * @return the docreatedby
	 */
	public String getDocreatedby() {
		return docreatedby;
	}

	/**
	 * @param docreatedby the docreatedby to set
	 */
	public void setDocreatedby(String docreatedby) {
		this.docreatedby = docreatedby;
	}

	/**
	 * @return the supplier
	 */
	public String getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the doIds
	 */
	public String getDoIds() {
		return doIds;
	}

	/**
	 * @param doIds the doIds to set
	 */
	public void setDoIds(String doIds) {
		this.doIds = doIds;
	}

	/**
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return the actionBy
	 */
	public String getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
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
	 * @return the deliveryId
	 */
	public String getDeliveryId() {
		return deliveryId;
	}

	/**
	 * @param deliveryId the deliveryId to set
	 */
	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
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
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	 * @return the status
	 */
	public DoStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(DoStatus status) {
		this.status = status;
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
	 * @return the buyerCompanyName
	 */
	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}

	/**
	 * @param buyerCompanyName the buyerCompanyName to set
	 */
	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
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

	/**
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate;
	}

	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	@Override
	public String toString() {
		return "DoSupplierPojo [id=" + id + ", deliveryId=" + deliveryId + ", referenceNumber=" + referenceNumber + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", name=" + name + ", createdBy=" + createdBy + ", currency=" + currency + ", decimal=" + decimal + ", businessUnit=" + businessUnit + ", grandTotal=" + grandTotal + ", poNumber=" + poNumber + ", status=" + status + ", description=" + description + ", buyerCompanyName=" + buyerCompanyName + ", supplierCompanyName=" + supplierCompanyName + ", doIds=" + doIds + ", actionDate=" + actionDate + ", actionBy=" + actionBy + ", donumber=" + donumber + ", referencenumber=" + referencenumber + ", doname=" + doname + ", ponumber=" + ponumber + ", buyer=" + buyer + ", businessunit=" + businessunit + ", docreatedby=" + docreatedby + ", supplier=" + supplier + "]";
	}

	public String getSendDateStr() {
		return sendDateStr;
	}

	public void setSendDateStr(String sendDateStr) {
		this.sendDateStr = sendDateStr;
	}

	public String getActionDateStr() {
		return actionDateStr;
	}

	public void setActionDateStr(String actionDateStr) {
		this.actionDateStr = actionDateStr;
	}

	public String getCreatedDateStr() {
		return createdDateStr;
	}

	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

}
