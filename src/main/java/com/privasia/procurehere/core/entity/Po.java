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
import javax.persistence.Lob;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Pr.PrPurchaseItem;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;
import org.hibernate.annotations.Type;

/**
 * @author Nitin Otageri
 */
@Entity
@Table(name = "PROC_PO")
public class Po implements Serializable {

	private static final long serialVersionUID = -1862913013832881187L;

	public interface PoCreate {
	}

	public interface PoDelivery {
	}

	public interface PoPurchaseItem {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@Column(name = "PO_ID", length = 64)
	private String poId;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PR_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_PR_ID"))
	private Pr pr;

//	@NotNull(message = "{pr.name.required}")
	@Column(name = "PO_NAME", length = 128)
	@Size(min = 1, max = 128, message = "{pr.name.length}")
	private String name;

	@Column(name = "REFERENCE_NUMBER", length = 64)
	@Size(min = 1, max = 64, message = "{pr.referenceNumber.length}")
	private String referenceNumber;

	@Size(max = 1000, message = "{pr.description.length}")
	@Column(name = "DESCRIPTION", length = 1050)
	private String description;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_BUYER_ID"))
	private Buyer buyer;

//	@NotNull(message = "{pr.requester.required}")
	@Size(min = 0, max = 550, message = "{pr.requester.length}")
	@Column(name = "REQUESTER", length = 550)
	private String requester;

	// @NotNull(message = "{pr.correspondenceAddress.required}")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CORRESPONDENCE_ADDRESS_ID", nullable = true)
	private BuyerAddress correspondenceAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull(message = "{pr.currency.required}")
	@JoinColumn(name = "BASE_CURRENCY_ID", foreignKey = @ForeignKey(name = "FK_PO_BUYER_CURRENCY"))
	private Currency currency;

	@NotNull(message = "{pr.decimal.required}")
	@Column(name = "PO_DECIMAL", length = 64)
	private String decimal;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_COSTCENTER_ID"))
	private CostCenter costCenter;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_BUSS_UNIT_ID"))
	private BusinessUnit businessUnit;

	@NotNull(message = "{pr.paymentTerm.required}")
	@Size(min = 1, max = 500, message = "{pr.paymentTerm.length}")
	@Column(name = "PAYMENT_TERM", length = 550)
	private String paymentTerm;

	// @NotNull(message = "{pr.supplier.required}")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PO_SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_FAV_SUP_ID"))
	private FavouriteSupplier supplier;

	// @NotEmpty(message = "{pr.supplier.name.required}")
	@Size(max = 128, message = "{pr.supplier.name.length}")
	@Column(name = "SUPPLIER_NAME", length = 128)
	private String supplierName;

	// @NotEmpty(message = "{pr.supplier.address.required}")
	@Size(max = 300, message = "{pr.supplier.address.length}")
	@Column(name = "SUPPLIER_ADDRESS", length = 300)
	private String supplierAddress;

	// @NotEmpty(message = "{pr.supplier.telnumber.required}")
	@Size(min = 6, max = 50, message = "{pr.supplier.telnumber.length}")
	@Column(name = "SUPPLIER_TEL_NO", length = 55)
	private String supplierTelNumber;

	@Size(max = 16, message = "{pr.supplier.faxnumber.length}")
	@Column(name = "SUPPLIER_FAX_NO", length = 16)
	private String supplierFaxNumber;

	@Size(max = 32, message = "{pr.supplier.taxnumber.length}")
	@Column(name = "SUPPLIER_TAX_NO", length = 32)
	private String supplierTaxNumber;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DELIVERY_DATE")
	private Date deliveryDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "DELIVERY_ADDRESS_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_DEL_ADD_ID"))
	private BuyerAddress deliveryAddress;

	// @NotEmpty(message = "{pr.deliveryReceiver.required}")
	@Size(max = 150, message = "{pr.deliveryReceiver.length}")
	@Column(name = "DELIVERY_RECEIVER", length = 150)
	private String deliveryReceiver;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "po", cascade = { CascadeType.ALL }, orphanRemoval = true)
	@OrderBy("level, order")
	private List<PoItem> poItems;

	@Size(max = 1050, message = "{pr.remarks.length}")
	@Column(name = "REMARKS", length = 1050)
	private String remarks;

	@Size(max = 900, message = "{pr.termsAndConditions.length}")
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
	@Column(name = "STATUS")
	private PoStatus status;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_ACTION_BY_ID"))
	private User actionBy;

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
	@Column(name = "PO_CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_CREATED_BY_ID"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PO_MODIFIED_DATE")
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_MODIFIED_BY_ID"))
	private User modifiedBy;

	@Column(name = "PO_NUMBER", length = 64)
	private String poNumber;

	@Column(name = "CANCEL_REASON", length = 1050)
	private String cancelReason;

	@Column(name = "URGENT_PO")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean urgentPo;

	@Column(name = "IS_PO_REPORT_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isPoReportSent;

	@Column(name = "ERP_DOC_NO", length = 64)
	private String erpDocNo;

	@Column(name = "IS_ERP_TRANSFER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean erpPrTransferred = Boolean.FALSE;

	@Column(name = "SUPPLIER_REMARK", length = 500)
	private String supplierRemark;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PO_ORDERED_DATE", nullable = true)
	private Date orderedDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ORDERED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_ORDERED_BY_ID"))
	private User orderedBy;

	@Column(name = "CORRESPOND_ADDRESS_TITLE", length = 128)
	private String correspondAddressTitle;

	@Column(name = "CORRESPOND_ADDRESS_LINE1", length = 250)
	private String correspondAddressLine1;

	@Column(name = "CORRESPOND_ADDRESS_LINE2", length = 250)
	private String correspondAddressLine2;

	@Column(name = "CORRESPOND_ADDRESS_CITY", length = 250)
	private String correspondAddressCity;

	@Column(name = "CORRESPOND_ADDRESS_STATE", length = 150)
	private String correspondAddressState;

	@Column(name = "CORRESPOND_ADDRESS_ZIP", length = 32)
	private String correspondAddressZip;

	@Column(name = "CORRESPOND_ADDRESS_COUNTRY", length = 128)
	private String correspondAddressCountry;

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

	@Column(name = "DO_COUNT")
	private Integer doCount;

	@Column(name = "DO_CANCEL_COUNT")
	private Integer doCancelCount;

	@Column(name = "INVOICE_COUNT")
	private Integer invoiceCount;

	@Column(name = "INVOICE_CANCEL_COUNT")
	private Integer invoiceCancelCount;

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

	@Column(name = "GRN_COUNT")
	private Integer grnCount;

	@Column(name = "GRN_CANCEL_COUNT")
	private Integer grnCancelCount;

	@Transient
	private Integer grnReceivedOrDraftCount;

	@Column(name = "PAYMENT_TERM_DAYS", length = 3, nullable = true)
	private Integer paymentTermDays;

	@Column(name = "FINANCE_REQUEST", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean requestForFinance = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PAYMENT_TERMES", nullable = true, foreignKey = @ForeignKey(name = "FK_PO_PAYMENT_TERMS"))
	private PaymentTermes paymentTermes;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "po", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<PoApproval> approvals;

	public String getPrId() {
		return prId;
	}

	public void setPrId(String prId) {
		this.prId = prId;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public List<PoTeamMember> getPoTeamMembers() {
		return poTeamMembers;
	}

	public void setPoTeamMembers(List<PoTeamMember> poTeamMembers) {
		this.poTeamMembers = poTeamMembers;
	}

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "po", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<PoTeamMember> poTeamMembers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "po", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<PoComment> comments;

	@Column(name = "IS_ENABLE_APPROVAL_ROUTE", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean enableApprovalRoute = Boolean.FALSE;

	@Column(name = "IS_ENABLE_APPROVAL_REMINDER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean enableApprovalReminder = Boolean.FALSE;

	@Column(name = "REMINDER_HOURS", length = 2, nullable = true)
	private Integer reminderAfterHour;

	@Column(name = "REMINDER_COUNT", length = 2, nullable = true)
	private Integer reminderCount;

	@Column(name = "IS_NOTIFY_EVENT_OWNER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean notifyEventOwner = Boolean.FALSE;

	@Column(name = "IS_REVISED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean revised = Boolean.FALSE;

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Column(name = "IS_CANCELLED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean cancelled = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "po", cascade = { CascadeType.ALL })
	private List<PurchaseOrderDocument> purchaseOrderDocuments;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "REVISE_PO_DETAILS")
	private String revisePoDetails;

	@Column(name = "REVISE_JUSTIFICATION", length = 550)
	private String reviseJustification;

	@Enumerated(EnumType.STRING)
	@Column(name = "OLD_STATUS")
	private PoStatus oldStatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PO_REVISED_DATE", nullable = true)
	private Date poRevisedDate;

	@Column(name = "IS_PO_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean poDetailCompleted = Boolean.FALSE;

	@Column(name = "IS_DOCUMENT_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean documentCompleted = Boolean.FALSE;

	@Column(name = "IS_SUPPLIER_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean supplierCompleted = Boolean.FALSE;

	@Column(name = "IS_DELIVERY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean deliveryCompleted = Boolean.FALSE;

	@Column(name = "IS_PO_ITEM_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean poItemCompleted = Boolean.FALSE;

	@Column(name = "IS_REMARK_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean remarkCompleted = Boolean.FALSE;

	@Column(name = "IS_FROM_INTEGRATION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean fromIntegration = Boolean.FALSE;

	@Column(name = "IS_SENT_TO_SAP")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean sentToSap = Boolean.FALSE;

	// this column is to identify passed but failed to send data, by default all are true
	// For the scheduler purpose we created this column
	@Column(name = "IS_SENT_TO_SAP_FAILED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean sentToSapFailed = Boolean.FALSE;

	public Boolean getPoReportSent() {
		return isPoReportSent;
	}

	public void setPoReportSent(Boolean poReportSent) {
		isPoReportSent = poReportSent;
	}

	public Boolean getPoDetailCompleted() {
		return poDetailCompleted;
	}

	public void setPoDetailCompleted(Boolean poDetailCompleted) {
		this.poDetailCompleted = poDetailCompleted;
	}

	public Boolean getDocumentCompleted() {
		return documentCompleted;
	}

	public void setDocumentCompleted(Boolean documentCompleted) {
		this.documentCompleted = documentCompleted;
	}

	public Boolean getSupplierCompleted() {
		return supplierCompleted;
	}

	public void setSupplierCompleted(Boolean supplierCompleted) {
		this.supplierCompleted = supplierCompleted;
	}

	public Boolean getDeliveryCompleted() {
		return deliveryCompleted;
	}

	public void setDeliveryCompleted(Boolean deliveryCompleted) {
		this.deliveryCompleted = deliveryCompleted;
	}

	public Boolean getPoItemCompleted() {
		return poItemCompleted;
	}

	public void setPoItemCompleted(Boolean poItemCompleted) {
		this.poItemCompleted = poItemCompleted;
	}

	public Boolean getRemarkCompleted() {
		return remarkCompleted;
	}

	public void setRemarkCompleted(Boolean remarkCompleted) {
		this.remarkCompleted = remarkCompleted;
	}

	public Boolean getSummaryCompleted() {
		return summaryCompleted;
	}

	public void setSummaryCompleted(Boolean summaryCompleted) {
		this.summaryCompleted = summaryCompleted;
	}

	@Column(name = "IS_SUMMARY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean summaryCompleted = Boolean.FALSE;

	@Transient
	private Date deliveryTime;
	@Transient
	private String orderBy;
	@Transient
	private String prId;

	public Po() {
	}

	public Po(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, PoStatus status, Boolean isPoReportSent, User orderedBy, Date orderedDate, String currencyCode, Date actioDate) {
		this.id = id;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.status = status;
		this.isPoReportSent = isPoReportSent;
		if (orderedBy != null) {
			orderedBy = orderedBy.createStripCopy();
			orderedBy.getName();
		}
		this.orderedBy = orderedBy;
		this.orderedDate = orderedDate;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.actionDate = actioDate;
	}

	public Po(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, //
			Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, //
			String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, //
			PoStatus status, Boolean isPoReportSent, User orderedBy, Date orderedDate, String currencyCode, Date actioDate, Pr pr) {
		this.id = id;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.status = status;
		this.isPoReportSent = isPoReportSent;
		if (orderedBy != null) {
			orderedBy = orderedBy.createStripCopy();
			orderedBy.getName();
		}
		this.orderedBy = orderedBy;
		this.orderedDate = orderedDate;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.actionDate = actioDate;
		if (pr != null) {
			pr = pr.createStripCopy();
			pr.getPrId();
		}
		this.pr = pr;
	}

	public Po(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, //
			Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, //
			String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, //
			PoStatus status, User orderedBy, Date orderedDate, String currencyCode, Date actioDate, Pr pr) {
		this.id = id;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.status = status;
		if (orderedBy != null) {
			orderedBy = orderedBy.createStripCopy();
			orderedBy.getName();
		}
		this.orderedBy = orderedBy;
		this.orderedDate = orderedDate;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.actionDate = actioDate;
		if (pr != null) {
			pr = pr.createStripCopy();
			pr.getPrId();
		}
		this.pr = pr;
	}

	// (p.id, p.poNumber, p.name,cb, p.createdDate, c.currencyCode, p.grandTotal, p.decimal, bu.unitName)
	public Po(String id, String poNumber, String name, User createdBy, Date poCreatedDate, String currencyCode, BigDecimal grandTotal, String decimal, String unitName,Pr pr) {
		this.id = id;
		this.poNumber = poNumber;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.decimal = decimal;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		if (pr != null) {
			pr = pr.createStripCopy();
			pr.getPrId();
		}
		this.pr = pr;
	}

	//PH-2860 Added this constructor for PO listing in buyer side
	public Po(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, //
			Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, //
			String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, //
			PoStatus status, Boolean isPoReportSent, User orderedBy, Date orderedDate, String currencyCode, Date actioDate, Pr pr, Date poRevisedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.status = status;
		this.isPoReportSent = isPoReportSent;
		if (orderedBy != null) {
			orderedBy = orderedBy.createStripCopy();
			orderedBy.getName();
		}
		this.orderedBy = orderedBy;
		this.orderedDate = orderedDate;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.actionDate = actioDate;
		if (pr != null) {
			pr = pr.createStripCopy();
			pr.getPrId();
		}
		this.pr = pr;
		this.poRevisedDate = poRevisedDate;
	}

	public Po(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, //
			  Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, //
			  String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, //
			  PoStatus status, Boolean isPoReportSent, User orderedBy, Date orderedDate, String currencyCode, Date actioDate, Pr pr, Date poRevisedDate, String orderBy, String prId) {
		this.id = id;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.status = status;
		this.isPoReportSent = isPoReportSent;
		if (orderedBy != null) {
			orderedBy = orderedBy.createStripCopy();
			orderedBy.getName();
		}
		this.orderedBy = orderedBy;
		this.orderedDate = orderedDate;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.actionDate = actioDate;
		if (pr != null) {
			pr = pr.createStripCopy();
			pr.getPrId();
		}
		this.pr = pr;
		this.poRevisedDate = poRevisedDate;
		this.orderBy = orderBy;
		this.prId = prId;
	}



	// this is being used in polist page
	public Po(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, //
			  Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, //
			  String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, //
			  PoStatus status, Boolean isPoReportSent, User orderedBy, Date orderedDate, String currencyCode, Date actioDate, Pr pr, Date poRevisedDate, String orderBy, String prId, Boolean fromIntegration) {
		this.id = id;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.status = status;
		this.isPoReportSent = isPoReportSent;
		if (orderedBy != null) {
			orderedBy = orderedBy.createStripCopy();
			orderedBy.getName();
		}
		this.orderedBy = orderedBy;
		this.orderedDate = orderedDate;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.actionDate = actioDate;
		if (pr != null) {
			pr = pr.createStripCopy();
			pr.getPrId();
		}
		this.pr = pr;
		this.poRevisedDate = poRevisedDate;
		this.orderBy = orderBy;
		this.prId = prId;
		this.fromIntegration = fromIntegration;
	}

	/**
	 * For CSV Download
	 * @param id
	 * @param name
	 * @param modifiedDate
	 * @param grandTotal
	 * @param createdBy
	 * @param modifiedBy
	 * @param poCreatedDate
	 * @param createdByName
	 * @param modifiedByName
	 * @param decimal
	 * @param referenceNumber
	 * @param description
	 * @param poNumber
	 * @param unitName
	 * @param mySupplierName
	 * @param openSupplierName
	 * @param status
	 * @param orderedBy
	 * @param orderedDate
	 * @param currencyCode
	 * @param actioDate
	 * @param pr
	 * @param poRevisedDate
	 */
	public Po(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, //
			Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, //
			String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, //
			PoStatus status, User orderedBy, Date orderedDate, String currencyCode, Date actioDate, Pr pr, Date poRevisedDate) {
		this.id = id;
		this.name = name;
		this.createdDate = poCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
		this.modifiedDate = modifiedDate;
		if (modifiedBy != null) {
			modifiedBy = modifiedBy.createStripCopy();
			modifiedBy.getName();
		}
		this.modifiedBy = modifiedBy;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.status = status;
		if (orderedBy != null) {
			orderedBy = orderedBy.createStripCopy();
			orderedBy.getName();
		}
		this.orderedBy = orderedBy;
		this.orderedDate = orderedDate;

		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.actionDate = actioDate;
		if (pr != null) {
			pr = pr.createStripCopy();
			pr.getPrId();
		}
		this.pr = pr;
		this.poRevisedDate = poRevisedDate;
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

	public Pr getPr() {
		return pr;
	}

	public void setPr(Pr pr) {
		this.pr = pr;
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
	public BuyerAddress getCorrespondenceAddress() {
		return correspondenceAddress;
	}

	/**
	 * @param correspondenceAddress the correspondenceAddress to set
	 */
	public void setCorrespondenceAddress(BuyerAddress correspondenceAddress) {
		this.correspondenceAddress = correspondenceAddress;
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
	 * @return the deliveryAddress
	 */
	public BuyerAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(BuyerAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
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
	 * @return the poItems
	 */
	public List<PoItem> getPoItems() {
		if (poItems == null) {
			poItems = new ArrayList<PoItem>();
		}
		return poItems;
	}

	/**
	 * @param poItems the poItems to set
	 */
	public void setPoItems(List<PoItem> poItems) {
		this.poItems = poItems;
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
	public PoStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(PoStatus status) {
		this.status = status;
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
	public User getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
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
	 * @return the cancelReason
	 */
	public String getCancelReason() {
		return cancelReason;
	}

	/**
	 * @param cancelReason the cancelReason to set
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	/**
	 * @return the urgentPo
	 */
	public Boolean getUrgentPo() {
		return urgentPo;
	}

	/**
	 * @param urgentPo the urgentPo to set
	 */
	public void setUrgentPo(Boolean urgentPo) {
		this.urgentPo = urgentPo;
	}

	/**
	 * @return the isPoReportSent
	 */
	public Boolean getIsPoReportSent() {
		return isPoReportSent;
	}

	/**
	 * @param isPoReportSent the isPoReportSent to set
	 */
	public void setIsPoReportSent(Boolean isPoReportSent) {
		this.isPoReportSent = isPoReportSent;
	}

	/**
	 * @return the erpDocNo
	 */
	public String getErpDocNo() {
		return erpDocNo;
	}

	/**
	 * @param erpDocNo the erpDocNo to set
	 */
	public void setErpDocNo(String erpDocNo) {
		this.erpDocNo = erpDocNo;
	}

	/**
	 * @return the erpPrTransferred
	 */
	public Boolean getErpPrTransferred() {
		return erpPrTransferred;
	}

	/**
	 * @param erpPrTransferred the erpPrTransferred to set
	 */
	public void setErpPrTransferred(Boolean erpPrTransferred) {
		this.erpPrTransferred = erpPrTransferred;
	}

	/**
	 * @return the supplierRemark
	 */
	public String getSupplierRemark() {
		return supplierRemark;
	}

	/**
	 * @param supplierRemark the supplierRemark to set
	 */
	public void setSupplierRemark(String supplierRemark) {
		this.supplierRemark = supplierRemark;
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

	/**
	 * @return the orderedBy
	 */
	public User getOrderedBy() {
		return orderedBy;
	}

	/**
	 * @param orderedBy the orderedBy to set
	 */
	public void setOrderedBy(User orderedBy) {
		this.orderedBy = orderedBy;
	}

	/**
	 * @return the correspondAddressTitle
	 */
	public String getCorrespondAddressTitle() {
		return correspondAddressTitle;
	}

	/**
	 * @param correspondAddressTitle the correspondAddressTitle to set
	 */
	public void setCorrespondAddressTitle(String correspondAddressTitle) {
		this.correspondAddressTitle = correspondAddressTitle;
	}

	/**
	 * @return the correspondAddressLine1
	 */
	public String getCorrespondAddressLine1() {
		return correspondAddressLine1;
	}

	/**
	 * @param correspondAddressLine1 the correspondAddressLine1 to set
	 */
	public void setCorrespondAddressLine1(String correspondAddressLine1) {
		this.correspondAddressLine1 = correspondAddressLine1;
	}

	/**
	 * @return the correspondAddressLine2
	 */
	public String getCorrespondAddressLine2() {
		return correspondAddressLine2;
	}

	/**
	 * @param correspondAddressLine2 the correspondAddressLine2 to set
	 */
	public void setCorrespondAddressLine2(String correspondAddressLine2) {
		this.correspondAddressLine2 = correspondAddressLine2;
	}

	/**
	 * @return the correspondAddressCity
	 */
	public String getCorrespondAddressCity() {
		return correspondAddressCity;
	}

	/**
	 * @param correspondAddressCity the correspondAddressCity to set
	 */
	public void setCorrespondAddressCity(String correspondAddressCity) {
		this.correspondAddressCity = correspondAddressCity;
	}

	/**
	 * @return the correspondAddressState
	 */
	public String getCorrespondAddressState() {
		return correspondAddressState;
	}

	/**
	 * @param correspondAddressState the correspondAddressState to set
	 */
	public void setCorrespondAddressState(String correspondAddressState) {
		this.correspondAddressState = correspondAddressState;
	}

	/**
	 * @return the correspondAddressZip
	 */
	public String getCorrespondAddressZip() {
		return correspondAddressZip;
	}

	/**
	 * @param correspondAddressZip the correspondAddressZip to set
	 */
	public void setCorrespondAddressZip(String correspondAddressZip) {
		this.correspondAddressZip = correspondAddressZip;
	}

	/**
	 * @return the correspondAddressCountry
	 */
	public String getCorrespondAddressCountry() {
		return correspondAddressCountry;
	}

	/**
	 * @param correspondAddressCountry the correspondAddressCountry to set
	 */
	public void setCorrespondAddressCountry(String correspondAddressCountry) {
		this.correspondAddressCountry = correspondAddressCountry;
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
	 * @return the doCount
	 */
	public Integer getDoCount() {
		return doCount;
	}

	/**
	 * @param doCount the doCount to set
	 */
	public void setDoCount(Integer doCount) {
		this.doCount = doCount;
	}

	/**
	 * @return the doCancelCount
	 */
	public Integer getDoCancelCount() {
		return doCancelCount;
	}

	/**
	 * @param doCancelCount the doCancelCount to set
	 */
	public void setDoCancelCount(Integer doCancelCount) {
		this.doCancelCount = doCancelCount;
	}

	/**
	 * @return the invoiceCount
	 */
	public Integer getInvoiceCount() {
		return invoiceCount;
	}

	/**
	 * @param invoiceCount the invoiceCount to set
	 */
	public void setInvoiceCount(Integer invoiceCount) {
		this.invoiceCount = invoiceCount;
	}

	/**
	 * @return the invoiceCancelCount
	 */
	public Integer getInvoiceCancelCount() {
		return invoiceCancelCount;
	}

	/**
	 * @param invoiceCancelCount the invoiceCancelCount to set
	 */
	public void setInvoiceCancelCount(Integer invoiceCancelCount) {
		this.invoiceCancelCount = invoiceCancelCount;
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
	 * @return the grnCount
	 */
	public Integer getGrnCount() {
		return grnCount;
	}

	/**
	 * @param grnCount the grnCount to set
	 */
	public void setGrnCount(Integer grnCount) {
		this.grnCount = grnCount;
	}

	/**
	 * @return the grnCancelCount
	 */
	public Integer getGrnCancelCount() {
		return grnCancelCount;
	}

	/**
	 * @param grnCancelCount the grnCancelCount to set
	 */
	public void setGrnCancelCount(Integer grnCancelCount) {
		this.grnCancelCount = grnCancelCount;
	}

	public Integer getGrnReceivedOrDraftCount() {
		return grnReceivedOrDraftCount;
	}

	public void setGrnReceivedOrDraftCount(Integer grnReceivedOrDraftCount) {
		this.grnReceivedOrDraftCount = grnReceivedOrDraftCount;
	}

	/**
	 * @return the paymentTermDays
	 */
	public Integer getPaymentTermDays() {
		return paymentTermDays;
	}

	/**
	 * @param paymentTermDays the paymentTermDays to set
	 */
	public void setPaymentTermDays(Integer paymentTermDays) {
		this.paymentTermDays = paymentTermDays;
	}

	/**
	 * @return the requestForFinance
	 */
	public Boolean getRequestForFinance() {
		return requestForFinance;
	}

	/**
	 * @param requestForFinance the requestForFinance to set
	 */
	public void setRequestForFinance(Boolean requestForFinance) {
		this.requestForFinance = requestForFinance;
	}

	/**
	 * @return the paymentTermes
	 */
	public PaymentTermes getPaymentTermes() {
		return paymentTermes;
	}

	/**
	 * @param paymentTermes the paymentTermes to set
	 */
	public void setPaymentTermes(PaymentTermes paymentTermes) {
		this.paymentTermes = paymentTermes;
	}

	/**
	 * @return the approvals
	 */
	public List<PoApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<PoApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<PoApproval>();
		} else {
			// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (PoApproval oldApproval : this.approvals) {
					for (PoApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);

							// Preserve individual approval user old state
							for (PoApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (PoApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
									if (newApprovalUser.getUser() == null || newApprovalUser.getUser().getId() == null) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
										newApprovalUser.setActionDate(oldApprovalUser.getActionDate());
										newApprovalUser.setApprovalStatus(oldApprovalUser.getApprovalStatus());
										newApprovalUser.setRemarks(oldApprovalUser.getRemarks());
									}
								}
							}
						}
					}
				}
			}
			this.approvals.clear();
		}
		if (approvals != null) {
			this.approvals.addAll(approvals);
		}
	}

	/**
	 * @return the comments
	 */
	public List<PoComment> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<PoComment> comments) {
		this.comments = comments;
	}

	/**
	 * @return the enableApprovalRoute
	 */
	public Boolean getEnableApprovalRoute() {
		return enableApprovalRoute;
	}

	/**
	 * @param enableApprovalRoute the enableApprovalRoute to set
	 */
	public void setEnableApprovalRoute(Boolean enableApprovalRoute) {
		this.enableApprovalRoute = enableApprovalRoute;
	}

	/**
	 * @return the enableApprovalReminder
	 */
	public Boolean getEnableApprovalReminder() {
		return enableApprovalReminder;
	}

	/**
	 * @param enableApprovalReminder the enableApprovalReminder to set
	 */
	public void setEnableApprovalReminder(Boolean enableApprovalReminder) {
		this.enableApprovalReminder = enableApprovalReminder;
	}

	/**
	 * @return the reminderAfterHour
	 */
	public Integer getReminderAfterHour() {
		return reminderAfterHour;
	}

	/**
	 * @param reminderAfterHour the reminderAfterHour to set
	 */
	public void setReminderAfterHour(Integer reminderAfterHour) {
		this.reminderAfterHour = reminderAfterHour;
	}

	/**
	 * @return the reminderCount
	 */
	public Integer getReminderCount() {
		return reminderCount;
	}

	/**
	 * @param reminderCount the reminderCount to set
	 */
	public void setReminderCount(Integer reminderCount) {
		this.reminderCount = reminderCount;
	}

	/**
	 * @return the notifyEventOwner
	 */
	public Boolean getNotifyEventOwner() {
		return notifyEventOwner;
	}

	/**
	 * @param notifyEventOwner the notifyEventOwner to set
	 */
	public void setNotifyEventOwner(Boolean notifyEventOwner) {
		this.notifyEventOwner = notifyEventOwner;
	}

	/**
	 * @return the revised
	 */
	public Boolean getRevised() {
		return revised;
	}

	/**
	 * @param revised the revised to set
	 */
	public void setRevised(Boolean revised) {
		this.revised = revised;
	}

	/**
	 * @return the purchaseOrderDocuments
	 */
	public List<PurchaseOrderDocument> getPurchaseOrderDocuments() {
		return purchaseOrderDocuments;
	}

	/**
	 * @param purchaseOrderDocuments the purchaseOrderDocuments to set
	 */
	public void setPurchaseOrderDocuments(List<PurchaseOrderDocument> purchaseOrderDocuments) {
		this.purchaseOrderDocuments = purchaseOrderDocuments;
	}

	/**
	 * @return the deliveryTime
	 */
	public Date getDeliveryTime() {
		return deliveryTime;
	}

	/**
	 * @param deliveryTime the deliveryTime to set
	 */
	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	/**
	 * @return the revisePoDetails
	 */
	public String getRevisePoDetails() {
		return revisePoDetails;
	}

	/**
	 * @param revisePoDetails the revisePoDetails to set
	 */
	public void setRevisePoDetails(String revisePoDetails) {
		this.revisePoDetails = revisePoDetails;
	}

	/**
	 * @return the reviseJustification
	 */
	public String getReviseJustification() {
		return reviseJustification;
	}

	/**
	 * @param reviseJustification the reviseJustification to set
	 */
	public void setReviseJustification(String reviseJustification) {
		this.reviseJustification = reviseJustification;
	}

	/**
	 * @return the oldStatus
	 */
	public PoStatus getOldStatus() {
		return oldStatus;
	}

	/**
	 * @param oldStatus the oldStatus to set
	 */
	public void setOldStatus(PoStatus oldStatus) {
		this.oldStatus = oldStatus;
	}

	public Date getPoRevisedDate() {
		return poRevisedDate;
	}

	public void setPoRevisedDate(Date poRevisedDate) {
		this.poRevisedDate = poRevisedDate;
	}


	public Boolean getFromIntegration() {
		return fromIntegration;
	}

	public void setFromIntegration(Boolean fromIntegration) {
		this.fromIntegration = fromIntegration;
	}

	public Boolean getSentToSap() {
		return sentToSap;
	}

	public void setSentToSap(Boolean sentToSap) {
		this.sentToSap = sentToSap;
	}

	public Boolean getSentToSapFailed() {
		return sentToSapFailed;
	}

	public void setSentToSapFailed(Boolean sentToSapFailed) {
		this.sentToSapFailed = sentToSapFailed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Po other = (Po) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
