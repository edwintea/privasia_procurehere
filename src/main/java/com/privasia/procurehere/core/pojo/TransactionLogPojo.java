package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.TransactionLogStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class TransactionLogPojo implements Serializable {

	private static final long serialVersionUID = -2223410162228602221L;

	private String referenceNumber;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionTimeStamp;

	private String txId;

	private String budgetId;

	private String budgetName;

	private String unitName;

	private String costCenter;

	private BigDecimal newAmount;

	private BigDecimal addAmount;

	private BigDecimal deductAmount;

	private String toBusinessUnit;

	private String fromBusinessUnit;

	private BigDecimal transferAmount;

	private TransactionLogStatus txStatus;

	private BigDecimal purchaseOrder;

	private String locked;

	private String prBaseCurrency;

	private String budgetBaseCurrency;

	private BigDecimal conversionRateAmount;

	private BigDecimal amountAfterConversion;

	private BigDecimal remainingAmount;

	private String transactionTimeStampStr;

	public TransactionLogPojo(String referenceNumber, Date transactionTimeStamp, String unitName, String costCenter, BigDecimal newAmount, BigDecimal addAmount, BigDecimal deductAmount, String toBusinessUnit, String fromBusinessUnit, TransactionLogStatus transactionLogStatus, BigDecimal purchaseOrder, String locked, String prBaseCurrency, String budgetBaseCurrency, BigDecimal conversionRate, BigDecimal amountAfterConversion, BigDecimal remainingAmount) {
		super();
		this.referenceNumber = referenceNumber;
		this.transactionTimeStamp = transactionTimeStamp;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.newAmount = newAmount;
		this.addAmount = addAmount;
		this.deductAmount = deductAmount;
		this.toBusinessUnit = toBusinessUnit;
		this.fromBusinessUnit = fromBusinessUnit;
		this.txStatus = transactionLogStatus;
		this.purchaseOrder = purchaseOrder;
		this.locked = locked;
		this.prBaseCurrency = prBaseCurrency;
		this.budgetBaseCurrency = budgetBaseCurrency;
		this.conversionRateAmount = conversionRate;
		this.amountAfterConversion = amountAfterConversion;
		this.remainingAmount = remainingAmount;
	}

	public TransactionLogPojo(String referenceNumber, Date transactionTimeStamp, String unitName, String costCenter, BigDecimal newAmount, BigDecimal addAmount, BigDecimal deductAmount, BigDecimal transferAmount, TransactionLogStatus transactionLogStatus, BigDecimal purchaseOrder, String locked, String prBaseCurrency, BigDecimal conversionRate) {
		super();
		this.referenceNumber = referenceNumber;
		this.transactionTimeStamp = transactionTimeStamp;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.unitName = unitName;
		this.costCenter = costCenter;
		this.newAmount = newAmount;
		this.addAmount = addAmount;
		this.deductAmount = deductAmount;
		this.transferAmount = transferAmount;
		this.txStatus = transactionLogStatus;
		this.purchaseOrder = purchaseOrder;
		this.locked = locked;
		this.prBaseCurrency = prBaseCurrency;
		this.conversionRateAmount = conversionRate;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public Date getTransactionTimeStamp() {
		return transactionTimeStamp;
	}

	public void setTransactionTimeStamp(Date transactionTimeStamp) {
		this.transactionTimeStamp = transactionTimeStamp;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
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

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
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

	public String getToBusinessUnit() {
		return toBusinessUnit;
	}

	public void setToBusinessUnit(String toBusinessUnit) {
		this.toBusinessUnit = toBusinessUnit;
	}

	public String getFromBusinessUnit() {
		return fromBusinessUnit;
	}

	public void setFromBusinessUnit(String fromBusinessUnit) {
		this.fromBusinessUnit = fromBusinessUnit;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public TransactionLogStatus getTxStatus() {
		return txStatus;
	}

	public void setTxStatus(TransactionLogStatus txStatus) {
		this.txStatus = txStatus;
	}

	public BigDecimal getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(BigDecimal purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	public String getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	public String getPrBaseCurrency() {
		return prBaseCurrency;
	}

	public void setPrBaseCurrency(String prBaseCurrency) {
		this.prBaseCurrency = prBaseCurrency;
	}

	/**
	 * @return the budgetBaseCurrency
	 */
	public String getBudgetBaseCurrency() {
		return budgetBaseCurrency;
	}

	/**
	 * @param budgetBaseCurrency the budgetBaseCurrency to set
	 */
	public void setBudgetBaseCurrency(String budgetBaseCurrency) {
		this.budgetBaseCurrency = budgetBaseCurrency;
	}

	/**
	 * @return the conversionRateAmount
	 */
	public BigDecimal getConversionRateAmount() {
		return conversionRateAmount;
	}

	/**
	 * @param conversionRateAmount the conversionRateAmount to set
	 */
	public void setConversionRateAmount(BigDecimal conversionRateAmount) {
		this.conversionRateAmount = conversionRateAmount;
	}

	public BigDecimal getAmountAfterConversion() {
		return amountAfterConversion;
	}

	public void setAmountAfterConversion(BigDecimal amountAfterConversion) {
		this.amountAfterConversion = amountAfterConversion;
	}

	public BigDecimal getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public String getTransactionTimeStampStr() {
		return transactionTimeStampStr;
	}

	public void setTransactionTimeStampStr(String transactionTimeStampStr) {
		this.transactionTimeStampStr = transactionTimeStampStr;
	}

}
