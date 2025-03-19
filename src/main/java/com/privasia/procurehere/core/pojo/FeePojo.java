package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.enums.AmountType;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Yogesh
 */
public class FeePojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7673453273074701597L;

	@ApiModelProperty(required = false, hidden = true)
	private BigDecimal amount;

	@ApiModelProperty(notes = "Payment Reference", required = true)
	@JsonProperty(value = "transactionReference")
	private String reference;

	@ApiModelProperty(notes = "Supplier Code", required = true)
	private String supplierCode;

	@ApiModelProperty(notes = "Event eventId No", required = true)
	private String eventId;

	@ApiModelProperty(notes = "Amount Type (FEE or DEPOSIT) ", required = true)
	private AmountType amountType;

	@ApiModelProperty(required = false, hidden = true)
	private Boolean feePaid;

	@ApiModelProperty(required = false, hidden = true)
	private Boolean depositPaid;

	@ApiModelProperty(required = false, hidden = true)
	private Date feePaidDate;

	@ApiModelProperty(required = false, hidden = true)
	private Date depositPaidDate;

	@ApiModelProperty(required = false, hidden = true)
	private String feeReference;

	@ApiModelProperty(required = false, hidden = true)
	private String depositReference;

	@ApiModelProperty(required = false, hidden = true)
	private String companyName;

	@ApiModelProperty(required = false, hidden = true)
	private String fullName;

	@ApiModelProperty(required = false, hidden = true)
	private String communicationEmail;

	@ApiModelProperty(required = false, hidden = true)
	private String companyContactNumber;

	@ApiModelProperty(required = false, hidden = true)
	private String id;

	@ApiModelProperty(required = false, hidden = true)
	private Date feePaidTime;

	@ApiModelProperty(required = false, hidden = true)
	private Date depositPaidTime;

	@ApiModelProperty(required = false, hidden = true)
	private String feeTime;

	@ApiModelProperty(required = false, hidden = true)
	private String depositTime;

	private Boolean selfInvited;

	/**
	 * @return the feePaidTime
	 */
	public Date getFeePaidTime() {
		return feePaidTime;
	}

	/**
	 * @param feePaidTime the feePaidTime to set
	 */
	public void setFeePaidTime(Date feePaidTime) {
		this.feePaidTime = feePaidTime;
	}

	/**
	 * @return the depositPaidTime
	 */
	public Date getDepositPaidTime() {
		return depositPaidTime;
	}

	/**
	 * @param depositPaidTime the depositPaidTime to set
	 */
	public void setDepositPaidTime(Date depositPaidTime) {
		this.depositPaidTime = depositPaidTime;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	/**
	 * @return the amountType
	 */
	public AmountType getAmountType() {
		return amountType;
	}

	/**
	 * @param amountType the amountType to set
	 */
	public void setAmountType(AmountType amountType) {
		this.amountType = amountType;
	}

	/**
	 * @return the feePaid
	 */
	public Boolean getFeePaid() {
		return feePaid;
	}

	/**
	 * @param feePaid the feePaid to set
	 */
	public void setFeePaid(Boolean feePaid) {
		this.feePaid = feePaid;
	}

	/**
	 * @return the depositPaid
	 */
	public Boolean getDepositPaid() {
		return depositPaid;
	}

	/**
	 * @param depositPaid the depositPaid to set
	 */
	public void setDepositPaid(Boolean depositPaid) {
		this.depositPaid = depositPaid;
	}

	/**
	 * @return the feePaidDate
	 */
	public Date getFeePaidDate() {
		return feePaidDate;
	}

	/**
	 * @param feePaidDate the feePaidDate to set
	 */
	public void setFeePaidDate(Date feePaidDate) {
		this.feePaidDate = feePaidDate;
	}

	/**
	 * @return the depositPaidDate
	 */
	public Date getDepositPaidDate() {
		return depositPaidDate;
	}

	/**
	 * @param depositPaidDate the depositPaidDate to set
	 */
	public void setDepositPaidDate(Date depositPaidDate) {
		this.depositPaidDate = depositPaidDate;
	}

	/**
	 * @return the feeReference
	 */
	public String getFeeReference() {
		return feeReference;
	}

	/**
	 * @param feeReference the feeReference to set
	 */
	public void setFeeReference(String feeReference) {
		this.feeReference = feeReference;
	}

	/**
	 * @return the depositReference
	 */
	public String getDepositReference() {
		return depositReference;
	}

	/**
	 * @param depositReference the depositReference to set
	 */
	public void setDepositReference(String depositReference) {
		this.depositReference = depositReference;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the communicationEmail
	 */
	public String getCommunicationEmail() {
		return communicationEmail;
	}

	/**
	 * @param communicationEmail the communicationEmail to set
	 */
	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	/**
	 * @return the companyContactNumber
	 */
	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	/**
	 * @param companyContactNumber the companyContactNumber to set
	 */
	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
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
	 * @return the selfInvited
	 */
	public Boolean getSelfInvited() {
		return selfInvited;
	}

	/**
	 * @param selfInvited the selfInvited to set
	 */
	public void setSelfInvited(Boolean selfInvited) {
		this.selfInvited = selfInvited;
	}

	public String getFeeTime() {
		return feeTime;
	}

	public void setFeeTime(String feeTime) {
		this.feeTime = feeTime;
	}

	public String getDepositTime() {
		return depositTime;
	}

	public void setDepositTime(String depositTime) {
		this.depositTime = depositTime;
	}

	public FeePojo(Boolean feePaid, Boolean depositPaid, Date feePaidDate, Date depositPaidDate, String feeReference, String depositReference, String companyName, String fullName, String communicationEmail, String companyContactNumber, String id, Boolean selfInvited) {
		super();
		this.feePaid = feePaid;
		this.depositPaid = depositPaid;
		this.feePaidDate = feePaidDate;
		this.depositPaidDate = depositPaidDate;
		this.feeReference = feeReference;
		this.depositReference = depositReference;
		this.companyName = companyName;
		this.fullName = fullName;
		this.communicationEmail = communicationEmail;
		this.companyContactNumber = companyContactNumber;
		this.id = id;
		this.selfInvited = selfInvited;
	}

	public FeePojo(BigDecimal amount, String reference, String supplierCode, String eventRefranceNo, AmountType amountType, Boolean feePaid, Boolean depositPaid, Date feePaidDate, Date depositPaidDate, String feeReference, String depositReference, String companyName, String fullName, String communicationEmail, String companyContactNumber, String id) {
		super();
		this.amount = amount;
		this.reference = reference;
		this.supplierCode = supplierCode;
		this.eventId = eventRefranceNo;
		this.amountType = amountType;
		this.feePaid = feePaid;
		this.depositPaid = depositPaid;
		this.feePaidDate = feePaidDate;
		this.depositPaidDate = depositPaidDate;
		this.feeReference = feeReference;
		this.depositReference = depositReference;
		this.companyName = companyName;
		this.fullName = fullName;
		this.communicationEmail = communicationEmail;
		this.companyContactNumber = companyContactNumber;
		this.id = id;
	}

	public FeePojo() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FeePojo [amount=" + amount + ", reference=" + reference + ", supplierCode=" + supplierCode + ", eventRefranceNo=" + eventId + ", amountType=" + amountType + ", feePaid=" + feePaid + ", depositPaid=" + depositPaid + ", feePaidDate=" + feePaidDate + ", depositPaidDate=" + depositPaidDate + ", feeReference=" + feeReference + ", depositReference=" + depositReference + ", companyName=" + companyName + ", fullName=" + fullName + ", communicationEmail=" + communicationEmail + ", companyContactNumber=" + companyContactNumber + ", id=" + id + "]";
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

}
