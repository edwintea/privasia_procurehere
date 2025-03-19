package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.privasia.procurehere.core.enums.TransactionStatus;

public class PaymentTransactionPojo implements Serializable {

	private static final long serialVersionUID = -3306115854698322192L;

	private String id;

	private String paymentIds;

	private String companyname;

	private String country;

	private String transactionid;

	private BigDecimal amount;

	private String plan;

	private Date createdDate;

	private TransactionStatus status;

	private String currency;

	private String amountWithCurrency;

	private String transactionTime;

	public PaymentTransactionPojo() {
		super();
	}

	public PaymentTransactionPojo(String id, String companyName, String country, String referenceTransactionId, BigDecimal totalPriceAmount, String supplierPlan, String buyerPlan, Date createdDate, TransactionStatus status, String currency) {
		super();
		this.id = id;
		this.companyname = companyName;
		this.country = country;
		this.transactionid = referenceTransactionId;
		this.amount = totalPriceAmount;
		if (supplierPlan != null) {
			this.plan = supplierPlan;
		}
		if (buyerPlan != null) {
			this.plan = buyerPlan;
		}
		this.createdDate = createdDate;
		this.status = status;
		this.currency = currency;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPaymentIds() {
		return paymentIds;
	}

	public void setPaymentIds(String paymentIds) {
		this.paymentIds = paymentIds;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
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
	 * @return the amountWithCurrency
	 */
	public String getAmountWithCurrency() {
		return currency + " " + (amount != null ? amount : "");
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

}
