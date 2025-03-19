package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author shubham
 */
@Entity
@Table(name = "PROC_BUDGET")
public class Budget implements Serializable {

	private static final long serialVersionUID = 3869163744573210017L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "BUDGET_ID", length = 64, nullable = false)
	private String budgetId;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@Column(name = "BUDGET_NAME", length = 64, nullable = false)
	private String budgetName;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "BUDGET_DOCUMENT", nullable = true)
	private List<BudgetDocument> budgetDocuments;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "budget", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<BudgetApproval> approvals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "budget")
	private List<TransactionLog> transactionLog;

	@Transient
	private List<MultipartFile> budgetFiles;

	@Column(name = "REFERANCE_NUMBER", length = 64)
	@Size(min = 1, max = 64, message = "{budget.referencenumber.length}")
	private String referanceNumber;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "BUDGET_OWNER", nullable = false, foreignKey = @ForeignKey(name="FK_BUDGET_OWN_USER_ID") )
	private User budgetOwner;

	@Enumerated(EnumType.STRING)
	@Column(name = "BUDGET_STATUS")
	private BudgetStatus budgetStatus;

	@Column(name = "ALLOW_BUDGET_LOCK", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean budgetLock = Boolean.FALSE;

	@Column(name = "ALLOW_BUDGET_OVER_RUN", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean budgetOverRun = Boolean.FALSE;

	@Column(name = "ALLOW_OVER_RUN_NOTIFICATION", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean budgetOverRunNotification = Boolean.FALSE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "VALID_FROM")
	private Date validFrom;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "VALID_TO")
	private Date validTo;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CURRENCY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUDGET_CURR_ID"))
	private Currency baseCurrency;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name="FK_BUDGET_BU_ID"))
	private BusinessUnit businessUnit;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER_ID", nullable = true , foreignKey = @ForeignKey(name="FK_BUDGET_CS_ID"))
	private CostCenter costCenter;

	@Column(name = "TOTAL_AMOUNT", precision = 22, scale = 6)
	private BigDecimal totalAmount;

	@Column(name = "PENDING_AMOUNT", precision = 22, scale = 6)
	private BigDecimal pendingAmount;

	@Column(name = "APPROVED_AMOUNT", precision = 22, scale = 6)
	private BigDecimal approvedAmount;

	@Column(name = "AMOUNT_LOCKED", precision = 22, scale = 6)
	private BigDecimal lockedAmount;

	@Column(name = "PAID_AMOUNT", precision = 22, scale = 6)
	private BigDecimal paidAmount;

	@Column(name = "TRANSFER_AMOUNT", precision = 22, scale = 6)
	private BigDecimal transferAmount;

	@Column(name = "REMAINING_AMOUNT", precision = 22, scale = 6)
	private BigDecimal remainingAmount;

	@Size(max = 550)
	@Column(name = "CANCEL_REASON", length = 550)
	private String cancelReason;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_BUDGET_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUDGET_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "budget", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<BudgetComment> budgetComment;

	@Column(name = "REVISION_AMOUNT", precision = 22, scale = 6)
	private BigDecimal revisionAmount;

	@Column(name = "REVISION_JUSTIFICATION")
	private String revisionJustification;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REVISION_DATE")
	private Date revisionDate;

	@Column(name = "CONVERSION_RATE", precision = 22, scale = 6)
	private BigDecimal conversionRate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "TO_BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name="FK_BUDGET_TO_BU_ID"))
	private BusinessUnit toBusinessUnit;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "TO_COST_CENTER_ID", nullable = true, foreignKey = @ForeignKey(name="FK_BUDGET_TO_CS_ID"))
	private CostCenter toCostCenter;

	public Budget() {
	}

	public Budget(String id, Date validFrom, String budgetId, Date validTo) {
		this.id = id;
		this.validFrom = validFrom;
		this.budgetId = budgetId;
		this.validTo = validTo;
	}

	public List<TransactionLog> getTransactionLog() {
		return transactionLog;
	}

	public void setTransactionLog(List<TransactionLog> transactionLog) {
		this.transactionLog = transactionLog;
	}

	public Boolean getBudgetLock() {
		return budgetLock;
	}

	public void setBudgetLock(Boolean budgetLock) {
		this.budgetLock = budgetLock;
	}

	public Boolean getBudgetOverRun() {
		return budgetOverRun;
	}

	public void setBudgetOverRun(Boolean budgetOverRun) {
		this.budgetOverRun = budgetOverRun;
	}

	public Boolean getBudgetOverRunNotification() {
		return budgetOverRunNotification;
	}

	public void setBudgetOverRunNotification(Boolean budgetOverRunNotification) {
		this.budgetOverRunNotification = budgetOverRunNotification;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(String budgetId) {
		this.budgetId = budgetId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getBudgetName() {
		return budgetName;
	}

	public List<BudgetApproval> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<BudgetApproval> approvals) {
		this.approvals = approvals;
	}

	public void setBudgetName(String budgetName) {
		this.budgetName = budgetName;
	}

	public String getReferanceNumber() {
		return referanceNumber;
	}

	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	public User getBudgetOwner() {
		return budgetOwner;
	}

	public void setBudgetOwner(User budgetOwner) {
		this.budgetOwner = budgetOwner;
	}

	public BudgetStatus getBudgetStatus() {
		return budgetStatus;
	}

	public void setBudgetStatus(BudgetStatus budgetStatus) {
		this.budgetStatus = budgetStatus;
	}

	public boolean isBudgetLock() {
		return budgetLock;
	}

	public void setBudgetLock(boolean budgetLock) {
		this.budgetLock = budgetLock;
	}

	public boolean isBudgetOverRun() {
		return budgetOverRun;
	}

	public void setBudgetOverRun(boolean budgetOverRun) {
		this.budgetOverRun = budgetOverRun;
	}

	public boolean isBudgetOverRunNotification() {
		return budgetOverRunNotification;
	}

	public void setBudgetOverRunNotification(boolean budgetOverRunNotification) {
		this.budgetOverRunNotification = budgetOverRunNotification;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	public CostCenter getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(BigDecimal pendingAmount) {
		this.pendingAmount = pendingAmount;
	}

	public BigDecimal getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(BigDecimal approvedAmount) {
		this.approvedAmount = approvedAmount;
	}

	public BigDecimal getLockedAmount() {
		return lockedAmount;
	}

	public void setLockedAmount(BigDecimal lockedAmount) {
		this.lockedAmount = lockedAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<BudgetDocument> getBudgetDocuments() {
		return budgetDocuments;
	}

	public void setBudgetDocuments(List<BudgetDocument> budgetDocuments) {
		this.budgetDocuments = budgetDocuments;
	}

	public List<MultipartFile> getBudgetFiles() {
		return budgetFiles;
	}

	public void setBudgetFiles(List<MultipartFile> budgetFiles) {
		this.budgetFiles = budgetFiles;
	}

	public List<BudgetComment> getBudgetComment() {
		return budgetComment;
	}

	public void setBudgetComment(List<BudgetComment> budgetComment) {
		this.budgetComment = budgetComment;
	}

	public BigDecimal getRevisionAmount() {
		return revisionAmount;
	}

	public void setRevisionAmount(BigDecimal revisionAmount) {
		this.revisionAmount = revisionAmount;
	}

	public String getRevisionJustification() {
		return revisionJustification;
	}

	public void setRevisionJustification(String revisionJustification) {
		this.revisionJustification = revisionJustification;
	}

	public Date getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}

	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public BusinessUnit getToBusinessUnit() {
		return toBusinessUnit;
	}

	public void setToBusinessUnit(BusinessUnit toBusinessUnit) {
		this.toBusinessUnit = toBusinessUnit;
	}

	public CostCenter getToCostCenter() {
		return toCostCenter;
	}

	public void setToCostCenter(CostCenter toCostCenter) {
		this.toCostCenter = toCostCenter;
	}

	@Override
	public String toString() {
		return "Budget [id=" + id + ", budgetId=" + budgetId + ", tenantId=" + tenantId + ", budgetName=" + budgetName + ", budgetDocuments=" + budgetDocuments + ", approvals=" + approvals + ", budgetFiles=" + budgetFiles + ", referanceNumber=" + referanceNumber + ", budgetOwner=" + budgetOwner + ", budgetStatus=" + budgetStatus + ", budgetLock=" + budgetLock + ", budgetOverRun=" + budgetOverRun + ", budgetOverRunNotification=" + budgetOverRunNotification + ", validFrom=" + validFrom + ", validTo=" + validTo + ", baseCurrency=" + baseCurrency + ", businessUnit=" + businessUnit + ", costCenter=" + costCenter + ", totalAmount=" + totalAmount + ", pendingAmount=" + pendingAmount + ", approvedAmount=" + approvedAmount + ", lockedAmount=" + lockedAmount + ", paidAmount=" + paidAmount + ", transferAmount=" + transferAmount + ", remainingAmount=" + remainingAmount + ", cancelReason=" + cancelReason + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate + "]";
	}

}
