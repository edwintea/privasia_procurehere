package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Pr.PrPurchaseItem;
import com.privasia.procurehere.core.enums.GrnStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author ravi
 */
@Entity
@Table(name = "PROC_GRN")
public class GoodsReceiptNote implements Serializable {

	private static final long serialVersionUID = 3988453333266334592L;

	public interface GoodsReceiptNoteItemsInterface {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@Column(name = "GRN_NUMBER", length = 64)
	private String grnId;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PO_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_PO_ID"))
	private Po po;

	@Column(name = "GRN_NAME", length = 200)
	@Size(min = 0, max = 200, message = "{grn.title.length}")
	private String grnTitle;

	@Column(name = "REFERENCE_NUMBER", length = 64)
	@Size(max = 64, message = "{grn.referenceNumber.length}")
	private String referenceNumber;

	@Size(max = 1000, message = "{grn.description.length}")
	@Column(name = "DESCRIPTION", length = 1050)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_BUYER_ID"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENCY_ID", foreignKey = @ForeignKey(name = "FK_GRN_BUYER_CURRENCY"))
	private Currency currency;

	@NotNull(message = "{grn.decimal.required}")
	@Column(name = "GRN_DECIMAL", length = 2)
	private Integer decimal;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_COSTCENTER_ID"))
	private CostCenter costCenter;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_BUSS_UNIT_ID"))
	private BusinessUnit businessUnit;

	@Column(name = "LINE1", length = 64)
	private String line1;

	@Column(name = "LINE2", length = 64)
	private String line2;

	@Column(name = "LINE3", length = 64)
	private String line3;

	@Column(name = "LINE4", length = 64)
	private String line4;

	@Column(name = "LINE5", length = 64)
	private String line5;

	@Column(name = "LINE6", length = 64)
	private String line6;

	@Column(name = "LINE7", length = 64)
	private String line7;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_SUP_ID"))
	private Supplier supplier;

	@Size(max = 128, message = "{grn.supplier.name.length}")
	@Column(name = "SUPPLIER_NAME", length = 128)
	private String supplierName;

	@Size(max = 300, message = "{grn.supplier.address.length}")
	@Column(name = "SUPPLIER_ADDRESS", length = 300)
	private String supplierAddress;

	@Size(min = 6, max = 50, message = "{grn.supplier.telnumber.length}")
	@Column(name = "SUPPLIER_TEL_NO", length = 55)
	private String supplierTelNumber;

	@Size(max = 16, message = "{grn.supplier.faxnumber.length}")
	@Column(name = "SUPPLIER_FAX_NO", length = 16)
	private String supplierFaxNumber;

	@Size(max = 32)
	@Column(name = "SUPPLIER_TAX_NO", length = 32)
	private String supplierTaxNumber;

	@Size(max = 1050)
	@Column(name = "REMARKS", length = 1050)
	private String remarks;

	@Size(max = 900)
	@Column(name = "TERMS_AND_COND", length = 1000)
	private String termsAndConditions;

	@Column(name = "FIELD1_LABEL", nullable = true, length = 32)
	private String field1Label;

	@Column(name = "FIELD2_LABEL", nullable = true, length = 32)
	private String field2Label;

	@Column(name = "FIELD3_LABEL", nullable = true, length = 32)
	private String field3Label;

	@Column(name = "FIELD4_LABEL", nullable = true, length = 32)
	private String field4Label;

	@Column(name = "FIELD5_LABEL", nullable = true, length = 32)
	private String field5Label;

	@Column(name = "FIELD6_LABEL", nullable = true, length = 32)
	private String field6Label;

	@Column(name = "FIELD7_LABEL", nullable = true, length = 32)
	private String field7Label;

	@Column(name = "FIELD8_LABEL", nullable = true, length = 32)
	private String field8Label;

	@Column(name = "FIELD9_LABEL", nullable = true, length = 32)
	private String field9Label;

	@Column(name = "FIELD10_LABEL", nullable = true, length = 32)
	private String field10Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", length = 10)
	private GrnStatus status;

	@Column(name = "TOTAL", precision = 22, scale = 6)
	private BigDecimal total = new BigDecimal(0);

	@Column(name = "TAX_DESCRIPTION", length = 150)
	private String taxDescription;

	@Column(name = "ADDITIONAL_TAX", precision = 22, scale = 6)
	private BigDecimal additionalTax = new BigDecimal(0);

	@Digits(integer = 16, fraction = 8, message = "{grandtotal.length.error}", groups = { PrPurchaseItem.class })
	@Column(name = "GRAND_TOTAL", precision = 22, scale = 6)
	private BigDecimal grandTotal = new BigDecimal(0);

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_CREATED_BY_ID"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_MODIFIED_BY_ID"))
	private User modifiedBy;

	@Column(name = "DELIVERY_ADDRESS_TITLE", length = 128)
	private String deliveryAddressTitle;

	@Column(name = "DELIVERY_ADDRESS_LINE1", length = 250)
	private String deliveryAddressLine1;

	@Column(name = "DELIVERY_ADDRESS_LINE2", length = 250)
	private String deliveryAddressLine2;

	@Column(name = "DELIVERY_ADDRESS_CITY", length = 250)
	private String deliveryAddressCity;

	@Column(name = "DELIVERY_ADDRESS_STATE", length = 150)
	private String deliveryAddressState;

	@Column(name = "DELIVERY_ADDRESS_ZIP", length = 32)
	private String deliveryAddressZip;

	@Column(name = "DELIVERY_ADDRESS_COUNTRY", length = 128)
	private String deliveryAddressCountry;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "goodsReceiptNote", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OrderBy("level, order")
	private List<GoodsReceiptNoteItem> goodsReceiptNoteItems;

	// Accept or Decline by
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "RECEIVED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_GRN_REC_BY_ID"))
	private User receivedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GOODS_RECEIPT_DATE")
	private Date goodsReceiptDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RECEIVED_DATE")
	private Date grnReceivedDate;

	@Size(max = 150, message = "{invoice.deliveryReceiver.length}")
	@Column(name = "DELIVERY_RECEIVER", length = 150)
	private String deliveryReceiver;

	@Transient
	private Date goodsReceiptTime;

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
	 * @return the po
	 */
	public Po getPo() {
		return po;
	}

	/**
	 * @param po the po to set
	 */
	public void setPo(Po po) {
		this.po = po;
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
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
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
	public CostCenter getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
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
	 * @return the line1
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @return the line2
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @param line2 the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @return the line3
	 */
	public String getLine3() {
		return line3;
	}

	/**
	 * @param line3 the line3 to set
	 */
	public void setLine3(String line3) {
		this.line3 = line3;
	}

	/**
	 * @return the line4
	 */
	public String getLine4() {
		return line4;
	}

	/**
	 * @param line4 the line4 to set
	 */
	public void setLine4(String line4) {
		this.line4 = line4;
	}

	/**
	 * @return the line5
	 */
	public String getLine5() {
		return line5;
	}

	/**
	 * @param line5 the line5 to set
	 */
	public void setLine5(String line5) {
		this.line5 = line5;
	}

	/**
	 * @return the line6
	 */
	public String getLine6() {
		return line6;
	}

	/**
	 * @param line6 the line6 to set
	 */
	public void setLine6(String line6) {
		this.line6 = line6;
	}

	/**
	 * @return the line7
	 */
	public String getLine7() {
		return line7;
	}

	/**
	 * @param line7 the line7 to set
	 */
	public void setLine7(String line7) {
		this.line7 = line7;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
	 * @return the field1Label
	 */
	public String getField1Label() {
		return field1Label;
	}

	/**
	 * @param field1Label the field1Label to set
	 */
	public void setField1Label(String field1Label) {
		this.field1Label = field1Label;
	}

	/**
	 * @return the field2Label
	 */
	public String getField2Label() {
		return field2Label;
	}

	/**
	 * @param field2Label the field2Label to set
	 */
	public void setField2Label(String field2Label) {
		this.field2Label = field2Label;
	}

	/**
	 * @return the field3Label
	 */
	public String getField3Label() {
		return field3Label;
	}

	/**
	 * @param field3Label the field3Label to set
	 */
	public void setField3Label(String field3Label) {
		this.field3Label = field3Label;
	}

	/**
	 * @return the field4Label
	 */
	public String getField4Label() {
		return field4Label;
	}

	/**
	 * @param field4Label the field4Label to set
	 */
	public void setField4Label(String field4Label) {
		this.field4Label = field4Label;
	}

	/**
	 * @return the field5Label
	 */
	public String getField5Label() {
		return field5Label;
	}

	/**
	 * @param field5Label the field5Label to set
	 */
	public void setField5Label(String field5Label) {
		this.field5Label = field5Label;
	}

	/**
	 * @return the field6Label
	 */
	public String getField6Label() {
		return field6Label;
	}

	/**
	 * @param field6Label the field6Label to set
	 */
	public void setField6Label(String field6Label) {
		this.field6Label = field6Label;
	}

	/**
	 * @return the field7Label
	 */
	public String getField7Label() {
		return field7Label;
	}

	/**
	 * @param field7Label the field7Label to set
	 */
	public void setField7Label(String field7Label) {
		this.field7Label = field7Label;
	}

	/**
	 * @return the field8Label
	 */
	public String getField8Label() {
		return field8Label;
	}

	/**
	 * @param field8Label the field8Label to set
	 */
	public void setField8Label(String field8Label) {
		this.field8Label = field8Label;
	}

	/**
	 * @return the field9Label
	 */
	public String getField9Label() {
		return field9Label;
	}

	/**
	 * @param field9Label the field9Label to set
	 */
	public void setField9Label(String field9Label) {
		this.field9Label = field9Label;
	}

	/**
	 * @return the field10Label
	 */
	public String getField10Label() {
		return field10Label;
	}

	/**
	 * @param field10Label the field10Label to set
	 */
	public void setField10Label(String field10Label) {
		this.field10Label = field10Label;
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
	 * @return the total
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	/**
	 * @return the taxDescription
	 */
	public String getTaxDescription() {
		return taxDescription;
	}

	/**
	 * @param taxDescription the taxDescription to set
	 */
	public void setTaxDescription(String taxDescription) {
		this.taxDescription = taxDescription;
	}

	/**
	 * @return the additionalTax
	 */
	public BigDecimal getAdditionalTax() {
		return additionalTax;
	}

	/**
	 * @param additionalTax the additionalTax to set
	 */
	public void setAdditionalTax(BigDecimal additionalTax) {
		this.additionalTax = additionalTax;
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
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
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
	 * @return the goodsReceiptNoteItems
	 */
	public List<GoodsReceiptNoteItem> getGoodsReceiptNoteItems() {
		return goodsReceiptNoteItems;
	}

	/**
	 * @param goodsReceiptNoteItems the goodsReceiptNoteItems to set
	 */
	public void setGoodsReceiptNoteItems(List<GoodsReceiptNoteItem> goodsReceiptNoteItems) {
		if (this.goodsReceiptNoteItems == null) {
			this.goodsReceiptNoteItems = new ArrayList<GoodsReceiptNoteItem>();
		} else {
			// this.goodsReceiptNoteItems.clear();
		}
		if (goodsReceiptNoteItems != null) {
			this.goodsReceiptNoteItems.addAll(goodsReceiptNoteItems);
		}
	}

	public User getReceivedBy() {
		return receivedBy;
	}

	public void setReceivedBy(User receivedBy) {
		this.receivedBy = receivedBy;
	}

	public Date getGrnReceivedDate() {
		return grnReceivedDate;
	}

	public void setGrnReceivedDate(Date grnReceivedDate) {
		this.grnReceivedDate = grnReceivedDate;
	}

	public Date getGoodsReceiptDate() {
		return goodsReceiptDate;
	}

	public void setGoodsReceiptDate(Date goodsReceiptDate) {
		this.goodsReceiptDate = goodsReceiptDate;
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
	 * @return the goodsReceiptTime
	 */
	public Date getGoodsReceiptTime() {
		return goodsReceiptTime;
	}

	/**
	 * @param goodsReceiptTime the goodsReceiptTime to set
	 */
	public void setGoodsReceiptTime(Date goodsReceiptTime) {
		this.goodsReceiptTime = goodsReceiptTime;
	}

}
