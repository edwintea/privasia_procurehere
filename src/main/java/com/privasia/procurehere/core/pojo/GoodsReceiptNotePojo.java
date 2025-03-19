package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.GrnStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author ravi
 */
public class GoodsReceiptNotePojo implements Serializable {

	private static final long serialVersionUID = -8243392137748966863L;

	private String id;

	private String grnId;

	private String grnTitle;

	private String referenceNumber;

	private String currency;

	private Integer decimal;

	private String businessUnit;

	private String poNumber;

	private String description;

	private String buyerCompanyName;

	private String supplierCompanyName;

	private String buyer;

	private String supplier;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	private String createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date actionDate;

	private String actionBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date sendDate;

	private BigDecimal grandTotal;

	private GrnStatus status;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date acceptRejectDate;

	private String grnIds;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date goodsReceiptDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date grnReceivedDate;

	private String createDate;

	private String receiptDate;

	private String grnDate;

	public GoodsReceiptNotePojo() {
		super();
	}

	public GoodsReceiptNotePojo(String id, String grnId, String grnTitle, String currency, Integer decimal, String businessUnit, String supplierCompanyName, String createdBy, Date createdDate, BigDecimal grandTotal, GrnStatus status, Date acceptRejectDate) {
		super();
		this.id = id;
		this.grnId = grnId;
		this.grnTitle = grnTitle;
		this.currency = currency;
		this.decimal = decimal;
		this.businessUnit = businessUnit;
		this.supplierCompanyName = supplierCompanyName;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.grandTotal = grandTotal;
		this.status = status;
		this.acceptRejectDate = acceptRejectDate;
	}

	public GoodsReceiptNotePojo(String id, String grnId, String grnTitle, Date createdDate, String currency, Integer decimal, String businessUnit, String buyerCompanyName, BigDecimal grandTotal, GrnStatus status, Date acceptRejectDate) {
		super();
		this.id = id;
		this.grnId = grnId;
		this.grnTitle = grnTitle;
		this.createdDate = createdDate;
		this.currency = currency;
		this.decimal = decimal;
		this.businessUnit = businessUnit;
		this.buyerCompanyName = buyerCompanyName;
		this.grandTotal = grandTotal;
		this.status = status;
		this.acceptRejectDate = acceptRejectDate;
	}

	// GRN List data
	public GoodsReceiptNotePojo(String id, String grnId, String grnTitle, String currency, Integer decimal, String businessUnit, String supplierCompanyName, String supplierName, String createdBy, Date createdDate, BigDecimal grandTotal, GrnStatus status, String referenceNumber, Date grnReceivedDate, Date goodsReceiptDate, String poNumber) {
		super();
		this.id = id;
		this.grnId = grnId;
		this.grnTitle = grnTitle;
		this.currency = currency;
		this.decimal = decimal;
		this.businessUnit = businessUnit;
		this.supplierCompanyName = supplierCompanyName != null ? supplierCompanyName : supplierName;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.grandTotal = grandTotal;
		this.status = status;
		this.referenceNumber = referenceNumber;
		this.grnReceivedDate = grnReceivedDate;
		this.goodsReceiptDate = goodsReceiptDate;
		this.poNumber = poNumber;
	}

	public GoodsReceiptNotePojo(String id, String grnId, String grnTitle, String referenceNumber, Integer decimal, String createdBy, BigDecimal grandTotal, GrnStatus status, Date goodsReceiptDate, Date grnReceivedDate, Date createdDate) {
		super();
		this.id = id;
		this.grnId = grnId;
		this.grnTitle = grnTitle;
		this.referenceNumber = referenceNumber;
		this.decimal = decimal;
		this.createdBy = createdBy;
		this.grandTotal = grandTotal;
		this.status = status;
		this.goodsReceiptDate = goodsReceiptDate;
		this.grnReceivedDate = grnReceivedDate;
		this.createdDate = createdDate;
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
	 * @return the status
	 */
	public GrnStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(GrnStatus status) {
		this.status = status;
	}

	/**
	 * @return the acceptRejectDate
	 */
	public Date getAcceptRejectDate() {
		return acceptRejectDate;
	}

	/**
	 * @param acceptRejectDate the acceptRejectDate to set
	 */
	public void setAcceptRejectDate(Date acceptRejectDate) {
		this.acceptRejectDate = acceptRejectDate;
	}

	/**
	 * @return the grnIds
	 */
	public String getGrnIds() {
		return grnIds;
	}

	/**
	 * @param grnIds the grnIds to set
	 */
	public void setGrnIds(String grnIds) {
		this.grnIds = grnIds;
	}

	public Date getGoodsReceiptDate() {
		return goodsReceiptDate;
	}

	public void setGoodsReceiptDate(Date goodsReceiptDate) {
		this.goodsReceiptDate = goodsReceiptDate;
	}

	public Date getGrnReceivedDate() {
		return grnReceivedDate;
	}

	public void setGrnReceivedDate(Date grnReceivedDate) {
		this.grnReceivedDate = grnReceivedDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getGrnDate() {
		return grnDate;
	}

	public void setGrnDate(String grnDate) {
		this.grnDate = grnDate;
	}

}
