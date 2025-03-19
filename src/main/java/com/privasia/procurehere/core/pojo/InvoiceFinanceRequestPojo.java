package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author nitin
 */
public class InvoiceFinanceRequestPojo implements Serializable {

	private static final long serialVersionUID = 441351608029890795L;

	private String id;

	private String invoiceId;

	private String referenceNumber;

	private String name;

	private String invoiceNumber;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date requestedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date invoiceSendDate;

	private String requestedBy;

	private String decimal;

	private String currency;

	private String businessUnit;

	private BigDecimal invoiceAmount;

	private String poNumber;

	private FinanceRequestStatus status;

	private String description;

	private String buyerCompanyName;

	private String supplierCompanyName;

	private String actionBy;

	public InvoiceFinanceRequestPojo() {
		super();
	}

	public InvoiceFinanceRequestPojo(String id, String invoiceId, String invoiceNumber, String invoiceTitle, Date requestedDate, BigDecimal invoiceAmount, String decimal, String poNumber, String supplierCompanyName,String buyerCompanyName, FinanceRequestStatus requestStatus, String unitName, String currencyCode, String requestedBy, String invoiceReferenceNumber, Date invoiceSendDate) {
		this.id = id;
		this.invoiceId = invoiceId;
		this.invoiceNumber = invoiceNumber;
		this.name = invoiceTitle;
		this.requestedDate = requestedDate;
		this.invoiceAmount = invoiceAmount;
		this.decimal = decimal;
		this.poNumber = poNumber;
		this.supplierCompanyName = supplierCompanyName;
		this.buyerCompanyName=buyerCompanyName;
		this.status = requestStatus;
		this.businessUnit = unitName;
		this.currency = currencyCode;
		this.requestedBy = requestedBy;
		this.referenceNumber = invoiceReferenceNumber;
		this.invoiceSendDate = invoiceSendDate;
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
	 * @return the invoiceNumber
	 */
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	/**
	 * @param invoiceNumber the invoiceNumber to set
	 */
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
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
	 * @return the requestedDate
	 */
	public Date getRequestedDate() {
		return requestedDate;
	}

	/**
	 * @param requestedDate the requestedDate to set
	 */
	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * @return the invoiceSendDate
	 */
	public Date getInvoiceSendDate() {
		return invoiceSendDate;
	}

	/**
	 * @param invoiceSendDate the invoiceSendDate to set
	 */
	public void setInvoiceSendDate(Date invoiceSendDate) {
		this.invoiceSendDate = invoiceSendDate;
	}

	/**
	 * @return the requestedBy
	 */
	public String getRequestedBy() {
		return requestedBy;
	}

	/**
	 * @param requestedBy the requestedBy to set
	 */
	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
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
	 * @return the invoiceAmount
	 */
	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the status
	 */
	public FinanceRequestStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(FinanceRequestStatus status) {
		this.status = status;
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

}
