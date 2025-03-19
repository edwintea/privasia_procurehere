package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.TransactionLogStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author shubham
 */

@Entity
@Table(name = "PROC_TRANSACTION_LOG")
public class TransactionLog implements Serializable {

	private static final long serialVersionUID = 1205764116435107403L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@Column(name = "REFERANCE_NUMBER", length = 64)
	@Size(min = 1, max = 64, message = "{budget.referencenumber.length}")
	private String referanceNumber;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUDGET_ID", foreignKey = @ForeignKey(name = "FK_BUDGET_ID"))
	private Budget budget;

	@Enumerated(EnumType.STRING)
	@Column(name = "TRANSACTION_LOG_STATUS")
	private TransactionLogStatus transactionLogStatus;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_TO_ID", nullable = true)
	private BusinessUnit toBusinessUnit;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true)
	private BusinessUnit fromBusinessUnit;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER", nullable = true)
	private CostCenter costCenter;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TRANSACTION_TIMESTAMP")
	private Date transactionTimeStamp;

	@Column(name = "LOCKED_AMOUNT", nullable = true, length = 64)
	private String locked;

	@Column(name = "NEW_AMOUNT", precision = 22, scale = 6)
	private BigDecimal newAmount;

	@Column(name = "ADD_AMOUNT", precision = 22, scale = 6)
	private BigDecimal addAmount;

	@Column(name = "DEDUCT_AMOUNT", precision = 22, scale = 6)
	private BigDecimal deductAmount;

	@Column(name = "TRANSFER_AMOUNT", precision = 22, scale = 6)
	private BigDecimal transferAmount;

	@Column(name = "PO_AMOUNT", precision = 22, scale = 6)
	private BigDecimal purchaseOrderAmount;

	@Column(name = "CONVERSION_RATE", precision = 22, scale = 6)
	private BigDecimal conversionRateAmount;

	@Column(name = "AFTER_CONVERSION_AMOUNT", precision = 22, scale = 6)
	private BigDecimal afterConversionAmount;

	@Column(name = "REMAINING_AMOUNT", precision = 22, scale = 6)
	private BigDecimal remainingAmount;

	@Column(name = "PR_CURRENCY", nullable = true, length = 64)
	private String prBaseCurrency;

	public BusinessUnit getFromBusinessUnit() {
		return fromBusinessUnit;
	}

	public void setFromBusinessUnit(BusinessUnit fromBusinessUnit) {
		this.fromBusinessUnit = fromBusinessUnit;
	}

	public String getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getReferanceNumber() {
		return referanceNumber;
	}

	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	public Budget getBudget() {
		return budget;
	}

	public void setBudget(Budget budget) {
		this.budget = budget;
	}

	public TransactionLogStatus getTransactionLogStatus() {
		return transactionLogStatus;
	}

	public void setTransactionLogStatus(TransactionLogStatus transactionLogStatus) {
		this.transactionLogStatus = transactionLogStatus;
	}

	public BusinessUnit getToBusinessUnit() {
		return toBusinessUnit;
	}

	public void setToBusinessUnit(BusinessUnit toBusinessUnit) {
		this.toBusinessUnit = toBusinessUnit;
	}

	public CostCenter getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	public Date getTransactionTimeStamp() {
		return transactionTimeStamp;
	}

	public void setTransactionTimeStamp(Date transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}

	public BigDecimal getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(BigDecimal newAmount) {
		this.newAmount = newAmount;
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

	public BigDecimal getPurchaseOrderAmount() {
		return purchaseOrderAmount;
	}

	public void setPurchaseOrderAmount(BigDecimal purchaseOrderAmount) {
		this.purchaseOrderAmount = purchaseOrderAmount;
	}

	public BigDecimal getConversionRateAmount() {
		return conversionRateAmount;
	}

	public void setConversionRateAmount(BigDecimal conversionRateAmount) {
		this.conversionRateAmount = conversionRateAmount;
	}

	public BigDecimal getAfterConversionAmount() {
		return afterConversionAmount;
	}

	public void setAfterConversionAmount(BigDecimal afterConversionAmount) {
		this.afterConversionAmount = afterConversionAmount;
	}

	public String getPrBaseCurrency() {
		return prBaseCurrency;
	}

	public void setPrBaseCurrency(String prBaseCurrency) {
		this.prBaseCurrency = prBaseCurrency;
	}

}
