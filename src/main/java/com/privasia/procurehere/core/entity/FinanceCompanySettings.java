package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PROC_FINANCE_SETTINGS")
public class FinanceCompanySettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4585518108576689452L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{buyerSettings.timeZone.not.empty}")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "FINANCE_TIME_ZONE", nullable = false, foreignKey = @ForeignKey(name = "FK_FINANCESET_TIMEZONE"))
	private TimeZone timeZone;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_FINANCEESET_CREATED_BY"))
	private User createdBy;

	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_FINANCESET_MODIFIED_BY"))
	private User modifiedBy;

	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@Column(name = "SEQUENCE_NUMBER", nullable = true, length = 64)
	private String poSequenceNumber;

	@Column(name = "SEQUENCE_PREFIX", nullable = true, length = 64)
	private String poSequencePrefix;

	@Column(name = "SEQUENCE_LENGTH", nullable = true, length = 9)
	private Integer poSequenceLength;

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
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
	 * @return the poSequenceNumber
	 */
	public String getPoSequenceNumber() {
		return poSequenceNumber;
	}

	/**
	 * @param poSequenceNumber the poSequenceNumber to set
	 */
	public void setPoSequenceNumber(String poSequenceNumber) {
		this.poSequenceNumber = poSequenceNumber;
	}

	/**
	 * @return the poSequencePrefix
	 */
	public String getPoSequencePrefix() {
		return poSequencePrefix;
	}

	/**
	 * @param poSequencePrefix the poSequencePrefix to set
	 */
	public void setPoSequencePrefix(String poSequencePrefix) {
		this.poSequencePrefix = poSequencePrefix;
	}

	/**
	 * @return the poSequenceLength
	 */
	public Integer getPoSequenceLength() {
		return poSequenceLength;
	}

	/**
	 * @param poSequenceLength the poSequenceLength to set
	 */
	public void setPoSequenceLength(Integer poSequenceLength) {
		this.poSequenceLength = poSequenceLength;
	}


}
