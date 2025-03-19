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
public class PoFinanceRequestPojo implements Serializable {

	private static final long serialVersionUID = 441351608029890795L;

	private String id;

	private String poId;

	private String referenceNumber;

	private String name;

	private String poNumber;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date requestedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date acceptedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date declinedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date approvedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date poDate;

	private String requestedBy;

	private String acceptedBy;

	private String declinedBy;

	private String approvedBy;

	private String decimal;

	private String currency;

	private String businessUnit;

	private BigDecimal invoiceAmount;

	private FinanceRequestStatus status;

	private String description;

	private String buyerCompanyName;

	private String supplierCompanyName;

	private String actionBy;

	public PoFinanceRequestPojo() {
		super();
	}

	public PoFinanceRequestPojo(String id, String poId, String poNumber, String invoiceTitle, Date requestedDate, BigDecimal invoiceAmount, String decimal, String supplierCompanyName, String buyerCompanyName, FinanceRequestStatus requestStatus, String unitName, String currencyCode, String requestedBy, String invoiceReferenceNumber, Date poDate) {
		this.id = id;
		this.poId = poId;
		this.poNumber = poNumber;
		this.name = invoiceTitle;
		this.requestedDate = requestedDate;
		this.invoiceAmount = invoiceAmount;
		this.decimal = decimal;
		this.poNumber = poNumber;
		this.supplierCompanyName = supplierCompanyName;
		this.buyerCompanyName = buyerCompanyName;
		this.status = requestStatus;
		this.businessUnit = unitName;
		this.currency = currencyCode;
		this.requestedBy = requestedBy;
		this.referenceNumber = invoiceReferenceNumber;
		this.poDate = poDate;
	}

	public PoFinanceRequestPojo(String id, String poId, String poNumber, String invoiceTitle, Date requestedDate, Date acceptedDate, Date declinedDate, Date approvedDate, BigDecimal invoiceAmount, String decimal, String supplierCompanyName, String buyerCompanyName, FinanceRequestStatus requestStatus, String currencyCode, String requestedBy, String acceptedBy, String declinedBy, String approvedBy, Date poDate) {
		this.id = id;
		this.poId = poId;
		this.poNumber = poNumber;
		this.name = invoiceTitle;
		this.requestedDate = requestedDate;
		this.acceptedDate = acceptedDate;
		this.declinedDate = declinedDate;
		this.approvedDate = approvedDate;
		this.invoiceAmount = invoiceAmount;
		this.decimal = decimal;
		this.poNumber = poNumber;
		this.supplierCompanyName = supplierCompanyName;
		this.buyerCompanyName = buyerCompanyName;
		this.status = requestStatus;
		this.currency = currencyCode;
		this.requestedBy = requestedBy;
		this.acceptedBy = acceptedBy;
		this.declinedBy = declinedBy;
		this.approvedBy = approvedBy;
		this.poDate = poDate;
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
	 * @return the poDate
	 */
	public Date getPoDate() {
		return poDate;
	}

	/**
	 * @param poDate the poDate to set
	 */
	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}

	/**
	 * @return the acceptedDate
	 */
	public Date getAcceptedDate() {
		return acceptedDate;
	}

	/**
	 * @param acceptedDate the acceptedDate to set
	 */
	public void setAcceptedDate(Date acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

	/**
	 * @return the acceptedBy
	 */
	public String getAcceptedBy() {
		return acceptedBy;
	}

	/**
	 * @param acceptedBy the acceptedBy to set
	 */
	public void setAcceptedBy(String acceptedBy) {
		this.acceptedBy = acceptedBy;
	}

	/**
	 * @return the approvedDate
	 */
	public Date getApprovedDate() {
		return approvedDate;
	}

	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	/**
	 * @return the approvedBy
	 */
	public String getApprovedBy() {
		return approvedBy;
	}

	/**
	 * @param approvedBy the approvedBy to set
	 */
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	/**
	 * @return the declinedDate
	 */
	public Date getDeclinedDate() {
		return declinedDate;
	}

	/**
	 * @param declinedDate the declinedDate to set
	 */
	public void setDeclinedDate(Date declinedDate) {
		this.declinedDate = declinedDate;
	}

	/**
	 * @return the declinedBy
	 */
	public String getDeclinedBy() {
		return declinedBy;
	}

	/**
	 * @param declinedBy the declinedBy to set
	 */
	public void setDeclinedBy(String declinedBy) {
		this.declinedBy = declinedBy;
	}

}
