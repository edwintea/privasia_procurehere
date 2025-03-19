package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author pooja
 */
public class InvoiceSupplierPojo implements Serializable {

	private static final long serialVersionUID = 441351608029890795L;

	private String id;

	private String invoiceId;

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

	private InvoiceStatus status;

	private String description;

	private String buyerCompanyName;

	private String supplierCompanyName;

	private String invoiceIds;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date actionDate;

	private String actionBy;

	private String invoicenumber;

	private String invoicename;

	private String ponumber;

	private String supplier;

	private String businessunit;

	private String buyer;

	private String referencenumber;

	private String invoicecreatedby;

	private String invoicestatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date sendDate;

	private String createdby;

	private String sendDateStr;

	private String actionDateStr;

	private String createdDateStr;

	public InvoiceSupplierPojo() {
		super();
	}

	/**
	 * constructor Supplier Invoice List
	 */
	public InvoiceSupplierPojo(String id, String invoiceId, String name, String createdBy, Date createdDate, BigDecimal grandTotal, String decimal, String referenceNumber, String poNumber, String buyerCompanyName, InvoiceStatus status, String businessUnit, String currency, String actionBy, Date actionDate, Date sendDate) {
		this.id = id;
		this.invoiceId = invoiceId;
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
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.sendDate = sendDate;
	}

	/**
	 * constructor Buyer Invoice List
	 */
	public InvoiceSupplierPojo(String id, String invoiceId, String name, Date createdDate, BigDecimal grandTotal, String decimal, String poNumber, String supplierCompanyName, InvoiceStatus status, String businessUnit, String currency, String actionBy, Date actionDate, String referenceNumber, Date sendDate) {
		this.id = id;
		this.invoiceId = invoiceId;
		this.name = name;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.decimal = decimal;
		this.poNumber = poNumber;
		this.supplierCompanyName = supplierCompanyName;
		this.status = status;
		this.businessUnit = businessUnit;
		this.currency = currency;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.referenceNumber = referenceNumber;
		this.sendDate = sendDate;
	}

	public InvoiceSupplierPojo(String id, String invoiceId, String name, Date createdDate, BigDecimal grandTotal, String poNumber, InvoiceStatus status, String referenceNumber, String decimal, String createdBy, Date actionDate, Date sendDate) {
		this.id = id;
		this.invoiceId = invoiceId;
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

	// PH-1889 Buyer Csv
	public InvoiceSupplierPojo(String id, Date actionDate, String invoiceId, String name, Date createdDate, BigDecimal grandTotal, String decimal, Date sendDate, String poNumber, String supplierCompanyName, InvoiceStatus status, String businessUnit, String currency, String actionBy, String referenceNumber) {
		this.id = id;
		this.actionDate = actionDate;
		this.invoiceId = invoiceId;
		this.name = name;
		this.createdDate = createdDate;
		this.grandTotal = grandTotal;
		this.decimal = decimal;
		this.sendDate = sendDate;
		this.ponumber = poNumber;
		this.supplierCompanyName = supplierCompanyName;
		this.status = status;
		this.businessunit = businessUnit;
		this.currency = currency;
		this.actionBy = actionBy;
		this.referencenumber = referenceNumber;
	}

	// PH-1889 Supplier Csv
	public InvoiceSupplierPojo(String id, Date actionDate, Date invoiceSendDate, String invoiceId, String name, String createdBy, Date createdDate, String decimal, String referenceNumber, String poNumber, String buyerCompanyName, BigDecimal grandTotal, InvoiceStatus status, String businessUnit, String currency, String actionBy) {
		this.id = id;
		this.actionDate = actionDate;
		this.sendDate = invoiceSendDate;
		this.invoiceId = invoiceId;
		this.name = name;
		this.invoicecreatedby = createdBy;
		this.createdDate = createdDate;
		this.decimal = decimal;
		this.referencenumber = referenceNumber;
		this.ponumber = poNumber;
		this.buyerCompanyName = buyerCompanyName;
		this.grandTotal = grandTotal;
		this.status = status;
		this.businessunit = businessUnit;
		this.currency = currency;
		this.actionBy = actionBy;
	}

	/**
	 * @return the invoicestatus
	 */
	public String getInvoicestatus() {
		return invoicestatus;
	}

	/**
	 * @param invoicestatus the invoicestatus to set
	 */
	public void setInvoicestatus(String invoicestatus) {
		this.invoicestatus = invoicestatus;
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
	 * @return the invoicecreatedby
	 */
	public String getInvoicecreatedby() {
		return invoicecreatedby;
	}

	/**
	 * @param invoicecreatedby the invoicecreatedby to set
	 */
	public void setInvoicecreatedby(String invoicecreatedby) {
		this.invoicecreatedby = invoicecreatedby;
	}

	/**
	 * @return the invoicenumber
	 */
	public String getInvoicenumber() {
		return invoicenumber;
	}

	/**
	 * @param invoicenumber the invoicenumber to set
	 */
	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}

	/**
	 * @return the invoicename
	 */
	public String getInvoicename() {
		return invoicename;
	}

	/**
	 * @param invoicename the invoicename to set
	 */
	public void setInvoicename(String invoicename) {
		this.invoicename = invoicename;
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
	 * @return the invoiceIds
	 */
	public String getInvoiceIds() {
		return invoiceIds;
	}

	/**
	 * @param invoiceIds the invoiceIds to set
	 */
	public void setInvoiceIds(String invoiceIds) {
		this.invoiceIds = invoiceIds;
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
	 * @return the invoiceId
	 */
	public String getInvoiceId() {
		return invoiceId;
	}

	/**
	 * @param invoiceId the invoiceId to set
	 */
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
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
	public InvoiceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(InvoiceStatus status) {
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

	@Override
	public String toString() {
		return "InvoiceSupplierPojo [id=" + id + ", invoiceId=" + invoiceId + ", referenceNumber=" + referenceNumber + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", name=" + name + ", createdBy=" + createdBy + ", currency=" + currency + ", decimal=" + decimal + ", businessUnit=" + businessUnit + ", grandTotal=" + grandTotal + ", poNumber=" + poNumber + ", status=" + status + ", description=" + description + ", buyerCompanyName=" + buyerCompanyName + ", supplierCompanyName=" + supplierCompanyName + ", invoiceIds=" + invoiceIds + ", actionDate=" + actionDate + ", actionBy=" + actionBy + ", invoicenumber=" + invoicenumber + ", invoicename=" + invoicename + ", ponumber=" + ponumber + ", supplier=" + supplier + ", businessunit=" + businessunit + ", buyer=" + buyer + ", referencenumber=" + referencenumber + ", invoicecreatedby=" + invoicecreatedby + "]";
	}

}
