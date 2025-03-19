package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
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
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.PrStatus;
import com.privasia.procurehere.core.pojo.ApprovedRejectEventPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_PR")
@SqlResultSetMapping(name = "myEventResult", classes = { @ConstructorResult(targetClass = ApprovedRejectEventPojo.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "eventType"), @ColumnResult(name = "creatorName"), @ColumnResult(name = "eventName"), @ColumnResult(name = "referenceNumber"), @ColumnResult(name = "status"), @ColumnResult(name = "createdDate"), @ColumnResult(name = "unitName"), @ColumnResult(name = "auctionType"), @ColumnResult(name = "mySupplierName"), @ColumnResult(name = "openSupplier") }) })
public class Pr implements Serializable {

	private static final Logger LOG = LogManager.getLogger(Pr.class);

	private static final long serialVersionUID = 1182355319272656804L;

	public interface PrCreate {
	}

	public interface PrSupplierList {
	}

	public interface PrSupplierManual {
	}

	public interface PrDelivery {
	}

	public interface PrRemark {
	}

	public interface PrPurchaseItem {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@Column(name = "PR_ID", length = 64)
	private String prId;

	@NotNull(message = "{pr.name.required}", groups = { PrCreate.class })
	@Column(name = "PR_NAME", length = 128)
	@Size(min = 1, max = 128, message = "{pr.name.length}", groups = { PrCreate.class })
	private String name;

	// @NotNull(message = "{pr.referenceNumber.name}", groups = { PrCreate.class })
	@Column(name = "REFERENCE_NUMBER", length = 64)
	@Size(min = 1, max = 64, message = "{pr.referenceNumber.length}", groups = { PrCreate.class })
	private String referenceNumber;

	@Size(max = 1000, message = "{pr.description.length}", groups = { PrCreate.class })
	@Column(name = "DESCRIPTION", length = 1050)
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_BUYER_ID"))
	private Buyer buyer;

	@NotNull(message = "{pr.requester.required}", groups = { PrCreate.class })
	@Size(min = 1, max = 550, message = "{pr.requester.length}", groups = { PrCreate.class })
	@Column(name = "REQUESTER", length = 550)
	private String requester;

	@JsonIgnore
	@NotNull(message = "{pr.correspondenceAddress.required}", groups = { PrCreate.class })
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CORRESPONDENCE_ADDRESS_ID", nullable = true)
	private BuyerAddress correspondenceAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull(message = "{pr.currency.required}", groups = { PrCreate.class })
	@JoinColumn(name = "BASE_CURRENCY_ID", foreignKey = @ForeignKey(name = "FK_PR_BUYER_CURRENCY"))
	private Currency currency;

	@NotNull(message = "{pr.decimal.required}", groups = { PrCreate.class })
	@Column(name = "PR_DECIMAL", length = 64)
	private String decimal;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_COSTCENTER_ID"))
	private CostCenter costCenter;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_BUSS_UNIT_ID"))
	private BusinessUnit businessUnit;

	@NotNull(message = "{pr.paymentTerm.required}", groups = { PrCreate.class })
	@Size(min = 1, max = 500, message = "{pr.paymentTerm.length}", groups = { PrCreate.class })
	@Column(name = "PAYMENT_TERM", length = 550)
	private String paymentTerm;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pr")
	private List<PrDocument> prDocuments;

	@NotNull(message = "{pr.supplier.required}", groups = { PrSupplierList.class })
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PR_SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_FAV_SUP_ID"))
	private FavouriteSupplier supplier;

	@NotEmpty(message = "{pr.supplier.name.required}", groups = { PrSupplierManual.class })
	@Size(max = 128, message = "{pr.supplier.name.length}", groups = { PrSupplierManual.class })
	@Column(name = "SUPPLIER_NAME", length = 128)
	private String supplierName;

	@NotEmpty(message = "{pr.supplier.address.required}", groups = { PrSupplierManual.class })
	@Size(max = 300, message = "{pr.supplier.address.length}", groups = { PrSupplierManual.class })
	@Column(name = "SUPPLIER_ADDRESS", length = 300)
	private String supplierAddress;

	@NotEmpty(message = "{pr.supplier.telnumber.required}", groups = { PrSupplierManual.class })
	@Size(min = 6, max = 50, message = "{pr.supplier.telnumber.length}", groups = { PrSupplierManual.class })
	@Column(name = "SUPPLIER_TEL_NO", length = 55)
	private String supplierTelNumber;

	@Size(max = 16, message = "{pr.supplier.faxnumber.length}", groups = { PrSupplierManual.class })
	@Column(name = "SUPPLIER_FAX_NO", length = 16)
	private String supplierFaxNumber;

	@Size(max = 32, message = "{pr.supplier.taxnumber.length}", groups = { PrSupplierManual.class })
	@Column(name = "SUPPLIER_TAX_NO", length = 32)
	private String supplierTaxNumber;

	@JsonIgnore
	@Valid
	// @NotNull(message = "{common.contact.required}", groups = { PrSupplierManual.class, PrSupplierList.class })
	// @Size(min = 1, message = "{common.contact.length}", groups = { PrSupplierManual.class, PrSupplierList.class })
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pr", cascade = { CascadeType.ALL })
	private List<PrContact> prContacts;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull(message = "{pr.deliverydate.required}", groups = { PrDelivery.class })
	@Column(name = "DELIVERY_DATE")
	private Date deliveryDate;

	@Transient
	private Date deliveryTime;

	@JsonIgnore
	@NotNull(message = "{pr.deliveryAddress.required}", groups = { PrDelivery.class })
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "DELIVERY_ADDRESS_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_DEL_ADD_ID"))
	private BuyerAddress deliveryAddress;

	@NotEmpty(message = "{pr.deliveryReceiver.required}", groups = { PrDelivery.class })
	@Size(max = 150, message = "{pr.deliveryReceiver.length}", groups = { PrDelivery.class })
	@Column(name = "DELIVERY_RECEIVER", length = 150)
	private String deliveryReceiver;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pr")
	@OrderBy("level, order")
	private List<PrItem> prItems;

	@Size(max = 1050, message = "{pr.remarks.length}", groups = { PrRemark.class })
	@Column(name = "REMARKS", length = 1050)
	private String remarks;

	@Size(max = 900, message = "{pr.termsAndConditions.length}", groups = { PrRemark.class })
	@Column(name = "TERMS_AND_COND", length = 1000)
	private String termsAndConditions;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pr", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<PrComment> prComments;

	@Column(name = "FIELD1_LABEL", nullable = true, length = 32)
	private String field1Label;

	@Column(name = "FIELD2_LABEL", nullable = true, length = 32)
	private String field2Label;

	@Column(name = "FIELD3_LABEL", nullable = true, length = 32)
	private String field3Label;

	@Column(name = "FIELD4_LABEL", nullable = true, length = 32)
	private String field4Label;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private PrStatus status;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACTION_BY", nullable = true)
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
	@Column(name = "PR_CREATED_DATE")
	private Date prCreatedDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_CREATED_BY_ID"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PR_MODIFIED_DATE")
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_MODIFIED_BY_ID"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PO_CREATED_DATE")
	private Date poCreatedDate;

	@Column(name = "PO_NUMBER", length = 64)
	private String poNumber;

	@Column(name = "IS_PR_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean prDetailCompleted = Boolean.FALSE;

	@Column(name = "IS_DOCUMENT_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean documentCompleted = Boolean.FALSE;

	@Column(name = "IS_SUPPLIER_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean supplierCompleted = Boolean.FALSE;

	@Column(name = "IS_DELIVERY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean deliveryCompleted = Boolean.FALSE;

	@Column(name = "IS_PR_ITEM_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean prItemCompleted = Boolean.FALSE;

	@Column(name = "IS_REMARK_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remarkCompleted = Boolean.FALSE;

	@Column(name = "IS_SUMMARY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean summaryCompleted = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pr", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<PrApproval> prApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pr", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<PrTeamMember> prTeamMembers;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TEMPLATE_ID", nullable = true)
	private PrTemplate template;

	@Column(name = "CANCEL_REASON", length = 1050)
	private String cancelReason;

	@Column(name = "URGENT_PR")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean urgentPr;

	@Column(name = "IS_PO")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isPo;

	@Column(name = "IS_PO_REPORT_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isPoReportSent;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pr")
	private List<PoDocument> poDocuments;

	@Column(name = "ERP_DOC_NO", length = 64)
	private String erpDocNo;

	@Column(name = "ERP_STATUS", length = 64)
	private String erpStatus;

	@Column(name = "ERP_MESSAGE", length = 1050)
	private String erpMessage;

	@Column(name = "IS_FINAL_APPROVED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isFinalApproved;

	@Column(name = "IS_ERP_TRANSFER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean erpPrTransferred = Boolean.FALSE;

	@Column(name = "AVAILABLE_BUDGET", precision = 20, scale = 4)
	private BigDecimal availableBudget;

	@Column(name = "HIDE_CONTRACT_BASED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean hideContractBased = Boolean.FALSE;

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

	@Column(name = "LOCK_BUDGET")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean lockBudget = Boolean.FALSE;

	@Column(name = "CONVERSION_RATE", precision = 20, scale = 4)
	private BigDecimal conversionRate;

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
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PAYMENT_TERMES", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_PAYMENT_TERMS"))
	private PaymentTermes paymentTermes;
	
	@Column(name = "PAYMENT_TERM_DAYS", length = 3, nullable = true)
	private Integer paymentTermDays;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean conversionRateRequired = Boolean.FALSE;

	@Transient
	private String budgetCurrencyCode;

	@Transient
	private BigDecimal remainingBudgetAmount;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean templateActive;

	@Transient
	private String templateId;

	public Pr() {
		this.enableApprovalReminder = Boolean.FALSE;
	}

	public Pr(String id, String name, Date prCreatedDate, BigDecimal grandTotal, User createdBy) {
		this.id = id;
		this.name = name;
		this.prCreatedDate = prCreatedDate;
		this.grandTotal = grandTotal;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
	}

	public Pr(String id, String name, String referenceNumber, Date prCreatedDate, User createdBy) {
		this.id = id;
		this.name = name;
		this.referenceNumber = referenceNumber;
		this.prCreatedDate = prCreatedDate;
		if (createdBy != null) {
			createdBy = createdBy.createStripCopy();
			createdBy.getName();
		}
		this.createdBy = createdBy;
	}

	public Pr(String id, String name, String referenceNumber, Date prCreatedDate, PrTemplate template) {
		this.id = id;
		this.name = name;
		this.referenceNumber = referenceNumber;
		this.prCreatedDate = prCreatedDate;
		this.template = template;
	}

	public Pr(String id, String name, Date prCreatedDate, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal) {
		this.id = id;
		this.name = name;
		this.prCreatedDate = prCreatedDate;
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
	}

	public Pr(String id, String name, Date prCreatedDate, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String prId, String unitName, String mySupplierName, String openSupplierName) {
		this.id = id;
		this.name = name;
		this.prCreatedDate = prCreatedDate;
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
		this.prId = prId;
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

	}

	public Pr(String id, String name, Date prCreatedDate, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String prId, String unitName, String mySupplierName, String openSupplierName, PrStatus status, Boolean isPoReportSent) {
		this.id = id;
		this.name = name;
		this.prCreatedDate = prCreatedDate;
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
		this.prId = prId;
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
	}

	// <----------------------------dash-board table contractor---------------->
	public Pr(String id, String name, Date prCreatedDate, Date modifiedDate, Boolean isPo, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String prId, String unitName, String mySupplierName, String openSupplierName, PrStatus status, Boolean isPoReportSent) {
		this.id = id;
		this.name = name;
		this.prCreatedDate = prCreatedDate;
		this.isPo = isPo;
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
		this.prId = prId;
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
	}

	public Pr(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, Date poCreatedDate, String createdByName, String modifiedByName, String decimal) {
		this.id = id;
		this.name = name;
		this.poCreatedDate = poCreatedDate;
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
	}

	public Pr(String id, String name, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String poNumber, String unitName, String mySupplierName, String openSupplierName) {
		this.id = id;
		this.name = name;
		this.poCreatedDate = poCreatedDate;
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

	}

	public Pr(String id, String name, Boolean lockBudget, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, Date poCreatedDate, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String poNumber, String unitName, String mySupplierName, String openSupplierName, PrStatus status, Boolean isPoReportSent, String currencyCode) {
		this.id = id;
		this.name = name;
		this.lockBudget = lockBudget;
		this.poCreatedDate = poCreatedDate;
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
		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;

	}

	public Pr(String id, String name, Date modifiedDate, BigDecimal grandTotal, Date poCreatedDate, String decimal, String referenceNumber, String description, String poNumber, PrStatus status) {
		this.id = id;
		this.name = name;
		this.poCreatedDate = poCreatedDate;
		this.grandTotal = grandTotal;
		this.modifiedDate = modifiedDate;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		this.status = status;
	}

	// this is for prReport list screen please check query
	public Pr(String id, String name, Date prCreatedDate, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String prId, String unitName, String mySupplierName, String openSupplierName, PrStatus status, Boolean isPoReportSent, String approvalName, Date approvalDate) {
		this.id = id;
		this.name = name;
		this.prCreatedDate = prCreatedDate;
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
		this.prId = prId;
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
	}

	// this is for pr list screen please check query
	public Pr(String id, String name, Boolean lockBudget, Date prCreatedDate, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String prId, String unitName, PrStatus status, Boolean isPoReportSent, String currencyCode, String mySupplierName, String openSupplierName) {
		this.id = id;
		this.name = name;
		this.lockBudget = lockBudget;
		this.prCreatedDate = prCreatedDate;
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
		this.prId = prId;
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
		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
	}

	public Pr(String id, String name, Boolean lockBudget, Date prCreatedDate, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String prId, String unitName, PrStatus status, Boolean isPoReportSent, String currencyCode, String mySupplierName, String openSupplierName, String poNumber) {
		this.id = id;
		this.name = name;
		this.lockBudget = lockBudget;
		this.prCreatedDate = prCreatedDate;
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
		this.prId = prId;
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
		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		this.poNumber = poNumber;
	}

	// this is for pr export report screen please check query
	public Pr(String id, String name, Date prCreatedDate, Date modifiedDate, BigDecimal grandTotal, User createdBy, User modifiedBy, String createdByName, String modifiedByName, String decimal, String referenceNumber, String description, String prId, String unitName, PrStatus status, Boolean isPoReportSent, String currencyCode, String mySupplierName, String openSupplierName) {
		this.id = id;
		this.name = name;
		this.prCreatedDate = prCreatedDate;
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
		this.prId = prId;
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
		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
	}
	
	//PH-1889
	public Pr(String id, String referenceNumber, String name, String mySupplierName, String openSupplierName, String description, String prId, String createdByName, Date prCreatedDate, String currencyCode, BigDecimal grandTotal, String unitName, PrStatus status, User createdBy,String poNumber) {
		this.id = id;
		this.referenceNumber = referenceNumber;
		this.name = name;
		FavouriteSupplier supplier = new FavouriteSupplier();
		if (StringUtils.checkString(mySupplierName).length() > 0) {
			supplier.setFullName(mySupplierName);
		} else {
			supplier.setFullName(openSupplierName);
		}
		this.supplier = supplier;
		this.description = description;
		if (StringUtils.checkString(createdByName).length() > 0) {
			User user = new User();
			user.setName(createdByName);
			this.createdBy = user;
		}
		this.prId = prId;
		this.prCreatedDate = prCreatedDate;
		this.grandTotal = grandTotal;
		Currency currency = new Currency();
		if (StringUtils.checkString(currencyCode).length() > 0) {
			currency.setCurrencyCode(currencyCode);
		}
		this.currency = currency;
		BusinessUnit businessUnit = new BusinessUnit();
		if (StringUtils.checkString(unitName).length() > 0) {
			businessUnit.setUnitName(unitName);
		}
		this.businessUnit = businessUnit;
		this.status = status;
		this.poNumber=poNumber;
	}

	public Pr copyFrom(Pr oldPr) {
		Pr newPr = new Pr();
		newPr.setReferenceNumber(oldPr.getReferenceNumber());
		newPr.setName(oldPr.getName());
		newPr.setDescription(oldPr.getDescription());
		newPr.setRequester(oldPr.getRequester());
		newPr.setCorrespondenceAddress(oldPr.getCorrespondenceAddress());
		newPr.setCurrency(oldPr.getCurrency());
		newPr.setDecimal(oldPr.getDecimal());
		newPr.setCostCenter(oldPr.getCostCenter());
		newPr.setAvailableBudget(oldPr.getAvailableBudget());
		newPr.setHideContractBased(oldPr.getHideContractBased());
		newPr.setCorrespondenceAddress(oldPr.getCorrespondenceAddress());
		newPr.setBusinessUnit(oldPr.getBusinessUnit());
		newPr.setCurrency(oldPr.getCurrency());
		newPr.setDecimal(oldPr.getDecimal());
		newPr.setDeliveryAddress(oldPr.getDeliveryAddress());
		newPr.setDeliveryReceiver(oldPr.getDeliveryReceiver());
		newPr.setSupplier(oldPr.getSupplier());
		newPr.setRemarks(oldPr.getRemarks());
		newPr.setSupplierAddress(oldPr.getSupplierAddress());
		newPr.setSupplierFaxNumber(oldPr.getSupplierFaxNumber());
		newPr.setSupplierName(oldPr.getSupplierName());
		newPr.setSupplierTaxNumber(oldPr.getSupplierTaxNumber());
		newPr.setSupplierTelNumber(oldPr.getSupplierTelNumber());
		newPr.setTermsAndConditions(oldPr.getTermsAndConditions());
		newPr.setAdditionalTax(oldPr.getAdditionalTax());
		newPr.setGrandTotal(oldPr.getGrandTotal());
		newPr.setTaxDescription(oldPr.getTaxDescription());
		newPr.setTotal(oldPr.getTotal());
		newPr.setTemplate(oldPr.getTemplate());
		newPr.setField1Label(oldPr.getField1Label());
		newPr.setField2Label(oldPr.getField2Label());
		newPr.setField3Label(oldPr.getField3Label());
		newPr.setField4Label(oldPr.getField4Label());
		newPr.setField5Label(oldPr.getField5Label());
		newPr.setField6Label(oldPr.getField6Label());
		newPr.setField7Label(oldPr.getField7Label());
		newPr.setField8Label(oldPr.getField8Label());
		newPr.setField9Label(oldPr.getField9Label());
		newPr.setField10Label(oldPr.getField10Label());
		newPr.setPaymentTerm(oldPr.getPaymentTerm());
		newPr.setPaymentTermDays(oldPr.getPaymentTermDays());
		newPr.setPaymentTermes(oldPr.getPaymentTermes());
		newPr.setLockBudget(oldPr.getLockBudget());
		newPr.setEnableApprovalReminder(oldPr.getEnableApprovalReminder());
		newPr.setReminderAfterHour(oldPr.getReminderAfterHour());
		newPr.setReminderCount(oldPr.getReminderCount());
		newPr.setNotifyEventOwner(oldPr.getNotifyEventOwner());

		// copy approval
		if (CollectionUtil.isNotEmpty(oldPr.getPrApprovals())) {
			List<PrApproval> approvalList = new ArrayList<>();
			for (PrApproval prApproval : oldPr.getPrApprovals()) {
				PrApproval newPrApproval = new PrApproval();
				newPrApproval.setApprovalType(prApproval.getApprovalType());
				newPrApproval.setLevel(prApproval.getLevel());
				newPrApproval.setPr(newPr);
				if (CollectionUtil.isNotEmpty(prApproval.getApprovalUsers())) {
					List<PrApprovalUser> prApprovalList = new ArrayList<>();
					for (PrApprovalUser prApprovalUser : prApproval.getApprovalUsers()) {
						PrApprovalUser newPrApprovalUser = new PrApprovalUser();
						newPrApprovalUser.setApproval(newPrApproval);
						newPrApprovalUser.setUser(prApprovalUser.getUser());
						prApprovalList.add(newPrApprovalUser);
					}
					newPrApproval.setApprovalUsers(prApprovalList);
				}
				approvalList.add(newPrApproval);
			}
			newPr.setPrApprovals(approvalList);
		}
		// copy TeamMembers
		copyTeamMemberDetails(newPr, oldPr);

		// copy PrItems
		copyPrItems(newPr, oldPr);

		// copy Contacts
		copyContactDetails(newPr, oldPr);

		return newPr;
	}

	// Team Members
	private void copyTeamMemberDetails(Pr newPr, Pr oldPr) {
		if (CollectionUtil.isNotEmpty(oldPr.getPrTeamMembers())) {
			newPr.setPrTeamMembers(new ArrayList<PrTeamMember>());
			for (PrTeamMember tm : oldPr.getPrTeamMembers()) {
				newPr.getPrTeamMembers().add(tm.copyFrom());
			}
		}
	}

	// ContactDetails
	private void copyContactDetails(Pr newPr, Pr oldPr) {
		if (CollectionUtil.isNotEmpty(oldPr.getPrContacts())) {
			newPr.setPrContacts(new ArrayList<PrContact>());
			for (PrContact contact : oldPr.getPrContacts()) {
				newPr.getPrContacts().add(contact.copyForPr());
			}
		}
	}

	// Pr Items
	private void copyPrItems(Pr newPr, Pr oldPr) {
		if (CollectionUtil.isNotEmpty(oldPr.getPrItems())) {
			newPr.setPrItems(new ArrayList<PrItem>());
			for (PrItem item : oldPr.getPrItems()) {
				newPr.getPrItems().add(item.copyFrom());
			}
		}
	}

	/**
	 * @param prApprovals the prApprovals to set
	 */
	public void setPrApprovals(List<PrApproval> prApprovals) {
		if (this.prApprovals == null) {
			this.prApprovals = new ArrayList<PrApproval>();
		} else {
			// Do update only it the passed list is a fresh list and not the same instance list.
			if (prApprovals != null) {
				for (PrApproval oldApproval : this.prApprovals) {
					for (PrApproval newApproval : prApprovals) {
						if (newApproval.getId() == null)
							continue;
						// LOG.info(" oldApproval.getId() :" + oldApproval.getId() + " newApproval.getId() :" +
						// newApproval.getId());
						if (newApproval.getId().equals(oldApproval.getId())) {
							// LOG.info(" oldApproval.getId() :" + oldApproval.getId() + " newApproval.getId() :" +
							// newApproval.getId());
							// newApproval.setApprovalType(oldApproval.getApprovalType());
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							// newApproval.setLevel(oldApproval.getLevel());
							newApproval.setId(null);

							// Preserve individual approval user old state
							for (PrApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (PrApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
			this.prApprovals.clear();
		}
		if (prApprovals != null) {
			// LOG.info("Setting fresh list...");
			this.prApprovals.addAll(prApprovals);
		}
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
	 * @return the prDocuments
	 */
	public List<PrDocument> getPrDocuments() {
		return prDocuments;
	}

	/**
	 * @param prDocuments the prDocuments to set
	 */
	public void setPrDocuments(List<PrDocument> prDocuments) {
		this.prDocuments = prDocuments;
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

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
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
	 * @return the prContacts
	 */
	public List<PrContact> getPrContacts() {
		return prContacts;
	}

	/**
	 * @param prContacts the prContacts to set
	 */
	public void setPrContacts(List<PrContact> prContacts) {
		this.prContacts = prContacts;
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
	 * @return the prItems
	 */
	public List<PrItem> getPrItems() {
		return prItems;
	}

	/**
	 * @param prItems the prItems to set
	 */
	public void setPrItems(List<PrItem> prItems) {
		this.prItems = prItems;
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
	 * @return the prComments
	 */
	public List<PrComment> getPrComments() {
		return prComments;
	}

	/**
	 * @param prComments the prComments to set
	 */
	public void setPrComments(List<PrComment> prComments) {
		this.prComments = prComments;
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
	 * @return the poCreatedDate
	 */
	public Date getPoCreatedDate() {
		return poCreatedDate;
	}

	/**
	 * @param poCreatedDate the poCreatedDate to set
	 */
	public void setPoCreatedDate(Date poCreatedDate) {
		this.poCreatedDate = poCreatedDate;
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
	 * @return the prDetailCompleted
	 */
	public Boolean getPrDetailCompleted() {
		return prDetailCompleted;
	}

	/**
	 * @param prDetailCompleted the prDetailCompleted to set
	 */
	public void setPrDetailCompleted(Boolean prDetailCompleted) {
		this.prDetailCompleted = prDetailCompleted;
	}

	/**
	 * @return the documentCompleted
	 */
	public Boolean getDocumentCompleted() {
		return documentCompleted;
	}

	/**
	 * @param documentCompleted the documentCompleted to set
	 */
	public void setDocumentCompleted(Boolean documentCompleted) {
		this.documentCompleted = documentCompleted;
	}

	/**
	 * @return the supplierCompleted
	 */
	public Boolean getSupplierCompleted() {
		return supplierCompleted;
	}

	/**
	 * @param supplierCompleted the supplierCompleted to set
	 */
	public void setSupplierCompleted(Boolean supplierCompleted) {
		this.supplierCompleted = supplierCompleted;
	}

	/**
	 * @return the deliveryCompleted
	 */
	public Boolean getDeliveryCompleted() {
		return deliveryCompleted;
	}

	/**
	 * @param deliveryCompleted the deliveryCompleted to set
	 */
	public void setDeliveryCompleted(Boolean deliveryCompleted) {
		this.deliveryCompleted = deliveryCompleted;
	}

	/**
	 * @return the prItemCompleted
	 */
	public Boolean getPrItemCompleted() {
		return prItemCompleted;
	}

	/**
	 * @param prItemCompleted the prItemCompleted to set
	 */
	public void setPrItemCompleted(Boolean prItemCompleted) {
		this.prItemCompleted = prItemCompleted;
	}

	/**
	 * @return the remarkCompleted
	 */
	public Boolean getRemarkCompleted() {
		return remarkCompleted;
	}

	/**
	 * @param remarkCompleted the remarkCompleted to set
	 */
	public void setRemarkCompleted(Boolean remarkCompleted) {
		this.remarkCompleted = remarkCompleted;
	}

	/**
	 * @return the summaryCompleted
	 */
	public Boolean getSummaryCompleted() {
		return summaryCompleted;
	}

	/**
	 * @param summaryCompleted the summaryCompleted to set
	 */
	public void setSummaryCompleted(Boolean summaryCompleted) {
		this.summaryCompleted = summaryCompleted;
	}

	/**
	 * @return the prApprovals
	 */
	public List<PrApproval> getPrApprovals() {
		return prApprovals;
	}

	/**
	 * @return the prTeamMembers
	 */
	public List<PrTeamMember> getPrTeamMembers() {
		return prTeamMembers;
	}

	/**
	 * @param prTeamMembers the prTeamMembers to set
	 */
	public void setPrTeamMembers(List<PrTeamMember> prTeamMembers) {
		this.prTeamMembers = prTeamMembers;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		try {
			if (createdBy != null) {
				createdBy.getName();
			}
		} catch (Exception e) {
			LOG.error("error fetching createdby details in PR. Lazy??? : " + e.getMessage(), e);
		}
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the template
	 */
	public PrTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(PrTemplate template) {
		this.template = template;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
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
	 * @return the urgentPr
	 */
	public Boolean getUrgentPr() {
		return urgentPr;
	}

	/**
	 * @param urgentPr the urgentPr to set
	 */
	public void setUrgentPr(Boolean urgentPr) {
		this.urgentPr = urgentPr;
	}

	/**
	 * @return the isPo
	 */
	public Boolean getIsPo() {
		return isPo;
	}

	/**
	 * @param isPo the isPo to set
	 */
	public void setIsPo(Boolean isPo) {
		this.isPo = isPo;
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
	 * @return the poDocuments
	 */
	public List<PoDocument> getPoDocuments() {
		return poDocuments;
	}

	/**
	 * @param poDocuments the poDocuments to set
	 */
	public void setPoDocuments(List<PoDocument> poDocuments) {
		this.poDocuments = poDocuments;
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
	 * @return the erpStatus
	 */
	public String getErpStatus() {
		return erpStatus;
	}

	/**
	 * @param erpStatus the erpStatus to set
	 */
	public void setErpStatus(String erpStatus) {
		this.erpStatus = erpStatus;
	}

	/**
	 * @return the erpMessage
	 */
	public String getErpMessage() {
		return erpMessage;
	}

	/**
	 * @param erpMessage the erpMessage to set
	 */
	public void setErpMessage(String erpMessage) {
		this.erpMessage = erpMessage;
	}

	/**
	 * @return the isFinalApproved
	 */
	public Boolean getIsFinalApproved() {
		return isFinalApproved;
	}

	/**
	 * @param isFinalApproved the isFinalApproved to set
	 */
	public void setIsFinalApproved(Boolean isFinalApproved) {
		this.isFinalApproved = isFinalApproved;
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
	 * @return the hideContractBased
	 */
	public Boolean getHideContractBased() {
		return hideContractBased;
	}

	/**
	 * @param hideContractBased the hideContractBased to set
	 */
	public void setHideContractBased(Boolean hideContractBased) {
		this.hideContractBased = hideContractBased;
	}

	public String getField5Label() {
		return field5Label;
	}

	public void setField5Label(String field5Label) {
		this.field5Label = field5Label;
	}

	public String getField6Label() {
		return field6Label;
	}

	public void setField6Label(String field6Label) {
		this.field6Label = field6Label;
	}

	public String getField7Label() {
		return field7Label;
	}

	public void setField7Label(String field7Label) {
		this.field7Label = field7Label;
	}

	public String getField8Label() {
		return field8Label;
	}

	public void setField8Label(String field8Label) {
		this.field8Label = field8Label;
	}

	public String getField9Label() {
		return field9Label;
	}

	public void setField9Label(String field9Label) {
		this.field9Label = field9Label;
	}

	public String getField10Label() {
		return field10Label;
	}

	public void setField10Label(String field10Label) {
		this.field10Label = field10Label;
	}

	public Boolean getConversionRateRequired() {
		return conversionRateRequired;
	}

	public void setConversionRateRequired(Boolean conversionRateRequired) {
		this.conversionRateRequired = conversionRateRequired;
	}

	public Boolean getLockBudget() {
		return lockBudget;
	}

	public void setLockBudget(Boolean lockBudget) {
		this.lockBudget = lockBudget;
	}

	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getBudgetCurrencyCode() {
		return budgetCurrencyCode;
	}

	public void setBudgetCurrencyCode(String budgetCurrencyCode) {
		this.budgetCurrencyCode = budgetCurrencyCode;
	}

	public BigDecimal getRemainingBudgetAmount() {
		return remainingBudgetAmount;
	}

	public void setRemainingBudgetAmount(BigDecimal remainingBudgetAmount) {
		this.remainingBudgetAmount = remainingBudgetAmount;
	}

	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
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
	 * @return the templateActive
	 */
	public boolean isTemplateActive() {
		return templateActive;
	}

	/**
	 * @param templateActive the templateActive to set
	 */
	public void setTemplateActive(boolean templateActive) {
		this.templateActive = templateActive;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((decimal == null) ? 0 : decimal.hashCode());
		result = prime * result + ((deliveryDate == null) ? 0 : deliveryDate.hashCode());
		result = prime * result + ((deliveryReceiver == null) ? 0 : deliveryReceiver.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((paymentTerm == null) ? 0 : paymentTerm.hashCode());
		result = prime * result + ((prId == null) ? 0 : prId.hashCode());
		result = prime * result + ((referenceNumber == null) ? 0 : referenceNumber.hashCode());
		result = prime * result + ((remarks == null) ? 0 : remarks.hashCode());
		result = prime * result + ((requester == null) ? 0 : requester.hashCode());
		result = prime * result + ((supplierAddress == null) ? 0 : supplierAddress.hashCode());
		result = prime * result + ((supplierFaxNumber == null) ? 0 : supplierFaxNumber.hashCode());
		result = prime * result + ((supplierName == null) ? 0 : supplierName.hashCode());
		result = prime * result + ((supplierTaxNumber == null) ? 0 : supplierTaxNumber.hashCode());
		result = prime * result + ((supplierTelNumber == null) ? 0 : supplierTelNumber.hashCode());
		result = prime * result + ((termsAndConditions == null) ? 0 : termsAndConditions.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pr other = (Pr) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (decimal == null) {
			if (other.decimal != null)
				return false;
		} else if (!decimal.equals(other.decimal))
			return false;
		if (deliveryDate == null) {
			if (other.deliveryDate != null)
				return false;
		} else if (!deliveryDate.equals(other.deliveryDate))
			return false;
		if (deliveryReceiver == null) {
			if (other.deliveryReceiver != null)
				return false;
		} else if (!deliveryReceiver.equals(other.deliveryReceiver))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (paymentTerm == null) {
			if (other.paymentTerm != null)
				return false;
		} else if (!paymentTerm.equals(other.paymentTerm))
			return false;
		if (prId == null) {
			if (other.prId != null)
				return false;
		} else if (!prId.equals(other.prId))
			return false;
		if (referenceNumber == null) {
			if (other.referenceNumber != null)
				return false;
		} else if (!referenceNumber.equals(other.referenceNumber))
			return false;
		if (remarks == null) {
			if (other.remarks != null)
				return false;
		} else if (!remarks.equals(other.remarks))
			return false;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		if (supplierAddress == null) {
			if (other.supplierAddress != null)
				return false;
		} else if (!supplierAddress.equals(other.supplierAddress))
			return false;
		if (supplierFaxNumber == null) {
			if (other.supplierFaxNumber != null)
				return false;
		} else if (!supplierFaxNumber.equals(other.supplierFaxNumber))
			return false;
		if (supplierName == null) {
			if (other.supplierName != null)
				return false;
		} else if (!supplierName.equals(other.supplierName))
			return false;
		if (supplierTaxNumber == null) {
			if (other.supplierTaxNumber != null)
				return false;
		} else if (!supplierTaxNumber.equals(other.supplierTaxNumber))
			return false;
		if (supplierTelNumber == null) {
			if (other.supplierTelNumber != null)
				return false;
		} else if (!supplierTelNumber.equals(other.supplierTelNumber))
			return false;
		if (termsAndConditions == null) {
			if (other.termsAndConditions != null)
				return false;
		} else if (!termsAndConditions.equals(other.termsAndConditions))
			return false;
		return true;
	}

	public String toLogString() {
		return "Pr [prId=" + prId + ", name=" + name + ", referenceNumber=" + referenceNumber + ", description=" + description + ", requester=" + requester + ", decimal=" + decimal + ", paymentTerm=" + paymentTerm + ", supplierName=" + supplierName + ", supplierAddress=" + supplierAddress + ", supplierTelNumber=" + supplierTelNumber + ", supplierFaxNumber=" + supplierFaxNumber + ", supplierTaxNumber=" + supplierTaxNumber + ", deliveryDate=" + deliveryDate + ", deliveryReceiver=" + deliveryReceiver + ", remarks=" + remarks + ", termsAndConditions=" + termsAndConditions + "]";
	}

	public Pr createStripCopy() {
		Pr copy =new Pr();
		copy.setId(getId());
		copy.setName(getName());
		copy.setPrId(getPrId());
		copy.setDescription(getDescription());
		copy.setReferenceNumber(getReferenceNumber());
		return copy;
	}
}

