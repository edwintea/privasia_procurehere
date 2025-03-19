package com.privasia.procurehere.core.pojo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.BudgetApproval;
import com.privasia.procurehere.core.entity.BudgetComment;
import com.privasia.procurehere.core.entity.BudgetDocument;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BudgetStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class BudgetPojo {

	private String id;

	private String budgetId;

	private String budgetName;

	private BusinessUnit businessUnit;

	private BusinessUnit toBusinessUnit;

	private CostCenter toCostCenter;

	private String businessUnitName;

	private String currencyName;

	private String costCenterName;

	private CostCenter costCenter;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	private Boolean budgetLock = Boolean.FALSE;

	private Boolean budgetOverRun = Boolean.FALSE;

	private Boolean budgetOverRunNotification = Boolean.FALSE;

	/*
	 * taiga #2955 private Boolean budgetLockNo = Boolean.TRUE; private Boolean budgetOverRunNo = Boolean.TRUE; private
	 * Boolean budgetOverRunNotificationNo = Boolean.TRUE;
	 */

	private BigDecimal totalAmount;

	private BigDecimal FromAmount;

	private Currency baseCurrency;

	private List<MultipartFile> budgetFiles;

	private MultipartFile[] budgetFilesArr;

	private List<BudgetDocument> budgetDocuments;

	private List<BudgetApproval> approvals;

	private String referanceNumber;

	private User budgetOwner;

	private BudgetStatus budgetStatus;

	private BigDecimal pendingAmount;

	private BigDecimal approvedAmount;

	private BigDecimal lockedAmount;

	private BigDecimal paidAmount;

	private BigDecimal conversionRate;

	private BigDecimal transferAmount;

	private BigDecimal addAmount;

	private BigDecimal deductAmount;

	private BigDecimal remainingAmount;

	private String cancelReason;

	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	private long budgetCount;

	private List<BudgetComment> budgetComment;

	private BigDecimal addRevisionAmount;

	private String addRevisionJustification;

	private BigDecimal deductRevisionAmount;

	private String deductRevisionJustification;

	private BigDecimal transferRevisionAmount;

	private String transferRevisionJustification;

	private BigDecimal transferRemainingAmount;

	private BigDecimal addRevisionRemainingAmount;

	private BigDecimal deductRevisionRemainingAmount;

	private BigDecimal transferRevisionRemainingAmount;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date revisionDate;

	public BudgetPojo() {
	}

	public BudgetPojo(long budgetCount) {
		this.budgetCount = budgetCount;
	}

	public BudgetPojo(String id, String budgetId, String budgetName, BudgetStatus budgetStatus, String unitName, String costCenter, Date validFrom, Date validTo, BigDecimal totalAmount, BigDecimal pendingAmount, BigDecimal approvedAmount, BigDecimal lockedAmount, BigDecimal paidAmount, BigDecimal transferAmount, BigDecimal remainingAmount) {
		super();
		this.id = id;
		this.budgetId = budgetId;
		this.budgetName = budgetName;
		this.budgetStatus = budgetStatus;
		this.businessUnitName = unitName;
		this.costCenterName = costCenter;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.totalAmount = totalAmount;
		this.pendingAmount = pendingAmount;
		this.approvedAmount = approvedAmount;
		this.lockedAmount = lockedAmount;
		this.paidAmount = paidAmount;
		this.transferAmount = transferAmount;
		this.remainingAmount = remainingAmount;
	}

	public BudgetPojo(String id, BudgetStatus budgetStatus, String budgetId, String budgetName, String unitName, String costCenter, Date validFrom, Date validTo, BigDecimal totalAmount) {
		this.id = id;
		this.budgetStatus = budgetStatus;
		this.budgetId = budgetId;
		this.budgetName = budgetName;
		this.businessUnitName = unitName;
		this.costCenterName = costCenter;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.totalAmount = totalAmount;
	}

	public List<BudgetDocument> getBudgetDocuments() {
		return budgetDocuments;
	}

	public void setBudgetDocuments(List<BudgetDocument> budgetDocuments) {
		this.budgetDocuments = budgetDocuments;
	}

	public BigDecimal getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public BigDecimal getFromAmount() {
		return FromAmount;
	}

	public void setFromAmount(BigDecimal fromAmount) {
		FromAmount = fromAmount;
	}

	public BusinessUnit getToBusinessUnit() {
		return toBusinessUnit;
	}

	public void setToBusinessUnit(BusinessUnit toBusinessUnit) {
		this.toBusinessUnit = toBusinessUnit;
	}

	public String getBusinessUnitName() {
		return businessUnitName;
	}

	public void setBusinessUnitName(String businessUnitName) {
		this.businessUnitName = businessUnitName;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCostCenterName() {
		return costCenterName;
	}

	public void setCostCenterName(String costCenterName) {
		this.costCenterName = costCenterName;
	}

	public BigDecimal getAddAmount() {
		return addAmount;
	}

	public void setAddAmount(BigDecimal addAmount) {
		this.addAmount = addAmount;
	}

	public BigDecimal getDeductAmount() {
		return deductAmount;
	}

	public void setDeductAmount(BigDecimal deductAmount) {
		this.deductAmount = deductAmount;
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

	public String getBudgetName() {
		return budgetName;
	}

	public void setBudgetName(String budgetName) {
		this.budgetName = budgetName;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public List<MultipartFile> getBudgetFiles() {
		return budgetFiles;
	}

	public void setBudgetFiles(List<MultipartFile> budgetFiles) {
		this.budgetFiles = budgetFiles;
	}

	public List<BudgetApproval> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<BudgetApproval> approvals) {
		this.approvals = approvals;
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

	public List<BudgetComment> getBudgetComment() {
		return budgetComment;
	}

	public void setBudgetComment(List<BudgetComment> budgetComment) {
		this.budgetComment = budgetComment;
	}

	/*
	 * public Boolean getBudgetLockNo() { return budgetLockNo; } public void setBudgetLockNo(Boolean budgetLockNo) {
	 * this.budgetLockNo = budgetLockNo; } public Boolean getBudgetOverRunNo() { return budgetOverRunNo; } public void
	 * setBudgetOverRunNo(Boolean budgetOverRunNo) { this.budgetOverRunNo = budgetOverRunNo; } public Boolean
	 * getBudgetOverRunNotificationNo() { return budgetOverRunNotificationNo; } public void
	 * setBudgetOverRunNotificationNo(Boolean budgetOverRunNotificationNo) { this.budgetOverRunNotificationNo =
	 * budgetOverRunNotificationNo; }
	 */

	public BigDecimal getAddRevisionAmount() {
		return addRevisionAmount;
	}

	public void setAddRevisionAmount(BigDecimal addRevisionAmount) {
		this.addRevisionAmount = addRevisionAmount;
	}

	public String getAddRevisionJustification() {
		return addRevisionJustification;
	}

	public void setAddRevisionJustification(String addRevisionJustification) {
		this.addRevisionJustification = addRevisionJustification;
	}

	public BigDecimal getDeductRevisionAmount() {
		return deductRevisionAmount;
	}

	public void setDeductRevisionAmount(BigDecimal deductRevisionAmount) {
		this.deductRevisionAmount = deductRevisionAmount;
	}

	public String getDeductRevisionJustification() {
		return deductRevisionJustification;
	}

	public void setDeductRevisionJustification(String deductRevisionJustification) {
		this.deductRevisionJustification = deductRevisionJustification;
	}

	public BigDecimal getTransferRevisionAmount() {
		return transferRevisionAmount;
	}

	public void setTransferRevisionAmount(BigDecimal transferRevisionAmount) {
		this.transferRevisionAmount = transferRevisionAmount;
	}

	public String getTransferRevisionJustification() {
		return transferRevisionJustification;
	}

	public void setTransferRevisionJustification(String transferRevisionJustification) {
		this.transferRevisionJustification = transferRevisionJustification;
	}

	public long getBudgetCount() {
		return budgetCount;
	}

	public void setBudgetCount(long budgetCount) {
		this.budgetCount = budgetCount;
	}

	public CostCenter getToCostCenter() {
		return toCostCenter;
	}

	public void setToCostCenter(CostCenter toCostCenter) {
		this.toCostCenter = toCostCenter;
	}

	public BigDecimal getTransferRemainingAmount() {
		return transferRemainingAmount;
	}

	public void setTransferRemainingAmount(BigDecimal transferRemainingAmount) {
		this.transferRemainingAmount = transferRemainingAmount;
	}

	public MultipartFile[] getBudgetFilesArr() {
		return budgetFilesArr;
	}

	public void setBudgetFilesArr(MultipartFile[] budgetFilesArr) {
		this.budgetFilesArr = budgetFilesArr;
	}

	public Date getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(Date revisionDate) {
		this.revisionDate = revisionDate;
	}

	/**
	 * @return the addRevisionRemainingAmount
	 */
	public BigDecimal getAddRevisionRemainingAmount() {
		return addRevisionRemainingAmount;
	}

	/**
	 * @param addRevisionRemainingAmount the addRevisionRemainingAmount to set
	 */
	public void setAddRevisionRemainingAmount(BigDecimal addRevisionRemainingAmount) {
		this.addRevisionRemainingAmount = addRevisionRemainingAmount;
	}

	/**
	 * @return the deductRevisionRemainingAmount
	 */
	public BigDecimal getDeductRevisionRemainingAmount() {
		return deductRevisionRemainingAmount;
	}

	/**
	 * @param deductRevisionRemainingAmount the deductRevisionRemainingAmount to set
	 */
	public void setDeductRevisionRemainingAmount(BigDecimal deductRevisionRemainingAmount) {
		this.deductRevisionRemainingAmount = deductRevisionRemainingAmount;
	}

	/**
	 * @return the transferRevisionRemainingAmount
	 */
	public BigDecimal getTransferRevisionRemainingAmount() {
		return transferRevisionRemainingAmount;
	}

	/**
	 * @param transferRevisionRemainingAmount the transferRevisionRemainingAmount to set
	 */
	public void setTransferRevisionRemainingAmount(BigDecimal transferRevisionRemainingAmount) {
		this.transferRevisionRemainingAmount = transferRevisionRemainingAmount;
	}

}
