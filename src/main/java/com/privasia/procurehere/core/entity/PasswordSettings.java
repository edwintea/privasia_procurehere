/**
 * 
 */
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author Yogesh
 */
@Entity
@Table(name = "PROC_PASSWORD_SETTINGS")
public class PasswordSettings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3852404139899708314L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	String id;

	@Column(name = "PASSWORD_LENGHT", length = 2)
	private Integer passwordLength;

	@Column(name = "CONTAIN_UPPER_CASE_LETTERS")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean containOneUpperCaseLetters = Boolean.FALSE;

	@Column(name = "CONTAIN_LOWER_CASE_LETTERS")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean containOneLowerCaseLetters = Boolean.FALSE;

	@Column(name = "CONTAIN_NUMBERS")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean containOneNumbers = Boolean.FALSE;

	@Column(name = "CONTAIN_NON_ALPHANUMERIC")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean containNonAlphanumeric = Boolean.FALSE;

	@Column(name = "ENABLE_EXPIRATION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableExpiration = Boolean.FALSE;

	@Column(name = "PASSWORD_EXPIRY_IN_DAYS", length = 3)
	private Integer passwordExpiryInDays;

	@Column(name = "ENABLE_REUSE_PASSWORD")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableReusePassword = Boolean.FALSE;

	@Column(name = "NUMBER_OF_PASSWORD_REMEMBER", length = 5)
	private Integer numberOfPasswordRemember;

	@Column(name = "ENABLE_FAILED_ATTEMPTS")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableFailedAttempts = Boolean.FALSE;

	@Column(name = "FAILED_ATTEMPTS", length = 2)
	private Integer failedAttempts;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PS_SETTING_MOD_BY"))
	private User modifiedBy;

	@Transient
	private String message;

	@Transient
	private String regx;

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

	@Column(name = "TENANT_ID", length = 64)
	String tenantId;

	public PasswordSettings() {
		this.containOneUpperCaseLetters = Boolean.FALSE;
		this.containOneLowerCaseLetters = Boolean.FALSE;
		this.containOneNumbers = Boolean.FALSE;
		this.containNonAlphanumeric = Boolean.FALSE;
		this.enableExpiration = Boolean.FALSE;
		this.enableReusePassword = Boolean.FALSE;
		this.enableFailedAttempts = Boolean.FALSE;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public Integer getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(Integer passwordLength) {
		this.passwordLength = passwordLength;
	}

	public Boolean getContainOneUpperCaseLetters() {
		return containOneUpperCaseLetters;
	}

	public void setContainOneUpperCaseLetters(Boolean containOneUpperCaseLetters) {
		this.containOneUpperCaseLetters = containOneUpperCaseLetters;
	}

	public Boolean getContainOneLowerCaseLetters() {
		return containOneLowerCaseLetters;
	}

	public void setContainOneLowerCaseLetters(Boolean containOneLowerCaseLetters) {
		this.containOneLowerCaseLetters = containOneLowerCaseLetters;
	}

	public Boolean getContainOneNumbers() {
		return containOneNumbers;
	}

	public void setContainOneNumbers(Boolean containOneNumbers) {
		this.containOneNumbers = containOneNumbers;
	}

	public Boolean getContainNonAlphanumeric() {
		return containNonAlphanumeric;
	}

	public void setContainNonAlphanumeric(Boolean containNonAlphanumeric) {
		this.containNonAlphanumeric = containNonAlphanumeric;
	}

	public Boolean getEnableExpiration() {
		return enableExpiration;
	}

	public void setEnableExpiration(Boolean enableExpiration) {
		this.enableExpiration = enableExpiration;
	}

	public Integer getPasswordExpiryInDays() {
		return passwordExpiryInDays;
	}

	public void setPasswordExpiryInDays(Integer passwordExpiryInDays) {
		this.passwordExpiryInDays = passwordExpiryInDays;
	}

	public Boolean getEnableReusePassword() {
		return enableReusePassword;
	}

	public void setEnableReusePassword(Boolean enableReusePassword) {
		this.enableReusePassword = enableReusePassword;
	}

	public Integer getNumberOfPasswordRemember() {
		return numberOfPasswordRemember;
	}

	public void setNumberOfPasswordRemember(Integer numberOfPasswordRemember) {
		this.numberOfPasswordRemember = numberOfPasswordRemember;
	}

	public Boolean getEnableFailedAttempts() {
		return enableFailedAttempts;
	}

	public void setEnableFailedAttempts(Boolean enableFailedAttempts) {
		this.enableFailedAttempts = enableFailedAttempts;
	}

	public Integer getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(Integer failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRegx() {
		return regx;
	}

	public void setRegx(String regx) {
		this.regx = regx;
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
		PasswordSettings other = (PasswordSettings) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
