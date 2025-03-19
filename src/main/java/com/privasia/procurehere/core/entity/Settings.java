package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
@Table(name = "PROC_FIN_SETTINGS")
public class Settings implements Serializable {

	private static final long serialVersionUID = 188716183178357312L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "PLATFORM_FEE", precision = 5, scale = 2, nullable = false)
	private BigDecimal platformFee = new BigDecimal("1.5");

	@Column(name = "PYMT_TERM_BUFFER_PERIOD", length = 3, nullable = false)
	private Integer paymentTermBufferPeriod = 60;

	@Column(name = "APPROVAL_TIMEOUT", length = 5, nullable = false)
	private Integer approvalTimeout = 45;

	@Column(name = "LATE_PAYMENT_CHARGE", precision = 5, scale = 2, nullable = false)
	private BigDecimal latePaymentCharge = new BigDecimal("1");

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_FINS_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "MODIFIED_DATE", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@Column(name = "COMMUNICATION_EMAIL", length = 128, nullable = false)
	private String communicationEmail;

	@Column(name = "CONTACT_NUMBER", length = 16, nullable = false)
	private String contactNumber;

	public Settings() {
		this.platformFee = new BigDecimal("1.5");
		this.paymentTermBufferPeriod = 60;
		this.approvalTimeout = 45;
		this.communicationEmail = "admin@finanshere.com";
		this.contactNumber = "0379679600";
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
	 * @return the platformFee
	 */
	public BigDecimal getPlatformFee() {
		return platformFee;
	}

	/**
	 * @param platformFee the platformFee to set
	 */
	public void setPlatformFee(BigDecimal platformFee) {
		this.platformFee = platformFee;
	}

	/**
	 * @return the paymentTermBufferPeriod
	 */
	public Integer getPaymentTermBufferPeriod() {
		return paymentTermBufferPeriod;
	}

	/**
	 * @param paymentTermBufferPeriod the paymentTermBufferPeriod to set
	 */
	public void setPaymentTermBufferPeriod(Integer paymentTermBufferPeriod) {
		this.paymentTermBufferPeriod = paymentTermBufferPeriod;
	}

	/**
	 * @return the approvalTimeout
	 */
	public Integer getApprovalTimeout() {
		return approvalTimeout;
	}

	/**
	 * @param approvalTimeout the approvalTimeout to set
	 */
	public void setApprovalTimeout(Integer approvalTimeout) {
		this.approvalTimeout = approvalTimeout;
	}

	/**
	 * @return the latePaymentCharge
	 */
	public BigDecimal getLatePaymentCharge() {
		return latePaymentCharge;
	}

	/**
	 * @param latePaymentCharge the latePaymentCharge to set
	 */
	public void setLatePaymentCharge(BigDecimal latePaymentCharge) {
		this.latePaymentCharge = latePaymentCharge;
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
	 * @return the contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * @param contactNumber the contactNumber to set
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

}